module controllability_assessment
!
! This is the main program for the optimal solution of the model
! predictive control problem of nonlinear dynamic systems
! using the parameterized KKT optimality conditions
!
!  24.03.1998   Initial coding  (PS)
!  14.08.1998   Modify for sparse Jacobian formulation  (PS)
!  18.08.1998   Modified to handle active set changes automatically  (PS)
!  25.08.1998   For active variable bounds replace Lagrangian gradient
!               with varaible bound constraint  (PS)
!  05.10.1998   Relax strict complementarity violation 0.1 (PS)
!  17.11.2010   Modify for optimal model predictive control problems (PS)
!  08.04.2014   Modify for design optimization (PS)
!  16.05.2014   Modify for CO2 capture design problem  (PS-TD)
!
! Structure of integer valued user-defined parameter vector IPAR
!
! Position index    Description
!  1-NVBD (max 100)  indices of variables that are
!               fixed at bounds at the starting point
!
!  101         number of lower bound violations
!
!  111-700 indices of variables at lower bound
!
!  701         number of upper bound violations
!
!  711-1300 indices of variables at upper bound
!
!  1310     number of inactive inequalities lower than
!
!  1311-1950 indices of constraints as inactive inequalities lower than
!
!  1960     number of inactive inequalities greater than
!
!  1961-2500 indices of constraints as inactive inequalities greater than
!
!  1301       index =0 at first call of FUNCON, 1 otherwise
!
!  1302       enable (=1) or disable (=0) task skipping
!
!  2511-2520 indices of variables undergoing a parametric variation
!
! Structure of real valued user-defined parameter vector FPAR
!
!  1-NVBD  (max 100)  variable values that are
!               fixed at bounds at the starting point (indices in IPAR)
!
!  111-700 variable values at lower bound
!
!  711-1300 variable values at upper bound
!
!  1311-1950 Lagrange multipliers for active lower variable bounds
!
!  1961-2500 Lagrange multipliers for active upper variable bounds
!
!  1301     Objective function value
!
!  1302     Objective function value for nominal change
!
!  1303     Pitcon max time limit
!
!  1304     Pitcon start time
!
!  2511-2520 Initial conditions - point A (known solution)
!
!  2521-2530 Initial conditions - point B (target solution)
!
   use sa_parameters, only: dp, BIG_MAGIC_NUMBER
   use optimization_parameters
   implicit none
   private

   integer, parameter :: idx_obj = 1301  ! fpar
   integer, parameter :: idx_obj_nominal = 1302  ! fpar

   ! The following three, idx_pitcon_task_skipping, idx_pitcon_start_time
   ! and idx_pitcon_time_limit are used inside pitcon source code in order
   ! to implement task skipping.  Do not modify them unless you plan to
   ! modify pitcon source as well.
   integer, parameter :: idx_pitcon_task_skipping = 1302  ! ipar
   integer, parameter :: idx_pitcon_time_limit = 1303  ! fpar
   integer, parameter :: idx_pitcon_start_time = 1304  ! fpar

   integer, parameter :: idx_target_zeta = 2510

   integer, parameter :: idx_lbound_viol = 101
   integer, parameter :: idx_ubound_viol = 701

   integer, parameter :: ind_lbound = 110
   integer, parameter :: ind_ubound = 710

   integer, parameter :: inact_inequal_lt = 1310
   integer, parameter :: inact_inequal_gt = 1960

   integer, parameter :: idx_known_sol = 2510
   integer, parameter :: idx_target_sol = 2520

   integer, parameter :: ncv = 12
   integer, parameter :: nmv = 14

   integer, parameter :: VNCARD1 = 509  ! XM(1)
   integer, parameter :: VSP1CAT = 67   ! XM(2)
   integer, parameter :: QB = 463       ! XM(3)
   integer, parameter :: LCARFA = 504   ! XM(4)
   integer, parameter :: LMEAFA = 505   ! XM(5)
   integer, parameter :: LTFA = 506     ! XM(6)
   integer, parameter :: TEMPCL = 553   ! XM(7)
   integer, parameter :: QCL = 548      ! XM(8)
   integer, parameter :: LMEAMK = 502   ! XM(9)
   integer, parameter :: QD = 531       ! XM(10)
   integer, parameter :: SPLIT = 494    ! XM(11)
   integer, parameter :: QLTAM = 78     ! XM(12)
   integer, parameter :: QLTAB = 155    ! XM(13)
   integer, parameter :: LWATMK = 500   ! XM(14)

   ! Zeta variables
   ! Vapor Total Input DUMMY 1
   ! VTND1 = 507, X(VTND1) = X(VNWATD1) + X(VNCARD1) + X(VNNITD1)
   ! Vapor Input H2O DUMMY 1
   ! VNWATD1 = 508 (fixed to initial value)
   ! Vapor Input CO2 DUMMY 1
   ! VNCARD1 = 509  (change this variable)
   ! Vapor Input N2 DUMMY 1
   ! VNNITD1 = 511  (fixed to initial value)
   ! Vapor Input TMP DUMMY 1
   ! VNTMPD1 = 512 (fixed to initial value)

   integer, parameter :: manip_var_index(nmv) = (/ &
   ! 1        2     3     4       5       6     7
   VNCARD1, VSP1CAT, QB, LCARFA, LMEAFA, LTFA, TEMPCL, &
   ! 8     9     10   11     12     13     14
   QCL, LMEAMK, QD, SPLIT, QLTAM, QLTAB, LWATMK /)

   integer, parameter, public :: nvar = VARIABLES_SIZE + CONSTRAINTS_SIZE + 1

   integer, parameter :: liw = nvar + 29
   integer :: iwork(liw)
   integer, parameter :: lrw = 29 + (6 + nvar) * nvar
   real(kind=dp) :: rwork(lrw)

   integer, parameter :: imax_iterations = 5000

   integer, parameter :: isize_par = 2550
   real(kind=dp), public :: fpar(isize_par)
   integer, public :: ipar(isize_par)

   ! init variables
   real(kind=dp), public :: x_l_init(VARIABLES_SIZE)
   real(kind=dp), public :: x_u_init(VARIABLES_SIZE)

   ! variables
   real(kind=dp), public :: x(nvar)
   real(kind=dp) :: x_l(VARIABLES_SIZE)
   real(kind=dp) :: x_u(VARIABLES_SIZE)

   real(kind=dp) :: x_setpoint(ncv)
   real(kind=dp) :: setpoint_weight(ncv)

   ! The values of the indices in IBND remain fixed and equal to their
   ! initial values throughout the execution of PITCON. These are the
   ! indices of the values which are fixed (lower bound == upper bound) and
   ! all the structural variables (lengths, diameters etc). Notice that the
   ! total lengths and the diameters are not defined because they are
   ! expressed as constraints.
   integer, parameter, public :: nvbd = 40
   integer, parameter, public :: IBND(nvbd) = (/ &
      1,   2,  56,  58,  59,  79, 133, 135, 136, 156, &
      209, 210, 212, 213, 232, 233, 287, 289, 290, 309, 310, 364, &
      366, 367, 386, 387, 440, 441, 443, 444, 501, 510, 519, &
      520, 521, 522, 523, 524, 541, 542 /)
   integer, parameter, public :: nlbnd = 1
   integer, parameter, public :: ILBND(nlbnd) = (/ 448 /)

   ! IPARM indices of the variables linked with zeta
   ! X(507) = VTND1, Vapor Total Input DUMMY 1 (sum of the next three)
   ! X(508) = VNWATD1, Vapor Input H2O DUMMY 1 (fixed to initial value)
   ! X(509) = VNCARD1, Vapor Input CO2 DUMMY 1 (change this variable)
   ! X(511) = VNNITD1, Vapor Input N2 DUMMY 1 (fixed to initial value)
   ! X(512) = VNTMPD1, Vapor Input TMP DUMMY 1 (fixed to initial value)
   integer, parameter, public :: nparm = 5
   integer, parameter, public :: IPARM(nparm) = (/ 507, 508, 509, 511, 512 /)

   public :: init
   public :: solve

   public :: set_before_solve
   public :: constraints
   public :: objective
   public :: read_variable_names

