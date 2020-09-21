C
C Parallel Stochastic Simulated Annealing Algorithm
C With Data Mining and Clustering Techniques
C Author: Dr.Sakis Papadopoulos
C Minor amendments for parallelization by Akis Giannakoudis
C Modifications in the Subroutine Simulate to run in Parallel by P. Natsiavas
C Addition of MEAPITCON by P. Natsiavas
C

      program main
        implicit double precision (a-h,o-z)
        include 'mpi.inc' ! includes MPI attributes
        include 'SA_params.inc' !includes Simulated annealing parameters
!-------***GLOBAL VARIABLES***----------------------------------------
        dimension
!                 Temperature array
     &            Temp(IT_MAX),
!                 Design variables matrix for each lmc iteration
     &            var_mov(NVARS_MOVE, LMC),
!                 Design variables for each temperature iteration
     &            var_mov_init(NVARS_MOVE),
     &            TL(3),
!                 Accepted objective values per LMC iteration
     &            accepted_obj(LMC),
!                 Declaration of file units
     &            iun(IFILES_SIZE),
!                 Design variables array
     &            var_sim(NVARS_MOVE, 0:NPRC_MAX-1),
!                 Design variables helper matrix for exit_flag_not_zero
     &            var_sim_aux(NVARS_MOVE, NPRC_MAX),
!                 Last successful move
     &            alast_suc_move(NVARS_MOVE),
!                 Best move
     &            best_move(NVARS_MOVE)

!       data for timing
        double precision wtime_begin(0:NPRC_MAX-1),
     &                   wtime_end(0:NPRC_MAX-1)
        logical found_solution, is_master_node

        integer i_count_suc, i_count_rej
        common /calling/ i_count_suc, i_count_rej

        integer ibest_prcID
        double precision E_tot_min
        common /best_GLOBAL/ E_tot_min, ibest_prcID

!       START MPI
        call sa_mpi_init(Nprc, iprcID)
        call cpu_timer_init(wtime_begin, iprcID)
        print *,'START',iprcID,'OF ',Nprc,'PROCESS ELEMENTS'

        if (.not. is_master_node(iprcID)) then
!         read and save to common blocks
          call read_init_sol_and_bounds()
          call frpinit()
        endif

        if (is_master_node(iprcID)) then
!         The initialization will take place in the
!         master node (MASTER) which has the ID = 0
          call init_counters(iglob, itmp, iexit_code, iexit_LMC,
     &                       isuc, irej, ideriv_counter, index_change)
          call initiallizations(object_temp, best_move,
     &                          itmp, Temp, iglob, best_so_far,
     &                          worst_val, slow_cool, amin_temp,
     &                          object_lmc_current, iun, TL, Nprc,
     &                          index_change, iflag_sa,
     &                          wtime_begin(MASTER))
          call init_design_arrays(best_move, alast_suc_move,
     &                            var_mov, var_mov_init)
        else  ! Slaves DO work
          call slaves_mpi(iprcID, iflag_sa)
        endif  !iprcID

!-------CHECK IFLAG FOR SOLUTION---------------------------------------
        if (.not. found_solution(iflag_sa)) then
          ! true only for bad initial solution
          if (is_master_node(iprcID)) then
            call init_exit_flag_not_zero(var_sim_aux, var_mov, Nprc,
     &                                   ifl_ne_0, il_old, inval_sol)
          endif
          do while (.not. found_solution(iflag_sa))
            if (is_master_node(iprcID)) then
              call exit_flag_not_zero(ifl_ne_0, iexit_code, Temp, itmp,
     &                                Nprc, var_sim_aux, il_old,
     &                                inval_sol, ideriv_counter,
     &                                index_change, alast_suc_move,
     &                                best_move, iflag_sa, change)
              if (found_solution(iflag_sa)) then
                call annealing(object_lmc_current, object_lmc_previous,
     &                         var_mov, isuc, amed, accepted_obj,
     &                         irej, ilev, Temp, itmp, iglob, iun,
     &                         Nprc, var_sim, alast_suc_move,
     &                         wtime_begin(MASTER), change)
              end if
            else  ! Slaves DO work
              call slaves_mpi(iprcID, iflag_sa)
            endif  ! iprcID
          enddo
        endif  ! exit_flag_not_zero

        call broadcast_exit_code(iexit_code)
        if (iexit_code.eq.1) then
          write(*,*)'EXIT INITIALLIZATION CODE 1'
          goto 991  ! EXIT
        endif
        call broadcast_temp(itmp, Temp, amin_temp)

