
#include "enthalpy3.h"
#include "utils.h"
#include "x2p_variables.h"
#include <cassert>
#include <numeric>

static constexpr double CPIGCOEF[4][5] = {
    {3.3738112e4, -7.0175634, 2.72961e-2, -1.665e-5, 4.2976e-9},
    {1.9795190e4, 7.3436472e1, -5.60193e-3, 1.7153e-5, 0.0},
    {1.3207400e4, 2.8157700e2, -1.513066e-1, 3.1287e-5, 0.0},
    {2.9801820e4, -7.0190710, 1.744400e-2, -8.4803e-6, 0.0}};

static constexpr double HLCOEF[7] = {-1.351e-1, 1.58e-1, 2.64e-2, 8.6714e1,
                                     -9.1314e1, 2.861e1, -5.362e4};

static constexpr double TREF = 298.15;
static constexpr double SCALER = 1.0e6;

void cpp_enthalpy3_(const int &gradient, const char *phase, const double &T,
                    const double *comp, double *const ent, double *const dentdt,
                    double *dentdc)
{
    // Call from Fortran for testing
    if (cpp_lsame(phase, 'L')) {
        if (gradient)
            enthalpy_grad_liq(T, comp, ent, dentdt, dentdc);
        else
            *ent = enthalpy_liq(T, comp);
    } else {
        if (gradient)
            enthalpy_grad_vap(T, comp, ent, dentdt, dentdc);
        else
            *ent = enthalpy_vap(T, comp);
    }
}

double enthalpy_liq(const double T, const double *comp)
{
    const auto diff_T = T - TREF;
    const auto diff_T2 = diff_T * diff_T;
    const auto diff_T3 = diff_T2 * diff_T;
    const auto diff_T4 = diff_T3 * diff_T;
    const auto diff_T5 = diff_T4 * diff_T;

    const double sum_comp = std::accumulate(comp, comp + LIQUID_NCOMP, 0.0);
    assert(sum_comp == (comp[0] + comp[1] + comp[2]));

    const auto zload = comp[1] / comp[2];
    const auto zload_2 = zload * zload;

    double ent = 0.0;
    for (auto ia = 0; ia < LIQUID_NCOMP; ++ia) {
        const double intcpig =
            (CPIGCOEF[ia][0] * diff_T + CPIGCOEF[ia][1] * diff_T2 / 2.0 +
             CPIGCOEF[ia][2] * diff_T3 / 3.0 + CPIGCOEF[ia][3] * diff_T4 / 4.0 +
             CPIGCOEF[ia][4] * diff_T5 / 5.0) *
            1e-3;

        const double x = comp[ia] / sum_comp;
        ent += x * intcpig;
    }
    const auto entdep =
        (HLCOEF[0] * zload_2 + HLCOEF[1] * zload + HLCOEF[2]) * T * T +
        (HLCOEF[3] * zload_2 + HLCOEF[4] * zload + HLCOEF[5]) * T + HLCOEF[6];
    ent += entdep;
    return ent / SCALER;
}

void enthalpy_grad_liq(const double T, const double *comp, double *const _ent,
                       double *const _dentdt, double *dentdc)
{
    const auto diff_T = T - TREF;
    const auto diff_T2 = diff_T * diff_T;
    const auto diff_T3 = diff_T2 * diff_T;
    const auto diff_T4 = diff_T3 * diff_T;
    const auto diff_T5 = diff_T4 * diff_T;

    const double sum_comp = std::accumulate(comp, comp + LIQUID_NCOMP, 0.0);
    assert(sum_comp == comp[0] + comp[1] + comp[2]);

    const auto zload = comp[1] / comp[2];

    double ent = 0.0;
    double dentdt = 0.0;
    dentdc[0] = dentdc[1] = dentdc[2] = 0.0;

    const double dzloaddc[] = {0.0, 1.0 / comp[2],
                               -comp[1] / (comp[2] * comp[2])};
    for (auto ia = 0; ia < LIQUID_NCOMP; ++ia) {
        const double intcpig =
            (CPIGCOEF[ia][0] * diff_T + CPIGCOEF[ia][1] * diff_T2 / 2.0 +
             CPIGCOEF[ia][2] * diff_T3 / 3.0 + CPIGCOEF[ia][3] * diff_T4 / 4.0 +
             CPIGCOEF[ia][4] * diff_T5 / 5.0) *
            1e-3;

        const double x = comp[ia] / sum_comp;
        ent += x * intcpig;

        const double dintcpigdt =
            (CPIGCOEF[ia][0] + CPIGCOEF[ia][1] * diff_T +
             CPIGCOEF[ia][2] * diff_T2 + CPIGCOEF[ia][3] * diff_T3 +
             CPIGCOEF[ia][4] * diff_T4) *
            1e-3;
        dentdt += x * dintcpigdt;
        for (auto ib = 0; ib < LIQUID_NCOMP; ++ib) {
            const auto dxdc = (ia == ib) ? (1.0 - x) / sum_comp : -x / sum_comp;
            dentdc[ib] += dxdc * intcpig / SCALER;
        }
        const double dentdepdc =
            2.0 * HLCOEF[0] * zload * dzloaddc[ia] * T * T +
            HLCOEF[1] * dzloaddc[ia] * T * T +
            2.0 * HLCOEF[3] * zload * dzloaddc[ia] * T +
            HLCOEF[4] * dzloaddc[ia] * T;
        dentdc[ia] += dentdepdc / SCALER;
    }
    const auto zload_2 = zload * zload;
    const auto entdep =
        (HLCOEF[0] * zload_2 + HLCOEF[1] * zload + HLCOEF[2]) * T * T +
        (HLCOEF[3] * zload_2 + HLCOEF[4] * zload + HLCOEF[5]) * T + HLCOEF[6];
    ent += entdep;
    *_ent = ent / SCALER;

    const auto dentdepdt =
        2.0 * (HLCOEF[0] * zload_2 + HLCOEF[1] * zload + HLCOEF[2]) * T +
        HLCOEF[3] * zload_2 + HLCOEF[4] * zload + HLCOEF[5];
    dentdt += dentdepdt;
    *_dentdt = dentdt / SCALER;
}

