//import相关类
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//Files接口，文件能用到的一些共同方法
public interface Files {

    //计算hashcode
    //sha-1
    static String shaEncode(String inStr) throws Error, UnsupportedEncodingException {
        MessageDigest sha = null;
        try {
            sha = MessageDigest.getInstance("SHA");
        } catch (Error e) {
            System.out.println(e);
            e.printStackTrace();
            return "";
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] byteArray = inStr.getBytes("UTF-8");
        byte[] MessageDigestBytes = sha.digest(byteArray);
        StringBuffer hexNumber = new StringBuffer();
        for (int i = 0; i < MessageDigestBytes.length; i++) {
            int val = ((int) MessageDigestBytes[i]) & 0xff;
            if (val < 16) {
                hexNumber.append("0");
            }
            hexNumber.append(Integer.toHexString(val));
        }
        return hexNumber.toString();
    }

    //objectOutputStream写入，将Blob序列化到index中
    static void objectOutputStream(Object object,File fileToWrite) throws IOException {
        //创建FileOutPutStream
        FileOutputStream fileOutputStream = new FileOutputStream(fileToWrite);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.close();
    }

    //File Writer写文件，写入文本文件
    static void fileWriter(String info, File file) throws IOException {
        FileWriter fileWriter = new FileWriter(file, true);
        fileWriter.write(info + " ");
        fileWriter.close();
    }
    static void fileWriterNoAppend(String info, File file) throws IOException {
        FileWriter fileWriter = new FileWriter(file, false);
        fileWriter.write(info + " ");
        fileWriter.close();
    }
    //读文件，返回字符串
    static String readFile(File fileToRead) throws IOException {
        //创建fileInputStream,InputStreamReader,BufferedReader
            FileInputStream fis = new FileInputStream(fileToRead);
            InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);

            String line;
            String str="";

            //当line不为空时，循环继续
            while((line = br.readLine()) != null){
                //将Line加入str
                str+="\n"+line;
            }
            //关闭br
            br.close();
           return str;

        }
    public static void deleteDirectory(File dir) {


        if(!dir.isDirectory() == false) {
            return;
        }
        File[] listFiles = dir.listFiles();
        for(File file : listFiles){
            file.delete();
        }

        dir.delete();

    }
    }


