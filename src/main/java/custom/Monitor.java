package custom;

import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple Monitor thread to do stuff every second.
 * You can extend or delete the parameter of TestClass for usage in this monitor.
 */
public class Monitor implements Runnable {

  private Logger logger = LoggerFactory.getLogger(this.getClass());
  private TestClass myclass;

  public Monitor(TestClass myclass) {
    this.myclass = myclass;
  }

  public void run() {
    while (true) {
      //TODO Do stuff here
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