contains

   subroutine init()
      call read_initial_vector()
      call init_parameter_vectors()
   end subroutine init

   subroutine set_before_solve(x_process)
      real(kind=dp), intent(in) :: x_process(VARIABLES_SIZE)

      call set_options()
      call set_setpoint(x_process)
      call set_variables(x_process)
      call set_parameter_vectors(x_process)
#ifdef ITEST
      call init_print_variables()
#endif
   end subroutine set_before_solve

   subroutine solve(x_process, change, pitcon_results, constraints_test)
      use approximate, only: TIME_LIMIT_PT, CHANGE_NOMINAL
      real(kind=dp), intent(in) :: x_process(VARIABLES_SIZE)
      real(kind=dp), intent(in) :: change
      type(simulation_results), intent(out) :: pitcon_results

      real(kind=dp), parameter :: blag = 1.0d-01
      real(kind=dp), parameter :: btol = 1.0d-08
      real(kind=dp), parameter :: brelax = 1.0d-03

      real(kind=dp) :: omp_get_wtime
      real(kind=dp) :: start, time_so_far, penalty
      integer :: number_of_steps, ierror
      integer :: istep, i, nvio, ip, j, klo, kup

      external :: DGE_SLV
      external :: PITCON

      interface
         subroutine constraints_test(nvar, fpar, ipar, x, f)
            integer, parameter :: dp = kind(1.d0)
            integer, intent(in) :: nvar
            real(kind=dp), intent(inout) :: fpar(*)
            integer, intent(in) :: ipar(*)
            real(kind=dp), intent(in) :: x(nvar)
            real(kind=dp), intent(inout) :: f(nvar-1)
         end subroutine constraints_test
      end interface
      procedure(), optional :: constraints_test
      procedure(), pointer :: constraints_ => null()

      if (present(constraints_test)) then
         constraints_ => constraints_test
      else
         constraints_ => constraints
      end if

      call set_before_solve(x_process)

      number_of_steps =  get_number_of_steps(change)
#ifdef DISABLE_TASK_SKIPPING
! When compiled __with__ DISABLE_TASK_SKIPPING=1
      call disable_task_skipping()
#else
! TASK_SKIPPING is enabled by default,
! compile __without__ DISABLE_TASK_SKIPPING=1
      call enable_task_skipping()
#endif
      start = omp_get_wtime()
      fpar(idx_pitcon_start_time) = start
      do istep = 1, number_of_steps
         ! Run PITCON in 2 steps. 1st step get the objective value for the
         ! nominal (minimum) change and let it be the reference objective value,
         ! 2nd step solve for the target (actual) change.
         if (istep == 1) then
            call set_zeta_target_variables(CHANGE_NOMINAL)
            call set_target_zeta(CHANGE_NOMINAL, change)
         else
            call set_options_to_rerun()
            call set_zeta_target_variables(change)
            call set_target_zeta(change, change)
         end if
         do i = 1, imax_iterations
            ! call PITCON(constraints_gradient, fpar, constraints, &
            !    ierror, ipar, iwork, liw, nvar, rwork, lrw, x, DGE_SLV)
            call PITCON(constraints_gradient, fpar, constraints_, &
               ierror, ipar, iwork, liw, nvar, rwork, lrw, x, DGE_SLV)
#ifndef DISABLE_TASK_SKIPPING
            ! TASK_SKIPPING is enabled by default,
            ! compile __without__ DISABLE_TASK_SKIPPING=1
            ! if (IERROR .eq. 10) then
            !   write(*,*) '__ IERROR=10 SET FROM CORRECTOR'
            ! endif
            time_so_far = omp_get_wtime() - start
            if (time_so_far > TIME_LIMIT_PT) then
               ! write(*,*) '__ TASK SKIP PITCON'
               ! write(*,*) '__ time_so_far', time_so_far
               pitcon_results%iflag = 10
               pitcon_results%objective_value = big_magic_number
               fpar(idx_obj_nominal) = big_magic_number
               goto 9000  ! exit
            end if
#endif
            if (ierror /= 0) then  ! ierror
               pitcon_results%iflag = ierror
               ! write(*,*)'pitcon did not complete, error: ', ierror
               ! Add penalty in the F OBJ for no solution
               if (rwork(7) .ge. x(nvar)) then
                  !            (target - zeta_termination) * big value
                  penalty = abs(rwork(7) - x(nvar)) * 10.0  
               else
                  penalty = 10.0
               end if
               pitcon_results%objective_value = scale_objective(fpar(idx_obj)) + penalty
               if (istep == 1) then
                  fpar(idx_obj_nominal) = pitcon_results%objective_value
               else
                  fpar(idx_obj_nominal) = fpar(idx_obj_nominal) + penalty
               endif
               pitcon_results%x = x(1:VARIABLES_SIZE)
               if (number_of_steps == 1) then
                  goto 9000  ! exit
               else
                  exit
               end if
            end if  ! ierror
#ifdef ITEST
            call print_variables()
