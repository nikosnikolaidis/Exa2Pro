
      SUBROUTINE FUNCON(GRADIENT, M, N, NJAC, X, F, G)
      IMPLICIT NONE
      INCLUDE 'opt_params.inc'
      INCLUDE 'frp_params.inc'

      DOUBLE PRECISION PRESCP(NINCO),dPRESCPdP(NINCO),dPRESCPdL(NINCO)
      COMMON /PR/ PRESCP,DPRESCPDP,DPRESCPDL

      INTEGER JACEL_IDX, JACEL_PATTERN(NONZERO_JAC)
      COMMON /JACEL_ENTRIES/ JACEL_IDX, JACEL_PATTERN

      INTEGER JFEED_ABS_TOP(22),JFEED_ABS_MID(22),JFEED_ABS_BOT(22),
     &  JFEED_STRP_TOP(22),JFEED_STRP_MID(22),JFEED_STRP_BOT(22),
     &  JFEED_HEATER(5),JFEED_HEXCH(10),JFEED_SPLITM(5),
     &  JFEED_STR_MIXER(10),JFEED_MAKEUP(5),JFEED_DUMMY(6),
     &  JFEED_HEATER2(6),JFEED_MK_MIXER(10),JFEED_COOLER(5)
      COMMON /JFEED_GLOBAL/ JFEED_ABS_TOP,JFEED_ABS_MID,JFEED_ABS_BOT,
     &  JFEED_STRP_TOP,JFEED_STRP_MID,JFEED_STRP_BOT,
     &  JFEED_HEATER,JFEED_HEXCH,JFEED_SPLITM,
     &  JFEED_STR_MIXER,JFEED_MAKEUP,JFEED_DUMMY,
     &  JFEED_HEATER2,JFEED_MK_MIXER,JFEED_COOLER

      INTEGER JVAR_ARRAY(MODULES)
      COMMON /JVAR_GLOBAL/ JVAR_ARRAY

      INTEGER ID, JCON, JVAR, M, N, NJAC
      DOUBLE PRECISION X(N), F(M), G(NJAC)
      DOUBLE PRECISION PRESAB(NINCO,IELABS),PRESST(NINCO,IELSTRP),
     &  dPRESABdP(NINCO,IELABS),dPRESSTdP(NINCO,IELSTRP),
     &  dPRESABdL(NINCO,IELABS),dPRESSTdL(NINCO,IELSTRP)
      LOGICAL GRADIENT

      INTEGER MODULE_NUMBER

      jacel_idx = 1
      JCON = 0
C
C Absorber Column
C Absorber Top Element
C
      MODULE_NUMBER = 1
      JVAR = JVAR_ARRAY(MODULE_NUMBER)
      CALL ELEMENT(GRADIENT,JCON,JVAR,JFEED_ABS_TOP,
     &             M,N,NJAC,X,F,G,JACEL_PATTERN,JACEL_IDX)
      DO ID = 1, NINCO
        PRESAB(ID,1) = PRESCP(ID)
        dPRESABdP(ID,1) = dPRESCPdP(ID)
        dPRESABdL(ID,1) = dPRESCPdL(ID)
      ENDDO
C
C Absorber Middle Element
C
      MODULE_NUMBER = MODULE_NUMBER + 1
      JVAR = JVAR_ARRAY(MODULE_NUMBER)
      CALL ELEMENT(GRADIENT,JCON,JVAR,JFEED_ABS_MID,
     &             M,N,NJAC,X,F,G,JACEL_PATTERN,JACEL_IDX)
      DO ID = 1, NINCO
        PRESAB(ID,2) = PRESCP(ID)
        dPRESABdP(ID,2) = dPRESCPdP(ID)
        dPRESABdL(ID,2) = dPRESCPdL(ID)
      ENDDO
C
C Absorber Bottom Element
C
      MODULE_NUMBER = MODULE_NUMBER + 1
      JVAR = JVAR_ARRAY(MODULE_NUMBER)
      CALL ELEMENT(GRADIENT,JCON,JVAR,JFEED_ABS_BOT,
     &             M,N,NJAC,X,F,G,JACEL_PATTERN,JACEL_IDX)
      DO ID = 1, NINCO
        PRESAB(ID,3) = PRESCP(ID)
        dPRESABdP(ID,3) = dPRESCPdP(ID)
        dPRESABdL(ID,3) = dPRESCPdL(ID)
      ENDDO
C
C Stripper Column
C Stripper Top Section
C
      MODULE_NUMBER = MODULE_NUMBER + 1
      JVAR = JVAR_ARRAY(MODULE_NUMBER)
      CALL ELEMENT(GRADIENT,JCON,JVAR,JFEED_STRP_TOP,
     &             M,N,NJAC,X,F,G,JACEL_PATTERN,JACEL_IDX)
      DO ID = 1, NINCO
        PRESST(ID,1) = PRESCP(ID)
        dPRESSTdP(ID,1) = dPRESCPdP(ID)
        dPRESSTdL(ID,1) = dPRESCPdL(ID)
      ENDDO
