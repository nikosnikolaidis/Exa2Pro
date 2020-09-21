#include "cg.h"

int mw_cg_solve(mw_cg_t *__restrict cg, mw_system_t *__restrict system, real_h *__restrict x, 
                skepu_data *__restrict skepu_containers) {
      

   int num; ///< size of the solution vector
   int i, iter;
   real_h rsold; ///< scalar to store residual norm
   real_h alpha, beta;  ///< CG coefficients //alpha,
   real_h res_norm, res_tol;
   bool cg_converged;
   real_h average_charges;
   int rank=0, np=1, count=0;

   num = *cg->N_b;

#ifdef MW_USE_MPI
   MPI_Comm_rank(MW_COMM_WORLD, &rank);
   MPI_Comm_size(MW_COMM_WORLD, &np);
#endif
   std::cout.precision(16);
   /// Setup tolerance on residual norm
   /// stopping criteria: |r_k| < tol * |b| where tol is a user input parameter
   res_tol = *cg->tol * sqrt(real_t(num));

   /// Compute Ax0
   apply_A(system, x, cg->Ap, skepu_containers);

   /// Setup initial residual
   for (i = 0; i < num; i++) {
      cg->res[i] = cg->b[i] - cg->Ap[i];
   }

   /// p0 = z0
   for (i = 0; i < num; i++) {
      cg->p[i] = cg->res[i];
   }

   rsold = dot_product(cg->res,cg->res,system->num_atoms);

   /// Begin CG iterations
   cg_converged = false;
   for (iter = 0; iter < *cg->max_iterations; iter++) {
      //printf("we are starting iteration number %d \n",iter);
      apply_A(system, cg->p, cg->Ap, skepu_containers);

      const real_h pAp = dot_product(cg->p,cg->Ap,system->num_atoms);
      alpha = rsold / pAp;
      for (i = 0; i < num; i++) {
         x[i] = x[i] + alpha * cg->p[i];
      }

      if (*system->electrode_charge_neutrality) {
         const real_h total_charges = sum(x, system->NX_q_atoms);
         average_charges = total_charges / real_h(num);
         for (i = 0; i < num; i++) {
            x[i] = x[i] - average_charges;
         }
      }

      for (i = 0; i < num; i++) {
         cg->res[i] = cg->res[i] - alpha * cg->Ap[i];
      }

#ifdef CADNA
      real_h rsnew = dot_product(cg->res,cg->res,system->num_atoms);
      count++;
#else
      const real_h rsnew = dot_product(cg->res,cg->res,system->num_atoms);
#endif
      res_norm = sqrt(rsnew);  

#ifdef CADNA
      //std::cout<<"number of significant digit of res_norm: "<<res_norm.nb_significant_digit()<<" alpha: "<<alpha.nb_significant_digit()<<std::endl;
      std::cout<<" "<<rank<<" This is the end of iteration number "<<iter<<" convergence: res_norm = "<<res_norm<<std::endl;
      res_norm.display();  

#ifdef MW_USE_MPI
      if (rank==0) {
#endif
         if (count==1) // resetting every n iterations (here n=1)
         {
            std::cout<<" We are resetting all 3 cadna values (x,y,z) with their mean of rank 0"<<std::endl;
            res_norm = ( res_norm.getx() + res_norm.gety() + res_norm.getz() ) / 3.0;
            rsnew = ( rsnew.getx() + rsnew.gety() + rsnew.getz() ) / 3.0;
            rsold = ( rsold.getx() + rsold.gety() + rsold.getz() ) / 3.0;
            for (i = 0; i < num; i++) {
               cg->res[i] = ( cg->res[i].getx() + cg->res[i].gety() + cg->res[i].getz() ) / 3.0;
               cg->p[i]   = ( cg->p[i].getx() + cg->p[i].gety() + cg->p[i].getz() ) / 3.0;
            }
            count = 0;
         }
#ifdef MW_USE_MPI
      }//*/
#ifdef SINGLE_P
      MPI_Bcast(&res_norm,1,MPI_FLOAT_ST,0,MW_COMM_WORLD);
      MPI_Bcast(&rsnew,1,MPI_FLOAT_ST,0,MW_COMM_WORLD);
      MPI_Bcast(&rsold,1,MPI_FLOAT_ST,0,MW_COMM_WORLD);
      MPI_Bcast(cg->res,num,MPI_FLOAT_ST,0,MW_COMM_WORLD);
      MPI_Bcast(cg->p,num,MPI_FLOAT_ST,0,MW_COMM_WORLD);
#else
      MPI_Bcast(&res_norm,1,MPI_DOUBLE_ST,0,MW_COMM_WORLD);
      MPI_Bcast(&rsnew,1,MPI_DOUBLE_ST,0,MW_COMM_WORLD);
      MPI_Bcast(&rsold,1,MPI_DOUBLE_ST,0,MW_COMM_WORLD);
      MPI_Bcast(cg->res,num,MPI_DOUBLE_ST,0,MW_COMM_WORLD);
      MPI_Bcast(cg->p,num,MPI_DOUBLE_ST,0,MW_COMM_WORLD);
#endif
#endif
#endif

      if (res_norm < res_tol) {
         *cg->last_iteration_count = iter;
         *cg->total_iteration_count = *cg->total_iteration_count + iter;
         *cg->last_residual = res_norm;
         *cg->last_residual_tol = res_tol;
         cg_converged = true;
         break;
      }

      /// Setup for next iteration
      beta = rsnew / rsold;
      for (i = 0; i < num; i++) {
         cg->p[i] = cg->res[i] + beta * cg->p[i];
      }
      rsold = rsnew;
   }

   if (!cg_converged) {
      *cg->last_iteration_count = iter;
      *cg->last_residual = res_norm;
      *cg->last_residual_tol = res_tol;
      print_statistics(cg->last_iteration_count, cg->last_residual, cg->last_residual_tol);
      runtime_error();
   }

   return 0;
}


