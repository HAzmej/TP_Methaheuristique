package jobshop.solvers;

import jobshop.Instance;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Schedule;
import jobshop.solvers.neighborhood.Neighborhood;
import jobshop.solvers.neighborhood.Nowicki;

import java.util.*;

/** An implementation of a Taboo solver. */
public class Tabou implements Solver {

    final Neighborhood neighborhood;
    final Solver baseSolver;
    final int maxIterations;
    final int tabooDuration;

    public Tabou(Neighborhood neighborhood, Solver baseSolver, int maxIterations, int tabooDuration) {
        this.neighborhood = neighborhood;
        this.baseSolver = baseSolver;
        this.maxIterations = maxIterations;
        this.tabooDuration = tabooDuration;
    }

    @Override
    public Optional<Schedule> solve(Instance instance, long deadline) {
        Optional<Schedule> sched = this.baseSolver.solve(instance, deadline);
        if (!sched.isPresent()) {
            return Optional.empty();
        }

        ResourceOrder bestR0 = new ResourceOrder(sched.get());
        ResourceOrder R0 = bestR0;
        Nowicki now = new Nowicki();

        List<ResourceOrder> tabooOrders = new ArrayList<>();
        List<Integer> tabooIterations = new ArrayList<>();

        int bestMakespan = bestR0.toSchedule().get().makespan();

        for (int iteration = 0; iteration < maxIterations && System.currentTimeMillis() < deadline; iteration++) {
            List<ResourceOrder> neighbors = now.generateNeighbors(R0);
            ResourceOrder bestNeighbor = null;
            int bestNeighborMakespan = Integer.MAX_VALUE;

            for (ResourceOrder neighbor : neighbors) {
                if (!neighbor.toSchedule().isPresent()) {
                    continue;
                }
                int neighborMakespan = neighbor.toSchedule().get().makespan();

                boolean isTaboo = false;
                for (int i = 0; i < tabooOrders.size(); i++) {
                    if (tabooOrders.get(i).equals(neighbor) && (iteration - tabooIterations.get(i) <= tabooDuration)) {
                        isTaboo = true;
                        break;
                    }
                }

                if (!isTaboo && (bestNeighbor == null || neighborMakespan < bestNeighborMakespan)) {
                    bestNeighbor = neighbor;
                    bestNeighborMakespan = neighborMakespan;
                }
            }

            if (bestNeighbor != null) {
                R0 = bestNeighbor;
                int currentMakespan = bestNeighborMakespan;

                if (currentMakespan < bestMakespan) {
                    bestR0 = R0;
                    bestMakespan = currentMakespan;
                }

                tabooOrders.add(R0);
                tabooIterations.add(iteration);
            }
        }

        return bestR0.toSchedule();
    }
}
