package com.fse.stockprice.service;

import com.fse.stockprice.dto.StockPriceDto;
import com.fse.stockprice.model.StockPriceDetail;


public interface StockPriceService {
    StockPriceDto addStockPrice(StockPriceDto stockPriceDto,String companyCode);

    int deleteStockPrice(String companyCode);

    StockPriceDetail getStockPriceDetails(String companyCode, String startDate, String endDate);

    boolean checkCompanyCodeInvalid(String companyCode) ;
}
