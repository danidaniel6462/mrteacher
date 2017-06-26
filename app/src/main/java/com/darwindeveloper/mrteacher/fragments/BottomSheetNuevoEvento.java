package com.darwindeveloper.mrteacher.fragments;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.darwindeveloper.mrteacher.R;
import com.darwindeveloper.mrteacher.base_de_datos.DataBaseManager;
import com.darwindeveloper.mrteacher.tablas.Evento;
import com.darwindeveloper.mrteacher.utils.AlarmReceiver;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * Created by Darwin Morocho on 19/3/2017.
 */

public class BottomSheetNuevoEvento extends BottomSheetDialogFragment {

    private Context context;
    private DataBaseManager databasemanager;
    private BottomSheetBehavior mBehavior;
    private int RQS_1 = 100;


    public static final String CARRERA_ID = "mrteacher.eventos.BottomSheetNuevoEvento.carrera_id";
    public static final String CARRERA_NOMBRE = "mrteacher.eventos.BottomSheetNuevoEvento.carrera_nombre";
    public static final String MATERIA_ID = "mrteacher.eventos.BottomSheetNuevoEvento.materia_id";
    public static final String MATERIA_NOMBRE = "mrteacher.eventos.BottomSheetNuevoEvento.materia_nombre";

    private String carrera_id, carrera_nombre, materia_id, materia_nombre;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;


    public static BottomSheetDialogFragment newInstance(Bundle args) {
        BottomSheetNuevoEvento bottomSheetNuevoEvento = new BottomSheetNuevoEvento();
        bottomSheetNuevoEvento.setArguments(args);
        return bottomSheetNuevoEvento;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        try {
            databasemanager = new DataBaseManager(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bundle args = getArguments();
        carrera_id = args.getString(CARRERA_ID);
        carrera_nombre = args.getString(CARRERA_NOMBRE);
        materia_id = args.getString(MATERIA_ID, "-1");
        materia_nombre = args.getString(MATERIA_NOMBRE, "-1");

    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(context, R.layout.bottom_sheet_nuevo_evento, null);
        dialog.setContentView(contentView);

        mBehavior = BottomSheetBehavior.from((View) contentView.getParent());
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);


        final DatePicker datePicker = (DatePicker) contentView.findViewById(R.id.datePicker);
        final TimePicker timePicker = (TimePicker) contentView.findViewById(R.id.timePicker);
        final EditText editTextTitulo = (EditText) contentView.findViewById(R.id.editTextTitulo);
        final EditText editTextDescripcion = (EditText) contentView.findViewById(R.id.editTextDescripcion);
        final Switch swith_alarm = (Switch) contentView.findViewById(R.id.switch_alarm);

        timePicker.setIs24HourView(true);

        ImageButton btn_cancel = (ImageButton) contentView.findViewById(R.id.btn_cancel);
        ImageButton btn_save = (ImageButton) contentView.findViewById(R.id.btn_save);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth();
                int year = datePicker.getYear();

                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();

                String fecha_creacion = getDateTime();
                String s_day = day + "", s_month = (month + 1) + "", s_hour = hour + "", s_minute = minute + "";


                if (hour < 10) {
                    s_hour = "0" + s_hour;
                }
                if (minute < 10) {
                    s_minute = "0" + s_minute;
                }
                if (day < 10) {
                    s_day = "0" + s_day;
                }
                if (month < 10) {
                    s_month = "0" + s_month;
                }

                String fecha_evento = year + "/" + s_month + "/" + s_day + " " + s_hour + ":" + s_minute + ":00";

                String titulo = editTextTitulo.getText().toString();
                String descripcion = editTextDescripcion.getText().toString();
                String alarma_id = "0";


                if (titulo.trim().length() < 4) {
                    editTextTitulo.setError("Ingrese un titulo valido");
                    editTextTitulo.requestFocus();
                } else {
                    Evento evento = null;
                    if (swith_alarm.isChecked()) {//creamos una nueva alarma
                        alarma_id = "1";

                        evento = databasemanager.insertEvento(titulo, fecha_creacion, fecha_evento, descripcion, alarma_id, carrera_id, materia_id);


                        if (evento != null) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.DAY_OF_MONTH, day);
                            calendar.set(Calendar.MONTH, month);
                            calendar.set(Calendar.YEAR, year);
                            calendar.set(Calendar.HOUR_OF_DAY, hour);
                            calendar.set(Calendar.MINUTE, minute);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);

                            Intent myIntent = new Intent(context, AlarmReceiver.class);
                            myIntent.putExtra(AlarmReceiver.EVENTO_ID, evento.getId());
                            myIntent.putExtra(AlarmReceiver.EVENTO_TITULO, titulo);
                            myIntent.putExtra(AlarmReceiver.EVENTO_DESCRIPCION, descripcion);
                            myIntent.putExtra(AlarmReceiver.EVENTO_FECHA_CREACION, fecha_creacion);
                            myIntent.putExtra(AlarmReceiver.EVENTO_FECHA_EVENTO, fecha_evento);
                            myIntent.putExtra(AlarmReceiver.EVENTO_CARRERA_ID, carrera_id);
                            myIntent.putExtra(AlarmReceiver.EVENTO_CARRERA_NOMBRE, carrera_nombre);
                            myIntent.putExtra(AlarmReceiver.EVENTO_MATERIA_ID, materia_id);
                            myIntent.putExtra(AlarmReceiver.EVENTO_MATERIA_NOMBRE, materia_nombre);
                            myIntent.putExtra(AlarmReceiver.EVENTO_DIA, day);
                            myIntent.putExtra(AlarmReceiver.EVENTO_MES, month);
                            myIntent.putExtra(AlarmReceiver.EVENTO_ANIO, year);
                            myIntent.putExtra(AlarmReceiver.EVENTO_HORA, hour);
                            myIntent.putExtra(AlarmReceiver.EVENTO_MINUTO, minute);


                            pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);
                            alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);

                        }


                    } else {
                        alarmManager.cancel(pendingIntent);
                        evento = databasemanager.insertEvento(titulo, fecha_creacion, fecha_evento, descripcion, alarma_id, carrera_id, materia_id);
                    }


                    if (evento != null) {
                        Toast.makeText(context, "Evento creado", Toast.LENGTH_SHORT).show();
                        mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        onEventChangeListener.onEventChange();
                    } else {
                        Toast.makeText(context, "NO se pudo crear el evento", Toast.LENGTH_SHORT).show();
                    }

                }


            }
        });


    }


    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            onEventChangeListener = (OnEventChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement");
        }
    }


    public interface OnEventChangeListener {
        void onEventChange();
    }

    private OnEventChangeListener onEventChangeListener;
}
