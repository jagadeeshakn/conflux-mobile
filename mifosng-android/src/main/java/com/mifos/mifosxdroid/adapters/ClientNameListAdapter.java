/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.client.Client;
import com.mifos.services.API;
import com.mifos.utils.MifosApplication;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ishankhanna on 27/02/14.
 */
public class ClientNameListAdapter extends BaseAdapter {

    LayoutInflater layoutInflater;
    List<Client> pageItems;
    API api;
    ReusableViewHolder reusableViewHolder;

    public ClientNameListAdapter(Context context, List<Client> pageItems,API api){

        layoutInflater = LayoutInflater.from(context);
        this.pageItems = pageItems;
        this.api=api;
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

        return view;
    }

     static class ReusableViewHolder{

         @InjectView(R.id.tv_clientName) TextView tv_clientName;
         @InjectView(R.id.tv_clientAccountNumber) TextView tv_clientAccountNumber;
         @InjectView(R.id.quickContactBadge) QuickContactBadge quickContactBadge;

         public ReusableViewHolder(View view) {
             ButterKnife.inject(this, view);
         }

    }


}