!-------START TEMPERATURE ITERATIONS-----------------------------------
        do while(Temp(itmp).ge.amin_temp)  ! known to each node
          if (is_master_node(iprcID)) then
            ! iun(2) = out_all.txt
            write(iun(2),*) 'TEMP', Temp(itmp),
     &                      'OBJ', object_temp,
     &                      'SUCCESS', i_count_suc,
     &                      'FAIL IPOPT', i_count_rej,
     &                      'min Temp', amin_temp,
     &                      'slow_cool', slow_cool,
     &                      'iter', iglob
            write(*,*) 'TEMP', Temp(itmp),
     &                 'OBJ', object_temp,
     &                 'best', best_so_far,
     &                 '#ID:', ibest_prcID,
     &                 'success', i_count_suc,
     &                 'FAIL IPOPT', i_count_rej,
     &                 'iter', iglob
! setting objective and variables of last
            object_lmc_current = object_temp
            do ivmoves = 1, NVARS_MOVE
! equal to first Markov iteration
              var_mov(ivmoves, 1) = var_mov_init(ivmoves)
            enddo
            isuc = 1  ! Re-initializing counter of succesful moves
            amed = 0.d0  ! counter for median used in cool_SA
            worst = worst_val
          endif  ! MASTER

!---------START MARKOV CHAIN ITERATIONS--------------------------------
          do ilev = 2, LMC  !known to each node
            if (is_master_node(iprcID)) then
              call prep_annealing(var_mov, iexit_code, ilev,
     &                            Temp, itmp, Nprc, var_sim,
     &                            ideriv_counter, index_change,
     &                            alast_suc_move, best_move, iflag_sa,
     &                            change)
              if (found_solution(iflag_sa)) then
                call annealing(object_lmc_current, object_lmc_previous,
     &                         var_mov, isuc, amed, accepted_obj, irej,
     &                         ilev, Temp, itmp, iglob, iun, Nprc,
     &                         var_sim, alast_suc_move,
     &                         wtime_begin(MASTER), change)
              endif
            else  ! Slaves DO work
              call slaves_mpi(iprcID, iflag_sa)
            endif  !iprcID

!-----------CHECK IFLAG FOR SOLUTION-----------------------------------
            if (.not. found_solution(iflag_sa)) then
              if (is_master_node(iprcID)) then
                call init_exit_flag_not_zero(var_sim_aux, var_mov, Nprc,
     &                                       ifl_ne_0, il_old,
     &                                       inval_sol)
              endif
              do while (.not. found_solution(iflag_sa))
                if (is_master_node(iprcID)) then
                  call exit_flag_not_zero(ifl_ne_0, iexit_code, Temp,
     &                                    itmp, Nprc, var_sim_aux,
     &                                    il_old, inval_sol,
     &                                    ideriv_counter, index_change,
     &                                    alast_suc_move, best_move,
     &                                    iflag_sa, change)
                  if (found_solution(iflag_sa)) then
                    call annealing(object_lmc_current,
     &                             object_lmc_previous, var_mov, isuc,
     &                             amed, accepted_obj, irej, ilev,
     &                             Temp, itmp, iglob, iun, Nprc,
     &                             var_sim, alast_suc_move,
     &                             wtime_begin(MASTER), change)
                  end if
                else  ! Slaves DO work
                  call slaves_mpi(iprcID, iflag_sa)
                endif  ! iprcID
              enddo
            endif   !exit_flag_not_zero

            call broadcast_exit_code(iexit_code)
            if (iexit_code.eq.1) then
              write(*,*) 'EXIT ANNEALING CODE 1'
              goto 991  ! EXIT
            endif

            if (is_master_node(iprcID)) then
              call markov_chain_checks(irej, object_lmc_current, ilev,
     &                                 worst, best_so_far, iexit_code,
     &                                 isuc, iexit_LMC, ilev_last, iun,
     &                                 alast_suc_move, best_move)
              call object_derivative_check(object_lmc_current,
     &                                     object_lmc_previous,
     &                                     ideriv_counter, iun,
     &                                     iexit_deriv)
            endif

            call broadcast_exit_code(iexit_deriv)
            if (iexit_deriv.eq.1) then
              goto 991  ! EXIT OBJECT_DERIVATIVE_CHECK CONSTANT
            endif
            call broadcast_exit_code(iexit_code)
            if (iexit_code.eq.1) then
              goto 991  ! EXIT MARKOV_CHAIN_CHECKS
            endif
            call broadcast_exit_code(iexit_LMC)
            if(iexit_LMC.eq.1) then
              goto 191  ! exiting current LMC, reduce Temp
            endif

          enddo
