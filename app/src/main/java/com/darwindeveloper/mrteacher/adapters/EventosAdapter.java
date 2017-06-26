package com.darwindeveloper.mrteacher.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.darwindeveloper.mrteacher.R;
import com.darwindeveloper.mrteacher.tablas.Evento;

import java.util.ArrayList;

/**
 * Created by Darwin Morocho on 16/3/2017.
 */

public class EventosAdapter extends RecyclerView.Adapter<EventosAdapter.EventosViewHolder> {


    private Context context;
    private ArrayList<Evento> eventos;

    public EventosAdapter(Context context, ArrayList<Evento> eventos) {
        this.context = context;
        this.eventos = eventos;
    }

    @Override
    public EventosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_evento, parent, false);
        return new EventosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventosViewHolder holder, final int position) {
        final Evento evento = eventos.get(position);

        holder.title.setText(evento.getNombre());
        holder.date.setText(evento.getFecha());

        holder.btn_see.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEventClickListener.onEventWatch(evento, position);
            }
        });

        holder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEventClickListener.onEventEdit(evento, position);
            }
        });


        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEventClickListener.onEventDelete(evento, position);
            }
        });

        holder.btn_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEventClickListener.onEventCalendar(evento);
            }
        });


    }

    @Override
    public int getItemCount() {
        return eventos.size();
    }

    public class EventosViewHolder extends RecyclerView.ViewHolder {

        ImageButton btn_event, btn_alarm;
        Button btn_see, btn_edit, btn_delete;
        TextView title, date;

        public EventosViewHolder(View itemView) {
            super(itemView);

            btn_alarm = (ImageButton) itemView.findViewById(R.id.btn_alarm);
            btn_event = (ImageButton) itemView.findViewById(R.id.btn_event);
            btn_see = (Button) itemView.findViewById(R.id.buttonSee);
            btn_edit = (Button) itemView.findViewById(R.id.buttonEdit);
            btn_delete = (Button) itemView.findViewById(R.id.buttonDelete);
            title = (TextView) itemView.findViewById(R.id.textViewTitle);
            date = (TextView) itemView.findViewById(R.id.textViewDate);

        }
    }


    public interface OnEventClickListener {
        void onEventWatch(Evento evento, int position);

        void onEventEdit(Evento evento, int position);

        void onEventDelete(Evento evento, int position);

        void onEventCalendar(Evento evento);
    }

    private OnEventClickListener onEventClickListener;

    public void setOnEventClickListener(OnEventClickListener onEventClickListener) {
        this.onEventClickListener = onEventClickListener;
    }
}
