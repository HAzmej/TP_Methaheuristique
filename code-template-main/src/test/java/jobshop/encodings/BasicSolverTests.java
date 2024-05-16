package jobshop.encodings;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.Test;

import jobshop.Instance;
import jobshop.solvers.BasicSolver;
import jobshop.solvers.Solver;

public class BasicSolverTests {

    @Test
    public void testBasicSolver() throws IOException {
        Instance instance = Instance.fromFile(Paths.get("instances/aaa1"));

        Solver solver = new BasicSolver();
        Optional<Schedule> result = solver.solve(instance, System.currentTimeMillis() + 10);

        assert result.isPresent() : "The solver did not find a solution";
        // extract the schedule associated to the solution
        Schedule schedule = result.get();
        assert  schedule.isValid() : "The solution is not valid";

        System.out.println("Makespan: " + schedule.makespan());
        System.out.println("Schedule: \n" + schedule);
        System.out.println(schedule.asciiGantt());

        assert schedule.makespan() == 12 : "The basic solver should have produced a makespan of 12 for this instance.";
    }

    @Test
    public void testDescentSover() throws IOException{
        Instance instance = Instance.fromFile(Paths.get("instances/ft06"));
        Solver DescentSolver = Solver.getSolver("spt");
        /*Solver DescentSolver1 = Solver.getSolver("lpt");
        Solver DescentSolver2 = Solver.getSolver("lrpt");
        Solver DescentSolver3 = Solver.getSolver("est_spt");
        Solver DescentSolver4 = Solver.getSolver("est_lpt");*/
        /* 

        

        assert result.isPresent() : "The solver did not find a solution";

        

        assert  schedule.isValid() : "The solution is not valid";*/
        Optional<Schedule> result = DescentSolver.solve(instance, System.currentTimeMillis() + 10);
        Schedule schedule = result.get();

        System.out.println("Makespan: " + schedule.makespan());
        System.out.println("Schedule: \n" + schedule);
        System.out.println(schedule.asciiGantt());

    }

}
