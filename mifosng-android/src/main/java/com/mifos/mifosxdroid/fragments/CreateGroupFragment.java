package com.mifos.mifosxdroid.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.CenterSpinnerAdapter;
import com.mifos.mifosxdroid.adapters.OfficeSpinnerAdapter;
import com.mifos.mifosxdroid.adapters.StaffNameAdapter;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.db.UserDetails;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.GroupCreationResponseData;
import com.mifos.objects.group.GroupPayload;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.Staff;
import com.mifos.services.API;
import com.mifos.utils.FragmentConstants;
import com.mifos.utils.MifosApplication;
import com.mifos.utils.SafeUIBlockingUtility;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateGroupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateGroupFragment extends Fragment implements MFDatePicker.OnDatePickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String office_Id = "office Id";
    private static final String center_Id = "center Id";


    private View rootView;
    private int officeId;
    private int centerId;
    private  List<Office> officesList;
    private List<Center> centerList;
    private Map<String,Object> options = new HashMap<String,Object>();
    private int datePickerInput;
    private int year, month, day;
    private DatePicker datePicker;
    private Calendar calendar;
    private  boolean checkboxActivate=false;
    private final String TAG = getClass().getSimpleName();
    private MFDatePicker mfDatePicker;
    private List<Staff> staffList;
    private int staffId = -1;
    private SafeUIBlockingUtility safeUIBlockingUtility;

    @InjectView(R.id.sp_offices)
    Spinner spOffices;
    @InjectView(R.id.et_groupName)
    EditText et_groupName;
    @InjectView(R.id.tv_officeId)
    TextView tvOfficeId;
    @InjectView(R.id.tv_centerId)
    TextView tvCenterId;
    @InjectView(R.id.sp_centers)
    Spinner spCenter;
    @InjectView(R.id.tv_groupName)
    TextView tvGroupName;
    @InjectView(R.id.chk_bx_active_group_status)
    CheckBox cb_groupActiveStatus;
    @InjectView(R.id.line_active)
    LinearLayout line_active;
    @InjectView(R.id.btn_edit_active_date)
    ImageButton ed_active_date;
    @InjectView(R.id.btn_edit_submission_date)
    ImageButton btn_ed_submission_date;
    @InjectView(R.id.tv_active_date)
    TextView tv_activate_date;
    @InjectView(R.id.et_center_submission_date)
    TextView et_group_submission_date;
    @InjectView(R.id.sp_staffs)
    Spinner spStaffs;

    private OnFragmentInteractionListener mListener;

    public static CreateGroupFragment newInstance(int officeId, String officeName,int centerId,String centerName) {

        CreateGroupFragment fragment = new CreateGroupFragment();
        Bundle args = new Bundle();
        args.putInt(office_Id, officeId);
        args.putInt(center_Id, centerId);
        fragment.setArguments(args);
        return fragment;
    }

    public CreateGroupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Crete New Group action has been Instantiated");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            officeId = getArguments().getInt(office_Id);
            centerId = getArguments().getInt(center_Id);
        }
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i(TAG, "View has been Created");
        rootView = inflater.inflate(R.layout.fragment_create_group, container, false);
        ButterKnife.inject(this, rootView);
        setColor(getString(R.string.officeId), tvOfficeId);
        setColor(getString(R.string.enter_group_name), tvGroupName);
        options.put("paged", "false");
        Picasso.with(getActivity()).load(R.drawable.edit).resize((int) tv_activate_date.getTextSize() + 30, (int) tv_activate_date.getTextSize() + 30).into(ed_active_date);
        Picasso.with(getActivity()).load(R.drawable.edit).resize((int) tv_activate_date.getTextSize() + 30, (int) tv_activate_date.getTextSize() + 30).into(btn_ed_submission_date);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        ((ActionBarActivity)getActivity()).getSupportActionBar().setSubtitle(R.string.create_group);
        inflateSubmissionDate();
        safeUIBlockingUtility = new SafeUIBlockingUtility(getActivity());
        inflateOfficeSpinner();
        setSpinnerListnerForOffice();
        setSpinnerListnerForCenter();
        setSpinnerListnerForStaff();
        cb_groupActiveStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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

        return rootView;
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.i(TAG, "Restored");
        if (getArguments() != null) {
            officeId = getArguments().getInt(office_Id);
            centerId = getArguments().getInt(center_Id);
        }
    }

    public void setSpinnerListnerForCenter()
    {
        spCenter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                centerId = centerList.get(i).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void setSpinnerListnerForStaff()
    {
        spStaffs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                staffId = staffList.get(i).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void setActivationdate()
    {
        tv_activate_date.setText(new StringBuilder().append(day).append("-")
                .append(month + 1).append("-").append(year));

    }

    public void inflateSubmissionDate() {
        et_group_submission_date.setText(new StringBuilder().append(day).append("-")
                .append(month + 1).append("-").append(year));

    }

    public void onDatePicked(String date) {

        switch (datePickerInput) {
            case R.id.tv_active_date:
            case R.id.btn_edit_active_date:
                tv_activate_date.setText(date);
                break;
            case R.id.et_center_submission_date:
            case R.id.btn_edit_submission_date:
                et_group_submission_date.setText(date);
                break;
            default:break;
        }
    }

    @OnClick(R.id.tv_active_date)
    public void changeActivationDate(View view)
    {
        Log.i(TAG, "Change the Activation Date");
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

    public void setSpinnerListnerForOffice()
    {
       spOffices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               if (officesList.get(i).getId() != -1 && officesList.get(i).getId() != officeId) {
                   officeId = officesList.get(i).getId();
                   populateCenterSpinner();
               }
           }

           @Override
           public void onNothingSelected(AdapterView<?> adapterView) {

           }
       });
    }

    public void inflateOfficeSpinner()
    {
        safeUIBlockingUtility.safelyBlockUI();
        ((MifosApplication)getActivity().getApplicationContext()).api.officeService.getAllOffices(new Callback<List<Office>>() {

            @Override
            public void success(List<Office> offices, Response response) {
                safeUIBlockingUtility.safelyUnBlockUI();
                Log.i(TAG, "Success: Get List of offices.");
                // dummyOffice holds the id =-1 and office name = Select and office from the list
                officesList = offices;
                Office dummyOffice = new Office();
                dummyOffice.setId(-1);
                dummyOffice.setName(getResources().getString(R.string.select_office));
                offices.add(0, dummyOffice);
                OfficeSpinnerAdapter officeSpinnerAdapter = new OfficeSpinnerAdapter(getActivity(), offices);
                spOffices.setAdapter(officeSpinnerAdapter);
                //office ID has been inherited from centers activity so setting the spinner value to officeId
                setOfficeSpinnerSelection();
            }

            @Override
            public void failure(RetrofitError error) {
                safeUIBlockingUtility.safelyUnBlockUI();
                Log.i(TAG, "Failure: Get List of offices.");
                safeUIBlockingUtility.safelyUnblockUIForFailure(TAG, API.userErrorMessage);

            }
        });
    }

    public void setOfficeSpinnerSelection()
    {
        if(officeId!=-1)
        {
            for(Office office: officesList)
            {
                if(office.getId() == officeId)
                {
                    int position = officesList.indexOf(office);
                    spOffices.setSelection(position, true);

                    break;
                }
            }
            populateCenterSpinner();
            populateStaffSpinner();
        }
    }

    public void populateStaffSpinner()
    {

        safeUIBlockingUtility.safelyBlockUI();
        ((MifosApplication)getActivity().getApplicationContext()).api.staffService.getStaffForOffice(officeId, new Callback<List<Staff>>() {
            @Override
            public void success(List<Staff> staffs, Response response) {
                Log.i(TAG, "Success: Get List of Staff.");
                safeUIBlockingUtility.safelyUnBlockUI();
                staffList = staffs;
                Staff dummyStaff = new Staff();
                dummyStaff.setId(-1);
                dummyStaff.setDisplayName("Select Staff");
                staffList.add(0, dummyStaff);
                StaffNameAdapter staffNameAdapter = new StaffNameAdapter(getActivity(), staffList);
                spStaffs.setAdapter(staffNameAdapter);
                setStaffSpinnerSelection();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i(TAG, "Failure: Get List of Staff.");
                safeUIBlockingUtility.safelyUnblockUIForFailure(TAG, API.userErrorMessage);

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
               spStaffs.setSelection(position,true);
               break;
           }
       }


    }

    public void populateCenterSpinner()
    {
        safeUIBlockingUtility.safelyBlockUI();
        ((MifosApplication)getActivity().getApplicationContext()).api.centerService.getAllCentersInOffice(officeId, options, new Callback<List<Center>>() {
            @Override
            public void success(List<Center> centers, Response response) {
                Log.i(TAG, "Success: Get List of Centers.");
                safeUIBlockingUtility.safelyUnBlockUI();
                centerList = centers;
                Center dummyCenter = new Center();
                dummyCenter.setId(-1);
                dummyCenter.setName(getResources().getString(R.string.select_office));
                centers.add(0, dummyCenter);
                CenterSpinnerAdapter centerSpinnerAdapter = new CenterSpinnerAdapter(getActivity(), centers);
                spCenter.setAdapter(centerSpinnerAdapter);
                //office ID has been inherited from centers activity so setting the spinner value to officeId
                setCenterSpinnerSelection();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i(TAG, "Failure: Get List of Centers.");
                safeUIBlockingUtility.safelyUnblockUIForFailure(TAG, API.userErrorMessage);
            }
        });
    }

    public void setCenterSpinnerSelection()
    {
        if(centerId!=-1)
        {
            for(Center center:centerList)
            {
                if(center.getId()==centerId)
                {
                    int position = centerList.indexOf(center);
                    spCenter.setSelection(position,true);
                }
            }
        }
    }

    @OnClick(R.id.btn_submit)
    public void submit()
    {
        if(officeId != -1)
        {
            GroupPayload groupPayload = new GroupPayload();
            groupPayload.setOfficeId(String.valueOf(officeId));
            String groupName = et_groupName.getText().toString().trim();
            if(groupName.length()!=0||groupName.length()>4)
            {
                groupPayload.setName(groupName);
            }
            else
            {
                Toast.makeText(getActivity(),"Group Name must be more than 4 charecters",Toast.LENGTH_LONG).show();
            }
            if(cb_groupActiveStatus.isChecked())
            {
                groupPayload.setActive(true);
                String activationDate = tv_activate_date.getText().toString().trim();
                activationDate = activationDate.replace("-"," ");
                groupPayload.setActivationDate(activationDate);
            }
            else
            {
                groupPayload.setActive(false);
            }
            if(staffId!=-1)
            {
                groupPayload.setStaffId(staffId);
            }
            if(centerId!=-1)
            {
                groupPayload.setCenterId(String.valueOf(centerId));
            }
            String submittedOn = et_group_submission_date.getText().toString().trim();
            submittedOn = submittedOn.replace("-"," ");
            groupPayload.setSubmittedOnDate(submittedOn);
            System.out.println("the payload is " + groupPayload);
            submitPaylodForGroupCreation(groupPayload);
        }
        else
        {
            Toast.makeText(getActivity(),"Please Select the Office ",Toast.LENGTH_LONG).show();
        }

    }

    public void submitPaylodForGroupCreation(GroupPayload groupPayload)
    {
        ((MifosApplication)getActivity().getApplicationContext()).api.groupService.createGroup(groupPayload, new Callback<GroupCreationResponseData>() {
            @Override
            public void success(GroupCreationResponseData groupCreationResponseData, Response response) {
                Toast.makeText(getActivity(),"Group with the name "+et_groupName.getText().toString().trim()+" has been Created successfully",Toast.LENGTH_LONG).show();
                getActivity().onBackPressed();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(),"Failure : "+API.userErrorMessage,Toast.LENGTH_LONG).show();
            }
        });
    }
    public void setColor(String value,TextView temp_text_view)
    {
        Spannable name=new SpannableString(value);
        name.setSpan(new ForegroundColorSpan(Color.BLACK), 0, value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        name.setSpan(new ForegroundColorSpan(Color.RED), value.length() - 1, value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        temp_text_view.setText(name);

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
        /*try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
