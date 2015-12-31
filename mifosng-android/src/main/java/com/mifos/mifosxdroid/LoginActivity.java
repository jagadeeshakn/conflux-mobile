/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mifos.exceptions.ShortOfLengthException;
import com.mifos.mifosxdroid.online.DashboardFragmentActivity;
import com.mifos.objects.User;
import com.mifos.objects.db.UserDetails;
import com.mifos.objects.db.Permissions;
import com.mifos.objects.noncore.DisplayAlert;
import com.mifos.services.API;
import com.mifos.utils.Constants;
import com.mifos.utils.MifosApplication;

import org.apache.http.HttpStatus;

import java.util.List;

import javax.net.ssl.SSLHandshakeException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ishankhanna on 08/02/14.
 */
public class LoginActivity extends ActionBarActivity implements Callback<User>{

    private static final String DEFALUT_PORT ="80" ;
    SharedPreferences sharedPreferences;
    @InjectView(R.id.et_username) EditText et_username;
    @InjectView(R.id.et_password) EditText et_password;
    @InjectView(R.id.bt_login) Button bt_login;
    private String username;
    private String password;
    private Context context;
    private String authenticationToken;
    private ProgressDialog progressDialog;
    private final static String TAG = "LoginActivity";
    SharedPreferences settingsPreferences;
    String TenantIdentifier;
    String previouslyEnteredPort;
    String url;
    SharedPreferences.Editor edit;

