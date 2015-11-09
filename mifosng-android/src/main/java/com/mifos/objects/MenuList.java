package com.mifos.objects;

/**
 * Created by conflux37 on 10/22/2015.
 */
import android.content.Context;

import com.mifos.mifosxdroid.R;

/**
 * Created by conflux37 on 10/21/2015.
 */
public class MenuList {
    public String id;
    public String name;
    public String imageName;

    public int getImageResourceId(Context context) {
        String[] strings=context.getResources().getStringArray(R.array.menu_list);
        return context.getResources().getIdentifier(this.imageName, "drawable", context.getPackageName());
    }
}