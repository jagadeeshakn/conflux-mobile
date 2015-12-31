package com.mifos.mifosxdroid.fragments;

/**
 * Created by conflux37 on 10/22/2015.
 */
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.MenuListAdapter;
import com.mifos.mifosxdroid.online.CentersActivity;
import com.mifos.mifosxdroid.online.ClientListFragment;
import com.mifos.mifosxdroid.online.CreateCenterFragment;
import com.mifos.mifosxdroid.createnewclient.activity.CreateNewClientActivity;
import com.mifos.mifosxdroid.collectionsheet.activity.GenerateCollectionSheet;
import com.mifos.utils.FragmentConstants;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class GetMenuListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    View rootView;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mStaggeredLayoutManager;
    private MenuListAdapter mAdapter;
    private boolean isListView;

    Fragment newFragment;

    String TAG=GetMenuListFragment.class.getSimpleName();
    @InjectView(R.id.btn_create_new_client)
    ImageButton btn_create_client;
    @InjectView(R.id.linear_layout_create_client)
    LinearLayout linearLayout_create_client;
    @InjectView(R.id.text_create_new_client)
    TextView textView_create_new_client;
    @InjectView(R.id.btn_create_new_center)
    ImageButton btn_create_center;
    @InjectView(R.id.linear_layout_create_center)
    LinearLayout linearLayout_create_center;
    @InjectView(R.id.text_create_new_center)
    TextView textView_create_new_center;
    @InjectView(R.id.btn_collection_sheet)
    ImageButton btn_collection_sheet;
    @InjectView(R.id.linear_layout_center_list)
    LinearLayout linearLayout_center_list;
    @InjectView(R.id.text_center_list)
    TextView textView_center_listr;
    @InjectView(R.id.btn_center_list)
    ImageButton btn_center_list;
    String FRAG_TAG;


    // TODO: Rename and change types and number of parameters
    public GetMenuListFragment() {
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
        Log.i(TAG, getResources().getString(R.string.menu_options));
        rootView= inflater.inflate(R.layout.fragment_get_menu_recyclerview, null);
        ButterKnife.inject(this,rootView);


        Picasso.with(getActivity()).load(R.drawable.createclient).fit().into(btn_create_client);
        Picasso.with(getActivity()).load(R.drawable.createnewcenter).fit().into(btn_create_center);
        Picasso.with(getActivity()).load(R.drawable.collectionsheet).fit().into(btn_collection_sheet);
        Picasso.with(getActivity()).load(R.drawable.centerlist).fit().into(btn_center_list);

      /*  mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        mStaggeredLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mStaggeredLayoutManager);
        mRecyclerView.setHasFixedSize(false);
        mAdapter = new MenuListAdapter(getActivity().getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(onItemClickListener);
        isListView = true;*/
        return rootView;
    }


    @OnClick(R.id.btn_collection_sheet)
    public void collectionsheet(View view)
    {
        startActivity(new Intent(getActivity(), GenerateCollectionSheet.class));
    }

    @OnClick(R.id.btn_create_new_client)
    public void createClient(View view)
    {
        Log.i(TAG, "Create New Client has been initiated by user ");
        popStacEntryFragments();
        startActivity(new Intent(getActivity(), CreateNewClientActivity.class));
    }
    @OnClick(R.id.btn_create_new_center)
    public void createCenter(View view)
    {
        Log.i(TAG, "Create New Center has been initiated by user ");
        popStacEntryFragments();
        newFragment=new CreateCenterFragment();
        FRAG_TAG=FragmentConstants.FRAG_CREATE_CENTER;
        startNewFragment(newFragment, FRAG_TAG);
    }

    @OnClick(R.id.btn_center_list)
    public void centerList(View view)
    {
        Log.i(TAG, "Center List has been initiated");
        popStacEntryFragments();
        startActivity(new Intent(getActivity(), CentersActivity.class));
    }
    @OnClick(R.id.btn_collection_sheet)
            public void collectionSheet(View view){
        Log.i(TAG,"Collection sheet has been initiated");
        popStacEntryFragments();
        startActivity(new Intent(getActivity(), GenerateCollectionSheet.class));
    }


    MenuListAdapter.OnItemClickListener onItemClickListener= new MenuListAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            String FRAG_TAG;
            Log.i(TAG,"menu option selected is "+position);
            switch (position)
            {
                case 0:popStacEntryFragments();
                    startActivity(new Intent(getActivity(), CreateNewClientActivity.class));
                    break;
                case 1: popStacEntryFragments();
                    /*newFragment = new ClientListFragment();
                    TAG=FragmentConstants.FRAG_CLIENT_SEARCH;*/
                    newFragment=new CreateCenterFragment();
                    FRAG_TAG=FragmentConstants.FRAG_CREATE_CENTER;
                    startNewFragment(newFragment,FRAG_TAG);
                    break;
                case 3: popStacEntryFragments();
                    startActivity(new Intent(getActivity(), CentersActivity.class));
                    break;
                default:break;

            }

        }
    };


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed() {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void popStacEntryFragments() {
        if(getActivity().getSupportFragmentManager().getBackStackEntryCount()!=0)
        {
            getActivity().getSupportFragmentManager().popBackStackImmediate();
        }
    }
public void startNewFragment(Fragment fragment,String TAG)
{

    Log.i(TAG, "Start the Fragments");
    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
    fragmentTransaction.addToBackStack(TAG);
    fragmentTransaction.replace(R.id.dashboard_global_container, fragment,TAG);
    fragmentTransaction.commit();
}

    @Override
    public void onDetach() {
        super.onDetach();

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
        public void onFragmentInteraction();
    }

}