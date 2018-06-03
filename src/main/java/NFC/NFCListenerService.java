package NFC;

import com.tinkerforge.BrickletNFC;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider for different services involving the NFC reader.
 * Initialize with the TinkerForge BrickletNFC and a NFCStorageHandler
 *
 * @see NFCStorageHandler
 */
public class NFCListenerService {

  /**
   * Service to explore new Tags
   */
  public BrickletNFC.ReaderStateChangedListener explorerService;
  public BrickletNFC.ReaderStateChangedListener passwordExplorer;
  public BrickletNFC.ReaderStateChangedListener dataExtractionService;
  public NFCStorageHandler storageHandler;
  private Logger logger = LoggerFactory.getLogger(this.getClass());
  private BrickletNFC nfc;
  private BrickletNFC.ReaderGetTagID ret;

  public NFCListenerService(BrickletNFC nfc, NFCStorageHandler sHandler) {
    this.nfc = nfc;
    this.storageHandler = sHandler;
  }

  public void registerServices() {
    explorerService = (state, idle) -> {
      if (state == BrickletNFC.READER_STATE_IDLE) {
        try {
          nfc.readerRequestTagID();
        } catch (Exception e) {
          return;
        }
      } else if (state == BrickletNFC.READER_STATE_REQUEST_TAG_ID_READY) {
        try {
          ret = nfc.readerGetTagID();
          storageHandler.createTag(ret);
        } catch (Exception e) {
          return;
        }
      }
    };
    passwordExplorer = new BrickletNFC.ReaderStateChangedListener() {
      private int commonPage = 0;
      private Pair<NFCData, PasswordService> candidate;
      private Logger anylogger = LoggerFactory.getLogger(logger.getName() + ".passwordExplorer");

      public void readerStateChanged(int state, boolean idle) {
        if (ret == null) {
          return;
        } else if (state == BrickletNFC.READER_STATE_REQUEST_TAG_ID_READY) {
          try {
            if (ret.tagType != BrickletNFC.TAG_TYPE_MIFARE_CLASSIC) {
              return;
            }
            candidate = storageHandler.getDataPair(ret);
            nfc.readerAuthenticateMifareClassicPage(commonPage, 0, candidate.getValue1().enumeratePassWord());
          } catch (Exception e) {
            return;
          }
        } else if (state == BrickletNFC.READER_STATE_AUTHENTICATE_MIFARE_CLASSIC_PAGE_READY) {
          try {
            candidate = storageHandler.getDataPair(ret);
            int[] foundPW = candidate.getValue1().getPassword();
            candidate.getValue0().setPassword(foundPW);
            String tagId = generateTagIdString(ret);
            anylogger.info(String.format("Password found: %s - for Tag ID [%s]", buildPassword(foundPW), tagId));
            if (commonPage < 4) {
              nfc.readerRequestPage(commonPage, 16);
            }
          } catch (TimeoutException | NotConnectedException e) {
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
    };
    dataExtractionService = new BrickletNFC.ReaderStateChangedListener() {
      private int commonPage = 0;
      private Logger anylogger = LoggerFactory.getLogger(logger.getName() + ".dataExtractionService");

      public void readerStateChanged(int state, boolean idle) {
        if (state == BrickletNFC.READER_STATE_REQUEST_TAG_ID_READY) {
          try {
            Pair<NFCData, PasswordService> candidate = storageHandler.getDataPair(ret);
            if (candidate.getValue0().passwordNotExisting()) {
              anylogger.info(String.format("Tag Password not found for Tag [%s]", generateTagIdString(ret)));
              return;
            } else if (commonPage < 4) {
              nfc.readerAuthenticateMifareClassicPage(commonPage, 0, candidate.getValue1().getPassword());
            } else {
              commonPage = 0;
            }
          } catch (Exception e) {
            return;
          }
        } else if (state == BrickletNFC.READER_STATE_AUTHENTICATE_MIFARE_CLASSIC_PAGE_READY) {
          try {
            Pair<NFCData, PasswordService> candidate = storageHandler.getDataPair(ret);
            nfc.readerRequestPage(commonPage, 16);
          } catch (TimeoutException | NotConnectedException e) {
            e.printStackTrace();
          }
        } else if (state == BrickletNFC.READER_STATE_AUTHENTICATE_MIFARE_CLASSIC_PAGE_ERROR) {
          try {
            Pair<NFCData, PasswordService> candidate = storageHandler.getDataPair(ret);
            candidate.getValue0().setPassword(new int[]{-1, -1, -1, -1});
            candidate.getValue1().setEnumeration(0);
            nfc.readerRequestTagID();
          } catch (Exception e) {
            return;
          }
        } else if (state == BrickletNFC.READER_STATE_REQUEST_PAGE_READY) {
          try {
            int[] page = nfc.readerReadPage();
            anylogger.info(NFCUtil.printTagData(page, commonPage));
            storageHandler.insertPageToTag(nfc.readerGetTagID(), commonPage, page);
            commonPage++;
            if (commonPage < 4) {
              nfc.readerRequestPage(commonPage, 16);
            } else {
              commonPage = 0;
            }
          } catch (Exception e) {
            return;
          }
        }
      }
    };
  }

  private String generateTagIdString(BrickletNFC.ReaderGetTagID ret) {
    return NFCUtil.buildStringWithSeperationFromIntArray(ret.tagID);
  }

  public void setNfc(BrickletNFC nfc) {
    this.nfc = nfc;
  }

  private String buildPassword(int[] pw) {
    return NFCUtil.buildStringWithSeperationFromIntArray(pw);
  }

}
