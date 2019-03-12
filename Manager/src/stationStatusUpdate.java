import java.io.Serializable;

public class stationStatusUpdate implements Serializable {
    int stationID;
    stationStatus stationStatus;
    int JobID;
    Task task;

    public stationStatusUpdate(int stationID, stationStatus stationStatus, int jobID, Task task) {
        this.stationID = stationID;
        this.stationStatus = stationStatus;
        JobID = jobID;
        this.task = task;
    }

    public stationStatusUpdate(){
        this.stationID = 0;
        this.stationStatus = stationStatus.OFF;
        this.JobID = 0;
        this.task = null;
    }

    public String statusToString(){
        String output = "";
        if(stationStatus.equals(stationStatus.OFF)){
            output += "Station: " + stationID + " is OFF";
        }else{
            output = "Station: " + stationID + " is working on " + getTask().getTaskDescription() + " JOB STATUS: ";
            if(stationStatus.equals(stationStatus.WAITING)){
                output += "WAITING";
            }else if(stationStatus.equals(stationStatus.WORKING)){
                output += "WORKING";
            }else if(stationStatus.equals(stationStatus.HALTED)){
                output += "HALTED";
            }
            output += " ON JOB: " + getJobID();
        }
        return output;
    }

    public int getStationID() {
        return stationID;
    }

    public stationStatus getStationStatus() {
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

    public void setStationStatus(stationStatus stationStatus) {
        this.stationStatus = stationStatus;
    }

    public void setJobID(int jobID) {
        JobID = jobID;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
