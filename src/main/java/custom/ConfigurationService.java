package custom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component("configService")
@PropertySource(name = "initProperties", value = "application.properties")
public class ConfigurationService {

  @Value("${brick.hostname:localhost}")
  private String hostname;

  @Value("${brick.port:4223}")
  private Integer port;

  @Value("${brick.master.uid}")
  private String masterUID;

  @Value("${brick.nfc.uid}")
  private String nfcUID;

  @Value("${brick.motiondetection.uid}")
  private String mmotiondetectionUID;

  @Value("${nfc.save.pathname}")
  private String pathname;

  @Value("${nfc.read.printchanges:false}")
  private boolean printdiff;

  @Value("${nfc.save.loadData}")
  private boolean loadNFCData;

  public boolean isLoadNFCData() {
    return loadNFCData;
  }

  public String getHostname() {
    return hostname;
  }

  public String getPathname() {
    return pathname;
  }

  public boolean isPrintdiff() {
    return printdiff;
  }

  public Integer getPort() {
    return port;
  }

  public String getMasterUID() {
    return masterUID;
  }

  public String getNfcUID() {
    return nfcUID;
  }

  public String getMmotiondetectionUID() {
    return mmotiondetectionUID;
  }
}
