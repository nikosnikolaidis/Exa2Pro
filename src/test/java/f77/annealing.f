      subroutine annealing(object_lmc_current, object_lmc_previous,
     &                     var_mov, isuc, amed, accepted_obj, irej,
     &                     ilev, Temp, itmp, iglob, iun, Nprc, var_sim,
     &                     alast_suc_move, wtime_master, change)
        implicit none
        include 'SA_params.inc'

        integer isuc, irej, ilev, itmp, iglob, Nprc
        integer ivmoves, ibest_prcID, j
        integer iun(IFILES_SIZE),
     &          istore(NVARS_MOVE)
        double precision object_lmc_current, object_lmc_previous, amed
        double precision wtime_master, DE, best_obj, change
        double precision work2, RANDOM_GEN, probability
        double precision var_mov(NVARS_MOVE,LMC),
     &                   accepted_obj(LMC),
     &                   Temp(IT_MAX),
     &                   var_sim(NVARS_MOVE, 0:Nprc-1),
     &                   alast_suc_move(NVARS_MOVE)

!       iun(1) = out_stat.txt
!       iun(2) = out_all.txt

        call object_calc(best_obj, ibest_prcID)
        object_lmc_previous = object_lmc_current
        object_lmc_current = best_obj

        do ivmoves = 1, NVARS_MOVE
          var_mov(ivmoves, ilev) = var_sim(ivmoves, ibest_prcID)
        enddo

        write(*,100) 'ilev = ', ilev,
     &               'obj_current = ', object_lmc_current,
     &               'obj_previous = ', object_lmc_previous,
     &               'best #ID = ', ibest_prcID
100     format(1X, A, I4, 4X, A, F12.6, 4X, A, F12.6, 4X, A, I4)
101     format(1X, A, F6.2, F6.2, F6.2, F6.2, F6.2, F6.2) 
        write(*,101) 'var_mov', (var_mov(j,ilev),j=1,NVARS_MOVE)

        DE = object_lmc_current - object_lmc_previous
        if (DE .lt. 0.d0) then
          write(iun(2),*) 'ACCEPT1'
          write(*,*) 'ACCEPT1'
          do ivmoves = 1, NVARS_MOVE
            alast_suc_move(ivmoves) = var_mov(ivmoves, ilev)
          enddo
          call median_calc(isuc, amed, accepted_obj, object_lmc_current)
          call printing(iglob, Temp, object_lmc_current, alast_suc_move,
     &                  itmp, iun, Nprc, ibest_prcID, wtime_master,
     &                  change)
          irej = 1
        else
          call store_isd(istore, NVARS_MOVE, Temp(itmp), ilev)
          work2 = RANDOM_GEN(istore(2))
          probability = exp(-DE/Temp(itmp))
C
C          write(iun(2),*)'work_anneal, probability', work2,probability
C          write(*,*)'work2, probability', work2, probability
C
          if (work2 .le. probability) then
!           Accept solutions with probability
            write(iun(2),*) 'ACCEPT2'
            write(*,*) 'ACCEPT2'
            do ivmoves = 1, NVARS_MOVE
              alast_suc_move(ivmoves) = var_mov(ivmoves, ilev)
            enddo
            call median_calc(isuc, amed, accepted_obj,
     &                       object_lmc_current)
            call printing(iglob, Temp, object_lmc_current,
     &                    alast_suc_move, itmp, iun, Nprc,
     &                    ibest_prcID, wtime_master, change)
            irej = 1
          else
            write(iun(2),*) 'REJECT'
            write(*,*) 'REJECT'
            object_lmc_current = object_lmc_previous
            do ivmoves = 1, NVARS_MOVE
              var_mov(ivmoves, ilev) = var_mov(ivmoves, ilev-1)
            enddo
            irej = irej + 1  ! rejected moves counter
          endif
        endif
        iglob = iglob + 1

      return
      end


      subroutine median_calc(isuc, amed, accepted_obj, best_obj)
        implicit none
        include 'SA_params.inc'

        integer isuc
        double precision amed, best_obj
        double precision accepted_obj(LMC)
C
C       Storing accepted objective value used in cool_SA
C
        accepted_obj(isuc) = best_obj
C
C       Counter for median used in cool_SA
C
        amed = amed + best_obj
C
C       Counter of succesful moves
C
        isuc = isuc + 1

      return
      end
