package com.darwindeveloper.mrteacher.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.darwindeveloper.mrteacher.R;
import com.darwindeveloper.mrteacher.tablas.InstitucionEducativa;

import java.util.ArrayList;

/**
 * @author Darwin Morocho
 */

public class InstitucionEducativaAdapter extends RecyclerView.Adapter<InstitucionEducativaAdapter.IEViewHolder> {

    private Context context;
    private ArrayList<InstitucionEducativa> instituciones;

    public InstitucionEducativaAdapter(Context context, ArrayList<InstitucionEducativa> instituciones) {
        this.context = context;
        this.instituciones = instituciones;
    }

    @Override
    public IEViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_universidad, parent, false);
        return new IEViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IEViewHolder holder, final int position) {
        final InstitucionEducativa institucionEducativa = instituciones.get(position);
        holder.textViewNombre.setText(institucionEducativa.getNombre());
        holder.textViewSiglas.setText(institucionEducativa.getSiglas());

        holder.item_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onIEClickListener.onIEClick(institucionEducativa, position);
            }
        });

        holder.item_content.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onIEClickListener.onIELongClick(institucionEducativa, position);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return instituciones.size();
    }

    class IEViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNombre, textViewSiglas;
        LinearLayout item_content;

        public IEViewHolder(View itemView) {
            super(itemView);
            textViewNombre = (TextView) itemView.findViewById(R.id.textViewNombre);
            textViewSiglas = (TextView) itemView.findViewById(R.id.textViewSiglas);
            item_content = (LinearLayout) itemView.findViewById(R.id.item_content);
        }


    }

    public interface OnIEClickListener {
        void onIEClick(InstitucionEducativa institucionEducativa, int position);

        void onIELongClick(InstitucionEducativa institucionEducativa, int position);
    }


    private OnIEClickListener onIEClickListener;

    public void setOnIEClickListener(OnIEClickListener onIEClickListener) {
        this.onIEClickListener = onIEClickListener;
    }
}
