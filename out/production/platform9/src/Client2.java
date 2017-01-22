/**
 * Created by kasi-mac on 1/21/17.
 */
import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Scanner;

public class Client2 {
    static String currentClient;
    private final static String MY_NAME = "Client2";
    private final static int PORT =3456;
    private final static String QUEUE_NAME =MY_NAME;
    public static void receiveFromServer(String from,String msg) throws IOException {
        // System.out.println(from);

        //if(from.equals(currentClient)) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        String message = from+","+msg;
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        //System.out.println(" [x] Sent '" + message + "'");
        channel.close();
        connection.close();
        //  }
    }
    public static void ProcessMessage() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                //    System.out.println(message);
                String[] data=message.split(",");
                System.out.println(data[0]+": "+data[1]);

            }
        };
        channel.basicConsume(QUEUE_NAME, true, consumer);
    }
    static StringBuffer openChat(String clientName) {
        return null;
    }
    public static void main(String args[]) throws Exception {
        String hostName = "localhost";
        int portNumber = 1234;
        HashMap<String,Boolean> map=new HashMap<String,Boolean>();
        map.put("Client1",true);
        map.put("Client2",true);
        map.put("Client3",true);
        map.put("Client4",true);
        ProcessMessage();
        new Thread(new ClientRecv(PORT), "recv").start();
        while(true) {
            System.out.println("Client List");
            for ( String key : map.keySet() ) {
                System.out.println( key );
            }
            System.out.println("Enter Client Name:");
            Scanner sc=new Scanner(System.in);
            String currentClient=sc.nextLine();
            if(map.containsKey(currentClient)) {
                System.out.println("Start Typing......");
                try (

                        Socket kkSocket = new Socket(hostName, portNumber);
                        PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(kkSocket.getInputStream()));
                ) {

                    BufferedReader stdIn =
                            new BufferedReader(new InputStreamReader(System.in));
                    String fromServer;
                    String fromUser;
                    while ((fromServer = in.readLine()) != null) {
                        //System.out.println("You: " + fromServer);
                        if (fromServer.equals("Bye"))
                            break;

                        fromUser = stdIn.readLine();
                        if (fromUser != null) {
                            System.out.println("You: " + fromUser);
                            out.println(MY_NAME+","+currentClient+","+fromUser);
                        }
                    }
                } catch (UnknownHostException e) {
                    System.err.println("Don't know about host " + hostName);
                    System.exit(1);
                } catch (IOException e) {
                    System.err.println("Couldn't get I/O for the connection to " +
                            hostName);
                    System.exit(1);
                }
            }
        }
    }




}
//  out.println(MY_NAME+","+currentClient+","+fromUser+","+map.get(currentClient));


