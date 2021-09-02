package com.app.alarmavecinal;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Vibrator;
import android.util.Log;

import com.app.alarmavecinal.Models.Usuario;
import com.app.alarmavecinal.Sqlite.Base;
import com.app.alarmavecinal.Usuario.Registro.RegistroView;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Metodos {
    Context context;
    public Metodos(Context context) {
        this.context = context;
    }
    public String GetUrl(){
        String URL="";
        if(BuildConfig.DEBUG){

            URL=context.getString(R.string.url_debug);
        }else{
            URL=context.getString(R.string.url);
        }
        return URL;
    }

    public void Vibrar(long[] pattern) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milisegundos
        //pattern = { 0, milli};
        v.vibrate(pattern, -1);
    }

    public long[] VibrarPush() {
        long[] pattern = {0, 70};
        return pattern;
    }

    public void CreaLogin(Usuario usuario) {

        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();

            String id_usuario=usuario.getId_usuario();
            String nombre=usuario.getNombres()+" "+ usuario.getApellidos();
            String direccion=usuario.getDireccion();
            ContentValues login = new ContentValues();
            Log.i("Login",toutf8(id_usuario));
            Log.i("Login",toutf8(nombre));
            Log.i("Login",toutf8(direccion));
            login.put("id_usuario", toutf8(id_usuario));
            login.put("nombre",toutf8(nombre));
            login.put("direccion",toutf8(direccion));


            db.insert("login", null, login);

            db.close();

        } catch (Exception e) {
            Log.i("login",e.getMessage());
        }

    }

    public String toutf8(String texto){
        byte[] bt = texto.getBytes();

        return new String(bt, Charset.forName("UTF-8"));

    }
}
