package custom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Class to initialize stuff before running the main application.
 */
@Service
public class InitializationService {

  @Autowired
  TestClass toRun;

  private Logger logger = LoggerFactory.getLogger(this.getClass());

  public boolean init() {
    /*
      Do stuff here.
     */
    toRun.exe();
    /*
      If your exe() finishes at some point, the application will close.
      If you want to keep it alive put a Thread.sleep(XXXXX) or
      preferably System.in.read() here if you do not need user input.
     */
    return true;
  }

}
