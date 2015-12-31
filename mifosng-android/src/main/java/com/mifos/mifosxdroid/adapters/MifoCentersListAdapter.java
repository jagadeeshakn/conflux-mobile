/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.db.MeetingCenter;
import com.mifos.objects.group.Center;

import java.util.List;

/**
 * Created by ishankhanna on 11/03/14.
 */
public class MifoCentersListAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private MeetingCenter[] centers;
    private Resources resources;

    public MifoCentersListAdapter(Context context, MeetingCenter[] centers){

        layoutInflater = LayoutInflater.from(context);
        this.centers = centers;
        resources = context.getResources();

    }

    @Override
    public int getCount() {
        return this.centers.length;
    }

    @Override
    public MeetingCenter getItem(int i) {
        return this.centers[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder;

        if(view==null){
            view = layoutInflater.inflate(R.layout.row_new_center_list_item,viewGroup,false);
            viewHolder = new ViewHolder();
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }

        /*viewHolder.tv_center_id = (TextView) view.findViewById(R.id.tv_center_id);*/
        viewHolder.tv_center_name = (TextView) view.findViewById(R.id.tv_center_name);
        /*viewHolder.tv_staff_id = (TextView) view.findViewById(R.id.tv_staff_id);*/
        viewHolder.tv_staff_name = (TextView) view.findViewById(R.id.tv_staff_name);
       /* viewHolder.tv_office_id = (TextView) view.findViewById(R.id.tv_office_id);*/
       /* viewHolder.tv_office_name = (TextView) view.findViewById(R.id.tv_office_name);*/

       /* viewHolder.tv_center_id.setText(resources.getString(R.string.center_id)+ ": " +centers[i].getId());*/
        viewHolder.tv_center_name.setText(centers[i].getName());

       /* viewHolder.tv_staff_id.setText(resources.getString(R.string.staff_id)+ ": " +centers[i].getStaffId());*/
        viewHolder.tv_staff_name.setText(centers[i].getStaffName());

       /* viewHolder.tv_office_id.setText(resources.getString(R.string.office_id)+ ": " +centers[i].getOfficeId());*/
/*        viewHolder.tv_office_name.setText(centers[i].getOfficeId());*/

        return view;
    }

    public static class ViewHolder{

        TextView tv_center_name;
        TextView tv_center_id;
        TextView tv_staff_name;
        TextView tv_staff_id;
        TextView tv_office_name;
        TextView tv_office_id;

    }
}
