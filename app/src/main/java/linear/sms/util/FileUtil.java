package linear.sms.util;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZCYL on 2018/5/17.
 */
public class FileUtil {

    private static String BLACK_CONTACT = "black.txt";
    private static List<String> sBlackContact;

//    static {
//        String s = Environment.getExternalStorageDirectory() + "/linear";
//        File f = new File(s);
//        f.mkdirs();
//    }
//
//    public static void saveBlackContact(String contact) {
//        if (sBlackContact == null){
//            sBlackContact = new ArrayList<>();
//        }
//        sBlackContact.add(contact);
//        saveToLocal(BLACK_CONTACT, sBlackContact);
//    }
//
//    public static void removeBlackContact(String contact){
//        sBlackContact.remove(contact);
//        saveToLocal(BLACK_CONTACT, sBlackContact);
//    }

//    public static List<String> readBlackContact() {
//        if (sBlackContact == null) {
//            sBlackContact = readFromLocal(BLACK_CONTACT);
//        }
//        return sBlackContact;
//    }

    private static void saveToLocal(String fileName, List<String> contact) {
        String path = Environment.getExternalStorageDirectory() + "/linear/" + fileName;
        File f = new File(path);
        if (f.exists()){
            f.delete();
        }
        try {
            f.createNewFile();
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
            oos.writeObject(contact);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String> readFromLocal(String fileName) {
        List<String> resultList = new ArrayList<>();
        String path = Environment.getExternalStorageDirectory() + "/linear/" + fileName;
        File f = new File(path);
        if (!f.exists()){
            return resultList;
        }
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
            List<String> list_ext = (List<String>) ois.readObject();

            for (String obj : list_ext) {
                if (obj != null) {
                    resultList.add(obj);
                }
            }
            ois.close();
        } catch (Exception e) {
            return resultList;
        }
        return resultList;
    }

}