C
C Stripper Middle Section
C
      MODULE_NUMBER = MODULE_NUMBER + 1
      JVAR = JVAR_ARRAY(MODULE_NUMBER)
      CALL ELEMENT(GRADIENT,JCON,JVAR,JFEED_STRP_MID,
     &             M,N,NJAC,X,F,G,JACEL_PATTERN,JACEL_IDX)
      DO ID = 1, NINCO
        PRESST(ID,2) = PRESCP(ID)
        dPRESSTdP(ID,2) = dPRESCPdP(ID)
        dPRESSTdL(ID,2) = dPRESCPdL(ID)
      ENDDO
C
C Stripper Bottom Section
C
      MODULE_NUMBER = MODULE_NUMBER + 1
      JVAR = JVAR_ARRAY(MODULE_NUMBER)
      CALL ELEMENT(GRADIENT,JCON,JVAR,JFEED_STRP_BOT,
     &             M,N,NJAC,X,F,G,JACEL_PATTERN,JACEL_IDX)
      DO ID = 1, NINCO
        PRESST(ID,3) = PRESCP(ID)
        dPRESSTdP(ID,3) = dPRESCPdP(ID)
        dPRESSTdL(ID,3) = dPRESCPdL(ID)
      ENDDO
C
C Reboiler
C
      MODULE_NUMBER = MODULE_NUMBER + 1
      JVAR = JVAR_ARRAY(MODULE_NUMBER)
      CALL HEATER(GRADIENT,JCON,JVAR,JFEED_HEATER,
     &            M,N,NJAC,X,F,G,JACEL_PATTERN,JACEL_IDX)
C
C Cross Flow Heat Exchanger
C Rich Cold Stream - Lean Hot Stream
C
      MODULE_NUMBER = MODULE_NUMBER + 1
      JVAR = JVAR_ARRAY(MODULE_NUMBER)
      CALL HEXCH(GRADIENT,JCON,JVAR,JFEED_HEXCH,M,N,NJAC,X,F,G,
     &           JACEL_PATTERN,JACEL_IDX)
C
C Stream Splitter
C
      MODULE_NUMBER = MODULE_NUMBER + 1
      JVAR = JVAR_ARRAY(MODULE_NUMBER)
      CALL SPLITM(GRADIENT,JCON,JVAR,JFEED_SPLITM,M,N,NJAC,X,F,G,
     &            JACEL_PATTERN,JACEL_IDX)
C
C Stream Mixer
C
      MODULE_NUMBER = MODULE_NUMBER + 1
      JVAR = JVAR_ARRAY(MODULE_NUMBER)
      CALL MIXER(GRADIENT,JCON,JVAR,JFEED_STR_MIXER,
     &           M,N,NJAC,X,F,G,JACEL_PATTERN,JACEL_IDX)
C
C Lean Stream Make-up
C
      MODULE_NUMBER = MODULE_NUMBER + 1
      JVAR = JVAR_ARRAY(MODULE_NUMBER)
      CALL MAKEUP(GRADIENT,JCON,JVAR,JFEED_MAKEUP,M,N,NJAC,X,F,G,
     &            JACEL_PATTERN,JACEL_IDX)
C
C Auxilliary Feed Blocks
C
      MODULE_NUMBER = MODULE_NUMBER + 1
      JVAR = JVAR_ARRAY(MODULE_NUMBER)
      CALL DUMMY(GRADIENT,JCON,JVAR,JFEED_DUMMY + JVAR,
     &           M,N,NJAC,X,F,G,JACEL_PATTERN,JACEL_IDX)

      MODULE_NUMBER = MODULE_NUMBER + 1
      JVAR = JVAR_ARRAY(MODULE_NUMBER)
      CALL DUMMY(GRADIENT,JCON,JVAR,JFEED_DUMMY + JVAR,
     &           M,N,NJAC,X,F,G,JACEL_PATTERN,JACEL_IDX)
C
C Condenser
C
      MODULE_NUMBER = MODULE_NUMBER + 1
      JVAR = JVAR_ARRAY(MODULE_NUMBER)
      CALL HEATER2(GRADIENT,JCON,JVAR,JFEED_HEATER2,
     &             M,N,NJAC,X,F,G,JACEL_PATTERN,JACEL_IDX)
C
C Make-up Mixer
C
      MODULE_NUMBER = MODULE_NUMBER + 1
      JVAR = JVAR_ARRAY(MODULE_NUMBER)
      CALL MIXER(GRADIENT,JCON,JVAR,JFEED_MK_MIXER,
     &           M,N,NJAC,X,F,G,JACEL_PATTERN,JACEL_IDX)
C
C Stream Cooler
C
      MODULE_NUMBER = MODULE_NUMBER + 1
      JVAR = JVAR_ARRAY(MODULE_NUMBER)
      CALL COOLER(GRADIENT,JCON,JVAR,JFEED_COOLER,
     &            M,N,NJAC,X,F,G,JACEL_PATTERN,JACEL_IDX)
