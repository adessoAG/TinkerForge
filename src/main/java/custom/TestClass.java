package custom;

import NFC.NFCListenerService;
import NFC.NFCStorageHandler;
import NFC.RegisterBrickService;
import com.tinkerforge.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This is the main class to code in. Just throw some code in the @exe function.
 */
@Service
public class TestClass {

  @Autowired
  private ConfigurationService configService;

  @Autowired
  private RegisterBrickService nfcRegister;

  @Autowired
  private NFCStorageHandler storageHandler;

  private Logger logger = LoggerFactory.getLogger(this.getClass());
  private Monitor monitorThread = new Monitor(this);
  private IPConnection ipcon = new IPConnection();
  private BrickletNFC nfc;
  private NFCListenerService listenerService;
  private ArrayList<BrickletNFC.ReaderStateChangedListener> activeListener = new ArrayList<>();

  /**
   * Example with use of some NFC Services.
   */
  public void exe() {
    try {
      listenerService.registerServices();
      activeListener.add(listenerService.explorerService);
      activeListener.add(listenerService.dataExtractionService);
      activeListener.add(listenerService.passwordExplorer);
      activeListener.forEach((x) -> nfc.addReaderStateChangedListener(x));
      nfc.setMode(BrickletNFC.MODE_READER);
      logger.info("Press key to exit.");
      System.in.read();
    } catch (NotConnectedException | TimeoutException | IOException e) {
      e.printStackTrace();
    }
  }

  @PostConstruct
  public void initIt() {
    logger.info("Connecting to Brick...");
    nfc = nfcRegister.exe(ipcon);
    try {
      ipcon.connect(configService.getHostname(), configService.getPort());
    } catch (AlreadyConnectedException | NetworkException e) {
      e.printStackTrace();
    }
    logger.info("connected.");
    listenerService = new NFCListenerService(nfc, storageHandler);
    if (configService.shouldIRunAMonitorThread()){
      monitorThread.run();
    }
  }

  @PreDestroy
  public void cleanUp() {
    try {
      ipcon.disconnect();
      if (!activeListener.isEmpty()) {
        activeListener.forEach((x) -> nfc.removeReaderStateChangedListener(x));
      }
    } catch (NotConnectedException e) {
      logger.error("Could not properly disconnect from Brick");
    }
  }
}