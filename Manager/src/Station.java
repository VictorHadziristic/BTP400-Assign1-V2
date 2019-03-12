import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Station implements Serializable {
    private int id;
    private stationStatus StationStatus = stationStatus.OFF;
    private Task task = null;
    private int socketReceive;
    private int socketTransmit;
    private HashMap<Part, Integer> inventory;

    public Station(int id, Task task, int socketReceive, int socketTransmit) {
        this.id = id;
        this.task = task;
        this.socketReceive = socketReceive;
        this.socketTransmit = socketTransmit;
        this.StationStatus = stationStatus.WAITING;
    }

    public int getId() {
        return id;
    }

    public stationStatus getStationStatus() {
        return StationStatus;
    }

    public Task getTask() {
        return task;
    }

    public int getSocketReceive() {
        return socketReceive;
    }

    public int getSocketTransmit() {
        return socketTransmit;
    }

    public void setStationStatus(stationStatus stationStatus) {
        this.StationStatus = stationStatus;
    }

}