C
C Funcon Rest
C
      MODULE_NUMBER = MODULE_NUMBER + 1
      JVAR = JVAR_ARRAY(MODULE_NUMBER)
      CALL REST(GRADIENT,JCON,JVAR,M,N,NJAC,X,F,G,
     &          JACEL_PATTERN,JACEL_IDX,
     &          PRESAB,PRESST,DPRESABDP,DPRESSTDP)

      RETURN
      END SUBROUTINE


      SUBROUTINE REST(GRADIENT,JCON,JVAR,M,N,NJAC,X,F,G,
     &                JACEL_PATTERN,JACEL_IDX,
     &                PRESAB,PRESST,DPRESABDP,DPRESSTDP)
      IMPLICIT NONE
      INCLUDE 'frp_params.inc'

      INTEGER IELEM
      PARAMETER (IELEM=IELABS + IELSTRP)

      INTEGER JCON, JVAR, M, N, NJAC, JACEL_IDX
      INTEGER JACEL_PATTERN(NJAC)
      DOUBLE PRECISION X(N), F(M), G(NJAC)
      DOUBLE PRECISION PRESAB(NINCO,IELABS),PRESST(NINCO,IELSTRP),
     &  dPRESABdP(NINCO,IELABS),dPRESSTdP(NINCO,IELSTRP)

      INTEGER IA, JC, JF
      INTEGER JHGTABS, JHGTSTRP, JDIAMABS, JDIAMSTRP
      DOUBLE PRECISION HGTABS,HGTSTRP,DIAMABS,DIAMSTRP
      DOUBLE PRECISION LENG(IELEM),LENG_ABS,LENG_STR,
     &  PRESR,PRBABS(IELABS),PRBSTR(IELSTRP)
      LOGICAL GRADIENT

      JF = JVAR

      JF = JF + 1
      HGTABS = X(JF)
      JHGTABS = JF

      JF = JF + 1
      HGTSTRP = X(JF)
      JHGTSTRP = JF

      JF = JF + 1
      DIAMABS = X(JF)
      JDIAMABS = JF

      JF = JF + 1
      DIAMSTRP = X(JF)
      JDIAMSTRP = JF

      DO IA = 1, IELEM
        LENG(IA) = X(77*(IA-1)+ 56)
      ENDDO

      LENG_ABS = 0.0
      DO IA = 1, IELABS
        LENG_ABS = LENG_ABS + LENG(IA)
      ENDDO
      LENG_STR = 0.0
      DO IA = IELSTRP+1, IELEM
        LENG_STR = LENG_STR + LENG(IA)
      ENDDO

      PRESR = X(474)
      DO IA = 1, IELABS
        PRBABS(IA) = X(55+(IA-1)*77)
      ENDDO
      DO IA = 1, IELSTRP
        PRBSTR(IA) = X(55+(IELABS+IA-1) * 77)
      ENDDO

      JC = JCON

      JC = JC + 1
      F(JC) = HGTABS - LENG_ABS * 4D-01
      IF (GRADIENT) THEN
        G(jacel_pattern(jacel_idx)) = 1.0D0
#ifdef ITEST
        JF = JHGTABS
        call check_jacel_pattern(jc, jf)
#endif
        jacel_idx = jacel_idx + 1
        DO IA = 1, IELABS
          G(jacel_pattern(jacel_idx)) = - 4D-01
#ifdef ITEST
          JF = 77*(IA-1)+56
          call check_jacel_pattern(jc, jf)
#endif
          jacel_idx = jacel_idx + 1
        ENDDO
      ENDIF  ! GRADIENT

      JC = JC + 1
      F(JC) = HGTSTRP - LENG_STR * 4D-01
      IF (GRADIENT) THEN
        G(jacel_pattern(jacel_idx)) = 1.0D0
#ifdef ITEST
        JF = JHGTSTRP
        call check_jacel_pattern(jc, jf)
#endif
        jacel_idx = jacel_idx + 1
        DO IA = IELSTRP+1, IELEM
          G(jacel_pattern(jacel_idx)) = - 4D-01
#ifdef ITEST
          JF = 77*(IA-1)+56
          call check_jacel_pattern(jc, jf)
#endif
          jacel_idx = jacel_idx + 1
        ENDDO
      ENDIF  ! GRADIENT

      JC = JC + 1
      F(JC) = DIAMABS - HGTABS / 5D+00
      IF (GRADIENT) THEN
        G(jacel_pattern(jacel_idx)) = 1.0D0
#ifdef ITEST
        JF = JDIAMABS
        call check_jacel_pattern(jc, jf)
#endif
        jacel_idx = jacel_idx + 1

        G(jacel_pattern(jacel_idx)) = - 1 / 5d00
#ifdef ITEST
        JF = JHGTABS
        call check_jacel_pattern(jc, jf)
#endif
        jacel_idx = jacel_idx + 1
      ENDIF  ! GRADIENT

      JC = JC + 1
      F(JC) = DIAMSTRP - HGTSTRP / 10D+00
      IF (GRADIENT) THEN
        G(jacel_pattern(jacel_idx)) = 1.0D0
#ifdef ITEST
        JF = JDIAMSTRP
        call check_jacel_pattern(jc, jf)
#endif
        jacel_idx = jacel_idx + 1

        G(jacel_pattern(jacel_idx)) = - 1 / 10d00
