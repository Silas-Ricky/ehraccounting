package org.openmrs.module.ehraccounting.web.controller.incomereceipt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.ehraccounting.api.EhraccountingService;
import org.openmrs.module.ehraccounting.api.model.Account;
import org.openmrs.module.ehraccounting.api.model.AccountType;
import org.openmrs.module.ehraccounting.api.model.IncomeReceipt;
import org.openmrs.module.hospitalcore.util.PagingUtil;
import org.openmrs.module.hospitalcore.util.RequestUtil;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/module/ehraccounting/incomereceipt.list")
public class IncomeReceiptListController {
	Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(method=RequestMethod.GET)
	public String fistView(@RequestParam(value="pageSize",required=false)  Integer pageSize, 
	                         @RequestParam(value="currentPage",required=false)  Integer currentPage,
	                         @RequestParam(value="accountId",required=false)  Integer accountId,
	                         Map<String, Object> model, HttpServletRequest request){
		EhraccountingService ehraccountingService = Context.getService(EhraccountingService.class);
		

		int total = ehraccountingService.countListIncomeReceipt(true);
		
		PagingUtil pagingUtil = new PagingUtil( RequestUtil.getCurrentLink(request) , pageSize, currentPage, total );
		
    	List<IncomeReceipt> incomeReceipts  =  ehraccountingService.getListIncomeReceipt(true,pagingUtil.getStartPos(), pagingUtil.getPageSize());
    	if ( incomeReceipts != null ) {
    		Collections.sort(incomeReceipts, new Comparator<IncomeReceipt>() {
                public int compare(IncomeReceipt o1, IncomeReceipt o2) {
    	            return o1.getReceiptDate().compareTo(o2.getReceiptDate());
                }});
    		model.put("incomeReceipts", incomeReceipts );
    		model.put("pagingUtil", pagingUtil);
    	}

    	List<Account> accounts = ehraccountingService.listAccount(AccountType.INCOME, false);
		model.put("accounts", accounts);
		return "/module/ehraccounting/incomereceipt/list";
	}
	
	 @RequestMapping(method=RequestMethod.POST)
	    public String deleteCompanies(@RequestParam("ids") String[] ids,HttpServletRequest request){
	    	EhraccountingService ehraccountingService = Context.getService(EhraccountingService.class);
	    	HttpSession httpSession = request.getSession();
			Integer id  = null;
			try{  
				if( ids != null && ids.length > 0 ){
					for(String sId : ids )
					{
						id = Integer.parseInt(sId);
						IncomeReceipt incomeReceipt = ehraccountingService.getIncomeReceipt(id);
						if( incomeReceipt!= null )
						{
							ehraccountingService.delete(incomeReceipt);
						}
					}
				}
			}catch (Exception e) {
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR,
				"Can not delete Income Receipt because it has link to other reocords ");
				log.error(e);
				return "redirect:/module/ehraccounting/incomereceipt.list";
			}
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR,
			"IncomeReceipt.deleted");
	    	
	    	return "redirect:/module/ehraccounting/incomereceipt.list";
	    }
}
