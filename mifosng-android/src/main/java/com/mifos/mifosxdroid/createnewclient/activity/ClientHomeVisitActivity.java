package com.mifos.mifosxdroid.createnewclient.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.createnewclient.data.clientdetails.SourceOfIncomeOption;
import com.mifos.mifosxdroid.createnewclient.data.clientdetails.Template;
import com.mifos.services.TestAPI;
import com.mifos.utils.SafeUIBlockingUtility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by jagadeeshakn on 12/1/2015.
 */
public class ClientHomeVisitActivity extends ActionBarActivity {
    @InjectView(R.id.bt_save)
    Button bt_save;
    @InjectView(R.id.et_distance_center)
    EditText et_distance_center;
    @InjectView(R.id.et_eligible_amount)
    EditText et_eligible_amount;
    @InjectView(R.id.et_comment)
    EditText et_comment;
    @InjectView(R.id.sp_source_of_income)
    Spinner sp_source_of_income;
   TestAPI testAPI=new TestAPI();
    private HashMap<String, Integer> sourceofincomemap = new HashMap<String, Integer>();
    SafeUIBlockingUtility safeUIBlockingUtility;
    public static final String TAG = "Client HomeVisit Activity";

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_client_home_visit);
        ButterKnife.inject(this);
        safeUIBlockingUtility = new SafeUIBlockingUtility(ClientHomeVisitActivity.this);
        inflatetheparent();
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_HOME_AS_UP | android.support.v7.app.ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher);
        getSupportActionBar().setTitle(R.string.dashboard);
        getSupportActionBar().setSubtitle(R.string.client_home_visit);
    }

    public void inflatetheparent() {

        safeUIBlockingUtility.safelyBlockUI();

        testAPI.parentServices.getAllParent(new Callback<Template>() {
            @Override
            public void success(Template parents, Response response) {
                final List<String> sourceofincome = new ArrayList<String>();
                sourceofincome.add(getString(R.string.spinner_source_of_income));
                sourceofincomemap.put(getString(R.string.spinner_source_of_income), -1);
                for (SourceOfIncomeOption source : parents.getSourceOfIncomeOptions()) {
                    sourceofincome.add(source.getName());
                    sourceofincomemap.put(source.getName(), source.getId());
                }

                ArrayAdapter<String> sourceAdapter = new ArrayAdapter<String>(getApplicationContext(),
                        R.layout.simple_spinner_item, sourceofincome);

                sourceAdapter.notifyDataSetChanged();

                sourceAdapter.setDropDownViewResource(R.layout.simple_spinner_item);
                sp_source_of_income.setAdapter(sourceAdapter);

                sp_source_of_income.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        int sourceId = sourceofincomemap.get(sourceofincome.get(position));

                        if (sourceId != -1) {
                        } else {

                            Toast.makeText(getApplicationContext(), getString(R.string.error_select_source), Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                safeUIBlockingUtility.safelyUnBlockUI();
            }

            @Override
            public void failure(RetrofitError retrofitError) {

                System.out.println(retrofitError.getLocalizedMessage());

                safeUIBlockingUtility.safelyUnBlockUI();
            }
        });


    }


}
