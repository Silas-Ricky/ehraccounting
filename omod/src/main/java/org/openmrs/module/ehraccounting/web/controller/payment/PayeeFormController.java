package org.openmrs.module.ehraccounting.web.controller.payment;

import org.openmrs.api.context.Context;
import org.openmrs.module.ehraccounting.api.EhraccountingService;
import org.openmrs.module.ehraccounting.api.model.Payee;
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
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/module/ehraccounting/payee.form")
public class PayeeFormController {

	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(java.lang.Boolean.class, new CustomBooleanEditor("true", "false", true));
	}
	
	
	@RequestMapping(method = RequestMethod.GET)
	public String firstView( @RequestParam(value = "id", required = false) Integer id, Model model) {
		if (id != null) {
			Payee payee = Context.getService(EhraccountingService.class).getPayee(id);
			model.addAttribute("payee", payee);
		} else {
			Payee payee = new Payee();
			model.addAttribute("payee", payee);
		}
		
		return "/module/ehraccounting/payment/payeeForm";
	}
	
	
	
	@RequestMapping(method = RequestMethod.POST)
	public String onSubmit(@ModelAttribute("payee") Payee payee, BindingResult bindingResult, Model model,
	                       HttpServletRequest request, SessionStatus status) {
		
		new PayeeValidator().validate(payee, bindingResult);
		if (bindingResult.hasErrors()) {
			return "/module/ehraccounting/payment/payeeform";
		}
		Context.getService(EhraccountingService.class).savePayee(payee);
		// Clean the session attribute after successful submit
		status.setComplete();
		return "/module/ehraccounting/payment/successPayee";
	}
}
