import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by kasi-mac on 1/21/17.
 */
class ListenerThread extends Thread {
    private Socket socket = null;
    private HashMap<String, String> map;
    Scanner in;

    public ListenerThread(Socket socket, HashMap<String, String> map) {
        super("ListenerThread");
        this.socket = socket;
        this.map = map;

    }

    public void run() {


        //socket.close();
        //    String[] data = inputLine.split(",");
        //  Server.receivefromClient(data[0],data[1],data[2],data[3]);

        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));
        ) {
            String inputLine, outputLine = "Hello";
            out.println(outputLine);

            while ((inputLine = in.readLine()) != null) {
                // outputLine = kkp.processInput(inputLine);
                System.out.println("Server:" + inputLine);
                outputLine = inputLine;
                String[] data = inputLine.split(",");
                Server.receivefromClient(data[0], data[1], data[2], map.get(data[1]));
                if (data[2].equals("Bye")) {
                    out.println(outputLine);
                    break;
                }
                out.println(outputLine);


            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

public class Server {
    private final static String QUEUE_NAME = "hello";
    static Socket clientSocket = null;

    /* Server thread calls this method when message is received from the socket
    This method add the request to the RabbitMQ from Processing
    * */

    public static void receivefromClient(String from, String to, String msg, String port) throws IOException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        String message = from + "," + to + "," + msg + "," + port;
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");
        channel.close();
        connection.close();
    }

    /* Method to process the messages receive on RabbitMQ. Once the message is received, it has to be transferred to the
    corresponding client.
    * */

    public static void receiveFromQueue() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        HashMap<Integer, Socket> map = new HashMap<Integer, Socket>();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
                String[] data = message.split(",");
                String hostName = "localhost";
                int portNumber = Integer.parseInt(data[3]);
                try {
                    if (!map.containsKey(portNumber)) {
                        map.put(portNumber, new Socket(hostName, portNumber));
                    }
                    clientSocket = map.get(portNumber);
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(clientSocket.getInputStream()));
                    System.out.println("To Client");
                    out.println(message);
                    String inputLine = null;

                } catch (UnknownHostException e) {
                    System.err.println("Don't know about host " + hostName);
                    System.exit(1);
                } catch (IOException e) {
                    System.err.println("Couldn't get I/O for the connection to " +
                            hostName);
                    System.exit(1);
                }



                /*try {
                    Class cls = Class.forName(data[1]);
                    Object obj = cls.newInstance();
                    Class[] argTypes = new Class[] { String.class,String.class };
                    java.lang.reflect.Method method = cls.getDeclaredMethod("receiveFromServer", argTypes);
                    method.invoke(obj, data[0],data[2]);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                //Client2.receive(data[0],data[2]);
            }
        };
        channel.basicConsume(QUEUE_NAME, true, consumer);
    }

    public static void main(String[] args) throws Exception {
        receiveFromQueue();
        int portNumber = 1234;
        boolean listening = true;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("Client1", "2345");
        map.put("Client2", "3456");
        map.put("Client3", "4567");
        map.put("Client4", "5678");
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (listening) {
                new ListenerThread(serverSocket.accept(), map).start();

            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }

}
