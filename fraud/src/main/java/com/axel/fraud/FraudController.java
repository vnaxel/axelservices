package com.axel.fraud;

import com.axel.clients.fraud.FraudCheckResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fraud-check")
@Slf4j
public record FraudController(FraudCheckService fraudCheckService) {
    @GetMapping(path = "{customerId}")
    public FraudCheckResponse isFraudster(@PathVariable("customerId") Integer customerID) {
    boolean isFraudulentCustomer = fraudCheckService.isFraudulentCustomer(customerID);
    log.info("fraud check request for customer {}", customerID);
    return new FraudCheckResponse(isFraudulentCustomer);
    }
}
