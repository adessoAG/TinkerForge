package NFC;

import com.tinkerforge.BrickletNFC;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

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
  private BrickletNFC nfcBrick;
  private BrickletNFC.ReaderGetTagID rfidTag;
  private ArrayList<int[]> TagIDsToListenTo;

  public NFCListenerService(BrickletNFC nfc, NFCStorageHandler sHandler) {
    this.nfcBrick = nfc;
    this.storageHandler = sHandler;
  }

  /**
   * !!!Caution: Some Services need information from one another. For Example @rfidTag is used in the passwordExplorer
   * !!!         and is set by the explorerService.
   * <p>
   * As always you can extend or improve all the strategies for your needs.
   * These are just example uses to load for your application.
   */
  public void registerServices() {

    /**
     * An Explorer Service to look out for new Tags and storing them in the storageHandler
     */
    explorerService = (state, idle) -> {
      Logger anylogger = LoggerFactory.getLogger(logger.getName() + ".explorerService");
      if (state == BrickletNFC.READER_STATE_IDLE) {
        try {
          nfcBrick.readerRequestTagID();
        } catch (Exception e) {
          return;
        }
      } else if (state == BrickletNFC.READER_STATE_REQUEST_TAG_ID_READY) {
        try {
          rfidTag = nfcBrick.readerGetTagID();
          storageHandler.createTag(rfidTag);
        } catch (Exception e) {
          return;
        }
      }
      /**
       * These three else parts are optional, if you like to use them elsewhere you should delete them here.
       */
      else if (state == BrickletNFC.READER_STATE_WRITE_PAGE_READY) {
        anylogger.info("Write page ready");
      } else if (state == BrickletNFC.READER_STATE_REQUEST_PAGE_ERROR) {
        anylogger.info("Request page error");
      } else if (state == BrickletNFC.READER_STATE_WRITE_PAGE_ERROR) {
        anylogger.info("Write page error");
      }
    };

    /**
     * !!!Caution: Only MiFare Classic Tags are secured with a password!!!
     * An Password Explorer to solve passwords for given Tags.
     */
    passwordExplorer = new BrickletNFC.ReaderStateChangedListener() {
      private int pageToTryPasswordOn = 0;
      private Pair<NFCData, PasswordService> candidate;
      private Logger anylogger = LoggerFactory.getLogger(logger.getName() + ".passwordExplorer");

      public void readerStateChanged(int state, boolean idle) {
        if (rfidTag == null) {
          return;
        } else if (shouldIListenToThisTag(rfidTag)){
          candidate = storageHandler.getDataPair(rfidTag);
        }
        if (state == BrickletNFC.READER_STATE_REQUEST_TAG_ID_READY) {
          try {
            if (candidate.getValue0().passwordNotExisting()) {
              nfcBrick.readerAuthenticateMifareClassicPage(pageToTryPasswordOn, 0, candidate.getValue1().enumeratePassWord());
            } else {
              //TODO - If password is already found. What should I do now?
            }
          } catch (Exception e) {
            return;
          }
        } else if (state == BrickletNFC.READER_STATE_AUTHENTICATE_MIFARE_CLASSIC_PAGE_READY) {
//         At this point the password is resolved and the password is set in the related storage files

          int[] foundPW = candidate.getValue1().getPassword();
          candidate.getValue0().setPassword(foundPW);
          String tagId = generateTagIdString(rfidTag);
          anylogger.info(String.format("Password found: %s - for Tag ID [%s]", buildPassword(foundPW), tagId));

        } else if (state == BrickletNFC.READER_STATE_AUTHENTICATE_MIFARE_CLASSIC_PAGE_ERROR) {
//          If the password is rejected restart the reader.
//          System.out.format("Password %s failed\n", buildPassword());
          try {
            nfcBrick.readerRequestTagID();
          } catch (Exception e) {
            return;
          }
        }
      }
    };

    /**
     * Data extraction Service to extract data from the Tag. This Service only works if there is a DataPair registered
     * and a password is found for that Tag.
     */
    dataExtractionService = new BrickletNFC.ReaderStateChangedListener() {
      private int pageToTryPasswordOn = 0;
      Pair<NFCData, PasswordService> candidate;
      private Logger anylogger = LoggerFactory.getLogger(logger.getName() + ".dataExtractionService");

      public void readerStateChanged(int state, boolean idle) {
        if (rfidTag == null) {
          return;
        } else if (shouldIListenToThisTag(rfidTag)){
          candidate = storageHandler.getDataPair(rfidTag);
        }
        if (state == BrickletNFC.READER_STATE_REQUEST_TAG_ID_READY) {
          try {
            if (candidate.getValue0().passwordNotExisting()) {
              anylogger.info(String.format("Tag Password not found for Tag [%s]", generateTagIdString(rfidTag)));
              return;
            } else if (pageToTryPasswordOn < 4) {
              nfcBrick.readerAuthenticateMifareClassicPage(pageToTryPasswordOn, 0, candidate.getValue1().getPassword());
            } else {
              pageToTryPasswordOn = 0;
              nfcBrick.readerRequestTagID();
            }
          } catch (Exception e) {
            return;
          }
        } else if (state == BrickletNFC.READER_STATE_AUTHENTICATE_MIFARE_CLASSIC_PAGE_READY) {
          try {
            nfcBrick.readerRequestPage(pageToTryPasswordOn, 16);
          } catch (TimeoutException | NotConnectedException e) {
            e.printStackTrace();
          }
        } else if (state == BrickletNFC.READER_STATE_AUTHENTICATE_MIFARE_CLASSIC_PAGE_ERROR) {
          try {
//          If the provided password is wrong reset it in the data file and reset the reader.
            candidate.getValue0().setPassword(new int[]{-1, -1, -1, -1});
            candidate.getValue1().setEnumeration(0);
            nfcBrick.readerRequestTagID();
          } catch (Exception e) {
            return;
          }
        } else if (state == BrickletNFC.READER_STATE_REQUEST_PAGE_READY) {
          try {
            int[] page = nfcBrick.readerReadPage();
            if (storageHandler.insertPageToTag(nfcBrick.readerGetTagID(), pageToTryPasswordOn, page)) {
              anylogger.info(NFCUtil.returnPrintTagData(page, pageToTryPasswordOn));
            }
            pageToTryPasswordOn++;
            if (pageToTryPasswordOn < 4) {
              nfcBrick.readerRequestPage(pageToTryPasswordOn, 16);
            } else {
              pageToTryPasswordOn = 0;
              nfcBrick.readerRequestTagID();
            }
          } catch (Exception e) {
            return;
          }
        }
      }
    };
  }

  private boolean shouldIListenToThisTag(BrickletNFC.ReaderGetTagID tag){
    if(this.TagIDsToListenTo.isEmpty()) {
      return true;
    } else {
      return this.TagIDsToListenTo.contains(tag.tagID);
    }
  }

  private String generateTagIdString(BrickletNFC.ReaderGetTagID ret) {
    return NFCUtil.buildStringWithSeperationFromIntArray(ret.tagID);
  }

  private String buildPassword(int[] pw) {
    return NFCUtil.buildStringWithSeperationFromIntArray(pw);
  }

  public void setNfcBrick(BrickletNFC nfcBrick) { this.nfcBrick = nfcBrick; }

  public ArrayList<int[]> getTagIDsToListenTo() { return TagIDsToListenTo; }

  public void setTagIDsToListenTo(ArrayList<int[]> tagIDsToListenTo) { TagIDsToListenTo = tagIDsToListenTo; }

  public void addThisTagToListenTo(int[] newTagId){
    if (!this.TagIDsToListenTo.contains(newTagId)){
      this.TagIDsToListenTo.add(newTagId);
    }
  }

  public void removeThisTagToListenTo(int[] newTagId){
    if (this.TagIDsToListenTo.contains(newTagId)){
      this.TagIDsToListenTo.remove(newTagId);
    }
  }
}
