package jobshop.solvers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jobshop.Instance;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Schedule;
import jobshop.solvers.neighborhood.Neighborhood;
import jobshop.solvers.neighborhood.Nowicki;

/** An empty shell to implement a descent solver. */
public class Tabou implements Solver {

    final Neighborhood neighborhood;
    final Solver baseSolver;
    final int maxiter;
    final int dureetabou;

    /** Creates a new descent solver with a given neighborhood and a solver for the initial solution.
     *
     * @param neighborhood Neighborhood object that should be used to generates neighbor solutions to the current candidate.
     * @param baseSolver A solver to provide the initial solution.
     */
    public Tabou(Neighborhood neighborhood, Solver baseSolver, int maxiter,int dureetabou) {
        this.neighborhood = neighborhood;
        this.baseSolver = baseSolver;
        this.maxiter=maxiter;
        this.dureetabou=dureetabou;
    }

    @Override
    public Optional<Schedule> solve(Instance instance, long deadline) {

        Optional <Schedule> sched = this.baseSolver.solve(instance, deadline);
        Nowicki now = new Nowicki();

        ResourceOrder R0 = new ResourceOrder(sched.get());

        List <ResourceOrder> list_R0;
        int makespan_init = R0.toSchedule().get().makespan();

        long exec_time=0;
        boolean recherche =true;
        ResourceOrder bestR0=R0;
        

        for (int i = 0; i < maxiter && System.currentTimeMillis() < deadline; i++) {
            list_R0 = now.generateNeighbors(bestR0);
            ResourceOrder prevR0=null;
        while((exec_time<deadline)&&(recherche)){
            //meilleur ordre trouve pour le moment
            prevR0=bestR0;
            List <Nowicki.Swap> listswapR0 = now.allSwaps(bestR0);
            ArrayList<Nowicki.Swap> nontabouswaps = new ArrayList<Nowicki.Swap>();
            Nowicki.Swap best_swap=null;
            //à chaque fois on genere les voisins du meilleur ordre obtenu
            //list_RO = neighborhood.generateNeighbors(bestR0);
            /*for(Nowicki.Swap NS :listswapR0){
                boolean taboo=false;
                if (R.toSchedule().get().isValid() && R.toSchedule().isPresent()){
                    int makespan = R.toSchedule().get().makespan();
                    if (makespan < makespan_init){
                        makespan_init = makespan;
                        bestR0 = R;
                    }
                }
            }*/
            if (prevR0==bestR0){
                recherche=false;
            }
            exec_time=System.currentTimeMillis();
        }
    

        return bestR0.toSchedule();
    }

}
}