import java.io.*;
import java.util.HashMap;

public class Tree implements Serializable,Files {
    String type = "tree";
    String content;
    HashMap<String,Blob> treeMap;
    int len;
    String hash;
    File file;

    Tree() throws Error, IOException, ClassNotFoundException {
        createTreeFile();
    }
        //读取index内容
    void createTreeFile() throws Error, IOException, ClassNotFoundException {
        //读取Index
        File index=new File(Git.Property+File.separator+".git/index.txt");
        HashMap<String, Blob> IndexMap;
        FileInputStream fileInputStream = new FileInputStream(index);
        ObjectInputStream in = new ObjectInputStream(fileInputStream);
        Object obj = in.readObject();
        treeMap = (HashMap<String, Blob>) obj;
        content=treeMap.toString();
        hash= Files.shaEncode(content);
        len=content.length();

        //创建文件
        File file = new File(Git.Property+File.separator+".git/Objects/" +hash + ".txt");
        this.file=file;
        if (!file.exists()) {
            file.createNewFile();
            System.out.println("Tree 文件创建成功");
        } else {
            System.out.println("Tree 已存在。");

        }
        System.out.println("Tree Path= " + file.getAbsolutePath());
//            fileWriter(type+String.valueOf(len)+hash, file);

        //blob序列化入tree
        Files.objectOutputStream(treeMap,file);







    }
}
