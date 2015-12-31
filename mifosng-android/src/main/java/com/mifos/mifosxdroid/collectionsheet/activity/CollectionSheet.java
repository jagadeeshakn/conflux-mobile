package com.mifos.mifosxdroid.collectionsheet.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.collectionsheet.fragment.CollectionSheetFragment;
import com.mifos.mifosxdroid.fragments.*;
import com.mifos.utils.Constants;


public class CollectionSheet extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_sheet);

        SharedPreferences preferences = this.getSharedPreferences(MifoCenterListFragment.PREF_COLLECTION_DETAILS, Context.MODE_PRIVATE);
        Integer centerId = preferences.getInt(Constants.CENTER_ID, 0);
        String dateOfCollection = preferences.getString(Constants.DATE_OF_COLLECTION, "");
        //String dateOfCollection = String.valueOf(DateHelper.getCurrentDateAsString());
        String centerTitle = preferences.getString(Constants.CENTER_TITLE, "");
        Integer calendarId = preferences.getInt(Constants.CALENDAR_INSTANCE_ID,0);
        preferences.edit().clear();
        this.setTitle(centerTitle);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, CollectionSheetFragment.newInstance(centerId, dateOfCollection, calendarId))
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.generate_collection_sheet, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }




}
