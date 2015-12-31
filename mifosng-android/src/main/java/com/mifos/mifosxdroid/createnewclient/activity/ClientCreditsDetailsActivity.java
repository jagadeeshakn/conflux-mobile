package com.mifos.mifosxdroid.createnewclient.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by jagadeeshakn on 11/25/2015.
 */
public class ClientCreditsDetailsActivity extends ActionBarActivity {
    @InjectView(R.id.tv_no_of_loans)
    TextView tv_no_of_loans;
    @InjectView(R.id.tv_indebtedness)
    TextView tv_indebtedness;
    @InjectView(R.id.tv_no_of_mifs)
    TextView tv_no_of_mifs;
    @InjectView(R.id.tv_arrears)
    TextView tv_arrears;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_new_client_credits_check);
        ButterKnife.inject(this);
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_HOME_AS_UP | android.support.v7.app.ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher);
        getSupportActionBar().setTitle(R.string.dashboard);
        getSupportActionBar().setSubtitle(R.string.client_credit_checks);
    }
}
