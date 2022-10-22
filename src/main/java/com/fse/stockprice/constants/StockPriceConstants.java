package com.fse.stockprice.constants;

public class StockPriceConstants {
    private StockPriceConstants(){

    }

    public static final String STOCK_V1_PATH = "/api/v1.0/market/stock";
    public static final String COMPANY_CODE_INVALID_URL = "http://localhost:8082/api/v1.0/market/company/invalid/{companyCode}";

    public static final String DATE_FIELD = "date";
    public static final String COMPANY_CODE = "companyCode";


    //Error Messages
    public static final String DATE_ERROR = "Enter valid startDate and endDate" ;
    public static final String ERROR_MSG = "Unexpected error occurred during while adding stock price details";
    public static final String STOCK_DETAIL_INSERT_FAILED = "Stock price insertion failed";
    public static final String MSG_PARSE_ERROR = "JSON Error parsing the stock price input";
    public static final String SUCCESS_MSG = "Stock price added successfully for companyCode : ";
    public static final String COMPANY_CODE_ERROR = "companyCode does not exist.Please enter a valid companyCode";
    public static final String READ_ERROR = "Exception occurred while retrieving stock price details";
    public static final String DATE_RANGE_ERROR = "startDate cannot be after endDate.Please enter a valid date range";
}
