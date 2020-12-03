
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class TCPClient {
    
    public static void main(String[] args) throws IOException {
        System.out.println("TCP Client");    
        boolean run = true;
 
        try {
              Socket socket = new Socket("localhost", 2000);
            
            while (run) {                
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                Scanner scan = new Scanner(System.in);
                System.out.print("Client: ");

                String send = scan.next();
                out.println(send);
                 
                if(send.equals("bye")){
                    run = false;
                }
                
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String receive = input.readLine();
                      
                if(receive.equals("CORRECT") || receive.equals("bye") || receive.equals("Took to long time")){
                    run = false;
                }
                
                System.out.println("Server: Response: " + receive);
            }
        } 
        
        catch (SocketException ex) {
            System.out.println("BUSY");
        } 
        
        catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
  }
    
    
    
