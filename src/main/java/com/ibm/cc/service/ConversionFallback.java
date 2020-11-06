package com.ibm.cc.service;

import org.springframework.stereotype.Component;

@Component
public class ConversionFallback implements ConversionFeignClient {
	@Override
	public Double calcCurrencyConversion(String countryCode) {
		System.out.println("ConversionFallBack:");
		double amount = 0.0;
		return amount;
	}

}
