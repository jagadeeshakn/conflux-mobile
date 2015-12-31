/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.mifos.mifosxdroid.LoginActivity;
import com.mifos.mifosxdroid.OfflineCenterInputActivity;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.MifoCentersListAdapter;
import com.mifos.mifosxdroid.collectionsheet.activity.CollectionSheet;
import com.mifos.objects.db.MeetingCenter;
import com.mifos.objects.db.OfflineCenter;
import com.mifos.objects.group.Center;
import com.mifos.mifosxdroid.collectionsheet.data.Payload;
import com.mifos.utils.Constants;
import com.mifos.utils.DateHelper;
import com.mifos.utils.MifosApplication;
import com.mifos.utils.Network;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MifoCenterListFragment extends Fragment implements AdapterView.OnItemClickListener {

    public static final String TAG = "Center List Fragment";
    public static final String CENTER_ID = "offline_center_id";
    private final List<Center> centerList2 = new ArrayList<Center>();
    @InjectView(R.id.lv_center)
    ListView lv_center;
    @InjectView(R.id.progress_center)
    ProgressBar progressCenter;
    MifoCentersListAdapter adapter = null;
    View view;
    @InjectView(R.id.tv_empty_center)
    TextView tv_empty_center;
    private String date;
    public long calendarId;

    public static String PREF_COLLECTION_DETAILS = "pref_collection_details";

    SharedPreferences sharedPreferences;

    MeetingCenter[] centerList;

    CollectionSheet collectionSheets ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_center_list, null);
        ButterKnife.inject(this, view);
        setHasOptionsMenu(true);
        if (getAllCenters().size() == 0)
            getData();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.new_list_center_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final int itemId = item.getItemId();
        if (itemId == R.id.action_clear_offline_data) {
            startCenterInputActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        //setAdapter(centerList);
    }

    private void setAdapter(MeetingCenter[] centers) {
        if (adapter == null) {
            adapter = new MifoCentersListAdapter(getActivity(), centers);
            lv_center.setAdapter(adapter);
        }
        lv_center.setOnItemClickListener(this);
        lv_center.setEmptyView(progressCenter);
        adapter.notifyDataSetChanged();

    }

    private List<MeetingCenter> getAllCenters() {
        return Select.from(MeetingCenter.class).list();
    }

    private void getData() {

        if (Network.isOnline(getActivity().getApplicationContext())) {
            String dateFormat = Constants.DATE_FORMAT;
            String locale = "en";

            SharedPreferences preferences = getActivity().getSharedPreferences(OfflineCenterInputActivity.PREF_CENTER_DETAILS, Context.MODE_PRIVATE);
            int staffId = preferences.getInt(OfflineCenterInputActivity.STAFF_ID_KEY, -1);
            String meetingDate = DateHelper.getDateAsStringUsedForCollectionSheet(preferences.getString(OfflineCenterInputActivity.TRANSACTION_DATE_KEY, null));
            int officeId = preferences.getInt(OfflineCenterInputActivity.BRANCH_ID_KEY, -1);

            ((MifosApplication)getActivity().getApplicationContext()).api.centerService.getCenterList(dateFormat, locale, meetingDate, officeId, staffId, new Callback<List<OfflineCenter>>() {
                @Override
                public void success(List<OfflineCenter> centers, Response response) {
                    Log.i(TAG, "-----------Success-----Got the list of centers--------" + centers);
                    if(centers.size() > 0){
                        centerList = centers.get(0).getMeetingFallCenters();
                        setAdapter(centerList);
                    }else{
                        Toast.makeText(getActivity(), getString(R.string.no_center), Toast.LENGTH_LONG).show();
                        getActivity().finish();
                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    try {
                        Toast.makeText(getActivity(), getString(R.string.error_login_again), Toast.LENGTH_LONG).show();
                    } catch (Exception ex) {
                        Crashlytics.logException(ex);
                    } finally {
                        getActivity().finish();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    }

                }
            });
        }
    }



    private void startCenterInputActivity() {
        SharedPreferences preferences = getActivity().getSharedPreferences(OfflineCenterInputActivity.PREF_CENTER_DETAILS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
        getActivity().finish();
        startActivity(new Intent(getActivity(), CollectionSheet.class));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        /*Intent intent = new Intent(getActivity(), GroupActivity.class);
        intent.putExtra(CENTER_ID, centerList.get(i).getId());
        startActivity(intent);*/
        MifoCentersListAdapter listAdapter = (MifoCentersListAdapter) adapterView.getAdapter();
        calendarId =  listAdapter.getItem(i).getCollectionMeetingCalendar().getId();
        long centerId =  listAdapter.getItem(i).getId();
        System.out.println("value of calendarId"+calendarId);
        getCollectionSheet(centerId,calendarId,centerList[i].getName());


    }

    private void getCollectionSheet(Long centerId,long calendarId ,  String centerTitle) {

        SharedPreferences transactionPreferences = getActivity().getSharedPreferences(OfflineCenterInputActivity.PREF_CENTER_DETAILS, Context.MODE_PRIVATE);

        SharedPreferences preferences = getActivity().getSharedPreferences(PREF_COLLECTION_DETAILS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.CENTER_ID, String.valueOf(centerId));
        editor.putString(Constants.DATE_OF_COLLECTION, DateHelper.getDateAsStringUsedForCollectionSheet(transactionPreferences.getString(OfflineCenterInputActivity.TRANSACTION_DATE_KEY,null)));
        editor.putString(Constants.CALENDAR_INSTANCE_ID, String.valueOf(calendarId));
        editor.putString(Constants.CENTER_TITLE, centerTitle);
        editor.commit();
        editor.apply();

        Payload payload = new Payload();
        payload.setCalendarId(calendarId);
        payload.setDateFormat("dd MM yyyy");
        payload.setLocale("en");
        payload.setTransactionDate(DateHelper.getDateAsStringUsedForCollectionSheet(transactionPreferences.getString(OfflineCenterInputActivity.TRANSACTION_DATE_KEY,null)));
        startActivity(new Intent(getActivity(), CollectionSheet.class));

    }


}
