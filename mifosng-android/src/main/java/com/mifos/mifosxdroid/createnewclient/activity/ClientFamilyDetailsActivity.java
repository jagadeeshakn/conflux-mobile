package com.mifos.mifosxdroid.createnewclient.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.createnewclient.data.clientdetails.DependentsOption;
import com.mifos.mifosxdroid.createnewclient.data.clientdetails.ParentOptions;
import com.mifos.mifosxdroid.createnewclient.data.clientdetails.Template;
import com.mifos.services.TestAPI;
import com.mifos.utils.SafeUIBlockingUtility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by jagadeeshakn on 11/26/2015.
 */
public class ClientFamilyDetailsActivity extends ActionBarActivity {
    @InjectView(R.id.bt_next)
    Button bt_next;
    @InjectView(R.id.bt_save)
    Button bt_save;
    @InjectView(R.id.sp_parent)
    Spinner sp_parent;
    @InjectView(R.id.sp_dependent)
    Spinner sp_dependent;
    @InjectView(R.id.bt_add_parent)
    ImageButton bt_add_parent;
    @InjectView(R.id.bt_add_dependent)
    ImageButton bt_add_dependent;
    SafeUIBlockingUtility safeUIBlockingUtility;
    private HashMap<String, Integer> parentmap = new HashMap<String, Integer>();
    private HashMap<String, Integer> depndentmap = new HashMap<String, Integer>();
    public static List<String> parentNames = new ArrayList<String>();
    public static List<String> dependentNames = new ArrayList<String>();
    List<String> addDependentList = new ArrayList<String>();
    List<String> addParentList = new ArrayList<String>();
    TestAPI testApi = new TestAPI();

