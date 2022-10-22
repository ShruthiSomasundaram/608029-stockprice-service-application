package com.fse.stockprice.exception;

public class StockPriceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public StockPriceException(String message) {
        super(message);
    }
}
