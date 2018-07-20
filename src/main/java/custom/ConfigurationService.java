package custom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Configuration for distributing the Application Properties.
 */
@Component("configService")
@PropertySource(name = "initProperties", value = "application.properties")
public class ConfigurationService {

  @Value("${brick.hostname:localhost}")
  private String hostname;

  @Value("${brick.port:4223}")
  private Integer port;

  @Value("${brick.master.uid}")
  private String masterUID;

  @Value("${brick.nfcbrick.uid}")
  private String nfcUID;

  @Value("${brick.load.uid}")
  private String loadBrickUID;

  @Value("${brick.temperature.uid}")
  private String tempBrickUID;

  @Value("${brick.ambientlight.uid}")
  private String ambientLightUID;

  @Value("${brick.dualbutton.uid}")
  private String dualButtonUID;

  @Value("${brick.distanceus.uid}")
  private String distanceUSUID;

  @Value("${brick.motiondetection.uid}")
  private String mmotiondetectionUID;

  @Value("${nfcBrick.save.pathname}")
  private String pathname;

  @Value("${nfcBrick.read.printchanges:false}")
  private boolean printdiff;

  @Value("${nfcBrick.save.loadData}")
  private boolean loadNFCData;

  @Value("${monitor.run}")
  private boolean runMonitorThread;

  public boolean shouldILoadNFCData() { return loadNFCData; }

  public String getHostname() { return hostname; }

  public String getPathname() { return pathname; }

  public boolean shouldIPrintDiff() { return printdiff; }

  public Integer getPort() { return port; }

  public String getMasterUID() { return masterUID; }

  public String getNfcUID() { return nfcUID; }

  public String getMmotiondetectionUID() { return mmotiondetectionUID; }

  public String getLoadBrickUID() { return loadBrickUID; }

  public String getTempBrickUID() { return tempBrickUID; }

  public String getAmbientLightUID() { return ambientLightUID; }

  public String getDualButtonUID() { return dualButtonUID; }

  public String getDistanceUSUID() { return distanceUSUID; }

  public boolean shouldIRunAMonitorThread() { return runMonitorThread; }
}
