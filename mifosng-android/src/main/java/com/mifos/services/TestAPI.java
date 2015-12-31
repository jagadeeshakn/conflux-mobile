package com.mifos.services;

import com.mifos.objects.CgtData;
import com.mifos.objects.GrtData;
import com.mifos.mifosxdroid.createnewclient.data.clientdetails.Template;
import com.mifos.services.data.APIEndPoint;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Headers;

/**
 * Created by conflux37 on 11/25/2015.
 */
public class TestAPI {

    public static String jsonServerUrl="http://192.168.0.112:3000";
    public static final String ACCEPT_JSON = "Accept: application/json";
    public static final String CONTENT_TYPE_JSON = "Content-Type: application/json";
    public CGT cgt;
    public GRT grt;
    public ParentServices parentServices;
    public TestAPI()
    {
        RestAdapter.Builder restAdapterBuilder = new RestAdapter.Builder();
        restAdapterBuilder.setEndpoint(jsonServerUrl);
        RestAdapter restAdapter = restAdapterBuilder.build();
        restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        cgt=restAdapter.create(CGT.class);
        grt=restAdapter.create(GRT.class);
        parentServices=restAdapter.create(ParentServices.class);
    }

    public interface CGT {
        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET("/groupcgt")
        public void getCgt(Callback<List<CgtData>> callback);
    }

    public interface GRT{
        @Headers({ACCEPT_JSON,CONTENT_TYPE_JSON})
        @GET("/groupGrt")
        public void getGrt(Callback<GrtData> callback);
    }
    public interface ParentServices{
        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET(APIEndPoint.TEMPLATE)
        public void getAllParent(Callback<Template> callback);

    }
}
