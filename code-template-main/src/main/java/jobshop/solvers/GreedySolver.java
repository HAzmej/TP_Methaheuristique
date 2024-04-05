package jobshop.solvers;

import java.util.ArrayList;
import java.util.Optional;

import jobshop.Instance;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Schedule;
import jobshop.encodings.Task;

/** An empty shell to implement a greedy solver. */
public class GreedySolver implements Solver {

    /** All possible priorities for the greedy solver. */
    public enum Priority {
        SPT, LPT, SRPT, LRPT, EST_SPT, EST_LPT, EST_SRPT, EST_LRPT
    }

    /** Priority that the solver should use. */
    final Priority priority;

    /** Creates a new greedy solver that will use the given priority. */
    public GreedySolver(Priority p) {
        this.priority = p;
    }

    @Override
    public Optional<Schedule> solve(Instance instance, long deadline) {
        int nb_tache_per_job = instance.numTasks;
        int nb_job_instan = instance.numJobs; 
        ResourceOrder manualRO = new ResourceOrder(instance);
        ArrayList <Task> tabtask = new ArrayList();

        //Si on a des Task à faire : on crée tous les Jobs
        if (nb_tache_per_job>0) {
            for (int i=0; i<nb_job_instan;i++) {
                tabtask.add(new Task(i,0));
            }
        }

        ArrayList <Integer> machine_dispo = new ArrayList<>();
        for ( int i=0 ;i <instance.numMachines;i++) {
            machine_dispo.add(i,0);
        } 

        ArrayList <Integer> job_dispo = new ArrayList<>();
        for ( int i=0 ;i <instance.numJobs;i++) {
            job_dispo.add(i,0);
        } 

        //Traitement des Cas de Priorité
        while (!tabtask.isEmpty()) {
            switch(priority) {
                case SPT: 
                    System.out.println("Je suis SPT");

                    break;
                case LRPT : 
                    System.out.println("Je suis LRPT");
                    break;
            }
        }
        throw new UnsupportedOperationException();  
    }
}
