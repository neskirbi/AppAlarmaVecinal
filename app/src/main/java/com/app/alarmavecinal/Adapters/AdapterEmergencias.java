package com.app.alarmavecinal.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.alarmavecinal.Estructuras.Emergencias;
import com.app.alarmavecinal.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdapterEmergencias extends RecyclerView.Adapter<AdapterEmergencias.EmergenciasViewHolder> {

    ArrayList<Emergencias> emergencias;
    RecyclerItemClick recyclerItemClick;

    public AdapterEmergencias(ArrayList<Emergencias> emergencias, RecyclerItemClick recyclerItemClick) {
        this.emergencias = emergencias;
        this.recyclerItemClick = recyclerItemClick;
    }

    @NonNull
    @Override
    public AdapterEmergencias.EmergenciasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_emergencia, parent,false);
        return new EmergenciasViewHolder(view);
    }
    int row_index=0;
    @Override
    public void onBindViewHolder(@NonNull final AdapterEmergencias.EmergenciasViewHolder holder, final int position) {
        final Emergencias item=emergencias.get(position);
        holder.nombre.setText(item.getNombre());
        try {
            JSONObject ubicacion=new JSONObject(item.getUbicacion());
            holder.direccion.setText(ubicacion.getString("direccion"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.fecha.setText(item.getFecha());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerItemClick.itemClick(item,position);

            }
        });


    }

    @Override
    public int getItemCount() {
        return emergencias.size();
    }

    class EmergenciasViewHolder extends RecyclerView.ViewHolder{
        TextView nombre;
        TextView direccion;
        TextView fecha;
        public EmergenciasViewHolder( View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombre);
            direccion = itemView.findViewById(R.id.direccion);
            fecha = itemView.findViewById(R.id.fecha);

        }
    }

    public interface RecyclerItemClick{
        void itemClick(Emergencias emergencias, int position);
    }

}
