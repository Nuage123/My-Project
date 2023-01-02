import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

public class Commit implements Serializable,Files {
    String type="commit";
    String commitID;
    String committer;
    String lastID;
    String message;
    Tree tree;
    File file;

    Commit(Tree tree,String message) throws Error, IOException, ClassNotFoundException {
        this.tree=tree;
        this.message=message;
        lastID= Files.readFile(new File(Git.Property+File.separator+".git/Head.txt"));
        commitID= Files.shaEncode(tree.hash);
        System.out.println("commit ID= "+commitID);
        createCommitFile();
        System.out.println("message="+message);
        printChange();
        System.out.println("commit Path= "+file.getAbsolutePath());

    }

    void createCommitFile() throws Error, IOException {
        //创建文件
        File commit=new File(Git.Property+File.separator+".git/Objects/"+commitID+".txt");
        file=commit;
        if(!file.exists()){
            file.createNewFile();
            //System.out.println("commit文件创建成功");




            Scanner sc=new Scanner(System.in);
            System.out.println("Enter your name:");
            String name=sc.nextLine();
            System.out.println("Enter your email:");
            String email=sc.nextLine();
            committer=name+"<"+email+">";
            Files.fileWriter(type+" "+commitID+" "+tree.type+" "+tree.hash+" "+message+" last_update: "+ lastID+" \ncommitter: "+committer+" update time: "+getTime(),file);

            FileWriter fileWriter = new FileWriter(new File(Git.Property+File.separator+".git/Head.txt"));
            fileWriter.write(commitID);
            System.out.println("commit 成功");
            //System.out.println("message= "+message);
            //System.out.println("commit Path= "+commit.getAbsolutePath());
            fileWriter.close();

        }else{
            System.out.println("commit文件已存在");
            if(message!=Files.readFile(file).split(" ")[4])
                Files.fileWriterNoAppend(type+" "+commitID+" "+tree.type+" "+tree.hash+" "+message+" last_update: "+ lastID+" \ncommitter: "+committer+" update time: "+getTime(),file);
                System.out.println("message已更新");

        }


    }

    private void printChange() throws IOException, ClassNotFoundException {
        if(lastID.equals("")) System.out.println("已经是第一次commit");
        else if(!lastID.equals(commitID)){
        File lastCommitFile = new File(Git.Property + File.separator + ".git/Objects/" + lastID.trim() + ".txt");
        File lastTree=new File(Git.Property + File.separator + ".git/Objects/" +Files.readFile(lastCommitFile).split(" ")[3]+".txt");
            //反序列化
        HashMap<String, Blob> LastTreeMap;
        FileInputStream fileInputStream = new FileInputStream(lastTree);
        ObjectInputStream in = new ObjectInputStream(fileInputStream);
        Object obj = in.readObject();
        LastTreeMap = (HashMap<String, Blob>) obj;

        Boolean flag=false;
       for(String h:tree.treeMap.keySet()){
           if(!LastTreeMap.containsKey(h)){
               System.out.println("本次commit增加/改动了文件"+tree.treeMap.get(h).name);
               flag=true;
           }
       }
        for(String h:LastTreeMap.keySet()){
            if(!tree.treeMap.containsKey(h)){
                System.out.println("本次commit删除了文件"+LastTreeMap.get(h).name);
                flag=true;
            }
        }
        if(flag==false){
            System.out.println("本次commit无改动");
        }
        }



    }

    String getTime(){
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);

    }
}
