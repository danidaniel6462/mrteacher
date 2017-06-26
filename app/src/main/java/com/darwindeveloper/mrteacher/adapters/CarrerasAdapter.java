package com.darwindeveloper.mrteacher.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.darwindeveloper.mrteacher.R;
import com.darwindeveloper.mrteacher.tablas.Carrera;

import java.util.ArrayList;

/**
 * Created by Darwin Morocho on 12/3/2017.
 */

public class CarrerasAdapter extends RecyclerView.Adapter<CarrerasAdapter.CViewHolder> {
    private Context context;
    private ArrayList<Carrera> carreras;

    public CarrerasAdapter(Context context, ArrayList<Carrera> carreras) {
        this.context = context;
        this.carreras = carreras;
    }

    @Override
    public CViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_carrera, parent, false);
        return new CViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CViewHolder holder, final int position) {
        final Carrera carrera = carreras.get(position);

        holder.carrera.setText(carrera.getNombre());

        holder.carrera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCarreraClickListener.onCarreraClickName(carrera, position);
            }
        });

        holder.students.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCarreraClickListener.onCarreraClickStudents(carrera, position);
            }
        });


        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCarreraClickListener.onCarreraClickMenu(carrera, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return carreras.size();
    }

    class CViewHolder extends RecyclerView.ViewHolder {

        TextView carrera;
        ImageButton menu, students;

        public CViewHolder(View itemView) {
            super(itemView);
            carrera = (TextView) itemView.findViewById(R.id.textViewCareer);
            menu = (ImageButton) itemView.findViewById(R.id.btn_menu);
            students = (ImageButton) itemView.findViewById(R.id.btn_students);
        }
    }


    public interface OnCarreraClickListener {
        void onCarreraClickName(Carrera carrera, int position);

        void onCarreraClickMenu(Carrera carrera, int position);

        void onCarreraClickStudents(Carrera carrera, int position);
    }


    private OnCarreraClickListener onCarreraClickListener;

    public void setOnCarreraClickListener(OnCarreraClickListener onCarreraClickListener) {
        this.onCarreraClickListener = onCarreraClickListener;
    }
}
