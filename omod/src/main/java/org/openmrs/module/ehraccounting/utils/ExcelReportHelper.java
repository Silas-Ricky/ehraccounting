package org.openmrs.module.ehraccounting.utils;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.openmrs.module.ehraccounting.api.model.ExpenseBalance;
import org.openmrs.module.ehraccounting.api.model.FiscalPeriod;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.util.List;


public class ExcelReportHelper {
	
	
	public static void generatePaymentReport(List<ExpenseBalance> balances, ServletOutputStream outStream) throws IOException {
		HSSFWorkbook wb = new HSSFWorkbook();
	    HSSFSheet sheet = wb.createSheet("Monthly payment and Commitment Summary");
	    
	    // Header
	    HSSFRow row = sheet.createRow(0);

	    // Create a cell and put a date value in it.  The first cell is not styled
	    // as a date.
	   
	    HSSFCell cell = null;
	    for (int i=0; i< PaymentExportConstant.headers.length; i++) {
	    	cell = row.createCell(i);
		    cell.setCellValue(PaymentExportConstant.headers[i]);
	    }
	    ExpenseBalance  balance = null;
	    for(int i=1; i<= balances.size();i++) {
	    	balance = balances.get(i-1);
	    	row = sheet.createRow(i);
	    	cell = row.createCell(PaymentExportConstant.ITEM_CODE_INDEX);
	    	cell.setCellValue(balance.getAccount().getAccountNumber());
	    	
	    	cell = row.createCell(PaymentExportConstant.DESCRIPTION_INDEX);
	    	cell.setCellValue(balance.getAccount().getName());
	    	
	    	cell = row.createCell(PaymentExportConstant.NEW_AIE_INDEX);
	    	cell.setCellValue(balance.getNewAIE().doubleValue());
	    	
	    	cell = row.createCell(PaymentExportConstant.CUMMULATIVE_AIE_INDEX);
	    	cell.setCellValue(balance.getCummulativeAIE().doubleValue());
	    	
	    	cell = row.createCell(PaymentExportConstant.CURRENT_PAYMENT_INDEX);
	    	cell.setCellValue(balance.getCurrentPayment().doubleValue());
	    	
	    	cell = row.createCell(PaymentExportConstant.CCUMMULATIVE_PAYMENT_INDEX);
	    	cell.setCellValue(balance.getCummulativePayment().doubleValue());
	    	
	    	cell = row.createCell(PaymentExportConstant.AVAILABLE_BALANCE_INDEX);
	    	cell.setCellValue(balance.getAvailableBalance().doubleValue());
	    }

	    // we style the second cell as a date (and time).  It is important to
	    // create a new cell style from the workbook otherwise you can end up
	    // modifying the built in style and effecting not only this cell but other cells.
//	    CellStyle cellStyle = wb.createCellStyle();
//	    cellStyle.setDataFormat(   createHelper.createDataFormat().getFormat("m/d/yy h:mm"));
//	    cell = row.createCell(1);
//	    cell.setCellValue(new Date());
//	    cell.setCellStyle(cellStyle);

	    //you can also set date as java.util.Calendar
//	    cell = row.createCell(2);
//	    cell.setCellValue(Calendar.getInstance());
//	    cell.setCellStyle(cellStyle);
		
	    
	    wb.write(outStream);
	    outStream.flush();
		
	}
	
