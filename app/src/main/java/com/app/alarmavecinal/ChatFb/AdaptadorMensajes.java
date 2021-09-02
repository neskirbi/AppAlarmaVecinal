package com.app.alarmavecinal.ChatFb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.app.alarmavecinal.Estructuras.Mensaje;
import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.R;

import java.util.ArrayList;

public class AdaptadorMensajes extends ArrayAdapter <Mensaje>{

    private int resource=0;
    private final Context context;
    Funciones funciones;
    ArrayList<Mensaje> mensaje = new ArrayList();

    public AdaptadorMensajes(@NonNull Context context, ArrayList<Mensaje> mensaje) {
        super(context, R.layout.dialogo1);
        this.context=context;
        this.mensaje=mensaje;
        funciones=new Funciones(context);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        funciones.Logo("fbmensaje","----:"+mensaje.get(position).getMensaje());
        if(mensaje.get(position).getAudio().length()!=0){
            resource=R.layout.dialogo1;
            View item = LayoutInflater.from(context).inflate(resource,null);

            TextView textView1 = item.findViewById(R.id.nombre);
            textView1.setText(mensaje.get(position).getNombre());

            TextView textView2 = item.findViewById(R.id.mensaje);
            textView2.setText(mensaje.get(position).getMensaje());

            TextView textView3 = item.findViewById(R.id.hora);
            textView3.setText(mensaje.get(position).getFecha());
            funciones.Logo("fbmensaje",":"+mensaje.get(position).getMensaje());


            return(item);
        }else{
            resource=R.layout.dialogo2;
            View item = LayoutInflater.from(context).inflate(resource,null);

            TextView textView1 = item.findViewById(R.id.nombre);
            textView1.setText(mensaje.get(position).getNombre());

            TextView textView2 = item.findViewById(R.id.mensaje);
            textView2.setText(mensaje.get(position).getMensaje());
            funciones.Logo("fbmensaje",":"+mensaje.get(position).getMensaje());
            return(item);
        }



    }



}
