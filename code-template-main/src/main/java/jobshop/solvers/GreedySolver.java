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
        ArrayList <Task> tabtask = new ArrayList<Task>();

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
            dispo_machine.add(i,0);

        }

        for(int i =0;i<instance.numJobs;i++){
            dispo_job.add(i,0);
        }
        //Traitement des Cas de Priorité
        while (!tabtask.isEmpty()) {
            int min=0;
            int max=0;
            int temps=0;
            switch(this.priority) {

                case SPT: 

                    System.out.println("Je suis SPT");
                    Task SPT_task = tabtask.get(0);
                    min = instance.duration(SPT_task);
                    
                    for (int i =0 ; i< tabtask.size();i++) {
                        temps = instance.duration(tabtask.get(i));
                        if (temps <= min ) {
                            min = temps ;
                            SPT_task = tabtask.get(i);
                        }
                    }
                    System.out.println("remove : " + SPT_task);
                    tabtask.remove(SPT_task);
                    manualRO.addTaskToMachine(instance.machine(SPT_task), SPT_task);
                    System.out.println(min);
                    break;
                
                case LPT :

                    System.out.println("Je suis LPT");
                    Task LPT_task = tabtask.get(0);
                    max = instance.duration(LPT_task);
                    
                    for (int i =0 ; i< tabtask.size();i++) {
                        temps = instance.duration(tabtask.get(i));
                        if (temps >= max ) {
                            max = temps ;
                            LPT_task = tabtask.get(i);
                        }
                    }
                    System.out.println("remove : " + LPT_task);
                    tabtask.remove(LPT_task);
                    manualRO.addTaskToMachine(instance.machine(LPT_task), LPT_task);
                    System.out.println(tabtask);
                    break;


                case LRPT : 

                    System.out.println("Je suis LRPT");
                    max=0;
                    Task LRPT_task = tabtask.get(0);

                    //Duree restantes de tous les jobs
                    for (int i =LRPT_task.task ; i<= nb_tache_per_job-1;i++) {
                        max += instance.duration(LRPT_task.job,1);
                    }

                    //parcours de toutes les taches pour trouver la plus longue
                    for (Task newtask: tabtask) {
                        temps=0;

                        //calcul durée restante pour chaque tache à partir de la tache actuelle
                        for (int i = newtask.task ; i<= nb_tache_per_job-1;i++){
                            temps += instance.duration(newtask.job,i);
                        }
                        if (temps > max){
                            max=temps;
                            LRPT_task=newtask;

                        }
                    }
                    tabtask.remove(LRPT_task);
                    //Si task selectionne n'est pas la derniere tache de son job 
                    if ((LRPT_task.task+1)<nb_tache_per_job){
                        //cree prochaine tache du meme job et l'ajoute a liste des taches à traiter
                        Task  task_ajouter = new Task(LRPT_task.job,LRPT_task.task+1);
                        tabtask.add(task_ajouter);
                    }

                    manualRO.addTaskToMachine(instance.machine(LRPT_task), LRPT_task);
                    LRPT_task=null;
                    break;

                case EST_SPT:
                    
                    System.out.println("Je suis EST_SPT");
                    Task EST_SPT_task = tabtask.get(0);
                    //check le temps de demarrage de la tache init en prenant le max entre dispo du job et machine
                    
                    min = Math.max(dispo_job.get(EST_SPT_task.job),dispo_machine.get(instance.machine(EST_SPT_task)));

                    //pour chaque tache on va check le temps par rapport au max entre dispo_job et machine
                    for (Task newtask : tabtask){
                        temps = Math.max(dispo_job.get(newtask.job),dispo_machine.get(instance.machine(newtask)));

                        if (temps<min){
                            min=temps;
                            EST_SPT_task=newtask;
                        }
                        // Si on a le meme temps de demarrage
                        else if (temps==min){
                            //Si la duree de la nouvelel tache est plus courte 
                            if (instance.duration(newtask)<instance.duration(EST_SPT_task)){
                                min=temps;
                                EST_SPT_task=newtask;
                            }
                        }
                    }
                    tabtask.remove(EST_SPT_task);

                    //Si la tache n'est pas la derniere de son job
                    if ((EST_SPT_task.task+1)<nb_tache_per_job){
                        //cree prochaine tache du meme job et l'ajoute a liste des taches à traiter
                        Task  task_ajouter = new Task(EST_SPT_task.job,EST_SPT_task.task+1);
                        tabtask.add(task_ajouter);
                    }
                    manualRO.addTaskToMachine(instance.machine(EST_SPT_task), EST_SPT_task);

                    dispo_machine.set(instance.machine(EST_SPT_task),min+instance.duration(EST_SPT_task));
                    dispo_job.set(EST_SPT_task.job,min+instance.duration(EST_SPT_task));
                    
                break;

                case EST_LPT:

                    System.out.println("Je suis EST_LPT");
                    Task EST_LPT_task = tabtask.get(0);
                    max = Math.max(dispo_job.get(EST_LPT_task.job),dispo_machine.get(instance.machine(EST_LPT_task)));

                    for (Task newtask : tabtask){
                        temps = Math.max(dispo_job.get(newtask.job),dispo_machine.get(instance.machine(newtask)));

                        if (temps < max){
                            max=temps;
                            EST_LPT_task=newtask;
                        }
                        else if (temps==max){
                            if (instance.duration(newtask) > instance.duration(EST_LPT_task)){
                                max=temps;
                                EST_LPT_task=newtask;
                            }
                        }
                    }
                    tabtask.remove(EST_LPT_task);
                    if ((EST_LPT_task.task+1)<nb_tache_per_job){
                        Task  task_ajouter = new Task(EST_LPT_task.job,EST_LPT_task.task+1);
                        tabtask.add(task_ajouter);
                    }
                    manualRO.addTaskToMachine(instance.machine(EST_LPT_task), EST_LPT_task);

                    dispo_machine.set(instance.machine(EST_LPT_task),max+instance.duration(EST_LPT_task));
                    dispo_job.set(EST_LPT_task.job,max+instance.duration(EST_LPT_task));
                
                break;
            }
        }
        return  manualRO.toSchedule();
    }
}