    private API api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle(R.string.dashboard);
        actionBar.setSubtitle(R.string.login);
        actionBar.setLogo(R.mipmap.ic_launcher);
        context = LoginActivity.this;
      // sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        settingsPreferences = PreferenceManager.getDefaultSharedPreferences(Constants.applicationContext);
        authenticationToken = settingsPreferences.getString(User.AUTHENTICATION_KEY, "NA");
        TenantIdentifier=settingsPreferences.getString(Constants.TENANT_IDENTIFIER_KEY, "");
        previouslyEnteredPort=settingsPreferences.getString(Constants.INSTANCE_PORT_KEY,DEFALUT_PORT);
        url=settingsPreferences.getString(Constants.INSTANCE_URL_KEY,"");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ButterKnife.inject(this);
        setupUI();
    }


    public void setupUI() {
        progressDialog = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Logging In");
        progressDialog.setCancelable(false);
    }

    public boolean validateUserInputs() throws ShortOfLengthException {

        username = et_username.getEditableText().toString();
        if (username.length() < 5) {
            throw new ShortOfLengthException("Username", 5);
        }

        password = et_password.getEditableText().toString();
        if (password.length() < 6) {
            throw new ShortOfLengthException("Password", 6);
        }
        return true;
    }

    @Override
    public void success(User user, Response response) {
        ((MifosApplication) getApplication()).api = api;
        Permissions permissions;
        List<String> permission=user.getPermissions();
        for(String eachPermission:permission)
        {
            permissions=new Permissions(eachPermission);
            permissions.save();
        }
        UserDetails userDetails;
        userDetails = new UserDetails(user.getUsername(),user.getUserId(),user.getOfficeId(),user.getOfficeName(),user.getStaffId());
        userDetails.save();
        progressDialog.dismiss();
        Toast.makeText(context, getString(R.string.toast_welcome)+" " + user.getUsername(), Toast.LENGTH_SHORT).show();
        saveLastAccessedInstanceUrl(url);
        String lastAccessedTenantIdentifier = TenantIdentifier;
        saveLastAccessedTenant(lastAccessedTenantIdentifier);
        saveAuthenticationKey("Basic " + user.getBase64EncodedAuthenticationKey());
        Intent intent = new Intent(LoginActivity.this, DashboardFragmentActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void failure(RetrofitError retrofitError) {
        try {
            progressDialog.dismiss();
            if (retrofitError.getCause() instanceof SSLHandshakeException) {
                promptUserToByPassTheSSLHandshake();
            } else if (retrofitError.getResponse().getStatus() == HttpStatus.SC_UNAUTHORIZED)
                Toast.makeText(context, getString(R.string.error_login_failed), Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
            Toast.makeText(context, getString(R.string.error_unknown), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This method should show a dialog box and ask the user
     * if he wants to use and unsafe connection. If he agrees
     * we must update our rest adapter to use an unsafe OkHttpClient
     * that trusts any damn thing.
     */
    private void promptUserToByPassTheSSLHandshake(){

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("SSL Certificate Problem")
                .setMessage("There is a problem with your SSLCertificate, would you like to continue? This connection would be unsafe.")
                .setIcon(android.R.drawable.stat_sys_warning)
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("User Login Info","User aggred for continuing with the unsafe connection");
                        edit = settingsPreferences.edit();
                        edit.putBoolean("Trust",true);
                        edit.commit();
                        login(true);
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .create();
        alertDialog.show();

    }

    @OnClick(R.id.bt_login)
    public void onLoginClick(Button button){
        edit = settingsPreferences.edit();
        edit.putBoolean("Trust",false);
        edit.commit();
        login(false);
    }

    private void login(boolean shouldByPassSSLSecurity) {
        try {
            if ( url !=""||TenantIdentifier!="") {
                if (validateUserInputs())
                // Toast.makeText(context, "URL: " + url + "    Tenant:" + TenantIdentifier, Toast.LENGTH_LONG).show();
                progressDialog.show();
                //api = new API(instanceURL, et_tenantIdentifier.getEditableText().toString().trim(), shouldByPassSSLSecurity);
                api = new API(url, TenantIdentifier, shouldByPassSSLSecurity);
                api.userAuthService.authenticate(username, password, this);
            }
            else
            {
                String msg="Please go to settings and provide the url and Tenant ID.";
                String title="Alert";
                String button="OK";
                DisplayAlert.DisplayAlertMessageWithOkButton(context,msg,title,android.R.drawable.stat_sys_warning,button);
            }

            }catch(ShortOfLengthException e){
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }

    }


    @OnEditorAction(R.id.et_password)
    public boolean passwordSubmitted(KeyEvent keyEvent) {
        if (keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            login(false);
            return true;
        }
        return false;
    }

    /**
     * After the user is authenticated the Base64
     * encoded auth token is saved in Shared Preferences
     * so that user can be logged in when returning to the app
     * even if the app is terminated from the background.
     *
     * @param authenticationKey
     */
    public void saveAuthenticationKey(String authenticationKey) {
        SharedPreferences.Editor editor = settingsPreferences.edit();
        editor.putString(User.AUTHENTICATION_KEY, authenticationKey);
        editor.apply();
    }

    /**
     * Stores the domain name in shared preferences
     * if the login was successful, so that it can be
     * referenced later or with multiple login/logouts
     * user doesn't need to type in the domain name
     * over and over again.
     *
     * @param instanceDomain
     */
   /* public void saveLastAccessedInstanceDomainName(String instanceDomain) {
        SharedPreferences.Editor editor = settingsPreferences.edit();
        editor.putString(Constants.INSTANCE_DOMAIN_KEY, instanceDomain);
        editor.apply();
    }*/

    /**
     * Stores the complete instance URL in shared preferences
     * if the login was successful.

     * @param instanceURL
     */
    public void saveLastAccessedInstanceUrl(String instanceURL) {
        SharedPreferences.Editor editor = settingsPreferences.edit();
        editor.putString(Constants.INSTANCE_URL_KEY, instanceURL);
        editor.apply();
    }

    /**
     * Stores the Tenant Identifier in shared preferences
     * if the login was successful, so that it can be
     * referenced later of with multiple login/logouts
     * user doesn't need to type in the tenant identifier
     * over and over again.
     * @param tenantIdentifier
     */
    private void saveLastAccessedTenant(String tenantIdentifier) {
        SharedPreferences.Editor editor = settingsPreferences.edit();
        editor.putString(Constants.TENANT_IDENTIFIER_KEY, tenantIdentifier);
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: " + item.getTitle());
        switch (item.getItemId()) {
            case R.id.offline:
                startActivity(new Intent(this, OfflineCenterInputActivity.class));
                break;
            case R.id.setting :
                startActivity(new Intent(this,Settings.class));
            default: //DO NOTHING
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