double enthalpy_vap(const double T, const double *comp)
{
    const auto diff_T = T - TREF;
    const auto diff_T2 = diff_T * diff_T;
    const auto diff_T3 = diff_T2 * diff_T;
    const auto diff_T4 = diff_T3 * diff_T;
    const auto diff_T5 = diff_T4 * diff_T;

    const double sum_comp = std::accumulate(comp, comp + VAPOR_NCOMP, 0.0);
    assert(sum_comp == comp[0] + comp[1] + comp[2] + comp[3]);

    double ent = 0.0;
    for (auto ia = 0; ia < VAPOR_NCOMP; ++ia) {
        const double intcpig =
            (CPIGCOEF[ia][0] * diff_T + CPIGCOEF[ia][1] * diff_T2 / 2.0 +
             CPIGCOEF[ia][2] * diff_T3 / 3.0 + CPIGCOEF[ia][3] * diff_T4 / 4.0 +
             CPIGCOEF[ia][4] * diff_T5 / 5.0) *
            1e-3;
        const double y = comp[ia] / sum_comp;
        ent += y * intcpig;
    }
    return ent / SCALER;
}

void enthalpy_grad_vap(const double T, const double *comp, double *const _ent,
                       double *const _dentdt, double *dentdc)
{
    const auto diff_T = T - TREF;
    const auto diff_T2 = diff_T * diff_T;
    const auto diff_T3 = diff_T2 * diff_T;
    const auto diff_T4 = diff_T3 * diff_T;
    const auto diff_T5 = diff_T4 * diff_T;

    const double sum_comp = std::accumulate(comp, comp + VAPOR_NCOMP, 0.0);
    assert(sum_comp == comp[0] + comp[1] + comp[2] + comp[3]);

    double ent = 0.0;
    double dentdt = 0.0;
    dentdc[0] = dentdc[1] = dentdc[2] = dentdc[3] = 0.0;

    for (auto ia = 0; ia < VAPOR_NCOMP; ++ia) {
        const double intcpig =
            (CPIGCOEF[ia][0] * diff_T + CPIGCOEF[ia][1] * diff_T2 / 2.0 +
             CPIGCOEF[ia][2] * diff_T3 / 3.0 + CPIGCOEF[ia][3] * diff_T4 / 4.0 +
             CPIGCOEF[ia][4] * diff_T5 / 5.0) *
            1e-3;

        const double y = comp[ia] / sum_comp;
        ent += y * intcpig;
        const double dintcpigdt =
            (CPIGCOEF[ia][0] + CPIGCOEF[ia][1] * diff_T +
             CPIGCOEF[ia][2] * diff_T2 + CPIGCOEF[ia][3] * diff_T3 +
             CPIGCOEF[ia][4] * diff_T4) *
            1e-3;
        dentdt += y * dintcpigdt;
        for (auto ib = 0; ib < VAPOR_NCOMP; ++ib) {
            const auto dydc = (ia == ib) ? (1.0 - y) / sum_comp : -y / sum_comp;
            dentdc[ib] += dydc * intcpig / SCALER;
        }
    }
    *_ent = ent / SCALER;
    *_dentdt = dentdt / SCALER;
}
