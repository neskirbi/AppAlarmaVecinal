package com.app.alarmavecinal.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.alarmavecinal.Estructuras.Alertas;
import com.app.alarmavecinal.Estructuras.AlertasL;
import com.app.alarmavecinal.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterAlertasLista extends RecyclerView.Adapter<AdapterAlertasLista.AlertasViewHolder>{
    ArrayList<AlertasL> alertasre;
    AdapterAlertasLista.RecyclerItemClick recyclerItemClick;

    public AdapterAlertasLista(ArrayList<AlertasL> alertasre, AdapterAlertasLista.RecyclerItemClick recyclerItemClick) {
        this.alertasre=alertasre;
        this.recyclerItemClick=recyclerItemClick;
    }

    @Override
    public AdapterAlertasLista.AlertasViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_alerta, parent,false);
        return new AdapterAlertasLista.AlertasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdapterAlertasLista.AlertasViewHolder holder, int position) {
        final AlertasL item=alertasre.get(position);
        if(item.getImagen().replace(" ","").length()>0)
        Picasso.with((Context) recyclerItemClick).load(item.getImagen()).into(holder.imagen);

        holder.titulo.setText(item.getTitulo());
        holder.nombre.setText(item.getNombre());
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
        return alertasre.size();
    }

    class AlertasViewHolder extends RecyclerView.ViewHolder{
        ImageView imagen;
        TextView titulo;
        TextView nombre;
        TextView fecha;
        public AlertasViewHolder( View itemView) {
            super(itemView);
            imagen = itemView.findViewById(R.id.imagen);
            titulo = itemView.findViewById(R.id.titulo);
            nombre = itemView.findViewById(R.id.nombre);
            fecha = itemView.findViewById(R.id.fecha);

        }
    }

    public interface RecyclerItemClick{
        void itemClick(AlertasL alertas);
    }



}
