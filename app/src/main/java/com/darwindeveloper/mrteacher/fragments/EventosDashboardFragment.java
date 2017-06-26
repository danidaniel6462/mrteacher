package com.darwindeveloper.mrteacher.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.darwindeveloper.mrteacher.EventoActivity;
import com.darwindeveloper.mrteacher.R;

/**
 * Created by Darwin Morocho on 12/3/2017.
 */

public class EventosDashboardFragment extends Fragment {

    public static final String EVENTO_ID = "mrteacher.EventosDashboardFragmentr.evento_id";
    public static final String EVENTO_TITULO = "mrteacher.EventosDashboardFragmentr.evento_titulo";
    public static final String EVENTO_DESCRIPCION = "mrteacher.EventosDashboardFragmentr.evento_descripcion";
    public static final String EVENTO_FECHA_CREACION = "mrteacher.EventosDashboardFragmentr.evento_fecha_creacion";
    public static final String EVENTO_FECHA_EVENTO = "mrteacher.EventosDashboardFragmentr.evento_fecha_evento";
    public static final String EVENTO_CARRERA_ID = "mrteacher.EventosDashboardFragmentr.evento_carrera_id";
    public static final String EVENTO_CARRERA_NOMBRE = "mrteacher.EventosDashboardFragmentr.evento_carrera_nombre";
    public static final String EVENTO_MATERIA_ID = "mrteacher.EventosDashboardFragmentr.evento_materia_Id";
    public static final String EVENTO_MATERIA_NOMBRE = "mrteacher.EventosDashboardFragmentr.evento_materia_nombre";

    private String evento_id, evento_titulo, evento_descripcion, evento_fecha_creacion, evento_fecha_evento;
    private String carrera_id, carrera_nombre, materia_id, materia_nombre;

    private int evento_dia, evento_mes, evento_anio, event_hora, evento_minuto;

    private Context context;
    private View rootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

        Bundle args = getArguments();
        evento_id = args.getString(EVENTO_ID);
        evento_titulo = args.getString(EVENTO_TITULO);
        evento_fecha_evento = args.getString(EVENTO_FECHA_EVENTO);
        evento_descripcion = args.getString(EVENTO_DESCRIPCION, "Sin descripción");
        evento_fecha_creacion = args.getString(EVENTO_FECHA_CREACION);
        carrera_id = args.getString(EVENTO_CARRERA_ID);
        carrera_nombre = args.getString(EVENTO_CARRERA_ID);
        materia_id = args.getString(EVENTO_MATERIA_ID, "-1");
        materia_nombre = args.getString(EVENTO_MATERIA_NOMBRE, "-1");


    }


    private TextView textViewTitulo, textViewDate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = LayoutInflater.from(context).inflate(R.layout.fragment_eventos_dashboard, container, false);

        textViewTitulo = (TextView) rootView.findViewById(R.id.textViewTitle);
        textViewDate = (TextView) rootView.findViewById(R.id.textViewDate);

        textViewTitulo.setText(evento_titulo);
        textViewDate.setText(evento_fecha_evento);


        Button btn_wacth = (Button) rootView.findViewById(R.id.buttonSee);
        btn_wacth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInts();

                Intent intent = new Intent(context, EventoActivity.class);
                intent.putExtra(EventoActivity.EVENTO_ID, evento_id);
                intent.putExtra(EventoActivity.EVENTO_TITULO, evento_titulo);
                intent.putExtra(EventoActivity.EVENTO_DESCRIPCION, evento_descripcion);
                intent.putExtra(EventoActivity.EVENTO_FECHA_CREACION, evento_fecha_creacion);
                intent.putExtra(EventoActivity.EVENTO_FECHA_EVENTO, evento_fecha_evento);
                intent.putExtra(EventoActivity.EVENTO_CARRERA_ID, carrera_id);
                intent.putExtra(EventoActivity.EVENTO_CARRERA_NOMBRE, carrera_nombre);
                intent.putExtra(EventoActivity.EVENTO_MATERIA_ID, materia_id);
                intent.putExtra(EventoActivity.EVENTO_DIA, evento_dia);
                intent.putExtra(EventoActivity.EVENTO_MES, evento_mes);
                intent.putExtra(EventoActivity.EVENTO_ANIO, evento_anio);
                intent.putExtra(EventoActivity.EVENTO_HORA, event_hora);
                intent.putExtra(EventoActivity.EVENTO_MINUTO, evento_minuto);


                context.startActivity(intent);


            }
        });

        return rootView;
    }


    private void getInts() {
        String enteros[] = evento_fecha_evento.split("\\/|\\:|\\s");


        Log.i("enteros", enteros.length + "");
        evento_anio = Integer.parseInt(enteros[0]);
        evento_mes = Integer.parseInt(enteros[1]);
        evento_dia = Integer.parseInt(enteros[2]);
        event_hora = Integer.parseInt(enteros[3]);
        evento_minuto = Integer.parseInt(enteros[4]);
    }
}
