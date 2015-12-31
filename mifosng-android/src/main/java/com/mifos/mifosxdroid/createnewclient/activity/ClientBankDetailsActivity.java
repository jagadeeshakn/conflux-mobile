package com.mifos.mifosxdroid.createnewclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by jagadeeshakn on 12/2/2015.
 */
public class ClientBankDetailsActivity extends ActionBarActivity {
    @InjectView(R.id.tv_bankName)
    TextView tv_bankName;
    @InjectView(R.id.et_bankName)
    EditText et_bankName;
    @InjectView(R.id.tv_branchName)
    TextView tv_branchName;
    @InjectView(R.id.et_branchName)
    EditText et_branchName;
    @InjectView(R.id.tv_accountNo)
    TextView tv_accountNumber;
    @InjectView(R.id.et_accountNo)
    EditText et_accountNumber;
    @InjectView(R.id.tv_account_type)
    TextView tv_account_type;
    @InjectView(R.id.et_account_type)
    EditText et_account_type;
    @InjectView(R.id.tv_ifsc_code)
    TextView tv_ifsc_code;
    @InjectView(R.id.et_ifsc_code)
    EditText et_ifsc_code;
    @InjectView(R.id.bt_next)
    Button bt_next;
    @InjectView(R.id.bt_save)
    Button bt_save;
    public static final String TAG = "Client bank details";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_newclient_bank_details);
        ButterKnife.inject(this);
        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientIncomeDetails();
            }
        });
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_HOME_AS_UP | android.support.v7.app.ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher);
        getSupportActionBar().setTitle(R.string.dashboard);
        getSupportActionBar().setSubtitle(R.string.client_bank_details);
    }
    public void clientIncomeDetails(){
        Handler handler= new Handler(Looper.myLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(),ClientIncomeDtailsActivity.class);
                startActivity(intent);
            }
        });
    }
}
