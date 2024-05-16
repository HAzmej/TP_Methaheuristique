package jobshop.solvers;

import java.util.Optional;

import jobshop.Instance;
import jobshop.encodings.Schedule;
import jobshop.solvers.neighborhood.Nowicki;

/** Common interface that must implemented by all solvers. */
public interface Solver {

    /** Look for a solution until blocked or a deadline has been met.
     *
     * @param instance Jobshop instance that should be solved.
     * @param deadline Absolute time at which the solver should have returned a solution.
     *                 This time is in milliseconds and can be compared with System.currentTimeMilliseconds()
     * @return An optional schedule that will be non empty if a solution was found.
     */
    Optional<Schedule> solve(Instance instance, long deadline);

    /** Static factory method to create a new solver based on its name. */
    static Solver getSolver(String name) {
        switch (name) {
            case "basic": return new BasicSolver();
            case "spt": return new GreedySolver(GreedySolver.Priority.SPT);
            case "lpt": return new GreedySolver(GreedySolver.Priority.LPT);
            case "lrpt": return new GreedySolver(GreedySolver.Priority.LRPT);
            case "est_spt": return new GreedySolver(GreedySolver.Priority.EST_SPT);
            case "est_lpt": return new GreedySolver(GreedySolver.Priority.EST_LPT);
            case "descente_spt" : return new DescentSolver(new Nowicki(), new GreedySolver(GreedySolver.Priority.SPT));
            case "descente_lpt" : return new DescentSolver(new Nowicki(), new GreedySolver(GreedySolver.Priority.LPT));
            case "descente_lrpt" : return new DescentSolver(new Nowicki(), new GreedySolver(GreedySolver.Priority.LRPT));
            case "descente_est_spt" : return new DescentSolver(new Nowicki(), new GreedySolver(GreedySolver.Priority.EST_SPT));
            case "descente_est_lpt" : return new DescentSolver(new Nowicki(), new GreedySolver(GreedySolver.Priority.EST_LPT));
            
            // TODO: add new solvers
            default: throw new RuntimeException("Unknown solver: "+ name);
        }
    }

}
