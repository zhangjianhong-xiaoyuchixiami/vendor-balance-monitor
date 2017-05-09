package org.qydata.po;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by jonhn on 2017/4/19.
 */
public class ApiCost implements Serializable {

    private Integer id;
    private Integer years;
    private Integer months;
    private Integer days;
    private Integer apiId;
    private Integer totleCost;
    private Integer usageAmount;
    private Integer feeAmount;
    private Date consuTime;
    private Timestamp timestampl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getYears() {
        return years;
    }

    public void setYears(Integer years) {
        this.years = years;
    }

    public Integer getMonths() {
        return months;
    }

    public void setMonths(Integer months) {
        this.months = months;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public Integer getApiId() {
        return apiId;
    }

    public void setApiId(Integer apiId) {
        this.apiId = apiId;
    }

    public Integer getTotleCost() {
        return totleCost;
    }

    public void setTotleCost(Integer totleCost) {
        this.totleCost = totleCost;
    }

    public Integer getUsageAmount() {
        return usageAmount;
    }

    public void setUsageAmount(Integer usageAmount) {
        this.usageAmount = usageAmount;
    }

    public Date getConsuTime() {
        return consuTime;
    }

    public void setConsuTime(Date consuTime) {
        this.consuTime = consuTime;
    }

    public Timestamp getTimestampl() {
        return timestampl;
    }

    public void setTimestampl(Timestamp timestampl) {
        this.timestampl = timestampl;
    }

    public Integer getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(Integer feeAmount) {
        this.feeAmount = feeAmount;
    }
}
