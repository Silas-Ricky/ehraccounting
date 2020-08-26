package org.openmrs.module.ehraccounting.web.controller.incomereceipt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.ehraccounting.api.EhraccountingService;
import org.openmrs.module.ehraccounting.api.model.Account;
import org.openmrs.module.ehraccounting.api.model.AccountType;
import org.openmrs.module.ehraccounting.api.model.IncomeReceiptItem;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/module/ehraccounting/incomeReceiptItem.list")
public class IncomeReceiptItemListController {
	Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(method=RequestMethod.GET)
	public String fistView(@RequestParam(value="pageSize",required=false)  Integer pageSize, 
	                         @RequestParam(value="currentPage",required=false)  Integer currentPage,
	                         @RequestParam(value="accountId",required=false)  Integer accountId,
	                         Map<String, Object> model, HttpServletRequest request){
		
    	
		model.put("selectedAccount", accountId);
		
    	EhraccountingService ehraccountingService = Context.getService(EhraccountingService.class);
    	List<Account> accounts = ehraccountingService.listAccount(AccountType.INCOME, false);
		model.put("accounts", accounts);
    	if (accountId == null) {
    		return "/module/ehraccounting/incomereceipt/list";
    	}
    	
    	List<IncomeReceiptItem> incomeReceipts  =  ehraccountingService.getListIncomeReceiptItemByAccount(accountId);
    	if ( incomeReceipts != null ) {
    		Collections.sort(incomeReceipts, new Comparator<IncomeReceiptItem>() {
                public int compare(IncomeReceiptItem o1, IncomeReceiptItem o2) {
    	            return o1.getTransactionDate().compareTo(o2.getTransactionDate());
                }});
    		model.put("incomeReceipts", incomeReceipts );
    	}

    	
		
		return "/module/ehraccounting/incomereceipt/listReceiptItem";
	}
	
}
