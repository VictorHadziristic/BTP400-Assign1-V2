import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class StationMain {

    static boolean hasJob = true; //True for testing, False for production
    static boolean hasParts = true;
    static Job currentJob = null;
    static ServerSocket serverSocket = null;
    static Socket receiveSocket = null;
    static Socket transmitSocket = null;
    static Station station = null;
    static Inventory inventory = null;

    static ObjectInputStream objectInputStream = null;
    static ObjectOutputStream objectOutputStream = null;

    public static void main(String args[]) {

        // Get Station from the Manager
        try (Socket socketToManager = new Socket("localhost", 27000);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socketToManager.getOutputStream());
             ObjectInputStream objectInputStream = new ObjectInputStream(socketToManager.getInputStream());
        ){
           Message message = new Message(new stationStatusUpdate());
           objectOutputStream.writeObject(message);
           station = (Station) objectInputStream.readObject();
        } catch (UnknownHostException e) {
            System.out.println("Station Shut Down: Unknown Host");
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Station Shut Down: IO Exception");
            System.exit(1);
        } catch (ClassNotFoundException e) {
            System.out.println("Station Shut Down: Station object not found");
            System.exit(1);
        }

        boolean connectedToNextStation = false;
        //Setting up connection
        try {
            // Listens for previous station and creates client socket for communication
            serverSocket = new ServerSocket(station.getSocketReceive());
            receiveSocket = serverSocket.accept();
            while (!connectedToNextStation) {
                try {
                    transmitSocket = new Socket("localhost", station.getSocketTransmit());
                    if (transmitSocket.isConnected()) {
                        connectedToNextStation = true;
                    }
                } catch (ConnectException e) {
                    //Do nothing, we want the transmit socket to try again until it connects to next node
                } finally {
                    objectInputStream = new ObjectInputStream(receiveSocket.getInputStream());
                    objectOutputStream = new ObjectOutputStream(transmitSocket.getOutputStream());
                }
            }
        } catch (IOException e) {
            System.out.println("Station Could not connect to nearby Stations");
            e.printStackTrace();
        }

        // Bus thread
        Thread busThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        try {
                            Message message = (Message) objectInputStream.readObject();
                            if (message.getMessageType().equals(MessageType.STATUS_UPDATE) || message.getMessageType().equals(MessageType.PART_REQUEST)) {
                                objectOutputStream.writeObject(message);
                            } else if (message.getMessageType().equals(MessageType.PART_RESPONSE)) {
                                if (message.getResponsePart().getStationID() == station.getId()) {
                                    //While loop with if, to prevent race condition
                                    boolean state = true;
                                    while (state) {
                                        if (!hasParts) {
                                            inventory.restockInventory(message.getResponsePart().getBody());
                                            hasParts = true;
                                            state = false;
                                        }
                                    }
                                } else {
                                    objectOutputStream.writeObject(message);
                                }
                            } else if (message.getMessageType().equals(MessageType.JOB)) {
                                if (hasJob) {
                                    objectOutputStream.writeObject(message);
                                } else {
                                    if(message.getJob().getCurrentTask().getId() == station.getTask().getId()){
                                        currentJob = message.getJob();
                                    }else{
                                        objectOutputStream.writeObject(message);
                                    }
                                }
                            }
                        } catch (EOFException e) {
                            // If nothing in stream, keep trying
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Error getting inputStream");
                }
            }
        });
        busThread.start();

        stationStatusUpdate stationStatusUpdate = new stationStatusUpdate(station.getId(),stationStatus.WAITING,currentJob.getId(),station.getTask());
        Message status = new Message(stationStatusUpdate);
        postStatus(status);

        //Main Thread
        while (true) {
            if (hasJob) {
                boolean hasCompletedJob = false;
                while (!hasCompletedJob) {
                    if (hasParts) {
                        if (inventory.consumeParts(station.getTask())) {
                            try {
                                // Post status that station is currently working
                                stationStatusUpdate = new stationStatusUpdate(station.getId(),stationStatus.WORKING,currentJob.getId(),station.getTask());
                                status = new Message(stationStatusUpdate);
                                postStatus(status);

                                Thread.sleep(station.getTask().getTaskDuration());
                                currentJob.completeTask();
                                hasCompletedJob = true;

                                // Post status that station has is waiting since it has successfully completed the job
                                stationStatusUpdate = new stationStatusUpdate(station.getId(),stationStatus.WAITING,currentJob.getId(),station.getTask());
                                status = new Message(stationStatusUpdate);
                                postStatus(status);

                                Message message = new Message(currentJob);
                                try {
                                    objectOutputStream.writeObject(message);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            hasParts = false;
                            // Post status that station is halted form lack of parts
                            stationStatusUpdate = new stationStatusUpdate(station.getId(),stationStatus.HALTED,currentJob.getId(),station.getTask());
                            status = new Message(stationStatusUpdate);
                            postStatus(status);

                            requestPart requestPart = new requestPart(station.getId(), station.getTask());
                            Message message = new Message(requestPart);
                            try {
                                objectOutputStream.writeObject(message);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        /*
        public static Inventory testInventory () {
            Map<Part, Integer> chassisInventory = new HashMap();
            chassisInventory.put(new Part(1, "Screw"), 20);
            chassisInventory.put(new Part(2, "Bolt"), 20);
            chassisInventory.put(new Part(3, "Chassis Base"), 20);
            return new Inventory(chassisInventory);
        }

        public static Job testJob ( int i){
            Map<Part, Integer> chassisPartList = new HashMap();
            chassisPartList.put(new Part(1, "Screw"), 5);
            chassisPartList.put(new Part(2, "Bolt"), 4);
            chassisPartList.put(new Part(3, "Chassis Base"), 4);
            Inventory chassisInventory = new Inventory(chassisPartList);

            ArrayList<Task> tasks = new ArrayList<Task>();
            Task chassis0 = new Task(1, true, taskType.INITIAL, 5000, chassisInventory, "Installing chassis");
            tasks.add(chassis0);
            return new Job(i, tasks);
        }

        public static Station testStation () {
            // Initializing a station object, as the manager would normally provide through the manager socket
            Map<Part, Integer> chassisPartList = new HashMap();
            chassisPartList.put(new Part(1, "Screw"), 20);
            chassisPartList.put(new Part(2, "Bolt"), 4);
            chassisPartList.put(new Part(3, "Chassis Base"), 4);
            Inventory chassisInventory = new Inventory(chassisPartList);
            Task chassis = new Task(1, true, taskType.INITIAL, 5000, chassisInventory, "Installing chassis");
            return new Station(1, chassis, 27001, 27002);
        }
        */
    }

    public static boolean postStatus(Message message){
        boolean state = true;
        try {
            objectOutputStream.writeObject(message);
        } catch (IOException e) {
            state = false;
        }
        return state;
    }
}
