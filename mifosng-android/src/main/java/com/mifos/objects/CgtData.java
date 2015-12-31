package com.mifos.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by conflux37 on 11/25/2015.
 */
public class CgtData {

    private Integer id;
    private Integer groupId;
    private Integer day;
    private List<Integer> plannedDate = new ArrayList<Integer>();

    private List<Integer> doneDate= new ArrayList<Integer>();
    private String comments;

    private Integer updatedByUserID;

    private List<ClientMember> clientMembers = new ArrayList<ClientMember>();

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

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public List<Integer> getPlannedDate() {
        return plannedDate;
    }

    public void setPlannedDate(List<Integer> plannedDate) {
        this.plannedDate = plannedDate;
    }


    public List<Integer> getDoneDate() {
        return doneDate;
    }

    public void setDoneDate(List<Integer> doneDate) {
        this.doneDate = doneDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }


    public Integer getUpdatedByUserID() {
        return updatedByUserID;
    }

    /**
     *
     * @param updatedByUserID
     * The updatedByUserID
     */
    public void setUpdatedByUserID(Integer updatedByUserID) {
        this.updatedByUserID = updatedByUserID;
    }

    /**
     *
     * @return
     * The clientMembers
     */
    public List<ClientMember> getClientMembers() {
        return clientMembers;
    }

    /**
     *
     * @param clientMembers
     * The clientMembers
     */
    public void setClientMembers(List<ClientMember> clientMembers) {
        this.clientMembers = clientMembers;
        toString();
    }

    @Override
    public String toString() {
        return "CgtData{" +
                "id=" + id +
                ", groupId=" + groupId +
                ", day=" + day +
                ", plannedDate=" + plannedDate +
                ", doneDate='" + doneDate + '\'' +
                ", comments='" + comments + '\'' +
                ", updatedByUserID=" + updatedByUserID +
                ", clientMembers=" + clientMembers +
                '}';
    }
}
