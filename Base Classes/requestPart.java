package Assign1;

import java.io.Serializable;

public class requestPart implements Serializable {
    int taskID;
    int stationID;

    public requestPart(int taskID, int stationID) {
        this.taskID = taskID;
        this.stationID = stationID;
    }

    public int getTaskID() {
        return taskID;
    }

    public int getStationID() {
        return stationID;
    }
}
