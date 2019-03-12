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

    public boolean consumeParts(Task task){
        boolean state = true;
        for(Part entry : task.taskParts.inventory.keySet()) {
            for(Part entry1 : this.getInventory().keySet()) {
                if(entry.id == entry1.id){
                    if(this.getInventory().get(entry1) < task.taskParts.getInventory().get(entry)){
                        state = false;
                    }
                }
            }
        }
        if(state){
            for(Part entry : task.taskParts.inventory.keySet()) {
                for(Part entry1 : this.getInventory().keySet()) {
                    if(entry.id == entry1.id){
                        int currentQuantity = this.inventory.get(entry1);
                        currentQuantity -= task.taskParts.inventory.get(entry);
                        this.getInventory().put(entry1,currentQuantity);
                    }
                }
            }
        }
        return state;
    }

    public void createStationRestock(){
        for(Part entry : this.inventory.keySet()) {
            this.inventory.put(entry, 100);
        }
    }

    public void restockInventory(Inventory incoming){
        for(Part entry : incoming.inventory.keySet()) {
            for(Part entry1 : this.getInventory().keySet()) {
                if(entry.id == entry1.id){
                    int currentQuantity = this.inventory.get(entry1);
                    currentQuantity += incoming.inventory.get(entry);
                    this.getInventory().put(entry1,currentQuantity);
                }
            }
        }
    }
}
