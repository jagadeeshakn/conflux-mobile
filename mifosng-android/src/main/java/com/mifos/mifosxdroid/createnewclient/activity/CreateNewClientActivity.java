package com.mifos.mifosxdroid.createnewclient.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.PopupMenu;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.exceptions.InvalidTextInputException;
import com.mifos.exceptions.RequiredFieldException;
import com.mifos.exceptions.ShortOfLengthException;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.createnewclient.data.AadharDetail;
import com.mifos.objects.client.Client;
import com.mifos.objects.organisation.Office;
import com.mifos.services.ActivityResultBus;
import com.mifos.services.ActivityResultEvent;
import com.mifos.mifosxdroid.createnewclient.data.ClientPayload;
import com.mifos.utils.DateHelper;
import com.mifos.utils.MifosApplication;
import com.mifos.utils.SafeUIBlockingUtility;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
import retrofit.mime.TypedFile;

public class CreateNewClientActivity extends ActionBarActivity {


    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 11;
    private static final String TAG = "CreateNewClientActivity";
    @InjectView(R.id.et_uid)
    EditText et_uid;
    @InjectView(R.id.et_client_first_name)
    EditText et_clientFirstName;
    @InjectView(R.id.et_client_last_name)
    EditText et_clientLastName;
    @InjectView(R.id.et_fname)
    EditText et_fname;
    @InjectView(R.id.cb_client_active_status)
    CheckBox cb_clientActiveStatus;
    @InjectView(R.id.tv_submission_date)
    TextView tv_submissionDate;
    @InjectView(R.id.sp_offices)
    Spinner sp_offices;
    @InjectView(R.id.bt_scan)
    ImageButton bt_scan;
    @InjectView(R.id.sp_gender)
    Spinner sp_gender;
    @InjectView(R.id.et_dob)
    EditText et_dob;
    @InjectView(R.id.edit_dob)
    ImageButton edit_dob;
    @InjectView(R.id.edit_submission_date)
    ImageButton edit_submission;
    @InjectView(R.id.bt_next)
    Button bt_next;
    @InjectView(R.id.bt_save)
    Button bt_save;
    @InjectView(R.id.bt_check)
    Button bt_check;
    @InjectView(R.id.bt_home)
    Button bt_home;
    @InjectView(R.id.iv_new_clientImage)
    ImageView iv_clientImage;
    int officeId;
    Boolean result = true;
    View rootView;
    String dateString;
    SafeUIBlockingUtility safeUIBlockingUtility;
    ActionBarActivity activity;
    SharedPreferences sharedPreferences;
    private File capturedClientImageFile;
    ActionBar actionBar;
    private DialogFragment mfDatePicker;
    private HashMap<String, Integer> officeNameIdHashMap = new HashMap<String, Integer>();
    private int datePickerInput;
    private int year, month, day;
    private Calendar calendar;
    @InjectView(R.id.line_submission)
    LinearLayout line_submission;
    InputMethodManager inputMethodManager;
    int clientId = 1;
    AadharDetail aadharDetail = new AadharDetail();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_create_new_client);
        ButterKnife.inject(this);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Picasso.with(this).load(R.drawable.edit).resize((int) et_dob.getTextSize() + 30, (int) et_dob.getTextSize() + 30).into(edit_dob);
        Picasso.with(this).load(R.drawable.edit).resize((int) et_dob.getTextSize() + 30, (int) et_dob.getTextSize() + 30).into(edit_submission);
        Picasso.with(this).load(R.drawable.scanaadhar).resize(180, 100).into(bt_scan);
        activity = (ActionBarActivity) this;
        safeUIBlockingUtility = new SafeUIBlockingUtility(this);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        if (savedInstanceState == null) {
            inflateOfficeSpinner();
        }
        capturedClientImageFile = new File(this.getExternalCacheDir(), "client_image.png");
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
        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactDetails();
            }
        });
        bt_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeVisitActivity();
            }
        });
        bt_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientCheckActivity();
            }
        });
        iv_clientImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu menu = new PopupMenu(getApplicationContext(), view);
                menu.getMenuInflater().inflate(R.menu.client_image_popup, menu.getMenu());
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.client_image_capture:
                                captureClientImage();
                                break;
                            case R.id.client_image_remove:
                                deleteClientImage();
                                break;
                            default:
                                Log.e(TAG, "Unrecognized client image menu item");
                        }
                        return true;
                    }
                });
                menu.show();
            }
        });
        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ClientPayload clientPayload = new ClientPayload();
                //save new client creation
                clientPayload.setFirstname(et_clientFirstName.getEditableText().toString());
                clientPayload.setLastname(et_clientLastName.getEditableText().toString());
                clientPayload.setActive(cb_clientActiveStatus.isChecked());
                clientPayload.setLocale("en");
                clientPayload.setDateFormat("dd-MM-YYYY");
                clientPayload.setActivationDate(tv_submissionDate.getText().toString());
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
        tv_submissionDate.setText(new StringBuilder().append(day).append("-")
                .append(month + 1).append("-").append(year));
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
                et_dob.setText(new StringBuilder().append(day).append("-")
                        .append(month).append("-").append(year));
                break;
            case R.id.edit_submission_date:
                tv_submissionDate.setText(new StringBuilder().append(day).append("-")
                        .append(month).append("-").append(year));
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
    @OnClick(R.id.edit_dob)
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

    public void contactDetails() {
        Handler handler = new Handler(Looper.myLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), ClientContactDetailsActivity.class);
                Bundle contactData = new Bundle();
                contactData.putSerializable("data", aadharDetail);
                intent.putExtra("details", contactData);
                startActivity(intent);
                //finish();
            }
        });
    }

    public void homeVisitActivity() {
        Handler handler = new Handler(Looper.myLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), ClientHomeVisitActivity.class);
                startActivity(intent);
            }
        });
    }

    public void clientCheckActivity() {
        Handler handler = new Handler(Looper.myLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), ClientCreditsDetailsActivity.class);
                startActivity(intent);
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
        switch (requestCode) {
            case 111:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle resultData = data.getExtras();
                    Bundle bundleOfobject = resultData.getBundle("details");
                    aadharDetail = (AadharDetail) bundleOfobject.getSerializable("data");
                    setViews(aadharDetail);
                }
                break;
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                onClientImageCapture(resultCode, data);
                break;
        }
    }

    public void setViews(AadharDetail data) {
        String gender;
        et_clientFirstName = (EditText) findViewById(R.id.et_client_first_name);
        et_clientFirstName.setText(data.getName());
        et_clientLastName = (EditText) findViewById(R.id.et_client_last_name);
        et_clientLastName.setText(data.getName());
        et_uid = (EditText) findViewById(R.id.et_uid);
        et_uid.setText(data.getUid());
        et_fname = (EditText) findViewById(R.id.et_fname);
        et_fname.setText(data.getGname());
        gender = data.getGender();
        if (gender.equals("M")) {
            sp_gender.setSelection(0);
        } else {
            sp_gender.setSelection(1);
        }
        et_dob = (EditText) findViewById(R.id.et_dob);
        et_dob.setText(data.getDob());
    }

    public void captureClientImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(capturedClientImageFile));
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    public void deleteClientImage() {
        ((MifosApplication) this.getApplication()).api.clientService.deleteClientImage(clientId, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Toast.makeText(activity, "Image deleted", Toast.LENGTH_SHORT).show();
                iv_clientImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Toast.makeText(activity, "Failed to delete image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onClientImageCapture(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            uploadImage(capturedClientImageFile);
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // User cancelled the image capture.
        } else {
            Toast.makeText(activity, activity.getString(R.string.failed_to_capture_image), Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage(File pngFile) {

        final String imagePath = pngFile.getAbsolutePath();
        ((MifosApplication) this.getApplication()).api.clientService.uploadClientImage(clientId,
                new TypedFile("image/png", pngFile),
                new Callback<Response>() {


                    @Override
                    public void success(Response response, Response response2) {
                        Toast.makeText(activity, activity.getString(R.string.client_image_updated), Toast.LENGTH_SHORT).show();
                        Bitmap bitMap = BitmapFactory.decodeFile(imagePath);
                        iv_clientImage.setImageBitmap(bitMap);

                        File file = new File(imagePath);
                        BufferedReader reader = null;
                        StringBuilder text = new StringBuilder();
                        try {
                            reader = new BufferedReader(new FileReader(file));
                            String str;
                            if (imagePath != null) {
                                while ((str = reader.readLine()) != null) {
                                    text.append(str + "\n");
                                }
                            }
                            reader.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        Toast.makeText(activity, "Failed to update image", Toast.LENGTH_SHORT).show();

                    }
                }
        );
    }

}
