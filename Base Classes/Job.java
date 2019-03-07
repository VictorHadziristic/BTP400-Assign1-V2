package Assign1;

import java.io.Serializable;
import java.util.ArrayList;

public class Job implements Serializable {
    int id;
    ArrayList<Task> assemblyTasks;

    public Job(int id, ArrayList<Task> assemblyTasks) {
        this.id = id;
        this.assemblyTasks = assemblyTasks;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Task> getAssemblyTasks() {
        return assemblyTasks;
    }

    public boolean isJobComplete(){
        return (this.assemblyTasks.size() == 0);
    }

    public Task getCurrentTask(){
        return this.assemblyTasks.get(0);
    }

    public void completeTask(){
        this.assemblyTasks.remove(0);
    }
}
