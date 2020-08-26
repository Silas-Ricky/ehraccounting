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

import org.openmrs.api.context.Context;
import org.openmrs.module.ehraccounting.api.EhraccountingService;
import org.openmrs.module.ehraccounting.api.model.Account;
import org.openmrs.module.ehraccounting.api.model.AccountType;
import org.openmrs.module.ehraccounting.api.model.FiscalPeriod;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 */
@Controller
@RequestMapping("/module/ehraccounting/account.form")
@SessionAttributes("accountCommand")
public class AccountFormController {
	
	@ModelAttribute("periods")
	public List<FiscalPeriod> registerPeriods() {
		return Context.getService(EhraccountingService.class).getCurrentYearPeriods();
	}
	
	@ModelAttribute("listParents")
	public List<Account> registerListParents() {
		List<Account> listParents = new ArrayList<Account>(Context.getService(EhraccountingService.class)
		        .getListParrentAccount());
		Collections.sort(listParents, new Comparator<Account>() {
			
			public int compare(Account o1, Account o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		});
		return listParents;
	}
	
	@ModelAttribute("accountTypes")
	public AccountType[] registerAccountTypes() {
		return AccountType.values();
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(java.lang.Boolean.class, new CustomBooleanEditor("true", "false", true));
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String firstView( @RequestParam(value = "id", required = false) Integer id, Model model) {
		if (id != null) {
			EhraccountingService service = Context.getService(EhraccountingService.class);
			Account account = service.getAccount(id);
			AccountCommand command = new AccountCommand();
			command.setAccount(account);
			model.addAttribute("accountCommand", command);
			/*
			 *  Disable edit fields :
			 *  - Account type
			 *  - start period
			 */
			
			model.addAttribute("disableEdit",true);
			
		} else {
			AccountCommand command = new AccountCommand();
			
			model.addAttribute("accountCommand", command);
		}
		
		return "/module/ehraccounting/account/form";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String onSubmit(@ModelAttribute("accountCommand") AccountCommand command, BindingResult bindingResult, Model model,
	                       HttpServletRequest request, SessionStatus status) {
		
		new AccountValidator().validate(command, bindingResult);
		if (bindingResult.hasErrors()) {
			return "/module/ehraccounting/account/form";
		}
		Context.getService(EhraccountingService.class).saveAccount(command.getAccount(), command.getPeriod());
		// Clean the session attribute after successful submit
		status.setComplete();
		return "redirect:/module/ehraccounting/account.list";
	}
	
	
	
}
