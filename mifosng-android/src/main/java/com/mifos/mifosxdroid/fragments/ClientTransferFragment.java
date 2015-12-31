package com.mifos.mifosxdroid.fragments;

import android.app.Activity;
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
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.ClientNameListAdapter;
import com.mifos.mifosxdroid.adapters.GroupSuggestionAdapter;
import com.mifos.objects.ClientTransfer;
import com.mifos.objects.ClientTransferResponse;
import com.mifos.objects.Clients;
import com.mifos.objects.client.Client;
import com.mifos.objects.group.Group;
import com.mifos.services.API;
import com.mifos.utils.Constants;
import com.mifos.utils.MifosApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ClientTransferFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ClientTransferFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClientTransferFragment extends Fragment {


    private static final String GROUP_ID = "groupId";
    private static final String GROUP_NAME = "groupName";
    private static final String OFFICE_ID = "officeId";
    private View rootView;

    private int groupId;
    private int officeId;
    private String groupName;
    private List<Client> clientList;
    private List<Client> clientListToTransfer;
    private List<Boolean> itemSelected;
    private OnFragmentInteractionListener mListener;
    private ClientTransfer clientTransfer;
    private String locale="en";
    private final String TAG = getClass().getSimpleName();
    private GroupSuggestionAdapter groupSuggestionAdapter;
    private int selectedGroupId;

    @InjectView(R.id.tv_group_name)
    TextView tvGroupName;
    @InjectView(R.id.ac_tv_search_group)
    AutoCompleteTextView autoCompleteTextViewSearchGroup;
    @InjectView(R.id.chk_inherit_group_loan_officer)
    CheckBox chk_inherit_group_loan_officer;
    @InjectView(R.id.btn_transfer_clients)
    Button btnTransfer;
    @InjectView(R.id.list_transfer_clients)
    ListView list_transfer_clients;
    public static ClientTransferFragment newInstance(int groupId, String groupName, int officeId, List<Client> clientList, List<Client> clientListToTransfer,List<Boolean> itemSelected) {
        ClientTransferFragment fragment = new ClientTransferFragment();
        Bundle args = new Bundle();
        args.putInt(GROUP_ID, groupId);
        args.putString(GROUP_NAME, groupName);
        args.putInt(OFFICE_ID, officeId);
        fragment.setArguments(args);
        fragment.clientList = clientList;
        fragment.clientListToTransfer = clientListToTransfer;
        fragment.itemSelected = itemSelected;
        return fragment;
    }

    public ClientTransferFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            groupId = getArguments().getInt(GROUP_ID);
            officeId = getArguments().getInt(OFFICE_ID);
            groupName = getArguments().getString(GROUP_NAME);
        }
        clientTransfer = new ClientTransfer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG,"Client Transfer Fragment Started");
        rootView =  inflater.inflate(R.layout.fragment_client_transfer, container, false);
        ButterKnife.inject(this, rootView);
        tvGroupName.setText(groupName);
        ((ActionBarActivity)getActivity()).getSupportActionBar().setSubtitle(R.string.transfer_client);
        infflaterAutoCompleteTextView();
        inflaterTransferClientList();
        Log.d(TAG, "Clients has been listed in listview");
        btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                populateClientTransferObject();
                transferClient();
            }
        });
        return rootView;
    }



    public void infflaterAutoCompleteTextView()
    {
        autoCompleteTextViewSearchGroup.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public synchronized void onTextChanged(CharSequence charSequence, int count, int i1, int i2) {
                if (i2 == 1) {
                    ++count;
                }
                if (count >= Constants.MIN_THRESHOLD && count < Constants.MAX_THRESHOLD) {
                    Log.d(TAG, "Get the list of client names ");
                    // get the list of groups
                    ((MifosApplication) getActivity().getApplicationContext()).api.groupService.getGroupsInOffice(charSequence.toString(), officeId, "name", "ASC", new Callback<List<Group>>() {
                        @Override
                        public synchronized void success(List<Group> groups, Response response) {
                            //using the groups provide suggestions to the user
                                groupSuggestionAdapter = new GroupSuggestionAdapter(getActivity(), groups, groupId);
                                autoCompleteTextViewSearchGroup.setThreshold(3);
                                autoCompleteTextViewSearchGroup.setAdapter(groupSuggestionAdapter);
                                groupSuggestionAdapter.notifyDataSetChanged();

                        }

                        @Override
                        public void failure(RetrofitError error) {
                                Toast.makeText(getActivity(),API.userErrorMessage,Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        autoCompleteTextViewSearchGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "Group has been selected" + groupSuggestionAdapter.getItem(i).getName());
                selectedGroupId = groupSuggestionAdapter.getItem(i).getId();
                System.out.println("the group id of nalanda is " + selectedGroupId);
            }
        });
    }

    public void transferClient()
    {
        if(selectedGroupId!=-1) {
            System.out.println("the client transfer is " + clientTransfer.getClients().toArray() + " group id" + clientTransfer.getDestinationGroupId());
            ((MifosApplication) getActivity().getApplicationContext()).api.groupService.transferCLient(groupId, clientTransfer, new Callback<ClientTransferResponse>() {
                @Override
                public void success(ClientTransferResponse clientTransferResponse, Response response) {
                    Toast.makeText(getActivity(), "Clients has been transfered successfully", Toast.LENGTH_LONG).show();
                    clientList.removeAll(clientListToTransfer);
                    clientListToTransfer.clear();
                    getActivity().onBackPressed();
                }

                @Override
                public void failure(RetrofitError error) {
                    Toast.makeText(getActivity(), API.userErrorMessage, Toast.LENGTH_LONG).show();
                }
            });
        }
        else
        {
            Toast.makeText(getActivity(),"Select an Appropriate group",Toast.LENGTH_LONG).show();
        }
    }

    public void populateClientTransferObject()
    {
        Log.d(TAG, "Populating the Client Transfer object");
        if(groupId!=-1) {
            clientTransfer.setLocale(locale);
            List<Clients> clientId = new ArrayList<Clients>();
            for (Client client : clientListToTransfer) {
                Clients clients = new Clients();
                clients.setId(client.getId());
                clientId.add(clients);
            }
            clientTransfer.setClients(clientId);
            if (chk_inherit_group_loan_officer.isChecked()) {
                clientTransfer.setInheritDestinationGroupLoanOfficer(true);
            } else {
                clientTransfer.setInheritDestinationGroupLoanOfficer(false);
            }
            clientTransfer.setDestinationGroupId(selectedGroupId);
        }
        else
        {
            Toast.makeText(getActivity(),"Please Slelect a group",Toast.LENGTH_LONG).show();
        }
    }


    public void inflaterTransferClientList()
    {
        final ClientNameListAdapter clientNameListAdapter = new ClientNameListAdapter(getActivity(), clientListToTransfer,((MifosApplication)getActivity().getApplication()).api);
        list_transfer_clients.setAdapter(clientNameListAdapter);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        /*try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
