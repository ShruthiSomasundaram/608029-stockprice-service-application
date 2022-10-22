package com.fse.stockprice.controller;

import com.fse.stockprice.constants.StockPriceConstants;
import com.fse.stockprice.dto.StockPriceDto;
import com.fse.stockprice.exception.StockPriceException;
import com.fse.stockprice.model.StockPrice;
import com.fse.stockprice.model.StockPriceDetail;
import com.fse.stockprice.response.StockPriceDetailResponse;
import com.fse.stockprice.response.StockPriceResponse;
import com.fse.stockprice.service.StockPriceService;
import com.fse.stockprice.validator.DateValidator;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;

import javax.validation.*;


@RestController
@CrossOrigin(origins="http://localhost:4200")
@Slf4j
@RequestMapping(StockPriceConstants.STOCK_V1_PATH)
public class StockPriceController {

    @Autowired
    StockPriceService stockPriceService;

    @Autowired
    DateValidator dateValidator;

    /**
     * API to addd stock Price to a company
     *
     * @param companyCode
     * @param stockPriceDto
     * @return
     */

    @ApiOperation(value = "Adds a stock price to a company",
            notes = "Adds stock price to a company given companyCode and stockPrice")
    @PostMapping(path = "/add/{companyCode}")
    public ResponseEntity<StockPriceResponse> addStockPrice(
            @PathVariable("companyCode") String companyCode,
            @Valid @RequestBody StockPriceDto stockPriceDto) {
        StockPriceResponse stockPriceResponse = new StockPriceResponse();
        try {
            log.info("Add stock price started for companyCode : {}", companyCode);
            stockPriceService.checkCompanyCodeInvalid(companyCode);
            stockPriceService.addStockPrice(stockPriceDto, companyCode);
            stockPriceResponse.setMessage(StockPriceConstants.SUCCESS_MSG +companyCode);
            log.info("Add stock price completed for companyCode : {}", companyCode);
        } catch(StockPriceException e) {
            stockPriceResponse.setErrorMessage(e.getMessage());
            return new ResponseEntity<>(stockPriceResponse,HttpStatus.BAD_REQUEST);
        } catch(Exception e) {
            stockPriceResponse.setErrorMessage(StockPriceConstants.ERROR_MSG);
            log.error("Exception occurred while adding stock price details :{}",e.getMessage());
            return new ResponseEntity<>(stockPriceResponse,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(stockPriceResponse, HttpStatus.OK);

    }

    /**
     * API to get Stock Price for a given date range
     *
     * @param companyCode
     * @param startDate
     * @param endDate
     * @return
     */
    @ApiOperation(value = "Retrieves stock price of a company over a time frame",
            notes = "Retrieves stock price of a company given companyCode and date range")
    @GetMapping(path = "/get/{companyCode}/{startDate}/{endDate}")
    public ResponseEntity<StockPriceDetailResponse> getStockPriceDetails(
            @PathVariable("companyCode") String companyCode,
            @PathVariable("startDate") String startDate,
            @PathVariable("endDate") String endDate) {
        log.info("Retrieve stock price details for companyCode :{} from startDate :{} and endDate : {}",companyCode,startDate,endDate);
        StockPriceDetailResponse stockPriceDetailResponse =new StockPriceDetailResponse();
        try {
            dateValidator.validateDate(startDate,endDate);
            StockPriceDetail stockPriceDetail= stockPriceService.getStockPriceDetails(companyCode, startDate, endDate);
            if(stockPriceDetail.getStockPriceList().isEmpty()) {
                stockPriceDetailResponse.setStatusMessage("No stock data available");
            } else {
                stockPriceDetailResponse.setStatusMessage("Retrieved stock price details");
            }
            stockPriceDetailResponse.setStockPriceDetail(stockPriceDetail);
            return new ResponseEntity<>(stockPriceDetailResponse, HttpStatus.OK);

        } catch(StockPriceException e) {
            stockPriceDetailResponse.setErrorMessage(e.getMessage());
            return new ResponseEntity<>(stockPriceDetailResponse,HttpStatus.BAD_REQUEST);
        } catch(Exception e) {
            stockPriceDetailResponse.setErrorMessage(StockPriceConstants.READ_ERROR);
            log.error("Exception occurred while retrieving stock price details :{}",e.getMessage());
            return new ResponseEntity<>(stockPriceDetailResponse,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * API to delete stock price based on company code
     *
     * @param companyCode
     */
    @ApiOperation(value = "Deletes stock price of a company",
            notes = "Deletes stock price of a company given companyCode")
    @DeleteMapping(path = "delete/stockPrice/{companyCode}")
    public ResponseEntity<String> deleteStockPrice(@PathVariable("companyCode") String companyCode) {
        int count = stockPriceService.deleteStockPrice(companyCode);
        if(count == 0 ) {
            return new ResponseEntity<>("",HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>("Deleted successfully",HttpStatus.OK);
    }

}