#ifdef ITEST
        JF = JHGTSTRP
        call check_jacel_pattern(jc, jf)
#endif
        jacel_idx = jacel_idx + 1
      ENDIF  ! GRADIENT

C--------------------------------------------------------------------
C--------------- Connection of Pressures in the Columns -------------
C---------------------------- and Reboilers -------------------------
C--------------------------------------------------------------------

      DO IA = 1, IELABS - 1
        JC = JC + 1
        F(JC) = PRESAB(NINCO,IA) - PRESAB(1,IA+1)
        IF (GRADIENT) THEN
          G(jacel_pattern(jacel_idx)) = dPRESABdP(NINCO,IA)
#ifdef ITEST
          JF = 55+(IA-1)*77
          call check_jacel_pattern(jc, jf)
#endif
          jacel_idx = jacel_idx + 1

          G(jacel_pattern(jacel_idx)) = - dPRESABdP(1,IA+1)
#ifdef ITEST
          JF = 55+(IA)*77
          call check_jacel_pattern(jc, jf)
#endif
          jacel_idx = jacel_idx + 1
        ENDIF  ! GRADIENT
      ENDDO

      DO IA = 1, IELSTRP - 1
        JC = JC + 1
        F(JC) = PRESST(NINCO,IA) - PRESST(1,IA+1)
        IF (GRADIENT) THEN
          G(jacel_pattern(jacel_idx)) = dPRESSTdP(NINCO,IA)
#ifdef ITEST
          JF = 55 + (IELABS+IA-1)*77
          call check_jacel_pattern(jc, jf)
#endif
          jacel_idx = jacel_idx + 1

          G(jacel_pattern(jacel_idx)) = - dPRESABdP(1,IA+1)
#ifdef ITEST
          JF = 55 + (IELABS+IA)*77
          call check_jacel_pattern(jc, jf)
#endif
          jacel_idx = jacel_idx + 1
        ENDIF  ! GRADIENT
      ENDDO

      JC = JC + 1
      F(JC) = PRBSTR(IELSTRP) - PRESR
      IF (GRADIENT) THEN
        G(jacel_pattern(jacel_idx)) = 1.0D0
#ifdef ITEST
        JF = 55+(IELABS+IELSTRP-1) * 77
        call check_jacel_pattern(jc, jf)
#endif
        jacel_idx = jacel_idx + 1

        G(jacel_pattern(jacel_idx)) = -1.0D0
#ifdef ITEST
        JF = 474
        call check_jacel_pattern(jc, jf)
#endif
      ENDIF  ! GRADIENT

#ifdef GET_SPARSITY
        write(*,*) 'WRITE NEW JACEL_ENTRIES'
        stop
#endif
        JCON = JC

      RETURN
      END SUBROUTINE


      INTEGER FUNCTION JACEL(JC,JF)
C
C  05.03.1998   Initial coding  (PS)
C
C  This subroutine returns the entry number JACEL in the sparce Jacobian
C  vector given the column JF and row JC entries
C
      IMPLICIT NONE
      INTEGER NJAC, JCOLP(800),JROW(50000)
      COMMON /SPARSITY/ NJAC,JCOLP,JROW

      INTEGER JC, JF, K1, K2, K

      JACEL = 0

      K1 = JCOLP(JF)
      K2 = JCOLP(JF+1) - 1

      DO K=K1, K2
        IF (JROW(K).EQ.JC) THEN
          JACEL = K
        ENDIF
      ENDDO

      IF (JACEL.EQ.0) THEN
        WRITE (6,*) 'UNIDENTIFIED JACOBIAN ELEMENT IN ROW',JC,
     &              '  COL',JF
        JACEL = NJAC
      ENDIF

#ifdef GET_SPARSITY
      open(226, file="input/jacel_entries", status="old",
     &     position="append", action="write")
      write(226, *) JACEL
      close(226)
#endif
          
      RETURN
      END FUNCTION


      SUBROUTINE GSPAR(N,NJAC)
C 
C   05.03.1998   Initial coding  (PS)
C 
C   This subroutine reads the sparsity pattern for the current problem
C   It is usually called before the first iteration and stores the
C   sparsity pattern of the Jacobian in the vectors JCOLP(N) and JROW(NJAC)
C   JCOLP(J) contains as entries the location in the JROW vector where the
C   elements that correspond to J-th variable (size N)
C   JROW(J) gives the row index for the J-th Jacobian element (size NJAC)
C 
      IMPLICIT NONE
      COMMON /SPARSITY/ NNJAC,JCOLP,JROW
      INTEGER JCOLP(800),JROW(50000)

      INTEGER N, NJAC, NNJAC, J
 
      OPEN(UNIT=26,FILE='input/mapjac',STATUS='OLD')

      NNJAC = NJAC

      DO J=1, N+1
        READ (26,100) JCOLP(J)
      ENDDO

      DO J=1, NJAC
        READ (26,100) JROW(J)
      ENDDO

