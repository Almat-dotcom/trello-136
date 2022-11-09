package kz.kacd.sso.realmcontroller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableAsync
@EnableScheduling
@SpringBootApplication
public class RealmControllerApplication {

	public static void main(String[] args) {
        SpringApplication.run(RealmControllerApplication.class, args);
    }

    /**
     * This endpoint serves simple method to lock spring context and up web server.
     */
    @RestController
    @RequestMapping("/hallo")
    public static class HalloEndpoint {

        @GetMapping(value = "", produces = MediaType.TEXT_PLAIN_VALUE)
        public String hallo() {
            return "Hallo";
        }
    }
}
