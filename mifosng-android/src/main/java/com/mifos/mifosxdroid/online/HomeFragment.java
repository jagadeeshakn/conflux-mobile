/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.fragments.GetMenuListFragment;
import com.mifos.objects.MenuListData;
import com.mifos.objects.SearchedEntity;
import com.mifos.utils.Constants;
import com.mifos.utils.FragmentConstants;
import com.mifos.utils.MifosApplication;
import com.mifos.utils.SafeUIBlockingUtility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "Client Search Fragment";

    @InjectView(R.id.et_search_by_id)
    EditText et_searchById;
    @InjectView(R.id.bt_searchClient)
    ImageButton btn_searchClient;

    View rootView;


    ClientSearchFragment clientSearchFragment;
    //Search the Client
    public String getSearchQuery() {
        return searchQuery;
    }


    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    private String searchQuery;

    InputMethodManager inputMethodManager;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //to disable the keyboard popup on view of an fragment
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        clientSearchFragment = new ClientSearchFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, null);
       ButterKnife.inject(this, rootView);
        Log.i(TAG, getResources().getString(R.string.searchbar));
        loadmenufragment();
        et_searchById.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    String clientId=textView.getText().toString().trim();
                    if(clientId.equals(""))
                    {
                        Toast.makeText(getActivity(),"Client name cannot be empty",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        popStacEntryFragments();
                        startClientSearchFragment(clientId);
                    }
                    return true;
                }
                return false;
            }
        });

        et_searchById.setText("");
        return rootView;
    }



    public void loadmenufragment()
    {
        //Import the the Home Screen Menu

        String[] strings=getResources().getStringArray(R.array.menu_list);
        MenuListData.setMenuListArray(strings);
        GetMenuListFragment getFragment = new GetMenuListFragment();
        FragmentTransaction fragmentTransaction=getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.home_dashboard_container, getFragment, "incomeList fragment");
        fragmentTransaction.commit();
    }



    @OnClick(R.id.bt_searchClient)
    public void SearchClient()
    {

        String clientId=et_searchById.getText().toString().trim();
        if(clientId.trim().equals(""))
        {
            Toast.makeText(getActivity(),"Client name cannot be empty",Toast.LENGTH_LONG).show();
        }
        else
        {
            popStacEntryFragments();
            startClientSearchFragment(clientId);
        }
    }

    public void startClientSearchFragment(String clientId)
    {
        clientSearchFragment=clientSearchFragment.newInstance();
        clientSearchFragment.setSearchQuery(clientId);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_SEARCH);
        fragmentTransaction.replace(R.id.home_dashboard_container, clientSearchFragment, FragmentConstants.FRAG_CLIENT_SEARCH);
        fragmentTransaction.commit();
    }

    public void popStacEntryFragments() {
        if(getActivity().getSupportFragmentManager().getBackStackEntryCount()!=0)
        {
            getActivity().getSupportFragmentManager().popBackStackImmediate();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        et_searchById.setText("");
    }

    @Override
    public void onPause() {

        //Fragment getting detached, keyboard if open must be hidden
       /* hideKeyboard();*/

        super.onPause();
    }

    /*
        There is a need for this method in the following cases :
        1. If user entered a search query and went out of the app.
        2. If user entered a search query and got some search results and went out of the app.
     */

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try{
            String queryString = et_searchById.getEditableText().toString();

            if(queryString != null && !(queryString.equals(""))) {
                outState.putString(TAG+et_searchById.getId(),queryString);
            }

        }catch(NullPointerException npe){
            //Looks like edit text didn't get initialized properly
        }

    }

    @Override
    public void onDetach() {

        super.onDetach();

    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {

            String queryString = savedInstanceState.getString(TAG+et_searchById.getId());

            if (queryString != null && !(queryString.equals(""))) {
                et_searchById.setText(queryString);

            }
        }


    }

}
