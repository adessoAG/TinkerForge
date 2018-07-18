package NFC;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;


/**
 * Class to load and save a HashMap of Tag data to the local storage on the disk with the provided path/filename from
 * the application.properties.
 */
public class NFCTagLoader {

  static HashMap<String, Pair<NFCData, PasswordService>> deserializeTagData(String pathName) {
    FileInputStream fin = null;
    ObjectInputStream ois = null;
    HashMap<String, Pair<NFCData, PasswordService>> result = null;
    try {
      fin = new FileInputStream(pathName);
      ois = new ObjectInputStream(fin);
      result = (HashMap<String, Pair<NFCData, PasswordService>>) ois.readObject();
    } catch (Exception ex) {
      ex.printStackTrace();
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
    return result;
  }

  static void serializeTagData(HashMap<String, Pair<NFCData, PasswordService>> toSave, String pathName) {
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
