import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Transmission {

    public static int port=7979;
    public static String host = "localhost";
    //            }
    public static Socket socket = null;

    @SuppressWarnings("resource")
    Client(){
      createSocket();
    }
    void push() throws Error, IOException {
        DataOutputStream dos=new DataOutputStream(socket.getOutputStream());
        dos.writeUTF("push");
        Transmission.push(Git.Property,socket);
    }

    void pull() throws Error, IOException {
        DataOutputStream dos=new DataOutputStream(socket.getOutputStream());
        dos.writeUTF("pull");
        Transmission.pull(Git.Property,socket);
    }
    public static void createSocket(){
        try {
            socket = new Socket(host, port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void close() throws IOException {
        socket.close();
    }

}



