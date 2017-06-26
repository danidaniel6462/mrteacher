package com.darwindeveloper.mrteacher.utils;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.darwindeveloper.mrteacher.AlarmActivity;
import com.darwindeveloper.mrteacher.R;

public class AlarmService extends IntentService {
    private NotificationManager alarmNotificationManager;

    private String evento_id, evento_titulo, evento_descripcion, evento_fecha_creacion, evento_fecha_evento;
    private String carrera_id, carrera_nombre, materia_id, materia_nombre;
    private int evento_dia, evento_mes, evento_anio, evento_hora, evento_minuto;

    public AlarmService() {
        super("AlarmService");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        sendNotification(evento_titulo);
        //Log.i("alarmservice", "onHandleIntent");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.i("alarmservice", "onStartCommand");

        evento_id = intent.getStringExtra(AlarmReceiver.EVENTO_ID);
        evento_titulo = intent.getStringExtra(AlarmReceiver.EVENTO_TITULO);
        evento_descripcion = intent.getStringExtra(AlarmReceiver.EVENTO_DESCRIPCION);
        evento_fecha_creacion = intent.getStringExtra(AlarmReceiver.EVENTO_FECHA_CREACION);
        evento_fecha_evento = intent.getStringExtra(AlarmReceiver.EVENTO_FECHA_EVENTO);
        carrera_id = intent.getStringExtra(AlarmReceiver.EVENTO_CARRERA_ID);
        carrera_nombre = intent.getStringExtra(AlarmReceiver.EVENTO_CARRERA_NOMBRE);
        materia_id = intent.getStringExtra(AlarmReceiver.EVENTO_MATERIA_ID);
        materia_nombre = intent.getStringExtra(AlarmReceiver.EVENTO_MATERIA_NOMBRE);


        evento_dia = intent.getIntExtra(AlarmReceiver.EVENTO_DIA, -1);
        evento_mes = intent.getIntExtra(AlarmReceiver.EVENTO_MES, -1);
        evento_anio = intent.getIntExtra(AlarmReceiver.EVENTO_ANIO, -1);
        evento_hora = intent.getIntExtra(AlarmReceiver.EVENTO_HORA, -1);
        evento_minuto = intent.getIntExtra(AlarmReceiver.EVENTO_MINUTO, -1);

        return super.onStartCommand(intent, flags, startId);

    }

    private void sendNotification(String msg) {
        Log.d("AlarmService", "Preparing to send notification...: " + msg);
        alarmNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, AlarmActivity.class);


        intent.putExtra(AlarmReceiver.EVENTO_ID, evento_id);
        intent.putExtra(AlarmReceiver.EVENTO_TITULO, evento_titulo);
        intent.putExtra(AlarmReceiver.EVENTO_DESCRIPCION, evento_descripcion);
        intent.putExtra(AlarmReceiver.EVENTO_FECHA_CREACION, evento_fecha_creacion);
        intent.putExtra(AlarmReceiver.EVENTO_FECHA_EVENTO, evento_fecha_evento);
        intent.putExtra(AlarmReceiver.EVENTO_CARRERA_ID, carrera_id);
        intent.putExtra(AlarmReceiver.EVENTO_CARRERA_NOMBRE, carrera_nombre);
        intent.putExtra(AlarmReceiver.EVENTO_MATERIA_ID, materia_id);
        intent.putExtra(AlarmReceiver.EVENTO_MATERIA_NOMBRE, materia_nombre);
        intent.putExtra(AlarmReceiver.EVENTO_DIA, evento_dia);
        intent.putExtra(AlarmReceiver.EVENTO_MES, evento_mes);
        intent.putExtra(AlarmReceiver.EVENTO_ANIO, evento_anio);
        intent.putExtra(AlarmReceiver.EVENTO_HORA, evento_hora);
        intent.putExtra(AlarmReceiver.EVENTO_MINUTO, evento_minuto);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder alamNotificationBuilder = new NotificationCompat.Builder(
                this).setContentTitle("Mr Teacher Evento").setSmallIcon(R.drawable.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);


        alamNotificationBuilder.setContentIntent(contentIntent);
        alamNotificationBuilder.setAutoCancel(true);
        alarmNotificationManager.notify(1, alamNotificationBuilder.build());
        Log.d("AlarmService", "Notification sent.");
    }
}