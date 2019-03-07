import Assign1.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ManagerMain {
    static ArrayList<ArrayList<Integer>> portPhoneBook = new ArrayList<>();
    static ArrayList<Socket> stationSendSockets = new ArrayList<>();
    static ArrayList<Socket> stationReceiveSockets = new ArrayList<>();
    static ArrayList<Part> inventoryRegistry = new ArrayList<>();
    static Map<Part, Integer> inventory = new HashMap<>();
    static ArrayList<Station> assemblyLine = new ArrayList<>();
    public static void main(String args[]){
        setupPhoneBook();
        readPartsFromCSV("inventory.csv");
        assemblyLine = createAssemblyLine(inventoryRegistry);

        ServerSocket serverSocket;
        try {
             serverSocket = new ServerSocket(27000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i=0; i<assemblyLine.size(); i++){
            try {
                Process proc = Runtime.getRuntime().exec("java -jar Station.jar");
                class clientThread implements Runnable{
                    Station station;
                    clientThread(Station station){this.station = station;}
                    @Override
                    public void run() {
                        try {
                            Socket socket = new ServerSocket().accept();
                            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

                            Message message = (Message) objectInputStream.readObject();
                            if(message.getStationStatusUpdate().equals(stationStatus.OFF)){
                                objectOutputStream.writeObject(station);
                                while(true){
                                    Message order = (Message) objectInputStream.readObject();
                                    if(order.getMessageType().equals(MessageType.PART_REQUEST)){

                                    }else if(order.getMessageType().equals(MessageType.STATUS_UPDATE)){
                                        // TO DO
                                    }
                                }
                            }
                        }catch (IOException | ClassNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
                Thread thread = new Thread(new clientThread(assemblyLine.get(i)));
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static ArrayList<Station> createAssemblyLine(ArrayList<Part> partRegistry){

        Map<Part, Integer> chassisPartList = new HashMap();
        chassisPartList.put(partRegistry.get(4), 20);
        chassisPartList.put(partRegistry.get(0), 4);
        chassisPartList.put(partRegistry.get(1), 4);
        Inventory chassisInventory = new Inventory(chassisPartList);

        Map<Part, Integer> drivetrainPartList = new HashMap();
        drivetrainPartList.put(partRegistry.get(1), 7);
        drivetrainPartList.put(partRegistry.get(9), 1);
        drivetrainPartList.put(partRegistry.get(10), 2);
        Inventory drivetrainInventory = new Inventory(drivetrainPartList);

        Map<Part, Integer> bodyPartList = new HashMap();
        bodyPartList.put(partRegistry.get(2), 2);
        bodyPartList.put(partRegistry.get(3), 4);
        bodyPartList.put(partRegistry.get(4), 10);
        Inventory bodyInventory = new Inventory(bodyPartList);

        Map<Part, Integer> wheelPartList = new HashMap();
        wheelPartList.put(partRegistry.get(6), 4);
        wheelPartList.put(partRegistry.get(5), 4);
        wheelPartList.put(partRegistry.get(1), 24);
        Inventory wheelInventory = new Inventory(wheelPartList);

        Map<Part, Integer> collisionSensorPartList = new HashMap();
        collisionSensorPartList.put(partRegistry.get(9), 1);
        collisionSensorPartList.put(partRegistry.get(10), 1);
        Inventory collisionSensorInventory = new Inventory(collisionSensorPartList);

        Map<Part, Integer> paintPartList = new HashMap();
        paintPartList.put(partRegistry.get(7), 2);
        Inventory paintInventory = new Inventory(paintPartList);

        Map<Part, Integer> leatherSeatPartList = new HashMap();
        leatherSeatPartList.put(partRegistry.get(8), 4);
        leatherSeatPartList.put(partRegistry.get(2), 2);
        Inventory leatherSeatInventory = new Inventory(leatherSeatPartList);

        Map<Part, Integer> sportPartList = new HashMap();
        sportPartList.put(partRegistry.get(7), 4);
        sportPartList.put(partRegistry.get(0), 1);
        sportPartList.put(partRegistry.get(4), 7);
        Inventory sportInventory = new Inventory(sportPartList);

        Map<Part, Integer> VINPartList = new HashMap();
        VINPartList.put(partRegistry.get(10), 2);
        Inventory VINInventory = new Inventory(VINPartList);

        Task chassis = new Task(1,true, taskType.INITIAL,5000, chassisInventory,"Installing chassis");
        Task drivetrain = new Task(2, true, taskType.INTERMEDIATE, 7000, drivetrainInventory, "Installing Drivetrain");
        Task body = new Task(3, true, taskType.INTERMEDIATE, 2500, bodyInventory, "Installing Body");
        Task wheels = new Task(4, true, taskType.INTERMEDIATE, 5000, wheelInventory, "Installing wheels and brakes");
        Task collision = new Task(5, false, taskType.INTERMEDIATE, 2000, collisionSensorInventory, "Installing Collision Sensors (Optional)");
        Task paint = new Task(6, true, taskType.INTERMEDIATE, 5000, paintInventory, "Painting car");
        Task leatherSeat = new Task(7, false, taskType.INTERMEDIATE, 6000, leatherSeatInventory, "Installing Leather seats");
        Task sportPackage = new Task(8, false, taskType.INTERMEDIATE, 10000, sportInventory, "Installing Sport Package");
        Task VIN = new Task(9, true, taskType.FINAL,5000, VINInventory, "Engraving VIN");

        ArrayList<Station> allStations = new ArrayList<>();

        Station chassisStation = new Station(1, chassis, stationReceiveSockets.get(0), stationSendSockets.get(0));
        Station drivetrainStation = new Station(2, drivetrain, stationReceiveSockets.get(1), stationSendSockets.get(1));
        Station bodyStation = new Station(3,body, stationReceiveSockets.get(2), stationSendSockets.get(2));
        Station wheelStation1 = new Station(4,wheels, stationReceiveSockets.get(3), stationSendSockets.get(3));
        Station wheelStation2 = new Station(5,wheels, stationReceiveSockets.get(4), stationSendSockets.get(4));
        Station collisionStation = new Station(6,collision, stationReceiveSockets.get(5), stationSendSockets.get(5));
        Station paintStation = new Station(7, paint, stationReceiveSockets.get(6), stationSendSockets.get(6));
        Station leatherSeatStation = new Station(8, leatherSeat, stationReceiveSockets.get(7), stationSendSockets.get(7));
        Station sportPackageStation = new Station(9, sportPackage, stationReceiveSockets.get(8), stationSendSockets.get(8));
        Station VINStation = new Station(10,VIN, stationReceiveSockets.get(9), stationSendSockets.get(9));

        allStations.add(chassisStation);
        allStations.add(drivetrainStation);
        allStations.add(bodyStation);
        allStations.add(wheelStation1);
        allStations.add(wheelStation2);
        allStations.add(collisionStation);
        allStations.add(paintStation);
        allStations.add(leatherSeatStation);
        allStations.add(sportPackageStation);
        allStations.add(VINStation);

        return allStations;
    }

    public static void setupPhoneBook(){
        ArrayList<Integer> station1 = new ArrayList<>();
        ArrayList<Integer> station2 = new ArrayList<>();
        ArrayList<Integer> station3 = new ArrayList<>();
        ArrayList<Integer> station4 = new ArrayList<>();
        ArrayList<Integer> station5 = new ArrayList<>();
        ArrayList<Integer> station6 = new ArrayList<>();
        ArrayList<Integer> station7 = new ArrayList<>();
        ArrayList<Integer> station8 = new ArrayList<>();
        ArrayList<Integer> station9 = new ArrayList<>();
        ArrayList<Integer> station10 = new ArrayList<>();

        station1.add(27001);
        station1.add(27002);
        station2.add(27002);
        station2.add(27003);
        station3.add(27003);
        station3.add(27004);
        station4.add(27004);
        station4.add(27005);
        station5.add(27005);
        station5.add(27006);
        station6.add(27006);
        station6.add(27007);
        station7.add(27007);
        station7.add(27008);
        station8.add(27008);
        station8.add(27009);
        station9.add(27009);
        station9.add(27010);
        station10.add(27010);
        station10.add(27001);

        portPhoneBook.add(station1);
        portPhoneBook.add(station2);
        portPhoneBook.add(station3);
        portPhoneBook.add(station4);
        portPhoneBook.add(station5);
        portPhoneBook.add(station6);
        portPhoneBook.add(station7);
        portPhoneBook.add(station8);
        portPhoneBook.add(station9);
        portPhoneBook.add(station10);

        for(int i=0; i<portPhoneBook.size(); i++){
            try {
                stationReceiveSockets.add(new Socket("localHost",portPhoneBook.get(i).get(0)));
                stationSendSockets.add(new Socket("localHost",portPhoneBook.get(i).get(1)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void readPartsFromCSV(String fileName) {
        ArrayList<String> partNames = new ArrayList<>();
        ArrayList<Integer> partQuantities = new ArrayList<>();
        File file= new File(fileName);
        Scanner inputStream;
        int count = 0;
        try{
            inputStream = new Scanner(file);
            while(inputStream.hasNext()){
                String line= inputStream.next();
                String[] values = line.split(",");
                if(count == 0){
                    for(int i=0; i<values.length; i++){
                        partNames.add(values[i]);
                    }
                }else{
                    for(int i=0; i<values.length; i++){
                        partQuantities.add(Integer.parseInt(values[i]));
                    }
                }
                count++;
            }
            inputStream.close();

            for(int i = 0; i < partNames.size(); i++){
                inventoryRegistry.add(new Part(i+1, partNames.get(i)));
            }

            for(int i=0; i<inventoryRegistry.size(); i++){
                inventory.put(inventoryRegistry.get(i),partQuantities.get(i));
            }
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
