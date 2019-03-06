package Assign1;

import java.io.Serializable;
import java.util.Map;

public class Task implements Serializable {
    int id;
    boolean isMandatory;
    taskType taskType;
    int taskDuration;
    Map<Part, Integer> taskParts;
    String taskDescription;

    public boolean isMandatory() {
        return isMandatory;
    }

    public int getId() {
        return id;
    }

    public Assign1.taskType getTaskType() {
        return taskType;
    }

    public int getTaskDuration() {
        return taskDuration;
    }

    public Map<Part, Integer> getTaskParts() {
        return taskParts;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public Task(int id, boolean isMandatory, Assign1.taskType taskType, int taskDuration, Map<Part, Integer> taskParts, String taskDescription) {
        this.id = id;
        this.isMandatory = isMandatory;
        this.taskType = taskType;
        this.taskDuration = taskDuration;
        this.taskParts = taskParts;
        this.taskDescription = taskDescription;
    }
}
