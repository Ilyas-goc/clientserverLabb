package client;

import java.io.*;
import java.net.*;
import java.util.Random;

public class Server {

    public static void main(String[] args) {

        int port = 8000; //Skriv in en port
        
        while (true) {  //Oändlig loop
            connected(port);
            System.out.println("Socket open for new Client");
        }
    }
    
    public static void connected(int port) {
        boolean n = true;
        
        try (ServerSocket serverSocket = new ServerSocket(port)) {   //Öppnar en serversocket för klienten att hoppa in

            System.out.println("Server is listening on port " + port);
            Random rand = new Random();
            int randomnumber = rand.nextInt(100) + 1 ;

            Socket socket = serverSocket.accept();                  //Här på accept väntar servern på att en klient joinar           
            serverSocket.close();                                  //close anropet ser till att ingen annan kan joina
          
            System.out.println("Ansluten..., Send over " + randomnumber + " To: " + socket);

            while (n) {                                          //loopar här tills klienten får rätt svar
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);

                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                try {
                    String msg = reader.readLine();           //Reader.readLine väntar på att en anrop från klienten. EFter den fått anropet fortsätter den
                    System.out.println("Recieved: " + msg);
                    
                    if (msg.equals("HELLO")) {
                        writer.println("WELCOME");           //Writer funktionen skickar över meddelande
                    }
                    
                    try {
                        int guess = Integer.parseInt(msg);
                        
                        if (guess >= 1 && guess <=100) {
                            if (guess > randomnumber) {
                                writer.println("HI");
                            }
                            
                            if (guess < randomnumber) {
                                writer.println("LO");
                            }
                           
                            if (guess == randomnumber) {
                                writer.println("Correct");
                                n = false;                  //om det är rätt svar sätts n till false och går ut ur loopet
                            }
                        }
                        
                        else{
                           writer.println("Number must be between 1-100");
                        }
                    } 
                    
                    catch (NumberFormatException ex) {
                        if (!msg.equals("HELLO") || (Integer.parseInt(msg)>100) ||(Integer.parseInt(msg)<1) ) {
                            writer.println("You can only type 'HELLO' then numbers between 1-100 ");  //Exception på om anropet från klienten är fel
                        }
                    }
                } 
                
                catch (SocketException ex) {
                    System.out.println("Client Disconnected, Throwing out");   //ifall klienten stängs, gör en finally på klientens sida
                    n = false;
                }
            }
        } 
        
        catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
