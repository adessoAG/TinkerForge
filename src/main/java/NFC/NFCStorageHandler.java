package NFC;

import com.tinkerforge.BrickletNFC;

import java.util.HashMap;

public class NFCStorageHandler {
  private static final String pathName = "C:\\Users\\hoefken\\Desktop\\nfc.ser";
  private static final boolean printDiff = true;
  private HashMap<String, NFCData> dataObject;

  public NFCStorageHandler(boolean loadSaves) {
    if (loadSaves) {  NFCTagLoader.deserializeTagData(pathName);}
    if (dataObject == null) {
      System.out.println("Error on loading Tag data.");
      dataObject = new HashMap<String, NFCData>();
    }
  }

  public NFCData createTag(BrickletNFC.ReaderGetTagID ret, int[] password) {
    String key = NFCUtil.getIdFromInt(ret.tagID);
    NFCData candidate = dataObject.get(key);
    if (candidate == null) {
      candidate = dataObject.put(key, new NFCData(password).setTagId(ret.tagID).setTagType(ret.tagType));
    }
    return candidate;
  }

  public NFCData createTag(BrickletNFC.ReaderGetTagID ret) {
    return createTag(ret, new int[]{-1,-1,-1,-1});
  }

  public NFCData insertPagesToTag(BrickletNFC.ReaderGetTagID ret, int pageNumber, int[] pageData) {
    NFCData candidate = dataObject.get(NFCUtil.getIdFromInt(ret.tagID));
    if (candidate != null) {
      if (printDiff) {
        checkChange(candidate, pageNumber, pageData);
      }
      candidate.setPageDataAtPage(pageNumber, pageData);
    } else {
      createTag(ret);
      candidate = insertPagesToTag(ret, pageNumber, pageData);
      System.out.println("Tag not found. New Tag created.");
    }
    return candidate;
  }

  private void checkChange(NFCData old, int pageNumber, int[] pageData) {
    int[] oldData = old.getPageDataAtPage(pageNumber);
    String result = "There are Changes in your Dataset for Tag[" + old.getTagIdAsString() + "] at Page " + pageNumber + " in Byte ";
    for (int i = 0; i < 16; i++) {
      if (oldData[i] != pageData[i]) {
        result = result.concat(i + ", ");
      }
    }
    if (result.contains(",")) {
      System.out.println(result.substring(0, result.length() - 1));
    }
  }


  public void saveTags() {
    NFCTagLoader.serializeTagData(dataObject, pathName);
  }
}
