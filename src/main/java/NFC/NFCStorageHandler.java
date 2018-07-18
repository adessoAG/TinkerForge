package NFC;

import com.tinkerforge.BrickletNFC;
import custom.ConfigurationService;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;

/**
 * Storage Handler to store all readings of the NFCReader into an Hashmap locally.
 * There is the option to debug print changes of the data of already explored Tags.
 */
@Component
public class NFCStorageHandler {

  @Autowired(required = true)
  private ConfigurationService configService;
  private Logger logger = LoggerFactory.getLogger(this.getClass());
  private HashMap<String, Pair<NFCData, PasswordService>> dataObject;


  @PostConstruct
  public void initIt() {
    if (configService.shouldILoadNFCData()) {
      NFCTagLoader.deserializeTagData(configService.getPathname());
      if (dataObject == null) {
        logger.info("Error on loading Tag data.");
      }
    }
    if (dataObject == null) {
      dataObject = new HashMap<>();
    }
  }

  @PreDestroy
  public void cleanUp() throws Exception {
    if (configService.shouldILoadNFCData()) {
      saveTags();
    }
  }

  public Pair<NFCData, PasswordService> getDataPair(BrickletNFC.ReaderGetTagID ret) {
    String key = NFCUtil.getIdFromInt(ret.tagID);
    Pair<NFCData, PasswordService> candidate = dataObject.get(key);
    if (candidate == null) {
      candidate = new Pair<>(new NFCData(ret), new PasswordService(ret.tagID));
      dataObject.put(key, candidate);
    }
    return candidate;
  }

  public Pair<NFCData, PasswordService> createTag(BrickletNFC.ReaderGetTagID ret, Pair<NFCData, PasswordService> dataPair) {
    String key = NFCUtil.getIdFromInt(ret.tagID);
    Pair<NFCData, PasswordService> candidate = dataObject.get(key);
    if (candidate == null) {
      candidate = dataObject.put(key, dataPair);
    }
    return candidate;
  }

  public Pair<NFCData, PasswordService> createTag(BrickletNFC.ReaderGetTagID ret) {
    return createTag(ret, new Pair<>(new NFCData().setTagId(ret), new PasswordService(ret.tagID)));
  }

  public boolean insertPageToTag(BrickletNFC.ReaderGetTagID ret, int pageNumber, int[] pageData) {
    Pair<NFCData, PasswordService> dataPair = dataObject.get(NFCUtil.getIdFromInt(ret.tagID));
    NFCData candidate = dataPair.getValue0();
    boolean changed = false;
    if (candidate != null) {
      if (configService.shouldIPrintDiff()) {
        changed = checkChange(candidate, pageNumber, pageData);
      }
      candidate.setPageDataAtPage(pageNumber, pageData);
      dataPair.setAt0(candidate);
      dataObject.replace(NFCUtil.getIdFromInt(ret.tagID), dataPair);
    } else {
      createTag(ret);
      logger.info("Tag not found. New Tag created.");
      return insertPageToTag(ret, pageNumber, pageData);
    }
    return changed;
  }

  private boolean checkChange(NFCData old, int pageNumber, int[] pageData) {
    int[] oldData = old.getPageDataAtPage(pageNumber);
    String result = "There are changes in your dataset for Tag[" + old.getTagIdAsString() + "] at page " + pageNumber + " in Byte(s) ";
    boolean changed = false;
    for (int i = 0; i < 16; i++) {
      if (oldData[i] != pageData[i]) {
        result = result.concat(i + ", ");
        changed = true;
      }
    }
    if (result.contains(",")) {
      logger.info(result.substring(0, result.length() - 2));
    }
    return changed;
  }

  private boolean isChanged(NFCData old, int pageNumber, int[] pageData) {
    int[] oldData = old.getPageDataAtPage(pageNumber);
    for (int i = 0; i < 16; i++) {
      if (oldData[i] != pageData[i]) {
        return true;
      }
    }
    return false;
  }

  public void saveTags() {
    NFCTagLoader.serializeTagData(dataObject, configService.getPathname());
  }
}
