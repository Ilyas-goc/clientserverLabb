
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class GameApp {

    private static final int SERVER = 1;
    private static final int CLIENT = 2;
    private static final int UDP = 1;
    private static final int TCP = 2;
    private static int type = 0;
    private static int protocol = 0;
    private static int wellKnownPort = 2000;
    private static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        do {
            System.out.print("(1) Server or (2) client? > ");
            type = scan.nextInt();
        } while (type != SERVER && type != CLIENT);

        if (type == SERVER) {
            System.out.print("Server port: ");
            wellKnownPort = scan.nextInt();
        }

        do {
            System.out.print("(1) UDP or (2) TCP? > ");
            protocol = scan.nextInt();
        } while (protocol != UDP && protocol != TCP);

        if (type == SERVER) {
            if (protocol == UDP) {
                udpServer(wellKnownPort);
            } else {
                tcpServer(wellKnownPort);
            }
        } else {

            if (protocol == UDP) {
                udpClient();
            } else {
                tcpClient();
            }
        }
    }

    public static void tcpServer(int port) throws Exception {
        System.out.println("TCP Server");

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            boolean run = true;
            Random rand = new Random();
            int random = rand.nextInt(100) + 1;
            long time;

            BufferedReader input = null;
            PrintWriter out = null;
            System.out.println("Server is listening on port " + port);
            Socket socket = serverSocket.accept();
            System.out.println("Cient connected");

            while (run) {
                serverSocket.close();
             
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                String receive = input.readLine();
                System.out.println("Client Response: " + receive);

                if (receive.equals("bye")) {
                    out.println("bye client");
                    serverSocket = new ServerSocket(port);
                    socket = serverSocket.accept();
                    System.out.println("Cient connected");
                } 
                
                else if (receive.equals("HELLO")) {
                    time = System.currentTimeMillis();

                    out.println("Welcome");
                    System.out.println("Random nr: " + random);

                    while (true) {
                        try {
                            String msg = input.readLine();
                            int guess = Integer.parseInt(msg);
                            
                            if (System.currentTimeMillis() - time > 1000) {
                                out.println("Took to long time");
                                System.out.println("Client kicked from the server");
                                serverSocket = new ServerSocket(port);
                                socket = serverSocket.accept();
                                break;
                            }

                            System.out.println("Client Response: " + msg);
                            
                            if(msg.equals("bye")){
                                out.println("bye");
                                break;
                            }

                            if (guess >= 1 && guess <= 100) {
                                if (guess > random) {
                                    out.println("HI");
                                }

                                if (guess < random) {
                                    out.println("LO");
                                }

                                if (guess == random) {
                                    out.println("CORRECT");
                                    serverSocket = new ServerSocket(port);
                                    socket = serverSocket.accept();
                                    break;
                                }
                            } else {
                                out.println("There must be numbers between 1-100");
                            }
                        } catch (NumberFormatException ex) {
                            out.println("Wrong input");
                        } catch (IOException ex) {
                            System.err.println("I/O Error " + ex);
                        }
                    }
                } else {
                    out.println("Write HELLO to access the game");
                }
            }
        } catch (IOException ex) {
            System.err.print("I/O Error");
        }
    }

    public static void tcpClient() {
        System.out.println("TCP Client");
        boolean run = true;

        try {
            Socket socket = new Socket("localhost", wellKnownPort);

            while (run) {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                Scanner scan = new Scanner(System.in);
                System.out.print("Client: ");

                String send = scan.next();
                out.println(send);

                if (send.equals("bye")) {
                    run = false;
                }

                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String receive = input.readLine();
                
                if(receive.equals("CORRECT") || receive.equals("bye") || receive.equals("Took to long time")){
                    run = false;
                }
                
                System.out.println("Server: Response: " + receive);
            }
        } catch (SocketException ex) {
            System.out.println("BUSY");
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }

    public static void udpServer(int port) throws IOException {

        System.out.println("Udp Server is started");
        DatagramPacket packet;
        InetAddress ipAdd;
        int thePort;
        long time;

        boolean run = true;
        boolean run2 = true;
        DatagramSocket ds = new DatagramSocket(port);
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        byte[] receive = new byte[1024];
        byte[] send = new byte[1024];

        DatagramPacket DpSend = null;
        DatagramPacket DpReceive = null;
        Random rand = new Random();
        int random = rand.nextInt(100) + 1;

        try {
            while (true) {
                DpReceive = new DatagramPacket(receive, receive.length);
                ds.receive(DpReceive);
                ipAdd = DpReceive.getAddress();
                thePort = DpReceive.getPort();

                if (thePort != DpReceive.getPort()) {
                    send = "BUSY".getBytes();
                    DpSend = new DatagramPacket(send, send.length, DpReceive.getAddress(), DpReceive.getPort());
                    ds.send(DpSend);
                }

                System.out.println("Client Response: " + data(receive));

                if (data(receive).equals("HELLO")) {
                    if (thePort == DpReceive.getPort() && ipAdd == DpReceive.getAddress()) {
                        send = "Weclome".getBytes();
                        DpSend = new DatagramPacket(send, send.length, ipAdd, thePort);
                        ds.send(DpSend);
                        send = new byte[1024];
                        receive = new byte[1024];
                        time = System.currentTimeMillis();

                        System.out.println("Rand nr: " + random);
                        System.out.println("The client is in the game ");
                        run2 = true;

                        while (run2) {
                            try {

                                DpReceive = new DatagramPacket(receive, receive.length);
                                ds.receive(DpReceive);

                                if (System.currentTimeMillis() - time > 20000) {
                                    System.out.println("Client kicked form the server");
                                    send = "Took to long".getBytes();
                                    DpSend = new DatagramPacket(send, send.length, DpReceive.getAddress(), DpReceive.getPort());
                                    ds.send(DpSend);
                                    run2 = false;
                                    break;
                                }

                                String msg = data(receive);
                                if (DpReceive.getPort() == thePort) {

                                    if (data(receive).equals("bye")) {
                                        send = "bye client".getBytes();
                                        DpSend = new DatagramPacket(send, send.length, DpReceive.getAddress(), DpReceive.getPort());
                                        ds.send(DpSend);
                                        break;
                                    }

                                    int guess = Integer.parseInt(msg);

                                    if (guess >= 1 && guess <= 100) {
                                        if (guess > random) {
                                            send = "HI".getBytes();
                                        }

                                        if (guess < random) {
                                            send = "LO".getBytes();
                                        }

                                        if (guess == random) {
                                            send = "CORRECT".getBytes();
                                            DpSend = new DatagramPacket(send, send.length, DpReceive.getAddress(), DpReceive.getPort());
                                            ds.send(DpSend);
                                            break;
                                        }
                                    } else {
                                        send = "Number must be between 1-100".getBytes();
                                    }

                                    if (run2 == false) {
                                        break;
                                    }
                                    System.out.println("Client Response: " + data(receive));

                                    DpSend = new DatagramPacket(send, send.length, DpReceive.getAddress(), DpReceive.getPort()); /////
                                    ds.send(DpSend);
                                    receive = new byte[1024];
                                    send = new byte[1024];
                                    time = System.currentTimeMillis();
                                } else {
                                    send = "BUSY".getBytes();
                                    DpSend = new DatagramPacket(send, send.length, DpReceive.getAddress(), DpReceive.getPort());
                                    ds.send(DpSend);
                                }
                            } catch (NumberFormatException ex) {
                                if (data(receive).equals("bye")) {
                                    send = "Bye client genom exception".getBytes();
                                    DpSend = new DatagramPacket(send, send.length, DpReceive.getAddress(), DpReceive.getPort());
                                    ds.send(DpSend);
                                } else {
                                    send = "Wrong input".getBytes();
                                    DpSend = new DatagramPacket(send, send.length, DpReceive.getAddress(), DpReceive.getPort());
                                    ds.send(DpSend);
                                    time = System.currentTimeMillis();
                                    receive = new byte[1024];
                                    send = new byte[1024];
                                }
                            } catch (NullPointerException ex) {
                                System.err.println("Null " + ex);
                            }
                        }
                    }
                }

                if (!data(receive).equals("HELLO")) {
                    String str = "Write Hello to access server";

                    if (data(receive).equals("bye")) {
                        send = "Bye client ".getBytes();
                    } else {
                        send = str.getBytes();
                    }
                }

                DpSend = new DatagramPacket(send, send.length, DpReceive.getAddress(), DpReceive.getPort());
                ds.send(DpSend);
                receive = new byte[1024];
                send = new byte[1024];
            }
        } catch (SocketException ex) {
            System.err.println("BUSY");
        } catch (IOException ex) {
            System.err.println("You got an error in I/O");
        }
    }

    public static void udpClient() throws IOException {
        System.out.println("UDP client");
        DatagramSocket ds = null;

        try {
            boolean run = true;

            ds = new DatagramSocket();
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            InetAddress ip = InetAddress.getByName("127.0.0.1");

            byte[] send = new byte[1024];
            byte[] receive = new byte[1024];

            while (run) {
                System.out.print("Client: ");
                send = input.readLine().getBytes();

                DatagramPacket packet = new DatagramPacket(send, send.length, ip, wellKnownPort);
                ds.send(packet);

                packet = new DatagramPacket(receive, receive.length, packet.getAddress(), packet.getPort());
                ds.receive(packet);

                String msg = data(receive);
                String exit = new String(send);

                System.out.println("Server response: " + "" + msg);

                if (msg.equals("BUSY") || exit.equals("bye") || msg.equals("Took to long") || msg.equals("CORRECT")) {
                    run = false;
                    ds.close();
                }
                receive = new byte[1024];
            }
        } catch (SocketException ex) {
            System.err.println("Busy");
        } catch (IOException ex) {
            System.err.print("I/O Error");
        } finally {
            ds.close();
        }

    }

    public static String data(byte[] data) {
        int loop = 0;
        StringBuilder sb = new StringBuilder();

        if (data == null) {
            return null;
        }

        while (data[loop] != 0) {
            sb.append((char) data[loop]);
            loop++;
        }
        return sb.toString();
    }
}