#endif
            ! Checking violations of optimality conditions
            nvio = 0
            ! Checking violation of strict complementarity slackness
            do ip = 1, ipar(idx_lbound_viol)
               if (fpar(inact_inequal_lt + ip) < 0.0d0 - blag) then
                  j = ipar(ind_lbound + ip)
                  ! write (*,1100) j, x(j)
                  ipar(ind_lbound + ip) = 0
                  fpar(ind_lbound + ip) = 0.0d0
                  fpar(inact_inequal_lt + ip) = 0.0d0
                  nvio = nvio + 1
               end if
            end do
            do ip = 1, ipar(idx_ubound_viol)
               if (fpar(inact_inequal_gt + ip) < 0.0d0 - blag) then
                  j = ipar(ind_ubound + ip)
                  ! write (*,1100) j, x(j)
                  ipar(ind_ubound + ip) = 0
                  fpar(ind_ubound + ip) = 0.0d0
                  fpar(inact_inequal_gt + ip) = 0.0d0
                  nvio = nvio + 1
               end if
            end do
            ! 1100  format('Lagrange multiplier sign change for variable bound', 1X, I4, 1X, E13.6)

            ! Feasibility violation for variable bounds
            do j = 1, VARIABLES_SIZE
               if (x(j) < x_l(j) - btol) then
                  ! write (*,1000) j, r(j)
                  klo = ind_lbound + ipar(idx_lbound_viol) + 1
                  ipar(klo) = j
                  ! x_l(j) = x(j) - brelax * dabs(x_l(j))
                  x_l(j) = x(j)
                  fpar(klo) = x_l(j)
                  ipar(idx_lbound_viol) = ipar(idx_lbound_viol) + 1
                  nvio = nvio + 1
               else if (x(j) > x_u(j) + btol) then
                  ! write (*,1020) j,x(j)
                  kup = ind_ubound + ipar(idx_ubound_viol) + 1
                  ipar(kup) = j
                  ! x_u(j) = x(j) + brelax * dabs(x_u(j))
                  x_u(j) = x(j)
                  fpar(kup) = x_u(j)
                  ipar(idx_ubound_viol) = ipar(idx_ubound_viol) + 1
                  nvio = nvio + 1
               end if
            end do
            ! 1000  format('Lower bound violation for variable', 1X, I4, 1X, E13.6)
            ! 1020  format('Upper bound violation for variable', 1X, I4, 1X, E13.6)

            if (nvio /= 0) then
               iwork(1) = 0
               iwork(4) = 1
               iwork(2) = nvar
               rwork(6) = 1.d0
            else
               iwork(4) = 2
            end if
            if (iwork(1) == 3) then  ! pitcon completed
               ! iwork(1) = corrected
               ! iwork(2) = continuation
               ! iwork(3) = target point
               ! iwork(4) = limit point
               pitcon_results%iflag = 0
               pitcon_results%objective_value = scale_objective(fpar(idx_obj))
               if (istep == 1) then
                  fpar(idx_obj_nominal) = pitcon_results%objective_value
               end if
               pitcon_results%x = x(1:VARIABLES_SIZE)
               if (number_of_steps == 1) then
                  goto 9000  ! exit
               else
                  exit
               end if
            end if  ! pitcon completed
         end do  ! pitcon loop

         if (i .ge. imax_iterations) then
            ! PITCON REACHED MAX ITERATION
            pitcon_results%iflag = 10
            ! WRITE(*,*)'PITCON REACHED MAX ITERATION'
            pitcon_results%objective_value = scale_objective(fpar(idx_obj))*1.d5
            if (istep == 1) then
               fpar(idx_obj_nominal) = pitcon_results%objective_value
            else
               fpar(idx_obj_nominal) = fpar(idx_obj_nominal) * 1.d5
            end if
            pitcon_results%x = x(1:VARIABLES_SIZE)
         end if
      end do  ! number of steps
9000 CONTINUE
      pitcon_results%time = omp_get_wtime() - start
      pitcon_results%iterations = i
#ifdef ITEST
      call close_print_variables()