100   FORMAT (T25,I8)
      CLOSE(26)

      RETURN
      END SUBROUTINE


      SUBROUTINE FUNOBJ(GRADIENT,N,X,F,G)
      IMPLICIT NONE

      INTEGER N
      DOUBLE PRECISION F, X(N), G(N)

      INTEGER JF, IA, I, J
      INTEGER JQB,JLSTRIPOUT(3),JLABSIN(3),JVIN(3),JVEX(3),JHEIGHTA,
     &  JHEIGHTS,JAREA,JDIAMA,JDIAMS,JAMNMK,JWATMK,JWATER,PR_ID_ABS,
     &  PR_ID_STR,JLABSOUT(3),JAMINE, JTLNH
      DOUBLE PRECISION LSTRIPOUT(3),LABSIN(3),QB,VIN(3),VEX(3),
     &  TLNH,AREA,HEIGHTA,MW(3),LABSOUT(3),HEIGHTS,DIAMA,DIAMS,AMNMK,
     &  WATMK,WATER,C_PRES_ABS(6),CPRESDEP(6,8),C_PRES_STR(6),
     &  W(3),dWdC(3,3),MWMIX,dMWMIXdC(3),dP_pumpdC(3),LTOT,
     &  dC_pumpdC(3),C_pump,P_pump, MW_am, MW_co2, A_reb, AMINE, C_ABS,
     &  C_amine, C_co2, C_HEX, C_reb, C_steam, C_STR, C_water, Steam
      DOUBLE PRECISION dA_rebdQb, dA_rebdTb, dC_ABSdHA, dC_aminedF,
     &  dC_aminedF2, dC_co2dF, dC_HEXdA, dC_rebdQb, dC_rebdTb,
     &  dC_steamdQb, dC_steamdTb, dC_STRdHS, dC_waterdF, dC_waterdF2,
     &  dSteamdQb, dSteamdTb
      DOUBLE PRECISION CUR, C_am, C_st, Cost_co2, p_eff
      PARAMETER (CUR = 7.3126d-1, C_am = 2d0, C_st = 5d-3,
     &  Cost_co2 = 4d0, p_eff = 0.7d0)
      LOGICAL GRADIENT

      DATA ((CPRESDEP(I,J), I=1, 6), J=1, 8)
     &/ 15.803d0, 1392.0d0,   822.1d0,  156.94d0, 0.2400d0,  242.50d0,
     &  15.694d0, 1566.8d0,  1208.8d0,  454.13d0, 0.1515d0,  709.28d0,
     &   8.706d0, 2093.3d0,  2472.2d0, 2686.56d0,-0.3929d0, 2936.73d0,
     &   9.553d0, 2443.7d0,  5234.6d0, 7729.14d0, 0.9700d0, -447.39d0,
     &   8.312d0, 2890.8d0,  8673.0d0, 26148.8d0,-0.9908d0, 11558.7d0,
     &   2.694d0, 3475.2d0, 12169.0d0, 6562.90d0, 0.9929d0, 11699.1d0,
     &  -0.308d0, 3947.3d0, 16552.0d0, 24928.8d0, 1.0080d0, 1004.70d0,
     &  -1.464d0, 4613.7d0, 26674.0d0, 45156.3d0, 1.0122d0, 2330.50d0/

      DATA MW / 18.02d0, 44d0, 61.08d0 /

      JF = 0

      JF = 463
      QB = X(463)
      JQB = JF

      DO IA = 1, 3
        JF = IA+479
        LSTRIPOUT(IA) = X(IA+479)
        JLSTRIPOUT(IA) = JF
      ENDDO

      DO IA = 1, 3
        JF = IA+213
        LABSIN(IA) = X(IA+213)
        JLABSIN(IA) = JF
      ENDDO

      DO IA = 1, 3
        JF = IA + 168
        LABSOUT(IA) = X(IA+168)
        JLABSOUT(IA) = JF
      ENDDO

      DO IA = 1, 3
        JF = IA+65
        VEX(IA) = X(IA+65)
        JVEX(IA) = JF
      ENDDO

      DO IA = 1, 3
        JF = IA+507
        VIN(IA) = X(IA+507)
        JVIN(IA) = JF
      ENDDO

      JF = 473
      TLNH = X(473)
      JTLNH = JF

      JF = 485
      AREA = X(485)
      JAREA = JF

      JF = 554
      HEIGHTA = X(554)
      JHEIGHTA = JF

      JF = 555
      HEIGHTS = X(555)
      JHEIGHTS = JF

      JF = 556
      DIAMA = X(556)
      JDIAMA = JF

      JF = 557
      DIAMS = X(557)
      JDIAMS = JF

      JF = 497
      AMINE = X(497)
      JAMINE = JF

      JF = 495
      WATER = X(495)
      JWATER = JF

      JF = 500
      WATMK = X(500)
      JWATMK = JF

      JF = 502
      AMNMK = X(502)
      JAMNMK = JF


      LTOT = 0.0
      DO I = 1, 3
        LTOT = LTOT + LABSOUT(I)
      ENDDO

      DO I = 1, 3
        W(I) = LABSOUT(I) / LTOT
        DO J = 1, 3
          IF (J.EQ.I) THEN
            DWDC(I,J) = (1.0D0 - W(I)) / LTOT
          ELSE
            DWDC(I,J) = - W(I) / LTOT
          ENDIF
        ENDDO
      ENDDO

      MWMIX = 0.0D0
      DO I=1,3
        DMWMIXDC(I) = 0.0D0
      ENDDO
      DO I = 1,3
        MWMIX = MWMIX + W(I) * MW(I)
        DO J = 1,3
          DMWMIXDC(J) = DMWMIXDC(J) + MW(I) * DWDC(I,J)
        ENDDO
      ENDDO


