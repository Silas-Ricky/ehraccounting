package org.openmrs.module.ehraccounting.web.controller.fiscalyear;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.ehraccounting.api.EhraccountingService;
import org.openmrs.module.ehraccounting.api.model.FiscalPeriod;
import org.openmrs.module.ehraccounting.api.model.FiscalYear;
import org.openmrs.module.ehraccounting.api.model.GeneralStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping("/module/ehraccounting/closeFiscalYear.htm")
public class CloseFiscalYearController {
	
	Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(method=RequestMethod.GET)
	public String view(@RequestParam(value = "id", required = false) Integer id,
	                   Model model) {
		
		AccountingService service = Context.getService(AccountingService.class);
		if(id != null) {
			FiscalYear fiscalYear = service.getFiscalYear(id);
			model.addAttribute("fiscalYear",fiscalYear);
			// get all open periods of this fiscalYear
			List<FiscalPeriod> periods = fiscalYear.getPeriods();
			for( FiscalPeriod period : periods) {
				if( period.getStatus().equals(GeneralStatus.ACTIVE)) {
					model.addAttribute("hasOpenPeriod", true);
					return "/module/ehraccounting/fiscalyear/closeFiscalYear";
				}
			}
			
			// All Periods are closed, can proceed to close this FiscalYear
		
			
			Collection<FiscalYear> years = service.getListFutureYear(fiscalYear.getEndDate());
			
			if (years.isEmpty()) {
				model.addAttribute("hasNextYear",false);
			} else {
				model.addAttribute("hasNextYear",true);
				model.addAttribute("listFiscalYear",years);
			}
			
			
			return "/module/ehraccounting/fiscalyear/closeFiscalYear";
		} else {
			return "/module/ehraccounting/fiscalyear/closeFiscalYear";
		}
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public String post(
	                   @RequestParam(value = "nextYearId", required = false) Integer nextYearId,
	                   @RequestParam(value = "closeYearId", required = false) Integer closeYearId) {
	
		log.debug("nextYearId: "+nextYearId);
		log.debug("closeYearId: "+closeYearId);
		
		EhraccountingService service = Context.getService(EhraccountingService.class);
		
		service.closeFiscalYear(closeYearId,nextYearId);
		
		return "redirect:/module/ehraccounting/fiscalyear.list";
	
	}
}