#endif
      ! There is a linear dependance of the objective function value and the
      ! disturbance level. If solution 1 at e.g. 2 % disturbance is lower than
      ! solution 2 at the same disturbance level then the objective value of
      ! solution 1 will be lower than the one of solution 2 at every
      ! disturbance level. Under this assumption, we use as pitcon objective
      ! function value at every disturbance level the value of the objective
      ! of the nominal disturbance, that is CHANGE_NOMINAL. This is only to
      ! keep the objective of pitcon at the same level as the ipopt objective,
      ! the solution vector is computed for the corresponded disturbance.
      pitcon_results%objective_value = FPAR(idx_obj_nominal)
   end subroutine solve

   subroutine read_initial_vector()
      use file_descriptors, only: bounds_pitcon_file
      integer :: i, unit

      open(newunit=unit, file=bounds_pitcon_file)
      do i = 1, VARIABLES_SIZE
         !            lower bound, upper bound
         read(unit, '(T20, F16.6, T41, F16.6)') x_l_init(i), x_u_init(i)
      end do
      close(unit)
   end subroutine read_initial_vector

   subroutine init_parameter_vectors()
      ipar = 0
      fpar = 0.0d0
   end subroutine init_parameter_vectors

   subroutine set_parameter_vectors(x_process)
      use approximate, only: TIME_LIMIT_PT
      real(kind=dp), intent(in) :: x_process(VARIABLES_SIZE)
      integer :: i

      ! inactive inequalities in constraints
      ipar(inact_inequal_lt) = 0
      ipar(inact_inequal_gt) = nlbnd
      do i = 1, nlbnd
         ipar(inact_inequal_gt + i) = ILBND(i)
      end do
      ! variables at fixed bounds
      do i = 1, nvbd
         fpar(i) = x_process(IBND(i))
         ipar(i) = IBND(i)
      end do
      ! save values for operating point a (starting solution)
      do i = 1, nparm
         fpar(idx_known_sol + i) = x_process(IPARM(i))
         ipar(idx_known_sol + i) = IPARM(i)
      end do
      fpar(idx_pitcon_time_limit) = TIME_LIMIT_PT
   end subroutine set_parameter_vectors

   subroutine set_variables(x_process)
      real(kind=dp), intent(in) :: x_process(VARIABLES_SIZE)
      integer :: i
      ! Set initial point, upper and lower bounds for variables
      do i = 1, VARIABLES_SIZE
         x(i) = x_process(i)
         x_l(i) = x_l_init(i)
         x_u(i) = x_u_init(i)
      end do
      do i = VARIABLES_SIZE + 1, VARIABLES_SIZE + CONSTRAINTS_SIZE
         x(i) = 0.0d0
      end do
      ! Zeta parameter initialization
      x(nvar) = 0.0d0
   end subroutine set_variables

   subroutine set_setpoint(x_process)
      real(kind=dp), intent(in) :: x_process(VARIABLES_SIZE)
      real(kind=dp) :: weighing_factor(ncv)
      integer :: i

      ! __Controlled variables__ =
      !     CO2 capture                    [XSP(1)]
      !     CO2 loading in lean stream     [XSP(3)]
      !     Lean stream temperature        [XSP(5)]

      ! __Manipulated variables__ =
      !     Reboiler duty                  [XSP(2)]
      !     Total flow in lean stream      [XSP(4)]
      !     Cooler duty                    [XSP(6)]
      !     Amine make-up flow             [XSP(7)]
      !     Condenser duty                 [XSP(8)]
      !     Stripper split ratio           [XSP(9)]
      !     Cooling duty in side cooler 1  [XSP(10)]
      !     Cooling duty in side cooler 2  [XSP(11)]
      !     Water make-up flow             [XSP(12)]

      ! CO2 capture ((509-67)/509 = 1 - 0.1 = 0.9) [Controlled variable]
      x_setpoint(1) = (x_process(VNCARD1) - x_process(VSP1CAT)) / x_process(VNCARD1)
      weighing_factor(1) = 1.0D6

      ! Reboiler Duty (MW) (463) [Manipulated variable]
      x_setpoint(2) = x_process(QB)                              
      weighing_factor(2) = 1.0D0

      ! CO2 loading in lean stream (504/505) [Controlled variable]
      x_setpoint(3) = x_process(LCARFA) / x_process(LMEAFA)
      weighing_factor(3) = 1.0D3

      ! Temperature @ bottom of Stripper's Section #1
      ! (use either this or CO2 loading) (285)
      ! x_setpoint(3) = x_process(TEBOTST)

      ! Total flow in lean stream (mol/s) (506) [Manipulated variable]
      x_setpoint(4) = x_process(LTFA)
      weighing_factor(4) = 1.0D0

      ! Lean stream temperature (K) (553) [Controlled variable]
      x_setpoint(5) = x_process(TEMPCL)
      weighing_factor(5) = 1.0D0

      ! Cooler Duty (MW) (548) [Manipulated variable]
      x_setpoint(6) = x_process(QCL)
      weighing_factor(6) = 1.0D0

      ! Amine make-up flow (mol/s) (502) [Manipulated variable]
      x_setpoint(7) = x_process(LMEAMK)
      weighing_factor(7) = 1.0D3

      ! Condenser Duty (MW) (531) [Manipulated variable]
      x_setpoint(8) = x_process(QD)
      weighing_factor(8) = 1.0D0

      ! Stripper split ratio (494) [Manipulated variable]
      x_setpoint(9) = x_process(SPLIT)
      weighing_factor(9) = 1.0D0

      ! Cooling Duty in Side Cooler #1 (MW) (78) [Manipulated variable]
      x_setpoint(10) = x_process(QLTAM)
      weighing_factor(10) = 1.0D0

      ! Cooling Duty in Side Cooler #2 (MW) (155) [Manipulated variable]
      x_setpoint(11) = x_process(QLTAB)
      weighing_factor(11) = 1.0D0

      ! Water make-up flow (mol/s) (500)
      ! x_setpoint(12) = 0, don't set x_setpoint(12) = 0.0000010000000E+00, too ugly
      x_setpoint(12) = x_process(LWATMK)
      weighing_factor(12) = 1.0D0

      do i = 1, ncv-1
         setpoint_weight(i) = weighing_factor(i) / x_setpoint(i) ** 2.0d0
      end do
      ! Because XSP(12) == 0.0
      setpoint_weight(ncv) = weighing_factor(ncv) / 0.0000010000000E+00 ** 2.0D0
   end subroutine set_setpoint

   subroutine constraints(nvar, fpar, ipar, x, f)
      use model, only: sparsity
      integer, intent(in) :: nvar
      real(kind=dp), intent(inout) :: fpar(isize_par)
      integer, intent(in) :: ipar(isize_par)
      real(kind=dp), intent(in) :: x(nvar)
      real(kind=dp), intent(inout) :: f(nvar-1)

      real(kind=dp) :: grad_obj(VARIABLES_SIZE)
      real(kind=dp) :: constraints_vector(CONSTRAINTS_SIZE)
      real(kind=dp) :: constraints_gradient_vector(NONZERO_JACOBIAN)
      real(kind=dp) :: theta(nparm)

      real(kind=dp) :: amu, obj_value
      integer :: i, kmbd, ipntr, jc, j, ip, k1, k2, ivar

      ! Initialize - Zero all entries in F(x)=0
      ! Every index of F(x) should be set otherwise there is an error. Easier
      ! to catch the error if F(x) not initialized to 0
      ! f = 0.0d0
      obj_value = big_magic_number

      ! Assign parametric variation
      do i = 1, nparm
         theta(i) = (fpar(idx_target_sol + i) &
            - fpar(idx_known_sol + i)) / fpar(idx_target_zeta)
      end do

      ! Evaluate nonlinear-linear constraints
      call constraints_and_gradients(nvar, x, CONSTRAINTS_SIZE, constraints_vector, &
         NONZERO_JACOBIAN, constraints_gradient_vector)
      call objective(nvar, x, obj_value, VARIABLES_SIZE, grad_obj)

      ! Objective function value
      fpar(idx_obj) = obj_value

      do i = 1, VARIABLES_SIZE
         ! Construct Lagrangian Gradient (KKT optimality conditions)
         ! Include objective gradients of active bounds saved in RG
         call langrangian_entry(kmbd, ipntr, ipar, i)
         if (kmbd == 0) then
            ! Regular Lagrangian entry
            f(i) = - grad_obj(i)
            k1 = sparsity%jcolp(i)
            k2 = sparsity%jcolp(i+1)-1
            do j=k1, k2
               f(i) = f(i) + x(VARIABLES_SIZE + sparsity%jrow(j)) &
                  * constraints_gradient_vector(j)
            end do
         else if (kmbd == 1) then
            ! Parametric variation linked to zeta
            f(i) = x(i) - fpar(idx_known_sol+ipntr) - theta(ipntr) * x(nvar)
            ! Delete IPNTR == 1 conditional branch and the constraint below
            !           F(I) = X(507) - X(508) - X(509) - X(511)
            ! Instead write the equation in terms of zeta
         else if (kmbd == 2) then
            ! Variables fixed or at lower bound
            f(i) = x(i) - fpar(ipntr)
         else if (kmbd == 3) then
            ! Variables fixed or at upper bound
            f(i) = - x(i) + fpar(ipntr)
         end if
      end do

      jc = VARIABLES_SIZE
      do i = 1, CONSTRAINTS_SIZE
         ! Constraints
         jc = jc + 1
         f(jc) = - constraints_vector(i)
         do j = 1, ipar(inact_inequal_lt)
            if (i == ipar(inact_inequal_lt+j)) then
               f(jc) = x(VARIABLES_SIZE + i) * constraints_vector(i)
            end if
         end do
         do j = 1, ipar(inact_inequal_gt)
            if (i == ipar(inact_inequal_gt+j)) then
               f(jc) = x(VARIABLES_SIZE + i) * constraints_vector(i)
            end if
         end do
      end do

      ! Evaluate Lagrange multipliers for active variable bounds
      do ip = 1, ipar(idx_lbound_viol)
         if (ipar(ind_lbound+ip) /= 0) then
            ivar = ipar(ind_lbound+ip)
            k1 = sparsity%jcolp(ivar)
            k2 = sparsity%jcolp(ivar+1) - 1
            amu = - grad_obj(ivar)
            do j = k1, k2
               amu = amu + x(VARIABLES_SIZE + sparsity%jrow(j)) &
                  * constraints_gradient_vector(j)
            end do
            fpar(inact_inequal_lt+ip) = - amu
         end if
      end do

      do ip = 1, ipar(idx_ubound_viol)
         if (ipar(ind_ubound + ip) /= 0) then
            ivar = ipar(ind_ubound + ip)
            k1 = sparsity%jcolp(ivar)
            k2 = sparsity%jcolp(ivar + 1) - 1
            amu = - grad_obj(ivar)
            do j = k1, k2
               amu = amu + x(VARIABLES_SIZE + sparsity%jrow(j)) &
                  * constraints_gradient_vector(j)
            end do
            fpar(inact_inequal_gt + ip) = amu
         end if
      end do
   end subroutine constraints

   subroutine constraints_and_gradients(nvar, x, ncon, f, njac, g)
