package custom;

import com.tinkerforge.IPConnection;
import com.tinkerforge.BrickletNFC;

public class setupNFC {

  private static final String UID = "Epg";

  public static BrickletNFC exe(IPConnection ipcon) {
    return new BrickletNFC(UID, ipcon);
  }
}
