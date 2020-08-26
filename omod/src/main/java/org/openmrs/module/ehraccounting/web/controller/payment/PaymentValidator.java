package org.openmrs.module.ehraccounting.web.controller.payment;

import org.openmrs.api.context.Context;
import org.openmrs.module.ehraccounting.api.EhraccountingService;
import org.openmrs.module.ehraccounting.api.model.ExpenseBalance;
import org.openmrs.module.ehraccounting.api.model.Payment;
import org.openmrs.module.ehraccounting.api.model.PaymentStatus;
import org.openmrs.module.ehraccounting.api.utils.DateUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.math.BigDecimal;
import java.util.Date;


public class PaymentValidator implements Validator {

	@Override
    public boolean supports(Class<?> arg0) {
		return Payment.class.equals(arg0);
    }

	@Override
    public void validate(Object arg0, Errors error) {
	    // TODO Auto-generated method stub
	    Payment payment = (Payment) arg0;
	    

	    if (payment.getAccount() == null) {
	    	error.reject("ehraccounting.account.required");
	    }
	    
	    if (payment.getPaymentDate() == null) {
	    	error.reject("ehraccounting.paymentDate.required");
	    } else  if (DateUtils.isFutureDate(payment.getPaymentDate())) {
	    	error.reject("ehraccounting.paymentDate.future");
	    }
	    
	    if (payment.getPayableAmount() == null) {
	    	error.reject("ehraccounting.payable.required");
	    } else if (payment.getPayableAmount().compareTo(new BigDecimal("0")) < 0 ){
	    	error.reject("ehraccounting.payable.invalid");
	    }
	    
	    if (payment.getPayee() == null) {
	    	error.reject("ehraccounting.payee.required");
	    }
	    Date date = payment.getPaymentDate();
	  	ExpenseBalance balance = Context.getService(EhraccountingService.class).findExpenseBalance(payment.getAccount().getId(), date);
	  	if ( balance == null || balance.getAvailableBalance() == null || balance.getAvailableBalance().compareTo(new BigDecimal("0")) < 0) {
	  		error.reject("ehraccounting.payment.budget.required");
	  	} else {
		  	if (payment.getStatus().equals(PaymentStatus.COMMITTED)) {
		  		if (payment.getCommitmentAmount() == null || payment.getCommitmentAmount().compareTo(new BigDecimal("0")) <= 0) {
		  			error.reject("ehraccounting.payment.commitmentAmount.invalid");
		  		} else if (payment.getCommitmentAmount().compareTo(balance.getAvailableBalance()) > 0) {
		  			error.reject("ehraccounting.payment.budget.notEnough");
		  		}
		  	}
		    if (payment.getStatus().equals(PaymentStatus.PAID)) {
		    	if(  payment.getActualPayment() == null || payment.getActualPayment().compareTo(new BigDecimal("0") ) <= 0  ) {
		    		error.reject("ehraccounting.payment.actualPayment.invalid");
		    	}else if (payment.getActualPayment().compareTo(balance.getLedgerBalance()) > 0) {
		    		error.reject("ehraccounting.payment.budget.notEnough");
		    	}
		    }
	  	}
    }
	
	
}
