package com.mifos.objects.client;

import com.mifos.objects.Gender;
import com.mifos.objects.Status;
import com.mifos.objects.SubStatus;
import com.mifos.objects.Timeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by conflux37 on 12/14/2015.
 */
public class ClientPageItem {
    private Integer id;
    private String accountNo;
    private Status status;
    private SubStatus subStatus;
    private Boolean active;
    private List<Integer> activationDate = new ArrayList<Integer>();
    private String firstname;
    private String lastname;
    private String displayName;
    private Gender gender;
    private ClientType clientType;
    private ClientClassification clientClassification;
    private Integer officeId;
    private String officeName;
    private Integer imageId;
    private Boolean imagePresent;
    private Timeline timeline;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public SubStatus getSubStatus() {
        return subStatus;
    }

    public void setSubStatus(SubStatus subStatus) {
        this.subStatus = subStatus;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<Integer> getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(List<Integer> activationDate) {
        this.activationDate = activationDate;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

    public ClientClassification getClientClassification() {
        return clientClassification;
    }

    public void setClientClassification(ClientClassification clientClassification) {
        this.clientClassification = clientClassification;
    }

    public Integer getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Integer officeId) {
        this.officeId = officeId;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public Boolean getImagePresent() {
        return imagePresent;
    }

    public void setImagePresent(Boolean imagePresent) {
        this.imagePresent = imagePresent;
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
    }

    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    @Override
    public String toString() {
        return "ClientPageItem{" +
                "id=" + id +
                ", accountNo='" + accountNo + '\'' +
                ", status=" + status +
                ", subStatus=" + subStatus +
                ", active=" + active +
                ", activationDate=" + activationDate +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", displayName='" + displayName + '\'' +
                ", gender=" + gender +
                ", clientType=" + clientType +
                ", clientClassification=" + clientClassification +
                ", officeId=" + officeId +
                ", officeName='" + officeName + '\'' +
                ", imageId=" + imageId +
                ", imagePresent=" + imagePresent +
                ", timeline=" + timeline +
                ", additionalProperties=" + additionalProperties +
                '}';
    }
}