    public ClientFamilyDetailsActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_new_client_family_details);
        ButterKnife.inject(this);
        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientbankDetails();
            }
        });
        safeUIBlockingUtility = new SafeUIBlockingUtility(ClientFamilyDetailsActivity.this);
        inflatetheparent();
        Picasso.with(this).load(R.drawable.addclientdetails).resize(150, 120).into(bt_add_parent);
        Picasso.with(this).load(R.drawable.addclientdetails).resize(150, 120).into(bt_add_dependent);
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_HOME_AS_UP | android.support.v7.app.ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher);
        getSupportActionBar().setTitle(R.string.dashboard);
        getSupportActionBar().setSubtitle(R.string.client_family_details);
    }

    public void inflatetheparent() {

        safeUIBlockingUtility.safelyBlockUI();

        testApi.parentServices.getAllParent(new Callback<Template>() {
            @Override
            public void success(Template parents, Response response) {
                parentNames.clear();
                parentNames.add(getString(R.string.spinner_parent));
                parentmap.put(getString(R.string.spinner_parent), -1);
                for (ParentOptions parent1 : parents.getParentOptions()) {
                    parentNames.add(parent1.getName());
                    parentmap.put(parent1.getName(), parent1.getId());
                }

                ArrayAdapter<String> parentOptionsAdapter = new ArrayAdapter<String>(getApplicationContext(),
                        R.layout.simple_spinner_item, parentNames);

                parentOptionsAdapter.notifyDataSetChanged();

                parentOptionsAdapter.setDropDownViewResource(R.layout.simple_spinner_item);
                sp_parent.setAdapter(parentOptionsAdapter);

                sp_parent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedParentName = parent.getItemAtPosition(position).toString();
                        int parentId = parentmap.get(parentNames.get(position));
                        addParentList.clear();
                        addParentList.addAll(parentNames);
                        if (parentId != -1) {
                            addParentList.remove(selectedParentName);
                            addParentView();
                        } else {

                            Toast.makeText(getApplicationContext(), getString(R.string.error_select_parent), Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                dependentNames.clear();
                dependentNames.add(getString(R.string.spinner_dependent));
                depndentmap.put(getString(R.string.spinner_dependent), -1);
                for (DependentsOption dependent : parents.getDependentsOptions()) {
                    dependentNames.add(dependent.getName());
                    depndentmap.put(dependent.getName(), dependent.getId());
                }

                ArrayAdapter<String> dependentAdapter = new ArrayAdapter<String>(getApplicationContext(),
                        R.layout.simple_spinner_item, dependentNames);

                dependentAdapter.notifyDataSetChanged();

                dependentAdapter.setDropDownViewResource(R.layout.simple_spinner_item);
                sp_dependent.setAdapter(dependentAdapter);

                sp_dependent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedDependentName = parent.getItemAtPosition(position).toString();
                        addDependentList.clear();
                        addDependentList.addAll(dependentNames);
                        int dependentId = depndentmap.get(dependentNames.get(position));
                        if (dependentId != -1) {
                            addDependentList.remove(selectedDependentName);
                            addDependentView();
                        } else {

                            Toast.makeText(getApplicationContext(), getString(R.string.error_select_dependent), Toast.LENGTH_SHORT).show();

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

    public void clientbankDetails() {
        Handler handler = new Handler(Looper.myLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), ClientBankDetailsActivity.class);
                startActivity(intent);
            }
        });
    }

    public void addParentView() {
        bt_add_parent.setOnClickListener(new View.OnClickListener() {
            int count = 0;

            @Override
            public void onClick(View v) {
                count++;
                if (count >= 1) {
                    final LinearLayout parentLayout = (LinearLayout) findViewById(R.id.ll_parent_addview);
                    LayoutInflater layoutInflater = getLayoutInflater();
                    final View view = layoutInflater.inflate(R.layout.client_family_parent_add, parentLayout, false);
                    final Spinner spinner = (Spinner) view.findViewById(R.id.sp_add_parent);
                    final EditText et_first_name = (EditText) view.findViewById(R.id.et_add_parent_first_name);
                    final EditText et_last_name = (EditText) view.findViewById(R.id.et_add_parent_last_name);
                    final ArrayAdapter<String> addSpinner = new ArrayAdapter<String>(getApplicationContext(),
                            R.layout.simple_spinner_item, addParentList);
                    addSpinner.setDropDownViewResource(R.layout.simple_spinner_item);
                    spinner.setAdapter(addSpinner);
                    parentLayout.addView(view);
                    et_first_name.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(
                                    ClientFamilyDetailsActivity.this);
                            alert.setTitle("Delete");
                            alert.setMessage("Do you want delete this item?");
                            alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TOD O Auto-generated method stub

                                    // main code on after clicking yes
                                    ((ViewGroup) view.getParent()).removeView(view);
                                    //view.remove(deletePosition);
                                }
                            });
                            alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    dialog.dismiss();
                                }
                            });

                            alert.show();
                            return true;
                        }
                    });


                }

            }
        });
    }

    public void addDependentView() {
        bt_add_dependent.setOnClickListener(new View.OnClickListener() {
            int count = 0;

            @Override
            public void onClick(View v) {
                count++;
                if (count >= 1) {
                    final LinearLayout parentLayout = (LinearLayout) findViewById(R.id.ll_add_dependent);
                    LayoutInflater layoutInflater = getLayoutInflater();
                    final View view = layoutInflater.inflate(R.layout.client_family_add_dependent, parentLayout, false);
                    final Spinner spinner = (Spinner) view.findViewById(R.id.sp_add_dependent);
                    final EditText et_first_name = (EditText) view.findViewById(R.id.et_add_dependent_first_name);
                    final EditText et_last_name = (EditText) view.findViewById(R.id.et_add_dependent_last_name);
                    final ArrayAdapter<String> addSpinner = new ArrayAdapter<String>(getApplicationContext(),
                            R.layout.simple_spinner_item, addDependentList);
                    addSpinner.setDropDownViewResource(R.layout.simple_spinner_item);
                    spinner.setAdapter(addSpinner);
                    parentLayout.addView(view);
                    et_first_name.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(
                                    ClientFamilyDetailsActivity.this);
                            alert.setTitle("Delete");
                            alert.setMessage("Do you want delete this item?");
                            alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TOD O Auto-generated method stub

                                    // main code on after clicking yes
                                    ((ViewGroup) view.getParent()).removeView(view);
                                    //view.remove(deletePosition);
                                }
                            });
                            alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    dialog.dismiss();
                                }
                            });

                            alert.show();
                            return true;
                        }
                    });
                }

            }
        });
    }
}
