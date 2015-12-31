package com.mifos.mifosxdroid.online;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.mifos.mifosxdroid.adapters.OfficeSpinnerAdapter;
import com.mifos.mifosxdroid.adapters.StaffNameAdapter;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.db.CenterCreationResponse;
import com.mifos.objects.db.UserDetails;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.Staff;
import com.mifos.services.API;
import com.mifos.objects.CenterPayload;
import com.mifos.utils.FragmentConstants;
import com.mifos.utils.MifosApplication;
import com.mifos.utils.SafeUIBlockingUtility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateCenterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateCenterFragment extends Fragment implements MFDatePicker.OnDatePickListener  {


    private OnFragmentInteractionListener mListener;


    private View rootview;
    @InjectView(R.id.tv_center_name)
    TextView tv_name;
    @InjectView(R.id.et_center_name)
    EditText et_center_name;
    @InjectView(R.id.tv_office)
    TextView tv_office;
    @InjectView(R.id.et_office)
    Spinner spinnerOffice;
    @InjectView(R.id.et_staff)
    Spinner spinnerStaff;
    @InjectView(R.id.cb_center_active_status)
    CheckBox cb_centerActiveStatus;
    @InjectView(R.id.line_active)
    LinearLayout line_active;
    @InjectView(R.id.btn_edit_active_date)
    ImageButton ed_active_date;
    @InjectView(R.id.btn_edit_submission_date)
    ImageButton btn_ed_submission_date;
    @InjectView(R.id.tv_active_date)
    TextView tv_activate_date;
    @InjectView(R.id.et_center_submission_date)
    TextView et_center_submission_date;
    @InjectView(R.id.btn_create_center)
    Button btn_create_center;
    @InjectView(R.id.et_external_id)
    EditText et_external_id;


    private int datePickerInput;
    private int year, month, day;
    private DatePicker datePicker;
    private Calendar calendar;
    SafeUIBlockingUtility safeUIBlockingUtility;
    private ArrayAdapter<String> officeAdapter;
    int officeId=-1;
    private int staffId=-1;
    private DialogFragment mfDatePicker;
    boolean checkboxActivate=false;
    CenterPayload centerPayload;
    boolean result=true;


    private HashMap<String, Integer> officeNameIdHashMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> staffNameIdHashMap = new HashMap<String, Integer>();
    private List<Office> officeList;
    private List<Staff> staffList;
    private String activationDateString;
    private String submissionDateString;

    final String TAG=CreateCenterFragment.class.getSimpleName();


    // TODO: Rename and change types and number of parameters
    public static CreateCenterFragment newInstance(String param1, String param2) {
        CreateCenterFragment fragment = new CreateCenterFragment();
        return fragment;
    }

    public CreateCenterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"Create a New Center");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

      //  getSupportActionBar().setSubtitle(R.string.title_activity_centers);

        ((ActionBarActivity)getActivity()).getSupportActionBar().setSubtitle(R.string.create_center);

        //Set adapter to AutoCompleteTextView

        rootview=inflater.inflate(R.layout.fragment_create_center, container, false);
        ButterKnife.inject(this, rootview);
        setColor(getResources().getString(R.string.create_center_name), tv_name);
        setColor(getResources().getString(R.string.office_name), tv_office);


        Picasso.with(getActivity()).load(R.drawable.edit).resize((int) tv_activate_date.getTextSize() + 30, (int) tv_activate_date.getTextSize() + 30).into(ed_active_date);
        Picasso.with(getActivity()).load(R.drawable.edit).resize((int) tv_activate_date.getTextSize() + 30, (int) tv_activate_date.getTextSize() + 30).into(btn_ed_submission_date);

        if(savedInstanceState==null)
        {
            Log.i(TAG,"Populate the list of Offices");
            inflateOfficeSpinner();
        }
        inflateSubmissionDate();


        cb_centerActiveStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    checkboxActivate = true;
                    line_active.setVisibility(View.VISIBLE);
                    setActivationdate();
                } else
                    line_active.setVisibility(View.GONE);
            }
        });





        return rootview;
    }




    private void initiateCenterCreation(final CenterPayload centerPayload) {
        if (!validCenterName()) {
            return;
        }
        if (!validOfficeName())
        {
            return;
        }

        else
        {
            safeUIBlockingUtility.safelyBlockUI();
            ((MifosApplication)getActivity().getApplicationContext()).api.centerService.createCenter(centerPayload, new Callback<CenterCreationResponse>() {
                @Override
                public void success(CenterCreationResponse centerCreationResponse, Response response) {
                    safeUIBlockingUtility.safelyUnBlockUI();
                    Log.i(TAG,"A new Center has been created successfully ");
                    Toast.makeText(getActivity(),"A New Center with the name "+centerPayload.getName()+" has been  created successfully ",Toast.LENGTH_LONG).show();

                    getActivity().onBackPressed();
                }

                @Override
                public void failure(RetrofitError error) {
                    safeUIBlockingUtility.safelyUnBlockUI();
                    Log.e(TAG, "Unsuccessfull " + API.userErrorMessage);
                    Toast.makeText(getActivity(),"Unsuccessful : "+API.userErrorMessage,Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    public void onDatePicked(String date) {

        switch (datePickerInput) {
            case R.id.tv_active_date:
            case R.id.btn_edit_active_date:
                tv_activate_date.setText(date);
                break;
            case R.id.et_center_submission_date:
            case R.id.btn_edit_submission_date:
            et_center_submission_date.setText(date);
                break;
            default:break;
        }
    }



    @OnClick(R.id.btn_create_center)
    public void createCenter(View view)
    {
        Log.i(TAG,"Populate the payload with data ");
        centerPayload=new CenterPayload();
        centerPayload.setName(et_center_name.getText().toString().trim());
        centerPayload.setOfficeId(officeId);
        submissionDateString = et_center_submission_date.getText().toString().trim();
        submissionDateString = submissionDateString.replace("-", " ");
        centerPayload.setSubmittedOnDate(submissionDateString);
        if(staffId!=-1) {
            centerPayload.setStaffId(staffId);
        }


        if(checkboxActivate)
        {
            activationDateString = tv_activate_date.getText().toString().trim();
            activationDateString = activationDateString.replace("-", " ");
            centerPayload.setActive(checkboxActivate);
            centerPayload.setActivationDate(activationDateString);
        }
        else
        {
            centerPayload.setActive(checkboxActivate);
        }

        centerPayload.setExternalId(et_external_id.getText().toString().trim());
        initiateCenterCreation(centerPayload);
    }

  @OnClick(R.id.tv_active_date)
    public void changeActivationDate(View view)
    {
        Log.i(TAG,"Change the Activation Date");
        datePickerInput=view.getId();
        mfDatePicker = MFDatePicker.newInsance(this);
        mfDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants.DFRAG_DATE_PICKER);
    }
@OnClick(R.id.btn_edit_active_date)
public void changeActivatedate(View view)
{
    changeActivationDate(view);
}

    @OnClick(R.id.btn_edit_submission_date)
    public void changeSubmissionDatebtn(View view)
    {
        changeActivationDate(view);
    }

    @OnClick(R.id.et_center_submission_date)
    public void changeSubmissionDate(View view)
    {
        changeActivationDate(view);
    }


    public void setActivationdate()
    {
        tv_activate_date.setText(new StringBuilder().append(day).append("-")
                .append(month + 1).append("-").append(year));

    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState!=null) {
            if(savedInstanceState.getSerializable("officeId")!=null)
            {
                officeNameIdHashMap = (HashMap<String, Integer>) savedInstanceState.getSerializable("officeId");
            }
            else
            {
                inflateOfficeSpinner();
            }
        }
    }

    private void inflateOfficeSpinner() {
        safeUIBlockingUtility = new SafeUIBlockingUtility(getActivity());
        safeUIBlockingUtility.safelyBlockUI();
        ((MifosApplication) getActivity().getApplicationContext()).api.officeService.getAllOffices(new Callback<List<Office>>() {

                                                                                                       public void success(List<Office> offices, Response response) {
                                                                                                           officeList = offices;
                                                                                                           Office dummyOffice = new Office();
                                                                                                           dummyOffice.setId(-1);
                                                                                                           dummyOffice.setName(getResources().getString(R.string.select_office));
                                                                                                           offices.add(0, dummyOffice);
                                                                                                           OfficeSpinnerAdapter officeSpinnerAdapter = new OfficeSpinnerAdapter(getActivity(), offices);
                                                                                                           spinnerOffice.setAdapter(officeSpinnerAdapter);

                                                                                                           officeSpinnerAdapter.notifyDataSetChanged();
                                                                                                           Iterator iterator = UserDetails.findAll(UserDetails.class);
                                                                                                           UserDetails userDetails = new UserDetails();
                                                                                                           while (iterator.hasNext()) {
                                                                                                               userDetails = (UserDetails) iterator.next();
                                                                                                           }

                                                                                                           for (Office office : officeList) {
                                                                                                               if (office.getId() == userDetails.getOfficeId()) {
                                                                                                                   int position = officeList.indexOf(office);
                                                                                                                   spinnerOffice.setSelection(position, true);
                                                                                                                   officeId = office.getId();
                                                                                                                   inflateStaffSpinner(officeId);
                                                                                                                   break;
                                                                                                               }
                                                                                                           }

                                                                                                           // officeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                                                                                           spinnerOffice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                                               @Override
                                                                                                               public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                                                                                                   officeId = officeList.get(position).getId();
                                                                                                                   if (officeId != -1) {

                                                                                                                       inflateStaffSpinner(officeId);

                                                                                                                   } else {

                                                                                                                       Toast.makeText(getActivity(), getString(R.string.error_select_office), Toast.LENGTH_SHORT).show();

                                                                                                                   }

                                                                                                               }

                                                                                                               @Override
                                                                                                               public void onNothingSelected(AdapterView<?> parent) {

                                                                                                               }
                                                                                                           });

                                                                                                           safeUIBlockingUtility.safelyUnBlockUI();

                                                                                                       }


                                                                                                       @Override
                                                                                                       public void failure(RetrofitError error) {
                                                                                                           safeUIBlockingUtility.safelyUnBlockUI();

                                                                                                           Toast.makeText(getActivity(), API.userErrorMessage, Toast.LENGTH_LONG).show();
                                                                                                           getActivity().onBackPressed();
                                                                                                       }
                                                                                                   }
        );
    }


    public void inflateStaffSpinner(final int officeId) {


        ((MifosApplication) getActivity().getApplicationContext()).api.staffService.getStaffForOffice(officeId, new Callback<List<Staff>>() {
            public void success(List<Staff> staffs, Response response) {
                safeUIBlockingUtility.safelyUnBlockUI();
                staffList = staffs;
                Staff dummyStaff = new Staff();
                dummyStaff.setId(-1);
                dummyStaff.setDisplayName("Select Staff");
                staffList.add(0, dummyStaff);
                StaffNameAdapter staffNameAdapter = new StaffNameAdapter(getActivity(), staffList);
                spinnerStaff.setAdapter(staffNameAdapter);
                setStaffSpinnerSelection();


                spinnerStaff.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        int staffId = staffList.get(position).getId();

                        if (staffId != -1) {

                        } else {

                           /* Toast.makeText(getActivity(), getString(R.string.error_select_staff), Toast.LENGTH_SHORT).show();*/

                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


            }


            @Override
            public void failure(RetrofitError retrofitError) {
                Toast.makeText(getActivity(), API.userErrorMessage, Toast.LENGTH_LONG).show();
                getActivity().onBackPressed();

            }
        });


    }

    public void setStaffSpinnerSelection()
    {
        Iterator iterator = UserDetails.findAll(UserDetails.class);
        UserDetails userDetails = new UserDetails();
        while(iterator.hasNext())
        {
            userDetails =(UserDetails)iterator.next();
        }
        int position;
        for(Staff staff:staffList)
        {
            if(staff.getId()==userDetails.getStaffId())
            {
                position = staffList.indexOf(staff);
                staffId = staff.getId();
                spinnerStaff.setSelection(position,true);
                break;
            }
        }


    }

    public void inflateSubmissionDate() {
        et_center_submission_date.setText(new StringBuilder().append(day).append("-")
                .append(month + 1).append("-").append(year));

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("officeId", officeNameIdHashMap);
        super.onSaveInstanceState(outState);
    }

    public void setColor(String value,TextView temp_text_view)
    {
        Spannable name=new SpannableString(value);
        name.setSpan(new ForegroundColorSpan(Color.BLACK), 0, value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        name.setSpan(new ForegroundColorSpan(Color.RED), value.length()-1, value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        temp_text_view.setText(name);

    }



    public boolean validOfficeName()
    {
        result=true;
        try
        {
            if(officeId==-1)
            {
                throw new InvalidTextInputException(getResources().getString(R.string.the_input),getResources().getString(R.string.office_name), getResources().getString(R.string.is_invalid));
            }
        }
        catch (InvalidTextInputException e)
        {
            e.notifyUserWithToast(getActivity());
            result=false;
        }
        return result;
    }
    public boolean validCenterName() {
        result = true;
        try {
            if (TextUtils.isEmpty(et_center_name.getEditableText().toString())) {
                throw new RequiredFieldException(getResources().getString(R.string.center_name), getResources().getString(R.string.error_cannot_be_empty));
            }

            if (et_center_name.getEditableText().toString().trim().length() < 4 && et_center_name.getEditableText().toString().trim().length() > 0) {
                throw new ShortOfLengthException(getResources().getString(R.string.center_name), 4);
            }

        }
        catch (ShortOfLengthException e) {
            e.notifyUserWithToast(getActivity());
            result = false;
        } catch (RequiredFieldException e) {
            e.notifyUserWithToast(getActivity());
            result = false;
        }

        return result;
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
