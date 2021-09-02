package com.app.alarmavecinal.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.alarmavecinal.Estructuras.Avisos;
import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.R;

import java.util.ArrayList;

public class AdapterAvisos extends RecyclerView.Adapter<AdapterAvisos.AvisosViewHolder> {
    ArrayList<Avisos> avisos;
    AdapterAvisos.RecyclerItemClick recyclerItemClick;
    Funciones funciones=null;
    public AdapterAvisos(ArrayList<Avisos> avisos, AdapterAvisos.RecyclerItemClick recyclerItemClick) {
        this.avisos=avisos;
        this.recyclerItemClick=recyclerItemClick;
        funciones=new Funciones(null);

    }

    @NonNull
    @Override
    public AdapterAvisos.AvisosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_aviso, parent,false);
        return new AvisosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AvisosViewHolder holder, int position) {
        final Avisos item=avisos.get(position);
        holder.titulo.setText(item.getTitulo());

        holder.resumen.setText(item.getMensaje());
        holder.fecha.setText(item.getFecha());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerItemClick.itemClick(item);
            }
        });

    }

    @Override
    public int getItemCount() {
        return avisos.size();
    }

    public class AvisosViewHolder extends RecyclerView.ViewHolder {
        TextView titulo;
        TextView resumen;
        TextView fecha;
        public AvisosViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo= itemView.findViewById(R.id.titulo);
            resumen= itemView.findViewById(R.id.resumen);
            fecha= itemView.findViewById(R.id.fecha);
        }
    }

    public interface RecyclerItemClick{
        void itemClick(Avisos avisos);
    }
}
