package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.Status;
import com.mifos.objects.group.Group;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by conflux37 on 11/17/2015.
 */
public class CgtGroupListAdaptor extends BaseAdapter {

    LayoutInflater layoutInflater;
    Context context;
    List<Group> groups = new ArrayList<Group>();

    public CgtGroupListAdaptor(Context context, List<Group> groups) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.groups = groups;
    }
    @Override
    public int getCount() {
        return this.groups.size();
    }

    @Override
    public Group getItem(int i) {
        return this.groups.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ReusableGroupViewHolder reusableGroupViewHolder;

        if(view == null) {
            view = layoutInflater.inflate(R.layout.row_group_list_for_cgt, null);
            reusableGroupViewHolder = new ReusableGroupViewHolder(view);
            view.setTag(reusableGroupViewHolder);
        } else {
            reusableGroupViewHolder = (ReusableGroupViewHolder) view.getTag();
        }

        Group group = groups.get(i);

        reusableGroupViewHolder.tv_groupName.setText(group.getName());

        return view;
    }
    public static class ReusableGroupViewHolder {

        @InjectView(R.id.tv_group_name)
        TextView tv_groupName;

        public ReusableGroupViewHolder(View view) { ButterKnife.inject(this, view); }


    }
}
