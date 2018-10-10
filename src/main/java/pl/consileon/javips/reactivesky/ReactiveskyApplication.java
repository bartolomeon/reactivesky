package pl.consileon.javips.reactivesky;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableReactiveMongoRepositories 
@EnableScheduling
public class ReactiveskyApplication {

    public static void main( String[] args ) {
        SpringApplication.run( ReactiveskyApplication.class, args );
    }
}
