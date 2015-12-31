package com.mifos.objects.group;

/**
 * Created by conflux37 on 12/21/2015.
 */
public class GroupCreationResponseData {
    private Integer officeId;
    private Integer groupId;
    private Integer resourceId;

    /**
     *
     * @return
     * The officeId
     */
    public Integer getOfficeId() {
        return officeId;
    }

    /**
     *
     * @param officeId
     * The officeId
     */
    public void setOfficeId(Integer officeId) {
        this.officeId = officeId;
    }

    /**
     *
     * @return
     * The groupId
     */
    public Integer getGroupId() {
        return groupId;
    }

    /**
     *
     * @param groupId
     * The groupId
     */
    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    /**
     *
     * @return
     * The resourceId
     */
    public Integer getResourceId() {
        return resourceId;
    }

    /**
     *
     * @param resourceId
     * The resourceId
     */
    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }
}
