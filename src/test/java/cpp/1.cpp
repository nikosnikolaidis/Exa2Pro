
#include "enthalpy3.hpp"
#include "utils.hpp"
#include <cassert>
#include <cmath>
#include <iostream>
#include <numeric>

static double cpp_enthalpy3_liquid(const int ncomp, const double T,
                                   const double *comp, const double scale);

static void cpp_enthalpy3_liquid_grad(const int ncomp, const double T,
                                      const double *comp, const double scale,
                                      double *const ent, double *const dentdt,
                                      double *dentdc);

static double cpp_enthalpy3_vapor(const int ncomp, const double T,
                                  const double *comp, const double scale);

static void cpp_enthalpy3_vapor_grad(const int ncomp, const double T,
                                     const double *comp, const double scale,
                                     double *const ent, double *const dentdt,
                                     double *dentdc);

static constexpr double CPIGCOEF[4][5] = {
    {3.3738112e4, -7.0175634, 2.72961e-2, -1.665e-5, 4.2976e-9},
    {1.9795190e4, 7.3436472e1, -5.60193e-3, 1.7153e-5, 0.0},
    {1.3207400e4, 2.8157700e2, -1.513066e-1, 3.1287e-5, 0.0},
    {2.9801820e4, -7.0190710, 1.744400e-2, -8.4803e-6, 0.0}};

static constexpr double HLCOEF[7] = {-1.351e-1, 1.58e-1, 2.64e-2, 8.6714e1,
                                     -9.1314e1, 2.861e1, -5.362e4};

// static constexpr double HVCOEF[11] = {
//     3.551e-1, -3.38e-1, 2.28e-2, 1.6467e3,
//     -2.0674e3, 8.6122e2, -8.0974e1, 4.71524e5,
//     5.45561e5, -2.08378e5, 2.1932e4
// };

static constexpr int LIQUID_NCOMP = 3;
static constexpr int VAPOR_NCOMP = LIQUID_NCOMP + 1;
static constexpr double TREF = 298.15;

void cpp_enthalpy3_(const int &gradient, const char *phase, const double &T,
                    const double *comp, const double &scale, double *const ent,
                    double *const dentdt, double *dentdc)
{
    if (cpp_lsame(phase, 'L')) {
        if (gradient)
            cpp_enthalpy3_liquid_grad(LIQUID_NCOMP, T, comp, scale, ent, dentdt,
                                      dentdc);
        else
            *ent = cpp_enthalpy3_liquid(LIQUID_NCOMP, T, comp, scale);
    } else {
        if (gradient)
            cpp_enthalpy3_vapor_grad(VAPOR_NCOMP, T, comp, scale, ent, dentdt,
                                     dentdc);
        else
            *ent = cpp_enthalpy3_vapor(VAPOR_NCOMP, T, comp, scale);
    }
}

double cpp_enthalpy3(const char *phase, const double T, const double *comp,
                     const double scale)
{
    return (cpp_lsame(phase, 'L'))
               ? cpp_enthalpy3_liquid(LIQUID_NCOMP, T, comp, scale)
               : cpp_enthalpy3_vapor(VAPOR_NCOMP, T, comp, scale);
}

void cpp_enthalpy3(const char *phase, const double T, const double *comp,
                   const double scale, double *const ent, double *const dentdt,
                   double *dentdc)
{
    if (cpp_lsame(phase, 'L'))
        cpp_enthalpy3_liquid_grad(LIQUID_NCOMP, T, comp, scale, ent, dentdt,
                                  dentdc);
    else
        cpp_enthalpy3_vapor_grad(VAPOR_NCOMP, T, comp, scale, ent, dentdt,
                                 dentdc);
}

