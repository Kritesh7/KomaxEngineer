package com.cfcs.komaxengineer.model;

public class ServiceHourListDataModel {

    private String ServiceHrID;

    private String EngineerID;

    private String ServiceHrDate;

    private String TodayStatusName;

    private String TomorrowPlanName;

    private String TotalHrs;

    public ServiceHourListDataModel(String serviceHrID, String engineerID, String serviceHrDate, String todayStatusName, String tomorrowPlanName, String totalHrs) {
        this.ServiceHrID = serviceHrID;
        this.EngineerID = engineerID;
        this.ServiceHrDate = serviceHrDate;
        this.TodayStatusName = todayStatusName;
        this.TomorrowPlanName = tomorrowPlanName;
        this.TotalHrs = totalHrs;

    }


    public String getServiceHrID() {
        return ServiceHrID;
    }

    public void setServiceHrID(String ServiceHrID) {
        this.ServiceHrID = ServiceHrID;
    }


    public String getEngineerID() {
        return EngineerID;
    }

    public void setEngineerID(String EngineerID) {
        this.EngineerID = EngineerID;
    }

    public String getServiceHrDate() {
        return ServiceHrDate;
    }

    public void setServiceHrDate(String ServiceHrDate) {
        this.ServiceHrDate = ServiceHrDate;
    }

    public String getTodayStatusName() {
        return TodayStatusName;
    }

    public void setTodayStatusName(String TodayStatusName) {
        this.TodayStatusName = TodayStatusName;
    }


    public String getTomorrowPlanName() {
        return TomorrowPlanName;
    }

    public void setTomorrowPlanName(String TomorrowPlanName) {
        this.TomorrowPlanName = TomorrowPlanName;
    }

    public String getTotalHrs() {
        return TotalHrs;
    }

    public void setTotalHrs(String TotalHrs) {
        this.TotalHrs = TotalHrs;
    }

}
