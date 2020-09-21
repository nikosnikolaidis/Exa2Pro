      subroutine SA_moves(var_sim, var_prev, Temp, itmp, lmc_lev,
     &                    inval_sol, id, Nprc, ideriv_counter,
     &                    alast_suc_move, best_move)
        implicit none
        include 'SA_params.inc'

        integer i, ivmoves
        integer Nprc, id, itmp, lmc_lev, ideriv_counter, inval_sol
        integer istore(NVARS_MOVE)
        double precision RANDOM_GEN, work_out
        double precision var_sim(NVARS_MOVE, 0:Nprc-1),
     &                   var_prev(NVARS_MOVE),
     &                   Temp(IT_MAX),
     &                   add_base(NVARS_MOVE),
     &                   plus(NVARS_MOVE, Nprc-1),
     &                   alast_suc_move(NVARS_MOVE),
     &                   best_move(NVARS_MOVE)

        call init_var_sim(var_sim, best_move, var_prev,
     &                    Nprc, id, Temp, itmp)
C
C Making sure you have a different random seed each time
C
        call store_isd(istore, NVARS_MOVE, Temp(itmp), lmc_lev)
        call moves_radius(Temp, itmp, lmc_lev, add_base, plus,
     &                    istore, id, Nprc, ideriv_counter,
     &                    alast_suc_move, best_move)
        work_out = RANDOM_GEN(istore(1))
        if (work_out.le.0.5d0) then
          if (work_out.le.0.25d0) then
            do i = 1, NVARS_MOVE/2
              var_sim(i, id) = add_base(i) + plus(i, id)
            enddo
          endif
          if (work_out.gt.0.25d0) then
            do i = NVARS_MOVE/2 + 1, NVARS_MOVE
              var_sim(i, id) = add_base(i) + plus(i, id)
            enddo
          endif
        elseif (work_out.gt.0.5d0) then
          if (work_out.le.0.75d0) then
            do i = 1, NVARS_MOVE/2
              var_sim(i, id) = add_base(i) - plus(i, id)
            enddo
          endif
          if (work_out.gt.0.75d0) then
            do i = NVARS_MOVE/2 + 1, NVARS_MOVE
              var_sim(i, id) = add_base(i) - plus(i, id)
            enddo
          endif
        endif

        call exceeded_limits(var_sim, istore, id, Nprc, alast_suc_move)
        if (inval_sol.eq.3*LMC) then
C
C this can happen only from exit_flag_not_zero subroutine
C
          do ivmoves = 1, NVARS_MOVE
            var_sim(ivmoves, id) = alast_suc_move(ivmoves)
          enddo
        endif

      return
      end


      subroutine exceeded_limits(var_sim, istore, id, Nprc,
     &                           alast_suc_move)
        implicit none
        include 'SA_params.inc'

        integer Nprc, id, i, ivmoves, isd1, iup, ilow, NUMRND
        integer istore(NVARS_MOVE)
        double precision diff, diff_, work_aux
        double precision var_sim(NVARS_MOVE, 0:Nprc-1),
     &                   alast_suc_move(NVARS_MOVE),
     &                   alim(NVARS_MOVE, 2)

        do i = 1, NVARS_MOVE
          alim(i, 1) = BOUND_UPPER_LENG
          alim(i, 2) = BOUND_LOWER_LENG
        enddo

        do ivmoves=1,NVARS_MOVE
          isd1 = istore(ivmoves)
C Upper limit violation
          if (var_sim(ivmoves, id) .gt.
     &          alim(ivmoves, 1)) then
            diff_ = abs(alim(ivmoves,1) - alast_suc_move(ivmoves))/2.d0
            diff = dble(int(diff_))  ! whole number
C            diff = round_decimal(diff_)
            if(isd1.gt.0.5)then
              work_aux = alast_suc_move(ivmoves) - diff
            else
              work_aux = alast_suc_move(ivmoves) + diff
            endif
            if(work_aux.gt.alim(ivmoves,1))then
              write(*,*)'DBEX_UP'
              iup = int(alim(ivmoves, 1))
              ilow = int(alim(ivmoves, 2))
              work_aux = dble(NUMRND(ilow, iup, isd1))
            endif
            var_sim(ivmoves, id) = work_aux
          endif
C Lower limit violation
          if (var_sim(ivmoves, id) .lt.
     &          alim(ivmoves, 2)) then
            diff_ = abs(alim(ivmoves,2) - alast_suc_move(ivmoves))/2.d0
            diff = dble(int(diff_))  ! whole number
