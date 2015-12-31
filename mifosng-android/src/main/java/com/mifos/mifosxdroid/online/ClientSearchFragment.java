/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.SearchedEntity;
import com.mifos.utils.Constants;
import com.mifos.utils.MifosApplication;
import com.mifos.utils.SafeUIBlockingUtility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ClientSearchFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String TAG = "Client Search Fragment";

   /* @InjectView(R.id.et_search_by_id)
    EditText et_searchById;
    @InjectView(R.id.bt_searchClient)
    Button bt_searchClient;*/
    @InjectView(R.id.lv_searchResults)
    ListView lv_searchResults;
    private String searchQuery;

    InputMethodManager inputMethodManager;

    View rootView;
    List<String> clientNames = new ArrayList<String>();
    List<Integer> clientIds = new ArrayList<Integer>();
    SafeUIBlockingUtility safeUIBlockingUtility;

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }





    public ClientSearchFragment() {
        // Required empty public constructor
    }

    public static ClientSearchFragment newInstance() {
        ClientSearchFragment fragment = new ClientSearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_client_search, null);
        ButterKnife.inject(this, rootView);
        //search();
        return rootView;
    }


public void search()
{
    if(!searchQuery.equals(null)) {
        if (!searchQuery.isEmpty()) {
            findClients(searchQuery);

        } else {
            Toast.makeText(getActivity(), "No Search Query Entered!", Toast.LENGTH_SHORT).show();
        }
    }

}

    public void findClients(final String clientName) {

        safeUIBlockingUtility = new SafeUIBlockingUtility(getActivity());
        safeUIBlockingUtility.safelyBlockUI();
        ((MifosApplication) getActivity().getApplicationContext()).api.searchService.searchClientsByName(clientName, new Callback<List<SearchedEntity>>() {
            @Override
            public void success(List<SearchedEntity> searchedEntities, Response response) {

                Iterator<SearchedEntity> iterator = searchedEntities.iterator();
                clientNames.clear();
                clientIds.clear();
                while (iterator.hasNext()) {
                    SearchedEntity searchedEntity = iterator.next();
                    clientNames.add("#" + searchedEntity.getEntityId() + " - " + searchedEntity.getEntityName());
                    clientIds.add(searchedEntity.getEntityId());
                }

                String[] clientNamesArrayForAdapter = clientNames.toArray(new String[clientNames.size()]);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1, clientNamesArrayForAdapter);

                lv_searchResults.setAdapter(adapter);
                lv_searchResults.setOnItemClickListener(ClientSearchFragment.this);

                //If the search query returned one or more results close the keyboard
               /* hideKeyboard();*/


                safeUIBlockingUtility.safelyUnBlockUI();


            }

            @Override
            public void failure(RetrofitError retrofitError) {
                safeUIBlockingUtility.safelyUnBlockUI();
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {

        //Fragment getting detached, keyboard if open must be hidden
        super.onPause();
    }

    /*
        There is a need for this method in the following cases :
        1. If user entered a search query and went out of the app.
        2. If user entered a search query and got some search results and went out of the app.
     */


    @Override
    public void onDetach() {
        super.onDetach();

    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        try{ if(searchQuery != null && !(searchQuery.equals(""))) {
            outState.putString(TAG,searchQuery);
        }

        }catch(NullPointerException npe){
            //Looks like edit text didn't get initialized properly
            System.out.println("swarch query saveinstance state "+npe.getMessage());
        }
        super.onSaveInstanceState(outState);


    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {

            String queryString = (String)savedInstanceState.getString(TAG);
                System.out.println("the query is " + queryString);
            if (queryString != null && !(queryString.equals(""))) {
                searchQuery = queryString;
            }
        }
        else {
            search();
        }
    }



    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Intent clientActivityIntent = new Intent(getActivity(), ClientActivity.class);
        clientActivityIntent.putExtra(Constants.CLIENT_ID, clientIds.get(i));
        startActivity(clientActivityIntent);

    }
}
