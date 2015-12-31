package com.mifos.objects.db;

import com.mifos.objects.client.Permission;
import com.orm.SugarApp;
import com.orm.SugarRecord;

import java.util.List;

/**
 * Created by conflux37 on 11/20/2015.
 */
public class Permissions extends SugarRecord<Permissions> {

    String permissions;

    public Permissions()
    {

    }
    public Permissions(String permissions)
    {
        this.permissions = permissions;
    }
    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        System.out.println(permissions);

        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return permissions;
    }
}
