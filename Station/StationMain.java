package Assign1Station;

import Assign1.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class StationMain {
    static boolean hasJob = false;
    public static void main(String args[]){
        try {
           Station station;
           Socket managerSocket;
           // Initialize manager socket to manager application
           managerSocket = new Socket("localhost", 27000);
           ObjectOutputStream managerOutput = new ObjectOutputStream(managerSocket.getOutputStream());
           ObjectInputStream managerInput = new ObjectInputStream(managerSocket.getInputStream());
           // Send startup status to manager
           managerOutput.writeObject(new stationStatusUpdate());
           // Receive station object from manager
           station = (Station) managerInput.readObject();

           // Setup transmit and receive sockets
           ObjectInputStream objectInputStream = new ObjectInputStream(station.getSocketReceive().getInputStream());
           ObjectOutputStream objectOutputStream = new ObjectOutputStream(station.getSocketTransmit().getOutputStream());

           // Now that the station is setup, the status of the station is changed, and a status report is sent to
           station.setStationStatus(stationStatus.WAITING);
           managerOutput.writeObject(new stationStatusUpdate(station.getId(),station.getStationStatus(),station.getCurrentJob().getId(),station.getTask()));

           // Setting up a thread to receive and pass along jobs, while the station works on a job
           Thread receivingThread = new Thread(new Runnable() {
               @Override
               public void run() {
                   while(true){
                       if(hasJob){
                           try {
                               objectOutputStream.writeObject(objectInputStream.readObject());
                           } catch (IOException | ClassNotFoundException e) {
                               e.printStackTrace();
                           }
                       }
                   }
               }
           });
           receivingThread.start();
           // While the main thread is
           while(true){
               if(!hasJob){
                   station.setCurrentJob((Job)objectInputStream.readObject());
                   if(station.getCurrentJob().getCurrentTask().equals(station.getTask())){
                       hasJob = true;
                       station.setStationStatus(stationStatus.HALTED);
                       Message partRequest = new Message(new requestPart(station));
                       managerOutput.writeObject(partRequest);
                       Message partResponse = (Message) managerInput.readObject();
                       if(partResponse.getResponsePart().getBody().getInventory().size() == 0){
                           station.setStationStatus(stationStatus.WORKING);
                           managerOutput.writeObject(new stationStatusUpdate(station.getId(),station.getStationStatus(),station.getCurrentJob().getId(),station.getTask()));
                           Thread.sleep(station.getTask().getTaskDuration());
                           station.getCurrentJob().completeTask();
                           station.setStationStatus(stationStatus.WAITING);
                           managerOutput.writeObject(new stationStatusUpdate(station.getId(),station.getStationStatus(),station.getCurrentJob().getId(),station.getTask()));
                           objectOutputStream.writeObject(station.getCurrentJob());
                           station.setCurrentJob(null);
                       }
                       hasJob = false;
                   }else{
                       objectOutputStream.writeObject(station.getCurrentJob());
                       station.setCurrentJob(null);
                   }
               }
           }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
