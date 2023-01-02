import java.io.*;
import java.math.RoundingMode;
import java.net.Socket;
import java.text.DecimalFormat;


import static java.io.File.separator;


public interface Transmission {

static void push(String path,Socket socket) throws Error, IOException {
    ZipUtil.compressDirectory(path+ separator+".git",path+separator+".git");   //压缩文件
    File zip=new File(path+ separator+".git.zip");
    sendFile(zip, socket);  //发送文件
    zip.delete();
}

static void pull(String path,Socket socket) throws Error, IOException {
    File git=new File(path+ separator+".git");
    if(git.exists()) Files.deleteDirectory(git);
    Transmission.receiveFile(path,socket);  //接收文件
    String zip=path+"\\.git.zip";
    ZipUtil.unzip(zip,path);    //解压文件
    new File(zip).delete();
}
static void sendFile(File file, Socket client) throws Error, IOException {
        FileInputStream fis = new FileInputStream(file);
        DataOutputStream dos = new DataOutputStream(client.getOutputStream());
        try {

                // 文件名和长度
                dos.writeUTF(file.getName());
                dos.flush();
                dos.writeLong(file.length());
                dos.flush();

                // 开始传输文件
                System.out.println("======== Push Started ========");
                byte[] bytes = new byte[1024];
                int length = 0;
                long progress = 0;
                while((length = fis.read(bytes, 0, bytes.length)) != -1) {
                    dos.write(bytes, 0, length);
                    dos.flush();
                    progress += length;
                    System.out.print("| " + (100*progress/file.length()) + "% |");
                }
                System.out.println();
                System.out.println("======== Push Success ========");

        } catch (Error e) {
            e.printStackTrace();
        } finally {
            if(fis != null)
                fis.close();
            if(dos != null)
                dos.close();
            client.close();
        }
    }
    static void receiveFile(String path, Socket socket){
        DataInputStream dis = null;
        FileOutputStream fos = null;
        try {
            System.out.println("======== Pull Started ========");
            dis = new DataInputStream(socket.getInputStream());

            // 文件名和长度
            String fileName = dis.readUTF();
            long fileLength = dis.readLong();
            File directory = new File(path);
            if(!directory.exists()) {
                directory.mkdir();
            }
            File file = new File(directory.getAbsolutePath() + File.separatorChar + fileName);
             fos = new FileOutputStream(file);

            // 开始接收文件
            byte[] bytes = new byte[1024];
            int length = 0;
            while((length = dis.read(bytes, 0, bytes.length)) != -1) {
                fos.write(bytes, 0, length);
                fos.flush();
            }
            System.out.println("======== Pull Success [File Name：" + fileName + "] [Size：" + getFormatFileSize(fileLength) + "] ========");
        } catch (Error e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if(fos != null)
                    fos.close();
                if(dis != null)
                    dis.close();
                socket.close();
            } catch (Error e) {} catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private static String getFormatFileSize(long length) {
        DecimalFormat df = new DecimalFormat("#0.0");
        df.setRoundingMode(RoundingMode.HALF_UP);
        df.setMinimumFractionDigits(1);
        df.setMaximumFractionDigits(1);

        double size = ((double) length) / (1 << 30);
        if(size >= 1) {
            return df.format(size) + "GB";
        }
        size = ((double) length) / (1 << 20);
        if(size >= 1) {
            return df.format(size) + "MB";
        }
        size = ((double) length) / (1 << 10);
        if(size >= 1) {
            return df.format(size) + "KB";
        }
        return length + "B";
    }







}






