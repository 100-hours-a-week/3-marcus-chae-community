package kr.adapterz.springboot;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InitialController {
    private final InitialService initialService;

    public InitialController(InitialService initialService) {
        this.initialService = initialService;
    }

    @GetMapping("/hello")
    public String hello() {
        return initialService.getMessage();
    }
}