C----------------------- Cost Estimation of HEX ---------------------
C---------------------------- PDC (2014) ----------------------------

      C_HEX = 3.14d3*AREA**0.62d0+1.4d2*AREA + 2.819d6/(AREA+3.65d2)
      IF (GRADIENT) THEN
        dC_HEXdA = 0.62d0 * 3.14d3 * AREA**(0.62d0-1d0) + 1.4d2 - 
     &              2.819d6/(AREA+3.65d2)**2d0
      ENDIF


C---------------------- Cost Estimation of Absorber -----------------
C----------------------------- PDC (2014) ---------------------------

      PR_ID_ABS = 3
      DO I = 1, 6
        C_PRES_ABS(I) = CPRESDEP(I,PR_ID_ABS)
      ENDDO

      C_ABS = 7.38d0*(C_PRES_ABS(1)*HEIGHTA**2d0+C_PRES_ABS(2)*HEIGHTA+
     &         C_PRES_ABS(3)) + 1.66d0 * (C_PRES_ABS(4) * 
     &         HEIGHTA**C_PRES_ABS(5) + C_PRES_ABS(6) * HEIGHTA)
      IF (GRADIENT) THEN
        dC_ABSdHA = 7.38d0*(2d0*C_PRES_ABS(1)*HEIGHTA + C_PRES_ABS(2)) + 
     &   1.66d0 * (C_PRES_ABS(5) * C_PRES_ABS(4) * 
     &   HEIGHTA**(C_PRES_ABS(5)-1d0) + C_PRES_ABS(6))
      ENDIF


C---------------------- Cost Estimation of Stripper -----------------
C----------------------------- PDC (2014) ---------------------------

      PR_ID_STR = 3
      DO I = 1, 6
        C_PRES_STR(I) = CPRESDEP(I,PR_ID_STR)
      ENDDO

      C_STR = 7.38d0*(C_PRES_STR(1)*HEIGHTS**2d0+C_PRES_STR(2)*HEIGHTS+ 
     &         C_PRES_STR(3)) + 1.66d0 * (C_PRES_STR(4) * 
     &         HEIGHTS**C_PRES_STR(5) + C_PRES_STR(6) * HEIGHTS)
      IF (GRADIENT) THEN
        dC_STRdHS = 7.38d0*(2d0*C_PRES_STR(1)*HEIGHTS + C_PRES_STR(2)) + 
     &   1.66d0 *(C_PRES_STR(5) * C_PRES_STR(4) * 
     &   HEIGHTS**(C_PRES_STR(5)-1d0) + C_PRES_STR(6))
      ENDIF


C---------------------- Cost Estimation of Reboiler -----------------
C----------------------------- PDC (2014) ---------------------------
C--------------------------- A_reb from Kookos ----------------------

      A_reb = Qb * 1d03 / (1.5d0 * (4.43d2 - TLNH))
      C_reb = 7.35d3 * A_reb**0.47d0 + 6.75d2 * A_reb + 4.517d6 / 
     &            (A_reb + 3.07d2)

      Steam = Qb*1d03 / (4.38d0 * (4.43d02-TLNH)+2.049d03)

      IF (GRADIENT) THEN
        dA_rebdQb = 1d03/(1.5d0*(4.43d2-TLNH))
        dA_rebdTb = Qb*1d03/(1.5d0*(4.43d2-TLNH)**2.0d0)

        dSteamdQb = 1d03 / (4.38d0 * (4.43d02-TLNH)+2.049d03)
        dSteamdTb = Qb*1d03*4.38d0/(4.38d0*(4.43d02-TLNH)+2.049d03)**2d0

        dC_rebdQb = 0.47d0 * 7.35d3 * A_reb**(0.47d0-1d0) * dA_rebdQb + 
     &    6.75d2 * dA_rebdQb -  4.517d6/(A_reb+3.07d2)**2d0 * dA_rebdQb
        dC_rebdTb = 0.47d0 * 7.35d3 * A_reb**(0.47d0-1d0) * dA_rebdTb + 
     &    6.75d2 * dA_rebdTb -  4.517d6/(A_reb+3.07d2)**2d0 * dA_rebdTb
      ENDIF

C----------------- Cost Estimation of Centrifugal Pump --------------


      P_pump = (9.81d0 * MWmix * LTOT / p_eff) * 1d-3  ! [=] kW

      IF (GRADIENT) THEN
        DO IA = 1, 3
          dP_pumpdC(IA) = 9.81d0 * 1d-3 / p_eff* (dMWmixdC(IA) * LTOT +
     &                                                            MWmix)
        ENDDO
      ENDIF

      C_pump = 1.3675d4 * P_pump**0.43d0 + 68d0 * P_pump + 
     &          2.35d6 / (P_pump + 6d2)
      IF (GRADIENT) THEN
        DO IA = 1, 3
          dC_pumpdC(IA) = dP_pumpdC(IA) * (1.3675d4 * 0.43d0 * 
     &             P_pump**(0.43d0-1d0) + 68d0 - 
     &             2.35d6 / (P_pump + 6d2)**2d0)
        ENDDO
      ENDIF

