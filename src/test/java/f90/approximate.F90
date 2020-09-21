module approximate
   use sa_parameters, only: dp
   implicit none
   private

   integer, parameter :: ICHANGE = 2
   real(kind=dp), parameter :: TEMP_TO_INCREASE_CHANGE = 0.5d0
   real(kind=dp), parameter, public :: CHANGE_NOMINAL = 0.02d0
   ! Acceptable changes for the target value of the continuation algorithm
   real(kind=dp), parameter :: CHANGE_ARRAY(ICHANGE) = &
      (/ CHANGE_NOMINAL, 0.10d0 /)

   real(kind=dp), parameter, public :: TIME_LIMIT_IP = 10.d0
   real(kind=dp), parameter, public :: TIME_LIMIT_PT = 5.d0

   logical, public :: DID_CHANGE_OCCURED
   integer :: INDEX_CHANGE

   public :: init
   public :: get_change
   public :: ischange_nominal
   public :: set_change_index
   public :: set_change_to_nominal

contains

   subroutine init()
      DID_CHANGE_OCCURED = .false.
      INDEX_CHANGE = init_index_change()
   end subroutine init

   function init_index_change()
      integer :: init_index_change
#ifdef DISABLE_GRADUAL_CHANGE
      ! When compiled __with__ DISABLE_GRADUAL_CHANGE=1, set the disturbance
      ! equal to the last element of CHANGE_ARRAY, that is 10 %
      init_index_change = ICHANGE
      ! Uncommnet to force to 2 % change
      ! init_index_change = 1
#else
      ! When compiled __without__ DISABLE_GRADUAL_CHANGE=1, set the
      ! disturbance equal to the first element of CHANGE_ARRAY, that is 2 %
      init_index_change = 1
#endif
#ifdef FORCE_NOMINAL_CHANGE
      ! When compiled __with__ FORCE_NOMINAL_CHANGE=1
      ! This takes precedence over the DISABLE_GRADUAL_CHANGE flag
      init_index_change = 1
#endif
   end function init_index_change

   function get_change()
      real(kind=dp) :: get_change
      get_change = CHANGE_ARRAY(INDEX_CHANGE)
   end function get_change

   subroutine set_change_to_nominal()
      INDEX_CHANGE = 1
   end subroutine set_change_to_nominal

   function ischange_nominal(change)
      real(kind=dp), intent(in) :: change
      logical :: ischange_nominal
      logical :: isclose
      ischange_nominal = isclose(change - CHANGE_NOMINAL, 1.d-6)
   end function ischange_nominal

   subroutine set_change_index(current_temp)
#ifndef DISABLE_LOOKUP_TABLE
      use memoization, only: memoization_reset => reset
#endif
      real(kind=dp), intent(in) :: current_temp
      integer :: index_change_prev
      ! Increase change for approximate computing target when current
      ! temperature reaches TEMP_TO_INCREASE_CHANGE. Then increase the
      ! change whenever the temperature is reduced until the maximum target
      ! changed is reached.
      index_change_prev = INDEX_CHANGE
      if (current_temp > TEMP_TO_INCREASE_CHANGE) then
         INDEX_CHANGE = 1
      else if (index_change_prev < ICHANGE) then
         INDEX_CHANGE = index_change_prev + 1
      else if (index_change_prev == ICHANGE) then
         INDEX_CHANGE = ICHANGE
      end if
#ifdef DISABLE_GRADUAL_CHANGE
      ! When compiled __with__ DISABLE_GRADUAL_CHANGE=1
      INDEX_CHANGE = init_index_change()
#endif
#ifdef FORCE_NOMINAL_CHANGE
      ! When compiled __with__ FORCE_NOMINAL_CHANGE=1
      ! This takes precedence over the DISABLE_GRADUAL_CHANGE flag
      INDEX_CHANGE = 1
#endif
      if (INDEX_CHANGE /= index_change_prev) then
#ifndef DISABLE_LOOKUP_TABLE
         ! Reset cache when target change occured
         ! and when compiled __without__ DISABLE_LOOKUP_TABLE=1
         call memoization_reset()
#endif
         ! did_change_occured to mark that a disturbance change occured
         DID_CHANGE_OCCURED = .true.
      else
         DID_CHANGE_OCCURED = .false.
      end if
   end subroutine set_change_index

end module approximate
