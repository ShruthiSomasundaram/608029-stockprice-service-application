package com.fse.stockprice.model;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StockPriceDetail {

    private Double minStockPrice;

    private Double maxStockPrice;

    private Double avgStockPrice;

    private List<StockPrice> stockPriceList;


}
