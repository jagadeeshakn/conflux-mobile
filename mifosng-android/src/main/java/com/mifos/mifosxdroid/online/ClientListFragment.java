/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.ClientNameListAdapter;
import com.mifos.objects.CgtData;
import com.mifos.objects.GrtData;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;
import com.mifos.objects.client.Permission;
import com.mifos.objects.db.Permissions;
import com.mifos.objects.group.Group;
import com.mifos.services.API;
import com.mifos.services.TestAPI;
import com.mifos.utils.Constants;
import com.mifos.utils.MifosApplication;

import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import butterknife.OnClick;


/**
 * Created by ishankhanna on 09/02/14.
 */
public class ClientListFragment extends Fragment {


    @InjectView(R.id.lv_clients)
    ListView lv_clients;
    View rootView;
    @InjectView(R.id.buttons)
    LinearLayout buttons;
    @InjectView(R.id.user_with_cgt_grt_permission)
    LinearLayout userWith_cgt_grt_Permission;
    @InjectView(R.id.user_with_grt_permission)
    LinearLayout userWith_grt_Permission;
    @InjectView(R.id.user_with_cgt_permission)
    LinearLayout userWith_cgt_Permission;

    List<Client> clientList = new ArrayList<Client>();
    private Context context;
    private int offset = 0;
    private int limit = 50;
    private int index = 0;
    private int top = 0;
    //listOffset is used for stting the limit of the api to limit the call to the server till the listoffset is reached to limit-10
    private int listOffset=0;
    private int listLimitOffset=limit;
    private  int centerId;
    private  int groupId;
    int officeId;
    private String groupName;
    private OnFragmentInteractionListener mListener;
    private final String TAG = getClass().getSimpleName();


    private boolean isInfiniteScrollEnabled = true;

    public ClientListFragment() {

    }

    public static ClientListFragment newInstance(int centerId,int groupId,String groupName,int officeId,List<Client> clientList) {
        ClientListFragment clientListFragment = new ClientListFragment();
        clientListFragment.setClientList(clientList);
        clientListFragment.centerId=centerId;
        clientListFragment.groupId=groupId;
        clientListFragment.groupName=groupName;
        clientListFragment.officeId=officeId;
        clientListFragment.setInfiniteScrollEnabled(false);
        return clientListFragment;
    }

    public static ClientListFragment newInstance(int centerid,int groupId,String groupName,int officeId,List<Client> clientList, boolean isParentFragmentAGroupFragment) {
        ClientListFragment clientListFragment = new ClientListFragment();
        clientListFragment.setClientList(clientList);
        clientListFragment.centerId=centerid;
        clientListFragment.groupId=groupId;
        clientListFragment.groupName=groupName;
        clientListFragment.officeId=officeId;
        if (isParentFragmentAGroupFragment) {
            clientListFragment.setInfiniteScrollEnabled(false);
        }
        return clientListFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_client, container, false);
        setHasOptionsMenu(true);
        context = getActivity().getApplicationContext();
        ButterKnife.inject(this, rootView);
        ((ActionBarActivity)getActivity()).getSupportActionBar().setSubtitle(R.string.client);
        //read the permissions table to retrieve the user permissions.
        List<Permissions> associatedPermission=Permissions.listAll(Permissions.class);
        List<String> listUserPermissions=new ArrayList<String>();
        for(Permissions permission:associatedPermission)
        {
            listUserPermissions.add(permission.getPermissions());
        }
        if(listUserPermissions.contains(Constants.ALL_FUNCTIONS)||(listUserPermissions.contains(Constants.USER_CGT_WRITE_PERMISSSION)&&listUserPermissions.contains(Constants.USER_GRT_WRITE_PERMISSION)))
        {
            buttons.setVisibility(View.VISIBLE);
            userWith_cgt_grt_Permission.setVisibility(View.VISIBLE);
        }
        else if(listUserPermissions.contains(Constants.USER_GRT_WRITE_PERMISSION))
        {
            buttons.setVisibility(View.VISIBLE);
            userWith_grt_Permission.setVisibility(View.VISIBLE);
        }
        else if(listUserPermissions.contains(Constants.USER_CGT_WRITE_PERMISSSION))
        {
            buttons.setVisibility(View.VISIBLE);
            userWith_cgt_Permission.setVisibility(View.VISIBLE);
        }
        if(!clientList.isEmpty()) {
            fetchClientList();
        }
        else
        {
            Toast.makeText(getActivity(),"There are no members in this group",Toast.LENGTH_LONG).show();
        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.client_list_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.mItem_add_new_client : Log.d(TAG,"Create new client option has been selected");
                break;
            case R.id.mItem_manage_client: Log.d(TAG,"Manage clients option has been selected");
                mListener.loadManageClientsFragment(centerId, groupId,groupName,officeId,clientList);
                break;
            case R.id.mItem_transfer_client:Log.d(TAG,"Menu item Transfer Client has been Selected");
                mListener.loadTransferClientFragment(centerId,groupId,groupName,officeId,clientList);
        }
        return super.onOptionsItemSelected(item);
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

