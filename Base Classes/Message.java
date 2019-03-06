package Assign1;

import java.io.Serializable;

public class Message implements Serializable {
    MessageType messageType;

    requestPart requestPart = null;
    responsePart responsePart = null;

    stationStatusUpdate stationStatusUpdate = null;

    public Message(requestPart requestPart){
        this.messageType = MessageType.PART_REQUEST;
        this.requestPart = requestPart;
    }

    public Message(responsePart responsePart){
        this.messageType = MessageType.PART_RESPONSE;
        this.responsePart = responsePart;
    }

    public Message(stationStatusUpdate stationStatusUpdate){
        this.messageType = MessageType.STATUS_UPDATE;
        this.stationStatusUpdate = stationStatusUpdate;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public Assign1.requestPart getRequestPart() {
        return requestPart;
    }

    public Assign1.responsePart getResponsePart() {
        return responsePart;
    }

    public Assign1.stationStatusUpdate getStationStatusUpdate() {
        return stationStatusUpdate;
    }
}
