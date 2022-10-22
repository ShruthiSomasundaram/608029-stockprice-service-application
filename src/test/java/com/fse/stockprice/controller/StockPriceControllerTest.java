package com.fse.stockprice.controller;

import com.fse.stockprice.dto.StockPriceDto;
import com.fse.stockprice.exception.StockPriceException;
import com.fse.stockprice.model.StockPrice;
import com.fse.stockprice.model.StockPriceDetail;
import com.fse.stockprice.response.StockPriceDetailResponse;
import com.fse.stockprice.response.StockPriceResponse;
import com.fse.stockprice.service.StockPriceService;
import com.fse.stockprice.validator.DateValidator;
import com.mongodb.MongoClientException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.pojo.tester.api.assertion.Method;

import java.util.Arrays;

import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsFor;


@RunWith(SpringJUnit4ClassRunner.class)
public class StockPriceControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    StockPriceController stockPriceController;

    @Mock
    StockPriceService stockPriceService;

    @Mock
    DateValidator dateValidator;

    StockPriceDto stockPriceDto;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(stockPriceController).build();
        stockPriceDto = new StockPriceDto();
        stockPriceDto.setPrice(145.50);
    }

    @Test
    public void testAddStockPrice() {
        Mockito.when(stockPriceService.addStockPrice(Mockito.any(),Mockito.anyString())).thenReturn(stockPriceDto);
        ResponseEntity<StockPriceResponse> stockPriceResponseResponseEntity =stockPriceController.addStockPrice("companyCode",stockPriceDto);
        Assert.assertEquals(HttpStatus.OK,stockPriceResponseResponseEntity.getStatusCode());
    }

    @Test
    public void testAddStockPriceInvalidCompanyCode() {
        Mockito.when(stockPriceService.checkCompanyCodeInvalid(Mockito.anyString())).thenThrow(StockPriceException.class);
        Mockito.when(stockPriceService.addStockPrice(Mockito.any(),Mockito.anyString())).thenReturn(stockPriceDto);
        ResponseEntity<StockPriceResponse> stockPriceResponseResponseEntity =stockPriceController.addStockPrice("companyCode",stockPriceDto);
        Assert.assertEquals( HttpStatus.BAD_REQUEST,stockPriceResponseResponseEntity.getStatusCode());
    }

    @Test
    public void testAddStockPriceException() {
        Mockito.when(stockPriceService.checkCompanyCodeInvalid(Mockito.anyString())).thenReturn(false);
        Mockito.when(stockPriceService.addStockPrice(Mockito.any(),Mockito.anyString())).thenThrow(MongoClientException.class);
        stockPriceController.addStockPrice("companyCode",stockPriceDto);
    }

    @Test
    public void testGetStockPriceDetails() {
        StockPriceDetail stockPriceDetail = getStockPriceDetails();
        Mockito.when(stockPriceService.getStockPriceDetails(Mockito.anyString(),Mockito.anyString(),Mockito.any())).thenReturn(stockPriceDetail);
        ResponseEntity<StockPriceDetailResponse> stockPriceResponseResponseEntity =stockPriceController.getStockPriceDetails("company1","2022-10-04","2022-10-07");
        Assert.assertEquals(HttpStatus.OK,stockPriceResponseResponseEntity.getStatusCode());
        Assert.assertEquals(new Double(14.50), stockPriceDetail.getMinStockPrice());
    }

    @Test
    public void testGetStockPriceDetailsNoData() {
        StockPriceDetail stockPriceDetail = getStockPriceDetails();
        stockPriceDetail.setStockPriceList(Arrays.asList());
        Mockito.when(stockPriceService.getStockPriceDetails(Mockito.anyString(),Mockito.anyString(),Mockito.any())).thenReturn(stockPriceDetail);
        ResponseEntity<StockPriceDetailResponse> stockPriceResponseResponseEntity =stockPriceController.getStockPriceDetails("company1","2022-10-04","2022-10-05");
        Assert.assertEquals(HttpStatus.OK,stockPriceResponseResponseEntity.getStatusCode());
        Assert.assertEquals("No stock data available", stockPriceResponseResponseEntity.getBody().getStatusMessage());
    }

    private StockPriceDetail getStockPriceDetails() {
        StockPriceDetail stockPriceDetail = new StockPriceDetail();
        StockPrice stockPrice1 = new StockPrice();
        stockPrice1.setCompanyCode("C1");
        stockPrice1.setPrice(14.50);

        StockPrice stockPrice2 = new StockPrice();
        stockPrice2.setCompanyCode("C2");
        stockPrice2.setPrice(25.50);

        stockPriceDetail.setStockPriceList(Arrays.asList(stockPrice1,stockPrice2));
        stockPriceDetail.setMinStockPrice(14.50);
        stockPriceDetail.setMaxStockPrice(25.50);
        stockPriceDetail.setAvgStockPrice(20.50);
        return stockPriceDetail;
    }

    @Test
    public void testGetStockPriceDetailsException() {
        Mockito.when(stockPriceService.getStockPriceDetails(Mockito.anyString(),Mockito.anyString(),Mockito.any())).thenThrow(MongoClientException.class);
        ResponseEntity<StockPriceDetailResponse> stockPriceResponseResponseEntity =stockPriceController.getStockPriceDetails("company1","2022-10-04","2022-10-07");
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,stockPriceResponseResponseEntity.getStatusCode());
    }


    @Test
    public void testGetStockPriceDetailsInvalidDate() {
        Mockito.when(dateValidator.validateDate(Mockito.anyString(),Mockito.anyString())).thenThrow(StockPriceException.class);
        ResponseEntity<StockPriceDetailResponse> stockPriceResponseResponseEntity =stockPriceController.getStockPriceDetails("company1","2022-10-09","2022-10-07");
        Assert.assertEquals(HttpStatus.BAD_REQUEST,stockPriceResponseResponseEntity.getStatusCode());
    }

    @Test
    public void testDeleteStockPriceSuccess() {
        Mockito.when(stockPriceService.deleteStockPrice(Mockito.anyString())).thenReturn(1);
        ResponseEntity<String> responseEntity = stockPriceController.deleteStockPrice("company1");
        Assert.assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @Test
    public void testDeleteStockPriceNotFound() {
        Mockito.when(stockPriceService.deleteStockPrice(Mockito.anyString())).thenReturn(0);
        ResponseEntity<String> responseEntity = stockPriceController.deleteStockPrice("company1");
        Assert.assertEquals(HttpStatus.NO_CONTENT,responseEntity.getStatusCode());
    }

    @Test
    public void  testStockPriceDtoGettersAndSetters() {
        final Class<?> stockPricedto =StockPriceDto.class;
        assertPojoMethodsFor(stockPricedto).testing(Method.GETTER).testing(Method.SETTER).testing(Method.CONSTRUCTOR).areWellImplemented();
    }

    @Test
    public void  testStockPriceGettersAndSetters() {
        final Class<?> stockPrice =StockPrice.class;
        assertPojoMethodsFor(stockPrice).testing(Method.GETTER).testing(Method.SETTER).testing(Method.CONSTRUCTOR).areWellImplemented();
    }

    @Test
    public void  testStockPriceDetailGettersAndSetters() {
        final Class<?> stockPriceDetail =StockPriceDetail.class;
        assertPojoMethodsFor(stockPriceDetail).testing(Method.GETTER).testing(Method.SETTER).testing(Method.CONSTRUCTOR).areWellImplemented();
    }

    @Test
    public void  testStockPriceResponseGettersAndSetters() {
        final Class<?> stockPriceresponse =StockPriceResponse.class;
        assertPojoMethodsFor(stockPriceresponse).testing(Method.GETTER).testing(Method.SETTER).testing(Method.CONSTRUCTOR).areWellImplemented();
    }

    @Test
    public void  testStockPriceDetailResponseGettersAndSetters() {
        final Class<?> stockPriceDetailResponse =StockPriceDetailResponse.class;
        assertPojoMethodsFor(stockPriceDetailResponse).testing(Method.GETTER).testing(Method.SETTER).testing(Method.CONSTRUCTOR).areWellImplemented();
    }

}
