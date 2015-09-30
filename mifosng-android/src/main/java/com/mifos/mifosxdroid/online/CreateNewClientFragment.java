/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */


package com.mifos.mifosxdroid.online;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.exceptions.InvalidTextInputException;
import com.mifos.exceptions.RequiredFieldException;
import com.mifos.exceptions.ShortOfLengthException;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.client.Client;
import com.mifos.objects.organisation.Office;
import com.mifos.services.data.ClientPayload;
import com.mifos.utils.DateHelper;
import com.mifos.utils.FragmentConstants;
import com.mifos.utils.MifosApplication;
import com.mifos.utils.SafeUIBlockingUtility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.mifos.utils.FragmentConstants.*;

public class CreateNewClientFragment extends Fragment implements MFDatePicker.OnDatePickListener {

    @InjectView(R.id.et_client_first_name)
    EditText et_clientFirstName;
    @InjectView(R.id.tv_client_first_name)
    TextView tv_clientFirstName;
    @InjectView(R.id.et_client_last_name)
    EditText et_clientLastName;
    @InjectView(R.id.tv_client_last_name)
    TextView tv_clientLastName;
    @InjectView(R.id.cb_client_active_status)
    CheckBox cb_clientActiveStatus;
    @InjectView(R.id.tv_submission_date)
    TextView tv_submissionDate;
    @InjectView(R.id.sp_offices)
    Spinner sp_offices;
    @InjectView(R.id.bt_submit)
    Button bt_submit;
    @InjectView(R.id.bt_scan)
    Button bt_scan;


    int officeId;
    Boolean result = true;
    View rootView;
    String dateString;
    SafeUIBlockingUtility safeUIBlockingUtility;
    private DialogFragment mfDatePicker;
    private HashMap<String, Integer> officeNameIdHashMap = new HashMap<String, Integer>();

    public CreateNewClientFragment() {
        // Required empty public constructor
    }

    public static CreateNewClientFragment newInstance() {
        CreateNewClientFragment createNewClientFragment = new CreateNewClientFragment();
        return createNewClientFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_create_new_client, container, false);
        ButterKnife.inject(this, rootView);
        inflateOfficeSpinner();
        inflateSubmissionDate();
        //handle when scan Aadhar button is clicked
        bt_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAadharScanner();
            }
        });
        //client active checkbox onCheckedListener
        cb_clientActiveStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)
                    tv_submissionDate.setVisibility(View.VISIBLE);
                else
                    tv_submissionDate.setVisibility(View.GONE);
            }
        });

        dateString = tv_submissionDate.getText().toString();
        dateString = DateHelper.getDateAsStringUsedForCollectionSheetPayload(dateString).replace("-", " ");

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

        return rootView;
    }

    //inflating office list spinner
    private void inflateOfficeSpinner() {
        safeUIBlockingUtility = new SafeUIBlockingUtility(getActivity());
        safeUIBlockingUtility.safelyBlockUI();
        ((MifosApplication) getActivity().getApplicationContext()).api.officeService.getAllOffices(new Callback<List<Office>>() {

                                                                                                       @Override
                                                                                                       public void success(List<Office> offices, Response response) {
                                                                                                           final List<String> officeList = new ArrayList<String>();

                                                                                                           for (Office office : offices) {
                                                                                                               officeList.add(office.getName());
                                                                                                               officeNameIdHashMap.put(office.getName(), office.getId());
                                                                                                           }
                                                                                                           ArrayAdapter<String> officeAdapter = new ArrayAdapter<String>(getActivity(),
                                                                                                                   android.R.layout.simple_spinner_item, officeList);
                                                                                                           officeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
            Toast.makeText(getActivity(), "Date cannot be in future", Toast.LENGTH_LONG).show();
        } else {

            safeUIBlockingUtility.safelyBlockUI();

            ((MifosApplication) getActivity().getApplicationContext()).api.clientService.createClient(clientPayload, new Callback<Client>() {
                @Override
                public void success(Client client, Response response) {
                    safeUIBlockingUtility.safelyUnBlockUI();
                    Toast.makeText(getActivity(), "Client created successfully", Toast.LENGTH_LONG).show();

                }

                @Override
                public void failure(RetrofitError error) {
                    safeUIBlockingUtility.safelyUnBlockUI();
                    Toast.makeText(getActivity(), "Try again", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void inflateSubmissionDate() {
        mfDatePicker = MFDatePicker.newInsance(this);

        tv_submissionDate.setText(MFDatePicker.getDatePickedAsString());

        tv_submissionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mfDatePicker.show(getActivity().getSupportFragmentManager(), DFRAG_DATE_PICKER);
            }
        });
    }

    @Override
    public void onDatePicked(String date) {
        tv_submissionDate.setText(date);
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
            e.notifyUserWithToast(getActivity());
            result = false;
        } catch (ShortOfLengthException e) {
            e.notifyUserWithToast(getActivity());
            result = false;
        } catch (RequiredFieldException e) {
            e.notifyUserWithToast(getActivity());
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
            e.notifyUserWithToast(getActivity());
            result = false;
        } catch (ShortOfLengthException e) {
            e.notifyUserWithToast(getActivity());
            result = false;
        } catch (RequiredFieldException e) {
            e.notifyUserWithToast(getActivity());
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

    public void loadAadharScanner() {
        Intent intent = new Intent(getActivity(), AadharQrcode.class);
        startActivityForResult(intent, 111);
    }

    @Override
    public void onActivityResult(int requestcode, int resultcode, Intent data) {
        super.onActivityResult(requestcode, resultcode, data);
        if (requestcode == 111) {
            if (resultcode == Activity.RESULT_OK) {
                Bundle resultData = data.getExtras();
                Bundle bundleOfobject = resultData.getBundle("details");
                AadharDetail ad = (AadharDetail) bundleOfobject.getSerializable("data");
                setViews(ad);
            }
        }

    }
    public void setViews(AadharDetail data) {
        et_clientFirstName = (EditText) rootView.findViewById(R.id.et_client_first_name);
        et_clientFirstName.setText(data.getName());

    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
