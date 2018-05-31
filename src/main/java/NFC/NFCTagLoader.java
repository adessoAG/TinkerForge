package NFC;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class NFCTagLoader {

    static HashMap<String, NFCData> deserializeTagData(String pathName) {
        FileInputStream fin = null;
        ObjectInputStream ois = null;
        HashMap<String, NFCData> result = null;
        try {
            fin = new FileInputStream(pathName);
            ois = new ObjectInputStream(fin);
            result = (HashMap<String, NFCData>) ois.readObject();
        } catch (Exception ex) {
//      ex.printStackTrace();
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
//    return result;
        return new HashMap<String, NFCData>();
    }

    static void serializeTagData(HashMap<String, NFCData> toSave, String pathName) {
        Logger logger = LoggerFactory.getLogger("NFCTagLoader");
        FileOutputStream fout = null;
        ObjectOutputStream oos = null;
        try {
            fout = new FileOutputStream(pathName);
            oos = new ObjectOutputStream(fout);
            oos.writeObject(toSave);
            logger.info("File saved.");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
