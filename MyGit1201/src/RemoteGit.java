import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class RemoteGit implements Runnable,Transmission{
    static int port = 7979;
    static ServerSocket server;
    static Socket socket;
    static String pathname = "C:\\Users\\nuage\\Desktop\\MyGit1201\\Master\\";
    //创建流
    static InputStream in;
    static DataInputStream dis;
    @SuppressWarnings("resource")
    public static void main(String[] args) throws Error, IOException {
           start();

    }
    static void connect() throws IOException {

                socket = server.accept();
                System.out.println("新客户端链接: " + socket.getInetAddress() + " P:" + socket.getLocalPort());
    }


static void start() throws Error, IOException {

    Boolean flag = true;
    server = new ServerSocket(port);
    System.out.println("服务器就绪");
    System.out.println("服务器信息： " + server.getInetAddress() + server.getLocalPort());
    System.out.println(System.getProperty("user.dir"));

    File d = new File(pathname);
    if (!d.exists()) {
        d.mkdirs();
    }
    System.out.println("远程仓库已创立: " + d.getAbsolutePath());

    String instruct;
    while (flag) {
        connect();
        dis = new DataInputStream(socket.getInputStream());
        instruct = dis.readUTF();
        if (instruct.equals("push")) {
            Transmission.pull(pathname, socket);


        } else if (instruct.equals("pull")) {
            Transmission.push(pathname, socket);

        } else if (instruct.equals("exit")) {
            flag = false;

        }
        flag=false;

    }
    close();


}
static void close() throws IOException {
    try{socket.close();
    dis.close();
  //  in.close();
    server.close();
    }catch (Error e){}
}




    @Override
    public void run() {
        try {
            start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Error e) {
            throw new RuntimeException(e);
        }
    }
}
