import java.io.Serializable;

public class responsePart implements Serializable {
    Inventory inventory;
    int stationID;

    public responsePart(int stationID, Inventory inventory) {
        this.stationID = stationID;
        this.inventory = inventory;
    }

    public int getStationID() {
        return stationID;
    }

    public Inventory getBody() {
        return inventory;
    }
}
