package fr.eletutour.asgard.hel.controller;

import fr.eletutour.asgard.hel.service.HelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hel")
public class HelController {

    private final HelService shutdownService;

    public HelController(HelService shutdownService) {
        this.shutdownService = shutdownService;
    }

    @PostMapping("/immediate")
    public ResponseEntity<String> shutdownImmediate() {
        shutdownService.shutdownImmediate();
        return ResponseEntity.ok("Application shutdown initiated");
    }

    @PostMapping("/scheduled")
    public ResponseEntity<String> shutdownScheduled(@RequestParam String cronExpression) {
        shutdownService.scheduleShutdown(cronExpression);
        return ResponseEntity.ok("Scheduled shutdown configured with cron: " + cronExpression);
    }
}
