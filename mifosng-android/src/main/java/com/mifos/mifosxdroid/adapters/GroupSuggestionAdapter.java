package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.client.Client;
import com.mifos.objects.group.Group;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by conflux37 on 12/17/2015.
 */
public class GroupSuggestionAdapter extends BaseAdapter implements Filterable {
    LayoutInflater layoutInflater;
    List<Group> pageItems;
    private ArrayList<Group> itemsAll;
    private ArrayList<Group> suggestions;
    ReusableViewHolder reusableViewHolder;
    private int groupId;
    private Group removeGroup;

    public GroupSuggestionAdapter(Context context, List<Group> pageItems,int groupId){
        layoutInflater = LayoutInflater.from(context);
        this.pageItems = pageItems;
        itemsAll = (ArrayList<Group>) pageItems;
        itemsAll = (ArrayList<Group>)itemsAll.clone();
        this.suggestions = new ArrayList<Group>();
        this.groupId=groupId;
        //remove the current group and display the other groups to which the user has to be transfered.
            for (Group group : pageItems) {
                if (group.getId() == groupId) {
                    removeGroup = group;
                }
            }
            pageItems.remove(removeGroup);
    }

    @Override
    public int getCount() {
        return pageItems.size();
    }

    @Override
    public Group getItem(int position) {
        return pageItems.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if(view==null)
        {
            view = layoutInflater.inflate(R.layout.spinner_item,null);
            reusableViewHolder = new ReusableViewHolder(view);
            view.setTag(reusableViewHolder);
        }else
        {
            reusableViewHolder = (ReusableViewHolder) view.getTag();
        }
            reusableViewHolder.tv_clientName.setText(pageItems.get(position).getName());
        return view;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            String str = ((Group)(resultValue)).getName();
            return str;
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if(constraint != null) {
                suggestions.clear();
                for (Group group : itemsAll) {
                    if(group.getId()!=groupId) {
                        if (group.getName().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                            suggestions.add(group);
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<Group> filteredList = (ArrayList<Group>) results.values;
            if(results != null && results.count > 0) {
                suggestions.clear();
                for (Group group : filteredList) {
                    suggestions.add(group);
                }
                notifyDataSetChanged();
            }
        }
    };


    static class ReusableViewHolder{

        @InjectView(android.R.id.text1)
        TextView tv_clientName;

        public ReusableViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

    }



}
