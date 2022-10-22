package com.fse.stockprice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
@Getter
@Setter
public class StockPrice 
{
	@Id
	private String id;
	private String companyCode;
	private Double price;
	private String date;

    @Transient
    private String time;
}
