package com.mifos.objects.db;

/**
 * Created by conflux37 on 11/2/2015.
 */
public class CenterCreationResponse {
    private Integer officeId;
    private Integer groupId;
    private Integer resourceId;

    public Integer getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Integer officeId) {
        this.officeId = officeId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public String toString() {
        return "CenterCreationResponse{" +
                "officeId=" + officeId +
                ", groupId=" + groupId +
                ", resourceId=" + resourceId +
                '}';
    }
}
