/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.comgroup.tzmedia.server.orderprocessing.entity;

/**
 *
 * @author pcnsh197
 */
public class CustomerOrderReportResult {

    private String reportName;

    public CustomerOrderReportResult(String reportName) {
        this.reportName = reportName;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }
}
