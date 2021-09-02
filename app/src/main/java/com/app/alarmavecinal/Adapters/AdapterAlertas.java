package com.app.alarmavecinal.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.alarmavecinal.Estructuras.Alertas;
import com.app.alarmavecinal.FuncionAlertas.NewAlerta;
import com.app.alarmavecinal.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterAlertas extends RecyclerView.Adapter<AdapterAlertas.AlertasViewHolder>{
    ArrayList<Alertas> alertasre;
    RecyclerItemClick recyclerItemClick;

    public AdapterAlertas(ArrayList<Alertas> alertasre,RecyclerItemClick recyclerItemClick) {
        this.alertasre=alertasre;
        this.recyclerItemClick=recyclerItemClick;
    }

    @Override
    public AdapterAlertas.AlertasViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.opciones_alertas, null);
        return new AlertasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdapterAlertas.AlertasViewHolder holder, int position) {
        final Alertas item=alertasre.get(position);
        if(item.getImagen().replace(" ","").length()>0)
        Picasso.with((Context) recyclerItemClick).load(item.getImagen()).into(holder.imagen);
        holder.titulo.setText(item.getTitulo());
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
        public AlertasViewHolder( View itemView) {
            super(itemView);
            imagen = itemView.findViewById(R.id.imagen);
            titulo = itemView.findViewById(R.id.titulo);

        }
    }

    public interface RecyclerItemClick{
        void itemClick(Alertas alertas);
    }



}
