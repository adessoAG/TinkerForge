package NFC;

import com.tinkerforge.BrickletNFC;

public class NFCUtil {

  public static String printTagData(int[] page, int pageNumber) {
    StringBuilder result = new StringBuilder("\n");
    result.append(String.format("Page %s : \t   A\t   B\t   C\t   D   \n", pageNumber));
    result.append(String.format("Byte 0-3:\t0x%X \t0x%X \t0x%X \t0x%X\n", page[0], page[1], page[2], page[3]));
    result.append(String.format("Byte 4-7:\t0x%X \t0x%X \t0x%X \t0x%X\n", page[4], page[5], page[6], page[7]));
    result.append(String.format("Byte 8-11:\t0x%X \t0x%X \t0x%X \t0x%X\n", page[8], page[9], page[10], page[11]));
    result.append(String.format("Byte 12-15:\t0x%X \t0x%X \t0x%X \t0x%X\n", page[12], page[13], page[14], page[15]));
    return result.toString();
  }

  public static String buildStringWithSeperationFromIntArray(int[] input) {
    String password = "";
    for (int step : input) {
      password += step + ":";
    }
    return password.substring(0, password.length() - 1);
  }

  public static String buildTagIdFromRet(BrickletNFC.ReaderGetTagID ret) {
    int i = 0;
    StringBuilder tag = new StringBuilder();
    for (int v : ret.tagID) {
      if (i < ret.tagID.length - 1) {
        tag.append(String.format("0x%X ", v));
      } else {
        tag.append(String.format("0x%X", v));
      }
      i++;
    }
    return tag.toString();
  }


  public static String getIdFromInt(int[] input) {
    String result = "";
    for (int candidate : input) {
      result = result.concat(candidate + ":");
    }
    return result.substring(0, result.length() - 1);
  }

  public static String getReadableId(BrickletNFC.ReaderGetTagID ret) {
    int i = 0;
    StringBuilder tag = new StringBuilder();
    for (int v : ret.tagID) {
      if (i < ret.tagID.length - 1) {
        tag.append(String.format("%X:", v));
      } else {
        tag.append(String.format("%X", v));
      }
      i++;
    }
    return tag.toString();
  }
}
