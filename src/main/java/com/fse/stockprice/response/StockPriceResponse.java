package com.fse.stockprice.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StockPriceResponse {

    private String message;

    private String errorMessage;

}
