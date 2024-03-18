package de.vw.productionline.productionline.exceptions;

public class ProductionLineNotRunningException extends RuntimeException{
    public ProductionLineNotRunningException(String message) {
        super(message);
    }

    public ProductionLineNotRunningException() {
        this("Production line is not running.");
    }
}
