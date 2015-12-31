package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.organisation.Office;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by conflux37 on 12/21/2015.
 */
public class OfficeSpinnerAdapter extends BaseAdapter {
    private Context context;
    private List<Office> offices;
    private LayoutInflater layoutInflater;

    public OfficeSpinnerAdapter(Context context,List<Office> offices)
    {
        layoutInflater = LayoutInflater.from(context);
        this.offices = offices;
        this.context = context;
        System.out.println("the offices are");
        for(Office office:this.offices)
        {
            System.out.println(office);
        }
        System.out.println("the context is "+context);
    }

    @Override
    public int getCount() {
        return offices.size();
    }

    @Override
    public Object getItem(int i) {
        return offices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return offices.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view == null)
        {
            view = layoutInflater.inflate(R.layout.office_spinner_selected_item, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.officeName.setText(offices.get(i).getName());
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
        viewHolder.officeName.setText(offices.get(position).getName());
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
