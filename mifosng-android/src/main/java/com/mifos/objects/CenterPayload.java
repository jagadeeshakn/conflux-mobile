package com.mifos.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by conflux37 on 11/2/2015.
 */
public class CenterPayload {


    private String name;
    private Integer officeId;
    private String locale="en";
    private String dateFormat ="dd MM yyyy";
    private Integer staffId;
    private Boolean active;
    private String activationDate;
    private String submittedOnDate;
    private List<String> groupMembers = new ArrayList<String>();
    private String externalId;

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }


    @Override
    public String toString() {
        return "CenterPayload{" +
                "name='" + name + '\'' +
                ", officeId=" + officeId +
                ", locale='" + locale + '\'' +
                ", dateFormat='" + dateFormat + '\'' +
                ", staffId=" + staffId +
                ", active=" + active +
                ", activationDate='" + activationDate + '\'' +
                ", submittedOnDate='" + submittedOnDate + '\'' +
                ", groupMembers=" + groupMembers +
                ", externalId=" + externalId +
                '}';
    }

    public List<String> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(List<String> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public String getSubmittedOnDate() {
        return submittedOnDate;
    }

    public void setSubmittedOnDate(String submittedOnDate) {
        this.submittedOnDate = submittedOnDate;
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
     * The officeId
     */
    public Integer getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Integer officeId) {
        this.officeId = officeId;
    }



    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }


    public String getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(String activationDate) {
        this.activationDate = activationDate;
    }


}
