package Assign1;

import java.io.Serializable;

public class responsePart implements Serializable {
    boolean orderStatus;

    public responsePart(boolean orderStatus) {
        this.orderStatus = orderStatus;
    }

    public boolean getOrderStatus() {
        return orderStatus;
    }
}
