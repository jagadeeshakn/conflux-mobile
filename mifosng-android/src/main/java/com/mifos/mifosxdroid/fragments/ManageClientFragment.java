package com.mifos.mifosxdroid.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.ManageClientsAdapter;
import com.mifos.mifosxdroid.adapters.ManageClientListAdapter;
import com.mifos.objects.ClientDetail;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.ClientDisassociate;
import com.mifos.objects.client.ClientIdObject;
import com.mifos.services.API;
import com.mifos.utils.Constants;
import com.mifos.utils.MifosApplication;
import com.mifos.utils.SafeUIBlockingUtility;

import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ManageClientFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ManageClientFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManageClientFragment extends Fragment{

    @InjectView(R.id.tv_group_name)
    TextView tvGroupName;
    @InjectView(R.id.lv_client)
    ListView lv_Client;
    @InjectView(R.id.btn_update_changes)
    Button btn_remove_from_group;
    @InjectView(R.id.ac_tv_search_client)
    AutoCompleteTextView autoCompleteTextViewSearchClient;
    private HashMap<String, Integer> officeNameIdHashMap = new HashMap<String, Integer>();
    private List<Client> tempAddListRemove =new ArrayList<Client>();
    private ManageClientListAdapter manageClientListAdapter;
    private OnFragmentInteractionListener mListener;
    private List<Integer> clientIds = new ArrayList<Integer>();
    Context context;
    View rootView;
    private List<Client> clientList;
    private List<Client> tempDeleteList = new ArrayList<Client>();
    private Client tempClient;
    private List<Client> tempAddList = new ArrayList<Client>();
    SafeUIBlockingUtility safeUIBlockingUtility;
    public static ClientDetail clientDetail;
    public  List<Client> clientPageItem = new ArrayList<Client>();
    final List<String> clientName = new ArrayList<String>();
    private int selectedClientId=-1;
    ManageClientsAdapter manageClientsAdapter ;
    private Client addNewTempClient;

    private int officeId;
    private int groupId;
    private String groupName;
    final String TAG = getClass().getSimpleName();
    public static ManageClientFragment newInstance(int groupId,String groupName,int officeId,List<Client> clientList) {
        ManageClientFragment fragment = new ManageClientFragment();
        fragment.clientList = clientList;
        Bundle args = new Bundle();
        args.putString(Constants.GROUP_NAME, groupName);
        args.putInt(Constants.GROUP_ID, groupId);
        args.putInt(Constants.OFFICE_ID,officeId);
        fragment.setArguments(args);
        return fragment;
    }

    public ManageClientFragment() {
        // Required empty public constructor
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_manage_client, container, false);
        ButterKnife.inject(this,rootView);
        context = getActivity().getApplicationContext();
        tvGroupName.setText(groupName);
        if(clientList.size()!=0) {
            inflateClientList();
        }
        else
        {
            Toast.makeText(getActivity(),"There are no clients in the selected group",Toast.LENGTH_LONG).show();
        }
        ((ActionBarActivity)getActivity()).getSupportActionBar().setSubtitle(R.string.manage_clients);
        inflateAutoComplteTextViewClientSearch();
        return rootView;
    }

    public void inflateAutoComplteTextViewClientSearch()
    {
        autoCompleteTextViewSearchClient.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int count, int i1, int i2) {
                if (i2 == 1) {
                    ++count;
                }
                if (count >= Constants.MIN_THRESHOLD && count < Constants.MAX_THRESHOLD) {
                    Log.d(TAG, "Get the list of client names ");
                    ((MifosApplication) getActivity().getApplicationContext()).api.searchService.searchClient(charSequence.toString().trim(), officeId, "displayName", true, "ASC", new Callback<ClientDetail>() {

                        @Override
                        public void success(ClientDetail clientDetail, Response response) {
                            clientPageItem = clientDetail.getPageItems();
                            manageClientsAdapter = new ManageClientsAdapter(getActivity(), clientPageItem);
                            autoCompleteTextViewSearchClient.setThreshold(2);
                            autoCompleteTextViewSearchClient.setAdapter(manageClientsAdapter);
                            manageClientsAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        autoCompleteTextViewSearchClient.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tempClient = manageClientsAdapter.getItem(i);
                            }
        });


    }

    @OnClick(R.id.btn_add_new_client)
    public void addClient(View view)
    {
        Log.d(TAG,"Temporarily add Client to the List");
        if(tempClient != null) {
            if (!tempAddList.contains(tempClient)) {
                autoCompleteTextViewSearchClient.setText("");
                manageClientListAdapter.insert(tempClient, 0);
                tempClient=null;
            } else {
                Toast.makeText(context, "The client " + tempClient.getDisplayName() + " has been already added to this group", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void inflateClientList()
    {
        Log.d(TAG,"Inflate Client List");
        manageClientListAdapter = new ManageClientListAdapter(context,getActivity(),clientList,tempDeleteList,tempAddList,tempAddListRemove);
        manageClientListAdapter.setManageClientListAdapter(manageClientListAdapter);
        lv_Client.setAdapter(manageClientListAdapter);
      }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {

        }
    }

    @OnClick(R.id.btn_update_changes)
    public void onButtonUpdateChenges(View view)
    {
        Log.d(TAG,"Update changes Button pressed");
        if(tempDeleteList.size()!=0||tempAddList.size()!=0) {
            alertDialog();
        }
        else
            if(tempAddListRemove.size()!=0)
            {
                for(Client client: tempAddListRemove)
                {
                    int position = clientList.indexOf(client);
                    clientList.remove(client);
                    tempAddListRemove.remove(client);
                    manageClientListAdapter.getItemChecked().remove(position);
                }
            }
    }
    public void alertDialog()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Alert")
                .setMessage("Do you want to update the changes to this group?")
                .setIcon(android.R.drawable.stat_sys_warning)
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("User Login Info", "User aggred for continuing to update the changes ");
                        updateTheChanges();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                })
                .create();
        alertDialog.show();
    }

    public void updateTheChanges()
    {
        Log.i(TAG,"Update the changes");
        safeUIBlockingUtility = new SafeUIBlockingUtility(getActivity());
        clientIds.clear();
        if(tempDeleteList.size()!=0) {

            for (Client tempClient : tempDeleteList) {
                clientIds.add(tempClient.getId());
            }
            ClientIdObject clientMembers = new ClientIdObject();
            clientMembers.setClientMembers(clientIds);
            safeUIBlockingUtility.safelyBlockUI();
            ((MifosApplication) getActivity().getApplication()).api.groupService.disassociateClients(groupId, clientMembers, new Callback<ClientDisassociate>() {
                @Override
                public void success(ClientDisassociate clientDisassociate, Response response) {
                    safeUIBlockingUtility.safelyUnBlockUI();
                    if (response.getStatus() == HttpStatus.SC_OK) {
                        List<Integer> clientId = new ArrayList<Integer>();
                        for (String id : clientDisassociate.getChanges().getClientMembers()) {
                            clientId.add(Integer.valueOf(id));
                        }
                        List<Client> tempClientlist = new ArrayList<Client>();
                        for (Client client : clientList) {
                            if (clientId.contains(client.getId())) {
                                tempClientlist.add(client);
                            }
                        }
                        for (Client client : tempClientlist) {
                            int position = clientList.indexOf(client);
                            clientList.remove(client);
                            tempDeleteList.remove(client);
                            manageClientListAdapter.getItemChecked().remove(position);
                        }
                        manageClientListAdapter.notifyDataSetChanged();
                        if(tempAddListRemove.size()!=0)
                        {
                            for(Client client: tempAddListRemove)
                            {
                                int position = clientList.indexOf(client);
                                clientList.remove(client);
                                tempAddListRemove.remove(client);
                                manageClientListAdapter.getItemChecked().remove(position);
                            }
                            tempAddListRemove.clear();
                        }
                        manageClientListAdapter.notifyDataSetChanged();
                        tempDeleteList.clear();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    safeUIBlockingUtility.safelyUnBlockUI();
                    Log.e(TAG, "Unsuccessfull " + API.userErrorMessage);
                    Toast.makeText(getActivity(), API.userErrorMessage, Toast.LENGTH_LONG).show();
                }
            });
        }
        if(tempAddList.size()!=0)
        {
            List<Integer> clientAddIds = new ArrayList<Integer>();
            for (Client tempClient : tempAddList) {
                clientAddIds.add(tempClient.getId());
            }
            ClientIdObject clientMembers = new ClientIdObject();
            clientMembers.setClientMembers(clientAddIds);
            safeUIBlockingUtility.safelyBlockUI();
            ((MifosApplication) getActivity().getApplication()).api.groupService.associateClients(groupId, clientMembers, new Callback<ClientDisassociate>() {
                @Override
                public void success(ClientDisassociate clientDisassociate, Response response) {
                    safeUIBlockingUtility.safelyUnBlockUI();
                    if (response.getStatus() == HttpStatus.SC_OK)
                    {
                        List<Integer> clientId = new ArrayList<Integer>();
                        for (String id : clientDisassociate.getChanges().getClientMembers()) {
                            clientId.add(Integer.valueOf(id));
                        }
                        tempAddList.clear();
                        if(tempAddListRemove.size()!=0)
                        {
                            for(Client client: tempAddListRemove)
                            {
                                clientList.remove(client);
                            }
                            tempAddListRemove.clear();
                        }
                        manageClientListAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    safeUIBlockingUtility.safelyUnBlockUI();
                }
            });
        }
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

    }

}