C            diff = round_decimal(diff_)
            if(isd1.gt.0.5)then
              work_aux = alast_suc_move(ivmoves) - diff
            else
              work_aux = alast_suc_move(ivmoves) + diff
            endif
            if(work_aux.lt.alim(ivmoves, 2))then
              write(*,*)'DBEX_DOWN'
              iup = int(alim(ivmoves, 1))
              ilow = int(alim(ivmoves, 2))
              work_aux = dble(NUMRND(ilow, iup, isd1))
            endif
            var_sim(ivmoves, id) = work_aux
          endif
        enddo

      return
      end


      subroutine moves_radius(Temp, itmp, lmc_lev,
     &                        add_base, plus, istore,
     &                        id, Nprc, ideriv_counter,
     &                        alast_suc_move, best_move)
        implicit none
        include 'SA_params.inc'

        integer Nprc, id, itmp, lmc_lev, ideriv_counter
        integer istore(NVARS_MOVE)
        double precision diff_Temp, get_diff_Temp
        double precision Temp(IT_MAX),
     &                   add_base(NVARS_MOVE),
     &                   alast_suc_move(NVARS_MOVE),
     &                   best_move(NVARS_MOVE),
     &                   plus(NVARS_MOVE, Nprc-1)

        diff_Temp = get_diff_Temp(Temp, itmp)

#ifdef SA_REFERENCE_STRATEGY
C
C Enter only when compiled __with__ SA_REFERENCE_STRATEGY=1
C
        call sa_reference_strategy(add_base, plus,
     &                             best_move, alast_suc_move,
     &                             diff_Temp, istore,
     &                             Nprc, id, lmc_lev)
#else
C
C Enter when __not__ compiled with SA_REFERENCE_STRATEGY=1
C This is the default strategy
C
        call sa_approximate_strategy(add_base, plus,
     &                               best_move, alast_suc_move,
     &                               diff_Temp, istore,
     &                               Nprc, id, lmc_lev,
     &                               ideriv_counter)
#endif

      return
      end


      subroutine sa_reference_strategy(add_base, plus,
     &                                 best_move, alast_suc_move,
     &                                 diff_Temp, istore,
     &                                 Nprc, id, lmc_lev)
        implicit none
        include 'SA_params.inc'

        integer Nprc, id, lmc_lev, NUMRND, ivmoves
        integer istore(NVARS_MOVE)
        double precision diff_Temp
        double precision add_base(NVARS_MOVE),
     &                   alast_suc_move(NVARS_MOVE),
     &                   best_move(NVARS_MOVE),
     &                   plus(NVARS_MOVE, Nprc-1)

        if (diff_Temp .lt. DIFF_TEMP_TO_REDUCE_PLUS) then
          do ivmoves = 1, NVARS_MOVE
            add_base(ivmoves) = best_move(ivmoves)
            plus(ivmoves, id) = dble(NUMRND(1, 3, istore(ivmoves)))
          enddo
        else
          if (lmc_lev .le. LMC/2) then
            do ivmoves = 1, NVARS_MOVE
              add_base(ivmoves) = alast_suc_move(ivmoves)
              plus(ivmoves, id) = dble(NUMRND(1, 18, istore(ivmoves)))
            enddo
          else
            do ivmoves = 1, NVARS_MOVE
              add_base(ivmoves) = best_move(ivmoves)
              plus(ivmoves, id) = dble(NUMRND(1, 18, istore(ivmoves)))
            enddo
          endif
        endif
        
      return
      end


      subroutine sa_approximate_strategy(add_base, plus,
     &                                   best_move, alast_suc_move,
     &                                   diff_Temp, istore,
     &                                   Nprc, id, lmc_lev,
     &                                   ideriv_counter)
        implicit none
        include 'SA_params.inc'

        integer Nprc, id, lmc_lev, ideriv_counter, NUMRND, ivmoves
        integer ilow, iup
        integer istore(NVARS_MOVE)
        double precision diff_Temp
        double precision add_base(NVARS_MOVE),
     &                   alast_suc_move(NVARS_MOVE),
     &                   best_move(NVARS_MOVE),
     &                   plus(NVARS_MOVE, Nprc-1)

        ilow = int(BOUND_LOWER_LENG)
        iup = int(BOUND_UPPER_LENG)

        if (ideriv_counter .gt. IDERIV_BREAK_COUNTER/3) then
          do ivmoves = 1, NVARS_MOVE
            add_base(ivmoves) = best_move(ivmoves)
            plus(ivmoves, id) = 0.d0
          enddo
        elseif (diff_Temp .lt. DIFF_TEMP_TO_REDUCE_PLUS * 0.1) then
          do ivmoves = 1, NVARS_MOVE
            add_base(ivmoves) = best_move(ivmoves)
            plus(ivmoves, id) = dble(NUMRND(0, 1, istore(ivmoves)))
          enddo
        elseif (diff_Temp .lt. DIFF_TEMP_TO_REDUCE_PLUS * 0.5) then
          do ivmoves = 1, NVARS_MOVE
            add_base(ivmoves) = best_move(ivmoves)
            plus(ivmoves, id) = dble(NUMRND(0, 2, istore(ivmoves)))
          enddo
        elseif (diff_Temp .lt. DIFF_TEMP_TO_REDUCE_PLUS) then
          do ivmoves = 1, NVARS_MOVE
            add_base(ivmoves) = best_move(ivmoves)
            plus(ivmoves, id) = dble(NUMRND(0, 4, istore(ivmoves)))
          enddo
        else
          if (lmc_lev .le. int(dble(LMC)/3.0)) then
            do ivmoves = 1, NVARS_MOVE
              add_base(ivmoves) = alast_suc_move(ivmoves)
              plus(ivmoves, id) = dble(NUMRND(1, 18, istore(ivmoves)))
            enddo
          else
            do ivmoves = 1, NVARS_MOVE
              add_base(ivmoves) = best_move(ivmoves)
              plus(ivmoves, id) = dble(NUMRND(1, 10, istore(ivmoves)))
            enddo
          endif
        endif

      return
      end


      double precision function get_diff_Temp(Temp, itmp)
        implicit none
        include 'SA_params.inc'

        integer itmp
        double precision Temp(IT_MAX)

        if (itmp .le. 1) then
          get_diff_Temp = 1000.d0
        else
          get_diff_Temp = abs(Temp(itmp) - Temp(itmp-1))
        endif

      return
      end


      subroutine init_var_sim(var_sim,
     &                        best_move, var_prev, Nprc, id,
     &                        Temp, itmp)
        implicit none
        include 'SA_params.inc'

        integer Nprc, id, itmp, ivmoves
        double precision diff_Temp, get_diff_Temp
        double precision var_sim(NVARS_MOVE, 0:Nprc-1),
     &                   var_prev(NVARS_MOVE),
     &                   best_move(NVARS_MOVE),
     &                   Temp(IT_MAX)
        diff_Temp = get_diff_Temp(Temp, itmp)

        if (diff_Temp .lt. DIFF_TEMP_TO_REDUCE_PLUS) then
