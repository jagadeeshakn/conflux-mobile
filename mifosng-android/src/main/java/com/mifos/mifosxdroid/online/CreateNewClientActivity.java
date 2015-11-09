package com.mifos.mifosxdroid.online;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.exceptions.InvalidTextInputException;
import com.mifos.exceptions.RequiredFieldException;
import com.mifos.exceptions.ShortOfLengthException;
import com.mifos.mifosxdroid.R;
import com.mifos.objects.client.Client;
import com.mifos.objects.organisation.Office;
import com.mifos.services.ActivityResultBus;
import com.mifos.services.ActivityResultEvent;
import com.mifos.services.data.ClientPayload;
import com.mifos.utils.DateHelper;
import com.mifos.utils.MifosApplication;
import com.mifos.utils.SafeUIBlockingUtility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CreateNewClientActivity extends ActionBarActivity {

    @InjectView(R.id.et_uid)
    EditText et_uid;
    @InjectView(R.id.et_client_first_name)
    EditText et_clientFirstName;
    //@InjectView(R.id.tv_client_first_name)
    //TextView tv_clientFirstName;
    @InjectView(R.id.et_client_last_name)
    EditText et_clientLastName;
    //@InjectView(R.id.tv_client_last_name)
    //TextView tv_clientLastName;
    @InjectView(R.id.et_fname)
    EditText et_fname;
    // @InjectView(R.id.tv_fname)
    //TextView tv_fname;
    @InjectView(R.id.cb_client_active_status)
    CheckBox cb_clientActiveStatus;
    @InjectView(R.id.tv_submission_date)
    TextView tv_submissionDate;
    @InjectView(R.id.sp_offices)
    Spinner sp_offices;
    @InjectView(R.id.bt_submit)
    Button bt_submit;
    @InjectView(R.id.bt_scan)
    ImageButton bt_scan;
    @InjectView(R.id.sp_gender)
    Spinner sp_gender;
    @InjectView(R.id.et_dob)
    EditText et_dob;
    @InjectView(R.id.et_village)
    EditText et_village;
    @InjectView(R.id.et_po)
    EditText et_po;
    @InjectView(R.id.et_pin)
    EditText et_pin;
    @InjectView(R.id.et_dist)
    EditText et_dist;
    @InjectView(R.id.et_state)
    EditText et_state;
    @InjectView(R.id.edit_dob)
    ImageButton edit_dob;
    @InjectView(R.id.edit_submission_date)
    ImageButton edit_submission;
    int officeId;
    Boolean result = true;
    View rootView;
    String dateString;
    SafeUIBlockingUtility safeUIBlockingUtility;
    private DialogFragment mfDatePicker;
    private HashMap<String, Integer> officeNameIdHashMap = new HashMap<String, Integer>();
    private int datePickerInput;
    private int year, month, day;
    private Calendar calendar;
    @InjectView(R.id.line_submission)
    LinearLayout line_submission;
    InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_create_new_client);
        ButterKnife.inject(this);
        inputMethodManager = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Picasso.with(this).load(R.drawable.edit).resize((int) et_dob.getTextSize() + 30, (int) et_dob.getTextSize() + 30).into(edit_dob);
        Picasso.with(this).load(R.drawable.edit).resize((int) et_dob.getTextSize() + 30, (int) et_dob.getTextSize() + 30).into(edit_submission);
        Picasso.with(this).load(R.drawable.scanaadhar).resize(180, 100).into(bt_scan);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        if (savedInstanceState == null) {
            inflateOfficeSpinner();
        }
        inflateSubmissionDate();
        cb_clientActiveStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)
                    line_submission.setVisibility(View.VISIBLE);
                else
                    line_submission.setVisibility(View.GONE);
            }
        });
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ClientPayload clientPayload = new ClientPayload();

                clientPayload.setFirstname(et_clientFirstName.getEditableText().toString());
                clientPayload.setLastname(et_clientLastName.getEditableText().toString());
                clientPayload.setActive(cb_clientActiveStatus.isChecked());
                clientPayload.setActivationDate(dateString);
                clientPayload.setOfficeId(officeId);

                initiateClientCreation(clientPayload);

            }
        });
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_HOME_AS_UP | android.support.v7.app.ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher);
        getSupportActionBar().setTitle(R.string.dashboard);
        getSupportActionBar().setSubtitle(R.string.create_new_client);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        officeNameIdHashMap = null;
    }

    public void inflateSubmissionDate() {
        tv_submissionDate.setText(new StringBuilder().append(day).append("/")
                .append(month + 1).append("/").append(year));

    }

    private void initiateClientCreation(ClientPayload clientPayload) {

        //TextField validations
        if (!isValidLastName()) {
            return;
        }

        if (!isValidFirstName()) {
            return;
        }

        //Date validation : check for date less than or equal to current date
        if (!isValidDate()) {
            Toast.makeText(getApplicationContext(), "Date cannot be in future", Toast.LENGTH_LONG).show();
        } else {

            safeUIBlockingUtility.safelyBlockUI();

            ((MifosApplication) CreateNewClientActivity.this.getApplicationContext()).api.clientService.createClient(clientPayload, new Callback<Client>() {
                @Override
                public void success(Client client, Response response) {
                    safeUIBlockingUtility.safelyUnBlockUI();
                    Toast.makeText(getApplicationContext(), "Client created successfully", Toast.LENGTH_LONG).show();

                }

                @Override
                public void failure(RetrofitError error) {
                    safeUIBlockingUtility.safelyUnBlockUI();
                    Toast.makeText(getApplicationContext(), "Try again", Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            showDate(arg1, arg2 + 1, arg3);
        }
    };

    private void showDate(int year, int month, int day) {
        switch (datePickerInput) {
            case R.id.edit_dob:
            case R.id.et_dob:
                et_dob.setText(new StringBuilder().append(day).append("/")
                        .append(month).append("/").append(year));
                break;
            case R.id.edit_submission_date:
                tv_submissionDate.setText(new StringBuilder().append(day).append("/")
                        .append(month).append("/").append(year));
                break;
            default:
                break;
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    @OnClick(R.id.et_dob | R.id.edit_dob)
    public void inflateBirthdate(View view) {
        datePickerInput = view.getId();
        showDialog(999);

    }

    @SuppressWarnings("deprecation")
    @OnClick(R.id.edit_submission_date)
    public void inflateSubmissiondate(View view) {
        datePickerInput = view.getId();
        showDialog(999);
    }

    public boolean isValidFirstName() {
        try {
            if (TextUtils.isEmpty(et_clientFirstName.getEditableText().toString())) {
                throw new RequiredFieldException(getResources().getString(R.string.first_name), getResources().getString(R.string.error_cannot_be_empty));
            }

            if (et_clientFirstName.getEditableText().toString().trim().length() < 4 && et_clientFirstName.getEditableText().toString().trim().length() > 0) {
                throw new ShortOfLengthException(getResources().getString(R.string.first_name), 4);
            }
            if (!et_clientFirstName.getEditableText().toString().matches("[a-zA-Z]+")) {
                throw new InvalidTextInputException(getResources().getString(R.string.first_name), getResources().getString(R.string.error_should_contain_only), InvalidTextInputException.TYPE_ALPHABETS);
            }
        } catch (InvalidTextInputException e) {
            e.notifyUserWithToast(this);
            result = false;
        } catch (ShortOfLengthException e) {
            e.notifyUserWithToast(this);
            result = false;
        } catch (RequiredFieldException e) {
            e.notifyUserWithToast(this);
            result = false;
        }
        return result;
    }

    public boolean isValidLastName() {
        result = true;
        try {
            if (TextUtils.isEmpty(et_clientLastName.getEditableText().toString())) {
                throw new RequiredFieldException(getResources().getString(R.string.last_name), getResources().getString(R.string.error_cannot_be_empty));
            }

            if (et_clientLastName.getEditableText().toString().trim().length() < 4 && et_clientFirstName.getEditableText().toString().trim().length() > 0) {
                throw new ShortOfLengthException(getResources().getString(R.string.last_name), 4);
            }

            if (!et_clientLastName.getEditableText().toString().matches("[a-zA-Z]+")) {
                throw new InvalidTextInputException(getResources().getString(R.string.last_name), getResources().getString(R.string.error_should_contain_only), InvalidTextInputException.TYPE_ALPHABETS);
            }

        } catch (InvalidTextInputException e) {
            e.notifyUserWithToast(this);
            result = false;
        } catch (ShortOfLengthException e) {
            e.notifyUserWithToast(this);
            result = false;
        } catch (RequiredFieldException e) {
            e.notifyUserWithToast(this);
            result = false;
        }

        return result;
    }

    public boolean isValidDate() {

        List<Integer> date1 = new ArrayList<>();
        List<Integer> date2 = new ArrayList<>();
        date1 = DateHelper.getCurrentDateAsListOfIntegers();
        date2 = DateHelper.getDateList(tv_submissionDate.getText().toString(), "-");

        Collections.reverse(date2);
        int i = DateHelper.dateComparator(date1, date2);
        if (i == -1) {
            result = false;
        }
        return result;
    }

    @OnClick(R.id.bt_scan)
    public void loadAadharScanner() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), AadharQrcode.class);
                startActivityForResult(intent, 111);
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putSerializable("officeId", officeNameIdHashMap);
        super.onSaveInstanceState(outState, outPersistentState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.getSerializable("officeId") != null) {
                officeNameIdHashMap = (HashMap<String, Integer>) savedInstanceState.getSerializable("officeId");
            } else {
                inflateOfficeSpinner();
            }
        }
    }

    private void inflateOfficeSpinner() {
        safeUIBlockingUtility = new SafeUIBlockingUtility(this);
        safeUIBlockingUtility.safelyBlockUI();
        ((MifosApplication) this.getApplicationContext()).api.officeService.getAllOffices(new Callback<List<Office>>() {

                                                                                              @Override
                                                                                              public void success(List<Office> offices, Response response) {
                                                                                                  final List<String> officeList = new ArrayList<String>();

                                                                                                  for (Office office : offices) {
                                                                                                      officeList.add(office.getName());
                                                                                                      officeNameIdHashMap.put(office.getName(), office.getId());
                                                                                                  }
                                                                                                  ArrayAdapter<String> officeAdapter = new ArrayAdapter<String>(getApplicationContext(),
                                                                                                          R.layout.simple_spinner_item, officeList);
                                                                                                  officeAdapter.setDropDownViewResource(R.layout.simple_spinner_item);
                                                                                                  sp_offices.setAdapter(officeAdapter);
                                                                                                  sp_offices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                                                                                      @Override
                                                                                                      public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                                                                          officeId = officeNameIdHashMap.get(officeList.get(i));
                                                                                                          Log.d("officeId " + officeList.get(i), String.valueOf(officeId));

                                                                                                      }

                                                                                                      @Override
                                                                                                      public void onNothingSelected(AdapterView<?> adapterView) {

                                                                                                      }

                                                                                                  });
                                                                                                  safeUIBlockingUtility.safelyUnBlockUI();
                                                                                              }

                                                                                              @Override
                                                                                              public void failure(RetrofitError error) {
                                                                                                  safeUIBlockingUtility.safelyUnBlockUI();
                                                                                              }
                                                                                          }
        );
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ActivityResultBus.getInstance().postQueue(
                new ActivityResultEvent(requestCode, resultCode, data));
        if (requestCode == 111) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle resultData = data.getExtras();
                Bundle bundleOfobject = resultData.getBundle("details");
                AadharDetail ad = (AadharDetail) bundleOfobject.getSerializable("data");
                setViews(ad);
            }
        }
    }

    public void setViews(AadharDetail data) {
        String gender;
        et_clientFirstName = (EditText) findViewById(R.id.et_client_first_name);
        et_clientFirstName.setText(data.getName());
        et_uid = (EditText) findViewById(R.id.et_uid);
        et_uid.setText(data.getUid());
        et_fname = (EditText) findViewById(R.id.et_fname);
        et_fname.setText(data.getGname());
        gender = data.getGender();
        if (gender.equals("M")) {
            sp_gender.setSelection(1);
        } else {
            sp_gender.setSelection(0);
        }
        et_dob = (EditText) findViewById(R.id.et_dob);
        et_dob.setText(data.getDob());
        et_village = (EditText) findViewById(R.id.et_village);
        et_village.setText(data.getVtc());
        et_po = (EditText) findViewById(R.id.et_po);
        et_po.setText(data.getPo());
        et_pin = (EditText) findViewById(R.id.et_pin);
        et_pin.setText(data.getPc());
        et_dist = (EditText) findViewById(R.id.et_dist);
        et_dist.setText(data.getDist());
        et_state = (EditText) findViewById(R.id.et_state);
        et_state.setText(data.getState());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scan_adhar_card, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id)
        {
            case R.id.action_settings:
            break;
            case android.R.id.home:
            //android.r.id.home is used to home button click listner
            super.onBackPressed();
            break;
            default:break;

        }

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }
}
