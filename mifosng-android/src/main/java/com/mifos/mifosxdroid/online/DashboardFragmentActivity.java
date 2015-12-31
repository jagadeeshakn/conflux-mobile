/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.mifos.mifosxdroid.OfflineCenterInputActivity;
import com.mifos.mifosxdroid.R;
import com.mifos.objects.db.UserDetails;
import com.mifos.utils.FragmentConstants;

import java.util.Iterator;

public class DashboardFragmentActivity extends ActionBarActivity {


    public final static String TAG = DashboardFragmentActivity.class.getSimpleName();
    public static Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, getResources().getString(R.string.login_successful));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        HomeFragment homeFragment=new HomeFragment();
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();

      /* List<Permissions> permission=Permissions.listAll(Permissions.class);

       */

        fragmentTransaction.replace(R.id.dashboard_global_container, homeFragment, getApplication().getResources().getString(R.string.home_fragment));
        fragmentTransaction.commit();
       //to enable home button
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_HOME_AS_UP | android.support.v7.app.ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher);
        getSupportActionBar().setSubtitle(R.string.home);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.client_search, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        Log.i(TAG,getResources().getString(R.string.dashboard_onResume));
        super.onResume();
        //on resume there can be fragments present in the backstack. Check if there are such fragments if present start those fragments
        if(getSupportFragmentManager().getBackStackEntryCount()!=0)
        {
            FragmentManager.BackStackEntry backEntry=getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount()-1);
            String fragmentName=backEntry.getName();
            System.out.println("fragment name"+fragmentName);
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentName);
            if(fragmentName.equals(FragmentConstants.FRAG_CLIENT_SEARCH))
            {
                startClientSearchFragment(fragment,fragmentName);
            }
            else {

                startFragmentPresentInBackStack(fragment, fragmentName);
            }
        }

    }

    public void startClientSearchFragment(Fragment fragment,String TAG)
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.home_dashboard_container, fragment,TAG);
        fragmentTransaction.commit();
    }
    public void startFragmentPresentInBackStack(Fragment fragment,String TAG)
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.dashboard_global_container, fragment,TAG);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: " + item.getItemId());

        switch (item.getItemId()) {
            case R.id.item_offline_centers:
                startActivity(new Intent(this, OfflineCenterInputActivity.class));
                break;
            case R.id.logout:
                startActivity(new Intent(DashboardFragmentActivity.this, LogoutActivity.class));
                finish();
                break;
            case android.R.id.home:
                //android.r.id.home is used to home button click listner
                    startActivity(new Intent(this, DashboardFragmentActivity.class));
                    finish();
                break;

            default: //DO NOTHING
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        EditText editText = (EditText) findViewById(R.id.et_search_by_id);
        if (editText != null) {
            editText.setText("");

        }
        super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            getSupportActionBar().setSubtitle(R.string.home);
        }
    }
}


