/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.comgroup.tzmedia.server.report;

import cn.comgroup.tzmedia.server.orderprocessing.entity.CustomerOrder;
import cn.comgroup.tzmedia.server.orderprocessing.entity.CustomerOrderReportResult;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * CustomerOrderReport
 *
 * @author pcnsh197
 */
public class CustomerOrderReport {

    public CustomerOrderReportResult runOrderReport(String deployPath,
            List<CustomerOrder> orders)
            throws ParseException, FileNotFoundException, IOException {
        String reportTemplate = deployPath + File.separator
                + "template" + File.separator + "CustomerOrderReport.xlsx";
        final XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(
                reportTemplate));
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        XSSFSheet sheet = workbook.getSheetAt(0);
        int startRow = 1;
        for (CustomerOrder co : orders) {
            XSSFRow row = sheet.getRow(startRow);
            if (row == null) {
                row = sheet.createRow(startRow);
            }
            XSSFCell cellOrderNumber = row.getCell(0);
            if (cellOrderNumber == null) {
                cellOrderNumber = row.createCell(0);
            }
            
            XSSFCell cellOrderDate = row.getCell(1);
            if (cellOrderDate == null) {
                cellOrderDate = row.createCell(1);
            }
            XSSFCell cellOrderType = row.getCell(2);
            if (cellOrderType == null) {
                cellOrderType = row.createCell(2);
            }
            XSSFCell cellOrderStatus = row.getCell(3);
            if (cellOrderStatus == null) {
                cellOrderStatus = row.createCell(3);
            }
            XSSFCell cellETN = row.getCell(4);
            if (cellETN == null) {
                cellETN = row.createCell(4);
            }
            XSSFCell cellShop = row.getCell(5);
            if (cellShop == null) {
                cellShop = row.createCell(5);
            }
            XSSFCell cellUserId = row.getCell(6);
            if (cellUserId == null) {
                cellUserId = row.createCell(6);
            }
            XSSFCell cellUserName = row.getCell(7);
            if (cellUserName == null) {
                cellUserName = row.createCell(7);
            }
            XSSFCell cellPaymentTerm = row.getCell(8);
            if (cellPaymentTerm == null) {
                cellPaymentTerm = row.createCell(8);
            }
            XSSFCell cellOrderAmount = row.getCell(9);
            if (cellOrderAmount == null) {
                cellOrderAmount = row.createCell(9);
            }
            XSSFCell cellCouponAmount = row.getCell(10);
            if (cellCouponAmount == null) {
                cellCouponAmount = row.createCell(10);
            }
            cellOrderNumber.setCellValue(co.getOrderNumber());
            if(co.getOrderDate()!=null){
                cellOrderDate.setCellValue(dateFormat.format(co.getOrderDate().getTime()));
            }else{
                cellOrderDate.setCellValue(dateFormat.format(co.getOrderTime().getTime()));
            }
            
            cellOrderType.setCellValue(co.getOrderType().toString());
            cellOrderStatus.setCellValue(co.getOrderStatus().toString());
            if(co.getExternalTransactionNumber()!=null){
                 cellETN.setCellValue(co.getExternalTransactionNumber());
            }
           
            cellShop.setCellValue(co.getShopName());
            cellUserId.setCellValue(co.getUserId());
            cellUserName.setCellValue(co.getUserName());
            cellPaymentTerm.setCellValue(co.getPaymentTerm().toString());
            cellOrderAmount.setCellValue(co.getOrderAmount());
            cellCouponAmount.setCellValue(co.getCouponAmount());
            startRow++;
            System.out.println(startRow + " orders in the report");
        }
        DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd-HHMMSS");
        String reportName = "CustomerOrderReport" + dateTimeFormat.format(new Date()) + ".xlsx";
        String reportPath = deployPath + File.separator + reportName;
        FileOutputStream fos = new FileOutputStream(reportPath);
        try (BufferedOutputStream bout = new BufferedOutputStream(fos)) {
            workbook.write(bout);
            bout.flush();
        }
        return new CustomerOrderReportResult(reportName);
    }

}