#ifdef HAS_X2P
      use x2p, only: x2p_constraints_all => constraints_all
      use time_model, only: time_model_all
#endif
      integer, intent(in) :: nvar
      real(kind=dp), intent(in) :: x(nvar)
      integer, intent(in) :: ncon
      real(kind=dp), intent(out) :: f(ncon)
      integer, intent(in) :: njac
      real(kind=dp), intent(out) :: g(njac)

      logical, parameter :: gradient = .true.
#ifdef TIME_MODEL
      call time_model_all(x, nvar, ncon, njac)
#endif
#ifdef X2P
      call x2p_constraints_all(x, nvar, f, ncon, g, njac)
#else
      call FUNCON(gradient, ncon, nvar, njac, x, f, g)
#endif
   end subroutine constraints_and_gradients

   subroutine objective(x_size, x, obj_value, grad_obj_size, grad_obj)
      ! Copyright, Panos Seferlis, 1998
      !
      ! This subroutine calculates the objective function value used
      ! in the steady-state multiple disturbance sensitivity analysis
      !
      ! 21.04.1999   Initial coding  (PS)
      ! 27.07.2014   Adapted to handle CO2 capture plant control  (PS)

      integer, intent(in) :: x_size
      real(kind=dp), intent(in) :: x(x_size)
      real(kind=dp), intent(out) :: obj_value
      integer, intent(in) :: grad_obj_size
      real(kind=dp), intent(out) :: grad_obj(grad_obj_size)

      integer :: jxm(nmv)
      real(kind=dp) :: xm(nmv)

      integer :: i, index_xm, jf
      real(kind=dp) :: xerr, error

      do i = 1, nmv
          index_xm = manip_var_index(i)
          xm(i) = x(index_xm)
          jxm(i) = index_xm
       end do
       ! initialize the jacobian
       obj_value = 0.0d0
       grad_obj = 0.0d0

       ! controlled variables
       do i = 1, ncv
          select case (i)
          case (1)
             xerr = (xm(1) - xm(2))/xm(1) - x_setpoint(i)
             error = 0.50d0 * setpoint_weight(i) * xerr ** 2.0d0
             obj_value = obj_value + error

             jf = jxm(1)
             grad_obj(jf) = setpoint_weight(i) * xerr * xm(2) / xm(1) ** 2.0d0

             jf = jxm(2)
             grad_obj(jf) = -setpoint_weight(i) * xerr / xm(1)
          case (2)
             xerr = xm(3) - x_setpoint(i)
             error = 0.50d0 * setpoint_weight(i) * xerr ** 2.0d0
             obj_value = obj_value + error

             jf = jxm(3)
             grad_obj(jf) = setpoint_weight(i) * xerr
          case (3)
             xerr = xm(4) / xm(5) - x_setpoint(i)
             error = 0.50d0 * setpoint_weight(i) * xerr ** 2.0d0
             obj_value = obj_value + error

             jf = jxm(4)
             grad_obj(jf) = setpoint_weight(i) * xerr / xm(5)

             jf = jxm(5)
             grad_obj(jf) = -setpoint_weight(i) * xerr * xm(4) / xm(5) ** 2.0d0
             ! activate this instead if you want to use the
             ! stripper's temperature insstead of co2 loading
             !             xerr = xm(4) - x_setpoint(i)                               
             !             error = 0.50d0 * setpoint_weight(i) * xerr ** 2.0d0
             !             obj_value = obj_value + error
             !             jf = jxm(4)
             !             grad_obj(jf) = setpoint_weight(i) * xerr
          case default
             xerr = xm(i + 2) - x_setpoint(i)
             error = 0.50d0 * (setpoint_weight(i) * xerr ** 2.0d0)
             obj_value = obj_value + error

             jf = jxm(i + 2)
             grad_obj(jf) = setpoint_weight(i) * xerr
          end select
        end do
     end subroutine objective

   subroutine langrangian_entry(kmbd, ipntr, ipar, i)
      integer, intent(out) :: kmbd
      integer, intent(out) :: ipntr
      integer, intent(in) :: ipar(isize_par)
      integer, intent(in) :: i

      integer :: ip

      ! KMBD is a flag that designates the type of Lagrangian entry
      ! 0:Regular
      ! 1:Parameter linked to zeta,
      ! 2:Fixed at bounds or lower bound
      ! 3:Fixed at upper bound
      kmbd = 0
      ! Variables that vary as a function of zeta (independent variable)
      do ip = 1, nparm
         if (i == ipar(idx_known_sol + ip)) then
            kmbd = 1
            ipntr = ip
            return
         end if
      end do
      ! Variables at bounds - fixed
      do ip = 1, nvbd
         if (i == ipar(ip)) then
            kmbd = 2
            ipntr = ip
            return
         end if
      end do
      ! Variables at lower bound
      do ip = 1, ipar(idx_lbound_viol)
         if (i == ipar(ind_lbound + ip)) then
            kmbd = 2
            ipntr = ind_lbound + ip
            return
         end if
      end do
      ! Variables at upper bound
      do ip=1, ipar(idx_ubound_viol)
         if (i == ipar(ind_ubound + ip)) then
            kmbd = 3
            ipntr = ind_ubound + ip
            return
         end if
      end do
   end subroutine langrangian_entry

   subroutine constraints_gradient()
      ! prototype
      !    subroutine constraints_gradient(nvar, fpar, ipar, x, fjac)
      ! computation of the jacobian of the kkt conditions
   end subroutine constraints_gradient

   subroutine set_options()
