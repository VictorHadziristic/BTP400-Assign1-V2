import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ManagerMain {
    static ArrayList<ArrayList<Integer>> portPhoneBook = new ArrayList<>();
    static ArrayList<Socket> stationToManagerSockets = new ArrayList<>();
    static ArrayList<Part> partRegistry = new ArrayList<>();
    static Map<Part, Integer> inventory = new HashMap<>();
    static ArrayList<Station> assemblyLine = new ArrayList<>();
    static ArrayList<Job> jobs = new ArrayList<>();
    static ArrayList<Job> completedJobs = new ArrayList<>();
    static ArrayList<Job> failedJobs = new ArrayList<>();
    static ArrayList<stationStatusUpdate> statusUpdates = new ArrayList<>();

    static ServerSocket managerSocket = null;
    static ServerSocket incomingServer = null;
    static Socket incoming = null;
    static Socket outgoing = null;
    public static void main(String args[]){
        try {
            managerSocket = new ServerSocket(27000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setupPhoneBook();
        readPartsFromCSV("C:\\Users\\acdc2\\Documents\\_School\\BTP400\\Assignment 1 V5\\Manager\\inventory.csv");
        assemblyLine = createAssemblyLine(partRegistry);
        jobs = makeJobs(partRegistry);
        for(int i = 0; i < assemblyLine.size(); i++)
        {
            Socket sock = stationToManagerSockets.get(i);
            Station station = assemblyLine.get(i);

            Runtime r = Runtime.getRuntime();
            Process p = null;
            try {
                p = r.exec(new String[] { "cmd", "/c", "start C:\\Users\\acdc2\\Documents\\_School\\BTP400\\Assignment 1 V5\\Manager\\Station.jar" });
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            try{
                sock = managerSocket.accept();
            } catch (IOException e2){
                e2.printStackTrace();
            }

            // listening for status
            try (ObjectInputStream objectInputStream = new ObjectInputStream(sock.getInputStream());
                 ObjectOutputStream objectOutputStream = new ObjectOutputStream(sock.getOutputStream())){
                Message message = (Message) objectInputStream.readObject();
                if(message.getMessageType() == MessageType.STATUS_UPDATE){
                    objectOutputStream.writeObject(station);
                }
            }catch (IOException | ClassNotFoundException e){
                System.out.println("Exception caught when trying to listen on port "
                        +  " or listening for a connection");
                System.out.println(e.getMessage());
            }
        }
        // Now that the stations have spawned we now send out the jobs to the assembly line
        ObjectOutputStream objectOutputStream = null;
        ObjectInputStream objectInputStream = null;
        try{
            incoming = incomingServer.accept();
            outgoing = new Socket("localhost",portPhoneBook.get(0).get(0));

            objectOutputStream = new ObjectOutputStream(outgoing.getOutputStream());
            objectInputStream = new ObjectInputStream(incoming.getInputStream());
        }catch(IOException e){
            e.printStackTrace();
        }

        for(int i=0; i < jobs.size(); i++){
            try{
                objectOutputStream.writeObject(jobs.get(i));
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        // the manager will now waiting for incoming messages

        while(true){
            try {
                Message message = (Message) objectInputStream.readObject();
                if(message != null){
                    if(message.getMessageType().equals(MessageType.PART_REQUEST)){
                        Task task = message.getRequestPart().getTask();
                        task.getTaskParts().createStationRestock();
                        Message partResponse = new Message(new responsePart(message.getRequestPart().getStationID(),task.getTaskParts()));
                        objectOutputStream.writeObject(partResponse);
                    }else if(message.getMessageType().equals(MessageType.PART_RESPONSE)){
                        objectOutputStream.writeObject(message);
                    }
                    else if(message.getMessageType().equals(MessageType.JOB)){
                        message.getJob().incrementJobTries();
                        if(message.getJob().getAssemblyTasks().size() == 0){
                            completedJobs.add(message.getJob());
                        }else{
                            if(message.getJob().getJobTries() > 50){
                                failedJobs.add(message.getJob());
                            }else{
                                objectOutputStream.writeObject(message);
                            }
                        }
                    }
                    else if(message.getMessageType().equals(MessageType.STATUS_UPDATE)){
                        statusUpdates.add(message.getStationStatusUpdate());
                        System.out.println(message.getStationStatusUpdate().statusToString());
                    }
                }
            }catch (IOException | ClassNotFoundException e){
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

        Station chassisStation = new Station(1, chassis, portPhoneBook.get(0).get(0), portPhoneBook.get(0).get(1));
        Station drivetrainStation = new Station(2, drivetrain, portPhoneBook.get(1).get(0), portPhoneBook.get(1).get(1));
        Station bodyStation = new Station(3,body, portPhoneBook.get(2).get(0), portPhoneBook.get(2).get(1));
        Station wheelStation1 = new Station(4,wheels, portPhoneBook.get(3).get(0), portPhoneBook.get(3).get(1));
        Station wheelStation2 = new Station(5,wheels, portPhoneBook.get(4).get(0), portPhoneBook.get(4).get(1));
        Station collisionStation = new Station(6,collision, portPhoneBook.get(5).get(0), portPhoneBook.get(5).get(1));
        Station paintStation = new Station(7, paint, portPhoneBook.get(6).get(0), portPhoneBook.get(6).get(1));
        Station leatherSeatStation = new Station(8, leatherSeat, portPhoneBook.get(7).get(0), portPhoneBook.get(7).get(1));
        Station sportPackageStation = new Station(9, sportPackage, portPhoneBook.get(8).get(0), portPhoneBook.get(8).get(1));
        Station VINStation = new Station(10,VIN, portPhoneBook.get(9).get(0), portPhoneBook.get(9).get(1));

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
        station10.add(28000);

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
                Socket temp = new Socket("localhost",27000);
                temp = managerSocket.accept();
                stationToManagerSockets.add(temp);
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
                partRegistry.add(new Part(i+1, partNames.get(i)));
            }

            for(int i = 0; i< partRegistry.size(); i++){
                inventory.put(partRegistry.get(i),partQuantities.get(i));
            }
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Job> makeJobs(ArrayList<Part> partRegistry)
    {
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

        ArrayList<Job> jobList = new ArrayList<>();

        ArrayList<Task> tasks = new ArrayList<>();
        ArrayList<Task> tasks2 = new ArrayList<>();
        ArrayList<Task> tasks3 = new ArrayList<>();

        tasks.add(chassis);
        tasks.add(drivetrain);
        tasks.add(body);
        tasks.add(wheels);
        tasks.add(collision);
        tasks.add(paint);
        tasks.add(leatherSeat);
        tasks.add(sportPackage);
        tasks.add(VIN);

        tasks2.add(chassis);
        tasks2.add(drivetrain);
        tasks2.add(body);
        tasks2.add(wheels);
        tasks2.add(collision);
        tasks2.add(paint);
        tasks2.add(VIN);


        tasks3.add(chassis);
        tasks3.add(drivetrain);
        tasks3.add(body);
        tasks3.add(wheels);
        tasks3.add(paint);
        tasks3.add(leatherSeat);
        tasks3.add(VIN);

        jobList.add(new Job(0, tasks));
        jobList.add(new Job(1, tasks2));
        jobList.add(new Job(1, tasks3));

        return jobList;
    }
}
