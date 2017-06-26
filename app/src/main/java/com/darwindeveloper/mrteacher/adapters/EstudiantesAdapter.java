package com.darwindeveloper.mrteacher.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.darwindeveloper.mrteacher.R;
import com.darwindeveloper.mrteacher.tablas.Estudiante;

import java.util.ArrayList;


/**
 * Created by Darwin Morocho on 16/3/2017.
 */

public class EstudiantesAdapter extends RecyclerView.Adapter<EstudiantesAdapter.EViewHolder> {

    private boolean showCheckboxes;

    private Context context;
    private ArrayList<Estudiante> estudiantes;

    public EstudiantesAdapter(Context context, ArrayList<Estudiante> estudiantes) {
        this.context = context;
        this.estudiantes = estudiantes;
    }

    public boolean isShowCheckboxes() {
        return showCheckboxes;
    }

    public void setShowCheckboxes(boolean showCheckboxes) {
        this.showCheckboxes = showCheckboxes;
    }

    @Override
    public EViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_student, parent, false);
        return new EViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final EViewHolder holder, final int position) {
        final Estudiante estudiante = estudiantes.get(position);

        holder.letra.setText(String.valueOf(estudiante.getApellidos().charAt(0)));
        holder.cedula.setText(estudiante.getId());
        holder.nombre.setText(estudiante.getApellidos() + " " + estudiante.getNombres());


        if (estudiante.isSelected() && showCheckboxes) {
            holder.item.setBackgroundColor(Color.parseColor("#d2d2d2"));
        } else {
            holder.item.setBackgroundColor(Color.parseColor("#ffffff"));
        }

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEstudiantesClickListener.onEstudianteItemClick(estudiante, position);
            }
        });


        holder.item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onEstudiantesClickListener.onEstudianteItemLongClick(estudiante, position);
                return false;
            }
        });


        holder.layout_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEstudiantesClickListener.onEstudianteItemClick(estudiante, position);
            }
        });


        holder.layout_text.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onEstudiantesClickListener.onEstudianteItemLongClick(estudiante, position);
                return false;
            }
        });

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEstudiantesClickListener.onEstudianteItemClickMore(v, estudiante, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return estudiantes.size();
    }


    public class EViewHolder extends RecyclerView.ViewHolder {


        TextView nombre, cedula, letra;
        ImageButton more;
        LinearLayout item, layout_text;

        public EViewHolder(View itemView) {
            super(itemView);

            nombre = (TextView) itemView.findViewById(R.id.textViewNombre);
            cedula = (TextView) itemView.findViewById(R.id.textViewID);
            letra = (TextView) itemView.findViewById(R.id.textViewLetra);
            more = (ImageButton) itemView.findViewById(R.id.more);
            item = (LinearLayout) itemView.findViewById(R.id.item_content);
            layout_text = (LinearLayout) itemView.findViewById(R.id.layout_text);
        }
    }


    public interface OnEstudiantesClickListener {
        /**
         * para cuando se de quick en el nombre de un estudiante
         *
         * @param estudiante
         * @param position
         */
        void onEstudianteItemClick(Estudiante estudiante, int position);


        /**
         * para cuando se de click prolongado
         *
         * @param estudiante
         * @param position
         */
        void onEstudianteItemLongClick(Estudiante estudiante, int position);

        /**
         * para cuando se de click en el boton de mas opciones
         *
         * @param view
         * @param estudiante
         * @param position
         */
        void onEstudianteItemClickMore(View view, Estudiante estudiante, int position);


    }


    private OnEstudiantesClickListener onEstudiantesClickListener;

    public void setOnEstudiantesClickListener(OnEstudiantesClickListener onEstudiantesClickListener) {
        this.onEstudiantesClickListener = onEstudiantesClickListener;
    }
}
