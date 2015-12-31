package com.mifos.mifosxdroid.createnewclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.createnewclient.data.AadharDetail;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by jagadeeshakn on 11/25/2015.
 */
public class ClientContactDetailsActivity extends ActionBarActivity {
    @InjectView(R.id.bt_next)
    Button bt_next;
    @InjectView(R.id.bt_save)
    Button bt_save;
    @InjectView(R.id.cb_current_address)
    CheckBox cb_current_address;
    @InjectView(R.id.et_current_village)
    EditText et_current_village;
    @InjectView(R.id.et_current_po)
    EditText et_current_po;
    @InjectView(R.id.et_current_dist)
    EditText et_current_dist;
    @InjectView(R.id.et_current_state)
    EditText et_current_state;
    @InjectView(R.id.et_current_pin)
    EditText et_current_pin;
    @InjectView(R.id.et_new_village)
    EditText et_village;
    @InjectView(R.id.et_new_po)
    EditText et_po;
    @InjectView(R.id.et_new_pin)
    EditText et_pin;
    @InjectView(R.id.et_new_dist)
    EditText et_dist;
    @InjectView(R.id.et_new_state)
    EditText et_state;
    @InjectView(R.id.ll_current_address)
    LinearLayout ll_current_address;
    public static final String TAG = "Client address details";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_new_client_address);
        ButterKnife.inject(this);
        Bundle resulData = this.getIntent().getExtras();
        AadharDetail ad = (AadharDetail) resulData.getBundle("details").getSerializable("data");
        setViews(ad);
        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                familyDetails();
            }
        });
        cb_current_address.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (ll_current_address.getVisibility() != View.VISIBLE) {
                    //getPermanentAddress();
                    ll_current_address.setVisibility(View.VISIBLE);
                } else {
                    ll_current_address.setVisibility(View.GONE);
                }
            }
        });
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_HOME_AS_UP | android.support.v7.app.ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher);
        getSupportActionBar().setTitle(R.string.dashboard);
        getSupportActionBar().setSubtitle(R.string.client_contact_details);
    }

    public void familyDetails() {
        Handler handler = new Handler(Looper.myLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), ClientFamilyDetailsActivity.class);
                startActivity(intent);
            }
        });
    }

    public void getPermanentAddress() {
        String villageName = et_village.getText().toString();
        et_current_village.setText(villageName);
        String postOfficeName = et_po.getText().toString();
        et_current_po.setText(postOfficeName);
        String distName = et_dist.getText().toString();
        et_current_dist.setText(distName);
        String stateName = et_state.getText().toString();
        et_current_state.setText(stateName);
        String pinNo = et_pin.getText().toString();
        et_current_pin.setText(pinNo);

    }

    public void setViews(AadharDetail data) {
        et_village.setText(data.getVtc());
        et_po.setText(data.getPo());
        et_pin.setText(data.getPc());
        et_dist.setText(data.getDist());
        et_state.setText(data.getState());
        getPermanentAddress();
    }

}
