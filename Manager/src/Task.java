import java.io.Serializable;

public class Task implements Serializable {
    int id;
    boolean isMandatory;
    taskType taskType;
    int taskDuration;
    Inventory taskParts;
    String taskDescription;

    public boolean isMandatory() {
        return isMandatory;
    }

    public int getId() {
        return id;
    }

    public taskType getTaskType() {
        return taskType;
    }

    public int getTaskDuration() {
        return taskDuration;
    }

    public Inventory getTaskParts() {
        return taskParts;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public Task(int id, boolean isMandatory, taskType taskType, int taskDuration, Inventory taskParts, String taskDescription) {
        this.id = id;
        this.isMandatory = isMandatory;
        this.taskType = taskType;
        this.taskDuration = taskDuration;
        this.taskParts = taskParts;
        this.taskDescription = taskDescription;
    }
}