C---------------------- Cost of Utilities ---------------------------

      MW_am = MW(3)
      C_steam = (Steam * C_st * 3.65d2 * 2.4d1 * 3.6d3) * 1536.5d0 /
     &                                                            1104d0
      C_water = (WATMK * 1.8d1 * 1d-3 * 1.16d-4 * 3.65d2 * 2.4d1*3.6d3+
     &           WATER * 1.8d1 * 1d-3 * 1.16d-4) * 1536.5d0 / 1104d0
      C_amine = AMNMK * MW_am * 1d-3 * C_am * 3.65d2 * 2.4d1 * 3.6d3 +
     &           AMINE * MW_am * 1d-3 * C_am

      IF (GRADIENT) THEN
        dC_steamdQb = dSteamdQb * C_st * 3.65d2 * 2.4d1 * 3.6d3 *
     &                                                 1536.5d0 / 1104d0
        dC_steamdTb = dSteamdTb * C_st * 3.65d2 * 2.4d1 * 3.6d3 *
     &                                                 1536.5d0 / 1104d0
        dC_waterdF = 1.8d1 * 1d-3 * 1.16d-4 * 3.65d2 * 2.4d1 * 3.6d3 *
     &                                                 1536.5d0 / 1104d0
        dC_waterdF2 = 1.8d1 * 1d-3 * 1.16d-4 * 1536.5d0 / 1104d0
        dC_aminedF = MW_am * 1d-3 * C_am * 3.65d2 * 2.4d1 * 3.6d3
        dC_aminedF2 = MW_am * 1d-3 * C_am
      ENDIF

C---------------------- Cost of CO2 emissions -----------------------

      MW_co2 = MW(2)
      C_co2 = VEX(2) * MW_co2 * 1d-6 * Cost_co2 * 3.65d2 * 2.4d1*3.6d3

      IF (GRADIENT) THEN
        dC_co2dF = MW_co2 * 1d-6 * Cost_co2 * 3.65d2 * 2.4d1 * 3.6d3
      ENDIF

C------------------ Objective Function (TAC) ------------------------

      F = (C_HEX + C_ABS + C_STR + C_reb + C_pump) / 1d1
     &    + (C_water + C_co2 + C_steam + C_amine)*CUR


C  ----------------------- IF for GRAD F------------------------------
      IF(GRADIENT) THEN
        DO I=1, N
          G(I) = 0.0D0
        ENDDO

        JF = JQB
        G(JF) = dC_rebdQb  / 1d1  + dC_steamdQb * CUR
        JF = JTLNH
        G(JF) = dC_rebdTb  / 1d1  + dC_steamdTb * CUR
        JF = JHEIGHTA
        G(JF) = dC_ABSdHA / 1d1
        JF = JHEIGHTS
        G(JF) = dC_STRdHS / 1d1
        JF = JAREA
        G(JF) = dC_HEXdA / 1d1
        JF = JWATMK
        G(JF) = dC_waterdF * CUR
        JF = JWATER
        G(JF) = dC_waterdF2 * CUR
        JF = JAMNMK
        G(JF) = dC_aminedF * CUR
        JF = JAMINE
        G(JF) =dC_aminedF2 * CUR
        JF = JVEX(2)
        G(JF) = dC_co2dF * CUR
        DO IA = 1,3
          JF = JLABSOUT(IA)
          G(JF) = dC_pumpdC(IA) / 1d1
        ENDDO  
      ENDIF


      RETURN
      END SUBROUTINE


      SUBROUTINE LAGRANGE(LENG,PRESBOT)
C
C THIS PROGRAM CALCULATES THE COEFFICIENTS FOR LAGRANGE INTERPOLATION
C
C    06 12 1997   Initial coding (PS)
C    10 02 1998   Modify for multiple sections (PS)
C    16 02 1998   Derivatives of the lagrange polynomial coefficients
C                 w.r.t. element lengths (PS)
C    16.03.1998   Modify for multiple columns
C
      IMPLICIT DOUBLE PRECISION (A-H, O-Z)

      INTEGER NINCO
      PARAMETER (NINCO=5)

      DOUBLE PRECISION ACOEF(NINCO,NINCO),BCOEF(NINCO,NINCO),
     &                 ANCOEF(NINCO),BTCOEF(NINCO)
      COMMON /COEF/ ACOEF, BCOEF, ANCOEF, BTCOEF

      DOUBLE PRECISION dACOEFdL(NINCO,NINCO),dBCOEFdL(NINCO,NINCO),
     &                 dANCOdL(NINCO),dBTCOdL(NINCO)
      COMMON /COEF2/ dACOEFdL, dBCOEFdL, dANCOdL, dBTCOdL

      DOUBLE PRECISION PRESCP(NINCO),dPRESCPdP(NINCO),dPRESCPdL(NINCO)
      COMMON /PR/ PRESCP,DPRESCPDP,DPRESCPDL

      DOUBLE PRECISION LENG,CO(NINCO),PRESBOT
      DOUBLE PRECISION dCOdL(NINCO)

