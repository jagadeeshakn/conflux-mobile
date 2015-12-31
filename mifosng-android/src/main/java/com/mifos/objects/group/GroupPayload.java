package com.mifos.objects.group;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by conflux37 on 12/21/2015.
 */
public class GroupPayload {
    private List<Object> clientMembers = new ArrayList<Object>();
    private String officeId;
    private String name;
    private Boolean active;
    private Integer staffId;
    private String activationDate;
    private String centerId;
    private String submittedOnDate;
    private String locale="en";
    private String dateFormat ="dd MM yyyy";

    /**
     *
     * @return
     * The clientMembers
     */
    public List<Object> getClientMembers() {
        return clientMembers;
    }

    /**
     *
     * @param clientMembers
     * The clientMembers
     */
    public void setClientMembers(List<Object> clientMembers) {
        this.clientMembers = clientMembers;
    }

    /**
     *
     * @return
     * The officeId
     */
    public String getOfficeId() {
        return officeId;
    }

    /**
     *
     * @param officeId
     * The officeId
     */
    public void setOfficeId(String officeId) {
        this.officeId = officeId;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The active
     */
    public Boolean getActive() {
        return active;
    }

    /**
     *
     * @param active
     * The active
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     *
     * @return
     * The staffId
     */
    public Integer getStaffId() {
        return staffId;
    }

    /**
     *
     * @param staffId
     * The staffId
     */
    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    /**
     *
     * @return
     * The activationDate
     */
    public String getActivationDate() {
        return activationDate;
    }

    /**
     *
     * @param activationDate
     * The activationDate
     */
    public void setActivationDate(String activationDate) {
        this.activationDate = activationDate;
    }

    /**
     *
     * @return
     * The centerId
     */
    public String getCenterId() {
        return centerId;
    }

    /**
     *
     * @param centerId
     * The centerId
     */
    public void setCenterId(String centerId) {
        this.centerId = centerId;
    }

    /**
     *
     * @return
     * The submittedOnDate
     */
    public String getSubmittedOnDate() {
        return submittedOnDate;
    }

    /**
     *
     * @param submittedOnDate
     * The submittedOnDate
     */
    public void setSubmittedOnDate(String submittedOnDate) {
        this.submittedOnDate = submittedOnDate;
    }

    /**
     *
     * @return
     * The locale
     */
    public String getLocale() {
        return locale;
    }

    /**
     *
     * @param locale
     * The locale
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }

    /**
     *
     * @return
     * The dateFormat
     */
    public String getDateFormat() {
        return dateFormat;
    }

    /**
     *
     * @param dateFormat
     * The dateFormat
     */
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    @Override
    public String toString() {
        return "GroupPayload{" +
                "clientMembers=" + clientMembers +
                ", officeId='" + officeId + '\'' +
                ", name='" + name + '\'' +
                ", active=" + active +
                ", staffId=" + staffId +
                ", activationDate='" + activationDate + '\'' +
                ", centerId='" + centerId + '\'' +
                ", submittedOnDate='" + submittedOnDate + '\'' +
                ", locale='" + locale + '\'' +
                ", dateFormat='" + dateFormat + '\'' +
                '}';
    }
}
