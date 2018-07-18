package NFC;

import com.tinkerforge.BrickletNFC;

import java.io.Serializable;

/**
 * Tag object with information about the tag type, the underlying data structure of an MiFare Tag and the password
 * to read the pages of that Tag. The Tag consists of 4 Pages with 16 Bytes.
 * Page 0 contains Tag information from producer, see @https://learn.adafruit.com/adafruit-pn532-rfid-nfc/mifare
 * for more information about that topic.
 */
public class NFCData implements Serializable {

  private int TagType = -1;
  private int[][] pageData = new int[4][16];
  private int[] password = new int[]{-1, -1, -1, -1};

  public NFCData() {
  }

  public NFCData(BrickletNFC.ReaderGetTagID ret) {
    setTagId(ret.tagID);
    setTagType(ret.tagType);
  }

  public NFCData(int[] password) {
    this.password = password;
  }

  public int getTagType() {
    return TagType;
  }

  NFCData setTagType(int tagType) {
    TagType = tagType;
    return this;
  }

  public int[] getTagId() {
    return new int[]{pageData[0][0], pageData[0][1], pageData[0][2], pageData[0][3]};
  }

  public NFCData setTagId(BrickletNFC.ReaderGetTagID ret) {
    for (int i = 0; i < 3; i++) {
      pageData[0][i] = ret.tagID[i];
    }
    return this;
  }

  public NFCData setTagId(int[] tagId) {
    for (int i = 0; i < 3; i++) {
      pageData[0][i] = tagId[i];
    }
    return this;
  }

  public boolean passwordNotExisting() {
    return password[0] == -1 || password[1] == -1 || password[2] == -1 || password[3] == -1;
  }

  public boolean passwordExisting() {
    return !passwordNotExisting();
  }

  /**
   * prettify the Id to readable String with format HH:HH:HH:HH (H = Hex)
   */
  public String getTagIdAsString() {
    StringBuilder tag = new StringBuilder();
    for (int i = 0; i < 3; i++) {
      tag.append(String.format("%X:", pageData[0][i]));
    }
    tag.append(String.format("%X", pageData[0][4]));
    return tag.toString();
  }

  public int[] getPassword() {
    return password;
  }

  public void setPassword(int[] password) {
    this.password = password;
  }

  public int[][] getPageData() {
    return pageData;
  }

  public void setPageData(int[][] pageData) {
    this.pageData = pageData;
  }

  public int[] getPageDataAtPage(int page) {
    return pageData[page];
  }

  public NFCData setPageDataAtPage(int page, int[] pageData) {
    this.pageData[page] = pageData;
    return this;
  }

}
