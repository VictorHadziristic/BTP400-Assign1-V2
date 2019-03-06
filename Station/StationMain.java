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

           managerSocket = new Socket("localhost", 27000);
           ObjectOutputStream managerOutput = new ObjectOutputStream(managerSocket.getOutputStream());
           ObjectInputStream managerInput = new ObjectInputStream(managerSocket.getInputStream());

           managerOutput.writeObject(new stationStatusUpdate());
           station = (Station) managerInput.readObject();

           ObjectInputStream objectInputStream = new ObjectInputStream(station.getSocketReceive().getInputStream());
           ObjectOutputStream objectOutputStream = new ObjectOutputStream(station.getSocketTransmit().getOutputStream());

           station.setStationStatus(stationStatus.WAITING);
           managerOutput.writeObject(new stationStatusUpdate(station.getId(),station.getStationStatus(),station.getCurrentJob().getId(),station.getTask()));

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
           while(true){
               if(!hasJob){
                   station.setCurrentJob((Job)objectInputStream.readObject());
                   hasJob = true;
                   station.setStationStatus(stationStatus.HALTED);
                   Message partRequest = new Message(new requestPart(station.getTask().getId(),station.getId()));
                   managerOutput.writeObject(partRequest);
                   Message partResponse = (Message) managerInput.readObject();
                   if(partResponse.getResponsePart().getOrderStatus()){
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