! Set work arrays to zero
      iwork = 0
      rwork = 0.0

!     The Integer Work Array IWORK
!
!
!  Input to the program includes the setting of some of the entries in IWORK.
!  Some of this input is optional.  The user input section of IWORK involves
!  entries 1 through 8, and, possibly also 17 and 29.
!
!  IWORK(1) must be set by the user.  All other entries have default values.
!
!
!  IWORK(1)        On first call only, the user must set IWORK(1)=0.
!                  Thereafter, the program sets IWORK(1) before return to
!                  explain what kind of point is being returned.  This return
!                  code is:
!
!                      1 return of corrected starting point.
!                      2 return of continuation point.
!                      3 return of target point.
!                      4 return of limit point.
!
!                  NOTE:  At any time, PITCON may be called with a negative
!                  value of IWORK(1). This requests a check of the
!                  jacobian routine against a finite difference approximation.
!                  The program will call the jacobian routine, and
!                  then estimate the jacobian.  If IWORK(1)=-1, then it will
!                  print out the value of the maximum difference, and the row
!                  and column of the jacobian in which it appears.  Otherwise,
!                  the program will print out the entire matrix
!                  FP(I,J)-DEL(J)F(I), where DEL(J)F(I) represents the finite
!                  difference approximation.
!
!                  Before a call with negative IWORK(1), the current value of
!                  IWORK(1) should be saved, and then restored to the previous
!                  value after the call, in order to resume calculation.
!
!                  IWORK(1) does not have a default value.  The user MUST set
!                  it.
!
        iwork(1)=0
!
!  IWORK(2)        The component of the current continuation point XR which is
!                  to be used as the continuation parameter.  On first call,
!                  the program is willing to use the index NVAR as a default,
!                  but the user should set this value if better information is
!                  available.
!
!                  After the first call, the program sets this value for each
!                  step automatically unless the user prevents this by setting
!                  the parameterization option IWORK(3) to a non-zero valus.
!                  Note that a poor choice of IWORK(2) may cause the algorithm
!                  to fail.  IWORK(2) defaults to NVAR on the first step.
!
        iwork(2) = nvar
!
!  IWORK(3)        Parameterization option.  The program would prefer to be
!                  free to choose a new local parameter from step to step.
!                  The value of IWORK(3) allows or prohibits this action.
!                  IWORK(3)=0 allows the program to vary the parameter,
!                  IWORK(3)=1 forces the program to use whatever the contents
!                  of IWORK(2) are, which will not be changed from the user's
!                  input or the default.  The default is IWORK(3)=0.
!
        iwork(3) = 1
!
!  IWORK(4)        Newton iteration Jacobian update option.
!                  0, the Jacobian is reevaluated at every step of the
!                     Newton iteration.  This is costly, but may result in
!                     fewer Newton steps and fewer Newton iteration rejections.
!                  1, the Jacobian is evaluated only on the first and
!                     IWORK(17)-th steps of the Newton process.
!                  2, the Jacobian is evaluated only when absolutely
!                     necessary, namely, at the first step, and when the
!                     process fails. This option is most suitable for problems
!                     with mild nonlinearities.
!
!                  The default is IWORK(4)=0.
!
        iwork(4)=2
!
!  IWORK(5)        Target point index.  If IWORK(5) is not zero, it is presumed
!                  to be the component index between 1 and NVAR for which
!                  target points are sought.  In this case, the value of
!                  RWORK(7) is assumed to be the target value.  The program
!                  will monitor every new continuation point, and if it finds
!                  that a target point may lie between the new point and the
!                  previous point, it will compute this target point and
!                  return.  This target point is defined by the property that
!                  its component with the index prescribed in IWORK(5) will
!                  have the value given in RWORK(7).  For a given problem there
!                  may be zero, one, or many target points requested.
!                  The default of IWORK(5) is 0.
!
        iwork(5)=nvar
!
!  IWORK(6)        Limit point index.  If IWORK(6) is nonzero, then the program
!                  will search for limit points with respect to the component
!                  with index IWORK(6); that is, of points for which the
!                  IWORK(6)-th variable has a local extremum, or equivalently
!                  where the IWORK(6)-th component of the tangent vector is
!                  zero.  The default of IWORK(6) is zero.
!
        iwork(6)=0
!
!  IWORK(7)        Control of the amount of intermediate output produced by the
!                  program. IWORK(7) may have a value between 0 and 3.
!                  For IWORK(7) = 0 there is no intermediate output while for
!                  IWORK(7) = 3 the most intermediate output is produced.
!                  The default is 1.
!
        iwork(7)=0
!
!  IWORK(8)        FORTRAN unit to which output is to be written.  The
!                  default value is site dependent but should be the standard
!                  output device.
!                  The default is 6 on the Cray, Vax and PC, or 9 on the
!                  Macintosh.
!
        iwork(8)=20
!
!  IWORK(9)        Control of the Jacobian option specifying whether the user
!                  has supplied a Jacobian routine, or wants the program
!                  to approximate the Jacobian.
!                  0, the user has supplied the Jacobian.
!                  1, program is to use forward difference approximation.
!                  2, program is to use central difference approximation.
!                  IWORK(9) defaults to 0.
!
        iwork(9)=1
!
!  IWORK(10)       State indicator of the progress of the program.
!                  The values are:
!                  0, start up with unchecked starting point.
!                  1, first step.  Corrected starting point available.
!                  2, two successive continuation points available, as well
!                     as the tangent vector at the oldest of them.
!                  3, two successive continuation points available, as well
!                     as the tangent vector at the newest of them.
!
!  IWORK(11)       Index of the last computed target point. This is used to
!                  avoid repeated computation of a target point.  If a target
!                  point has been found, then the target index IWORK(5) is
!                  copied into IWORK(11).
!
!  IWORK(12)       Second best choice for the local parameterization index.
!                  This index may be tried if the first choice causes poor
!                  performance in the Newton corrector.
!
        iwork(12)=3
!
!  IWORK(13)       Beginning location in IWORK of unused integer work space
!                  available for use by the solver.
!
!  IWORK(14)       LIW, the user declared dimension of the array IWORK.
!
!  IWORK(15)       Beginning location in RWORK of unused real work space
!                  available for use by the solver.
!
!  IWORK(16)       LRW, the user declared dimension of RWORK.
!
!  IWORK(17)       Maximum number of corrector steps allowed during one run
!                  of the Newton process in which the Jacobian is updated at
!                  every step.  If the Jacobian is only evaluated at
!                  the beginning of the Newton iteration then 2*IWORK(17) steps
!                  are allowed.
!                  IWORK(17) must be greater than 0.  It defaults to 10.
!
        iwork(17)=200
