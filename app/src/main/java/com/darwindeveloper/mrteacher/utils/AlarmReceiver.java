package com.darwindeveloper.mrteacher.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.darwindeveloper.mrteacher.AlarmActivity;

import static android.support.v4.content.WakefulBroadcastReceiver.startWakefulService;


/**
 * Created by Darwin Morocho on 19/3/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {


    public static final String EVENTO_ID = "mrteacher.alarmreciver.evento_id";
    public static final String EVENTO_TITULO = "mrteacher.alarmreciver.evento_titulo";
    public static final String EVENTO_DESCRIPCION = "mrteacher.alarmreciver.evento_descripcion";
    public static final String EVENTO_FECHA_CREACION = "mrteacher.alarmreciver.evento_fecha_creacion";
    public static final String EVENTO_FECHA_EVENTO = "mrteacher.alarmreciver.evento_fecha_evento";
    public static final String EVENTO_CARRERA_ID = "mrteacher.alarmreciver.evento_carrera_id";
    public static final String EVENTO_CARRERA_NOMBRE = "mrteacher.alarmreciver.evento_carrera_nombre";
    public static final String EVENTO_MATERIA_ID = "mrteacher.alarmreciver.evento_materia_Id";
    public static final String EVENTO_MATERIA_NOMBRE = "mrteacher.alarmreciver.evento_materia_nombre";


    public static final String EVENTO_DIA = "mrteacher.alarmreciver.evento_dia";
    public static final String EVENTO_MES = "mrteacher.alarmreciver.evento_mes";
    public static final String EVENTO_ANIO = "mrteacher.alarmreciver.evento_anio";
    public static final String EVENTO_HORA = "mrteacher.alarmreciver.evento_hora";
    public static final String EVENTO_MINUTO = "mrteacher.alarmreciver.evento_minuto";


    private String evento_id, evento_titulo, evento_descripcion, evento_fecha_creacion, evento_fecha_evento;
    private String carrera_id, carrera_nombre, materia_id, materia_nombre;
    private int evento_dia, evento_mes, evento_anio, evento_hora, evento_minuto;

    @Override
    public void onReceive(Context context, Intent intent) {

        evento_id = intent.getStringExtra(EVENTO_ID);
        evento_titulo = intent.getStringExtra(EVENTO_TITULO);
        evento_descripcion = intent.getStringExtra(EVENTO_DESCRIPCION);
        evento_fecha_creacion = intent.getStringExtra(EVENTO_FECHA_CREACION);
        evento_fecha_evento = intent.getStringExtra(EVENTO_FECHA_EVENTO);
        carrera_id = intent.getStringExtra(EVENTO_CARRERA_ID);
        carrera_nombre = intent.getStringExtra(EVENTO_CARRERA_NOMBRE);
        materia_id = intent.getStringExtra(EVENTO_MATERIA_ID);
        materia_nombre = intent.getStringExtra(EVENTO_MATERIA_NOMBRE);


        evento_dia = intent.getIntExtra(EVENTO_DIA, -1);
        evento_mes = intent.getIntExtra(EVENTO_MES, -1);
        evento_anio = intent.getIntExtra(EVENTO_ANIO, -1);
        evento_hora = intent.getIntExtra(EVENTO_HORA, -1);
        evento_minuto = intent.getIntExtra(EVENTO_MINUTO, -1);





        //this will sound the alarm tone
        //this will sound the alarm once, if you wish to
        //raise alarm in loop continuously then use MediaPlayer and setLooping(true)
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
        ringtone.play();

        //this will send a notification message
        ComponentName comp = new ComponentName(context.getPackageName(),
                AlarmService.class.getName());


        Intent service_intent = (intent.setComponent(comp));


        service_intent.putExtra(EVENTO_ID,evento_id);
        service_intent.putExtra(EVENTO_TITULO,evento_titulo);
        service_intent.putExtra(EVENTO_DESCRIPCION,evento_descripcion);
        service_intent.putExtra(EVENTO_FECHA_CREACION,evento_fecha_creacion);
        service_intent.putExtra(EVENTO_FECHA_EVENTO,evento_fecha_evento);
        service_intent.putExtra(EVENTO_CARRERA_ID,carrera_id);
        service_intent.putExtra(EVENTO_CARRERA_NOMBRE,carrera_nombre);
        service_intent.putExtra(EVENTO_MATERIA_ID,materia_id);
        service_intent.putExtra(EVENTO_MATERIA_NOMBRE,materia_nombre);

        service_intent.putExtra(EVENTO_DIA,evento_dia);
        service_intent.putExtra(EVENTO_MES,evento_mes);
        service_intent.putExtra(EVENTO_ANIO,evento_anio);

        service_intent.putExtra(EVENTO_HORA,evento_hora);
        service_intent.putExtra(EVENTO_MINUTO,evento_minuto);


        startWakefulService(context, service_intent);
        setResultCode(Activity.RESULT_OK);
    }

}