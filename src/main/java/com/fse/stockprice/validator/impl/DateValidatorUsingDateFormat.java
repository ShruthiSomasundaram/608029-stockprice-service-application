package com.fse.stockprice.validator.impl;

import com.fse.stockprice.constants.StockPriceConstants;
import com.fse.stockprice.exception.StockPriceException;
import com.fse.stockprice.validator.DateValidator;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class DateValidatorUsingDateFormat implements DateValidator {

    @Override
    public Date convert(String dateString) throws ParseException{

        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        return  sdf.parse(dateString);
    }

    @Override
    public boolean validateDate(String startDate, String endDate) {
        try {
            Date formattedStartDate =  convert(startDate);
            Date formattedEndDate =  convert(endDate);
            if(formattedStartDate.after(formattedEndDate)) {
                throw new StockPriceException(StockPriceConstants.DATE_RANGE_ERROR);
            }
        } catch (ParseException e) {
            throw new StockPriceException(StockPriceConstants.DATE_ERROR);
        }
        return true;
    }
}
