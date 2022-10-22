package com.fse.stockprice.response;

import com.fse.stockprice.model.StockPriceDetail;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockPriceDetailResponse {

    private String statusMessage;
    private String errorMessage;
    private StockPriceDetail stockPriceDetail;
}
