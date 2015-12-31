package com.mifos.mifosxdroid.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.fragments.ClientListCgtFragment;
import com.mifos.objects.ClientMember;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.PageItem;
import com.mifos.services.API;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by conflux37 on 11/18/2015.
 */
public class ClientAttendanceListAdapter extends BaseAdapter {
    LayoutInflater layoutInflater;
    List<Client> pageItems;
    API api;
    ReusableViewHolder reusableViewHolder;
    List<ClientMember> clientMembers;

    public ClientAttendanceListAdapter(Context context, List<Client> pageItems, API api,List<ClientMember> clientMembers) {

        layoutInflater = LayoutInflater.from(context);
        this.pageItems = pageItems;
        this.api = api;
        this.clientMembers=clientMembers;
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
        public View getView ( final int position, View view, ViewGroup viewGroup){


            if (view == null) {
                view = layoutInflater.inflate(R.layout.row_client_attendance_list, null);
                reusableViewHolder = new ReusableViewHolder(view);
                view.setTag(reusableViewHolder);

            } else {
                reusableViewHolder = (ReusableViewHolder) view.getTag();
            }


            reusableViewHolder.attendance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view != null) {

                        CheckBox checkBox = (CheckBox) view.findViewById(R.id.attendance);
                        if (checkBox.isChecked()) {
                            for(ClientMember clientAttendance:clientMembers)
                            {
                                if(clientAttendance.getId()== pageItems.get(position).getId())
                                {
                                    clientAttendance.setAttendance("P");
                                }
                            }
                            Toast.makeText(view.getContext(),  checkBox.getText() + " is present ", Toast.LENGTH_LONG).show();
                        } else {
                            for(ClientMember clientAttendance:clientMembers)
                            {
                                if(clientAttendance.getId()== pageItems.get(position).getId())
                                {
                                    clientAttendance.setAttendance("A");
                                }
                            }
                            Toast.makeText(view.getContext(),  checkBox.getText() + " is absent ", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
            reusableViewHolder.attendance.setChecked(true);
            reusableViewHolder.attendance.setText(pageItems.get(position).getFirstname() + " "
                    + pageItems.get(position).getLastname());

            return view;
        }

        static class ReusableViewHolder {

            @InjectView(R.id.attendance)
            CheckBox attendance;
            public ReusableViewHolder(View view) {
                ButterKnife.inject(this, view);
            }

        }


    }

