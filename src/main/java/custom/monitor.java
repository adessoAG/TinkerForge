package custom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class monitor implements Runnable {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private tester myclass;

    public monitor(tester myclass) {
        this.myclass = myclass;
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.info("Current Password: " + myclass.buildPassword());
        }
    }
}
