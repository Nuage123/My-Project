import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

public class Blob implements Serializable,Files{
    String type="blob";
    String content;
    int len;
    String hash;
    String name;
    File file;
    Blob(File file) throws Error, IOException {
        content= Files.readFile(file);
        len=content.length();
         hash= Files.shaEncode(content);
        name=file.getName();
        this.file=file;
        createBlobFile();
    }
    void createBlobFile() throws IOException {
        File file = new File(Git.Property+File.separator+".git/Objects" + File.separator + hash + ".txt");
        if (!file.exists()) {
            file.createNewFile();
            System.out.println("Blob of "+name+"创建成功");
            Files.fileWriter(type+" "+String.valueOf(len)+" "+hash+" "+name+" "+content,file);
        } else System.out.println("Blob of "+name+"已存在");

        System.out.println("Blob Path= " + file.getAbsolutePath());

    }



}
