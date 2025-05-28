package fr.eletutour.loki.chaos.service;

import org.springframework.stereotype.Service;

@Service
public class DummyService {
    public String process() {
        return "Processed";
    }

    public String processWithoutChaos() {
        return "Processed without chaos";
    }

    public void processWithException() {
        throw new RuntimeException("Test exception");
    }
}