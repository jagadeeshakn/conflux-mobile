package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.CgtData;
import com.mifos.objects.ClientMember;
import com.mifos.objects.client.Client;
import com.mifos.services.API;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by conflux37 on 11/26/2015.
 */
public class CgtInformationAdapter extends BaseAdapter{

    LayoutInflater layoutInflater;
    List<CgtData> pageItems;
    ReusableViewHolder reusableViewHolder;

    public CgtInformationAdapter(Context context, List<CgtData> pageItems) {
        layoutInflater = LayoutInflater.from(context);
        this.pageItems = pageItems;
    }
    @Override
    public int getCount() {
        return pageItems.size();
    }

    @Override
    public Object getItem(int i) {
        return pageItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = layoutInflater.inflate(R.layout.row_cgt_info, null);
            reusableViewHolder = new ReusableViewHolder(view);
            view.setTag(reusableViewHolder);

        } else {
            reusableViewHolder = (ReusableViewHolder) view.getTag();
        }

        reusableViewHolder.daysCount.setText("CGT - " + pageItems.get(position).getDay());
        String date=pageItems.get(position).getDoneDate().get(2)+"/"+pageItems.get(position).getDoneDate().get(1)+"/"+pageItems.get(position).getDoneDate().get(0);
        reusableViewHolder.date.setText(date);
        int count=0;
        count=absentCount(pageItems.get(position));
        if(count>0)
        {
            reusableViewHolder.absentCount.setText("A# "+count);
        }
        else
        {
            reusableViewHolder.absentCount.setText("");
        }
        return view;
    }

    public int absentCount(CgtData cgtData)
    {
        int count =0;
        for(ClientMember clientMember:cgtData.getClientMembers())
        {
            if(clientMember.getAttendance().equals("A"))
            {
                count++;
            }
        }
        return  count;
    }
    public class ReusableViewHolder
    {
        @InjectView(R.id.days_count)
        TextView daysCount;
        @InjectView(R.id.dates)
        TextView date;
        @InjectView(R.id.absent_count)
        TextView absentCount;

        public ReusableViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
