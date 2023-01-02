import java.io.*;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Scanner;

import static java.lang.System.exit;
import static java.lang.System.setOut;

public class Git implements Files {

    //当前位置
    //static String Property = System.getProperty("user.dir");

    static String Property=System.getProperty("user.dir");

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        try {
            System.out.println("Welcome to Git");
            System.out.println("Property= " + Property);
            if (args.length == 0)
                help();
                //task 1
            else if (args[0].equals("init")) {
                init();
            } else if (args[0].equals("add")) {
                if (args.length == 2 && args[1].equals(".")) {
                    //遍历文件夹
                    //  System.out.println("add all");
                    File dir = new File(Property);
                    File[] files = dir.listFiles();
                    boolean add = false;
                    for (File f : files) {
                        if (f.isFile()) {
                            add(f);
                            add = true;
                        }
                    }
                    if (!add) System.out.println("工作区中没有文件可以add。");
                } else {
                    //System.out.println("Add file");
                    add(new File(args[1]));

                }
            } else if (args[0].equals("commit")) {

                if (args.length < 2) commit("");
                else {
                    try {
                        commit(args[1].split("-m")[1].replaceAll("'", "").replaceAll("'", ""));
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("输入信息请使用-m'' ");
                    }
                }
                ;

                //task 2
            } else if (args[0].equals("rm")) {
                //rm--cache
                if (args[1].equals("--cached"))
                    rmCached(args[2]);
                    //rm
                else rm(args[1]);

            } else if (args[0].equals("log")) {
                log();

            } else if (args[0].equals("reset")) {
                if (args.length == 2) {
                    reset(args[1]);
                } else reset(args[2], args[1]);

                //task 3
            } else if (args[0].equals("pull")) {
                pull();
            } else if (args[0].equals("push")) {
                push();
            } else help();

            }catch (ArrayIndexOutOfBoundsException ob){
            System.out.println("参数缺失。请按要求输入参数");
        }catch(FileNotFoundException nf) {
            System.out.println("请输入合法路径");
        }catch (SocketException e){
            System.out.println("连接异常断开");

        } catch(Exception e){
                    System.out.println("未输入合法参数或程序异常退出,请输入Git help获取帮助。\n错误信息: "+e);
                    exit(0);
    }
    }



    //functions

    static void help() throws IOException {
       // System.out.println(Files.readFile(new File("out/production/MyGit1201/Help")));
        String help=
                "---------Welcome to My Git。请按照下列提示进行操作------------------------------------------\n" +
                "\n" +
                "Git init\t        \t  初始化Git（请在进行其他操作前确认已进行初始化）\n" +
                "Git add [file]\t        在index对象中添加/修改/删除(请输入输入文档路径并确认路径正确）\n" +
                "Git add .               对工作区的全部文件进行一次add操作。输入add . 时，将只存在于暂存区中，而不存在于工作区的文件记录，从index中删除\n" +
                "Git commit  \t        将index中所有条目生成tree对象序列化到objects文件夹下\n" +
                "Git rm [file]\t        在index对象中删除对应条目，在工作区中删除该文件(请输入输入文档路径并确认路径正确）\n" +
                "Git rm --cached [file]  仅删除index中对应条目(请输入输入文档路径并确认路径正确）\n" +
                "Git log                 打印前次的commit信息,直到打印完第一次commit的内容。\n" +
                "Git reset [commit ID]   默认为mixed模式（见reset--mixed)  （请输入commit ID并确认该commit文件存在）\n" +
                "Git reset --soft：      修改HEAD文件内容为给定commit id，（请输入commit ID并确认该commit文件存在）\n" +
                "Git reset --mixed：     在soft模式的基础上，重置暂存区到对应commit，（请输入commit ID并确认该commit文件存在）\n" +
                "Git reset --hard：      在mixed模式的基础上，重置工作区与暂存区内容一致。（请输入commit ID并确认该commit文件存在）\n" +
                "Git pull/push\t       上传本地仓库内容到远程仓库、从远程仓库取回内容\n" +
                "-----------------------------------------------------------------------------------------";
        System.out.println(help);
    }
    static void init() {
        try {//创建Git
            File git = new File(Property + File.separator + ".git/Objects");
            if (!git.exists()) {
                git.mkdirs();
                System.out.println(".git/Objects创建成功！");
            } else System.out.println(".git/Objects已存在");

            System.out.println("Path: " + git.getAbsolutePath());
            //创建Hash
            //创建Index
            File index = new File(Git.Property + File.separator + ".git/index.txt");

            try {
                if (!index.exists()) {
                    index.createNewFile();
                    System.out.println("index创建成功");
                } else System.out.println("Index已存在");
            } catch (Exception e) {
                System.out.println("index 创建失败");
                exit(0);
            }

            System.out.println("Path: " + index.getAbsolutePath());
            //创建Head
            File Head = new File(Property + File.separator + ".git/Head.txt");
            if (!Head.exists()) {
                Head.createNewFile();
                System.out.println("Head创建成功！");
            } else System.out.println("Head已存在");

            System.out.println("Path: " + Head.getAbsolutePath());

        } catch (Error e) {
            System.out.println("初始化失败，请输入合法地址");
            exit(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    static void add(File file) throws Error, ConcurrentModificationException, IOException {
        File index = new File(Property + File.separator + ".git/index.txt");
        if (!index.exists()) init();
        HashMap<String, Blob> IndexMap=new HashMap<>();

        //读出indexmap
        try {
            FileInputStream fileInputStream = new FileInputStream(index);
            ObjectInputStream in = new ObjectInputStream(fileInputStream);
            Object obj = in.readObject();
            IndexMap = (HashMap<String, Blob>) obj;

        } catch (Error e) {
            IndexMap = new HashMap<String, Blob>();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }catch (EOFException e){}

        //判断文件存在
        if (!file.exists()) {
            System.out.println("要add的文件不存在。");
            exit(0);
        }

        //add
        //生成blob
        // 更新Index
        Blob blob = new Blob(file);
        IndexMap.put(blob.hash, blob);
        //System.out.println(IndexMap.size());

//删除不存在于工作区的文件
        try {
            for (String f : IndexMap.keySet()) {
                Blob b = IndexMap.get(f);
                if (!b.file.exists()) {
                    System.out.println(b.name + "不存在，从index中删除。");
                    IndexMap.remove(f);
                }
            }
        } catch (ConcurrentModificationException e) {

        }


        //Map写入index
        Files.objectOutputStream(IndexMap, index);
        System.out.println("Index已更新");
        FileInputStream fileInputStream = new FileInputStream(index);


    }

    static void commit(String message) throws Error, IOException, ClassNotFoundException {

        //生成tree
        Tree tree = new Tree();
        //生成commit
        Commit commit = new Commit(tree, message);

    }


    static void rmCached(String fileName) throws IOException, ClassNotFoundException {
        //反序列化
        File index = new File(Git.Property + File.separator + ".git/index.txt");
        try {
            FileInputStream fileInputStream = new FileInputStream(index);
            ObjectInputStream in = new ObjectInputStream(fileInputStream);
            Object obj = in.readObject();
            HashMap<String, Blob> IndexMap = (HashMap<String, Blob>) obj;

            for (String f : IndexMap.keySet()) {
                if (IndexMap.get(f).file.equals(new File(Property+File.separator+fileName))){
                    IndexMap.remove(f);
                    System.out.println(fileName + "已在index中删除。");
                    Files.objectOutputStream(IndexMap, index);
                    return;
                }
            }
            System.out.println("Index中不存在可以删除的条例。");


        } catch (Exception e) {
            System.out.println("Index中不存在可以删除的条例。");
            exit(0);
        }

    }

    static void rm(String fileName) throws IOException, ClassNotFoundException {
        rmCached(fileName);
        File file = new File(Property+File.separator+fileName);
        if (file.exists()) {
            file.delete();
            System.out.println(fileName + "已在工作区中删除。");
        } else {
            System.out.println("工作区中不存在可以删除的条例。");
        }

    }

    static void log() throws IOException {
        File Head = new File(Property + File.separator + ".git/Head.txt");
        if(!Head.exists()){
            System.out.println("请先初始化");
            return ;
        }

        String commitID = Files.readFile(Head);
        if(commitID.equals("")){
            System.out.println("还未commit过");
            return ;
        }

        File commitFile=new File(Property + File.separator + ".git/Objects/" + commitID.trim() + ".txt");

        String message=Files.readFile(commitFile).split(" ")[4];

        String time=Files.readFile(commitFile).split("update time: ")[1];

//        File commitFile=new File(".git/Objects/"+commitID.trim()+".txt");

//        System.out.println(Files.readFile(commitFile).split(" ").length);
        File zero=new File("C:\\Users\\nuage\\Desktop\\MyGit1201\\Master\\.git\\Objects\\aa66e51c70bec1453ebc88f0d18151e38fab0a53.txt");


        // 循环打印
        boolean flag=true;
        while (commitFile.exists()) {

            System.out.println("commit ID: "+commitID.replace("\n",""));

            System.out.println("message: "+message);

            System.out.println("update time: "+time+"\n");



            commitID = Files.readFile(commitFile).split(" ")[6];
            commitFile = new File(Property + File.separator + ".git/Objects/" + commitID.trim() + ".txt");
            if(!commitFile.exists())   {
                System.out.println("已是第一次commit");
                return ;}


            message=Files.readFile(commitFile).split(" ")[4];


            time=Files.readFile(commitFile).split("update time: ")[1];


        }

        //循环结束



    }


    static void reset(String commitID) throws IOException, ClassNotFoundException {
        reset(commitID, "--mixed");
    }

    static HashMap<String, Blob> reset(String commitID, String mode) throws IOException, ClassNotFoundException {
        HashMap<String, Blob> IndexMap = null;
       try{ if (mode.equals("--soft")) {
            FileWriter fw = new FileWriter(Property + File.separator + ".git/Head.txt");
            fw.write(commitID);
            fw.close();
            System.out.println("Head已更新");
        } else if (mode.equals("--mixed")) {
            reset(commitID, "--soft");
            File commitFile = new File(Property + File.separator + ".git/Objects/" + commitID + ".txt");
            String treeID = Files.readFile(commitFile).split(" ")[3];
            File treeFile = new File(Property + File.separator + ".git/Objects/" + treeID + ".txt");
            //反序列化

            FileInputStream fileInputStream = new FileInputStream(treeFile);
            ObjectInputStream in = new ObjectInputStream(fileInputStream);
            Object obj = in.readObject();
            IndexMap = (HashMap<String, Blob>) obj;
            //重置暂存区
            File index = new File(Property + File.separator + ".git/index.txt");
            if (index.exists()) index.delete();
            File newIndex = new File(Property + File.separator + ".git/index.txt");
            //序列化
            Files.objectOutputStream(IndexMap, newIndex);
            System.out.println("暂存区已重置");

        } else if (mode.equals("--hard")) {
            IndexMap = reset(commitID, "--mixed");

            //重置工作区：多了删掉，少了还原
            File dir = new File(Property);
            File[] files = dir.listFiles();
            //System.out.println(files);
            ArrayList<File> temp = new ArrayList<>();
            HashMap<File, Blob> blobFiles = new HashMap<>();
            //int cnt = 0;
            //删除多余内容
            for (Blob b : IndexMap.values()) {
                blobFiles.put(b.file, b);
            }
           // System.out.println("blobfiles:" + blobFiles.toString());
            for (File f : files) {
                if (f.isFile()) {
                    temp.add(f);
                    if (!blobFiles.containsKey(f)) {
                            f.delete();
                        System.out.println(f.getName() + "不存在于暂存区，已在工作区删除");
                    }
                }
            }


            for (File f : blobFiles.keySet()) {
                if (!temp.contains(f)) {
                    File add = new File(Property + File.separator + f.getName());
                    Blob b = blobFiles.get(f);
                    Files.fileWriter(b.content.trim(), add);
                    System.out.println("已在工作区恢复文件" + b.name);
                }
            }
            System.out.println("工作区已恢复。");
        }


//                        if(IndexMap.containsKey(shaencode(Files.readFile(f)))
//                        }
//                        //删除
//                    if(!temp.contains(f)) f.delete();
//                    }


       }catch (FileNotFoundException e){
           System.out.println("请输入合法commit");
           exit(0);
       }finally {
           return IndexMap;
       }
        }
static void push() throws Error, IOException {
    Client client = null;
    try {
        Thread thread = new Thread(new RemoteGit());
        thread.start();
        client = new Client();
        client.push();
       // thread.interrupt();
    } catch (FileNotFoundException e) {
        System.out.println("没有内容可以push");
        exit(0);

    }
    }




static void pull() throws Error, IOException, ClassNotFoundException {
   try{
       Thread thread=new Thread(new RemoteGit());
    thread.start();
    Client client=new Client();
    client.pull();
    System.out.println("已从远程仓库取回内容");
    String commitID=Files.readFile(new File(Property+File.separator+".git/Head.txt")).trim();
    if(commitID!="")
    reset(commitID,"--hard");
   }catch (NullPointerException e){
       System.out.println("没有内容可以取回");
       exit(0);
   }

    }

}


