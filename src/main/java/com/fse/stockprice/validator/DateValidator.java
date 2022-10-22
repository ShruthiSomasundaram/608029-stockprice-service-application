package com.fse.stockprice.validator;

import java.text.ParseException;
import java.util.Date;

public interface DateValidator {

    Date convert(String date) throws ParseException;

    boolean validateDate(String startDate, String endDate);
}
