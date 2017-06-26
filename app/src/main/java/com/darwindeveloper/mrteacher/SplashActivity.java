package com.darwindeveloper.mrteacher;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;

import com.darwindeveloper.mrteacher.login.LoginActivity;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {
    // Set the duration of the splash screen
    private static final long SPLASH_SCREEN_DELAY = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        final boolean[] login = {false};

        new Thread() {
            @Override
            public void run() {
                super.run();
                login[0] =checkLogin();
            }
        };

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                if(login[0]){
                    Intent mainIntent = new Intent(SplashActivity.this,DashboardActivity.class);
                    startActivity(mainIntent);
                    finish();
                }else{
                    startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                    finish();
                }
            }
        }, SPLASH_SCREEN_DELAY);


    }

    /**
     * comprueba un inicio de sesion previo
     */
    private boolean checkLogin() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String login = sharedPref.getString(getString(R.string.usuario_login), "none");
        //si el susuario ya se logeo con anterioridad
        return login.equals("facebook") || login.equals("google");
    }

}
