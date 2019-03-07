package Assign1;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Inventory implements Serializable {
    Map<Part, Integer> inventory;

    public Inventory(Map<Part, Integer> inventory) {
        this.inventory = inventory;
    }

    public Map<Part, Integer> getInventory() {
        return inventory;
    }

    public void setInventory(Map<Part, Integer> inventory) {
        this.inventory = inventory;
    }
}
