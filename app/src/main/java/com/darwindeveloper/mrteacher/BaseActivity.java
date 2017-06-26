package com.darwindeveloper.mrteacher;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.darwindeveloper.mrteacher.base_de_datos.DataBaseManager;
import com.darwindeveloper.mrteacher.utils.Extras;

import java.io.IOException;

/**
 * Created by Darwin Morocho on 7/3/2017.
 */

public class BaseActivity extends AppCompatActivity {

    protected Context context;//contexto de la actividad
    protected DataBaseManager dataBaseManager;//adminsitrador de base de datos
    protected Extras extras;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = BaseActivity.this;//inicializamo el contexto
        try {
            dataBaseManager = new DataBaseManager(context);
        } catch (IOException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }

        extras = new Extras(context);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataBaseManager.cerrarConecion();
    }


    /**
     * comprueba la conneccion a internet
     *
     * @return
     */
    protected boolean checkInternetConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }


    /**
     * muestra un snackar personalizado
     *
     * @param sms   texto a mostrar
     * @param color color de fondo del snackbar
     */
    protected void mostrarSnackBar(String sms, int color) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), sms, Snackbar.LENGTH_SHORT);
        snackbar.setAction("Action", null).show();
        snackbar.setDuration(2000);
        snackbar.getView().setBackgroundColor(color);
        snackbar.show();
    }



}