///================================================================================
/// Compute A*x, the potential on electrode atoms due to electrode atoms
int apply_A(mw_system_t *__restrict system, real_h *__restrict x, real_h *__restrict y, 
            skepu_data *__restrict skepu_containers) {


   int i;

   MW_coulomb_elec2elec_potential(&system->localwork, &system->ewald, &system->box, 
                                  system->electrodes, system->N_electrodes, system->xyz_atoms, 
                                  system->NX_xyz_atoms, x, system->NX_coulomb_potential_atoms, 
                                  skepu_containers, system->coulomb_potential_atoms);

   for (i = 0; i < *system->num_atoms; i++) {
      y[i] = (system->coulomb_potential_atoms[i + (*system->NX_coulomb_potential_atoms)*0] + 
              system->coulomb_potential_atoms[i + (*system->NX_coulomb_potential_atoms)*1] +
              system->coulomb_potential_atoms[i + (*system->NX_coulomb_potential_atoms)*2] +
              system->coulomb_potential_atoms[i + (*system->NX_coulomb_potential_atoms)*3]);
   }

   return 0;
}


///================================================================================
/// Print the data structure parameters
int print_statistics(int *__restrict last_iteration_count, real_t *__restrict last_residual, 
                     real_t *__restrict last_residual_tol) {

   std::cout << "|cg| number of iterations: " << *last_iteration_count << "\n";
   std::cout << "|cg| Residual norm:        " << *last_residual << "\n";
   std::cout << "|cg| Residual target:      " << *last_residual_tol << "\n";

   return 0;
}


///================================================================================
/// Runtime error
int runtime_error() {

   std::cout << "[error]..." << "CG failed to converge" << "\n";
   std::cout << "          subroutine: " << "solve" << "\n";
   std::cout << "          file: " << "cg.cpp" << "\n";

#ifdef MW_USE_MPI
#ifdef CADNA
   cadna_mpi_end();
#endif
   MPI_Abort(MW_COMM_WORLD, 1);
#else
#ifdef CADNA
   cadna_end();
#endif
   std::exit(1);
#endif

}