!---------END MARKOV CHAIN ITERATIONS----------------------------------

191       if (is_master_node(iprcID)) then
            call temperature_iterations_checks(itmp, amin_temp,
     &                                         object_lmc_current,
     &                                         iexit_code, iun, T_lim)
            index_change_prev = index_change
            call get_target_change_index(index_change,
     &                                   index_change_prev,
     &                                   Temp(itmp))
          endif
          call broadcast_exit_code(iexit_code)
          if (iexit_code.eq.1) then
            goto 991  ! EXIT TEMPERATURE_ITER_CHECKS
          endif

          if (is_master_node(iprcID)) then
            object_temp = object_lmc_current
            do ivmoves = 1, NVARS_MOVE
              var_mov_init(ivmoves) = var_mov(ivmoves, ilev_last)
            enddo
            call cool_SA(itmp, Temp, worst, best_so_far,
     &                   isuc, amed, accepted_obj)

            if (itmp.gt.3 .and. slow_cool.lt.T_lim) then
              write(iun(2),*)' VERY SLOW COOLING-ABORTING EXECUTION'
              write(*,*)' VERY SLOW COOLING-ABORTING EXECUTION'
              write(*,*)'min Temp,slow_cool',amin_temp,slow_cool
              iexit_code = 1
            endif
          endif  ! iprcID=0

          call broadcast_exit_code(iexit_code)
          if (iexit_code.eq.1) then
            goto 991  ! EXIT
          endif

          slow_cool = (TL(3) - TL(1))/TL(2)
          TL(3) = TL(2)
          TL(2) = TL(1)
          TL(1) = Temp(itmp)

          call broadcast_temp(itmp, Temp, amin_temp)
        enddo
!-------END TEMPERATURE ITERATIONS-------------------------------------

991     continue  ! EXIT
        call cpu_timer_end(Nprc, wtime_end, wtime_begin,
     &                     iprcID, iun(2))

        if (is_master_node(iprcID)) then
          call close_file_descriptors(iun)
#ifdef ITEST
          call test_write_cache_lookup()
#endif
        endif
        call MPI_FINALIZE(IERR)
      end program main