    @OnClick(R.id.cgt)
    public void requestforCGT(View view)
    {
        TestAPI testAPI=new TestAPI();
        testAPI.cgt.getCgt(new Callback<List<CgtData>>() {
            @Override
            public void success(List<CgtData> cgtDatas, Response response) {

                mListener.loadCGTFragmentForTheGroup(centerId, groupId, groupName, cgtDatas, clientList);

            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), "Failure :" + API.userErrorMessage, Toast.LENGTH_LONG).show();

            }
        });
    }


    @OnClick(R.id.grt)
    public void loadGrtFragment(View view)
    {
        TestAPI testAPI = new TestAPI();
        testAPI.grt.getGrt(new Callback<GrtData>() {
            @Override
            public void success(GrtData grtData, Response response) {
                mListener.loadGRTFragmentFortheGroup(centerId, groupId, groupName, officeId, grtData, clientList);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), "Failure :" + API.userErrorMessage, Toast.LENGTH_LONG).show();
            }
        });

    }
                   /*     } else {
                            Toast.makeText(getActivity(), "There was some error fetching incomeList.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NullPointerException npe) {
                        Toast.makeText(getActivity(), "There is some problem with your internet connection.", Toast.LENGTH_SHORT).show();*/
    public void inflateClientList() {

        final ClientNameListAdapter clientNameListAdapter = new ClientNameListAdapter(context, clientList,((MifosApplication)getActivity().getApplication()).api);
        lv_clients.setAdapter(clientNameListAdapter);

        lv_clients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent clientActivityIntent = new Intent(getActivity(), ClientActivity.class);
                clientActivityIntent.putExtra(Constants.CLIENT_ID, clientList.get(i).getId());
                startActivity(clientActivityIntent);

            }
        });

    }


    public void fetchClientList() {

        if (clientList.size() > 0) {
            inflateClientList();
        } else {


            //swipeRefreshLayout.setRefreshing(true);
            //Get a Client List
            ((MifosApplication)getActivity().getApplication()).api.clientService.listAllClients(offset, limit,new Callback<Page<Client>>() {
                @Override
                public void success(Page<Client> page, Response response) {
                    clientList = page.getPageItems();
                    inflateClientList();
                    offset=+limit+1;
                    //swipeRefreshLayout.setRefreshing(false);

                }

                @Override
                public void failure(RetrofitError retrofitError) {

                   // swipeRefreshLayout.setRefreshing(false);

                    if (getActivity() != null) {
                        try {
                            Log.i("Error", "" + retrofitError.getResponse().getStatus());
                            if (retrofitError.getResponse().getStatus() == HttpStatus.SC_UNAUTHORIZED) {
                                Toast.makeText(getActivity(), "Authorization Expired - Please Login Again", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getActivity(), LogoutActivity.class));
                                getActivity().finish();

                            } else {
                                Toast.makeText(getActivity(), "There was some error fetching incomeList.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (NullPointerException npe) {
                            Toast.makeText(getActivity(), "There is some problem with your internet connection.", Toast.LENGTH_SHORT).show();

                        }


                    }
                }
            });

        }


    }

    public interface OnFragmentInteractionListener {
        public void loadCGTFragmentForTheGroup(int centerId,int groupId,String groupName,List<CgtData> cgtDatas,List<Client> clientList);
        public void loadGRTFragmentFortheGroup(int centerId,int groupId,String groupName,int officeId,GrtData grtData,List<Client> clientList);
        public void loadManageClientsFragment(int centerId,int groupId,String groupName,int officeId,List<Client> clientList);
        public void loadTransferClientFragment(int centerId,int groupId,String groupName,int officeId,List<Client> clientList);
    }
    public List<Client> getClientList() {
        return clientList;
    }

    public void setClientList(List<Client> clientList) {
        this.clientList = clientList;
    }

    public void setInfiniteScrollEnabled(boolean isInfiniteScrollEnabled) {
        this.isInfiniteScrollEnabled = isInfiniteScrollEnabled;
    }
}
