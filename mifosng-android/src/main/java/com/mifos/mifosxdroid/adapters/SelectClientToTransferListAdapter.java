/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.QuickContactBadge;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.client.Client;
import com.mifos.services.API;
import com.mifos.utils.MifosApplication;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ishankhanna on 27/02/14.
 */
public class SelectClientToTransferListAdapter extends BaseAdapter {

    LayoutInflater layoutInflater;
    List<Client> pageItems;
    API api;
    ReusableViewHolder reusableViewHolder;

    public boolean getItemSelected(int i) {
        return itemSelected.get(i);
    }

    public void setItemSelected(int i,boolean itemSelected) {
        this.itemSelected.add(i,itemSelected);
    }

    public List<Boolean> getItemSelected() {
        return itemSelected;
    }

    List<Boolean>itemSelected;

    public SelectClientToTransferListAdapter(Context context, List<Client> pageItems, API api){

        layoutInflater = LayoutInflater.from(context);
        this.pageItems = pageItems;
        this.api=api;
        itemSelected = new ArrayList<Boolean>(Arrays.asList(new Boolean[pageItems.size()]));
        Collections.fill(itemSelected,Boolean.FALSE);
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
    public View getView(final int position, View view, ViewGroup viewGroup) {
        if(view==null)
        {
            view = layoutInflater.inflate(R.layout.row_client_name,null);
            reusableViewHolder = new ReusableViewHolder(view);
            view.setTag(reusableViewHolder);

        }else
        {
            reusableViewHolder = (ReusableViewHolder) view.getTag();
        }


        reusableViewHolder.tv_clientName.setText(pageItems.get(position).getFirstname()+" "
                +pageItems.get(position).getLastname());
        reusableViewHolder.tv_clientAccountNumber.setText(pageItems.get(position).getAccountNo().toString());
        if (itemSelected.get(position))
        {
            reusableViewHolder.tv_clientName.setTextColor(Color.WHITE);
            reusableViewHolder.tv_clientAccountNumber.setTextColor(Color.WHITE);
            reusableViewHolder.relativeLayout.setBackgroundColor(Color.rgb 	(32,178,170));
        }
        else
        {
            reusableViewHolder.tv_clientName.setTextColor(Color.BLACK);
            reusableViewHolder.tv_clientAccountNumber.setTextColor(Color.BLACK);
            reusableViewHolder.relativeLayout.setBackgroundColor(Color.TRANSPARENT);
        }

        return view;
    }

    static class ReusableViewHolder{

        @InjectView(R.id.tv_clientName) TextView tv_clientName;
        @InjectView(R.id.tv_clientAccountNumber) TextView tv_clientAccountNumber;
        @InjectView(R.id.quickContactBadge) QuickContactBadge quickContactBadge;
        @InjectView(R.id.tableRow)
        RelativeLayout relativeLayout;

        public ReusableViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

    }


}
