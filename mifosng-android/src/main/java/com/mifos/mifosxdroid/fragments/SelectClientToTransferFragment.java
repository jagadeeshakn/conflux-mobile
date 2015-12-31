package com.mifos.mifosxdroid.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.SelectClientToTransferListAdapter;
import com.mifos.objects.client.Client;
import com.mifos.utils.Constants;
import com.mifos.utils.MifosApplication;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by conflux37 on 12/16/2015.
 */
public class SelectClientToTransferFragment extends Fragment {
    private List<Client> clientList;
    private int officeId;
    private int groupId;
    private String groupName;
    private View rootView;
    private Context context;
    @InjectView(R.id.list_clients)
    ListView list_clients;
    @InjectView(R.id.tv_group_name)
    TextView tvGroupName;
    @InjectView(R.id.btn_selected_clients)
    Button btn_selected_clients;
    private List<Client> clienListtToTransfer = new ArrayList<Client>();
    private int count =0;
    private OnFragmentInteractionListener mListener;
    private SelectClientToTransferListAdapter clientNameListAdapter;
    private final static String ITEM_SELECTED= "itemSelected";

    public SelectClientToTransferFragment()
    {

    }
    public static SelectClientToTransferFragment newInstance(int centerId, int groupId, String groupName, int officeId, List<Client > clientList)
    {
        SelectClientToTransferFragment transferClientFragment = new SelectClientToTransferFragment();
        transferClientFragment.clientList = clientList;
        Bundle args = new Bundle();
        args.putString(Constants.GROUP_NAME, groupName);
        args.putInt(Constants.GROUP_ID, groupId);
        args.putInt(Constants.OFFICE_ID,officeId);
        transferClientFragment.setArguments(args);
        return transferClientFragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            officeId = getArguments().getInt(Constants.OFFICE_ID);
            groupId = getArguments().getInt(Constants.GROUP_ID);
            groupName = getArguments().getString(Constants.GROUP_NAME);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_select_client_to_transfer, container, false);
        ButterKnife.inject(this,rootView);
        context = getActivity().getApplicationContext();
        ((ActionBarActivity)getActivity()).getSupportActionBar().setSubtitle(R.string.select_client_to_transfer);
        tvGroupName.setText(groupName);
        inflateTransferButton();
        inflateClientList();

        return rootView;
    }

    public void inflateTransferButton()
    {
        if(count>0)
        {
            btn_selected_clients.setText("Transfer Selected ("+count+") Clients");
            btn_selected_clients.setVisibility(View.VISIBLE);
        }
        else
        {
            btn_selected_clients.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(!clienListtToTransfer.isEmpty())
        {
            for(Client client: clienListtToTransfer)
            {
                int position = clientList.indexOf(client);
                clientNameListAdapter.getItemSelected().set(position,true);
            }
        }
        else
        {
            count = 0;
        }
    }

    public void inflateClientList()
    {
        clientNameListAdapter = new SelectClientToTransferListAdapter(context, clientList,((MifosApplication)getActivity().getApplication()).api);
        list_clients.setAdapter(clientNameListAdapter);

        list_clients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (clientNameListAdapter.getItemSelected().get(i)) {
                    clientNameListAdapter.getItemSelected().set(i,false);
                    clienListtToTransfer.remove(clientNameListAdapter.getItem(i));
                    count--;
                    clientNameListAdapter.notifyDataSetChanged();
                } else {
                    count++;
                    clienListtToTransfer.add(clientNameListAdapter.getItem(i));
                    clientNameListAdapter.getItemSelected().set(i,true);
                    clientNameListAdapter.notifyDataSetChanged();
                }
                inflateTransferButton();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        ArrayList<Boolean> itemSelected = new ArrayList<Boolean>(clientNameListAdapter.getItemSelected().size());
        itemSelected.addAll(clientNameListAdapter.getItemSelected());
        Intent intent = new Intent();
        intent.putExtra(ITEM_SELECTED, itemSelected);
        outState.putAll(intent.getExtras());
        super.onSaveInstanceState(outState);
    }

    @OnClick(R.id.btn_selected_clients)
    public void transferSelectedClients(View view)
    {
        mListener.loadTransferClientFragment(groupId,groupName,officeId,clientList,clienListtToTransfer,clientNameListAdapter.getItemSelected());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    public interface OnFragmentInteractionListener{
        public void loadTransferClientFragment(int groupId,String groupName,int officeId,List<Client> clientList,List<Client> clientListToTransfer,List<Boolean> itemsSelected);
    }
}
