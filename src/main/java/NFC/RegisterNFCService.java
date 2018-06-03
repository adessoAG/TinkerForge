package NFC;

import com.tinkerforge.BrickletNFC;
import com.tinkerforge.IPConnection;
import custom.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegisterNFCService {

  @Autowired
  private ConfigurationService configService;

  public BrickletNFC exe(IPConnection ipcon) {
    return new BrickletNFC(configService.getNfcUID(), ipcon);
  }
}
