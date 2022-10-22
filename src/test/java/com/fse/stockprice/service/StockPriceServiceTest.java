package com.fse.stockprice.service;

import com.fse.stockprice.dto.StockPriceDto;
import com.fse.stockprice.exception.StockPriceException;
import com.fse.stockprice.mapper.StockPriceMapper;
import com.fse.stockprice.model.StockPrice;
import com.fse.stockprice.model.StockPriceDetail;
import com.fse.stockprice.repository.StockPriceRepository;
import com.fse.stockprice.service.impl.StockPriceServiceImpl;
import com.mongodb.MongoClientException;
import com.mongodb.client.result.DeleteResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
public class StockPriceServiceTest {

    @InjectMocks
    StockPriceServiceImpl stockPriceService;

    @Mock
    private StockPriceRepository stockPriceRepository;

    @Mock
    private StockPriceMapper stockPriceMapper;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    MongoTemplate mongoTemplate;

    @Mock
    DeleteResult mockDeleteResult;

    StockPriceDto stockPriceDto;
    @Before
    public void setUp() {

        stockPriceDto = new StockPriceDto();
        stockPriceDto.setCompany("company1");
        stockPriceDto.setPrice(15.50);
    }

    @Test
    public void testAddStock() {
        StockPrice stockPrice = new StockPrice();
        stockPrice.setCompanyCode("company1");
        stockPrice.setPrice(15.50);
        Mockito.when(stockPriceMapper.toStockPrice(Mockito.any(),Mockito.anyString())).thenReturn(stockPrice);
        Mockito.when(stockPriceRepository.save(Mockito.any())).thenReturn(stockPrice);
        Mockito.when(stockPriceMapper.toStockPriceDto(Mockito.any())).thenReturn(stockPriceDto);
        StockPriceDto stockPriceDtoResponse  = stockPriceService.addStockPrice(stockPriceDto,"company1");
        Assert.assertEquals("company1",stockPriceDtoResponse.getCompany());
    }

    @Test(expected = StockPriceException.class)
    public void testAddStockException() {
        StockPrice stockPrice = new StockPrice();
        stockPrice.setCompanyCode("company1");
        stockPrice.setPrice(15.50);
        Mockito.when(stockPriceMapper.toStockPrice(Mockito.any(),Mockito.anyString())).thenReturn(stockPrice);
        Mockito.when(stockPriceRepository.save(Mockito.any())).thenThrow(MongoClientException.class);
        stockPriceService.addStockPrice(stockPriceDto,"company1");
    }

    @Test
    public void testGetStockPriceDetails() {
        List<StockPrice> stockPrices = getStockPrices();
        Mockito.when(mongoTemplate.find(Mockito.any(),Mockito.eq(StockPrice.class))).thenReturn(stockPrices);
        StockPriceDetail stockPriceDetail =stockPriceService.getStockPriceDetails("company1","2022-10-03","2022-10-05");
        Assert.assertEquals(new Double(15.50),stockPriceDetail.getMinStockPrice());
        Assert.assertEquals(new Double(35.50),stockPriceDetail.getMaxStockPrice());
        Assert.assertEquals(new Double(25.50),stockPriceDetail.getAvgStockPrice());

    }

    private List<StockPrice> getStockPrices() {
        StockPrice stockPrice = new StockPrice();
        stockPrice.setCompanyCode("company1");
        stockPrice.setPrice(15.50);
        stockPrice.setDate("2022-10-04T18:23:450");

        StockPrice stockPrice2 = new StockPrice();
        stockPrice2.setCompanyCode("company1");
        stockPrice2.setPrice(35.50);
        stockPrice2.setDate("2022-10-04T19:23:450");

        return Arrays.asList(stockPrice,stockPrice2);

    }

    @Test
    public void testCompanyCodeValid() {
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(),Mockito.eq(Boolean.class),Mockito.anyString())).thenReturn(new ResponseEntity<>(false, HttpStatus.OK));
        boolean response = stockPriceService.checkCompanyCodeInvalid("company1");
        Assert.assertEquals(false,response);
    }

    @Test(expected = StockPriceException.class)
    public void testCompanyCodeInValid() {
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(),Mockito.eq(Boolean.class),Mockito.anyString())).thenReturn(new ResponseEntity<>(true, HttpStatus.OK));
        stockPriceService.checkCompanyCodeInvalid("company2");
    }

    @Test
    public void testDeleteStockPrice() {
        Mockito.when(mongoTemplate.remove(Mockito.any(),Mockito.eq(StockPrice.class))).thenReturn(DeleteResult.acknowledged(1L));
        int count  = stockPriceService.deleteStockPrice("company1");
        Assert.assertEquals(1,count);
    }

}
