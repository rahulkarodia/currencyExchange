package com.ibm.cc.conversion;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.cc.model.ConversionDM;
import com.ibm.cc.service.ConversionService;

@RestController
public class ConversionController {

	@Autowired
	ConversionService service;

	@PostMapping(path = "/convertcurrency/direct")
	public BigDecimal convertCurrency(@RequestBody ConversionDM cDM) {
		return service.convertCurrency(cDM);
	}
	
	//FeignClient
	@PostMapping(value="/convertCurrency/feign")
	public BigDecimal convertCurrencyFeign(@RequestBody ConversionDM cDM) {
		return service.calcCurrencyConversionFeign(cDM);
	}
	
	//Eureka
	@PostMapping(value="/convertCurrency/eureka")
	public BigDecimal convertCurrencyEureka(@RequestBody ConversionDM cDM) {
		return service.calcCurrencyConversionEureka(cDM);
	}
	
	//Load Balancer
	@PostMapping(value="/convertCurrency/loadbalancer")
	public BigDecimal convertCurrencyLoadBalancer(@RequestBody ConversionDM cDM) {
		return service.calcCurrencyConversionLoadBalancer(cDM);
	}
	
	//Load Balancer - REST template
	@PostMapping(value="/convertCurrency/loadbalancerrest")
	public BigDecimal convertCurrencyLBRest(@RequestBody ConversionDM cDM) {
		return service.calcCurrencyConversionLBRest(cDM);
	}
	
	//Hystrix Circuit Breaker
	@PostMapping(value="/convertCurrency/hystrix")
	public BigDecimal convertCurrencyHystrix(@RequestBody ConversionDM cDM) {
		return service.calcCurrencyConversionHCB(cDM);
	}
}