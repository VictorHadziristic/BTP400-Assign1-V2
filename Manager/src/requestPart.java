import java.io.Serializable;

public class requestPart implements Serializable {
    int stationID;
    Task task;

    public requestPart(int StationID, Task task) {
        this.stationID = StationID;
        this.task = task;
    }

    public int getStationID() {
        return stationID;
    }

    public Task getTask() {
        return task;
    }
}
