package com.ibm.cc.model;

import java.math.BigDecimal;

public class ConversionDM {

	private String countryCode;
	private BigDecimal amount;
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
}
