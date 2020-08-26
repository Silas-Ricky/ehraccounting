package org.openmrs.module.ehraccounting.utils;


public class PaymentExportConstant {
	public static final String ITEMCODE = "Item Code";
	public static final Integer ITEM_CODE_INDEX = 0;
	public static final String DESCRIPTION = "Description";
	public static final Integer DESCRIPTION_INDEX = 1;
	public static final String NEW_AIE = "New AIE";
	public static final Integer NEW_AIE_INDEX = 2;
	public static final String CUMMULATIVE_AIE = "Cummulative AIE";
	public static final Integer CUMMULATIVE_AIE_INDEX = 3;
	public static final String CURRENT_PAYMENT = "Payment made this month";
	public static final Integer CURRENT_PAYMENT_INDEX = 4;
	public static final String CUMMULATIVE_PAYMENT = "Cummulative Payments this F/Y";
	public static final Integer CCUMMULATIVE_PAYMENT_INDEX = 5;
	public static final String AVAILABLE_BALANCE = "Available Balance";
	public static final Integer AVAILABLE_BALANCE_INDEX = 6;
	public static final  String[] headers = {
		PaymentExportConstant.ITEMCODE, 
		PaymentExportConstant.DESCRIPTION, 
		PaymentExportConstant.NEW_AIE, 
		PaymentExportConstant.CUMMULATIVE_AIE,
		PaymentExportConstant.CURRENT_PAYMENT, 
		PaymentExportConstant.CUMMULATIVE_PAYMENT,
		PaymentExportConstant.AVAILABLE_BALANCE} ;
}
