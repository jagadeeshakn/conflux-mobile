/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.collectionsheet.fragment;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.collectionsheet.activity.CollectionSheet;
import com.mifos.mifosxdroid.fragments.MifoCenterListFragment;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;
import com.mifos.objects.group.Group;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.Staff;
import com.mifos.utils.Constants;
import com.mifos.utils.DateHelper;
import com.mifos.utils.MifosApplication;
import com.mifos.utils.SafeUIBlockingUtility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.mifos.utils.FragmentConstants.DFRAG_DATE_PICKER;

public class GenerateCollectionSheetFragment extends Fragment implements MFDatePicker.OnDatePickListener {

    public static final String LIMIT = "limit";
    public static final String ORDER_BY = "orderBy";
    public static final String SORT_ORDER = "sortOrder";
    public static final String ASCENDING = "ASC";
    public static final String ORDER_BY_FIELD_NAME = "name";
    public static final String STAFF_ID = "staffId";
    private android.support.v4.app.DialogFragment mfDatePicker;
    String selectedDate;
    @InjectView(R.id.label_branch_office)
    TextView tv_office;
    @InjectView(R.id.label_meeting_date)
    TextView tv_meeting_date;
    @InjectView(R.id.label_group)
    TextView tv_group_name;
    @InjectView(R.id.label_center_name)
    TextView tv_center_name;
    @InjectView(R.id.label_staff)
    TextView tv_staff_name;
    @InjectView(R.id.sp_branch_offices)
    Spinner sp_offices;
    @InjectView(R.id.sp_loan_officers)
    Spinner sp_loan_officers;
    @InjectView(R.id.sp_centers)
    Spinner sp_centers;
    @InjectView(R.id.sp_groups)
    Spinner sp_groups;
    @InjectView(R.id.bt_generate_collection_sheet)
    Button bt_generate_collection_sheet;
    //@InjectView(R.id.date_picker)
    // DatePicker date_Picker;
    @InjectView(R.id.et_meeting_date)
    TextView textView_meeting_date;
    @InjectView(R.id.edit_meeting_date)
    ImageButton et_meeting_date;

    View rootView;
    SafeUIBlockingUtility safeUIBlockingUtility;
    String dateString;
    ActionBarActivity activity;
    SharedPreferences sharedPreferences;
    ActionBar actionBar;
    private HashMap<String, Integer> officeNameIdHashMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> staffNameIdHashMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> centerNameIdHashMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> groupNameIdHashMap = new HashMap<String, Integer>();
    private int officeId;
    private int staffId;
    private int centerId;
    private int calendarId;

    public GenerateCollectionSheetFragment() {
        // Required empty public constructor
    }

    public static GenerateCollectionSheetFragment newInstance() {

        GenerateCollectionSheetFragment generateCollectionSheetFragment = new GenerateCollectionSheetFragment();

        return generateCollectionSheetFragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_generate_collection_sheet, container, false);
        activity = (ActionBarActivity) getActivity();
        safeUIBlockingUtility = new SafeUIBlockingUtility(GenerateCollectionSheetFragment.this.getActivity());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        actionBar = activity.getSupportActionBar();

        ButterKnife.inject(this, rootView);
        Picasso.with(getActivity()).load(R.drawable.edit).resize((int) textView_meeting_date.getTextSize() + 30, (int) textView_meeting_date.getTextSize() + 30).into(et_meeting_date);
        //Picasso.with(getActivity()).load(R.drawable.edit).resize(180, 100).into(et_meeting_date);
        setColor(getResources().getString(R.string.center), tv_center_name);
        setColor(getResources().getString(R.string.meeting_date), tv_meeting_date);
        setColor(getResources().getString(R.string.office_name), tv_office);

