package com.mifos.mifosxdroid.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.fragments.ManageClientFragment;
import com.mifos.objects.client.Client;
import com.mifos.services.API;
import com.orm.dsl.Collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by conflux37 on 12/9/2015.
 */
public class ManageClientListAdapter extends ArrayAdapter<Client> {
    LayoutInflater layoutInflater;
    private ManageClientListAdapter manageClientListAdapter;
    final Context context;
    private List<Client> pageItems;
    private API api;
    private ReusableViewHolder reusableViewHolder;
    private final String TAG = getClass().getSimpleName();
    private List<Client> temDeleteList;
    private List<Client> tempAddList;
    private Client tempClient;
    private List<Client> tempAddListRemove =new ArrayList<Client>();
    private Activity activity;

    public static void setItemChecked(ArrayList<Boolean> itemChecked) {
        ManageClientListAdapter.itemChecked = itemChecked;
        Collections.fill(ManageClientListAdapter.itemChecked,Boolean.FALSE);
    }

    public static ArrayList<Boolean> getItemChecked() {
        return itemChecked;
    }

    private static ArrayList<Boolean> itemChecked;

    public void setManageClientListAdapter(ManageClientListAdapter manageClientListAdapter) {
        this.manageClientListAdapter = manageClientListAdapter;
    }



    public ManageClientListAdapter(Context context,Activity activity,List<Client> clientList,List<Client> tempDeleteList,List<Client> tempAddList,List<Client> tempAddListRemove)
    {
        super(context,R.layout.row_manage_clients_list,clientList);
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        pageItems = clientList;
        this.activity = activity;
        this.tempAddList = tempAddList;
        this.temDeleteList = tempDeleteList;
        this.tempAddListRemove = tempAddListRemove;
        itemChecked = new ArrayList<Boolean>(Arrays.asList(new Boolean[clientList.size()]));
        Collections.fill(itemChecked,Boolean.FALSE);

    }

    @Override
    public void insert(Client object, int index) {
        super.insert(object, index);
        tempAddList.add(object);
        itemChecked.add(index,false);

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
    public View getView(final int position, View view, final ViewGroup parent) {
        if (view == null) {
            view = layoutInflater.inflate(R.layout.row_manage_clients_list, null);
            reusableViewHolder = new ReusableViewHolder(view);
            view.setTag(reusableViewHolder);

        } else
        {
            reusableViewHolder = (ReusableViewHolder) view.getTag();
        }
        reusableViewHolder.tvClientName.setText(pageItems.get(position).getFirstname()+", "+pageItems.get(position).getLastname());
        reusableViewHolder.tvClientAccountNumber.setText(pageItems.get(position).getAccountNo());
        if (itemChecked.get(position)) {
            reusableViewHolder.chk_bx_delete.setChecked(true);
            changeColor(reusableViewHolder, Color.RED);
        }
        else
        {
            if(tempAddList.contains(manageClientListAdapter.getItem(position)))
            {
                reusableViewHolder.chk_bx_delete.setChecked(false);
                changeColor(reusableViewHolder, Color.rgb(104, 181, 35));
            }
            else
            {
                reusableViewHolder.chk_bx_delete.setChecked(false);
                changeColor(reusableViewHolder, Color.BLACK);
            }
        }
        reusableViewHolder.rl_row_client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReusableViewHolder reusableViewHolder = (ReusableViewHolder) view.getTag();
                if(itemChecked.get(position))
                {
                    System.out.println("button is relative checked");
                    tempClient = pageItems.get(position);
                    itemChecked.set(position, false);
                    if(tempAddListRemove.contains(tempClient))
                    {
                        tempAddListRemove.remove(tempClient);
                        tempAddList.add(tempClient);
                        changeColor(reusableViewHolder, Color.rgb(104, 181, 35));
                    }
                   else
                    {
                        temDeleteList.remove(tempClient);
                        changeColor(reusableViewHolder, Color.BLACK);

                    }
                    reusableViewHolder.chk_bx_delete.setChecked(false);
                    tempClient=null;
                }
                else
                {
                    System.out.println("button is relative unchecked");
                    itemChecked.set(position,true);
                    tempClient=pageItems.get(position);
                    if(tempAddList.contains(tempClient))
                    {
                        tempAddListRemove.remove(tempClient);
                        tempAddListRemove.add(tempClient);
                    }
                    else
                    {
                        temDeleteList.add(tempClient);
                    }
                    tempClient = null;
                    changeColor(reusableViewHolder, Color.RED);
                    reusableViewHolder.tvClientName.setTextColor(Color.RED);
                    reusableViewHolder.tvClientAccountNumber.setTextColor(Color.RED);
                    reusableViewHolder.chk_bx_delete.setChecked(true);
                }

            }
        });
        final View checkedView = view;
        reusableViewHolder.chk_bx_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewButton) {
                ReusableViewHolder reusableViewHolder = (ReusableViewHolder) checkedView.getTag();
                CheckBox checkBox = (CheckBox) viewButton;
                if (checkBox.isChecked()) {
                    System.out.println("button is chk checked");
                    itemChecked.set(position,true);
                    tempClient=pageItems.get(position);
                    if(tempAddList.contains(tempClient))
                    {
                        tempAddListRemove.remove(tempClient);
                        tempAddListRemove.add(tempClient);
                    }
                    else
                    {
                        temDeleteList.add(tempClient);
                    }
                    tempClient = null;
                    changeColor(reusableViewHolder, Color.RED);
                  /*  reusableViewHolder.tvClientName.setTextColor(Color.RED);
                    reusableViewHolder.tvClientAccountNumber.setTextColor(Color.RED);*/
                    reusableViewHolder.chk_bx_delete.setChecked(true);
                } else {
                    System.out.println("button is chk unchecked");
                    tempClient = pageItems.get(position);
                    itemChecked.set(position, false);
                    if(tempAddListRemove.contains(tempClient))
                    {
                        tempAddListRemove.remove(tempClient);
                        tempAddList.add(tempClient);
                        changeColor(reusableViewHolder, Color.rgb(104, 181, 35));
                    } else {
                        temDeleteList.remove(tempClient);
                        changeColor(reusableViewHolder, Color.BLACK);
                    }
                    reusableViewHolder.chk_bx_delete.setChecked(false);
                    tempClient=null;
                }
            }
        });

        return view;
    }
    public void changeColor(ReusableViewHolder reusableViewHolder,int color)
    {
        reusableViewHolder.tvClientName.setTextColor(color);
        reusableViewHolder.tvClientAccountNumber.setTextColor(color);
    }


    public static class ReusableViewHolder {

        @InjectView(R.id.tv_clientName)
        TextView tvClientName;
        @InjectView(R.id.chk_bx_delete)
        CheckBox chk_bx_delete;
        @InjectView(R.id.tv_clientAccountNumber)
        TextView tvClientAccountNumber;
        @InjectView(R.id.rl_row_client)
        RelativeLayout rl_row_client;
        public void setChk_bx_delete()
        {
            chk_bx_delete.setChecked(true);
        }
        public ReusableViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

    }
}
