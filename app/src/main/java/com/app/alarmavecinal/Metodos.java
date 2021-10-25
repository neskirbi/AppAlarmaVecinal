package com.app.alarmavecinal;

import static android.content.Context.ACTIVITY_SERVICE;

import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;

import com.app.alarmavecinal.Models.Grupo;
import com.app.alarmavecinal.Models.Usuario;
import com.app.alarmavecinal.Servicios.Emergencia;
import com.app.alarmavecinal.Servicios.Notificador;
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
        Log.i("URL",URL);
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
            Log.i("Login","id_usuario: "+ToUtf8(id_usuario));
            Log.i("Login","nombre: "+ToUtf8(nombre));
            Log.i("Login","direccion: "+ToUtf8(direccion));
            login.put("id_usuario", ToUtf8(id_usuario));
            login.put("nombre",ToUtf8(nombre));
            login.put("direccion",ToUtf8(direccion));


            db.insert("login", null, login);

            db.close();

        } catch (Exception e) {
            Log.i("login",e.getMessage());
        }

    }

    public String ToUtf8(String texto){
        if(texto==null)
            return"";

        byte[] bt = texto.getBytes();
        return new String(bt, Charset.forName("UTF-8"));

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public void GuardarGrupoLogin(Usuario usuario) {
        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();

            String id_usuario=usuario.getId_usuario();
            String id_grupo=usuario.getId_grupo();
            String nombre=usuario.getNombre();
            int enviado=1;
            ContentValues grupo = new ContentValues();

            Log.i("Login","id_grupo: "+ToUtf8(id_grupo));
            Log.i("Login","id_usuario: "+ToUtf8(id_usuario));
            Log.i("Login","nombre: "+ToUtf8(nombre));
            Log.i("Login","enviado: "+enviado+"");

            grupo.put("id_grupo", ToUtf8(id_grupo));
            grupo.put("id_usuario", ToUtf8(id_usuario));
            grupo.put("nombre",ToUtf8(nombre));
            grupo.put("enviado",enviado);


            db.insert("grupo", null, grupo);

            db.close();

        } catch (Exception e) {
            Log.i("login",e.getMessage());
        }
    }

    public String GetIdUsuario() {
        String id_usuario="";
        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();

            Cursor c =  db.rawQuery("SELECT * from login ",null);
            c.moveToFirst();
            if(c.getCount()>0){
                c.moveToFirst();

                id_usuario=c.getString(c.getColumnIndex("id_usuario"));

            }
            c.close();
            db.close();
        }catch (Exception e){}

        return id_usuario;
    }

    public void GuardarGrupoCreado(Grupo grupo) {
        Base base = new Base(context);
        SQLiteDatabase db = base.getWritableDatabase();

        String id_usuario=GetIdUsuario();

        ContentValues grupos = new ContentValues();

        grupos.put("id_grupo", grupo.getId_grupo());
        grupos.put("id_usuario",grupo.getId_usuario() );
        grupos.put("nombre", ToUtf8(grupo.getNombre() ));
        grupos.put("enviado", 1);


        db.insert("grupo", null, grupos);
        db.close();
    }

    public String GetIdGrupo() {
        String id_grupo="";
        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();

            Cursor c =  db.rawQuery("SELECT * from grupo ",null);
            if(c.getCount()>0){
                c.moveToFirst();

                id_grupo=c.getString(c.getColumnIndex("id_grupo")).replace(" ","");

            }
            c.close();
            db.close();
        }catch (Exception e){}




        return id_grupo;


    }

    public void SalirGrupo() {

        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();

            db.execSQL("DELETE from grupo ");
            db.close();
        }catch (Exception e){}

    }

    public void VerificarServicios(){
        if (GetIdGrupo().replace(" ", "").length() == 32) {
            IniciarServicioEmergencias();
            IniciarServicioNotificador();
        }else{
            DetenerServicioEmergencias();
            DetenerServicioNotificador();
        }

    }

    private void IniciarServicioEmergencias() {
        if (!isMyServiceRunning(Emergencia.class, context)) {

            Intent service1 = new Intent(context, Emergencia.class);
            service1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(service1);
            }
            context.startService(service1);


        }
    }

    public void IniciarServicioNotificador(){
        if (!isMyServiceRunning(Notificador.class, context) && Check_Log()) {
            Intent service3 = new Intent(context, Notificador.class);
            service3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(service3);
            }
            context.startService(service3);


        }
    }

    private void DetenerServicioEmergencias() {
        context.stopService(new Intent(context.getApplicationContext(), Emergencia.class));
    }

    private void DetenerServicioNotificador() {
        context.stopService(new Intent(context.getApplicationContext(), Emergencia.class));
    }

    public boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public boolean Check_Log() {

        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();

            String nombre="";

            Cursor c =  db.rawQuery("SELECT * from login ",null);
            c.moveToFirst();
            int cont=c.getCount();
            c.close();
            db.close();

            if(cont>0)
            {
                return true;
            }
        }catch (Exception e){}


        return false;


    }
}
