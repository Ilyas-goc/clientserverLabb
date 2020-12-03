import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Random;

public class UDPServer {

    public static void main(String[] args) throws IOException {
        new UDPServer(2000);
    }

    public UDPServer(int port) throws IOException {
        System.out.println("UDP server running at port");
        
        try {
            DatagramSocket ds = new DatagramSocket(port);
            Random rand = new Random();
            int clientPort = 0;
            InetAddress clientAddress = null;
            String helloMessage = "";      
           
            while (true) {
                boolean run = false;
                
                try {
                    byte[]data = new byte[1024];
                    DatagramPacket DpReceive = new DatagramPacket(data,data.length);

                    if (clientAddress == null && clientPort == 0) {
                        ds.receive(DpReceive);
                        clientPort = DpReceive.getPort();
                        clientAddress = DpReceive.getAddress();
                        helloMessage = new String(DpReceive.getData(), 0, DpReceive.getLength());
                    }

                    if (clientAddress != null && clientPort != 0 && helloMessage.equals("hello")) {
                        byte[] enter = new byte[2048];
                        enter = "Welcome".getBytes();
                        ds.send(new DatagramPacket(enter, enter.length, clientAddress, clientPort));
                       
                        System.out.println("A client has connected.");
                        int randomNr = rand.nextInt(100) + 1;
                        System.out.println("Random nr: " + randomNr);
                        long time = System.currentTimeMillis();
                        int addBotheringTime = 0;

                        while (!run) {
                            data = new byte[1024];
                            DatagramPacket dataPacket = new DatagramPacket(data, data.length);
                          
                            try {
                                ds.receive(dataPacket);
                                String exit = new String(dataPacket.getData(), 0, dataPacket.getLength());
                                                                            
                                if(clientPort == dataPacket.getPort() && (System.currentTimeMillis() - time) + addBotheringTime <= 15000){
                                    System.out.println("[Server] client Response: " + new String(dataPacket.getData(),0,dataPacket.getLength()));
                                } 
                                
                                if(exit.toLowerCase().equals("exit")){
                                    System.out.println("[Server] client Response: " + exit);
                                    System.out.println("Client left the game");
                                    byte[] send = new byte[2048];
                                    //ds.send(new DatagramPacket(send, send.length, dataPacket.getAddress(), dataPacket.getPort()));
                                    clientAddress = null;
                                    clientPort = 0;
                                    helloMessage = "hello";
                                    run = true;
                                    break;
                                } 
                                                    
                                if ((System.currentTimeMillis() - time) + addBotheringTime >= 15000) {
                                    System.out.println("[Server] The current client was not active so the client must leave");                                                    
                                    
                                    if (dataPacket.getPort() == clientPort) {
                                        byte[] msg = new byte[2048];
                                        msg = "You must be active".getBytes();
                                        ds.send(new DatagramPacket(msg, msg.length, clientAddress, clientPort));
                                        clientPort = 0;
                                        clientAddress = null;
                                        run = true;
                                        addBotheringTime = 0;
                                        break;
                                    }  
                                    
                                    else {
                                        System.out.println("\n[Server] A new client was writing Hello");
                                        byte[] msg = new byte[2048];
                                        msg = "You must be active".getBytes();
                                        ds.send(new DatagramPacket(msg, msg.length, clientAddress, clientPort));
                                        clientAddress = dataPacket.getAddress();
                                        clientPort = dataPacket.getPort();
                                        helloMessage = "hello";
                                        addBotheringTime = 0;
                                        run = true;
                                        break;
                                    }
                                } 
                                
                                else if (clientPort != dataPacket.getPort()) {
                                    byte[] send = new byte[2048];
                                    addBotheringTime = (int) (addBotheringTime + (System.currentTimeMillis() - time));
                                    send = "BUSY".getBytes();
                                    ds.send(new DatagramPacket(send, "BUSY".length(), dataPacket.getAddress(), dataPacket.getPort()));
                                }
                                   
                                else {
                                    addBotheringTime = 0;
                                }
                                
                                try {
                                    String msg = new String(dataPacket.getData(), 0, dataPacket.getLength());

                                    if (checkNum(msg)) {
                                     int guessValue = Integer.parseInt(msg);
                                       
                                     if (guessValue >= 1 && guessValue <=100) {

                                        if (guessValue == randomNr) {
                                            System.out.println("Client won the game and is leaving the server");
                                            byte[] send = new byte[2048];
                                            send = "CORRECT".getBytes();
                                            ds.send(new DatagramPacket(send, send.length, dataPacket.getAddress(), dataPacket.getPort())); 
                                            clientAddress = null;
                                            clientPort = 0;
                                            helloMessage = "hello";
                                            run = true;
                                            break;
                                        } 
                                            
                                        else if (guessValue > randomNr) {
                                            time = System.currentTimeMillis();
                                            byte[] send = new byte[2048];
                                            send = "HI".getBytes();
                                            ds.send(new DatagramPacket(send, send.length, dataPacket.getAddress(), dataPacket.getPort())); 
                                        }
                                                                                            
                                        else{
                                            time = System.currentTimeMillis();
                                            byte[] send = new byte[2048];
                                            send = "LO".getBytes();
                                            ds.send(new DatagramPacket(send, send.length, dataPacket.getAddress(), dataPacket.getPort()));
                                         }
                                       }
                                     
                                        else {
                                            time = System.currentTimeMillis();
                                            byte[] send = "You must use numbers between 1-100".getBytes();
                                            ds.send(new DatagramPacket(send, send.length, dataPacket.getAddress(), dataPacket.getPort())); 
                                           } 
                                        }
                                    
                                    else{
                                        time = System.currentTimeMillis();
                                        byte[] send = new byte[2048];
                                        send = "Wrong Input".getBytes();
                                        ds.send(new DatagramPacket(send, send.length, dataPacket.getAddress(), dataPacket.getPort())); 
                                    }
                                } 
                                
                                catch (IOException e) {
                                   System.err.print("You got a I/O Error");
                                }
                            } 
                            
                            catch (IOException e) {
                               System.err.print("You got a I/O Error");
                            }
                       }
                    } 
                    
                    else {
                        clientAddress = null;
                        clientPort = 0;
                    }
                } 
                
                catch (IOException ex) {
                   System.err.print("You got a I/O Error");
                }
            }
        } 
        
        catch (SocketException ex) {
            System.out.println("You got a Socket Error");
        }
    }
    
    private static boolean checkNum(String value) {
        try {
            Integer.parseInt(value);
            
            return true;
        }
        
        catch (NumberFormatException e) {
            return false;
        }
    }
}


