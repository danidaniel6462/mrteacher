package com.darwindeveloper.mrteacher.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.darwindeveloper.mrteacher.DashboardActivity;
import com.darwindeveloper.mrteacher.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    //facebook
    CallbackManager callbackManager;
    //google
    GoogleApiClient mGoogleApiClient;
    private int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        callbackManager = CallbackManager.Factory.create();

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]
        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]

        List<String> permissionNeeds = Arrays.asList("user_photos", "email",
                "user_birthday", "public_profile", "AccessToken");

        Button buttonFB = (Button) findViewById(R.id.buttonFB);
        Button buttonG = (Button) findViewById(R.id.buttonG);
        TextView link = (TextView) findViewById(R.id.link);
        link.setText(Html.fromHtml("<a href='http://darwindeveloper.com/'>Mr. Teacher Oficial Web</a>"));
        link.setMovementMethod(LinkMovementMethod.getInstance());


        buttonFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginFacebook();
            }
        });

        buttonG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginGoogle();
            }
        });

    }


    private void showKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.darwindeveloper.mrteacher",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    /**
     * comprueba un inicio de sesion previo
     */
    private void checkLogin() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);

        String login = sharedPref.getString(getString(R.string.usuario_login), "none");

        //si el susuario ya se logeo con anterioridad
        if (login.equals("facebook") || login.equals("google")) {

            Intent intent = new Intent(this, DashboardActivity.class);
            //intent.putExtra(MainActivity.LOGIN, login);
            startActivity(intent);
            finish();//terminamos la actividad actual

        }

    }


    /**
     * realiza el proceso de login con facebook
     */
    private void loginFacebook() {

        LoginButton loginButton = (LoginButton) findViewById(R.id.loginButton);
        loginButton.performClick();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {


                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object,
                                                    GraphResponse response) {
                                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString(getString(R.string.usuario_login), "facebook");

                                Log.i("LoginActivity",
                                        response.toString());
                                try {
                                    String url_foto = "https://graph.facebook.com/" + object.getString("id") + "/picture?width=150&height=150";

                                    String name = object.getString("name");
                                    String email = object.getString("email");

                                    editor.putString(getString(R.string.usuario_nombre), name);
                                    editor.putString(getString(R.string.usuario_email), email);
                                    editor.putString(getString(R.string.usuario_foto), url_foto);

                                    //gender = object.getString("gender");
                                    //birthday = object.getString("birthday");

                                    editor.apply();

                                    checkLogin();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }


                        });


                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }


    private void loginGoogle() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }


    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            callbackManager.onActivityResult(requestCode, responseCode, data);
        }
    }


    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("google login", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
            SharedPreferences.Editor editor = sharedPref.edit();
            String name = acct.getDisplayName();
            String email = acct.getEmail();
            String foto = acct.getPhotoUrl() + "";
            editor.putString(getString(R.string.usuario_login), "google");
            editor.putString(getString(R.string.usuario_nombre), name);
            editor.putString(getString(R.string.usuario_email), email);
            editor.putString(getString(R.string.usuario_foto), foto);
            editor.apply();

            checkLogin();

        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }
}
