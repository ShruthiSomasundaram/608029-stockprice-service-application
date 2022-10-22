package com.fse.stockprice.mapper;

import com.fse.stockprice.dto.StockPriceDto;
import com.fse.stockprice.model.StockPrice;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

@Component
public class StockPriceMapper 
{
	public StockPriceDto toStockPriceDto(StockPrice stockPrice)
	{
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		return mapper.map(stockPrice, StockPriceDto.class);
	}
	
	public StockPrice toStockPrice(StockPriceDto stockPriceDto,String companyCode)
	{
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		StockPrice stockPrice = mapper.map(stockPriceDto, StockPrice.class);
		stockPrice.setCompanyCode(companyCode);
		return stockPrice;
	}

}
