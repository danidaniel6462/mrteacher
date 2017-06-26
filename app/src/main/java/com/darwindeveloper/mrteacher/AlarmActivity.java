package com.darwindeveloper.mrteacher;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.darwindeveloper.mrteacher.utils.AlarmReceiver;
import com.darwindeveloper.mrteacher.utils.AlarmService;

public class AlarmActivity extends BaseActivity {

    private String evento_id, evento_titulo, evento_descripcion, evento_fecha_creacion, evento_fecha_evento;
    private String carrera_id, carrera_nombre, materia_id, materia_nombre;
    private int evento_dia, evento_mes, evento_anio, evento_hora, evento_minuto;


    private TextView textViewMonth, textViewDay, textViewYear, textViewTitulo;
    private TextView textViewCreationDate, textViewTime, textViewDescription;


    private String enero = "Enero";
    private String febrero = "Febrero";
    private String marzo = "Marzo";
    private String abril = "Abril";
    private String mayo = "Mayo";
    private String junio = "Junio";
    private String julio = "Julio";
    private String agosto = "Agosto";
    private String septiembre = "Septiembre";
    private String octubre = "Octubre";
    private String noviembre = "Noviembre";
    private String diciembre = "Deciembre";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_alarm);

        Intent intent = new Intent(this, AlarmService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);




        intent = getIntent();
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


        textViewDay = (TextView) findViewById(R.id.textViewDay);
        textViewMonth = (TextView) findViewById(R.id.textViewMonth);
        textViewYear = (TextView) findViewById(R.id.textViewYear);
        textViewTitulo = (TextView) findViewById(R.id.textViewTitle);
        textViewCreationDate = (TextView) findViewById(R.id.textViewCreationDate);
        textViewTime = (TextView) findViewById(R.id.textViewTime);
        textViewDescription = (TextView) findViewById(R.id.textViewDescription);


        textViewDay.setText(evento_dia + "");
        textViewYear.setText(evento_anio + "");
        textViewMonth.setText(getStringMonth(evento_mes));
        textViewTitulo.setText(evento_titulo);
        textViewDescription.setText(evento_descripcion);
        if(evento_minuto<9){
            textViewTime.setText(evento_hora + "H0" + evento_minuto);
        }else{
            textViewTime.setText(evento_hora + "H" + evento_minuto);
        }

        textViewCreationDate.setText(evento_fecha_creacion);


        ImageButton btn_close = (ImageButton) findViewById(R.id.btn_close);
        ImageButton btn_edit = (ImageButton) findViewById(R.id.btn_edit);
        ImageButton btn_delete = (ImageButton) findViewById(R.id.btn_delete);

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });


    }


    private void delete() {
        AlertDialog.Builder builder = extras.createDialog(R.drawable.ic_delete_forever_black_24dp, "Confirmación requerida", "¿Eliminar evento?", false);
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int result = dataBaseManager.deleteEvento(evento_id);
                if (result > 0) {
                    dialog.dismiss();
                    Toast.makeText(context, "Evento eliminado", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
        builder.create().show();
    }


    /**
     * retorna un mes como un string de pendiendo del idioma establecido en el OneCalendar
     *
     * @param numMonth numero del mes iniciando desde 0,1,2...
     * @return mes en texto segun el idioma elegido
     */
    public String getStringMonth(int numMonth) {
        switch (numMonth) {
            case 0:
                return enero;

            case 1:
                return febrero;

            case 2:
                return marzo;

            case 3:
                return abril;

            case 4:
                return mayo;

            case 5:
                return junio;

            case 6:
                return julio;

            case 7:
                return agosto;

            case 8:
                return septiembre;

            case 9:
                return octubre;

            case 10:
                return noviembre;

            case 11:
                return diciembre;

        }
        return enero;
    }


}
