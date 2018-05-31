package custom;

import NFC.NFCStorageHandler;
import NFC.NFCUtil;
import NFC.PasswordService;
import com.tinkerforge.*;

import java.io.*;

public class tester {
  private static final String HOST = "localhost";
  private static final int PORT = 4223;
  private static final String UID = "6rGJtJ"; // Change to your UID
  private int commonCounter = 0;
  private int commonPage = 0;
  private IPConnection ipcon = new IPConnection();
  private BrickletNFC nfc;
  private Thread monitorThread;
  private BrickletNFC.ReaderStateChangedListener listener;
  private NFCStorageHandler tagData = new NFCStorageHandler(true);
  private PasswordService pwService = new PasswordService();


  public tester() {
  }

  public void exe(){
    nfc =  setupNFC.exe(ipcon);
    monitor myMonitor = new monitor(this);
    monitorThread = new Thread(myMonitor);
//    monitorThread.start();
    listener = new BrickletNFC.ReaderStateChangedListener() {
      public void readerStateChanged(int state, boolean idle) {
        if (state == BrickletNFC.READER_STATE_IDLE) {
          try {
            nfc.readerRequestTagID();
          } catch (Exception e) {
            return;
          }
        }
        else if(state == BrickletNFC.READER_STATE_REQUEST_TAG_ID_READY) {
          try{
            BrickletNFC.ReaderGetTagID ret = nfc.readerGetTagID();

            if (ret.tagType != BrickletNFC.TAG_TYPE_MIFARE_CLASSIC) {
              System.out.println("Tag is not MiFare");

              return;
            }
            if (commonPage < 4) {
              nfc.readerAuthenticateMifareClassicPage(commonPage, 0, enumeratePassWord());
              commonCounter++;
            } else {
              commonCounter = 0;
              commonPage = 0;
              bruteforce();
            }
          }
          catch (Exception e) {
            return;
          }
        }
        else if(state == BrickletNFC.READER_STATE_AUTHENTICATE_MIFARE_CLASSIC_PAGE_READY) {
          try {
            System.out.format("Password found: %s - for Tag ID [%s]\n", buildPassword(), NFCUtil.getReadableId(nfc.readerGetTagID()));
            tagData.createTag(nfc.readerGetTagID(), pwService.getPassword());
            nfc.readerRequestPage(commonPage,16);

          } catch (TimeoutException e) {
            e.printStackTrace();
          } catch (NotConnectedException e) {
            e.printStackTrace();
          }
        }
        else if(state == BrickletNFC.READER_STATE_AUTHENTICATE_MIFARE_CLASSIC_PAGE_ERROR) {
//          System.out.format("Password %s failed\n", buildPassword());
          try {
            nfc.readerRequestTagID();
          } catch (Exception e) {
            return;
          }
        }
        else if (state == BrickletNFC.READER_STATE_REQUEST_TAG_ID_ERROR) {
          System.out.println("Request tag ID error");
        }
        else if (state == BrickletNFC.READER_STATE_REQUEST_PAGE_READY) {
          try {
              int[] page = nfc.readerReadPage();
              System.out.format("Page %s : \t   A\t   B\t   C\t   D   \n", commonPage);
              System.out.format("Byte 0-3:\t0x%X \t0x%X \t0x%X \t0x%X\n", page[0], page[1], page[2], page[3]);
              System.out.format("Byte 4-7:\t0x%X \t0x%X \t0x%X \t0x%X\n", page[4], page[5], page[6], page[7]);
              System.out.format("Byte 8-11:\t0x%X \t0x%X \t0x%X \t0x%X\n", page[8], page[9], page[10], page[11]);
              System.out.format("Byte 12-15:\t0x%X \t0x%X \t0x%X \t0x%X\n", page[12], page[13], page[14], page[15]);
              tagData.insertPagesToTag(nfc.readerGetTagID(), commonPage, page);
              commonPage++;
            if (commonPage < 4) {
              nfc.readerRequestPage(commonPage, 16);
            } else { commonPage = 0; }
          }
          catch (Exception e) {
            return;
          }
        }
        else if (state == BrickletNFC.READER_STATE_WRITE_PAGE_READY) {
          System.out.println("Write page ready");
        }
        else if (state == BrickletNFC.READER_STATE_REQUEST_PAGE_ERROR) {
          System.out.println("Request page error");
        }
        else if (state == BrickletNFC.READER_STATE_WRITE_PAGE_ERROR) {
          System.out.println("Write page error");
        }
      }
    };
    try {
      ipcon.connect(HOST, PORT);
    } catch (AlreadyConnectedException e) {
      e.printStackTrace();
    } catch (NetworkException e) {
      e.printStackTrace();
    }
    System.out.println("connected.");
    nfc.addReaderStateChangedListener(listener);
    try {
      nfc.setMode(BrickletNFC.MODE_READER);

      System.out.println("Press key to exit"); System.in.read();
      ipcon.disconnect();
      tagData.saveTags();
    } catch (NotConnectedException e) {
      e.printStackTrace();
    } catch (TimeoutException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }




  private void bruteforce(){
    nfc.removeReaderStateChangedListener(listener);
  }

  public String buildPassword(){
    return NFCUtil.buildStringWithSeperationFromIntArray(pwService.getPassword());
  }

  private int[] enumeratePassWord() {
    return pwService.enumeratePassWord(commonCounter);
  }
}
