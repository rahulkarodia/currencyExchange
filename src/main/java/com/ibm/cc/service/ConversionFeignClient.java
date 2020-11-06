package com.ibm.cc.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name="currencyConverter", fallback = ConversionFallback.class)
public interface ConversionFeignClient {

		@RequestMapping(value ="/currencyConverter/get/{countryCode}")
		public Double calcCurrencyConversion(@PathVariable("countryCode") String countryCode);
}
