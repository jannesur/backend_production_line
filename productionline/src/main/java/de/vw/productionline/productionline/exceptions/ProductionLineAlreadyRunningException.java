package de.vw.productionline.productionline.exceptions;

public class ProductionLineAlreadyRunningException extends RuntimeException {
    public ProductionLineAlreadyRunningException(String message) {
        super(message);
    }

    public ProductionLineAlreadyRunningException() {
        super("Production line is already running.");
    }

}
