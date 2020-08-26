package org.openmrs.module.ehraccounting.web.controller.fiscalyear;

import org.openmrs.api.context.Context;
import org.openmrs.module.ehraccounting.api.EhraccountingService;
import org.openmrs.module.ehraccounting.api.model.ExpenseBalance;
import org.openmrs.module.ehraccounting.api.model.FiscalPeriod;
import org.openmrs.module.ehraccounting.api.model.FiscalYear;
import org.openmrs.module.ehraccounting.api.model.GeneralStatus;
import org.openmrs.module.ehraccounting.api.model.IncomeBalance;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;


@Controller
public class PeriodController {
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(java.lang.Boolean.class, new CustomBooleanEditor("true", "false", true));
	}
	
	@ModelAttribute("listYears")
	public Collection<FiscalYear> registerListFiscalYears(){
		return Context.getService(EhraccountingService.class).getListFiscalYear(null);
	}
	
	@RequestMapping(value="/module/ehraccounting/closePeriod.htm",method=RequestMethod.POST)
	public String closePeriodSave(@RequestParam("periodId") Integer periodId, 
	                              @RequestParam("nextPeriodId") Integer nextPeriodId, 
	                              @RequestParam("resetBalance") Boolean resetBalance,
	                              Model model) {
		
		EhraccountingService  service = Context.getService(EhraccountingService.class);
		
		FiscalPeriod period = service.closePeriod(periodId, nextPeriodId, resetBalance);
		FiscalYear year = period.getFiscalYear();
		Boolean isAllPeriodClosed = service.isAllPeriodClosed(year.getId());
		
		if (isAllPeriodClosed) {
			return "redirect:/module/ehraccounting/closeFiscalYear.htm?id="+year.getId();
		} else {
			return "redirect:/module/ehraccounting/period.list";
		}
	}
	
	@RequestMapping(value="/module/ehraccounting/getPeriods.htm",method=RequestMethod.GET)
	public String getPeriods(@RequestParam("yearId") Integer yearId, Model model) {
		
		if (yearId == null) {
			return null;
		}
		
		EhraccountingService  service = Context.getService(EhraccountingService.class);
		FiscalYear year = service.getFiscalYear(yearId);
		if (year != null) {
			List<FiscalPeriod> periods = year.getPeriods();
			if (periods != null && periods.size() > 0) {
				model.addAttribute("listPeriods",periods);
			}
		}
		
		return "/module/ehraccounting/period/ajaxListPeriod";
	}
	
	@RequestMapping(value="/module/ehraccounting/closePeriod.htm",method=RequestMethod.GET)
	public String closePeriodView(@RequestParam("periodId") Integer periodId ,Model model) {
		if (periodId == null) {
			return "redirect:/module/ehraccounting/period.list";
		} else {
			EhraccountingService  service = Context.getService(EhraccountingService.class);
			FiscalPeriod period = service.getFiscalPeriod(periodId);
			List<IncomeBalance> accBalances = service.listActiveAccountBalance(period);
			List<ExpenseBalance> expBalances = service.listActiveExpenseBalance(period);
			
			boolean isLastPeriod = true;
			List<FiscalPeriod> periods = period.getFiscalYear().getPeriods();
			Iterator<FiscalPeriod> itr = periods.iterator();
			while (itr.hasNext()) {
				FiscalPeriod p = itr.next();
				if (p.getStatus().equals(GeneralStatus.CLOSED)) {
					itr.remove();
				}
				
				if (p.getStatus().equals(GeneralStatus.OPEN)) {
					isLastPeriod = false;
				}
				
			}
			periods.remove(period);
			
			if (isLastPeriod) {
				model.addAttribute("isLastPeriod",true);
			}
			
			model.addAttribute("period",period);
			model.addAttribute("listPeriods",periods);
			model.addAttribute("accBalances",accBalances);
			model.addAttribute("expBalances",expBalances);
			return "/module/ehraccounting/period/closeForm";
		}
	}
	
	@RequestMapping(value="/module/ehraccounting/period.list",method=RequestMethod.GET)
	public String listPeriods(@RequestParam(value="yearId",required=false) Integer yearId, Model model){
		if (yearId != null ) {
			FiscalYear year = Context.getService(EhraccountingService.class).getFiscalYear(yearId);
			if (year != null) {
				model.addAttribute("periods",year.getPeriods());
			}
		}
		return "/module/ehraccounting/period/listPeriods";
	}
}
