package custom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TinkerForgeApplication implements ApplicationRunner {

    @Autowired
    private InitializationService initService;

    public static void main(String[] args) {
        SpringApplication.run(TinkerForgeApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!initService.init()) {
            System.exit(1);
        }
    }
}