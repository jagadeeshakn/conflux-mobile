package com.mifos.mifosxdroid.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.CgtInformationAdapter;
import com.mifos.mifosxdroid.adapters.ClientAttendanceListAdapter;
import com.mifos.mifosxdroid.adapters.ClientNameListAdapter;
import com.mifos.mifosxdroid.uihelpers.NonScrollableListView;
import com.mifos.objects.CgtData;
import com.mifos.objects.ClientMember;
import com.mifos.objects.client.Client;
import com.mifos.utils.MifosApplication;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.OnClick;

/*
* cgt feature to list out the clients in the selected group and note the attendance of the clients.
* If cgt feature has been completed then display only the cgt information of previously completed cgt
* with the date of completion and number of clients absent for that cgt.
* */
public class ClientListCgtFragment extends Fragment{


 @InjectView(R.id.client_list_header)
    LinearLayout client_list_header;
    @InjectView(R.id.client_list)
    NonScrollableListView client_list;
    @InjectView(R.id.submit_cgt)
    Button submitCGT;
    @InjectView(R.id.day_count)
    TextView dayCount;
    @InjectView(R.id.planned_date)
    TextView plannedDate;
    @InjectView(R.id.cgt_attendance)
    LinearLayout cgt_attendance;
    @InjectView(R.id.layout_cgt_info)
    LinearLayout linearLayoutCgtInfo;
    @InjectView(R.id.cgt_info_list)
    NonScrollableListView cgtInfoList;
    @InjectView(R.id.tv_group_names)
    TextView tvGroupName;
    @InjectView(R.id.sc_cgt_client_list)
    ScrollView sc_cgt_clientList;
    @InjectView(R.id.sc_cgt_info)
    ScrollView sc_cgtInfo;
    @InjectView(R.id.et_comments)
    EditText etComments;
    @InjectView(R.id.cgt_info)
    NonScrollableListView cgtInfo;
    @InjectView(R.id.cgt_note)
            TextView cgtNote;
    boolean visibleFlag=false;
    View rootview;
    Context context;
    List<CgtData> cgtDatas;
    private List<CgtData> cgtDataList=new ArrayList<CgtData>();
    CgtData cgtData;
    private List<Integer> doneDate=new ArrayList<Integer>();
    private String groupName;
    private List<Client> clientList = new ArrayList<Client>();
    List<ClientMember> clientMembers = new ArrayList<ClientMember>();
    private Calendar calendar;
    private int year, month, day;
    private final  String TAG=getClass().getSimpleName();

    public static ClientListCgtFragment newInstance(List<Client> clientList,int centerId,int groupId,String groupName,List<CgtData> cgtDatas) {
        ClientListCgtFragment fragment = new ClientListCgtFragment();
        Bundle args = new Bundle();
        fragment.setClientList(clientList);
        fragment.setArguments(args);
        fragment.cgtDatas=cgtDatas;
        fragment.groupName=groupName;
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

    public ClientListCgtFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i(TAG,"CGT Fragment has been started");
        rootview= inflater.inflate(R.layout.fragment_client_list_cgt, container, false);
        ButterKnife.inject(this, rootview);
        ((ActionBarActivity)getActivity()).getSupportActionBar().setSubtitle(R.string.cgt);
        tvGroupName.setText(groupName);

        for(CgtData cgtData:cgtDatas)
        {
            if(cgtData.getDoneDate().isEmpty())
            {
                visibleFlag=true;
                this.cgtData=cgtData;
            }
        }
        if(visibleFlag)
        {
            Log.i(TAG,"The CGT has been not completed");
            sc_cgt_clientList.setVisibility(View.VISIBLE);
            inflateHeader(rootview);
            dayCount.setText("CGT - "+cgtData.getDay());
            String date = cgtData.getPlannedDate().get(2)+"/"+cgtData.getPlannedDate().get(1)+"/"+cgtData.getPlannedDate().get(0);
            plannedDate.setText(date);
            cgtNote.setText("Note :"+cgtData.getComments());
            inflateClientList(rootview);
            inflateCgtInfo(rootview, cgtInfoList);
        }
        else
        {
            Log.i(TAG,"ALL the CGT's has been completed");
            sc_cgtInfo.setVisibility(View.VISIBLE);
            cgtData=cgtDatas.get(cgtDatas.size()-1);
            dayCount.setText("CGT - "+cgtData.getDay());
            String date = cgtData.getPlannedDate().get(2)+"/"+cgtData.getPlannedDate().get(1)+"/"+cgtData.getPlannedDate().get(0);
            plannedDate.setText(date);
            inflateCgtInfo(rootview,cgtInfo);
        }
        return rootview;
    }

    /**
     * list out all the details of cgt's that has been done.
     * list the cgt days, cgt done date and absentese
     * */
    public void inflateCgtInfo(View rootview,NonScrollableListView listView)
    {

           for(CgtData cgtData:cgtDatas)
           {
               if(!cgtData.getDoneDate().isEmpty())
               {
                   cgtDataList.add(cgtData);
               }
           }


            CgtInformationAdapter cgtInformationAdapter= new CgtInformationAdapter(rootview.getContext(),cgtDataList);
            listView.setAdapter(cgtInformationAdapter);
    }

    @OnClick(R.id.submit_cgt)
    public void submitCGT()
    {
        Log.i(TAG,"Submit the CGT details of the group");
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        month=month+1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        doneDate.add(day);
        doneDate.add(month);
        doneDate.add(year);
        cgtData.setDoneDate(doneDate);
        cgtData.setClientMembers(clientMembers);
        System.out.println(cgtData);
    }

    public void inflateHeader(View rootview)
    {
        View child = getActivity().getLayoutInflater().inflate(R.layout.client_list_header, null);
        client_list_header.addView(child);

    }

    public void inflateClientList(View rootview)
    {
        final ClientAttendanceListAdapter clientNameListAdapter = new ClientAttendanceListAdapter(rootview.getContext(), clientList,((MifosApplication)getActivity().getApplication()).api,clientMembers);
        client_list.setAdapter(clientNameListAdapter);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        /*if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }*/
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
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


}
