package com.app.alarmavecinal;

import android.Manifest;
import android.app.Activity;
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
import android.content.pm.PackageManager;
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
import androidx.core.content.ContextCompat;

import com.app.alarmavecinal.Alertas.AlertasLista;
import com.app.alarmavecinal.Models.Grupo;
import com.app.alarmavecinal.Models.Ordenes;
import com.app.alarmavecinal.Models.Usuario;
import com.app.alarmavecinal.Orden.OrdenInterface;
import com.app.alarmavecinal.Orden.OrdenService;
import com.app.alarmavecinal.Vecinos.GrupoView;
import com.app.alarmavecinal.Sqlite.Base;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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




    public void LogOut() {

        BorrarLogin();
        BorrarGrupo();
        StopServiceAlertas();
    }
    public void StopServiceAlertas(){

        //DetenerServicioEmergencias();
        //DetenerServicioNotificador();
        DetenerServicioOrdenes();
    }
    public void VerificarServicios(){


        if (GetIdGrupo().replace(" ", "").length() == 32) {
            IniciarServicioOrdenes();
            //IniciarServicioEmergencias();
            //IniciarServicioNotificador();

        }else{
            StopServiceAlertas();
            //DetenerServicioEmergencias();
            //DetenerServicioNotificador();

        }

    }


    private void DetenerServicioOrdenes() {
        context.stopService(new Intent(context.getApplicationContext(), OrdenService.class));
    }

    private void IniciarServicioOrdenes() {
        if (!isMyServiceRunning(OrdenService.class, context)) {

            Intent service1 = new Intent(context, OrdenService.class);
            service1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(context,service1);
            }else{

                context.startService(service1);
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




    public JsonArray GetAlertas(){
        Base base = new Base(context);
        SQLiteDatabase db = base.getWritableDatabase();
        JsonArray jsonArray = new JsonArray();


        Cursor c =  db.rawQuery("SELECT * from alertas order by created_at desc ",null);
        Logo("ListaAlertas","Cont:"+c.getCount());
        if(c.getCount()>0){
            c.moveToFirst();
            while (!c.isAfterLast()){
                JsonObject jsonObject=new JsonObject();
                try {
                    jsonObject.addProperty("id_alerta", c.getString(c.getColumnIndex("id_alerta")));
                    jsonObject.addProperty("created_at", c.getString(c.getColumnIndex("created_at")));
                    jsonObject.addProperty("asunto", c.getString(c.getColumnIndex("asunto")));
                    jsonObject.addProperty("nombre", c.getString(c.getColumnIndex("nombre")));
                    jsonObject.addProperty("imagen", c.getString(c.getColumnIndex("imagen")));
                    jsonArray.add(jsonObject);
                } catch (Exception e) {

                    Logo("Avisos","Errorrrrrrrr:"+e.getMessage());
                }
                c.moveToNext();

            }


        }

        c.close();
        db.close();
        return jsonArray;
    }

    public int GetNumAlertas(){
        Base base = new Base(context);
        SQLiteDatabase db = base.getWritableDatabase();


        Cursor c =  db.rawQuery("SELECT * from alertas order by created_at desc ",null);
        Logo("ListaAlertas","Cont:"+c.getCount());
       int num=c.getCount();




        c.close();
        db.close();
        return num;
    }


    public JsonArray GetAvisos(){
        Base base = new Base(context);
        SQLiteDatabase db = base.getWritableDatabase();
        JsonArray jsonArray = new JsonArray();


        Cursor c =  db.rawQuery("SELECT * from avisos order by created_at desc ",null);
        Logo("Avisos","Cont:"+c.getCount());
        if(c.getCount()>0){
            c.moveToFirst();
            while (!c.isAfterLast()){
                JsonObject jsonObject=new JsonObject();
                try {
                    jsonObject.addProperty("id_alerta", c.getString(c.getColumnIndex("id_aviso")));
                    jsonObject.addProperty("created_at", c.getString(c.getColumnIndex("created_at")));
                    jsonObject.addProperty("asunto", c.getString(c.getColumnIndex("asunto")));
                    jsonObject.addProperty("nombre", c.getString(c.getColumnIndex("nombre")));
                    jsonObject.addProperty("mensaje", c.getString(c.getColumnIndex("mensaje")));
                    jsonArray.add(jsonObject);
                } catch (Exception e) {

                    Logo("Avisos","Errorrrrrrrr:"+e.getMessage());
                }
                c.moveToNext();

            }


        }

        c.close();
        db.close();
        return jsonArray;
    }

    public int GetNumAvisos(){
        Base base = new Base(context);
        SQLiteDatabase db = base.getWritableDatabase();


        Cursor c =  db.rawQuery("SELECT * from avisos order by created_at desc ",null);
        Logo("ListaAlertas","Cont:"+c.getCount());
        int num=c.getCount();




        c.close();
        db.close();
        return num;
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

    public void SinGrupo(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Sin Grupo");
        alertDialog.setMessage("Primero debes estar en un grupo, ¿Deseas unirte?");



        alertDialog.setIcon(R.drawable.boton_alerta);

        alertDialog.setPositiveButton("Unirse",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        context.startActivity(new Intent(context, GrupoView.class));

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

    public String[] GetTime(String str){
        String[] DT=null;
        try{
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            ZonedDateTime result = ZonedDateTime.parse(str, formatter);


            DT = result.toString().split("T");
            Logo("Tiempo",DT[1]);
            DT[0] = DT[0].replace("Z","");
            DT[1] = DT[1].replace("Z","");

            return DT;

        }catch(Exception e){}


        return DT;
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



    public void GetAlertasServer() {
        AbrirConexion();

        JsonArray jsonArray=new JsonArray();
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("id_grupo",GetIdGrupo());
        jsonObject.add("ids",GetIdAlertas());
        jsonArray.add(jsonObject);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GetUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        OrdenInterface peticion=retrofit.create(OrdenInterface.class);
        Call<JsonArray> call= peticion.GetAlertas(jsonArray);
        Log.i("GetAlertas", jsonArray+"");
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                Log.i("GetAlertas", "Code:"+response.code());
                if(response.body()!=null) {
                    Log.i("GetAlertas", response.body().toString());
                    if(IsSuccess(response.body())) {

                        GuardarAlertas(response.body());


                    } else {

                    }
                }

            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.i("GetAlertas","Erro:"+t.getMessage());

            }
        });
    }


    private void GuardarAlertas(JsonArray body) {
        Base base = new Base(context);
        SQLiteDatabase db = base.getWritableDatabase();
        for(int i=0;i<GetData(body).size();i++){


            try{
                if(!EstaAlerta(GetIndex2(GetData(body),i,"id_alerta"))){
                    ContentValues alerta = new ContentValues();

                    alerta.put("id_alerta", GetIndex2(GetData(body),i,"id_alerta"));
                    alerta.put("id_grupo", GetIndex2(GetData(body),i,"id_grupo"));
                    alerta.put("nombre", GetIndex2(GetData(body),i,"nombre"));
                    alerta.put("imagen", GetIndex2(GetData(body),i,"imagen"));

                    alerta.put("asunto", GetIndex2(GetData(body),i,"asunto"));
                    alerta.put("mensaje", GetIndex2(GetData(body),i,"mensaje"));

                    alerta.put("created_at", GetIndex2(GetData(body),i,"created_at"));



                    db.insert("alertas", null, alerta);
                    Notificar("Alerta",GetIndex2(GetData(body),i,"asunto")+"\n"+GetIndex2(GetData(body),i,"nombre"),R.drawable.boton_alerta,new Intent(context, AlertasLista.class),3);
                }

            }catch(Exception e){
                Logo("GuardarAlertas","Error:"+e.getMessage());
            }


        }
        db.close();

    }

    private JsonArray GetIdAlertas() {


        JsonArray jsonArray=new JsonArray();
        Base base = new Base(context);
        SQLiteDatabase db = base.getWritableDatabase();


        Cursor c =  db.rawQuery("SELECT * from alertas ",null);
        c.moveToFirst();
        Logo("GuardarAlertas",c.getCount()+"<---Hay");
        if(c.getCount()>0){
            while(!c.isAfterLast()){
                JsonObject jsonObject=new JsonObject();
                jsonObject.addProperty("id_alerta",c.getString(c.getColumnIndex("id_alerta")));
                jsonArray.add(jsonObject);
                c.moveToNext();
            }
        }



        c.close();
        db.close();
        return jsonArray;

    }


    private boolean EstaAlerta(String id_alerta) {


        Base base = new Base(context);
        SQLiteDatabase db = base.getWritableDatabase();

        Cursor c =  db.rawQuery("SELECT * from alertas where id_alerta='"+id_alerta+"' ",null);

        if(c.getCount()>0){

            Logo("GuardarAlertas",c.getCount()+"  Si esta:"+id_alerta);
            return true;
        }



        c.close();
        db.close();
        return false;

    }




    public void GetAvisosServer() {
        AbrirConexion();

        JsonArray jsonArray=new JsonArray();
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("id_grupo",GetIdGrupo());
        jsonObject.add("ids",GetIdAvisos());
        jsonArray.add(jsonObject);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GetUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        OrdenInterface peticion=retrofit.create(OrdenInterface.class);
        Call<JsonArray> call= peticion.GetAvisos(jsonArray);
        Log.i("GetAvisos", jsonArray+"");
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                Log.i("GetAvisos", "Code:"+response.code());
                if(response.body()!=null) {
                    Log.i("GetAvisos", response.body().toString());
                    if(IsSuccess(response.body())) {

                        GuardarAvisos(response.body());


                    } else {

                    }
                }

            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.i("GetAvisos","Erro:"+t.getMessage());

            }
        });
    }


    private void GuardarAvisos(JsonArray body) {
        Base base = new Base(context);
        SQLiteDatabase db = base.getWritableDatabase();
        for(int i=0;i<GetData(body).size();i++){

            try{

                if(!EstaAviso(GetIndex2(GetData(body),i,"id_aviso"))){
                    ContentValues aviso = new ContentValues();

                    aviso.put("id_aviso", GetIndex2(GetData(body),i,"id_aviso"));
                    aviso.put("id_grupo", GetIndex2(GetData(body),i,"id_grupo"));
                    aviso.put("nombre", GetIndex2(GetData(body),i,"nombre"));
                    aviso.put("created_at", GetIndex2(GetData(body),i,"created_at"));

                    aviso.put("asunto", GetIndex2(GetData(body),i,"asunto"));
                    aviso.put("mensaje", GetIndex2(GetData(body),i,"mensaje"));



                    db.insert("avisos", null, aviso);
                    Notificar("Aviso",GetIndex2(GetData(body),i,"asunto")+"\n"+GetIndex2(GetData(body),i,"nombre"), R.drawable.img_menu_aviso,new Intent(context, AlertasLista.class),3);
                }

            }catch(Exception e){
                Logo("GuardarAvisos",e.getMessage());
            }


        }
        db.close();

    }

    private JsonArray GetIdAvisos() {


        JsonArray jsonArray=new JsonArray();
        Base base = new Base(context);
        SQLiteDatabase db = base.getWritableDatabase();


        Cursor c =  db.rawQuery("SELECT * from avisos ",null);
        c.moveToFirst();
        Logo("GetAvisos",c.getCount()+"<---");
        if(c.getCount()>0){
            while(!c.isAfterLast()){
                JsonObject jsonObject=new JsonObject();
                jsonObject.addProperty("id_aviso",c.getString(c.getColumnIndex("id_aviso")));
                Logo("GetAvisos","--->"+c.getString(c.getColumnIndex("id_aviso")));
                jsonArray.add(jsonObject);
                c.moveToNext();

            }
        }



        c.close();
        db.close();
        return jsonArray;

    }

    private boolean EstaAviso(String id_aviso) {


        Base base = new Base(context);
        SQLiteDatabase db = base.getWritableDatabase();




        Cursor c =  db.rawQuery("SELECT * from avisos where id_aviso='"+id_aviso+"' ",null);

        if(c.getCount()>0){
            Logo("GuardarAvisos","Esta id: "+id_aviso+" "+c.getCount());
            return true;
        }



        c.close();
        db.close();
        return false;

    }

    public void ActualizarMensajes() {

        //Obteniendo id que ya tenemos de mensages
        JsonArray jsonArray=new JsonArray();

        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();

            Cursor c =  db.rawQuery("SELECT id_mensaje,created_at from mensajes order by created_at desc limit 0,1",null);

            if(c.getCount()>0){
                c.moveToFirst();
                while (!c.isAfterLast()){

                    JsonObject jsonObject=new JsonObject();
                    jsonObject.addProperty("id",c.getString(c.getColumnIndex("id_mensaje")));
                    jsonObject.addProperty("id_grupo",GetIdGrupo());
                    jsonArray.add(jsonObject);
                    c.moveToNext();
                }


            }else{
                JsonObject jsonObject=new JsonObject();
                jsonObject.addProperty("id","");
                jsonObject.addProperty("id_grupo",GetIdGrupo());
                jsonArray.add(jsonObject);
            }
            c.close();
            db.close();
        }catch (Exception e){

            Log.i("idsss",e.getMessage());
        }
        Log.i("idsss",jsonArray.toString());


        //Pidiendo nuevos mensajes

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GetUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Interface peticion=retrofit.create(Interface.class);
        Call<JsonArray> call= peticion.ActualizarMensajes(jsonArray);
        Log.i("ActualizarMensajes", GetUrl()+"/api/ActualizarMensajes");
        Log.i("ActualizarMensajes", jsonArray.toString());
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                Log.i("ActualizarMensajes", response.code()+"");
                if(response.body()!=null) {
                    Log.i("ActualizarMensajes", response.body()+"");
                    if(IsSuccess(response.body())) {
                        GuardarMensajes(response.body());


                    } else {
                        Toast(GetMsn(response.body()));
                    }
                }

            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.i("ActualizarMensajes","Error:"+t.getMessage());

            }
        });

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








    ////////////////////////////////////////////////////////



    public void AbrirConexion(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
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

    public void UpdateDatos(String nombre,String direccion,String ubicacion) {
        Base base = new Base(context);
        SQLiteDatabase db = base.getWritableDatabase();

        db.execSQL("UPDATE login SET nombre='"+nombre+"',direccion='"+direccion+"',ubicacion='"+ubicacion+"' ");

        db.close();
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

    public void GuardarGrupoLogin(String id_grupo,String id_usuario,String nombre) {
        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();

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


    public void GuardarGrupoCreadoArray(JsonArray jsonArray) {
        Base base = new Base(context);
        SQLiteDatabase db = base.getWritableDatabase();

        String id_usuario=GetIdUsuario();

        ContentValues grupos = new ContentValues();

        grupos.put("id_grupo", GetIndex(jsonArray,"id_grupo"));
        grupos.put("id_usuario",GetIndex(jsonArray,"id_grupo"));
        grupos.put("nombre", ToUtf8(GetIndex(jsonArray,"nombre")));
        grupos.put("enviado", 1);


        db.insert("grupo", null, grupos);
        db.close();
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


    public Boolean PedirPermisoArchivos(Activity view) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permsRequestCode = 100;
            String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};

            int archivos = context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

            if (archivos == PackageManager.PERMISSION_GRANTED) {

                return true;
            } else {

                view.requestPermissions(perms, permsRequestCode);
                return false;
            }

        }
        return true;
    }

    public Boolean PedirPermisoLocation(Activity view) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permsRequestCode = 100;
            String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};

            int gps = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

            if (gps == PackageManager.PERMISSION_GRANTED) {

                return true;
            } else {

                view.requestPermissions(perms, permsRequestCode);
                return false;
            }

        }
        return true;
    }

    public void Toast(String msn) {
        Toast.makeText(context, msn, Toast.LENGTH_SHORT).show();
    }

    public boolean IsSuccess(JsonArray body) {
        try{
            if(Integer.parseInt(body.get(0).getAsJsonObject().get("status").toString())==1) {
                return true;
            }
        }catch(Exception e){
            return false;
        }


        return false;
    }

    public String GetMsn(JsonArray body) {
        try {
            JSONObject jsonObject=new JSONObject(body.get(0).toString());
            return jsonObject.getString("msn");
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String GetIndex(JsonArray body,String index) {
        try {
            JSONObject jsonObject=new JSONObject(body.get(0).getAsJsonObject().get("datos").toString());
            return jsonObject.getString(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }


    public String GetIndex2(JsonArray body,int i,String index) {
        //Log.i("GetAlertas",body.get(i).toString());
        try {
            JSONObject jsonObject=new JSONObject(body.get(i).toString());
            return jsonObject.getString(index);
        } catch (JSONException e) {

            Log.i("ErrorGetIndex2","Error:"+e.getMessage());
            //return "";
        }
        return "";
    }

    public JsonArray GetData(JsonArray jsonArray) {
        return jsonArray.get(0).getAsJsonObject().get("datos").getAsJsonArray();
    }



    public void EnviarMensajes() {

        String id="";
        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();



            JsonArray mensajes=new JsonArray();
            Cursor c =  db.rawQuery("SELECT * from mensajes where enviado=0 order by created_at asc limit 0,1 ",null);
            c.moveToFirst();

            while (!c.isAfterLast()){
                JsonObject jsonObject=new JsonObject();


                id=c.getString(c.getColumnIndex("id_mensaje"));
                jsonObject.addProperty("id_mensaje",c.getString(c.getColumnIndex("id_mensaje")));
                jsonObject.addProperty("id_usuario",c.getString(c.getColumnIndex("id_usuario")));
                jsonObject.addProperty("id_grupo",c.getString(c.getColumnIndex("id_grupo")));
                jsonObject.addProperty("nombre",c.getString(c.getColumnIndex("nombre")));
                jsonObject.addProperty("imagen",c.getString(c.getColumnIndex("imagen")));
                jsonObject.addProperty("mensaje",c.getString(c.getColumnIndex("mensaje")));
                jsonObject.addProperty("audio",c.getString(c.getColumnIndex("audio")));
                jsonObject.addProperty("video",c.getString(c.getColumnIndex("video")));
                jsonObject.addProperty("enviado",c.getString(c.getColumnIndex("enviado")));
                jsonObject.addProperty("created_at",c.getString(c.getColumnIndex("created_at")));
                jsonObject.addProperty("updated_at",c.getString(c.getColumnIndex("updated_at")));

                mensajes.add(jsonObject);

                c.moveToNext();
            }

            c.close();
            db.close();
            Logo("Mensajes",mensajes+"");
            if(mensajes.size()>0){
                EnviarMensajes2(id,mensajes);
            }

        }catch (Exception e){
            Toast(e.getMessage());
        }



    }



    public void EnviarMensajes2(String id , JsonArray mensajes) {
        AbrirConexion();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GetUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Interface peticion=retrofit.create(Interface.class);
        Call<JsonArray> call= peticion.GuardarMensaje(mensajes);
        Log.i("Mensajes", mensajes.toString());
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                try {
                    if(response.code()==500){
                        Log.i("Mensajes", response.code()+"---"+response.errorBody().string());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(response.body()!=null) {
                Log.i("Mensajes", response.body().toString());
                    if(IsSuccess(response.body())) {
                        PonerEnviados(id);
                        EnviarOrden(new Ordenes(4,"",GetIdUsuario(),GetNombre(),"","",""));

                    } else {
                        Toast(GetMsn(response.body()));
                    }
                }

            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.i("Mensajes","Erro:"+t.getMessage());

            }
        });

    }

    private void PonerEnviados(String id) {
        Base base = new Base(context);
        SQLiteDatabase db = base.getWritableDatabase();

        db.execSQL("UPDATE mensajes SET enviado=1 where id_mensaje='"+id+"' ");

        db.close();
    }


    private void GuardarMensajes(JsonArray body) {
        Base base = new Base(context);
        SQLiteDatabase db = base.getWritableDatabase();
        for(int i=0;i<GetData(body).size();i++){


            try{
                if(!EstaAlerta(GetIndex2(GetData(body),i,"id_alerta"))){



                    ContentValues mensajes = new ContentValues();

                    mensajes.put("id_mensaje",GetIndex2(GetData(body),i,"id_mensaje"));
                    mensajes.put("id_grupo",GetIndex2(GetData(body),i,"id_grupo"));
                    mensajes.put("id_usuario",GetIndex2(GetData(body),i,"id_usuario"));
                    mensajes.put("nombre",GetIndex2(GetData(body),i,"nombre"));
                    mensajes.put("imagen",GetIndex2(GetData(body),i,"imagen"));
                    mensajes.put("mensaje",GetIndex2(GetData(body),i,"mensaje"));
                    mensajes.put("audio",GetIndex2(GetData(body),i,"audio"));
                    mensajes.put("video",GetIndex2(GetData(body),i,"video"));
                    mensajes.put("enviado",1);
                    mensajes.put("created_at",GetIndex2(GetData(body),i,"created_at"));
                    mensajes.put("updated_at",GetIndex2(GetData(body),i,"updated_at"));


                    db.insert("mensajes", null, mensajes);




                }

            }catch(Exception e){
                Logo("GuardarAlertas","Error:"+e.getMessage());
            }


        }
        //Toast.makeText(context, "# Mensajes:"+GetData(body).size(), Toast.LENGTH_SHORT).show();
        db.close();

    }

    public void EnviarOrden(Ordenes ordenes){
        FirebaseDatabase firebaseDatabaseAlerta = FirebaseDatabase.getInstance();
        DatabaseReference databaseReferenceAlerta = firebaseDatabaseAlerta.getReference(GetIdGrupo() + "/Ordenes");//Sala de chat
        try {

            //JSONObject jsonObject0=new JSONObject(json);
            databaseReferenceAlerta.setValue("");
            databaseReferenceAlerta.push().setValue(ordenes);


        } catch (Exception e) {
            Log.i("EnviarOrdenes","Error:"+e.getMessage());
        }

    }


}




