/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.collectionsheet.fragment.CollectionSheetFragment;
import com.mifos.mifosxdroid.fragments.ClientListCgtFragment;
import com.mifos.mifosxdroid.fragments.ClientTransferFragment;
import com.mifos.mifosxdroid.fragments.CreateGroupFragment;
import com.mifos.mifosxdroid.fragments.GrtFragment;
import com.mifos.mifosxdroid.fragments.ManageClientFragment;
import com.mifos.mifosxdroid.fragments.SelectClientToTransferFragment;
import com.mifos.objects.CgtData;
import com.mifos.objects.GrtData;
import com.mifos.objects.client.Client;
import com.mifos.utils.FragmentConstants;

import java.util.List;

import butterknife.ButterKnife;

public class CentersActivity extends ActionBarActivity
        implements CenterListFragment.OnFragmentInteractionListener,
                   GroupListFragment.OnFragmentInteractionListener,ClientListFragment.OnFragmentInteractionListener, ManageClientFragment.OnFragmentInteractionListener,SelectClientToTransferFragment.OnFragmentInteractionListener{
    private final String TAG=this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_centers);
        ButterKnife.inject(this);
        Log.i(TAG,"Centers Activity has been created");
       //getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher);
        getSupportActionBar().setTitle(R.string.dashboard);
        getSupportActionBar().setSubtitle(R.string.title_activity_centers);

        Log.i(TAG, "Centers Activity begin centerListFragment to list the Centers");
        FragmentTransaction fragmentTransaction =  getSupportFragmentManager().beginTransaction();
        CenterListFragment centerListFragment = new CenterListFragment();
        fragmentTransaction.replace(R.id.center_container, centerListFragment);
        fragmentTransaction.commit();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.centers, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Log.d(TAG, "onOptionsItemSelected: " + item.getItemId());
        switch (item.getItemId()) {

            case R.id.logout:
                startActivity(new Intent(this, LogoutActivity.class));
                finish();
                break;

            default: //DO NOTHING
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void loadCreateGroupFragment(int officeId, String officeName,int centerId,String centerName) {
        CreateGroupFragment createGroupFragment = CreateGroupFragment.newInstance(officeId, officeName,centerId,centerName);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack("create group");
        fragmentTransaction.replace(R.id.center_container,createGroupFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void loadGroupsOfCenter(int centerId,String centerName,int officeId,String officeName) {
        Log.i(TAG,"List out all the groups of the selected Center");
        GroupListFragment groupListFragment = GroupListFragment.newInstance(centerId,centerName,officeId,officeName);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CENTER_LIST);
        fragmentTransaction.replace(R.id.center_container, groupListFragment);
        fragmentTransaction.commit();

    }

    @Override
    public void loadManageClientsFragment(int centerId, int groupId,String groupName, int officeId, List<Client> clientList) {
        Log.d(TAG,"Requested for Manage Clients.");
        ManageClientFragment manageClientFragment = ManageClientFragment.newInstance(groupId,groupName,officeId,clientList);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_MANAGE_CLIENTS);
        fragmentTransaction.replace(R.id.center_container, manageClientFragment);
        fragmentTransaction.commit();

    }

    @Override
    public void loadTransferClientFragment(int centerId, int groupId, String groupName, int officeId, List<Client> clientList) {
        Log.d(TAG,"Request for TransferClient Fragment ");
        SelectClientToTransferFragment transferClientFragment = SelectClientToTransferFragment.newInstance(centerId, groupId, groupName, officeId, clientList);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_TRANSFER_CLIENT);
        fragmentTransaction.replace(R.id.center_container,transferClientFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void loadCollectionSheetForCenter(int centerId, String collectionDate, int calenderInstanceId) {
        Log.i(TAG,"Load Collection Sheet");
        CollectionSheetFragment collectionSheetFragment = CollectionSheetFragment.newInstance(centerId, collectionDate, calenderInstanceId);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CENTER_LIST);
        fragmentTransaction.replace(R.id.center_container, collectionSheetFragment);
        fragmentTransaction.commit();

    }

    @Override
    public void loadClientsOfGroup(int centerId,int groupId,String groupName,int officeId,List<Client> clientList) {
        ClientListFragment clientListFragment = ClientListFragment.newInstance(centerId,groupId,groupName,officeId,clientList);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_GROUP_LIST);
        fragmentTransaction.replace(R.id.center_container, clientListFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void loadTransferClientFragment(int groupId, String groupName, int officeId, List<Client> clientList, List<Client> clientListToTransfer,List<Boolean> itemSelected) {
        ClientTransferFragment clientTransferFragment = ClientTransferFragment.newInstance(groupId,groupName,officeId,clientList,clientListToTransfer,itemSelected);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_TRANSFER);
        fragmentTransaction.replace(R.id.center_container, clientTransferFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void loadCGTFragmentForTheGroup(int centerId,int groupId,String groupName,List<CgtData> cgtDatas,List<Client> clientList)
    {
        Log.i(TAG,"List out all the clients of the group for CGT");
        ClientListCgtFragment clientListCgtFragment= ClientListCgtFragment.newInstance(clientList,centerId,groupId,groupName,cgtDatas);
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack("client list");
        fragmentTransaction.replace(R.id.center_container,clientListCgtFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void loadGRTFragmentFortheGroup(int centerId,int groupId,String groupName,int officeId,GrtData grtData,List<Client> clientList) {
        Log.i(TAG, "GRT Fragment has been Initiated");
        GrtFragment grtFragment= GrtFragment.newInstance(clientList,centerId,groupId,officeId,grtData,groupName);
        startFragment("grt fragment",grtFragment);
    }

    public void startFragment(String fragmentName,Fragment fragment)
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(fragmentName);
        fragmentTransaction.replace(R.id.center_container,fragment);
        fragmentTransaction.commit();
    }
}