!
!  IWORK(18)       Number of stepsize reductions that were needed for
!                  producing the last continuation point.
!
!  IWORK(19)       Total number of calls to the user Jacobian routine DF.
!
!  IWORK(20)       Total number of calls to the matrix factorization routine.
!                  If DENSLV is the chose solver then factorization is done by
!                  the LINPACK routine SGEFA.  If BANSLV is the solver, the
!                  LINPACK routine SGBFA will be used.
!
!  IWORK(21)       Total number of calls to the back-substitution routine.
!                  If DENSLV is the chosen solver, then back substitution is
!                  done by the LINPACK routine SGESL.  If BANSLV is used, then
!                  the LINPACK routine SGBSL will be used.
!
!  IWORK(22)       Total number of calls to the user function routine FX.
!
!  IWORK(23)       Total number of steps taken in limit point iterations.
!                  Each step involves determining an approximate limit point
!                  and applying a Newton iteration to correct it.
!
!  IWORK(24)       Total number of Newton corrector steps used during the
!                  computation of target points.
!
!  IWORK(25)       Total number of Newton steps taken during the correction
!                  of a starting point or the continuation points.
!
!  IWORK(26)       Total number of predictor stepsize-reductions needed
!                  since the start of the continuation procesds.
!
!  IWORK(27)       Total number of calls to the program.  This also
!                  corresponds to the number of points computed.
!
!  IWORK(28)       Total number of Newton steps taken during current iteration.
!
!  IWORK(30)       and on are reserved for use by the linear equation solver,
!                  and typically are used for pivoting.
!
!
!     The Real Work Array RWORK
!
!
!  Input to the program includes the setting of some of the entries in RWORK.
!  Some of this input is optional.  The user input section of RWORK involves
!  entries 1 through 7 and possibly 20.  All entries of RWORK have default
!  values.
!
!
!  RWORK(1)        Absolute error tolerance.   This value is used mainly during
!                  the Newton iteration.  RWORK(1) defaults to SQRT(EPMACH)
!                  where EPMACH is the machine relative precision stored in
!                  RWORK(8).
!
        rwork(1)=0.0005
        !rwork(1)=1.0d-6
!
!  RWORK(2)        Relative error tolerance.  This value is used mainly during
!                  the Newton iteration.  RWORK(2) defaults to SQRT(EPMACH)
!                  where EPMACH is the machine relative precision stored in
!                  RWORK(8).
!
        rwork(2)=0.0005
        !rwork(2)=1.0d-6
!
!  RWORK(3)        Minimum allowable predictor stepsize.  If failures of
!                  the Newton correction force the stepsize down to this level,
!                  then the program will give up.  The default value is
!                  SQRT(EPMACH).
!
        rwork(3)=0.5
!
!  RWORK(4)        Maximum allowable predictor step.  Too generous a value
!                  may cause erratic behavior of the program.  The default
!                  value is SQRT(NVAR).
!
        rwork(4)=20.0
!
!  RWORK(5)        Predictor stepsize.  On first call, it should be set by
!                  the user.  Thereafter it is set by the program.
!                  RWORK(5) should be positive.  In order to travel in the
!                  negative direction, see RWORK(6).
!                  The default initial value equals 0.5*(RWORK(3)+RWORK(4)).
!
        rwork(5)=0.5
!
!  RWORK(6)        The local continuation direction, which is either +1.0
!                  or -1.0 .  This asserts that the program is moving in the
!                  direction of increasing or decreasing values of the local
!                  continuation variable, whose index is in IWORK(2).  On first
!                  call, the user must choose IWORK(2).  Therefore, by setting
!                  RWORK(6), the user may also specify whether the program is
!                  to move initially to increase or decrease the variable whose
!                  index is IWORK(2).
!                  RWORK(6) defaults to +1.
!
        rwork(6)=1.d0
