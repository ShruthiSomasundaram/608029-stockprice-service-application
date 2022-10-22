package com.fse.stockprice.exceptionhandler;

import com.fse.stockprice.constants.StockPriceConstants;
import com.fse.stockprice.response.StockPriceResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.util.stream.Collectors;

@RestControllerAdvice
public class StockPiceExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StockPriceResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        String errorMessage=   ex.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(","));
        StockPriceResponse error = new StockPriceResponse();
        error.setErrorMessage(errorMessage);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<StockPriceResponse> handleJSONParseException(
            HttpMessageNotReadableException ex) {
        StockPriceResponse error = new StockPriceResponse();
        error.setErrorMessage(StockPriceConstants.MSG_PARSE_ERROR);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }


}
