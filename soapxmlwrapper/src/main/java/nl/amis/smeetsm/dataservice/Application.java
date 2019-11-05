package nl.amis.smeetsm.dataservice;

import nl.amis.smeetsm.dataservice.utils.DSHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class Application {

    @Autowired
    private DSHelper dshelper;

    @PostConstruct
    private void init() {
        System.out.println(dshelper.getDbproccalls());
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
