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
        ArrayList <Task> tabtask = new ArrayList<>();

        /*for(int taskNumber = 0 ; taskNumber<nb_tache_per_job ; taskNumber++) {
            for(int jobNumber = 0 ; jobNumber<nb_job_instan ; jobNumber++) {
                Task taskToEnqueue = new Task(jobNumber, taskNumber);
                tabtask.add(taskToEnqueue);
            }
        }*/

        ArrayList<Integer> dispo_machine = new ArrayList<Integer>();
        ArrayList<Integer> dispo_job = new ArrayList<Integer>();
        if(nb_tache_per_job > 0){
            for(int job = 0 ;job < nb_job_instan;job++){
                tabtask.add(new Task(job,0));
            }
        }
        for(int i =0;i<instance.numMachines;i++){
            dispo_machine.add(0);

        }

        for(int i =0;i<instance.numJobs;i++){
            dispo_job.add(0);
        }
        //Traitement des Cas de Priorité
        while (!tabtask.isEmpty()) {
            int min, max, temps;
            switch(this.priority) {

                case SPT:
                    Task SPT_task = tabtask.get(0);
                    for (Task newtask : tabtask) {
                        if (instance.duration(newtask) <= instance.duration(SPT_task)) {
                            SPT_task = newtask;
                        }
                    }
                    tabtask.remove(SPT_task);
                    if ((SPT_task.task + 1) < nb_tache_per_job) {
                        Task task_ajouter = new Task(SPT_task.job, SPT_task.task + 1);
                        tabtask.add(task_ajouter);
                    }
                    manualRO.addTaskToMachine(instance.machine(SPT_task), SPT_task);

                    SPT_task = null;

                    break;
                case LPT:

                    Task LPT_task = tabtask.get(0);
                    for (Task newtask : tabtask) {
                        if (instance.duration(newtask) >= instance.duration(LPT_task)) {
                            LPT_task = newtask;
                        }
                    }
                    tabtask.remove(LPT_task);
                    if ((LPT_task.task + 1) < nb_tache_per_job) {
                        Task task_ajouter = new Task(LPT_task.job, LPT_task.task + 1);
                        tabtask.add(task_ajouter);
                    }
                    manualRO.addTaskToMachine(instance.machine(LPT_task), LPT_task);

                    LPT_task = null;
                    break;


                case LRPT:


                    max = 0;
                    Task LRPT_task = tabtask.get(0);

                    //Duree restantes de tous les jobs
                    for (int i = LRPT_task.task; i <= nb_tache_per_job - 1; i++) {
                        max += instance.duration(LRPT_task.job, 1);
                    }

                    //parcours de toutes les taches pour trouver la plus longue
                    for (Task newtask : tabtask) {
                        temps = 0;

                        //calcul durée restante pour chaque tache à partir de la tache actuelle
                        for (int i = newtask.task; i <= nb_tache_per_job - 1; i++) {
                            temps += instance.duration(newtask.job, i);
                        }
                        if (temps > max) {
                            max = temps;
                            LRPT_task = newtask;

                        }
                    }
                    if (LRPT_task != null) {
                        tabtask.remove(LRPT_task);
                        //Si task selectionne n'est pas la derniere tache de son job
                        if ((LRPT_task.task + 1) < nb_tache_per_job) {
                            //cree prochaine tache du meme job et l'ajoute a liste des taches à traiter
                            Task task_ajouter = new Task(LRPT_task.job, LRPT_task.task + 1);
                            tabtask.add(task_ajouter);
                        }

                        manualRO.addTaskToMachine(instance.machine(LRPT_task), LRPT_task);
                    }
                    LRPT_task = null;
                    break;

                case EST_SPT:

                    Task EST_SPT_task = tabtask.get(0);
                    //check le temps de demarrage de la tache init en prenant le max entre dispo du job et machine

                    min = Math.max(dispo_job.get(EST_SPT_task.job), dispo_machine.get(instance.machine(EST_SPT_task)));

                    //pour chaque tache on va check le temps par rapport au max entre dispo_job et machine
                    for (Task newtask : tabtask) {
                        temps = Math.max(dispo_job.get(newtask.job), dispo_machine.get(instance.machine(newtask)));

                        if (temps < min) {
                            min = temps;
                            EST_SPT_task = newtask;
                        }
                        // Si on a le meme temps de demarrage
                        else if (temps == min) {
                            //Si la duree de la nouvelel tache est plus courte 
                            if (instance.duration(newtask) < instance.duration(EST_SPT_task)) {
                                min = temps;
                                EST_SPT_task = newtask;
                            }
                        }
                    }
                    if (EST_SPT_task != null) {
                        tabtask.remove(EST_SPT_task);

                        //Si la tache n'est pas la derniere de son job
                        if ((EST_SPT_task.task + 1) < nb_tache_per_job) {
                            //cree prochaine tache du meme job et l'ajoute a liste des taches à traiter
                            Task task_ajouter = new Task(EST_SPT_task.job, EST_SPT_task.task + 1);
                            tabtask.add(task_ajouter);
                        }
                        manualRO.addTaskToMachine(instance.machine(EST_SPT_task), EST_SPT_task);

                        dispo_machine.set(instance.machine(EST_SPT_task), min + instance.duration(EST_SPT_task));
                        dispo_job.set(EST_SPT_task.job, min + instance.duration(EST_SPT_task));
                    }
                    break;

                case EST_LPT:
                    Task EST_LPT_task = tabtask.get(0);
                    max = Math.max(dispo_job.get(EST_LPT_task.job), dispo_machine.get(instance.machine(EST_LPT_task)));

                    for (Task newtask : tabtask) {
                        temps = Math.max(dispo_job.get(newtask.job), dispo_machine.get(instance.machine(newtask)));

                        if (temps < max) {
                            max = temps;
                            EST_LPT_task = newtask;
                        } else if (temps == max) {
                            if (instance.duration(newtask) > instance.duration(EST_LPT_task)) {
                                max = temps;
                                EST_LPT_task = newtask;
                            }
                        }
                    }
                    if (EST_LPT_task != null) {
                        tabtask.remove(EST_LPT_task);
                        if ((EST_LPT_task.task + 1) < nb_tache_per_job) {
                            Task task_ajouter = new Task(EST_LPT_task.job, EST_LPT_task.task + 1);
                            tabtask.add(task_ajouter);
                        }
                        manualRO.addTaskToMachine(instance.machine(EST_LPT_task), EST_LPT_task);

                        dispo_machine.set(instance.machine(EST_LPT_task), max + instance.duration(EST_LPT_task));
                        dispo_job.set(EST_LPT_task.job, max + instance.duration(EST_LPT_task));
                    }
                    break;

                case EST_LRPT:
                    Task EST_LRPT_task = tabtask.get(0);
                    max = Math.max(dispo_job.get(EST_LRPT_task.job), dispo_machine.get(instance.machine(EST_LRPT_task)));

                    for (Task newtask : tabtask) {
                        temps = Math.max(dispo_job.get(newtask.job), dispo_machine.get(instance.machine(newtask)));
                        if (temps < max) {
                            max = temps;
                            EST_LRPT_task = newtask;
                        } else if (temps == max) {
                            if (instance.duration(newtask) > instance.duration(EST_LRPT_task)) {
                                max = temps;
                                EST_LRPT_task = newtask;
                            }
                        }
                     if (EST_LRPT_task!=null){
                         tabtask.remove(EST_LRPT_task);
                         if (EST_LRPT_task.task+1<nb_tache_per_job){
                             Task task_ajouter = new Task(EST_LRPT_task.job, EST_LRPT_task.task + 1);
                             tabtask.add(task_ajouter);
                         }
                         manualRO.addTaskToMachine(instance.machine(EST_LRPT_task), EST_LRPT_task);

                         dispo_machine.set(instance.machine(EST_LRPT_task), max + instance.duration(EST_LRPT_task));
                         dispo_job.set(EST_LRPT_task.job, max + instance.duration(EST_LRPT_task));
                     }

                        break;
                    }
            }
        }

        return  manualRO.toSchedule();
    }
}
