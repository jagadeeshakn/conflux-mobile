package com.mifos.mifosxdroid.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.ClientAttendanceListAdapter;
import com.mifos.mifosxdroid.adapters.ClientListAttendanceAdapter;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.mifosxdroid.uihelpers.NonScrollableListView;
import com.mifos.objects.ClientMember;
import com.mifos.objects.GrtData;
import com.mifos.objects.client.Client;
import com.mifos.objects.organisation.Staff;
import com.mifos.services.API;
import com.mifos.utils.Constants;
import com.mifos.utils.FragmentConstants;
import com.mifos.utils.MifosApplication;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GrtFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GrtFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GrtFragment extends Fragment implements MFDatePicker.OnDatePickListener {
    @InjectView(R.id.tv_group_name)
    TextView tvGroupName;
    @InjectView(R.id.staff1)
    Spinner staffOneSpinner;
    @InjectView(R.id.staff2)
    Spinner staffTwoSpinner;
    @InjectView(R.id.client_list)
    NonScrollableListView nonScrollClientList;
    @InjectView(R.id.grt_view)
    ScrollView grtView;
    @InjectView(R.id.reschedule_grt_layout)
    ScrollView rescheduleGrt;
    @InjectView(R.id.grt_pass_view)
    ScrollView grtPassView;
    @InjectView(R.id.edt_date)
    ImageButton edtDateButton;
    @InjectView(R.id.tv_grt_next_planned_date)
    TextView tvNextPannneddate;
    @InjectView(R.id.tv_planned_date)
    TextView tvPlannedDate;
    @InjectView(R.id.grt_status)
    TextView grtStatus;
    @InjectView(R.id.tv_group_pass_name)
    TextView tvPassGroupName;
    @InjectView(R.id.grt_pass_status)
    TextView grtPassStatus;
    @InjectView(R.id.tv_comments)
    TextView tvComments;
    @InjectView(R.id.linear_layout_fail)
    LinearLayout linearLayoutFail;
    @InjectView(R.id.tv_date_planned)
    TextView tvDatePlanned;
    @InjectView(R.id.tv_date_done)
    TextView tvDoneDate;
    View layerView;
    @InjectView(R.id.et_grt_comments)
    EditText et_rescheduleGrtComment;
    @InjectView(R.id.et_comments)
    EditText et_comments;
    @InjectView(R.id.grt_list_client)
    NonScrollableListView clientAttendanceList;

    private View rootView;
    private String doneDate;
    private String groupName;
    private int groupId;
    private int officeId;
    private int staffIdOne;
    private int staffIdTwo;
    private GrtData grtData;
    private List<Client> clientList = new ArrayList<Client>();
    private List<ClientMember> clientMembers = new ArrayList<ClientMember>();
    private Calendar calendar;
    private int year, month, day;
    private OnFragmentInteractionListener mListener;
    private final String TAG = getClass().getSimpleName();
    private MFDatePicker mfDatePicker;
    int datePickerInput;
    private HashMap<String, Integer> staffOneNameIdHashMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> staffTwoNameIdHashMap = new HashMap<String, Integer>();
    private String status = null;


    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    // TODO: Rename and change types and number of parameters
    public static GrtFragment newInstance(List<Client> clientList,int centerId,int groupId,int officeId,GrtData grtData,String groupName) {
        GrtFragment fragment = new GrtFragment();
        Log.i(fragment.TAG, "GRT Fragment has been initialized");
        Bundle args = new Bundle();
        fragment.setClientList(clientList);
        fragment.setArguments(args);
        fragment.setGroupName(groupName);
        fragment.setOfficeId(officeId);
        fragment.setGrtData(grtData);

        for (Client client:clientList)
        {
            ClientMember clientMember=new ClientMember();
            clientMember.setId(client.getId());
            clientMember.setAttendance("P");
            fragment.clientMembers.add(clientMember);
        }
        return fragment;
    }



    public void setClientList(List<Client> clientList) {
        this.clientList = clientList;
    }

    public GrtFragment() {
        // Required empty public constructor
    }


    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public void setOfficeId(int officeId) {
        this.officeId = officeId;
    }

    public void setGrtData(GrtData grtData) {
        this.grtData = grtData;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_grt, container, false);
        ButterKnife.inject(this, rootView);
        ((ActionBarActivity)getActivity()).getSupportActionBar().setSubtitle(R.string.grt);
        Log.i(TAG, "GRT Fragment has been Started");
        tvGroupName.setText(groupName);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        setStatus(rootView);
        if(status==Constants.PASS)
        {
            grtView.setVisibility(View.INVISIBLE);
            grtPassView.setVisibility(View.VISIBLE);
            inflateViews(Color.rgb(104, 181, 35));
            inflateClientListAttendance();

        }else if(status==Constants.FAIL)
        {
            grtView.setVisibility(View.INVISIBLE);
            grtPassView.setVisibility(View.VISIBLE);
            inflateViews(Color.RED);
            linearLayoutFail.setVisibility(View.VISIBLE);
            inflateClientListAttendance();
        }
        else {
            inflateGrtStatus();
            innflatePlannedDate();
            inflateStaffSpinner(officeId);
            inflateClientList(rootView);
        }
        return rootView;
    }

    public void inflateClientListAttendance()
    {
        final ClientListAttendanceAdapter clientNameListAdapter = new ClientListAttendanceAdapter(rootView.getContext(), clientList,((MifosApplication)getActivity().getApplication()).api,grtData);
        clientAttendanceList.setAdapter(clientNameListAdapter);
    }


    public void inflateViews(int color)
    {
        tvPassGroupName.setText(groupName);
        grtPassStatus.setTextColor(color);
        grtPassStatus.setText(status);
        String plannedDate = grtData.getPlannedDate();
        tvDatePlanned.setText(plannedDate);
        if(!grtData.getDoneDate().isEmpty()) {
            doneDate = grtData.getDoneDate();
            tvDoneDate.setText(doneDate);
        }
        tvComments.setText(grtData.getComments().toString());
    }
    public void inflateGrtStatus()
    {
        grtStatus.setText(status);
    }

    public void innflatePlannedDate()
    {
        String date =grtData.getPlannedDate();
        tvPlannedDate.setText("Planned Date : "+date);
    }

    public void setStatus(View rootView)
    {
        Log.d(TAG, "Get the Grt Status");
        String plannedDate = grtData.getPlannedDate();
        //today's date
        String actualDate = day+"/"+month+"/"+year;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try
        {
            Date plDate = simpleDateFormat.parse(plannedDate);
            doneDate = grtData.getDoneDate();
            System.out.println("planned date " + plDate);
            Date actDate;
            if(grtData.getGrtStatus().equals(Constants.PASS))
            {

                status = Constants.PASS;
            }
            else
            if(doneDate.equals(null)||doneDate.equals(""))
            {
                status = Constants.PLANNED;
            }
            else
            {
                actDate=simpleDateFormat.parse(doneDate);
                if(plDate.compareTo(actDate)>0 && grtData.getGrtStatus().equals(""))
                {
                    status = Constants.PLANNED;
                }
                else
                    if(plDate.compareTo(actDate)>0 && grtData.getGrtStatus().equals(Constants.FAIL))
                    {
                        status = Constants.PLANNED;
                    }
                    else if(plDate.compareTo(actDate)<=0 && grtData.getGrtStatus().equals(Constants.FAIL))
                    {
                        status = Constants.FAIL;
                    }
            }

        }
        catch (ParseException e)
        {
            Log.e(TAG,"error in date parsing");
            Toast.makeText(getActivity(),"Problem in getting GRT details.",Toast.LENGTH_LONG).show();
        }
    }


    public void inflateClientList(View rootview)
    {
        final ClientAttendanceListAdapter clientNameListAdapter = new ClientAttendanceListAdapter(rootview.getContext(), clientList,((MifosApplication)getActivity().getApplication()).api,clientMembers);
        nonScrollClientList.setAdapter(clientNameListAdapter);
    }

    @OnClick(R.id.edt_date)
    public void changePlannedDate()
    {
        Log.i(TAG,"Change the Activation Date");
        mfDatePicker = MFDatePicker.newInsance(this);
        mfDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants.DFRAG_DATE_PICKER);
    }
    @OnClick(R.id.tv_grt_next_planned_date)
    public void changePlannedDateText()
    {
        changePlannedDate();
    }
    public void inflateDate() {
        tvNextPannneddate.setText(new StringBuilder().append(day).append("-")
                .append(month + 1).append("-").append(year));

    }

    @Override
    public void onDatePicked(String date) {
        tvNextPannneddate.setText(date);
    }



    @OnClick(R.id.reschedule_grt_btn_for_failgrt)
    public void grtFailedRescheduleGrt(View view)
    {
        layerView=grtPassView;
        grtPassView.setVisibility(View.INVISIBLE);
        viewRescheduleLayout();
    }

    @OnClick(R.id.grt_pass_btn)
    public void grtPassBtn(View view)
    {
        if(staffIdOne!=staffIdTwo&& staffIdOne!=-1 && staffIdTwo!=-1) {
            grtData.setComments(et_comments.getText().toString().trim());
            System.out.println("the client members are " + clientMembers);
            grtData.setClientMembers(clientMembers);
            grtData.setGrtStatus(Constants.PASS);
            System.out.println("The GRT data to be sent is " + grtData);
            getActivity().onBackPressed();
        }
        else
        {
            if(staffIdOne==-1 && staffIdTwo==-1)
            {
                Toast.makeText(getActivity(),getString(R.string.err_staff_names_empty),Toast.LENGTH_LONG).show();
            }
            else
            if(staffIdOne==-1)
            {
                Toast.makeText(getActivity(),getString(R.string.err_staff_name_one),Toast.LENGTH_LONG).show();
            }
            else
            if(staffIdTwo==-1)
            {
                Toast.makeText(getActivity(),getString(R.string.err_staff_name_two),Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(getActivity(), getString(R.string.err_staff_names_same), Toast.LENGTH_LONG).show();
            }
        }
    }
    @OnClick(R.id.grt_fail_btn)
    public void grtFailBtn(View view)
    {
        if(staffIdOne!=staffIdTwo&& staffIdOne!=-1 &&staffIdTwo!=-1) {
            grtData.setComments(et_comments.getText().toString().trim());
            System.out.println("the client members are " + clientMembers);
            grtData.setClientMembers(clientMembers);
            grtData.setGrtStatus(Constants.FAIL);
            System.out.println("The GRT data to be sent is " + grtData);
            getActivity().onBackPressed();
        }
        else
        {
            if(staffIdOne==-1 && staffIdTwo==-1)
            {
                Toast.makeText(getActivity(),getString(R.string.err_staff_names_empty),Toast.LENGTH_LONG).show();
            }
            else
            if(staffIdOne==-1)
            {
                Toast.makeText(getActivity(),getString(R.string.err_staff_name_one),Toast.LENGTH_LONG).show();
            }
            else
            if(staffIdTwo==-1)
            {
                Toast.makeText(getActivity(),getString(R.string.err_staff_name_two),Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(getActivity(), getString(R.string.err_staff_names_same), Toast.LENGTH_LONG).show();
            }
        }
    }

    @OnClick(R.id.submit)
    public void confirmRescheduleGrt(View view)
    {
        grtData.setComments(et_rescheduleGrtComment.getText().toString().trim());
        Toast.makeText(getActivity(),getString(R.string.reschedule_grt_success),Toast.LENGTH_LONG).show();
        getActivity().onBackPressed();

    }

    @OnClick(R.id.reschedule_grt_btn)
    public void rescheduleGrt(View view)
    {
        layerView = grtView;
        grtView.setVisibility(View.INVISIBLE);
        viewRescheduleLayout();
    }

    public void viewRescheduleLayout()
    {
        rescheduleGrt.setVisibility(View.VISIBLE);
        Picasso.with(getActivity()).load(R.drawable.edit).resize((int) tvNextPannneddate.getTextSize() + 30, (int) tvNextPannneddate.getTextSize() + 30).into(edtDateButton);
        inflateDate();
    }

    @OnClick(R.id.cancel)
    public void cancel(View view)
    {
        rescheduleGrt.setVisibility(View.INVISIBLE);
        ScrollView scrollView = (ScrollView)layerView;
        scrollView.setVisibility(View.VISIBLE);
    }

    public void inflateStaffSpinner(final int officeId) {
        ((MifosApplication) getActivity().getApplicationContext()).api.staffService.getStaffForOffice(officeId, new Callback<List<Staff>>() {
            public void success(List<Staff> staffs, Response response) {
                final List<String> staffOneNames = new ArrayList<String>();
                final List<String> staffTwoNames = new ArrayList<String>();
                staffOneNames.add(getString(R.string.spinner_staff_1));
                staffOneNameIdHashMap.put(getString(R.string.spinner_staff_1), -1);
                for (Staff staff : staffs) {
                    staffOneNames.add(staff.getDisplayName());
                    staffOneNameIdHashMap.put(staff.getDisplayName(), staff.getId());
                }
                ArrayAdapter<String> staffOneAdapter = new ArrayAdapter<String>(getActivity(),
                        R.layout.spinner_item, staffOneNames);
                staffOneAdapter.notifyDataSetChanged();
                staffOneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                staffOneSpinner.setAdapter(staffOneAdapter);
                staffOneSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        staffIdOne = staffOneNameIdHashMap.get(staffOneNames.get(position));
                        System.out.println(" staff Id 1" + staffIdOne);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


                staffTwoNames.add(getString(R.string.spinner_staff_2));
                staffTwoNameIdHashMap.put(getString(R.string.spinner_staff_2), -1);

                for (Staff staff : staffs) {
                    staffTwoNames.add(staff.getDisplayName());
                    staffTwoNameIdHashMap.put(staff.getDisplayName(), staff.getId());
                }
                ArrayAdapter<String> staffTwoAdapter = new ArrayAdapter<String>(getActivity(),
                        R.layout.spinner_item, staffTwoNames);
                staffTwoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                staffTwoSpinner.setAdapter(staffTwoAdapter);
                staffTwoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        staffIdTwo = staffTwoNameIdHashMap.get(staffTwoNames.get(position));
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
