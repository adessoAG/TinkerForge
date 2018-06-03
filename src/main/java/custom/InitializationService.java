package custom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InitializationService {

  @Autowired
  tester toRun;

  private Logger logger = LoggerFactory.getLogger(this.getClass());

  public boolean init() {
    logger.info("Connecting to Brick...");
    toRun.exe();
    return true;
  }

}
