/**
 * Created by kasi-mac on 1/21/17.
 */
import java.net.*;
import java.io.*;
import java.util.Scanner;
class KKMultiServerThread extends Thread {
    private Socket socket = null;
    Scanner in;
    public KKMultiServerThread(Socket socket) {
        super("KKMultiServerThread");
        this.socket = socket;
    }

    public void run() {

        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));
        ) {
            String inputLine, outputLine="Hello";
            out.println(outputLine);

            while ((inputLine = in.readLine()) != null) {
               // outputLine = kkp.processInput(inputLine);
                System.out.println("Server:"+inputLine);
                outputLine=inputLine+"fasfa";
                out.println(outputLine);
                if (outputLine.equals("Bye"))
                    break;
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
public class SocketServer {
    public static void main(String[] args) throws IOException {



        int portNumber = 1234;
        boolean listening = true;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (listening) {
                new KKMultiServerThread(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }
}
