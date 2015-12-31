/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.GroupListAdapter;
import com.mifos.objects.client.Client;
import com.mifos.objects.group.CenterWithAssociations;
import com.mifos.objects.group.GroupWithAssociations;
import com.mifos.services.TestAPI;
import com.mifos.utils.Constants;
import com.mifos.utils.FragmentConstants;
import com.mifos.utils.MifosApplication;
import com.mifos.utils.SafeUIBlockingUtility;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class GroupListFragment extends Fragment {


    @InjectView(R.id.lv_group_list)
    ListView lv_groupList;

    View rootView;

    SafeUIBlockingUtility safeUIBlockingUtility;

    ActionBarActivity activity;

    SharedPreferences sharedPreferences;

    ActionBar actionBar;

    private OnFragmentInteractionListener mListener;

    private int centerId;
    private int officeId;
    private String centerName;
    private String officeName;
    final String TAG=this.getClass().getSimpleName();

    public static GroupListFragment newInstance(int centerId,String centerName,int officeId,String officeName) {
        GroupListFragment fragment = new GroupListFragment();
        Log.d(fragment.TAG,"Group list Fragment has been instantiate");
        Bundle args = new Bundle();
        args.putInt(Constants.CENTER_ID, centerId);
        args.putInt(Constants.OFFICE_ID,officeId);
        args.putString(Constants.CENTER_NAME, centerName);
        args.putString(Constants.OFFICE_NAME,officeName);
        fragment.setArguments(args);
        return fragment;
    }

    public GroupListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            centerId = getArguments().getInt(Constants.CENTER_ID);
            centerName = getArguments().getString(Constants.CENTER_NAME);
            officeId = getArguments().getInt(Constants.OFFICE_ID);
            officeName = getArguments().getString(Constants.OFFICE_NAME);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_group_list, container, false);
        activity = (ActionBarActivity) getActivity();
        safeUIBlockingUtility = new SafeUIBlockingUtility(GroupListFragment.this.getActivity());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        actionBar = activity.getSupportActionBar();
        ButterKnife.inject(this, rootView);
        ((ActionBarActivity)getActivity()).getSupportActionBar().setSubtitle(R.string.group);

        inflateGroupList();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.group_list_fragment_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: " + item.getItemId());
        switch (item.getItemId())
        {
            case R.id.mItem_create_group: //call the group create fragment
                    mListener.loadCreateGroupFragment(officeId,officeName,centerId,centerName);
                break;
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        //list out all the clients of the group
        public void loadClientsOfGroup(int centerId,int groupId,String groupName,int officeId,List<Client> clientList);
        public void loadCreateGroupFragment(int officeId,String officeName,int centerId,String centerName);
    }

    public void inflateGroupList() {
        Log.i(TAG,"List out all the groups of the center");
        safeUIBlockingUtility.safelyBlockUI();

        ((MifosApplication) getActivity().getApplicationContext()).api.centerService.getAllGroupsForCenter(centerId, new Callback<CenterWithAssociations>() {
            @Override
            public void success(final CenterWithAssociations centerWithAssociations, Response response) {

                if (centerWithAssociations != null) {

                    GroupListAdapter groupListAdapter = new GroupListAdapter(getActivity(), centerWithAssociations.getGroupMembers());
                    lv_groupList.setAdapter(groupListAdapter);
                    lv_groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            final int groupId = centerWithAssociations.getGroupMembers().get(i).getId();
                            final String groupName= centerWithAssociations.getGroupMembers().get(i).getName();

                                    ((MifosApplication) getActivity().getApplicationContext()).api.groupService.getGroupWithAssociations(groupId,
                                    new Callback<GroupWithAssociations>() {
                                        @Override
                                        public void success(GroupWithAssociations groupWithAssociations, Response response) {

                                            if(groupWithAssociations != null) {
                                                mListener.loadClientsOfGroup(centerId,groupId,groupName,groupWithAssociations.getOfficeId(),groupWithAssociations.getClientMembers());
                                            }
                                        }

                                        @Override
                                        public void failure(RetrofitError retrofitError) {

                                        }
                                    });

                        }
                    });
                    safeUIBlockingUtility.safelyUnBlockUI();
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {

                safeUIBlockingUtility.safelyUnBlockUI();

            }
        });


    }


}
