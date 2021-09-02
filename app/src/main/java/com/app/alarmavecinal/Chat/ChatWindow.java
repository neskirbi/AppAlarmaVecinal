package com.app.alarmavecinal.Chat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.alarmavecinal.BuildConfig;
import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.R;
import com.app.alarmavecinal.Sqlite.Base;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class ChatWindow extends AppCompatActivity {
    Cargar cargar;
    Pintar pintar;
    Context context;
    Funciones funciones;
    EditText mensaje;
    boolean bandera = true,IsAudio=false;
    LinearLayout chat;
    FrameLayout grabacion;
    String ultimo="2020-01-01 00:00:00";
    ScrollView scroll;
    ImageView enviar,grabar,cancelar;
    TextView ngrupo,timerecord;
    MediaPlayer mPlayer = new  MediaPlayer();
    MediaRecorder recorder;
    File archivo;
    Tiempo tiempo;
    ArrayList<String> ids=new ArrayList<>();
    SeekBar onPlay;


    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;

    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);
        mensaje=findViewById(R.id.mensaje);
        context=this;
        funciones=new Funciones(context);
        chat=findViewById(R.id.chat);
        scroll=findViewById(R.id.scroll);
        enviar=findViewById(R.id.enviar);
        grabar=findViewById(R.id.grabar);
        cancelar=findViewById(R.id.cancelar);
        ngrupo=findViewById(R.id.ngrupo);
        timerecord=findViewById(R.id.timerecord);
        grabacion=findViewById(R.id.grabacion);
        ngrupo.setText(funciones.GetNombreGrupo());



        mensaje.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(mensaje.getText().toString().replace(" ","").length()==0){
                    EnviarDisable();
                }else{
                   EnviarEnable();


                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        grabar.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(PedirPermiso()) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        funciones.Vibrar(funciones.VibrarPush());
                        tiempo=new Tiempo();
                        corriendo=true;
                        tiempo.executeOnExecutor(threadPoolExecutor);
                        Grabar();
                        MensajeDisable();
                    }
                    else if(event.getAction() == MotionEvent.ACTION_UP){
                        corriendo=false;
                        Detener();


                    }
                }

                return false;
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cancelar();
            }
        });


        AdView m = findViewById(R.id.banner);
        AdRequest adRequest = null;
        if (BuildConfig.DEBUG) {
            adRequest=new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();

        }else{
            adRequest=new AdRequest.Builder().build();
        }
        m.loadAd(adRequest);

        funciones.Logo("corriendo","OnCreate");
        cargar=new Cargar();
        cargar.executeOnExecutor(threadPoolExecutor);
        pintar=new Pintar();
        pintar.executeOnExecutor(threadPoolExecutor);
    }

    public void EnviarEnable(){
        enviar.setVisibility(View.VISIBLE);
        grabar.setVisibility(View.GONE);
    }
    public void EnviarDisable(){
        enviar.setVisibility(View.GONE);
        grabar.setVisibility(View.VISIBLE);
    }

    public void MensajeDisable(){
        grabacion.setVisibility(View.VISIBLE);
        mensaje.setVisibility(View.GONE);
    }
    public void MensajeEnable(){
        grabacion.setVisibility(View.GONE);
        mensaje.setVisibility(View.VISIBLE);
    }

    public void Enviar(View view){
        if(IsAudio){
            funciones.Logo("grabando","Enviando audio");
            EnviaAudio();
        }else{
            EnviaTexto();

        }



    }

    public void EnviaTexto() {
        if(mensaje.getText().toString().length()>0){
            funciones.Logo("grabando","Enviando texto");
            funciones.GuardarChat(funciones.ToBase64(mensaje.getText().toString()),"1");
            mensaje.setText("");
        }
    }

    public void EnviaAudio() {
        funciones.Logo("tiempo",timerecord.getText().toString());
        try {
            if(archivo.exists() &&  !timerecord.getText().toString().contains("00:01")){
                funciones.Logo("grabando",""+archivo);
                if(archivo.exists()){
                    funciones.GuardarChat(funciones.Fileto64(archivo),"2");
                }else{
                    funciones.Logo("grabando","Error El archivo no esta");
                }

                EnviarDisable();
                MensajeEnable();
                IsAudio=false;
                archivo=null;
                timerecord.setText("00:00");
            }else{
                Toast.makeText(context, "¡Audio demasiado corto!", Toast.LENGTH_SHORT).show();
                Cancelar();
            }
        }catch (Exception e){}


    }



    public void Cancelar(){
        funciones.VibrarPush();
        EnviarDisable();
        MensajeEnable();
        IsAudio=false;
        archivo=null;
        timerecord.setText("00:00");
    }



    @Override
    protected void onStart() {
        super.onStart();
        funciones.Logo("corriendo","onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        funciones.Logo("corriendo","onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        funciones.Logo("corriendo","onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bandera= false;
        funciones.Logo("corriendo","onDestroy");
        cargar.cancel(true);
        pintar.cancel(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


        finish();
    }

    class  Pintar extends AsyncTask{
        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
            Inflar();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            while (bandera){
                publishProgress("");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    class  Cargar extends AsyncTask{
        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
            OnPlay();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            while (bandera){
                funciones.GetChat();
                publishProgress();
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    void Inflar(){
        Base base = new Base(context);
        SQLiteDatabase db = base.getWritableDatabase();

        try {

            Cursor c =  db.rawQuery("SELECT * from chat_in order by fecha asc ",null);

            if(c.getCount()>0){
                c.moveToFirst();
                JSONObject MensajeHora;
                while (c.moveToNext()){
                    //c.getString(c.getColumnIndex("nombre"));
                    if(!ids.contains(c.getString(c.getColumnIndex("id_chat")))){
                        ids.add(c.getString(c.getColumnIndex("id_chat")));
                        View  item=null;

                        MensajeHora=new JSONObject(c.getString(c.getColumnIndex("mensaje")));
                        ultimo=c.getString(c.getColumnIndex("fecha")) ;
                        funciones.Logo("chatee","Mensaje:"+c.getString(c.getColumnIndex("mensaje"))+"--"+c.getString(c.getColumnIndex("fecha"))+"--"+c.getString(c.getColumnIndex("id_usuario"))+"--"+c.getString(c.getColumnIndex("nombre")));


                        LayoutInflater inflater = getLayoutInflater();
                        if(!c.getString(c.getColumnIndex("id_usuario")).contains(funciones.GetIdUsuario())){
                            funciones.Logo("chatee","Blanco:"+c.getString(c.getColumnIndex("tipo")));
                            if(c.getString(c.getColumnIndex("tipo")).contains("1")){
                                item = inflater.inflate(R.layout.dialogo1, null);
                            }else{
                                item = inflater.inflate(R.layout.audio_globo, null);
                            }
                        }else{
                            funciones.Logo("chatee","Verde:"+c.getString(c.getColumnIndex("tipo")));
                            if(c.getString(c.getColumnIndex("tipo")).contains("1")){
                                item = inflater.inflate(R.layout.dialogo2, null);
                            }else{
                                item = inflater.inflate(R.layout.audio_globo, null);
                            }

                        }

                        TextView nombre = item.findViewById(R.id.nombre);
                        nombre.setText(c.getString(c.getColumnIndex("nombre")) + ": ");

                        TextView hora = item.findViewById(R.id.hora);
                        hora.setText(MensajeHora.get("hora").toString());


                        if(c.getString(c.getColumnIndex("tipo")).contains("1")) {
                            TextView mensaje = item.findViewById(R.id.mensaje);
                            mensaje.setText(funciones.ToString(MensajeHora.get("mensaje").toString()));
                        }else{

                            ImageView pausa = item.findViewById(R.id.pause);
                            ImageView play = item.findViewById(R.id.play);
                            //final String url=funciones.ToFile(c.getString(c.getColumnIndex("mensaje")),c.getString(c.getColumnIndex("id_chat")));
                            final String url=MensajeHora.get("mensaje").toString();
                            final SeekBar progreso = item.findViewById(R.id.progreso);
                            progreso.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                @Override
                                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                    if(fromUser && onPlay==seekBar) {
                                        funciones.Logo("grabando", "onProgressChanged");
                                        mPlayer.seekTo(seekBar.getProgress());
                                    }
                                }

                                @Override
                                public void onStartTrackingTouch(SeekBar seekBar) {
                                    funciones.Logo("grabando", "onStartTrackingTouch");
                                }

                                @Override
                                public void onStopTrackingTouch(SeekBar seekBar) {
                                    funciones.Logo("grabando", "onStopTrackingTouch");
                                }
                            });



                            play.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(PedirPermiso()) {
                                        funciones.Vibrar(funciones.VibrarPush());
                                        //funciones.Logo("reproduce",urlf);
                                        PlayAudio(url,progreso);
                                    }
                                }
                            });
                        }


                        chat.addView(item);

                        mPlayer.reset();
                        mPlayer = MediaPlayer.create(context, R.raw.pop);
                        mPlayer.start();


                        scroll.post(new Runnable() {
                            @Override
                            public void run() {
                                scroll.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        });
                    }

                }


            }
            c.close();

            /*
            View item = inflater.inflate(R.layout.dialogo1, null);
            TextView textView1 = item.findViewById(R.id.titulo);*/
        } catch (Exception e) {
            funciones.Logo("chatee",e.getMessage());
        }
        db.close();

    }

    public void Grabar() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        recorder.setAudioEncodingBitRate(16*44100);
        recorder.setAudioSamplingRate(44100);
        File path = new File(Environment.getExternalStorageDirectory() .getPath()+getString(R.string.dir_audio_send));
        if (! path.exists()){
            if (! path.mkdirs()){
                funciones.Logo("grabando", "failed to create directory");

            }else{
                funciones.Logo("grabando", "failed to create directory");
            }
        }
        try {
            String uiid=funciones.GetUIID();
            funciones.Logo("grabando","UIID: "+uiid);
            archivo =new  File(path+"/"+uiid+".mp3");
        } catch (Exception e) {
        }
        recorder.setOutputFile(archivo.getAbsolutePath());
        try {
            recorder.prepare();
        } catch (IOException e) {
        }
        recorder.start();

    }

    public void Detener() {
        try{
            recorder.stop();
            recorder.release();
        }catch (Exception e){
            funciones.Logo("grabando",""+e.getMessage());
        }


    }
    public Boolean PedirPermiso() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            int permsRequestCode = 100;
            String[] perms = {Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE};

            int audio = checkSelfPermission(Manifest.permission.RECORD_AUDIO);
            int sd = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (audio == PackageManager.PERMISSION_GRANTED && sd == PackageManager.PERMISSION_GRANTED ) {

                return true;
            } else {

                requestPermissions(perms, permsRequestCode);
                return false;
            }

        }else{
        }
        return true;
    }
    boolean corriendo=true;


    class Tiempo extends AsyncTask{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            funciones.Logo("grabando","onPreExecute: ");
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
            funciones.Logo("grabando",": "+values[0].toString());
            String result = String.format("%02d:%02d", Integer.parseInt(values[0].toString()) / 100, Integer.parseInt(values[0].toString()) % 100);
            timerecord.setText(result);
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            int time=0;
            while(corriendo){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                time++;

                publishProgress(time);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if(!timerecord.getText().toString().contains("00:00")){
                IsAudio=true;
                EnviarEnable();
            }else{
                IsAudio=false;
                EnviarDisable();
                MensajeEnable();
            }
        }
    }


    public void PlayAudio(String url, SeekBar seekBar) {
        onPlay=seekBar;
        funciones.Logo("reproduce","Repro1:"+url);
        url=url.replace(" ","").replace("\n","");
        File mp3=new File(url);
        if(mp3.exists()){

            funciones.Logo("reproduce","Repro2:"+url);
            try {

                mPlayer.reset();
                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                //mPlayer.setDataSource(context, Uri.parse(url));
                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {


                    }
                });
                mPlayer.setDataSource(url);
                mPlayer.prepare();
                seekBar.setMax(mPlayer.getDuration());
                mPlayer.start();

            } catch (Exception e) {
                funciones.Logo("reproduce","Error: "+e.getMessage());
            }
        }else{
            funciones.Logo("reproduce","Error: No exite el archivo");
            Toast.makeText(context, "Error: No exite el archivo.", Toast.LENGTH_SHORT).show();
        }

    }

    void OnPlay(){
        if(onPlay!=null){
            onPlay.setProgress(mPlayer.getCurrentPosition());
            funciones.Logo("Onplay",mPlayer.getCurrentPosition()+"----"+mPlayer.getDuration());
        }
    }



}
