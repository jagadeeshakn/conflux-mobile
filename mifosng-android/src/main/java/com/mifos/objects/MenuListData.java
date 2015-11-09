package com.mifos.objects;

/**
 * Created by conflux37 on 10/22/2015.
 */
import android.app.Application;

import java.util.ArrayList;

/**
 * Created by conflux37 on 10/21/2015.
 */
public class MenuListData {
    private static String[] menuListArray;

    public static void setMenuListArray(String[] menuListArray) {
        MenuListData.menuListArray = menuListArray;
    }

    public static ArrayList<MenuList> getMenuList(){
        ArrayList<MenuList> list=new ArrayList<>();
        for(int i=0;i<menuListArray.length;i++) {
            MenuList menuList = new MenuList();
            menuList.name = menuListArray[i];
            menuList.imageName=menuListArray[i].replaceAll("\\s+","").toLowerCase();
            list.add(menuList);

        }
        return list;
    }
    public static MenuList getItem(String _id) {
        for (MenuList place :getMenuList() ) {
            if (place.id.equals(_id)) {
                return place;
            }
        }
        return null;
    }

}
