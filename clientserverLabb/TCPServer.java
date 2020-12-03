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
