package de.vw.productionline.productionline.exceptions;

public class ProductionLineIncompleteException extends RuntimeException {
    public ProductionLineIncompleteException(String message) {
        super(message);
    }

    public ProductionLineIncompleteException() {
        super("Production line is incomplete.");
    }

}