C
C Initialize every new value to be equal to the best move.
C The ones that will not change randomly wil have the old value
C
          do ivmoves = 1, NVARS_MOVE
            var_sim(ivmoves, id) = best_move(ivmoves)
          enddo
        else
C
C Initialize every new value to be equal to the previous move.
C The ones that will not change randomly wil have the old value
C
          do ivmoves = 1, NVARS_MOVE
            var_sim(ivmoves, id) = var_prev(ivmoves)
          enddo
        endif

      return
      end


      integer function NUMRND(N1, N2, isd_in)
C
C      Select randomly an integer between N1 and N2 (N1=<N2)
C
        implicit none
        integer N1, N2, isd_in
        double precision DN, RANDOM_GEN

        DN = dble(N2 - N1) + 9.99999d-1
        NUMRND = idint(RANDOM_GEN(isd_in)*DN) + N1
        if (NUMRND .lt. 0) then
          write(*,*)'PROBLEM IN NUMRND', NUMRND
          NUMRND = 0  ! this shouldn't happen...
        endif

      return
      end


      subroutine store_isd(istore, NVARS_MOVE, temp, lmc_lev)
C
C       generating a varying random seed in all iterations
C
        implicit none
        integer NVARS_MOVE, lmc_lev, n, isd_aux, isd_aux1, isd_cnt
        integer istore(NVARS_MOVE)
        real r_out
        double precision temp, work_t

        call RANDOM_SEED(size = n)
        call RANDOM_NUMBER(r_out)
        work_t = dble(r_out)

        isd_aux = idint(1.d4*work_t + temp*1.d3 + dble(lmc_lev)*1.d3)
        do isd_cnt = 1,NVARS_MOVE
          isd_aux1 = idint(dble(isd_cnt)*1.d5*work_t)
          istore(isd_cnt) = isd_aux + isd_aux1
        enddo

      return
      end


      double precision function RANDOM_GEN(isd)
C
C     This  is a special function  for random  number generation.
C     The applied technique involves doing  multiplication and addition
C     in parts, by splitting all integers in a 'high' & a 'low' part.
C     The algorithm implemented is (following D E Knuth):
C     isd = isd*1592653589 + 453816691
C     if (isd.lt.0) isd = isd + 1 + 2147483647
C                            note that 1592653589 = 48603*2**15 + 30485
C
        implicit none
        integer isd, ia, ib, i1, i2, i3, isd1
        real rnd

        ia = isd/32768
        ib = mod(isd,32768)
        i1 = ia*30485
        i2 = ib*30485
        i3 = ib*48603
        i1 = mod(i1,65536)
        i3 = mod(i3,65536)
        i1 = i1+i3+13849+i2/32768+mod(ia,2)*32768
        i2 = mod(i2,32768)+12659
        i1 = i1+i2/32768
        i2 = mod(i2,32768)
        i1 = mod(i1,65536)
        isd1 = i1*32768+i2
        rnd = real(isd1)*4.65661287308e-10
        RANDOM_GEN = dble(rnd)

      return
      end
