package com.mifos.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by conflux37 on 12/4/2015.
 */
public class GrtData {
    private Integer id;
    private Integer groupId;
    private String plannedDate;
    private String doneDate;
    private String comments;
    private Integer UpdatedByID1;
    private Integer UpdatedByID2;
    private List<ClientMember> ClientMembers = new ArrayList<ClientMember>();
    private String grtStatus;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getPlannedDate() {
        return plannedDate;
    }

    public void setPlannedDate(String plannedDate) {
        this.plannedDate = plannedDate;
    }

    public String getDoneDate() {
        return doneDate;
    }

    public void setDoneDate(String doneDate) {
        this.doneDate = doneDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Integer getUpdatedByID1() {
        return UpdatedByID1;
    }

    public void setUpdatedByID1(Integer updatedByID1) {
        UpdatedByID1 = updatedByID1;
    }

    public Integer getUpdatedByID2() {
        return UpdatedByID2;
    }

    public void setUpdatedByID2(Integer updatedByID2) {
        UpdatedByID2 = updatedByID2;
    }

    public List<ClientMember> getClientMembers() {
        return ClientMembers;
    }

    public void setClientMembers(List<ClientMember> clientMembers) {
        ClientMembers = clientMembers;
    }

    public String getGrtStatus() {
        return grtStatus;
    }

    public void setGrtStatus(String grtStatus) {
        this.grtStatus = grtStatus;
    }

    @Override
    public String toString() {
        return "GrtData{" +
                "id=" + id +
                ", groupId=" + groupId +
                ", plannedDate=" + plannedDate +
                ", doneDate=" + doneDate +
                ", comments='" + comments + '\'' +
                ", UpdatedByID1=" + UpdatedByID1 +
                ", UpdatedByID2=" + UpdatedByID2 +
                ", ClientMembers=" + ClientMembers +
                ", grtStatus='" + grtStatus + '\'' +
                '}';
    }
}