!-----SUBROUTINES------------------------------------------------------
      subroutine exit_flag_not_zero(ifl_ne_0, iexit_code, Temp, itmp,
     &                              Nprc, var_sim_aux, il_old,
     &                              inval_sol, ideriv_counter,
     &                              index_change, alast_suc_move,
     &                              best_move, iflag_sa, change)
        implicit none
        include 'SA_params.inc'

        integer ifl_ne_0, iexit_code, itmp, Nprc, il_old, inval_sol
        integer ideriv_counter, index_change, iflag_sa, id, ivmoves
        double precision change, get_change
        double precision Temp(IT_MAX),
     &                   var_sim(NVARS_MOVE, 0:Nprc-1),
     &                   var_sim_aux(NVARS_MOVE, Nprc-1),
     &                   var_prev(NVARS_MOVE),
     &                   alast_suc_move(NVARS_MOVE),
     &                   best_move(NVARS_MOVE)

        iexit_code = 0
        ifl_ne_0 = ifl_ne_0 + 1
        inval_sol = inval_sol + 1

        do id = 1, Nprc-1
          do ivmoves = 1, NVARS_MOVE
            var_prev(ivmoves) = var_sim_aux(ivmoves, id)
          enddo
          call SA_moves(var_sim, var_prev, Temp, itmp, ifl_ne_0,
     &                  inval_sol, id, Nprc, ideriv_counter,
     &                  alast_suc_move, best_move)
          do ivmoves = 1, NVARS_MOVE
            var_sim_aux(ivmoves, id) = var_sim(ivmoves, id)
          enddo
        enddo
        change = get_change(index_change)
        call master_mpi(var_sim, Nprc, change, iflag_sa)

        if (inval_sol .eq. 5*LMC)then
          write(*,*) 'TOO MANY CONTINUOUS INVALID '//
     &               'STATES-ABORTING EXECUTION'
          iexit_code = 1  ! exit_code='true' means abort program
          return
        endif

        if (iflag_sa .gt. 0) then
          if (ifl_ne_0 .eq. LMC-1) then
            ifl_ne_0 = 0
          endif
          return
        elseif (iflag_sa .eq. 0) then
          ifl_ne_0 = il_old
          return
        endif

      return
      end


      subroutine cool_SA(itmp, Temp, worst, best_so_far,
     &                   isuc, amed, accepted_obj)
        implicit none
        include 'SA_params.inc'

        integer itmp, isuc, im
        double precision worst, best_so_far, amed
        double precision amedian, a_val, sigmaaux, sigma, DEmax
        double precision delta
        parameter (delta = 0.12d0)
        double precision accepted_obj(LMC),
     &                   Temp(IT_MAX)
        logical isclose

        amedian = amed/dble(isuc)
        if (isuc .lt. 2) then
          a_val = 0.996d0
          Temp(itmp) = a_val*Temp(itmp-1)
          return
        endif
        sigmaaux = 0.d0
        do im = 1, isuc
          sigmaaux = sigmaaux + (amedian - accepted_obj(im))**2
        enddo
        sigma = sqrt(sigmaaux/(isuc - 1))
        DEmax = min(amedian + 3*sigma, worst) - best_so_far
        if (isclose(DEmax, 1.d-6)) then
          write(*,*)'DEmax is zero'
          a_val = 0.996d0
        else
          a_val = 1.d0/(1.d0 + (log(1.d0 + delta)
     &                         *Temp(itmp-1)/DEmax))
        endif
        Temp(itmp) = a_val*Temp(itmp-1)

      return
      end


      subroutine temperature_iterations_checks(itmp, amin_Temp,
     &                                         object_lmc_current,
     &                                         iexit_code, iun, T_lim)
        implicit none
        include 'SA_params.inc'
        integer itmp, iexit_code
        integer iun(IFILES_SIZE)
        double precision amin_Temp, object_lmc_current, T_lim

        iexit_code = 0
        amin_Temp = max(SMALL_V, SMALL_V*abs(object_lmc_current))
        T_lim = amin_Temp*SMALL_V
        itmp = itmp + 1
        if (itmp .ge. IT_MAX) then
!         iun(2) = out_all.txt
          write(iun(2),*)'MAXIMUM TEMPERATURE ITERATION NUMBER '//
     &                   'REACHED-TERMINATING PROGRAM'
          write(*,*)'MAXIMUM TEMPERATURE ITERATION NUMBER '//
     &              'REACHED-TERMINATING PROGRAM'
          iexit_code = 1
        endif

      return
      end


      subroutine markov_chain_checks(irej, object_lmc_current, ilev,
     &                               worst, best_so_far, iexit_code,
     &                               isuc, iexit_LMC, ilev_last, iun,
     &                               alast_suc_move, best_move)
        implicit none
        include 'SA_params.inc'

        integer ivmoves
        integer irej, ilev, iexit_code, isuc, iexit_LMC, ilev_last
        integer iun(IFILES_SIZE)
        double precision object_lmc_current, worst, best_so_far
        double precision best_move(NVARS_MOVE),
     &                   alast_suc_move(NVARS_MOVE)

        iexit_code = 0
        iexit_LMC = 0

        if (irej .gt. 5*LMC) then
