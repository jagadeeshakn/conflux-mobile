package com.mifos.mifosxdroid.createnewclient.activity;

/**
 * Created by jagadeeshakn on 11/30/2015.
 */

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.createnewclient.data.clientdetails.ExpensesOption;
import com.mifos.mifosxdroid.createnewclient.data.clientdetails.IncomeTypeOption;
import com.mifos.mifosxdroid.createnewclient.data.clientdetails.Template;
import com.mifos.services.TestAPI;
import com.mifos.utils.SafeUIBlockingUtility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ClientIncomeDtailsActivity extends ActionBarActivity {

    @InjectView(R.id.bt_save)
    Button bt_save;
    @InjectView(R.id.sp_income_spinner)
    Spinner sp_income_spinner;
    @InjectView(R.id.sp_next_income_spinner)
    Spinner sp_next_income_spinner;
    @InjectView(R.id.sp_expenses_spinner)
    Spinner sp_expenses;
    @InjectView(R.id.sp_next_expenses_spinner)
    Spinner sp_next_expenses;
    @InjectView(R.id.et_income_amount)
    EditText et_total_income;
    @InjectView(R.id.et_next_income_amount)
    EditText et_next_total_income;
    @InjectView(R.id.et_expenses_amount)
    EditText et_total_expeneses;
    @InjectView(R.id.et_next_expenses_amount)
    EditText et_next_total_expenses;
    @InjectView(R.id.tv_income)
    TextView tv_income;
    @InjectView(R.id.tv_expenses)
    TextView tv_expenses;
    @InjectView(R.id.bt_income_row)
    ImageButton bt_income_row;
    @InjectView(R.id.bt_expenses_row)
    ImageButton bt_expenses_row;
    SafeUIBlockingUtility safeUIBlockingUtility;
    private HashMap<String, Integer> incomeMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> expensesMap = new HashMap<String, Integer>();
    public static List<String> incomeType = new ArrayList<String>();
    public static List<String> expensesType = new ArrayList<String>();
    List<String> secondExpenseList = new ArrayList<String>();
    List<String> secondIncomeList = new ArrayList<String>();
    List<String> addExpenseList = new ArrayList<String>();
    List<String> addIncomeList = new ArrayList<String>();
    private HashMap<String, Double> incomeValues = new HashMap<String, Double>();
    private HashMap<String, Double> expensesValues = new HashMap<String, Double>();
    public Double incomeTotal = 0.00;
    public Double expenseTotal = 0.00;
    TestAPI testApi = new TestAPI();

    public ClientIncomeDtailsActivity() {
        // Required empty public constructor

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_new_client_income_details);
        ButterKnife.inject(this);
        safeUIBlockingUtility = new SafeUIBlockingUtility(ClientIncomeDtailsActivity.this);
        inflateIncome();
        // Picasso.with(this).load(R.drawable.save).resize(150, 120).into(bt_save);
        Picasso.with(this).load(R.drawable.addclientdetails).resize(150, 120).into(bt_income_row);
        Picasso.with(this).load(R.drawable.addclientdetails).resize(150, 120).into(bt_expenses_row);
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_HOME_AS_UP | android.support.v7.app.ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher);
        getSupportActionBar().setTitle(R.string.dashboard);
        getSupportActionBar().setSubtitle(R.string.client_income_details);
    }

    public void inflateIncome() {

        safeUIBlockingUtility.safelyBlockUI();

        testApi.parentServices.getAllParent(new Callback<Template>() {
            @Override
            public void success(Template parents, final Response response) {
                expensesType.clear();
                expensesType.add(getString(R.string.spinner_expenses));
                expensesMap.put(getString(R.string.spinner_expenses), -1);
                for (ExpensesOption expenses : parents.getExpensesOptions()) {
                    expensesType.add(expenses.getName());
                    expensesMap.put(expenses.getName(), expenses.getId());
                }
                final ArrayAdapter<String> expensesAdapter = new ArrayAdapter<String>(getApplicationContext(),
                        R.layout.simple_spinner_item, expensesType);
                expensesAdapter.notifyDataSetChanged();
                expensesAdapter.setDropDownViewResource(R.layout.simple_spinner_item);
                sp_expenses.setAdapter(expensesAdapter);
                sp_expenses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        int expensesTypeId = expensesMap.get(expensesType.get(position));
                        final String expensesName = parent.getItemAtPosition(position).toString();
                        secondExpenseList.clear();
                        secondExpenseList.addAll(expensesType);
                        final ArrayAdapter<String> secondSpinnerAdapter = new ArrayAdapter<String>(getApplicationContext(),
                                R.layout.simple_spinner_item, secondExpenseList);
                        secondSpinnerAdapter.notifyDataSetChanged();
                        secondSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_item);
                        sp_next_expenses.setAdapter(secondSpinnerAdapter);
                        if (expensesTypeId != -1) {
                            secondExpenseList.remove(expensesName);
                            et_total_expeneses.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    try {
                                        Double expense_amount = Double.parseDouble(et_total_expeneses.getText().toString().trim());
                                        expensesValues.put(expensesName, s.toString().equals("") ? 0.00 : expense_amount);
                                        expenseTotal = 0.00;
                                        Collection<Double> expenselist = expensesValues.values();
                                        for (Double tempr : expenselist) {
                                            expenseTotal = expenseTotal + tempr;
                                        }
                                    } catch (NumberFormatException e) {
                                        expensesValues.put(expensesName, 0.00);
                                        et_total_expeneses.requestFocus();
                                    }

                                }
                            });

                        } else {

                            Toast.makeText(getApplicationContext(), getString(R.string.error_select_expenses), Toast.LENGTH_SHORT).show();
                            expensesValues.keySet().clear();
                            et_total_expeneses.setText("");
                            expenseTotal = 0.00;
                        }
                        et_total_expeneses.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if (!hasFocus) {

                                    tv_expenses.setText(String.valueOf(expenseTotal));

                                }

                            }
                        });

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                sp_next_expenses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        int expensesTypeId = expensesMap.get(expensesType.get(position));
                        final String expensesName = parent.getItemAtPosition(position).toString();
                        addExpenseList.clear();
                        addExpenseList.addAll(secondExpenseList);
                        if (expensesTypeId != -1) {
                            addExpenseList.remove(expensesName);
                            addExpenseRow();
                            et_next_total_expenses.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    try {
                                        Double next_expense_amount = Double.parseDouble(et_next_total_expenses.getText().toString().trim());
                                        expensesValues.put(expensesName, s.toString().equals("") ? 0.00 : next_expense_amount);
                                        expenseTotal = 0.00;
                                        Collection<Double> expenseList = expensesValues.values();
                                        for (Double tempr : expenseList) {
                                            expenseTotal = expenseTotal + tempr;
                                        }

                                    } catch (NumberFormatException e) {
                                        expensesValues.put(expensesName, 0.00);
                                        et_next_total_expenses.requestFocus();
                                    }
                                }
                            });

                        } else {

                            Toast.makeText(getApplicationContext(), getString(R.string.error_select_expenses), Toast.LENGTH_SHORT).show();
                            expensesValues.keySet().clear();
                            et_next_total_expenses.setText("");
                            expenseTotal = 0.00;
                        }
                        et_next_total_expenses.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if (!hasFocus) {
                                    tv_expenses.setText(String.valueOf(expenseTotal));

                                }

                            }
                        });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                incomeType.clear();
                incomeType.add(getString(R.string.spinner_income));
                incomeMap.put(getString(R.string.spinner_income), -1);
                for (IncomeTypeOption income : parents.getIncomeTypeOptions()) {
                    incomeType.add(income.getName());
                    incomeMap.put(income.getName(), income.getId());
                }
                final ArrayAdapter<String> incomeAdapter = new ArrayAdapter<String>(getApplicationContext(),
                        R.layout.simple_spinner_item, incomeType);
                incomeAdapter.notifyDataSetChanged();
                incomeAdapter.setDropDownViewResource(R.layout.simple_spinner_item);
                sp_income_spinner.setAdapter(incomeAdapter);
                sp_income_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                        int incomeTypeId = incomeMap.get(incomeType.get(position));
                        final String selectedName = parent.getItemAtPosition(position).toString();
                        secondIncomeList.clear();
                        secondIncomeList.addAll(incomeType);
                        final ArrayAdapter<String> secondSpinnerAdapter = new ArrayAdapter<String>(getApplicationContext(),
                                R.layout.simple_spinner_item, secondIncomeList);
                        secondSpinnerAdapter.notifyDataSetChanged();
                        secondSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_item);
                        sp_next_income_spinner.setAdapter(secondSpinnerAdapter);
                        if (incomeTypeId != -1) {
                            secondIncomeList.remove(selectedName);
                            et_total_income.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    try {
                                        Double income_amount = Double.parseDouble(et_total_income.getText().toString().trim());
                                        incomeValues.put(selectedName, s.toString().equals("") ? 0.00 : income_amount);
                                        incomeTotal = 0.00;
                                        Collection<Double> incomeList = incomeValues.values();
                                        for (Double temp : incomeList) {
                                            incomeTotal = incomeTotal + temp;
                                        }

                                    } catch (NumberFormatException e) {
                                        incomeValues.put(selectedName, 0.00);
                                        et_total_income.requestFocus();
                                    }
                                }
                            });
                            et_total_income.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View v, boolean hasFocus) {
                                    if (!hasFocus) {
                                        tv_income.setText(String.valueOf(incomeTotal));

                                    }

                                }
                            });

                        } else {

                            Toast.makeText(getApplicationContext(), getString(R.string.error_select_inocme), Toast.LENGTH_SHORT).show();
                            incomeValues.keySet().clear();
                            et_total_income.setText("");
                            incomeTotal = 0.00;

                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {


                    }
                });
                sp_next_income_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                        int incomeTypeId = incomeMap.get(incomeType.get(position));
                        final String selectedName = parent.getItemAtPosition(position).toString();
                        addIncomeList.clear();
                        addIncomeList.addAll(secondIncomeList);
                        if (incomeTypeId != -1) {
                            addIncomeList.remove(selectedName);
                            addIncomeRow();
                            et_next_total_income.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {


                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    try {
                                        Double next_income_amount = Double.parseDouble(et_next_total_income.getText().toString().trim());
                                        incomeValues.put(selectedName, s.toString().equals("") ? 0.00 : next_income_amount);
                                        incomeTotal = 0.00;
                                        Collection<Double> incomeList = incomeValues.values();
                                        for (Double temp : incomeList) {
                                            incomeTotal = incomeTotal + temp;

                                        }

                                    } catch (NumberFormatException e) {
                                        incomeValues.put(selectedName, 0.00);
                                        et_next_total_income.requestFocus();
                                    }
                                }
                            });
                            et_next_total_income.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View v, boolean hasFocus) {
                                    if (!hasFocus) {

                                        tv_income.setText(String.valueOf(incomeTotal));

                                    }

                                }
                            });


                        } else {

                            Toast.makeText(getApplicationContext(), getString(R.string.error_select_inocme), Toast.LENGTH_SHORT).show();
                            incomeValues.keySet().clear();
                            et_next_total_income.setText("");
                            incomeTotal = 0.00;
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

                safeUIBlockingUtility.safelyUnBlockUI();
            }
        });
    }


    public void addIncomeRow() {
        bt_income_row.setOnClickListener(new View.OnClickListener() {
            int count = 0;

            @Override
            public void onClick(View v) {
                count++;
                if (count >= 1) {
                    LinearLayout parentLayout = (LinearLayout) findViewById(R.id.ll_listview);
                    LayoutInflater layoutInflater = getLayoutInflater();
                    final View view = layoutInflater.inflate(R.layout.income_row_add, parentLayout, false);
                    Spinner spinnerIncome = (Spinner) view.findViewById(R.id.sp_income_spinners);
                    final EditText editTextIncome = (EditText) view.findViewById(R.id.et_income_amounts);
                    final ArrayAdapter<String> addSpinner = new ArrayAdapter<String>(getApplicationContext(),
                            R.layout.simple_spinner_item, addIncomeList);
                    addSpinner.notifyDataSetChanged();
                    addSpinner.setDropDownViewResource(R.layout.simple_spinner_item);
                    spinnerIncome.setAdapter(addSpinner);
                    parentLayout.addView(view);
                    editTextIncome.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(
                                    ClientIncomeDtailsActivity.this);
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
                    spinnerIncome.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                            int incomeTypeId = incomeMap.get(incomeType.get(position));
                            final String selectedName = parent.getItemAtPosition(position).toString();
                            if (incomeTypeId != -1) {
                                addIncomeRow();
                                editTextIncome.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {


                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {
                                        try {
                                            Double next_income_amount = Double.parseDouble(editTextIncome.getText().toString().trim());
                                            incomeValues.put(selectedName, s.toString().equals("") ? 0.00 : next_income_amount);
                                            incomeTotal = 0.00;
                                            Collection<Double> incomeList = incomeValues.values();
                                            for (Double temp : incomeList) {
                                                incomeTotal = incomeTotal + temp;

                                            }

                                        } catch (NumberFormatException e) {
                                            incomeValues.put(selectedName, 0.00);
                                            editTextIncome.requestFocus();
                                        }
                                    }
                                });
                                editTextIncome.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                    @Override
                                    public void onFocusChange(View v, boolean hasFocus) {
                                        if (!hasFocus) {

                                            tv_income.setText(String.valueOf(incomeTotal));

                                        }

                                    }
                                });


                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.error_select_inocme), Toast.LENGTH_SHORT).show();
                                incomeValues.keySet().clear();
                                editTextIncome.setText("");
                                incomeTotal = 0.00;
                            }

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                }


            }

        });

    }

    public void addExpenseRow() {
        bt_expenses_row.setOnClickListener(new View.OnClickListener() {
            int count = 0;

            @Override
            public void onClick(View v) {
                count++;
                if (count >= 1) {
                    final LinearLayout parentLayout = (LinearLayout) findViewById(R.id.ll_expenses_listview);
                    LayoutInflater layoutInflater = getLayoutInflater();
                    final View view = layoutInflater.inflate(R.layout.expenses_add_row, parentLayout, false);
                    final Spinner spinner = (Spinner) view.findViewById(R.id.sp_expenses_spinners);
                    final EditText editTextExpense = (EditText) view.findViewById(R.id.et_expenses_amounts);
                    final ArrayAdapter<String> addSpinner = new ArrayAdapter<String>(getApplicationContext(),
                            R.layout.simple_spinner_item, addExpenseList);
                    addSpinner.notifyDataSetChanged();
                    addSpinner.setDropDownViewResource(R.layout.simple_spinner_item);
                    spinner.setAdapter(addSpinner);
                    parentLayout.addView(view);
                    editTextExpense.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(
                                    ClientIncomeDtailsActivity.this);
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
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, final View view, int position, long id) {
                            int expensesTypeId = expensesMap.get(expensesType.get(position));
                            final String expensesName = parent.getItemAtPosition(position).toString();
                            if (expensesTypeId != -1) {
                                addExpenseRow();
                                editTextExpense.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {
                                        try {
                                            Double next_expense_amount = Double.parseDouble(editTextExpense.getText().toString().trim());
                                            expensesValues.put(expensesName, s.toString().equals("") ? 0.00 : next_expense_amount);
                                            expenseTotal = 0.00;
                                            Collection<Double> expenseList = expensesValues.values();
                                            for (Double tempr : expenseList) {
                                                expenseTotal = expenseTotal + tempr;


                                            }

                                        } catch (NumberFormatException e) {
                                            expensesValues.put(expensesName, 0.00);
                                            editTextExpense.requestFocus();
                                        }
                                    }
                                });

                            } else {

                                Toast.makeText(getApplicationContext(), getString(R.string.error_select_expenses), Toast.LENGTH_SHORT).show();
                                expensesValues.keySet().clear();
                                editTextExpense.setText("");
                                expenseTotal = 0.00;
                            }
                            editTextExpense.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View v, boolean hasFocus) {
                                    if (!hasFocus) {
                                        tv_expenses.setText(String.valueOf(expenseTotal));

                                    }

                                }
                            });

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                }

            }

        });

    }
}








