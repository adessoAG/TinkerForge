package NFC;

import com.tinkerforge.BrickletNFC;

public class NFCUtil {

  public static String buildStringWithSeperationFromIntArray(int[] input){
    String password = "";
    for (int step: input){
      password += step +":";
    }
    return password.substring(0,password.length()-1);
  }

  public static String buildTagIdFromRet(BrickletNFC.ReaderGetTagID ret){
    int i = 0;
    StringBuilder tag = new StringBuilder();
    for (int v : ret.tagID) {
      if (i < ret.tagID.length - 1) {
        tag.append(String.format("0x%X ", v));
      }
      else {
        tag.append(String.format("0x%X", v));
      }
      i++;
    }
    return tag.toString();
  }


  public static String getIdFromInt(int[] input){
    String result = "";
    for( int candidate: input){
      result = result.concat(candidate + ":");
    }
    return result.substring(0,result.length()-1);
  }

  public static String getReadableId(BrickletNFC.ReaderGetTagID ret){
    int i = 0;
    StringBuilder tag = new StringBuilder();
    for (int v : ret.tagID) {
      if (i < ret.tagID.length - 1) {
        tag.append(String.format("%X:", v));
      }
      else {
        tag.append(String.format("%X", v));
      }
      i++;
    }
    return tag.toString();
  }
}
