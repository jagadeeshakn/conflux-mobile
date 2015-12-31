package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.ClientPageItem;
import com.mifos.services.API;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by conflux37 on 12/11/2015.
 */
public class ManageClientsAdapter extends BaseAdapter implements Filterable{
    LayoutInflater layoutInflater;
    List<Client> pageItems;
    private ArrayList<Client> itemsAll;
    private ArrayList<Client> suggestions;
    ReusableViewHolder reusableViewHolder;

    public ManageClientsAdapter(Context context, List<Client> pageItems){
        layoutInflater = LayoutInflater.from(context);
        this.pageItems = pageItems;
        System.out.println("the sored page items are "+pageItems);
        itemsAll = (ArrayList<Client>) pageItems;
        itemsAll = (ArrayList<Client>)itemsAll.clone();
        this.suggestions = new ArrayList<Client>();
    }

    @Override
    public int getCount() {
        return pageItems.size();
    }

    @Override
    public Client getItem(int position) {
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
        reusableViewHolder.tv_clientName.setText(pageItems.get(position).getFirstname()+" "
                +pageItems.get(position).getLastname());
        return view;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            String str = ((Client)(resultValue)).getDisplayName();
            return str;
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if(constraint != null) {
                suggestions.clear();
                for (Client customer : itemsAll) {
                    if(customer.getDisplayName().toLowerCase().startsWith(constraint.toString().toLowerCase())){
                        suggestions.add(customer);
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
            ArrayList<Client> filteredList = (ArrayList<Client>) results.values;
            if(results != null && results.count > 0) {
                suggestions.clear();
                for (Client c : filteredList) {
                    suggestions.add(c);
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
