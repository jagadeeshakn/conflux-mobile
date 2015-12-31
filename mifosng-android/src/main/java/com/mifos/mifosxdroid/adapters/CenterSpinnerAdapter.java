package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.group.Center;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by conflux37 on 12/21/2015.
 */
public class CenterSpinnerAdapter extends BaseAdapter{
    private Context context;
    private List<Center> centers;
    private LayoutInflater layoutInflater;

    public CenterSpinnerAdapter(Context context,List<Center> centers)
    {
        layoutInflater = LayoutInflater.from(context);
        this.centers = centers;
        this.context = context;
    }

    @Override
    public int getCount() {
        return centers.size();
    }

    @Override
    public Object getItem(int i) {
        return centers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return centers.get(i).getId();
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
        viewHolder.officeName.setText(centers.get(i).getName());
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
        viewHolder.officeName.setText(centers.get(position).getName());
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
