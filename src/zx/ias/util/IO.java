package zx.ias.util;

import java.io.*;

/**
 * IOUtil
 */

public class IO {

    /**
     * 将序列化对象转为文件
     *
     * @param filePath 文件地址
     * @param object   实现Serializable接口的对象
     * @return 是否成功
     */
    public static boolean obj2File(String filePath, Object object) {
        File file = new File(filePath);
        System.out.println(object.getClass());
        if (!Serializable.class.isAssignableFrom(object.getClass())) return false;
        try {
            if (!file.exists()) if (!file.createNewFile()) return false;
            FileOutputStream fileOut = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(object);
            out.close();
            fileOut.close();
            return true;
        } catch (IOException i) {
            i.printStackTrace();
        }
        return false;
    }

    /**
     * 文件转为对象
     *
     * @param filePath 文件路径
     * @return 对象
     */
    public static Object file2Obj(String filePath) {
        try {
            FileInputStream fileIn = new FileInputStream(filePath);
            ObjectInputStream objIn = new ObjectInputStream(fileIn);
            return objIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 写入文本到文件
     *
     * @param text 文本
     * @param path 文件路径
     */
    public static void writeTxtFile(String text, String path) {
        try {
            File file = new File(path);
            File dir = new File(path.subSequence(0, path.lastIndexOf(File.separator)).toString());
            if (!dir.exists()) {
                DebugLog.i("mkdir:" + path + "," + dir.mkdirs());
            }
            FileOutputStream fos = new FileOutputStream(file);
            byte[] bytes = text.getBytes();
            fos.write(bytes);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
