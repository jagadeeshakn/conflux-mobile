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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.exceptions.InvalidTextInputException;
import com.mifos.exceptions.RequiredFieldException;
import com.mifos.exceptions.ShortOfLengthException;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.db.CenterCreationResponse;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.Staff;
import com.mifos.services.API;
import com.mifos.services.data.CenterPayload;
import com.mifos.utils.FragmentConstants;
import com.mifos.utils.MifosApplication;
import com.mifos.utils.SafeUIBlockingUtility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
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
    AutoCompleteTextView autoCompleteTextViewOffice;
    @InjectView(R.id.et_staff)
    AutoCompleteTextView autoCompleteTextViewStaff;
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
        if(!validStaffname())
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
                    Log.e(TAG,"Unsuccessfull "+API.userErrorMessage);
                    Toast.makeText(getActivity(),"unsuccessful \n"+ API.userErrorMessage,Toast.LENGTH_LONG).show();
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

        centerPayload=new CenterPayload();
        centerPayload.setName(et_center_name.getText().toString().trim());
        centerPayload.setOfficeId(officeId);
        submissionDateString = et_center_submission_date.getText().toString().trim();
        submissionDateString = submissionDateString.replace("-", " ");
        System.out.println(et_center_submission_date.getText().toString().trim());
        centerPayload.setSubmittedOnDate(submissionDateString);
        if(!TextUtils.isEmpty(autoCompleteTextViewStaff.getText().toString().trim())) {
            centerPayload.setStaffId(staffId);
        }



        if(checkboxActivate)
        {
            System.out.println("activation date"+tv_activate_date.getText().toString().trim());
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
        System.out.println("center payload " + centerPayload);
    }

  @OnClick(R.id.tv_active_date)
    public void changeActivationDate(View view)
    {
        datePickerInput=view.getId();
        System.out.println(R.id.btn_edit_active_date);
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

                                                                                                       @Override
                                                                                                       public void success(List<Office> offices, Response response) {
                                                                                                           final List<String> officeList = new ArrayList<String>();

                                                                                                           for (Office office : offices) {
                                                                                                               officeList.add(office.getName());
                                                                                                               officeNameIdHashMap.put(office.getName(), office.getId());
                                                                                                           }
                                                                                                           officeAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                                                                                                                  R.layout.simple_spinner_item, officeList);
                                                                                                           autoCompleteTextViewOffice.setThreshold(1);

                                                                                                           autoCompleteTextViewOffice.setAdapter(officeAdapter);
                                                                                                           autoCompleteTextViewOffice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                                                                                               @Override
                                                                                                               public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
                                                                                                                                          long arg3) {
                                                                                                                   Log.i(getClass().getName(),"Position:" + position + " Office:" + arg0.getItemAtPosition(position));

                                                                                                                   // TODO Auto-generated method stub
                                                                                                                   //Log.d("AutocompleteContacts", "onItemSelected() position " + position);
                                                                                                               }

                                                                                                               @Override
                                                                                                               public void onNothingSelected(AdapterView<?> arg0) {
                                                                                                                   // TODO Auto-generated method stub

                                                                                                                   InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                                                                                                                           getActivity().INPUT_METHOD_SERVICE);
                                                                                                                   imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

                                                                                                               }

                                                                                                           });
                                                                                                           autoCompleteTextViewOffice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                                                                                                                                 @Override
                                                                                                                                                                 public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                                                                                                                                     officeId = officeNameIdHashMap.get(adapterView.getItemAtPosition(i));
                                                                                                                                                                     Log.i(getClass().getName(),"office Id " + officeId);

                                                                                                                                                                     if (officeId != -1) {

                                                                                                                                                                         inflateStaffSpinner(officeId);

                                                                                                                                                                     } else {

                                                                                                                                                                         Toast.makeText(getActivity(), getString(R.string.error_select_office), Toast.LENGTH_SHORT).show();

                                                                                                                                                                     }

                                                                                                                                                                     Log.d("AutocompleteOffice", "Position : " + i + " Office : " + adapterView.getItemAtPosition(i));
                                                                                                                                                                 }
                                                                                                                                                             }
                                                                                                           );
                                                                                                           safeUIBlockingUtility.safelyUnBlockUI();
                                                                                                       }

                                                                                                       @Override
                                                                                                       public void failure(RetrofitError error) {
                                                                                                           safeUIBlockingUtility.safelyUnBlockUI();
                                                                                                           Toast.makeText(getActivity(),API.userErrorMessage,Toast.LENGTH_LONG).show();
                                                                                                       }
                                                                                                   }
        );
    }


    public void inflateStaffSpinner(final int officeId) {


        ((MifosApplication) getActivity().getApplicationContext()).api.staffService.getStaffForOffice(officeId, new Callback<List<Staff>>() {
            @Override
            public void success(List<Staff> staffs, Response response) {

                final List<String> staffNames = new ArrayList<String>();

                staffNames.add(getString(R.string.spinner_staff));
                staffNameIdHashMap.put(getString(R.string.spinner_staff), -1);

                for (Staff staff : staffs) {
                    staffNames.add(staff.getDisplayName());
                    staffNameIdHashMap.put(staff.getDisplayName(), staff.getId());
                }


                ArrayAdapter<String> staffAdapter = new ArrayAdapter<String>(getActivity(),
                        R.layout.simple_spinner_item, staffNames);

                staffAdapter.notifyDataSetChanged();

                staffAdapter.setDropDownViewResource(R.layout.simple_spinner_item);
                autoCompleteTextViewStaff.setThreshold(1);
                autoCompleteTextViewStaff.setAdapter(staffAdapter);

                autoCompleteTextViewStaff.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
                                               long arg3) {

                        // TODO Auto-generated method stub
                        //Log.d("AutocompleteContacts", "onItemSelected() position " + position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub

                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                                getActivity().INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

                    }


                });
                autoCompleteTextViewStaff.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                                     @Override
                                                                     public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                                         staffId = staffNameIdHashMap.get(adapterView.getItemAtPosition(i));
                                                                         Log.d("AutocompleteStaff", "Position:" + i + " Staff Name :" + adapterView.getItemAtPosition(i));
                                                                     }
                                                                 }
                );


            }

            @Override
            public void failure(RetrofitError retrofitError) {

                System.out.println(retrofitError.getLocalizedMessage());


            }
        });


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


    public boolean validStaffname()
    {
        result = true;
        try
        {
            if(!TextUtils.isEmpty(autoCompleteTextViewStaff.getText().toString())&&staffId<=0) {
                throw new RequiredFieldException("Invalid "+getResources().getString(R.string.staff_name), getResources().getString(R.string.invalid_saff_name));
            }
        }
        catch (RequiredFieldException e) {
            e.notifyUserWithToast(getActivity());
            result = false;
        }
        return result;

    }

    public boolean validOfficeName()
    {
        result=true;
        try
        {
            if(TextUtils.isEmpty(autoCompleteTextViewOffice.getText().toString()))
                throw  new RequiredFieldException(getResources().getString(R.string.office),getResources().getString(R.string.error_cannot_be_empty));
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
        catch (RequiredFieldException e) {
            e.notifyUserWithToast(getActivity());
            result = false;
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