!
!  RWORK(7)        A target value.  It is only used if a target index
!                  has been specified through IWORK(5).  In that case, solution
!                  points with the IWORK(5) component equal to RWORK(7) are
!                  to be computed. The code will return each time it finds such
!                  a point.  RWORK(7) does not have a default value.  The
!                  program does not set it, and it is not referenced unless
!                  IWORK(5) has been set.
!
!        RWORK(7)=100.0
!
!  RWORK(8)        EPMACH, the value of the machine precision.  The computer
!                  can distinguish 1.0+EPMACH from 1.0, but it cannot
!                  distinguish 1.0+(EPMACH/2) from 1.0. This number is used
!                  when estimating a reasonable accuracy request on a given
!                  computer.  PITCON computes a value for EPMACH internally.
!
!  RWORK(9)        Not currently used.
!
!  RWORK(10)       A minimum angle used in the steplength computation,
!                  equal to 2.0*ARCCOS(1-EPMACH).
!
!  RWORK(11)       Estimate of the angle between the tangent vectors at the
!                  last two continuation points.
!
!  RWORK(12)       The pseudo-arclength coordinate of the previous continuation
!                  pointl; that is, the sum of the Euclidean distances between
!                  all computed continuation points beginning with the start
!                  point.  Thus each new point should have a larger coordinate,
!                  except for target and limit points which lie between the two
!                  most recent continuation points.
!
!  RWORK(13)       Estimate of the pseudo-arclength coordinate of the current
!                  continuation point.
!
!  RWORK(14)       Estimate of the pseudoarclength coordinate of the current
!                  limit or target point, if any.
!
!  RWORK(15)       Size of the correction of the most recent continuation
!                  point; that is, the maximum norm of the distance between the
!                  predicted point and the accepted corrected point.
!
!  RWORK(16)       Estimate of the curvature between the last two
!                  continuation points.
!
!  RWORK(17)       Sign of the determinant of the augmented matrix at the
!                  last continuation point whose tangent vector has been
!                  calculated.
!
!  RWORK(18)       Not currently used.
!
!  RWORK(19)       Not currently used.
!
!  RWORK(20)       Maximum growth factor for the predictor stepsize based
!                  on the previous secant stepsize.  The stepsize algorithm
!                  will produce a suggested step that is no less that the
!                  previous secant step divided by this factor, and no greater
!                  than the previous secant step multiplied by that factor.
!                  RWORK(20) defaults to 3.
!
!  RWORK(21)       The (Euclidean) secant distance between the last two
!                  computed continuation points.
!
!  RWORK(22)       The previous value of RWORK(21).
!
!  RWORK(23)       A number judging the quality of the Newton corrector
!                  convergence at the last continuation point.
!
!  RWORK(24)       Value of the component of the current tangent vector
!                  corresponding to the current continuation index.
!
!  RWORK(25)       Value of the component of the previous tangent vector
!                  corresponding to the current continuation index.
!
!  RWORK(26)       Value of the component of the current tangent vector
!                  corresponding to the limit index in IWORK(6).
!
!  RWORK(27)       Value of the component of the previous tangent vector
!                  corresponding to the limit index in IWORK(6).
!
!  RWORK(28)       Value of RWORK(7) when the last target point was
!                  computed.
!
!  RWORK(29)       Sign of the determinant of the augmented matrix at the
!                  previous continuation point whose tangent vector has been
!                  calculated.
!
!  RWORK(30)       through RWORK(30+4*NVAR-1) are used by the program to hold
!                  an old and new continuation point, a tangent vector and a
!                  work vector.  Subsequent entries of RWORK are used by the
!                  linear solver.
!
   end subroutine set_options

   function scale_objective(objective_value)
      real(kind=dp), intent(in) :: objective_value
      real(kind=dp) :: scale_objective
      ! The 1.d3 term is to match the ipopt's objective order of magnitude
      scale_objective = objective_value * 1.d3
   end function scale_objective

   function get_number_of_steps(change)
      use approximate, only: ischange_nominal
      real(kind=dp), intent(in) :: change
      integer :: get_number_of_steps
      get_number_of_steps = merge(1, 2, ischange_nominal(change))
   end function get_number_of_steps

   subroutine set_zeta_target_variables(change)
      real(kind=dp), intent(in) :: change
      ! Save values for operating point B (target solution)
      ! XR(507) = VTND1, Vapor Total Input DUMMY 1 (sum of the next three)
      ! XR(508) = VNWATD1, Vapor Input H2O DUMMY 1 (fixed to initial value)
      ! XR(509) = VNCARD1, Vapor Input CO2 DUMMY 1 (change this variable)
      ! XR(511) = VNNITD1, Vapor Input N2 DUMMY 1 (fixed to initial value)
      ! XR(512) = VNTMPD1, Vapor Input TMP DUMMY 1 (fixed to initial value)
      FPAR(2522) = FPAR(2512)                            ! X(508)
      FPAR(2523) = FPAR(2513) + change * FPAR(2513)      ! X(509)
      FPAR(2524) = FPAR(2514)                            ! X(511)
      FPAR(2525) = FPAR(2515)                            ! X(512)
      ! Keep FPAR(2521) below the others
      FPAR(2521) = FPAR(2522) + FPAR(2523) + FPAR(2524)  ! X(507)
   end subroutine set_zeta_target_variables

   subroutine set_target_zeta(current_change, target_change)
      real(kind=dp), intent(in) :: current_change
      real(kind=dp), intent(in) :: target_change
      rwork(7) = 100.0 * current_change / target_change
      fpar(idx_target_zeta) = rwork(7)
   end subroutine set_target_zeta

   subroutine set_options_to_rerun()
      iwork(1) = 0
      iwork(2) = nvar
      iwork(3) = 1
      rwork(6) = 1.d0
   end subroutine set_options_to_rerun

   subroutine enable_task_skipping()
      ipar(idx_pitcon_task_skipping) = 1
   end subroutine enable_task_skipping

   subroutine disable_task_skipping()
      ipar(idx_pitcon_task_skipping) = 0
   end subroutine disable_task_skipping

   subroutine init_print_variables()
      use file_descriptors, only: out_pitcon_file, out_pitcon_unit
      integer :: i
      integer :: index_xm
      real(kind=dp) :: zeta
      real(kind=dp) :: bound_lower_xcv(ncv)
      real(kind=dp) :: bound_upper_xcv(ncv)

      open(unit=out_pitcon_unit, file=out_pitcon_file)
      zeta = 0.d0
      write(out_pitcon_unit,*) zeta, (x_setpoint(i), i = 1, ncv)
      write(out_pitcon_unit,*) zeta, (setpoint_weight(i), i = 1, ncv)

      do i = 1, ncv
         select case (i)
         case (1)
            bound_lower_xcv(1) = 0.9d0
            bound_upper_xcv(1) = 0.9d0
         case (2)
            bound_lower_xcv(2) = x_l(QB)
            bound_upper_xcv(2) = x_u(QB)
         case (3)
            ! Both variables are free, let the ratio free as well
            bound_lower_xcv(3) = x_l(LCARFA)
            bound_upper_xcv(3) = x_u(LCARFA)
         case default
            index_xm = manip_var_index(i + 2)
            bound_lower_xcv(i) = x_l(index_xm)
            bound_upper_xcv(i) = x_u(index_xm)
         end select
      end do
      write(out_pitcon_unit,*) zeta, (bound_lower_xcv(i), i = 1, ncv)
      write(out_pitcon_unit,*) zeta, (bound_upper_xcv(i), i = 1, ncv)
   end subroutine init_print_variables

   subroutine print_variables()
      use file_descriptors, only: out_pitcon_unit
      integer :: i
      integer :: index_xm
      real(kind=dp) :: xm(nmv)
      real(kind=dp) :: xt(ncv)

      do i = 1, nmv
         index_xm = MANIP_VAR_INDEX(i)
         xm(i) = x(index_xm)
      end do

      do i = 1, ncv
         select case (i)
         case (1)
            xt(i) = (xm(1) - xm(2)) / xm(1)
         case (2)
            xt(i) = xm(3)
         case (3)
            xt(i) = xm(4) / xm(5)
            ! Activate this instead if you want to use the
            ! stripper's temperature insstead of CO2 loading
            !  xt(i) = xm(4)
         case default
            xt(i) = xm(i + 2)
         end select
      end do
      write(out_pitcon_unit,*) x(nvar), (xt(i), i = 1, ncv)
   end subroutine print_variables

   subroutine close_print_variables()
      use file_descriptors, only: out_pitcon_unit
      close(out_pitcon_unit)
   end subroutine close_print_variables

   subroutine read_variable_names(names)
      use file_descriptors, only: bounds_pitcon_file
      character(len=8), intent(out) :: names(VARIABLES_SIZE)
      integer :: i, unit
      integer :: j_
      real(kind=dp) :: xl_, xu_

      open (newunit=unit, file=bounds_pitcon_file)
      do i = 1, VARIABLES_SIZE
         read(unit, &
            ! counter, name,  lower bound, upper bound
            '(t2, i3, t7, a8, t20, f16.6, t41, f16.6)') j_, names(i), xl_, xu_
      end do
      close(unit)
  end subroutine read_variable_names

end module controllability_assessment
