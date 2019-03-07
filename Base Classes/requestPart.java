package Assign1;

import java.io.Serializable;

public class requestPart implements Serializable {
    Station station;

    public requestPart(Station station) {
        this.station = station;
    }

    public Station getStation() {
        return station;
    }
}
