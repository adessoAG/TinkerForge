package NFC;

import com.tinkerforge.*;
import custom.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegisterBrickService {

  @Autowired
  private ConfigurationService configService;

  public BrickletNFC initNFCBrick(IPConnection ipcon) {
    return new BrickletNFC(configService.getNfcUID(), ipcon);
  }

  public BrickletLoadCellV2 initLoadCell(IPConnection ipcon) { return new BrickletLoadCellV2(configService.getLoadBrickUID(), ipcon); }

  public BrickletAmbientLightV2 initAmbientLight(IPConnection ipcon) { return new BrickletAmbientLightV2(configService.getAmbientLightUID(), ipcon); }

  public BrickletDistanceUS initDistanceUS(IPConnection ipcon) { return new BrickletDistanceUS(configService.getAmbientLightUID(), ipcon); }

  public BrickletDualButton initDualButton(IPConnection ipcon) { return new BrickletDualButton(configService.getAmbientLightUID(), ipcon); }

  public BrickletTemperature initTemperature(IPConnection ipcon) { return new BrickletTemperature(configService.getAmbientLightUID(), ipcon); }

  public BrickletMotionDetectorV2 initMotionDetector(IPConnection ipcon) { return new BrickletMotionDetectorV2(configService.getAmbientLightUID(), ipcon); }
}
