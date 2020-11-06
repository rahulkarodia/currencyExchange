package com.ibm.cc.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ibm.cc.model.ConversionDM;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Service
public class ConversionService {

	@Autowired
	ConversionFeignClient feignClient;
	
	@Autowired
	DiscoveryClient discoveryClient;
	
	@Autowired
	LoadBalancerClient lbClient;
	
	@Autowired
	RestTemplate lbRestTemplate;

	@Bean	  
	  @LoadBalanced 
	  RestTemplate getRestTemplate() 
	  { 
		  return new RestTemplate(); 
	  }

	public BigDecimal convertCurrency(ConversionDM cDM) {
		
		String url="http://localhost:8080/currencyfactor/get/{countryCode}";
		RestTemplate template = new RestTemplate();
		HttpEntity<String> reqEntity = new HttpEntity<String>(cDM.getCountryCode());
		ResponseEntity<Double> factor = template.exchange(url, HttpMethod.GET, reqEntity, Double.class, cDM.getCountryCode());
		return getAmount(factor.getBody(), cDM.getAmount());
	}

	//Feign
	public BigDecimal calcCurrencyConversionFeign(ConversionDM cDM) {	
		Double conversionFactor = feignClient.calcCurrencyConversion(cDM.getCountryCode());		
		return getAmount(conversionFactor,cDM.getAmount());
	}

	//Eureka
	public BigDecimal calcCurrencyConversionEureka(ConversionDM cDM) {
		
		List<ServiceInstance> instances = discoveryClient.getInstances("currencyConverter");
		
		System.out.println("Number of instances: " + instances.size());
		
		ServiceInstance instance = instances.get(0);				
		String url = "http://" + instance.getHost() + ":" + instance.getPort() + "/currencyfactor/get/{countryCode}";
		
		System.out.println("URL is  " + url);
		
		RestTemplate template = new RestTemplate();
		HttpEntity<String> reqEntity = new HttpEntity<String>(cDM.getCountryCode());
		ResponseEntity<Double> conversionFactor = template.exchange(url, HttpMethod.GET, reqEntity, Double.class, cDM.getCountryCode());
		return getAmount(conversionFactor.getBody(), cDM.getAmount());
	}

	//Load Balancer
	public BigDecimal calcCurrencyConversionLoadBalancer(ConversionDM cDM) {
		ServiceInstance instance = lbClient.choose("currencyConverter");			
		String url = "http://" + instance.getHost() + ":" + instance.getPort() + "/currencyfactor/get/{countryCode}";
		RestTemplate template = new RestTemplate();
		HttpEntity<String> reqEntity = new HttpEntity<String>(cDM.getCountryCode());
		ResponseEntity<Double> conversionFactor = template.exchange(url, HttpMethod.GET, reqEntity, Double.class, cDM.getCountryCode());
		return getAmount(conversionFactor.getBody(),cDM.getAmount());
	}

	//Load Balancer - REST template
	public BigDecimal calcCurrencyConversionLBRest(ConversionDM cDM) {
		String url="http://conversionfactor/currencyfactor/get/{countryCode}";
		HttpEntity<String> reqEntity = new HttpEntity<String>(cDM.getCountryCode());
		ResponseEntity<Double> conversionFactor = lbRestTemplate.exchange(url, HttpMethod.GET, reqEntity, Double.class, cDM.getCountryCode());
		return getAmount(conversionFactor.getBody(), cDM.getAmount());
	}

	//Hystrix Circuit Breaker
	@HystrixCommand(fallbackMethod = "calculateFallback")
	public BigDecimal calcCurrencyConversionHCB(ConversionDM cDM) {	
		Double conversionFactor = feignClient.calcCurrencyConversion(cDM.getCountryCode());		
		return getAmount(conversionFactor, cDM.getAmount());
	}

	private BigDecimal calculateFallback(ConversionDM cDM) {
		return BigDecimal.ZERO;
	}
	
	public BigDecimal getAmount(Double factor, BigDecimal amount) {
		BigDecimal newAmount = new BigDecimal ( factor).multiply(amount);
		return newAmount;
	}
}
