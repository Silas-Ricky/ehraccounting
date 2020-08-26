/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.ehraccounting.web.controller.account;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.ehraccounting.api.EhraccountingService;
import org.openmrs.module.ehraccounting.api.model.Account;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


/**
 *
 */
@Controller
@RequestMapping("/module/ehraccounting/account.list")
public class AccountListController {
	Log log = LogFactory.getLog(getClass());
    
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
					Account account = ehraccountingService.getAccount(id);
					if( account!= null )
					{
						ehraccountingService.deleteAccount(account);
					}
				}
			}
		}catch (Exception e) {
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR,
			"Can not delete account because it has link to other records ");
			log.error(e);
			e.printStackTrace();
			return "redirect:/module/ehraccounting/account.list";
		}
		httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR,
		"Account.deleted");
    	
    	return "redirect:/module/ehraccounting/account.list";
    }
	
    @RequestMapping(method=RequestMethod.GET)
	public String listTender(@RequestParam(value="pageSize",required=false)  Integer pageSize, 
	                         @RequestParam(value="currentPage",required=false)  Integer currentPage,
	                         Map<String, Object> model, HttpServletRequest request){
		
//		AccountingService accountingService = Context.getService(AccountingService.class);
    	List<Account> accounts = new ArrayList<Account>(Context.getService(EhraccountingService.class).getAccounts(true));
    	Collections.sort(accounts, new Comparator<Account>() {
            public int compare(Account o1, Account o2) {
	            return o1.getName().compareToIgnoreCase(o2.getName());
            }});
//		int total = accountingService.countListAmbulance();
//		
//		PagingUtil pagingUtil = new PagingUtil( RequestUtil.getCurrentLink(request) , pageSize, currentPage, total );
//		
//		List<Ambulance> ambulances = accountingService.listAmbulance(pagingUtil.getStartPos(), pagingUtil.getPageSize());
//		
		model.put("accounts", accounts );
//		
//		model.put("pagingUtil", pagingUtil);
//		
		return "/module/ehraccounting/account/list";
	}
}
