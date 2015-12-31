package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.Staff;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by conflux37 on 12/21/2015.
 */
public class StaffNameAdapter extends BaseAdapter {
    private Context context;
    private List<Staff> staffs;
    private LayoutInflater layoutInflater;

    public StaffNameAdapter(Context context,List<Staff> staffs)
    {
        layoutInflater = LayoutInflater.from(context);
        this.staffs = staffs;
        this.context = context;
        System.out.println("the offices are");
        for(Staff staff:this.staffs)
        {
            System.out.println(staff);
        }
        System.out.println("the context is "+context);
    }

    @Override
    public int getCount() {
        return staffs.size();
    }

    @Override
    public Object getItem(int i) {
        return staffs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return staffs.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view == null)
        {
            view = layoutInflater.inflate(R.layout.office_spinner_item, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.officeName.setText(staffs.get(i).getDisplayName());
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null)
        {
            convertView = layoutInflater.inflate(R.layout.office_spinner_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.officeName.setText(staffs.get(position).getDisplayName());
        return convertView;
    }

    public static class ViewHolder
    {
        @InjectView(R.id.tv_office_name)
        CheckedTextView officeName;
        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
