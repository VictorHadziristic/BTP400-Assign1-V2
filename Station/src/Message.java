import java.io.Serializable;

public class Message implements Serializable {
    MessageType messageType;

    Job job = null;

    requestPart requestPart = null;
    responsePart responsePart = null;

    stationStatusUpdate stationStatusUpdate = null;

    public Message(requestPart requestPart){
        this.messageType = MessageType.PART_REQUEST;
        this.requestPart = requestPart;
    }

    public Message(Job job){
        this.messageType = MessageType.JOB;
        this.job = job;
    }

    public Message(responsePart responsePart){
        this.messageType = MessageType.PART_RESPONSE;
        this.responsePart = responsePart;
    }

    public Message(stationStatusUpdate stationStatusUpdate){
        this.messageType = MessageType.STATUS_UPDATE;
        this.stationStatusUpdate = stationStatusUpdate;
    }

    public Job getJob() { return job; }

    public MessageType getMessageType() {
        return messageType;
    }

    public requestPart getRequestPart() {
        return requestPart;
    }

    public responsePart getResponsePart() {
        return responsePart;
    }

    public stationStatusUpdate getStationStatusUpdate() {
        return stationStatusUpdate;
    }
}
