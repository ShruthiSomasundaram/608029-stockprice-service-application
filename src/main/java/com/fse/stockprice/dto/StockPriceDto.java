package com.fse.stockprice.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockPriceDto
{
	@ApiModelProperty(hidden=true)
	private String company;
	@NotNull(message = "stockPrice cannot be null")
	private Double price;
}
