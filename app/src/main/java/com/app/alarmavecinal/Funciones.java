package com.app.alarmavecinal;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.app.alarmavecinal.Grupos.Grupo;
import com.app.alarmavecinal.LoginPack.Login;
import com.app.alarmavecinal.Servicios.Emergencia;
import com.app.alarmavecinal.Servicios.Notificador;
import com.app.alarmavecinal.Sqlite.Base;
import com.google.firebase.iid.FirebaseInstanceId;

import net.glxn.qrgen.android.QRCode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.graphics.Color.rgb;
import static java.lang.StrictMath.abs;

public class Funciones {
    Context context;
    MediaPlayer mPlayer = new  MediaPlayer();
    private double porcentaje=0.20;

    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;

    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);

    public Funciones(Context context) {
        this.context = context;
        //verificando envios cada que se carga una activity


    }

    public void VerificaConexion(){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // Si hay conexión a Internet en este momento

        } else {
            // No hay conexión a Internet en este momento
            Toast.makeText(context,  "Verificar conexión internet", Toast.LENGTH_SHORT).show();
        }
    }

    public String GetUIID(){
        return UUID.randomUUID().toString().replace("-","");
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

    public void AbrirConexion(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public String Conexion(String data, String url,String metodo) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String result = "[]";


        try {

            Logo("Conexion","Enviando: " + url + "    " + data);

            //Create a URL object holding our url
            URL myUrl = new URL(url);
            //Create a connection
            HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
            //Set methods and timeouts
            connection.setRequestMethod(metodo);
            connection.setReadTimeout(60000);
            connection.setConnectTimeout(60000);
            //Logo("Conexion",connection.getContentLength()+"");

            //connection.addRequestProperty("pr","33");

            //Connect to our url
            connection.connect();



            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write("data=" + data);
            writer.flush();
            writer.close();
            os.close();


            if (connection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                //_is = connection.getResponseCode();
                Logo("Conexion",url+"   getResponseCode: " +connection.getResponseCode());
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());

                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();

                //stringBuilder.append(data);

                //Check if the line we are reading is not null
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();
                Logo("Conexion", "Recibiendo: " + result);
                return result;

            } else {
                Logo("Conexion",url+"   getResponseCode: " +connection.getResponseCode());
                return result;
            }


        } catch (Exception ee) {
            Logo("Conexion", "Error_conexion: " + ee.getMessage()+"------->"+ee.getCause()+"--->"+ee.getLocalizedMessage());
            return "[]";
        }


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


    public long[] VibrarError() {
        long[] pattern = {0, 100, 70, 100};
        return pattern;
    }


    public void Notificar(String title, String body, int icono,Intent intent, int id) {



        intent.putExtra("NoreiniciarServicio", 1);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //
        String NOTIFICATION_CHANNEL_ID = "Emergencia";

        RemoteViews myRemoteView = new RemoteViews(context.getPackageName(), R.layout.imagen_notification);

        Bitmap bit= BitmapFactory.decodeResource(context.getResources(),icono);
        myRemoteView.setImageViewBitmap(R.id.icono,bit);
        myRemoteView.setTextViewText(R.id.noti_titulo, title);
        myRemoteView.setTextViewText(R.id.noti_body, body);


        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        notificationBuilder
                .setSmallIcon(R.drawable.logo)
                .setAutoCancel(false)
                .setVibrate(new long[]{1000, 1000, 500, 1000})
                .setSound(defaultSoundUri)
                .setPriority(1)
                .setContentIntent(pendingIntent)
                .setContentInfo("info")
                .setContent(myRemoteView);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);

            Uri sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


            notificationChannel.setDescription("Descripcion");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{1000, 1000, 500, 1000});
            notificationChannel.enableLights(true);
            notificationBuilder.setSound(sonido);
            notificationBuilder.setPriority(1);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(id, notificationBuilder.build());
    }


    public boolean Check_Log() {

        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();

            Logo("Checklog","mostrando...");
            String nombre="";

            Cursor c =  db.rawQuery("SELECT * from login ",null);
            c.moveToFirst();
            int cont=c.getCount();
            c.close();
            db.close();
            Logo("Checklog","-----"+cont);

            if(cont>0)
            {
                return true;
            }
        }catch (Exception e){}


        return false;


    }


    public void LogOut() {

        BorrarLogin();
        BorrarGrupo();
        StopServiceAlertas();

        context.startActivity(new Intent(context.getApplicationContext(), Login.class));



    }
    public void StopServiceAlertas(){

        DetenerServicioEmergencias();
        DetenerServicioNotificador();
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

    private void DetenerServicioEmergencias() {
        context.stopService(new Intent(context.getApplicationContext(), Emergencia.class));
    }

    private void DetenerServicioNotificador() {
        context.stopService(new Intent(context.getApplicationContext(), Emergencia.class));
    }

    private void IniciarServicioEmergencias() {
        if (!isMyServiceRunning(Emergencia.class, context)) {

            Intent service1 = new Intent(context, Emergencia.class);
            service1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(service1);
            }else{
                context.startService(service1);
            }

        }
    }

    public void IniciarServicioNotificador(){
        if (!isMyServiceRunning(Notificador.class, context) && Check_Log()) {
            Intent service3 = new Intent(context, Notificador.class);
            service3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(service3);
            }else{
                context.startService(service3);
            }

        }
    }





    public void BorrarLogin(){
        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();

            db.execSQL("DELETE from login ");
            db.close();
        }catch (Exception e){}



    }

    public String GetNombre() {
        String nombre="";

        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();

            Cursor c =  db.rawQuery("SELECT nombre from login ",null);
            c.moveToFirst();
            if(c.getCount()>0){
                c.moveToFirst();

                nombre=c.getString(c.getColumnIndex("nombre"));

            }
            c.close();
            db.close();
        }catch (Exception e){}



        return nombre;
    }

    public String GetDireccion() {
        String direccion="";

        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();

            Cursor c =  db.rawQuery("SELECT direccion from login ",null);
            c.moveToFirst();
            if(c.getCount()>0){
                c.moveToFirst();

                direccion=c.getString(c.getColumnIndex("direccion"));

            }
            c.close();
            db.close();
        }catch (Exception e){}



        return direccion;
    }

    public String GuardarGrupo(String id_grupo,String nombre) {
        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();

            String id_usuario=GetIdUsuario();

            ContentValues grupo = new ContentValues();

            grupo.put("id_grupo", id_grupo);
            grupo.put("id_usuario",id_usuario );
            grupo.put("nombre", nombre );
            grupo.put("enviado", 1);


            db.insert("grupo", null, grupo);
            db.close();
        }catch (Exception e){}


        return  id_grupo;
    }

    public boolean PuedoGrupo(String id_usuario,String id_grupo){
        String respuesta=Conexion("{\"id_usuario\":\""+id_usuario+"\",\"id_grupo\":\""+id_grupo+"\"}",GetUrl()+context.getString(R.string.url_GetPuedoGrupo),"POST");
        try {
            JSONArray jsonArray=new JSONArray(respuesta);
            if(jsonArray.length()>0){
                Toast.makeText(context, "Te han bloqueado de este grupo.", Toast.LENGTH_SHORT).show();
                return false;
            }else{
                return true;
            }
        } catch (JSONException e) {
            return false;
        }
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


    public String GetIdGrupo2() {
        String id_grupo="";
        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();

            Cursor c =  db.rawQuery("SELECT * from grupo where enviado='1'",null);
            if(c.getCount()>0){
                c.moveToFirst();

                id_grupo=c.getString(c.getColumnIndex("id_grupo")).replace(" ","");

            }
            c.close();
            db.close();
        }catch (Exception e){}




        return id_grupo;


    }
    public String GetNombreGrupo() {
        String nombre="";
        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();

            Cursor c =  db.rawQuery("SELECT * from grupo ",null);
            if(c.getCount()>0){
                c.moveToFirst();

                nombre=c.getString(c.getColumnIndex("nombre"));

            }
            c.close();
            db.close();
        }catch (Exception e){}




        return nombre;


    }

    public Bitmap GetQR(String texto){
        Bitmap bitmap = QRCode.from(texto).withSize(500,500).bitmap();
        return bitmap;
    }

    public Bitmap Base64ToBitmap(String string){
        byte[] decodedString = Base64.decode(string, Base64.URL_SAFE);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

    }


    public String GuardarGrupoCamara(String respons) {
        String id_grupo="";


            try {
                JSONObject jsonObject=new JSONObject(respons);
                if (!jsonObject.get("id_grupo").toString().isEmpty() ){

                    Base base = new Base(context);
                    SQLiteDatabase db = base.getWritableDatabase();

                    id_grupo=jsonObject.get("id_grupo").toString();
                    ContentValues grupo = new ContentValues();
                    grupo.put("id_grupo", jsonObject.get("id_grupo").toString());
                    grupo.put("id_usuario",jsonObject.get("id_usuario").toString());
                    grupo.put("nombre", jsonObject.get("nombre").toString() );
                    grupo.put("enviado", 1);

                    db.insert("grupo", null, grupo);

                    db.close();

                }


            } catch (JSONException e) {
                Logo("Escaner",e.getMessage());

            }


        return  id_grupo;

    }

    public void SalirGrupo() {

        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();

            db.execSQL("DELETE from grupo ");
            db.close();
        }catch (Exception e){}

    }

    public void BorrarGrupo() {

        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();

            db.execSQL("DELETE from grupo ");
            db.close();
        }catch (Exception e){}

    }

    public ProgressDialog Wait(String mensaje){
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setIcon(R.mipmap.ic_launcher);
        progressDialog.setMessage(mensaje);
        return progressDialog;
    }

    public void EnviarGrupo() {
        Logo("Enviador","Enviando...");

        String id_grupo="",id_usuario="",nombre="",response="[]";

        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();

            Cursor c =  db.rawQuery("SELECT * from grupo where enviado='0' ",null);
            if(c.getCount()>0){
                c.moveToFirst();

                id_grupo=c.getString(c.getColumnIndex("id_grupo"));
                id_usuario=c.getString(c.getColumnIndex("id_usuario"));
                nombre=c.getString(c.getColumnIndex("nombre"));

                String data="{\"id_grupo\":\""+id_grupo+"\",\"id_usuario\":\""+id_usuario+"\",\"nombre\":\""+nombre+"\"}";
                String url=GetUrl()+context.getString(R.string.url_SetGrupo);
                response=Conexion(data,url,"POST");

                if(response.length()>0){
                    JSONObject jsonObject=new JSONObject(response);
                    if(jsonObject.get("respuesta").toString().contains("1")){
                        db.execSQL("UPDATE grupo SET enviado=1 WHERE id_grupo='"+id_grupo+"' ");
                    }
                }


            }
            c.close();
            db.close();
        } catch (JSONException e) {
            e.printStackTrace();
        }




    }

    public void ActualizarGrupoEnviado(){
        Logo("Enviador","Enviando...");

        Base base = new Base(context);
        SQLiteDatabase db = base.getWritableDatabase();

        db.execSQL("UPDATE grupo SET enviado=1 WHERE id_grupo='"+GetIdGrupo()+"' ");

        db.close();



    }
    public boolean IsGrupoEnviado() {
        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();

            Cursor c =  db.rawQuery("SELECT * from grupo where enviado='1' ",null);
            if(c.getCount()>0){
                return true;

            }else{

            }
            c.close();
            db.close();
        } catch (Exception e) {
        }


        return  false;

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

    public boolean RevisarIdMensaje(String id_mensaje) {
        boolean bandera=false;

        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();

            Cursor c =  db.rawQuery("SELECT * from chat_notifica where id_mensaje='"+id_mensaje+"' ",null);
            c.moveToFirst();
            if(c.getCount()>0){
                bandera = true;
            }
            c.close();
            db.close();
        }catch (Exception e){}

        return bandera;
    }

    public boolean RevisarIdAviso(String id_aviso) {
        boolean bandera=false;

        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();

            Cursor c =  db.rawQuery("SELECT * from aviso_notifica where id_aviso='"+id_aviso+"' ",null);
            c.moveToFirst();
            if(c.getCount()>0){
                bandera = true;
            }
            c.close();
            db.close();
        }catch (Exception e){}

        return bandera;
    }

    public boolean RevisarIdAlerta(String id_alerta) {
        boolean bandera=false;

        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();

            Cursor c =  db.rawQuery("SELECT * from alerta_notifica where id_alerta='"+id_alerta+"' ",null);
            c.moveToFirst();
            if(c.getCount()>0){
                bandera = true;
            }
            c.close();
            db.close();
        }catch (Exception e){}

        return bandera;
    }

    public void GuardarIdAviso(String id_aviso) {
        String UUID="";
        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();

            ContentValues aviso_notifica = new ContentValues();

            aviso_notifica.put("id_aviso", id_aviso );


            db.insert("aviso_notifica", null, aviso_notifica);
            db.close();
        }catch (Exception e){}


    }

    public void GuardarIdAlerta(String id_alerta) {
        String UUID="";
        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();

            ContentValues alerta_notifica = new ContentValues();

            alerta_notifica.put("id_alerta", id_alerta );


            db.insert("alerta_notifica", null, alerta_notifica);
            db.close();
        }catch (Exception e){}


    }

    public void GuardarIdMensaje(String id_mensaje) {
        String UUID="";
        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();

            ContentValues chat_notifica = new ContentValues();

            chat_notifica.put("id_mensaje", id_mensaje );


            db.insert("chat_notifica", null, chat_notifica);
            db.close();
        }catch (Exception e){}


    }


    public String GetIdUsuarioGrupo() {
        String id_usuario="";
        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();

            Cursor c =  db.rawQuery("SELECT * from grupo ",null);
            if(c.getCount()>0){
                c.moveToFirst();

                id_usuario=c.getString(c.getColumnIndex("id_usuario"));

            }
            c.close();
            db.close();
        }catch (Exception e){}




        return id_usuario;
    }

    public String GetIdAlerta() {
        String id_alerta="";
        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();

            Cursor c =  db.rawQuery("SELECT * from alertas ",null);
            if(c.getCount()>0){
                c.moveToFirst();

                id_alerta=c.getString(c.getColumnIndex("id_alerta"));

            }
            c.close();
            db.close();


        }catch (Exception e){}


        return id_alerta;
    }


    public String GetIdAviso() {
        String id_aviso="";
        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();

            Cursor c =  db.rawQuery("SELECT * from avisos ",null);
            if(c.getCount()>0){
                c.moveToFirst();

                id_aviso=c.getString(c.getColumnIndex("id_aviso"));

            }
            c.close();
            db.close();
        }catch (Exception e){}




        return id_aviso;
    }


    public void CreaLogin(JSONObject jsonObject) {

        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();

            String id_usuario=jsonObject.get("id_usuario").toString();
            String nombre=jsonObject.get("nombres").toString()+" "+jsonObject.get("apellidos").toString();
            String direccion=jsonObject.get("direccion").toString();
            ContentValues login = new ContentValues();
            login.put("id_usuario", id_usuario);
            login.put("nombre",nombre);
            login.put("direccion",direccion);


            db.insert("login", null, login);

            db.close();

        } catch (Exception e) {
            Logo("login",e.getMessage());
        }

    }

    public Boolean GetAlertas(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            if(!GetIdAlerta().contains(jsonObject.get("id_alerta").toString())){
                Base base = new Base(context);
                SQLiteDatabase db = base.getWritableDatabase();

                if(GetIdAlerta()==""){
                    ContentValues alerta = new ContentValues();

                    alerta.put("id_alerta", jsonObject.get("id_alerta").toString());


                    db.insert("alertas", null, alerta);
                }else{
                    db.execSQL("UPDATE alertas SET id_alerta='"+jsonObject.get("id_alerta").toString()+"' " );
                }
                db.close();
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }


    public Boolean GetAvisos(String json){
        try{
            JSONObject jsonObject=new JSONObject(json);
            if(!GetIdAviso().contains(jsonObject.get("id_aviso").toString())) {
                Base base = new Base(context);
                SQLiteDatabase db = base.getWritableDatabase();

                if (GetIdAviso() == "") {
                    ContentValues avisos = new ContentValues();

                    avisos.put("id_aviso", jsonObject.get("id_aviso").toString());


                    db.insert("avisos", null, avisos);
                } else {
                    db.execSQL("UPDATE avisos SET id_aviso='" + jsonObject.get("id_aviso").toString() + "' ");
                }
                db.close();
                return true;
            }
        }catch (Exception e){
            Logo("Error",e.toString());



        }


        return false;
    }
    public void Logo(String tag,String men)
    {
        if(BuildConfig.DEBUG){
            Log.i(tag,men);
        }
    }

    public boolean GetChat() {
        boolean retorno=false;
        Base base = new Base(context);
        SQLiteDatabase db = base.getWritableDatabase();
        String url=GetUrl()+context.getString(R.string.url_GetChat),respuesta="";
        respuesta=Conexion("{\"id_grupo\":\""+GetIdGrupo()+"\",\"fecha\":\""+GetFechaChat()+"\"}",url,"POST");
        try {
            JSONArray chat_jsona=new JSONArray(respuesta);
            JSONObject chat_json;
            for (int i=0;i<chat_jsona.length();i++){
                chat_json=new JSONObject(chat_jsona.get(i).toString());

                ContentValues chat_in = new ContentValues();
                chat_in.put("id_chat", chat_json.get("id_chat").toString());
                chat_in.put("id_usuario", chat_json.get("id_usuario").toString());
                chat_in.put("nombre", chat_json.get("nombre").toString());

                if(chat_json.get("tipo").toString().contains("1")){
                    chat_in.put("mensaje",  "{\"mensaje\":\""+chat_json.get("mensaje").toString()+"\",\"hora\":\""+chat_json.get("hora").toString()+"\"} ");
                }else{
                    //Se guarda el audio y la direcion en mensaje si es tipo 2
                    chat_in.put("mensaje", "{\"mensaje\":\""+ToFile(chat_json.get("mensaje").toString(),chat_json.get("id_chat").toString(),".mp3")+"\",\"hora\":\""+chat_json.get("hora").toString()+"\"} ");
                }

                chat_in.put("tipo", chat_json.get("tipo").toString());
                chat_in.put("fecha", chat_json.get("fecha").toString());
                db.insert("chat_in", null, chat_in);
                retorno=true;
            }




        }catch (Exception e){
            Log.i("chatee",e.getMessage());
        }
        db.close();
        return retorno;
    }

    private String GetFechaChat() {
        String fecha="";
        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();

            Cursor c =  db.rawQuery("SELECT * from chat_in order by fecha desc ",null);
            c.moveToFirst();
            if(c.getCount()>0){
                c.moveToFirst();

                fecha=c.getString(c.getColumnIndex("fecha"));

            }else{
                fecha="2020-01-01 00:00:00";
            }
            c.close();
            db.close();
        }catch (Exception e){}

        return fecha;
    }


    public void GuardarChat(String mensaje,String tipo) {
        String UUID=GetUIID();
        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();
            ContentValues chat = new ContentValues();

            chat.put("id_chat", UUID);
            chat.put("mensaje", "{\"id_chat\":\""+UUID+"\",\"id_grupo\":\""+GetIdGrupo()+"\",\"id_usuario\":\""+GetIdUsuario()+"\",\"mensaje\":\""+mensaje+"\",\"tipo\":\""+tipo+"\"}" );
            chat.put("enviado", 0);


            db.insert("chat", null, chat);
            db.close();
        }catch (Exception e){
            Log.i("chatee",e.getMessage());
        }


    }

    public void SetChat() {
        String mensaje="",response="",id_chat="";
        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();

            Cursor c =  db.rawQuery("SELECT * from chat where enviado='0' ",null);
            if(c.getCount()>0){
                c.moveToFirst();

                id_chat=c.getString(c.getColumnIndex("id_chat"));
                mensaje=c.getString(c.getColumnIndex("mensaje"));


                String url=GetUrl()+context.getString(R.string.url_SetChat);
                response=Conexion(mensaje,url,"POST");

                JSONObject jsonObject=new JSONObject(response);
                if(jsonObject.get("respuesta").toString().contains("1")){
                    db.execSQL("UPDATE chat SET enviado=1 WHERE id_chat='"+id_chat+"' ");
                }
            }
            c.close();
            db.close();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    void SinGrupo(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Sin Grupo");
        alertDialog.setMessage("Primero debes estar en un grupo, ¿Deseas unirte?");



        alertDialog.setIcon(R.drawable.boton_alerta);

        alertDialog.setPositiveButton("Unirse",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        context.startActivity(new Intent(context, Grupo.class));

                    }
                });

        alertDialog.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }


    public String ToString(String str){
        byte[] data = Base64.decode(str, Base64.DEFAULT);
        try {
            str = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    public String ToBase64(String toString) {
        byte[] data;
        try {
            data = toString.getBytes("UTF-8");
            toString = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        return toString.replace("\n","");
    }

    public String Fileto64(File originalFile) {
        String encodedBase64 = "";
        try {
            FileInputStream fileInputStreamReader = new FileInputStream(originalFile);
            byte[] bytes = new byte[(int)originalFile.length()];
            fileInputStreamReader.read(bytes);

            encodedBase64 = Base64.encodeToString(bytes,Base64.DEFAULT);

        } catch(Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }

        return encodedBase64.replace("\n","").replace(" ","").replace("\r","");
    }


    public String ToFile(String audioDataString,String name,String extencion) {
        String url=Environment.getExternalStorageDirectory() .getPath()+context.getString(R.string.dir_audio);
        try
        {

            byte[] decoded = Base64.decode(audioDataString, Base64.DEFAULT);

            File path = new File(url);
            if (! path.exists()){
                if (! path.mkdirs()){
                    Logo("grabando", "failed to create directory: "+path);

                }else{
                    Logo("grabando", "success to create directory: "+path);
                }
            }
            File archivo=new File(path+ "/"+name+extencion);

            if(!archivo.exists()){
                FileOutputStream os = new FileOutputStream(path+ "/"+name+".mp3", true);
                os.write(decoded);
                os.close();
                Logo("grabando", "Existe");
            }
            url= url+name+".mp3";


        } catch(Exception e){
            Logo("grabando", e.getMessage());
        }
        Logo("grabando", url);
        return url;
    }


    public void CheckGrupo(){
        Logo("Enviador","Checando...");
        String respuesta=Conexion("{\"id_usuario\":\""+GetIdUsuario()+"\"}",GetUrl()+context.getString(R.string.url_CheckGrupo),"POST");
        try {
            JSONArray jsonArray = new JSONArray(respuesta);
            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=new JSONObject(jsonArray.get(i).toString());
                if(jsonObject.get("id_grupo").equals("") && GetIdGrupo2().length()==32){
                    SalirGrupo();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String GetDate(){
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        return timeStamp;
    }

    public boolean CargarAudio(String id_audio,String audio){
        boolean bandera=false;
        int contador=0;
        while(true){
            if(contador>1){
                //Toast.makeText(context, "Fallo eL cargar el audio.", Toast.LENGTH_SHORT).show();
                break;
            }
            contador++;
            String respuesta=Conexion("{\"id_audio\":\""+id_audio+"\",\"audio\":\""+audio+"\",\"id_grupo\":\""+GetIdGrupo()+"\"}",GetUrl()+context.getResources().getString(R.string.url_CargarAudio),"POST");
            contador++;
            try {
                JSONObject jsonObject=new JSONObject(respuesta);
                if(jsonObject.getString("respuesta").contains("1")){
                    bandera=true;
                    break;
                }else{
                    bandera=false;
                }
            } catch (JSONException e) {

                e.printStackTrace();
            }
        }
        return bandera;
    }
    int tam_envio=1024*1024;
    public boolean EnviarArchivo(String id_archivo,String archivo,String tipo){
        boolean bandera=false;
        int tam_total=archivo.length(),tam_parte= (int) Math.floor(tam_total/tam_envio),sobrante=tam_total%tam_envio;
        Logo("cargararchivo","Tamaño total: "+tam_total+" parte:"+tam_parte+" ,Sobrante: "+sobrante);
        if(tam_parte==0){
            bandera=CargarArchivo(id_archivo,archivo,"1",tipo);
            Logo("cargararchivo","Extremof: "+archivo.substring(tam_total-20,tam_total) );
        }else{
            for(int i=0;i<tam_parte;i++){

                int inicio=tam_envio*i,
                        fin=tam_envio*(i+1),
                        ultimo=tam_total;

                if(i==0){
                    bandera=CargarArchivo(id_archivo,archivo.substring(inicio,fin),"0",tipo);
                    Logo("cargararchivo","Extremos: "+inicio+"----"+fin );
                    Logo("cargararchivo","Extremof: "+archivo.substring(fin-20,fin+20) );
                }else{
                    bandera=CargarArchivo(id_archivo,archivo.substring(inicio,fin),"",tipo);
                    Logo("cargararchivo","Extremos: "+(inicio)+"----"+fin );
                    Logo("cargararchivo","Extremof: "+archivo.substring(fin-20,fin+20) );
                }

                if(i==tam_parte-1){
                    bandera=CargarArchivo(id_archivo,archivo.substring(fin,ultimo),"1",tipo);
                    Logo("cargararchivo","Extremos: "+(fin)+"----"+ultimo );
                    Logo("cargararchivo","Extremof: "+archivo.substring(ultimo-20,ultimo) );
                }

            }
        }


        return bandera;
    }

    public boolean CargarArchivo(String id_archivo,String archivo,String indice,String tipo){
        boolean bandera=false;
        int contador=0;
        while(true){
            if(contador>1){
                //Toast.makeText(context, "Fallo eL cargar la archivo.", Toast.LENGTH_SHORT).show();
                break;
            }
            String respuesta=Conexion("{\"indice\":\""+indice+"\",\"id_archivo\":\""+id_archivo+"\",\"id_grupo\":\""+GetIdGrupo()+"\",\"tipo\":\""+tipo+"\",\"archivo\":\""+archivo+"\"}",GetUrl()+context.getResources().getString(R.string.url_CargarArchivo),"POST");
            contador++;
            try {
                JSONObject jsonObject=new JSONObject(respuesta);
                if(jsonObject.getString("respuesta").contains("1")){
                    bandera=true;
                    break;
                }else{
                    bandera=false;
                }
            } catch (JSONException e) {
                Logo("cargararchivo",e.getMessage());
            }
        }
        return bandera;
    }

    public String DescargarArchivo(String archivo, String tipo, ImageView imageView){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String path="";
        try {
            //primero especificaremos el origen de nuestro archivo a descargar utilizando
            //la ruta completa
            File file=null;
            switch (tipo){
                case "audio":
                    path=Environment.getExternalStorageDirectory() .getPath()+context.getString(R.string.dir_audio);
                    file=new File(path) ;
                    if (! file.exists()){
                        if (! file.mkdirs()){
                            Logo("grabando", "failed to create directory: "+path);

                        }else{
                            Logo("grabando", "success to create directory: "+path);
                        }
                    }
                    path=path+archivo;
                    archivo=GetUrl()+context.getResources().getString(R.string.url_DescargaAudio)+"/"+GetIdGrupo()+"/"+archivo;
                    break;
                case"imagen":
                    path=Environment.getExternalStorageDirectory() .getPath()+context.getString(R.string.dir_imagen);
                    file=new File(path) ;
                    if (! file.exists()){
                        if (! file.mkdirs()){
                            Logo("grabando", "failed to create directory: "+path);

                        }else{
                            Logo("grabando", "success to create directory: "+path);
                        }
                    }
                    path=path+archivo;
                    archivo=GetUrl()+context.getResources().getString(R.string.url_DescargaImagen)+"/"+GetIdGrupo()+"/"+archivo;
                    break;
            }
            Logo("Descarga","Url: "+archivo);
            //URL url = new URL(archivo);

            DescargaAsynk descargaAsynk=new DescargaAsynk(archivo,path,imageView);
            descargaAsynk.executeOnExecutor(threadPoolExecutor);
            Logo("Descarga","path: "+path);



        } catch (Exception e) {
            Logo("DescargaAudio","Error: "+e.getMessage());
            e.printStackTrace();
            //path="";
        }
        return path;
    }

    public boolean ChecarIdEmergencia(String id_emergencia) {
        Logo("Emergencia","ChecarIdEmergencia...");
        boolean bandera=false;
        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();

            Cursor c =  db.rawQuery("SELECT * from emergencias where id_emergencia='"+id_emergencia+"' ",null);
            c.moveToFirst();
            if(c.getCount()>0){
                bandera = true;
            }
            c.close();
            db.close();
        }catch (Exception e){
            Logo("Emergencia","ChecarIdEmergenciaError:"+e.getMessage());
        }
        return bandera;
    }

    public void GuardarIdEmergencia(String id_emergencia) {
        this.Logo("Emergencia","GuardarIdEmergencia...");
        String UUID="";
        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();

            ContentValues chat_notifica = new ContentValues();

            chat_notifica.put("id_emergencia", id_emergencia );


            db.insert("emergencias", null, chat_notifica);
            db.close();
        }catch (Exception e){
            Logo("Emergencia","GuardarIdEmergenciaError:"+e.getMessage());
        }


    }

    public int TiempoToInt(String fecha){
        String[] fechapartes=fecha.split(" "),fechapartes2=GetDate().split(" ");
        int entero=0;
        String[] tiempopartes=fechapartes[1].split(":"),tiempopartes2=fechapartes2[1].split(":");
        entero+=(Integer.parseInt(tiempopartes[0])*60)-(Integer.parseInt(tiempopartes2[0])*60);
        entero+=(Integer.parseInt(tiempopartes[1])*60)-(Integer.parseInt(tiempopartes2[1])*60);
        entero+=(Integer.parseInt(tiempopartes[2]))-(Integer.parseInt(tiempopartes2[2]));
        entero=abs(entero);
        Logo("Emergencia","TiempoToInt:"+fecha+"-----"+entero);
        return entero;
    }

    public boolean FechaHoy(String fecha){
        String[] fechapartes=fecha.split(" ");
        boolean bandera=false;
        String[] fechahoy=GetDate().split(" ");
        Logo("Emergencia","FechaHoy:"+fechapartes[0]+"=="+fechahoy[0]);
        if(fechapartes[0].contains(fechahoy[0])){
            bandera = true;
        }
        Logo("Emergencia","FechaHoy:"+bandera);
        return bandera;
    }

    public void NotificarPicasso(String title, String body, int icono,Intent intent, int id) {



        intent.putExtra("NoreiniciarServicio", 1);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //
        String NOTIFICATION_CHANNEL_ID = "Emergencia";

        RemoteViews myRemoteView = new RemoteViews(context.getPackageName(), R.layout.imagen_notification);

        Bitmap bit= BitmapFactory.decodeResource(context.getResources(),icono);
        myRemoteView.setImageViewBitmap(R.id.icono,bit);
        myRemoteView.setTextViewText(R.id.noti_titulo, title);
        myRemoteView.setTextViewText(R.id.noti_body, body);


        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        notificationBuilder
                .setSmallIcon(R.drawable.logo)
                .setAutoCancel(false)
                .setVibrate(new long[]{1000, 1000, 500, 1000})
                .setSound(defaultSoundUri)
                .setPriority(1)
                .setContentIntent(pendingIntent)
                .setContentInfo("info")
                .setContent(myRemoteView);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);

            Uri sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


            notificationChannel.setDescription("Descripcion");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{1000, 1000, 500, 1000});
            notificationChannel.enableLights(true);
            notificationBuilder.setSound(sonido);
            notificationBuilder.setPriority(1);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(id, notificationBuilder.build());
    }


    public String GetDireccionAntes() {
        double lat;
        double lon;

        Geocoder geocoder=  new Geocoder(context, Locale.getDefault());
        List<Address> direccion = null;
        try {
            JSONObject ubicacion=new JSONObject(GetUbicacion());
            lat=Double.parseDouble(ubicacion.getString("lat"));
            lon=Double.parseDouble(ubicacion.getString("lon"));
            direccion = geocoder.getFromLocation(lat, lon, 1);
            return direccion.get(0).getAddressLine(0);
        } catch (Exception e) {

        }
        return "No has marcado tu ubicación.";
    }

    public String GetDireccionAhora(double lat, double lon) {
        Geocoder geocoder=  new Geocoder(context, Locale.getDefault());
        List<Address> direccion = null;
        try {
            direccion = geocoder.getFromLocation(lat, lon, 1);
            return direccion.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "No se pudo obtener la dirección.";
    }

    public void GuardarGrupoLogin(String id_grupo, String id_usuario_grupo, String nombre_grupo) {
        Base base = new Base(context);
        SQLiteDatabase db = base.getWritableDatabase();

        ContentValues grupo = new ContentValues();
        grupo.put("id_grupo",id_grupo);
        grupo.put("id_usuario",id_usuario_grupo);
        grupo.put("nombre", nombre_grupo);
        grupo.put("enviado", 1);


        db.insert("grupo", null, grupo);

        db.close();
    }


    class DescargaAsynk extends AsyncTask {
        String urls;
        String path;
        ImageView imageView;

        DescargaAsynk(String urls, String path,ImageView imageView){


            this.urls=urls;
            this.path=path;
            this.imageView=imageView;

        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
            try{
                if (imageView!=null){
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth()*(porcentaje)), (int) (bitmap.getHeight()*(porcentaje)), false));
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Vibrar(VibrarPush());
                            Intent intent = new Intent(); intent.setAction(Intent.ACTION_VIEW); intent.setDataAndType(Uri.parse(path), "image/*");
                            context.startActivity(intent);
                        }
                    });

                }
            }catch (Exception e){
                Logo("errorpintar",e.getMessage());
            }

        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try{
                URL url=new URL(urls);
                File filef = new File(path);
                if(!filef.exists()){
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoOutput(true);

                    urlConnection.connect();

                    //utilizaremos un objeto del tipo fileoutputstream
                    //para escribir el archivo que descargamos en el nuevo
                    FileOutputStream fileOutput = new FileOutputStream(filef);

                    //leemos los datos desde la url
                    InputStream inputStream = urlConnection.getInputStream();

                    //obtendremos el tamaño del archivo y lo asociaremos a una
                    //variable de tipo entero
                    int totalSize = urlConnection.getContentLength();
                    int downloadedSize = 0;

                    //creamos un buffer y una variable para ir almacenando el
                    //tamaño temporal de este
                    byte[] buffer = new byte[1024];
                    int bufferLength = 0;

                    //ahora iremos recorriendo el buffer para escribir el archivo de destino
                    //siempre teniendo constancia de la cantidad descargada y el total del tamaño
                    //con esto podremos crear una barra de progreso
                    while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                        Logo("Descarga","Tamoaño: "+downloadedSize);
                        fileOutput.write(buffer, 0, bufferLength);
                        downloadedSize += bufferLength;
                        //podríamos utilizar una función para ir actualizando el progreso de lo

                        //actualizaProgreso(downloadedSize, totalSize);

                    }
                    //cerramos
                    fileOutput.close();

                }
            }catch(Exception e){

            }
            publishProgress();


            return null;
        }
    }



    public void UpdatefbToken(){
        Conexion("{\"id_usuario\":\""+GetIdUsuario()+"\",\"fbtoken\":\""+ FirebaseInstanceId.getInstance().getToken()+""+"\"}",GetUrl()+context.getResources().getString(R.string.url_UpdatefbToken),"POST");
    }

    public String GetCurrentActivity(){
        ActivityManager am = (ActivityManager)context.getSystemService(ACTIVITY_SERVICE);
        List< ActivityManager.RunningTaskInfo > taskInfo = am.getRunningTasks(1);
        return taskInfo.get(0).topActivity.getClassName();
    }




    public void SetUbicacion(String ubicacion){
        Base base = new Base(context);
        SQLiteDatabase db = base.getWritableDatabase();
        db.execSQL("UPDATE login SET ubicacion='"+ubicacion+"' WHERE id_usuario='"+GetIdUsuario()+"' ");
        db.close();
    }

    public String GetUbicacion() {
        String ubicacion="";
        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();

            Cursor c =  db.rawQuery("SELECT * from login ",null);
            c.moveToFirst();
            if(c.getCount()>0){
                c.moveToFirst();

                ubicacion=c.getString(c.getColumnIndex("ubicacion"));

            }
            c.close();
            db.close();
        }catch (Exception e){}

        return ubicacion;
    }
    public boolean VerificarSwitchGPS() {
        String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        System.out.println("Provider contains=> " + provider);
        if (provider.contains("gps") || provider.contains("network")){
            return true;
        }
        return false;
    }


    int horasegundos=3600,minutosegundos=60;
    public int ObtenerSegundosDif(String horanew,String horaold){
        Logo("tiempodifereicnci",horanew+": "+ObtenerSegundos(horanew)+"---"+ObtenerSegundos(horaold)+": "+horaold);
        return ObtenerSegundos(horanew)-ObtenerSegundos(horaold);

    }
    public int ObtenerSegundos(String hora){
        String[] tiempo=hora.split(":");
        return (Integer.parseInt(tiempo[0])*horasegundos)+(Integer.parseInt(tiempo[1])*minutosegundos)+Integer.parseInt(tiempo[2]);
    }



    public boolean EsHoy(String fecha){
        String[] fechanow=GetDate().split(" "),fechaold=fecha.split(" ");

        if((fechanow[0]).contains((fechaold[0]))){
            return true;
        }
        return false;
    }
    public boolean EsUnMinuto(String fecha){
        String[] fechanow=GetDate().split(" "),fechaold=fecha.split(" ");
        if(ObtenerSegundosDif(fechanow[1],fechaold[1])<61){
            return true;
        }
        return false;
    }



    public class Enviador extends AsyncTask {


        @Override
        protected Object doInBackground(Object[] objects) {
            EnviarGrupo();
            CheckGrupo();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }
    }

    public void ForzarEnviador(){
        new Enviador().executeOnExecutor(threadPoolExecutor);

    }

    public void CorrerVerificarEnviado(View viewById){
        new VerificarEnviado(viewById).executeOnExecutor(threadPoolExecutor);
    }



    public class VerificarEnviado extends AsyncTask {
        boolean Flag=true;

        View viewById;

        public VerificarEnviado(View viewById) {
            this.viewById=viewById;
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
            if(IsGrupoEnviado()){
                viewById.setVisibility(View.GONE);
                Logo("VerificarEnviado","si");
                Flag=false;
            }else{
                viewById.setVisibility(View.VISIBLE);
                Logo("VerificarEnviado","no");
                ForzarEnviador();
            }


        }

        @Override
        protected Object doInBackground(Object[] objects) {
            while (Flag){
               publishProgress();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }
    }


}