        inflateOfficeSpinner();
        inflateMeetingDate();
        bt_generate_collection_sheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GenerateCollectionSheet();
            }
        });
        dateString = textView_meeting_date.getText().toString();
        dateString = DateHelper.getDateAsStringUsedForCollectionSheetPayload(dateString).replace("-", " ");
        return rootView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.findItem(R.id.mItem_search).setIcon(
                new IconDrawable(getActivity(), Iconify.IconValue.fa_search)
                        .colorRes(R.color.black)
                        .actionBarSize());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.mItem_search) {

            getActivity().finish();
        }


        return super.onOptionsItemSelected(item);
    }

    public void inflateMeetingDate() {
        mfDatePicker = MFDatePicker.newInsance(this);

        textView_meeting_date.setText(MFDatePicker.getDatePickedAsString());

        et_meeting_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mfDatePicker.show(getActivity().getSupportFragmentManager(), DFRAG_DATE_PICKER);
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


    public void inflateOfficeSpinner() {

        safeUIBlockingUtility.safelyBlockUI();

        ((MifosApplication) getActivity().getApplicationContext()).api.officeService.getAllOffices(new Callback<List<Office>>() {
            @Override
            public void success(List<Office> offices, Response response) {

                final List<String> officeNames = new ArrayList<String>();
                officeNames.add(getString(R.string.spinner_office));
                officeNameIdHashMap.put(getString(R.string.spinner_office), -1);
                for (Office office : offices) {
                    officeNames.add(office.getName());
                    officeNameIdHashMap.put(office.getName(), office.getId());
                }

                ArrayAdapter<String> officeAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, officeNames);

                officeAdapter.notifyDataSetChanged();

                officeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_offices.setAdapter(officeAdapter);

                sp_offices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        officeId = officeNameIdHashMap.get(officeNames.get(position));

                        if (officeId != -1) {

                            inflateStaffSpinner(officeId);
                            inflateCenterSpinner(officeId, -1);
                            inflateGroupSpinner(officeId, -1);

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
            public void failure(RetrofitError retrofitError) {

                System.out.println(retrofitError.getLocalizedMessage());

                safeUIBlockingUtility.safelyUnBlockUI();
            }
        });

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
                        android.R.layout.simple_spinner_item, staffNames);

                staffAdapter.notifyDataSetChanged();

                staffAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_loan_officers.setAdapter(staffAdapter);

                sp_loan_officers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        staffId = staffNameIdHashMap.get(staffNames.get(position));

                        if (staffId != -1) {

                            inflateCenterSpinner(officeId, staffId);
                            inflateGroupSpinner(officeId, staffId);

                        } else {

                            Toast.makeText(getActivity(), getString(R.string.error_select_staff), Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


            }

            @Override
            public void failure(RetrofitError retrofitError) {

                System.out.println(retrofitError.getLocalizedMessage());


            }
        });


    }

    public void inflateCenterSpinner(final int officeId, int staffId) {

        Map<String, Object> params = new HashMap<String, Object>();

        params.put(LIMIT, -1);
        params.put(ORDER_BY, ORDER_BY_FIELD_NAME);
        params.put(SORT_ORDER, ASCENDING);
        if (staffId >= 0) {
            params.put(STAFF_ID, staffId);
        }

        ((MifosApplication) getActivity().getApplicationContext()).api.centerService.getAllCentersInOffice(officeId, params, new Callback<List<Center>>() {
            @Override
            public void success(List<Center> centers, Response response) {

                final List<String> centerNames = new ArrayList<String>();

                centerNames.add(getString(R.string.spinner_center));
                centerNameIdHashMap.put(getString(R.string.spinner_center), -1);

                for (Center center : centers) {
                    centerNames.add(center.getName());
                    centerNameIdHashMap.put(center.getName(), center.getId());
                }


                ArrayAdapter<String> centerAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, centerNames);

                centerAdapter.notifyDataSetChanged();

                centerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_centers.setAdapter(centerAdapter);

                sp_centers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        centerId = centerNameIdHashMap.get(centerNames.get(position));

                        if (centerId != -1) {

                            inflateGroupSpinner(centerId);
                            getCalenderId(centerId);

                        } else {

                            Toast.makeText(getActivity(), getString(R.string.error_select_center), Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }

            @Override
            public void failure(RetrofitError retrofitError) {

                System.out.println(retrofitError.getLocalizedMessage());

            }
        });


    }

    public void inflateGroupSpinner(final int officeId, int staffId) {

        Map<String, Object> params = new HashMap<String, Object>();

        params.put(LIMIT, -1);
        params.put(ORDER_BY, ORDER_BY_FIELD_NAME);
        params.put(SORT_ORDER, ASCENDING);
        if (staffId >= 0) {
            params.put(STAFF_ID, staffId);
        }


        ((MifosApplication) getActivity().getApplicationContext()).api.groupService.getAllGroupsInOffice(officeId, params, new Callback<List<Group>>() {
            @Override
            public void success(List<Group> groups, Response response) {

                List<String> groupNames = new ArrayList<String>();

                groupNames.add(getString(R.string.spinner_group));
                groupNameIdHashMap.put(getString(R.string.spinner_group), -1);

                for (Group group : groups) {
                    groupNames.add(group.getName());
                    groupNameIdHashMap.put(group.getName(), group.getId());
                }


                ArrayAdapter<String> groupAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, groupNames);

                groupAdapter.notifyDataSetChanged();

                groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_groups.setAdapter(groupAdapter);

            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });


    }

    public void inflateGroupSpinner(final int centerId) {

        ((MifosApplication) getActivity().getApplicationContext()).api.centerService.getAllGroupsForCenter(centerId, new Callback<CenterWithAssociations>() {
            @Override
            public void success(CenterWithAssociations centerWithAssociations, Response response) {

                List<Group> groups = centerWithAssociations.getGroupMembers();

                List<String> groupNames = new ArrayList<String>();

                groupNames.add(getString(R.string.spinner_group));
                groupNameIdHashMap.put(getString(R.string.spinner_group), -1);

                for (Group group : groups) {
                    groupNames.add(group.getName());
                    groupNameIdHashMap.put(group.getName(), group.getId());
                }


                ArrayAdapter<String> groupAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, groupNames);

                groupAdapter.notifyDataSetChanged();

                groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_groups.setAdapter(groupAdapter);

            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });

    }
    public void getCalenderId(int centerId){
        ((MifosApplication)getActivity().getApplication()).api.centerService.getCenterWithGroupMembersAndCollectionMeetingCalendar(this.centerId, new Callback<CenterWithAssociations>() {
            @Override
            public void success(final CenterWithAssociations centerWithAssociations, Response response) {
                safeUIBlockingUtility.safelyUnBlockUI();
                calendarId = centerWithAssociations.getCollectionMeetingCalendar().getId();

            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

    }

    public void GenerateCollectionSheet () {


        SharedPreferences preferences = getActivity().getSharedPreferences(MifoCenterListFragment.PREF_COLLECTION_DETAILS, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit= preferences.edit();
        edit.putInt(Constants.CENTER_ID,centerId);
        edit.putString(Constants.DATE_OF_COLLECTION, selectedDate);
        edit.putInt(Constants.CALENDAR_INSTANCE_ID,calendarId);
        edit.commit();
        Intent intent = new Intent(getActivity(), CollectionSheet.class);
        startActivity(intent);
    }

    @Override
    public void onDatePicked(String date) {
        textView_meeting_date.setText(date);
        selectedDate = date;
    }
}
