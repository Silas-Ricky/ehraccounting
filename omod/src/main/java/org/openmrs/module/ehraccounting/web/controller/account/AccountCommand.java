package org.openmrs.module.ehraccounting.web.controller.account;

import org.openmrs.module.ehraccounting.api.model.Account;
import org.openmrs.module.ehraccounting.api.model.FiscalPeriod;


public class AccountCommand {
	private Account account;
	private Integer period;
	private FiscalPeriod fiscalPeriod;
	
	public AccountCommand(){
		this.account = new Account();
	}
	
    public Account getAccount() {
    	return account;
    }
	
    public void setAccount(Account account) {
    	this.account = account;
    }

	
    public Integer getPeriod() {
    	return period;
    }

	
    public void setPeriod(Integer period) {
    	this.period = period;
    }

	
    public FiscalPeriod getFiscalPeriod() {
    	return fiscalPeriod;
    }

	
    public void setFiscalPeriod(FiscalPeriod fiscalPeriod) {
    	this.fiscalPeriod = fiscalPeriod;
    }
	
}
