package Assign1;

import java.io.Serializable;
import java.net.Socket;

public class Station implements Serializable {
    private int id;
    private stationStatus stationStatus = Assign1.stationStatus.OFF;
    private Task task = null;
    private Job currentJob = null;
    private Socket socketReceive = null;
    private Socket socketTransmit = null;

    public Station(int id, Task task, Socket socketReceive, Socket socketTransmit) {
        this.id = id;
        this.task = task;
        this.socketReceive = socketReceive;
        this.socketTransmit = socketTransmit;
        this.stationStatus = Assign1.stationStatus.WAITING;;
    }

    public int getId() {
        return id;
    }

    public Assign1.stationStatus getStationStatus() {
        return stationStatus;
    }

    public Task getTask() {
        return task;
    }

    public Job getCurrentJob() {
        return currentJob;
    }

    public void setCurrentJob(Job currentJob) {
        this.currentJob = currentJob;
    }

    public Socket getSocketReceive() {
        return socketReceive;
    }

    public Socket getSocketTransmit() {
        return socketTransmit;
    }

    public void setStationStatus(Assign1.stationStatus stationStatus) {
        this.stationStatus = stationStatus;
    }
}
