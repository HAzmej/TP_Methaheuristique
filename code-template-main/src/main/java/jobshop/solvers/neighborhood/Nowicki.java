package jobshop.solvers.neighborhood;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Task;

/** Implementation of the Nowicki and Smutnicki neighborhood.
 *
 * It works on the ResourceOrder encoding by generating two neighbors for each block
 * of the critical path.
 * For each block, two neighbors should be generated that respectively swap the first two and
 * last two tasks of the block.
 */
public class Nowicki extends Neighborhood {

    /** A block represents a subsequence of the critical path such that all tasks in it execute on the same machine.
     * This class identifies a block in a ResourceOrder representation.
     *
     * Consider the solution in ResourceOrder representation
     * machine 0 : (0,1) (1,2) (2,2)
     * machine 1 : (0,2) (2,1) (1,1)
     * machine 2 : ...
     *
     * The block with : machine = 1, firstTask= 0 and lastTask = 1
     * Represent the task sequence : [(0,2) (2,1)]
     *
     * */
    public static class Block {
        /** machine on which the block is identified */
        public final int machine;
        /** index of the first task of the block */
        public  int firstTask;
        /** index of the last task of the block */
        public  int lastTask;

        /** Creates a new block. */
        Block(int machine, int firstTask, int lastTask) {
            this.machine = machine;
            this.firstTask = firstTask;
            this.lastTask = lastTask;
        }
        public int getFirstTask(){return this.firstTask;}
        public int getLastTask(){return this.lastTask;}

        public void setFirstTask(int indexFirstTask){
            this.firstTask = indexFirstTask;
        }

        public void setLastTask(int indexLastTask){
            this.lastTask = indexLastTask;
        }

    }

    /**
     * Represents a swap of two tasks on the same machine in a ResourceOrder encoding.
     *
     * Consider the solution in ResourceOrder representation
     * machine 0 : (0,1) (1,2) (2,2)
     * machine 1 : (0,2) (2,1) (1,1)
     * machine 2 : ...
     *
     * The swap with : machine = 1, t1= 0 and t2 = 1
     * Represent inversion of the two tasks : (0,2) and (2,1)
     * Applying this swap on the above resource order should result in the following one :
     * machine 0 : (0,1) (1,2) (2,2)
     * machine 1 : (2,1) (0,2) (1,1)
     * machine 2 : ...
     */
    public static class Swap {
        /** machine on which to perform the swap */
        public final int machine;

        /** index of one task to be swapped (in the resource order encoding).
         * t1 should appear earlier than t2 in the resource order. */
        public final int t1;

        /** index of the other task to be swapped (in the resource order encoding) */
        public final int t2;

        /** Creates a new swap of two tasks. */
        Swap(int machine, int t1, int t2) {
            this.machine = machine;
            if (t1 < t2) {
                this.t1 = t1;
                this.t2 = t2;
            } else {
                this.t1 = t2;
                this.t2 = t1;
            }
        }


        /** Creates a new ResourceOrder order that is the result of performing the swap in the original ResourceOrder.
         *  The original ResourceOrder MUST NOT be modified by this operation.
         */
        public ResourceOrder generateFrom(ResourceOrder original) {
            ResourceOrder contrefacon = original.copy();
            contrefacon.swapTasks(machine, t1, t2);
            return contrefacon;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Swap swap = (Swap) o;
            return machine == swap.machine && t1 == swap.t1 && t2 == swap.t2;
        }

        @Override
        public int hashCode() {
            return Objects.hash(machine, t1, t2);
        }
    }


    @Override
    public List<ResourceOrder> generateNeighbors(ResourceOrder current) {
        // convert the list of swaps into a list of neighbors (function programming FTW)
        return allSwaps(current).stream().map(swap -> swap.generateFrom(current)).collect(Collectors.toList());

    }

    /** Generates all swaps of the given ResourceOrder.
     * This method can be used if one wants to access the inner fields of a neighbors. */
    public List<Swap> allSwaps(ResourceOrder current) {
        List<Swap> neighbors = new ArrayList<>();
        // iterate over all blocks of the critical path
        for(var block : blocksOfCriticalPath(current)) {
            // for this block, compute all neighbors and add them to the list of neighbors
            neighbors.addAll(neighbors(block));
        }
        return neighbors;
    }

    /** Returns a list of all the blocks of the critical path. */
    List<Block> blocksOfCriticalPath(ResourceOrder order) {
        //recup num de machine
        //commencez par 1ere task avec sa machine
        //iterer juqu'a trouver une autre task avec la meme machine
        //stocker task1 , task2 et la meme machine

        List<Task> cheminCritique = order.toSchedule().get().criticalPath();
        List<Block> listeDesBlocs = new ArrayList<>();

        Task task = cheminCritique.get(0);

        int machine = order.instance.machine(cheminCritique.get(0));
        int firstTask = Arrays.asList(order.tasksByMachine[machine]).indexOf(task);
        int lastTask = firstTask;

        for (int i = 1; i < cheminCritique.size(); i++) {

            task = cheminCritique.get(i);

            if (machine == order.instance.machine(task)) {
                lastTask++;

            } else {

                if (firstTask != lastTask) {
                    listeDesBlocs.add(new Block(machine, firstTask, lastTask));
                }

                machine = order.instance.machine(task);
                firstTask = Arrays.asList(order.tasksByMachine[machine]).indexOf(task);
                lastTask = firstTask;

            }
        }
        return listeDesBlocs;
    }

    /** For a given block, return the possible swaps for the Nowicki and Smutnicki neighborhood */
    List<Swap> neighbors(Block block) {
        ArrayList<Swap> listSwap = new ArrayList<Swap>();
        /*
        for(int i = block.firstTask ; i < block.lastTask ; i++){
            //entre les taches consecutives
            Swap swap = new Swap(block.machine, i, i+1);
            listSwap.add(swap);
        }
        return listSwap;*/
        List<Swap> swap = new ArrayList<Swap>();
        int calc = block.lastTask - block.firstTask+1;

        if (calc >= 2) {
            swap.add(new Swap(block.machine, block.firstTask, block.firstTask+1 ));
            swap.add(new Swap(block.machine, block.lastTask-1, block.lastTask   ));
        }
        else {
            swap.add(new Swap(block.machine,block.firstTask,block.lastTask));
        }

        return swap;

    }


}
