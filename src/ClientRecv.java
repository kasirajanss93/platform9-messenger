import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

class ClientRecv implements Runnable {
    int port;
    ClientRecv(int port) {
        this.port=port;
    }
    public void run() {
        boolean listening = true;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
                Socket socket = serverSocket.accept();
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));
                String inputLine=null;
                while ((inputLine = in.readLine()) != null) {
                    // outputLine = kkp.processInput(inputLine);
                    String[] data=inputLine.split(",");
                    Class cls = Class.forName(data[1]);
                    Object obj = cls.newInstance();
                    Class[] argTypes = new Class[] { String.class,String.class };
                    java.lang.reflect.Method method = cls.getDeclaredMethod("receiveFromServer", argTypes);
                    method.invoke(obj, data[0],data[2]);
                    //out.println("ok");
            }

        } catch (IOException e) {
            System.err.println("Could not listen on port " + port);
            System.exit(-1);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}