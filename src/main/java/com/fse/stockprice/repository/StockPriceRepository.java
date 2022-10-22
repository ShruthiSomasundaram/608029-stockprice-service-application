package com.fse.stockprice.repository;

import java.util.List;

import com.fse.stockprice.model.StockPrice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StockPriceRepository extends MongoRepository<StockPrice, String>
{
    StockPrice findByCompanyCode(String companyCode);

    @Query("{date : {$gte : ?0,$lte : ?1 }}")
    List<StockPrice> findWithCondition(String startDate,String endDate);
}