C-------- Calculate roots of Hahn orthogonal polynomials ---------

      TERM = DSQRT((3.0D0*LENG**2.0D0)/20.0D0)
      CO(1) = 0.0D0
      CO(2) = (LENG+1.0D0)/2.0D0 - TERM
      CO(3) = (LENG+1.0D0)/2.0D0
      CO(4) = (LENG+1.0D0)/2.0D0 + TERM
      CO(5) = LENG+1.0D0
      dCOdL(1) = 0.0D0
      dCOdL(2) = 0.5D0 - 6 * LENG / 4.0D+1 / TERM
      dCOdL(3) = 0.5D0
      dCOdL(4) = 0.5D0 + 6 * LENG / 4.0D+1 / TERM
      dCOdL(5) = 1.0D0

      DO K=1, NINCO
        PRESCP(K) = PRESBOT - 0.5D0 * (1.0D0 - CO(K) / CO(NINCO))
        dPRESCPdP(K) = 1.0D0
        IF (K.NE.3) THEN
          dPRESCPdL(K) = 0.5D0 * (dCOdL(K) * 
     &        CO(NINCO)-CO(K)* dCOdL(NINCO)) / CO(NINCO)**2.0D0
        ELSE
          dPRESCPdL(3) = 0.0D0
        ENDIF
      ENDDO

C Calculation of the polynomial cofficients at the collocation points
C for the liquid phase, acoef, and for the vapour phase, bcoef.
      DO IC=1, NINCO-2
        DO ID=1, NINCO-1
          ACOEF(ID,IC) = 1.0D0
          BCOEF(ID+1,IC) = 1.0D0
          SUMAC = 0.0D0
          SUMBC = 0.0D0
          DO IK=1, NINCO-1
            IF (ID.NE.IK) THEN 
              ACOEF(ID,IC) = ACOEF(ID,IC)*
     &                  (CO(IC+1)-1.0D0-CO(IK))/
     &                  (CO(ID)-CO(IK))
              SUMAC = SUMAC +
     &                  (dCOdL(IC+1)-dCOdL(IK))/
     &                  (CO(IC+1)-1.0D0-CO(IK)) -
     &                  (dCOdL(ID)-dCOdL(IK)) /
     &                  (CO(ID)-CO(IK))

              BCOEF(ID+1,IC) = BCOEF(ID+1,IC)*
     &                  (CO(IC+1)+1.0D0-CO(IK+1))/
     &                  (CO(ID+1)-CO(IK+1))
              SUMBC = SUMBC +
     &                  (dCOdL(IC+1)-dCOdL(IK+1)) /
     &                  (CO(IC+1)+1.0D0-CO(IK+1)) -
     &                  (dCOdL(ID+1)-dCOdL(IK+1)) /
     &                  (CO(ID+1)-CO(IK+1))
            ENDIF
          ENDDO
          dACOEFdL(ID,IC) = ACOEF(ID,IC) * SUMAC
          dBCOEFdL(ID+1,IC) = BCOEF(ID+1,IC) * SUMBC
        ENDDO
      ENDDO

C Calculation of the polynomial coefficients for the points
C at the end of the element for the liquid phase, ancoef, and
C at the beginning of the element for the vapour phase, btcoef
      DO IC=1, NINCO-1
        ANCOEF(IC) = 1.0D0
        BTCOEF(IC+1) = 1.0D0
        SUMAN = 0.0D0
        SUMBT = 0.0D0
        DO IK=1, NINCO-1
          IF (IC.NE.IK) THEN
            ANCOEF(IC) = ANCOEF(IC)*
     &                          (LENG-CO(IK))/
     &                          (CO(IC)-CO(IK))
            SUMAN = SUMAN + (1.0-dCOdL(IK)) /
     &                          (LENG-CO(IK)) -
     &                          (dCOdL(IC)-dCOdL(IK)) /
     &                          (CO(IC)-CO(IK))

            BTCOEF(IC+1) = BTCOEF(IC+1)*
     &                          (1.0D0-CO(IK+1))/
     &                          (CO(IC+1)-CO(IK+1))
            SUMBT = SUMBT + (-dCOdL(IK+1)) /
     &                          (1.0D0-CO(IK+1)) -
     &                          (dCOdL(IC+1)-dCOdL(IK+1)) /
     &                          (CO(IC+1)-CO(IK+1))
          ENDIF
        ENDDO
        dANCOdL(IC) = ANCOEF(IC) * SUMAN
        dBTCOdL(IC+1) = BTCOEF(IC+1) * SUMBT
      ENDDO

C Calculation of the Lagrange polynomial coefficients
C for the determination of the liquid and vapour component molar
C flows at the non-collocation points which coincide with the 
C location of the real trays in the column.
C
C Check the file LAGRATE for the DIB for details 10.02.1998

      RETURN
      END SUBROUTINE
