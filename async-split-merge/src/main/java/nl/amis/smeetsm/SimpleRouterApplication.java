package nl.amis.smeetsm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages="nl.amis.smeetsm.camel")
public class SimpleRouterApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleRouterApplication.class, args);
    }
}
