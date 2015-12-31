package com.mifos.objects.db;

import com.orm.SugarRecord;

/**
 * Created by conflux37 on 12/22/2015.
 */
public class UserDetails extends SugarRecord<UserDetails>{
    private String username;
    private int userId;
    private int officeId;
    private int staffId;
    private String officeName;

    public UserDetails()
    {

    }
    public UserDetails(String username, int userId, int officeId, String officeName,int staffId) {
        this.username = username;
        this.userId = userId;
        this.officeId = officeId;
        this.officeName = officeName;
        this.staffId = staffId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getOfficeId() {
        return officeId;
    }

    public void setOfficeId(int officeId) {
        this.officeId = officeId;
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }
}
