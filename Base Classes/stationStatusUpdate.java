package Assign1;

import java.io.Serializable;

public class stationStatusUpdate implements Serializable {
    int stationID;
    stationStatus stationStatus;
    int JobID;
    Task task;

    public stationStatusUpdate(int stationID, Assign1.stationStatus stationStatus, int jobID, Task task) {
        this.stationID = stationID;
        this.stationStatus = stationStatus;
        JobID = jobID;
        this.task = task;
    }

    public stationStatusUpdate(){
        this.stationID = 0;
        this.stationStatus = Assign1.stationStatus.OFF;
        this.JobID = 0;
        this.task = null;
    }

    public int getStationID() {
        return stationID;
    }

    public Assign1.stationStatus getStationStatus() {
        return stationStatus;
    }

    public int getJobID() {
        return JobID;
    }

    public Task getTask() {
        return task;
    }

    public void setStationID(int stationID) {
        this.stationID = stationID;
    }

    public void setStationStatus(Assign1.stationStatus stationStatus) {
        this.stationStatus = stationStatus;
    }

    public void setJobID(int jobID) {
        JobID = jobID;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
