package custom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class InitializationService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public boolean init() {
        try{
            logger.info("Connecting to Brick...");
            tester toRun = new tester();
            toRun.exe();
            return true;
        }
        catch (Exception e){

        }
        return false;
    }
}