static double cpp_enthalpy3_liquid(const int ncomp, const double T,
                                   const double *comp, const double scale)
{
    assert(ncomp == LIQUID_NCOMP);

    const auto diff_T = T - TREF;
    const auto diff_T2 = diff_T * diff_T;
    const auto diff_T3 = diff_T2 * diff_T;
    const auto diff_T4 = diff_T3 * diff_T;
    const auto diff_T5 = diff_T4 * diff_T;

    const double sum_comp = std::accumulate(comp, comp + ncomp, 0.0);
    assert(sum_comp == (comp[0] + comp[1] + comp[2]));

    const auto zload = comp[1] / comp[2];
    const auto zload_2 = zload * zload;

    double ent = 0.0;
    for (auto ia = 0; ia < ncomp; ++ia) {
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
    return ent / scale;
}

static void cpp_enthalpy3_liquid_grad(const int ncomp, const double T,
                                      const double *comp, const double scale,
                                      double *const _ent, double *const _dentdt,
                                      double *dentdc)
{
    assert(ncomp == LIQUID_NCOMP);

    const auto diff_T = T - TREF;
    const auto diff_T2 = diff_T * diff_T;
    const auto diff_T3 = diff_T2 * diff_T;
    const auto diff_T4 = diff_T3 * diff_T;
    const auto diff_T5 = diff_T4 * diff_T;

    const double sum_comp = std::accumulate(comp, comp + ncomp, 0.0);
    assert(sum_comp == comp[0] + comp[1] + comp[2]);

    const auto zload = comp[1] / comp[2];

    double ent, dentdt;
    ent = dentdt = 0.0;

    const double dzloaddc[] = {0.0, 1.0 / comp[2],
                               -comp[1] / (comp[2] * comp[2])};
    dentdc[0] = dentdc[1] = dentdc[2] = 0.0;
    for (auto ia = 0; ia < ncomp; ++ia) {
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
        for (auto ib = 0; ib < ncomp; ++ib) {
            const auto dxdc = (ia == ib) ? (1.0 - x) / sum_comp : -x / sum_comp;
            dentdc[ib] += dxdc * intcpig / scale;
        }
        const double dentdepdc =
            2.0 * HLCOEF[0] * zload * dzloaddc[ia] * T * T +
            HLCOEF[1] * dzloaddc[ia] * T * T +
            2.0 * HLCOEF[3] * zload * dzloaddc[ia] * T +
            HLCOEF[4] * dzloaddc[ia] * T;
        dentdc[ia] += dentdepdc / scale;
    }
    const auto zload_2 = zload * zload;
    const auto entdep =
        (HLCOEF[0] * zload_2 + HLCOEF[1] * zload + HLCOEF[2]) * T * T +
        (HLCOEF[3] * zload_2 + HLCOEF[4] * zload + HLCOEF[5]) * T + HLCOEF[6];
    ent += entdep;
    *_ent = ent / scale;

    const auto dentdepdt =
        2.0 * (HLCOEF[0] * zload_2 + HLCOEF[1] * zload + HLCOEF[2]) * T +
        HLCOEF[3] * zload_2 + HLCOEF[4] * zload + HLCOEF[5];
    dentdt += dentdepdt;
    *_dentdt = dentdt / scale;
}

static double cpp_enthalpy3_vapor(const int ncomp, const double T,
                                  const double *comp, const double scale)
{
    assert(ncomp == VAPOR_NCOMP);

    const auto diff_T = T - TREF;
    const auto diff_T2 = diff_T * diff_T;
    const auto diff_T3 = diff_T2 * diff_T;
    const auto diff_T4 = diff_T3 * diff_T;
    const auto diff_T5 = diff_T4 * diff_T;

    const double sum_comp = std::accumulate(comp, comp + ncomp, 0.0);
    assert(sum_comp == comp[0] + comp[1] + comp[2] + comp[3]);

    double ent = 0.0;
    for (auto ia = 0; ia < ncomp; ++ia) {
        const double intcpig =
            (CPIGCOEF[ia][0] * diff_T + CPIGCOEF[ia][1] * diff_T2 / 2.0 +
             CPIGCOEF[ia][2] * diff_T3 / 3.0 + CPIGCOEF[ia][3] * diff_T4 / 4.0 +
             CPIGCOEF[ia][4] * diff_T5 / 5.0) *
            1e-3;
        const double y = comp[ia] / sum_comp;
        ent += y * intcpig;
    }
    return ent / scale;
}

static void cpp_enthalpy3_vapor_grad(const int ncomp, const double T,
                                     const double *comp, const double scale,
                                     double *const _ent, double *const _dentdt,
                                     double *dentdc)
{
    assert(ncomp == VAPOR_NCOMP);

    const auto diff_T = T - TREF;
    const auto diff_T2 = diff_T * diff_T;
    const auto diff_T3 = diff_T2 * diff_T;
    const auto diff_T4 = diff_T3 * diff_T;
    const auto diff_T5 = diff_T4 * diff_T;

    const double sum_comp = std::accumulate(comp, comp + ncomp, 0.0);
    assert(sum_comp == comp[0] + comp[1] + comp[2] + comp[3]);

    double ent, dentdt;
    ent = dentdt = 0.0;

    dentdc[0] = dentdc[1] = dentdc[2] = dentdc[3] = 0.0;
    for (auto ia = 0; ia < ncomp; ++ia) {
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
        for (auto ib = 0; ib < ncomp; ++ib) {
            const auto dydc = (ia == ib) ? (1.0 - y) / sum_comp : -y / sum_comp;
            dentdc[ib] += dydc * intcpig / scale;
        }
    }
    *_ent = ent / scale;
    *_dentdt = dentdt / scale;
}
