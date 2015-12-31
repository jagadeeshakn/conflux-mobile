/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.collectionsheet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.db.Client;
import com.mifos.objects.db.Loan;
import com.mifos.objects.db.MifosGroup;
import com.mifos.utils.NonScrollListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by ishankhanna on 17/07/14.
 */
public class CollectionListAdapter extends BaseExpandableListAdapter {
    Context context;
    LayoutInflater layoutInflater;
    private ExpandableListView mExpandableListView;
    private int[] groupStatus;
    public static List<MifosGroup> sMifosGroups = new ArrayList<MifosGroup>();
    public static ArrayList<Loan> loans = new ArrayList<Loan>();
    int spinnerValue;
    //Map for RepaymentTransaction<Loan Id, Transaction Amount>
    //TODO Check about SparseArray in Android and try to convert Map into SparseArray Implementation
    public static Map<Integer, Double> sRepaymentTransactions = new HashMap<Integer, Double>();
    public static Map<Integer, Integer> sRepaymentTransactions2 = new HashMap<Integer, Integer>();


    public CollectionListAdapter(Context context, ExpandableListView expandableListView, List<MifosGroup> mifosGroups) {
        this.context = context;
        layoutInflater = LayoutInflater.from(this.context);
        mExpandableListView = expandableListView;
        sMifosGroups = mifosGroups;
        groupStatus = new int[sMifosGroups.size()];

        for (MifosGroup mifosGroup : sMifosGroups) {
            for (Client client : mifosGroup.getClients()) {
                for (Loan loan : client.getLoans()) {
                    sRepaymentTransactions.put(loan.getLoanId(), loan.getTotalDue());
                }

            }
        }
        setListEvent();
    }

    private void setListEvent() {

        mExpandableListView
                .setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                    @Override
                    public void onGroupExpand(int arg0) {
                        // TODO Auto-generated method stub
                        groupStatus[arg0] = 1;
                    }
                });

        mExpandableListView
                .setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

                    @Override
                    public void onGroupCollapse(int arg0) {
                        // TODO Auto-generated method stub
                        groupStatus[arg0] = 0;
                    }
                });
    }

    @Override
    public int getGroupCount() {
        return sMifosGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return sMifosGroups.get(groupPosition).getClients().size();
    }

    @Override
    public MifosGroup getGroup(int groupPosition) {
        return sMifosGroups.get(groupPosition);
    }

    @Override
    public Client getChild(int groupPosition, int childPosition) {
        return sMifosGroups.get(groupPosition).getClients().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        MifosGroupReusableViewHolder mifosGroupReusableViewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.row_collection_list_group, null);
            mifosGroupReusableViewHolder = new MifosGroupReusableViewHolder(convertView);
            mifosGroupReusableViewHolder.img = (ImageView) convertView.findViewById(R.id.tag_img);
            convertView.setTag(mifosGroupReusableViewHolder);
        } else {
            mifosGroupReusableViewHolder = (MifosGroupReusableViewHolder) convertView.getTag();
        }
        if (groupStatus[groupPosition] == 0) {
            mifosGroupReusableViewHolder.img.setImageResource(R.drawable.group_down);
        } else {
            mifosGroupReusableViewHolder.img.setImageResource(R.drawable.group_up);
        }

        double groupTotalDue = 0;

        for (Client client : sMifosGroups.get(groupPosition).getClients()) {
            for (Loan loan : client.getLoans()) {
                groupTotalDue += sRepaymentTransactions.get(loan.getLoanId());
            }

        }

        mifosGroupReusableViewHolder.tv_groupName.setText(sMifosGroups.get(groupPosition).getGroupName());
        mifosGroupReusableViewHolder.tv_groupTotal.setText(String.valueOf(groupTotalDue));
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, final ViewGroup parent) {
        final ClientReusableViewHolder clientReusableViewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.row_collection_list_group_client, null);
            clientReusableViewHolder = new ClientReusableViewHolder(convertView);
            convertView.setTag(clientReusableViewHolder);
        } else {
            clientReusableViewHolder = (ClientReusableViewHolder) convertView.getTag();
        }
        final Client client = sMifosGroups.get(groupPosition).getClients().get(childPosition);
        double totalDue = 0;
        final List<Loan> loans = client.getLoans();

        for (Loan loan : loans) {
            totalDue += loan.getTotalDue();
        }
        //clientReusableViewHolder.tv_clientId.setText(String.valueOf(client.getClientId()));
        clientReusableViewHolder.tv_clientName.setText(client.getClientName());
        clientReusableViewHolder.tv_clientTotal.setText(String.valueOf(totalDue));
        clientReusableViewHolder.sp_attendance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String value = clientReusableViewHolder.sp_attendance.getItemAtPosition(position).toString();
                switch (position) {
                    case 0:
                        spinnerValue = 1;
                        break;
                    case 1:
                        spinnerValue = 0;
                        break;
                }
                CollectionListAdapter.sRepaymentTransactions2.put(client.getClientId(), spinnerValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final View finalConvertView = convertView;
        clientReusableViewHolder.tv_clientName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NonScrollListView non_scroll_list = (NonScrollListView) finalConvertView.findViewById(R.id.lv_loans);
                if (clientReusableViewHolder.lv_loans.getVisibility() != View.VISIBLE) {
                        CollectionSheetLoanAccountListAdapter adapter
                                = new CollectionSheetLoanAccountListAdapter(context, (ArrayList<Loan>) loans, groupPosition, childPosition);
                        non_scroll_list.setAdapter(adapter);
                        clientReusableViewHolder.lv_loans.setVisibility(View.VISIBLE);
                    clientReusableViewHolder.lv_loans.setItemsCanFocus(true);
                    }
                    else{
                        clientReusableViewHolder.lv_loans.setVisibility(View.GONE);

                    }
            }


        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public static class MifosGroupReusableViewHolder {

        @InjectView(R.id.tv_groupName)
        TextView tv_groupName;
        @InjectView(R.id.tv_groupTotal)
        TextView tv_groupTotal;
        ImageView img;

        public MifosGroupReusableViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    public static class ClientReusableViewHolder {
        /* @InjectView(R.id.tv_clientId)
          TextView tv_clientId;
        */
        @InjectView(R.id.tv_clientName)
        TextView tv_clientName;
        @InjectView(R.id.tv_clientTotal)
        TextView tv_clientTotal;
        @InjectView(R.id.lv_loans)
        ListView lv_loans;
        @InjectView(R.id.sp_attendance)
        Spinner sp_attendance;

        public ClientReusableViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

    }

    }

