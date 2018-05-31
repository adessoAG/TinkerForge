package custom;

import org.springframework.stereotype.Service;

@Service
public class InitializationService {

    public boolean init() {
        try{
            System.out.print("Connecting to Brick...");
            tester toRun = new tester();
            toRun.exe();
            return true;
        }
        catch (Exception e){

        }
        return false;
    }
}
