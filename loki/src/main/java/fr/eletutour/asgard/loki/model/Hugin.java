package fr.eletutour.asgard.loki.model;

public class Hugin {
    private boolean restcontroller;
    private boolean service;
    private boolean repository;
    private boolean controller;

    public boolean isRestcontroller() {
        return restcontroller;
    }

    public void setRestcontroller(boolean restcontroller) {
        this.restcontroller = restcontroller;
    }

    public boolean isService() {
        return service;
    }

    public void setService(boolean service) {
        this.service = service;
    }

    public boolean isRepository() {
        return repository;
    }

    public void setRepository(boolean repository) {
        this.repository = repository;
    }

    public boolean isController() {
        return controller;
    }

    public void setController(boolean controller) {
        this.controller = controller;
    }

    @Override
    public String toString() {
        return "Hugin{" +
                "restcontroller=" + restcontroller +
                ", service=" + service +
                ", repository=" + repository +
                ", controller=" + controller +
                '}';
    }
}
