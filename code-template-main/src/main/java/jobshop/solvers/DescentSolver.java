package jobshop.solvers;

import java.util.List;
import java.util.Optional;

import jobshop.Instance;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Schedule;
import jobshop.solvers.neighborhood.Neighborhood;

/** An empty shell to implement a descent solver. */
public class DescentSolver implements Solver {

    final Neighborhood neighborhood;
    final Solver baseSolver;

    /** Creates a new descent solver with a given neighborhood and a solver for the initial solution.
     *
     * @param neighborhood Neighborhood object that should be used to generates neighbor solutions to the current candidate.
     * @param baseSolver A solver to provide the initial solution.
     */
    public DescentSolver(Neighborhood neighborhood, Solver baseSolver) {
        this.neighborhood = neighborhood;
        this.baseSolver = baseSolver;
    }

    @Override
    public Optional<Schedule> solve(Instance instance, long deadline) {

        ResourceOrder R0 = new ResourceOrder(instance);
        List<ResourceOrder> list_RO ;

        int makespan_init = R0.toSchedule().get().makespan();

        long exec_time=0;
        boolean recherche =true;
        ResourceOrder bestR0=R0;
        ResourceOrder prevR0;
        while((exec_time<deadline)&&(recherche)){
            //meilleur ordre trouve pour le moment
            prevR0=bestR0;
            //à chaque fois on genere les voisins du meilleur ordre obtenu
            list_RO = neighborhood.generateNeighbors(bestR0);
            for(ResourceOrder R :list_RO){
                if (R.toSchedule().get().isValid() && R.toSchedule().isPresent()){
                    int makespan = R.toSchedule().get().makespan();
                    if (makespan < makespan_init){
                        makespan_init = makespan;
                        bestR0 = R;
                    }
                }
            }
            if (prevR0==bestR0){
                recherche=false;
            }
            exec_time=System.currentTimeMillis();
        }
        return bestR0.toSchedule();
    }

}