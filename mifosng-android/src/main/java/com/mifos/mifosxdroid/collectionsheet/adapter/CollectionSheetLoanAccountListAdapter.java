/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.collectionsheet.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.collectionsheet.fragment.CollectionSheetFragment;
import com.mifos.objects.db.Loan;

import java.util.ArrayList;
import butterknife.ButterKnife;

/**
 * Created by ishankhanna on 21/07/14.
 */
public class CollectionSheetLoanAccountListAdapter extends BaseAdapter {
    private static final String TAG = "LoanAccountListAdapter";
    LayoutInflater layoutInflater;
    public ArrayList<Loan> loans = new ArrayList<Loan>();
    int groupPosition;
    int childPosition;
    boolean textFocus=false;
    public CollectionSheetLoanAccountListAdapter(Context context, ArrayList<Loan>loans,int groupPosition,int childPosition) {
        super();
        layoutInflater = LayoutInflater.from(context);
        this.loans = loans;
        this.groupPosition = groupPosition;
        this.childPosition = childPosition;
    }

    @Override
    public int getCount() {
        return loans.size();
    }

    @Override
    public Loan getItem(int position) {
        return loans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView( int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView== null) {
            convertView = layoutInflater.inflate(R.layout.row_collection_sheet_loan,parent,false);
            holder = new ViewHolder(convertView);
            holder.productsName=(TextView)convertView.findViewById(R.id.tv_loan_shortname);
            holder.totalAmount = (TextView)convertView.findViewById(R.id.tv_amountDue);
            holder.totalRepay = (EditText) convertView.findViewById(R.id.et_amountPaid);
            holder.mWatcher = new MutableWatcher();
            holder.totalRepay.addTextChangedListener(holder.mWatcher);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Loan loan = loans.get(position);
        final Double transactionAmount = CollectionListAdapter.sRepaymentTransactions.get(loan.getLoanId());
        holder.totalAmount.setText(String.valueOf(loan.getTotalDue()));
        holder.productsName.setText(String.valueOf(loan.getProductShortName()));
        holder.totalRepay.setText(String.valueOf(transactionAmount));
        holder.mWatcher.setActive(false);
        holder.mWatcher.setPosition(position);
       holder.mWatcher.setActive(true);

        holder.totalRepay.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                textFocus = false;
            }
        });

        return convertView;
    }

    public static class ViewHolder {
        TextView productsName;
        TextView totalAmount;
        EditText totalRepay;
        MutableWatcher mWatcher;
        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
    class MutableWatcher implements TextWatcher {

        private int mPosition;
        private boolean mActive;
        void setPosition(int position) {
            mPosition = position;
        }

        void setActive(boolean active) {
            mActive = active;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mActive) {
                try {
                    CollectionListAdapter.sRepaymentTransactions.put(loans.get(mPosition).getLoanId(), s.toString().equals("") ? 0.00 : Double.parseDouble(s.toString()));
                }catch(NumberFormatException e) {

                    CollectionListAdapter.sRepaymentTransactions.put(loans.get(mPosition).getLoanId(), 0.00);
                    CollectionSheetFragment.refreshFragment();
                }
            }
        }
    }


}
