package com.fse.stockprice.service.impl;

import com.fse.stockprice.constants.StockPriceConstants;
import com.fse.stockprice.dto.StockPriceDto;
import com.fse.stockprice.exception.StockPriceException;
import com.fse.stockprice.mapper.StockPriceMapper;
import com.fse.stockprice.model.StockPrice;
import com.fse.stockprice.model.StockPriceDetail;
import com.fse.stockprice.repository.StockPriceRepository;
import com.fse.stockprice.service.StockPriceService;
import com.mongodb.client.result.DeleteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StockPriceServiceImpl implements StockPriceService {

    @Autowired
    private StockPriceRepository stockPriceRepository;

    @Autowired
    private StockPriceMapper stockPriceMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public StockPriceDto addStockPrice(StockPriceDto stockPriceDto,String companyCode) {
        log.info("StockPriceServiceImpl class addStockPrice method - begin");
        StockPrice stockPrice = stockPriceMapper.toStockPrice(stockPriceDto, companyCode);
        Instant instant = new Date().toInstant();
        stockPrice.setDate(instant.atZone(ZoneId.of("UTC")).toLocalDateTime().toString());
        try {
            stockPrice = stockPriceRepository.save(stockPrice);
        } catch(Exception e) {
            log.error("Exception occurred in Add StockPriceService : {}",e.getMessage());
            throw new StockPriceException(StockPriceConstants.STOCK_DETAIL_INSERT_FAILED);

        }
        log.info("StockPriceServiceImpl class addStockPrice method - end");
        return stockPriceMapper.toStockPriceDto(stockPrice);

    }

    @Override
    public StockPriceDetail getStockPriceDetails(String companyCode, String startDate, String endDate) {

        StockPriceDetail stockPriceDetail = new StockPriceDetail();
        Query query = new Query();
        query.addCriteria(Criteria.where(StockPriceConstants.DATE_FIELD).gte(startDate).lte(endDate).and(StockPriceConstants.COMPANY_CODE).is(companyCode));
        List<StockPrice> stockPrices =mongoTemplate.find(query,StockPrice.class);

        List<StockPrice> stockPriceList =  stockPrices.stream().map(stockPrice -> {
           List<String> dateParts = Arrays.asList(stockPrice.getDate().split("T"));
           stockPrice.setDate(dateParts.get(0));
           stockPrice.setTime(dateParts.get(1));
           return  stockPrice;
        }).collect(Collectors.toList());

        OptionalDouble minStockPrice = stockPriceList
                .stream()
                .mapToDouble(StockPrice::getPrice)
                .min();

        OptionalDouble maxStockPrice = stockPriceList
                .stream()
                .mapToDouble(StockPrice::getPrice)
                .max();

        OptionalDouble average = stockPriceList
                .stream()
                .mapToDouble(StockPrice::getPrice)
                .average();

         stockPriceDetail.setMinStockPrice(minStockPrice.isPresent() ? minStockPrice.getAsDouble() : null);
         stockPriceDetail.setMaxStockPrice(maxStockPrice.isPresent() ? maxStockPrice.getAsDouble() : null);
         stockPriceDetail.setAvgStockPrice(average.isPresent() ? average.getAsDouble() : null);
         stockPriceDetail.setStockPriceList(stockPrices);

        return  stockPriceDetail;
    }

    @Override
    public boolean checkCompanyCodeInvalid(String companyCode) {
        log.info("Validating companyCode : {}",companyCode);
        boolean invalid = false;

        ResponseEntity<Boolean> response = restTemplate.getForEntity(StockPriceConstants.COMPANY_CODE_INVALID_URL, Boolean.class,companyCode);
            if (response.getStatusCode() == HttpStatus.OK && (Boolean.TRUE).equals(response.getBody())){
                throw new StockPriceException(StockPriceConstants.COMPANY_CODE_ERROR);
            }
        log.info("companyCode :{}  validation successfully completed",companyCode);
        return invalid;
    }

    @Override
    public int deleteStockPrice(String companyCode) {
        Query query = new Query();
        query.addCriteria(Criteria.where(StockPriceConstants.COMPANY_CODE).is(companyCode));
        DeleteResult deleteResult = mongoTemplate.remove(query,StockPrice.class);
        return (int)deleteResult.getDeletedCount();
    }

}