	public static void generateCashReport(FiscalPeriod period, ServletOutputStream outStream) throws IOException {
		HSSFWorkbook wb = new HSSFWorkbook();
	    HSSFSheet sheet = wb.createSheet("Monthly Cash Analysis Report");
	    
	    // Header
	    HSSFRow row = sheet.createRow(0);

	    // Create a cell and put a date value in it.  The first cell is not styled
	    // as a date.
	   
	    String month = period.getName();
	    String year = period.getFiscalYear().getName();
	    
	    HSSFCell cell = null;
    	cell = row.createCell(0);
	    cell.setCellValue("MONTHLY COLLECTIONS BY FACILITY");
	    
	    sheet.addMergedRegion(new CellRangeAddress(
            0, //first row (0-based)
            0, //last row  (0-based)
            0, //first column (0-based)
            5  //last column  (0-based)
	    		));
	    
		cell = row.createCell(6);
	    cell.setCellValue("Facility: MACHAKOS LEVEL 5 HOSPITAL");
	    
	    sheet.addMergedRegion(new CellRangeAddress(
            0, //first row (0-based)
            0, //last row  (0-based)
            6, //first column (0-based)
            13  //last column  (0-based)
	    		));
	    
	    cell = row.createCell(14);
	    cell.setCellValue("Month: "+month);
	    
	    sheet.addMergedRegion(new CellRangeAddress(
            0, //first row (0-based)
            0, //last row  (0-based)
            14, //first column (0-based)
           16  //last column  (0-based)
	    		));
	    
	    cell = row.createCell(17);
	    cell.setCellValue("Year: "+year);
	    sheet.addMergedRegion(new CellRangeAddress(
            0, //first row (0-based)
            0, //last row  (0-based)
            17, //first column (0-based)
           19  //last column  (0-based)
	    		));
	    cell = row.createCell(20);
	    cell.setCellValue("FLS03D");
	    sheet.addMergedRegion(new CellRangeAddress(
            0, //first row (0-based)
            0, //last row  (0-based)
           20, //first column (0-based)
          23  //last column  (0-based)
	    		));
	    
	    row = sheet.createRow(1);
		cell = row.createCell(0);
	    cell.setCellValue("Date");
	    
	    
		cell = row.createCell(1);
	    cell.setCellValue("TOTAL SALES");
	    
	    cell = row.createCell(2);
	    cell.setCellValue( "BREAKDOWN BY DEPARTMENT - Specify if Breakdown is by : [ ] SALES [ ] CASH COLLECTION");
	    
	    sheet.addMergedRegion(new CellRangeAddress(
            1, //first row (0-based)
            1, //last row  (0-based)
           2, //first column (0-based)
         10  //last column  (0-based)
	    		));
	    
	    cell = row.createCell(11);
	    cell.setCellValue( "CASH RECEIPTS");
	    
	    sheet.addMergedRegion(new CellRangeAddress(
            1, //first row (0-based)
            1, //last row  (0-based)
           11, //first column (0-based)
        13  //last column  (0-based)
	    		));
	    
	    cell = row.createCell(14);
	    cell.setCellValue( "TOTAL CASH");
	    
	    cell = row.createCell(15);
	    cell.setCellValue( "REVENUE NOT COLLECTED");
	    
	    sheet.addMergedRegion(new CellRangeAddress(
            1, //first row (0-based)
            1, //last row  (0-based)
           15, //first column (0-based)
        19  //last column  (0-based)
	    		));
	    
	    cell = row.createCell(20);
	    cell.setCellValue( "BANK");
	    
	    sheet.addMergedRegion(new CellRangeAddress(
            1, //first row (0-based)
            1, //last row  (0-based)
          20, //first column (0-based)
        22 //last column  (0-based)
	    		));
	    
	    cell = row.createCell(23);
	    cell.setCellValue( "MONTHLY BREAKDOWN OF OTHER INCOME");
	    
	    sheet.addMergedRegion(new CellRangeAddress(
            1, //first row (0-based)
            1, //last row  (0-based)
          23, //first column (0-based)
        24 //last column  (0-based)
	    		));
	    
	    row = sheet.createRow(2);
	    for (int i=0; i< CashExportConstant.row3.length; i++) {
	    	cell = row.createCell(i);
		    cell.setCellValue(CashExportConstant.row3[i]);
		    
		    
	    }	 
	    
	    
	    
	    
	    wb.write(outStream);
	    outStream.flush();
		
	}
}
