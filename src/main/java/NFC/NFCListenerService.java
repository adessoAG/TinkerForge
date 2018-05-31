package NFC;

import com.tinkerforge.BrickletNFC;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import java.util.HashMap;

public class NFCListenerService {
  private BrickletNFC nfc;
  public BrickletNFC.ReaderStateChangedListener explorerService;
  public BrickletNFC.ReaderStateChangedListener passwordExplorer;
  public NFCStorageHandler storageHandler;
  private HashMap<String, BrickletNFC.ReaderStateChangedListener> passwordStorage = new HashMap<String, BrickletNFC.ReaderStateChangedListener>();

  public NFCListenerService(BrickletNFC nfc, NFCStorageHandler sHandler) {
    this.nfc = nfc;
    this.storageHandler = sHandler;
  }

  private void registerServices() {
    explorerService = new BrickletNFC.ReaderStateChangedListener() {
      public void readerStateChanged(int state, boolean idle) {
        if (state == BrickletNFC.READER_STATE_IDLE) {
          try {
            nfc.readerRequestTagID();
          } catch (Exception e) {
            return;
          }
        } else if (state == BrickletNFC.READER_STATE_REQUEST_TAG_ID_READY) {
          try {
            BrickletNFC.ReaderGetTagID ret = nfc.readerGetTagID();
            storageHandler.createTag(ret);
          } catch (Exception e) {
            return;
          }
        }
      }
    };
    passwordExplorer = new BrickletNFC.ReaderStateChangedListener() {
      private int commonCounter = 0;
      private int commonPage = 0;
      private PasswordService pwService = new PasswordService();

      public void readerStateChanged(int state, boolean idle) {
        if (state == BrickletNFC.READER_STATE_REQUEST_TAG_ID_READY) {
          try {
            BrickletNFC.ReaderGetTagID ret = nfc.readerGetTagID();
            if (ret.tagType != BrickletNFC.TAG_TYPE_MIFARE_CLASSIC) {
              return;
            }
            nfc.readerAuthenticateMifareClassicPage(commonPage, 0, pwService.enumeratePassWord(commonCounter++));
          } catch (Exception e) {
            return;
          }
        } else if (state == BrickletNFC.READER_STATE_AUTHENTICATE_MIFARE_CLASSIC_PAGE_READY) {
          try {
            String tagId = generateTagIdString(nfc.readerGetTagID());
            int[] foundPW = pwService.getPassword();
            System.out.format("Password found: %s - for Tag ID [%s]\n", buildPassword(foundPW), tagId);
            storageHandler.createTag(nfc.readerGetTagID(), foundPW);
            if (commonPage < 4) {
              nfc.readerRequestPage(commonPage, 16);
            }
          } catch (TimeoutException e) {
            e.printStackTrace();
          } catch (NotConnectedException e) {
            e.printStackTrace();
          }
        } else if (state == BrickletNFC.READER_STATE_AUTHENTICATE_MIFARE_CLASSIC_PAGE_ERROR) {
//          System.out.format("Password %s failed\n", buildPassword());
          try {
            nfc.readerRequestTagID();
          } catch (Exception e) {
            return;
          }
        }
      }
    }

    ;
  }

  String generateTagIdString(BrickletNFC.ReaderGetTagID ret) {
    return NFCUtil.buildStringWithSeperationFromIntArray(ret.tagID);
  }

  public void setNfc(BrickletNFC nfc) {
    this.nfc = nfc;
  }

  String buildPassword(int[] pw) {
    return NFCUtil.buildStringWithSeperationFromIntArray(pw);
  }

}