!         iun(2) = out_all.txt
          write(iun(2),*) 'TOO MANY SUCCESSIVE REJECTED '//
     &                    'MOVES-ABORTING EXECUTION'
          write(*,*) 'TOO MANY SUCCESSIVE REJECTED '//
     &               'MOVES-ABORTING EXECUTION'
          iexit_code = 1
        endif

        if (object_lmc_current .gt. worst) then
          ! find worst objective in each temperature level
          worst = object_lmc_current
        endif

        if (object_lmc_current .lt. best_so_far) then
!         find best objective found so far
          best_so_far = object_lmc_current
          do ivmoves = 1, NVARS_MOVE
            best_move(ivmoves) = alast_suc_move(ivmoves)
          enddo
          write(*,*)'new_best',best_so_far
        endif

        ilev_last = ilev
        if (isuc .eq. LMC/2) then
!         exit current Markov Chain, reduce Temp
          iexit_LMC = 1
        endif

      return
      end


      subroutine object_derivative_check(object_lmc_current,
     &                                   object_lmc_previous,
     &                                   ideriv_counter,
     &                                   iun, iexit_deriv)
        implicit none
        include 'SA_params.inc'

        integer ideriv_counter, iexit_deriv
        integer iun(IFILES_SIZE)
        double precision object_lmc_current, object_lmc_previous
        double precision diff_obj
        logical isclose

        iexit_deriv = 0

        diff_obj = object_lmc_current - object_lmc_previous
        if (isclose(diff_obj, 1.d-6)) then 
          ideriv_counter = ideriv_counter + 1

          if (ideriv_counter .gt. IDERIV_BREAK_COUNTER) then
            iexit_deriv = 1

!           iun(2) = out_all.txt
            write(iun(2),*) 'DERIVATIVE CHECK: '//
     &                      'OBJECTIVE REMAINED CONSTANT-'//
     &                      'ABORTING EXECUTION'
            write(*,*) 'DERIVATIVE CHECK: '//
     &                 'OBJECTIVE REMAINED CONSTANT-'//
     &                 'ABORTING EXECUTION'
          end if

        else
          ideriv_counter = 0
        end if

#ifdef DISABLE_GRADIENT_CRITERION
C
C Enter when compile __with__ DISABLE_GRADIENT_CRITERION=1
C
        ideriv_counter = 0
        iexit_deriv = 0
#endif
        
      return
      end


      subroutine get_target_change_index(index_change,       ! OUT
     &                                   index_change_prev,  ! OUT/IN
     &                                   cur_temp)           ! IN
        implicit none
        include 'approximate.inc'

        integer index_change, index_change_prev
#ifdef DISABLE_GRADUAL_CHANGE
        integer init_index_change
#endif
        double precision cur_temp
C
C Increase change for approximate computing target when current
C temperature reaches TEMP_TO_INCREASE_CHANGE. Then increase the
C change whenever the temperature is reduced until the maximum target
C changed is reached.
C
        if (cur_temp .gt. TEMP_TO_INCREASE_CHANGE) then
          index_change = 1
        elseif (index_change_prev .lt. ICHANGE) then
          index_change = index_change_prev + 1
        elseif (index_change_prev .eq. ICHANGE) then
          index_change = ICHANGE
        endif
#ifdef DISABLE_GRADUAL_CHANGE
C
C When compiled __with__ DISABLE_GRADUAL_CHANGE=1
C
        index_change = init_index_change()
#endif
#ifdef FORCE_NOMINAL_CHANGE
C
C When compiled __with__ FORCE_NOMINAL_CHANGE=1
C This takes precedence over the DISABLE_GRADUAL_CHANGE flag
C
        index_change = 1
#endif
#ifndef DISABLE_LOOKUP_TABLE
        if (index_change .ne. index_change_prev) then
C
C Reset cache when target change occured
C and when compiled __without__ DISABLE_LOOKUP_TABLE=1
C
          call reset_cache()
        endif
#endif
        index_change_prev = index_change

      return
      end
