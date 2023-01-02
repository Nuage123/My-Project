
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipInputStream;

//ZipUtil类用来压缩/解压文件
public class ZipUtil {

    private static final int  BUFFER_SIZE = 2 * 1024;

    /**
     * 压缩成ZIP
     * @param sourceDir 压缩文件夹路径
     * @param outputStream    压缩文件输出流
     * @param KeepDirStructure  是否保留原来的目录结构,true:保留目录结构;
     * 							false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */

    //压缩文件
    public static void toZip(String sourceDir, OutputStream outputStream, boolean KeepDirStructure)
            throws RuntimeException{
        long start = System.currentTimeMillis();
        ZipOutputStream zos = null ;
        try {
            zos = new ZipOutputStream(outputStream);
            //创建文件
            File sourceFile = new File(sourceDir);
            //调用compress函数进行压缩
            compress(sourceFile,zos,sourceFile.getName(),KeepDirStructure);
            long end = System.currentTimeMillis();
            System.out.println("仓库压缩完成，耗时：" + (end - start) +" ms");

        }
        //抛出异常
        catch (Error e) {
            throw new RuntimeException("zip error from ZipUtils",e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally{
            if(zos != null){
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

//使用递归的方法压缩

    /**
     * 递归压缩方法
     * @param sourceFileToCompress 源文件
     * @param zos zip输出流
     * @param fileName		 压缩后的名称
     * @param keepOriginalDirStructure  是否保留原来的目录结构,true:保留目录结构;
     * 							false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws Error
     */
    private static void compress(File sourceFileToCompress, ZipOutputStream zos, String fileName, boolean keepOriginalDirStructure) throws Error, IOException {
        //创建数组
        byte[] buffer = new byte[BUFFER_SIZE];
        //如果路径是文件
        if(sourceFileToCompress.isFile()){
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            zos.putNextEntry(new ZipEntry(fileName));
            // copy文件到zip输出流中
            int length;
            FileInputStream in = new FileInputStream(sourceFileToCompress);
            while ((length = in.read(buffer)) != -1){
                zos.write(buffer, 0, length);
            }
            // Complete the entry
            zos.closeEntry();
            //关闭输入流
            in.close();
        } else {
            File[] listFiles = sourceFileToCompress.listFiles();
            if(listFiles == null || listFiles.length == 0){
                // 需要保留原来的文件结构时,需要对空文件夹进行处理
                if(keepOriginalDirStructure){
                    // 空文件夹的处理
                    zos.putNextEntry(new ZipEntry(fileName + "/"));
                    // 没有文件，不需要文件的copy
                    zos.closeEntry();
                }

            }else {
                for (File file : listFiles) {
                    // 判断是否需要保留原来的文件结构
                    if (keepOriginalDirStructure) {
                        // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                        // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                        compress(file, zos, fileName + "/" + file.getName(),keepOriginalDirStructure);
                    } else {
                        compress(file, zos, file.getName(),keepOriginalDirStructure);
                    }

                }
            }
        }
    }

    //压缩文件夹
    public static void compressDirectory(String sourceFile,String targetAddress) throws Error, FileNotFoundException {
        /** 测试压缩方法1  */
        FileOutputStream fos1 = new FileOutputStream(new File(targetAddress+".zip"));
        toZip(sourceFile, fos1,true);

    }


    //解压文件

        /**
         * 解压
         *
         * @param zipFilePath 带解压文件
         * @param desDirectory 解压到的目录
         * @throws Error
         */
        public static void unzip(String zipFilePath, String desDirectory) throws Error, IOException {

            File desDir = new File(desDirectory);
            //判断目标路径是否存在
            if (!desDir.exists()) {
                boolean mkdirSuccess = desDir.mkdir();
                //不存在抛出错误
                if (!mkdirSuccess) {
                    throw new Error("创建解压目标文件夹失败");
                }
            }
            // 读入流
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath));
            // 遍历每一个文件
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                if (zipEntry.isDirectory()) { // 文件夹
                    String unzipFilePath = desDirectory + File.separator + zipEntry.getName();
                    // 直接创建
                    mkdir(new File(unzipFilePath));
                } else { // 文件
                    String unzipFilePath = desDirectory + File.separator + zipEntry.getName();
                    File file = new File(unzipFilePath);
                    // 创建父目录
                    mkdir(file.getParentFile());
                    // 写出文件流
                    BufferedOutputStream bufferedOutputStream =
                            new BufferedOutputStream(new FileOutputStream(unzipFilePath));
                    byte[] bytes = new byte[1024];
                    int readLen;
                    while ((readLen = zipInputStream.read(bytes)) != -1) {
                        bufferedOutputStream.write(bytes, 0, readLen);
                    }
                    bufferedOutputStream.close();
                }
                //关闭
                zipInputStream.closeEntry();
                zipEntry = zipInputStream.getNextEntry();
            }
            zipInputStream.close();
        }

        // 如果父目录不存在则创建
        private static void mkdir(File file) {
            //判断
            if (null == file || file.exists()) {
                return;
            }
            mkdir(file.getParentFile());
            //创建
            file.mkdir();
        }


    }

