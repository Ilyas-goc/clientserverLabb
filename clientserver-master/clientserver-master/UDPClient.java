
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class UDPClient {

    private static boolean run;

    public static void main(String[] args) {
        System.out.println("UDP client");
        DatagramSocket ds = null;
        String msg = "";
        byte[] send;
        byte[] receive;
        InetAddress ip = null;
        boolean run = true;
        boolean status = true;

        Scanner scan = new Scanner(System.in);

        try {
                System.out.println("Write 'Hello' to access or Exit too leave ");
                System.out.print("Client: ");
                msg = scan.next().toLowerCase();
                
            while (msg.compareTo("hello") != 0) {
                if (msg.equals("exit")) {
                    System.out.println("You left the game");
                    status = false;
                    return;  
                }
                
                else{
                    System.out.println("Write 'Hello' to access or Exit too leave ");
                    System.out.print("Client: ");
                    msg = scan.next().toLowerCase();
                }

            }
                        
            ds = new DatagramSocket();
            ip = InetAddress.getByName("127.0.0.1");

            send = new byte[1024];
            receive = new byte[1024];

            send = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(send, send.length, ip, 2000);
            ds.send(packet);

            packet = new DatagramPacket(receive, receive.length);
            ds.receive(packet);
            msg = new String(packet.getData(), 0, packet.getLength());

            System.out.println("Server response: " + "" + msg);

            if (msg.equals("BUSY") || msg.equals("You must be active")|| msg.equals("exit")) {
                status = false;
                ds.close();
                //msg = "CORRECT";
                run = false;
            }
            
            if(status == true){
                 System.out.println("If you want too leave write 'Exit'\n");
            }

            while (run) {
                send = new byte[2048];
                receive = new byte[2048];

                System.out.print("Client: ");
                send = scan.next().getBytes();
                
                packet = new DatagramPacket(send, send.length, ip, 2000);
                ds.send(packet);
                
                String exit = new String(packet.getData(),0,packet.getLength());
                
                if(exit.toLowerCase().equals("exit") ){
                    System.out.println("You left the game");
                    run = false;
                    ds.close();
                    break;                    
                }

                packet = new DatagramPacket(receive, receive.length);
                ds.receive(packet);

                msg = new String(packet.getData(), 0, packet.getLength());

                System.out.println("Server response: " + "" + msg);
                
                if (msg.equals("BUSY") || msg.equals("CORRECT") || msg.equals("You must be active")) {
                    run = false;
                    ds.close();
                    break;
                }

                receive = new byte[2048];
            }
        } catch (IOException ex) {
            System.err.print("You got I/O Error");
        } finally {
            if (ds != null) {
                ds.close();
            }

        }
    }
}
