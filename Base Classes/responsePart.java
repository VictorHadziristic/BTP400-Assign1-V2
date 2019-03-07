package Assign1;

import java.io.Serializable;

public class responsePart implements Serializable {
    Inventory inventory;

    public responsePart(Inventory inventory) {
        this.inventory = inventory;
    }

    public Inventory getBody() {
        return inventory;
    }
}
