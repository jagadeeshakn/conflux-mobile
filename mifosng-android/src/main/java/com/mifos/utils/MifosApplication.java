/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.utils;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.multidex.MultiDex;

import com.mifos.objects.client.Role;
import com.mifos.services.API;
import com.orm.SugarApp;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ishankhanna on 13/03/15.
 */
public class MifosApplication extends SugarApp {

    public API api;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
