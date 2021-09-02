package com.app.alarmavecinal.ChatFb;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.alarmavecinal.BuildConfig;
import com.app.alarmavecinal.Estructuras.Mensaje;
import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SalaChat extends AppCompatActivity {
    private static final int SELECT_FILE =0 ;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    Funciones funciones;
    Context context;
    String id_grupo="",id_usuario="",nombre="";
    ScrollView scroll;
    int position=0;
    ImageView enviar,grabar,cancelar,attach,preview_imagen,cam;
    EditText mensaje;
    AdaptadorMensajes adaptadorMensajes;
    ArrayList<Mensaje> mensajes=new ArrayList<Mensaje>();
    LinearLayout chat;



    boolean corriendo=true,sienvia=false;
    boolean reproduciendo = false,IsAudio=false,IsImagen=false;
    FrameLayout grabacion;
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
    private String Imagen_url="";
    private View cancel_img;
    private double porcentaje=0.20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sala_chat);
        context=this;
        funciones=new Funciones(context);
        id_grupo=funciones.GetIdGrupo();
        id_usuario=funciones.GetIdUsuario();
        nombre=funciones.GetNombre();

        enviar=findViewById(R.id.enviar);
        grabar=findViewById(R.id.grabar);
        cancelar=findViewById(R.id.cancelar);
        mensaje=findViewById(R.id.mensaje);
        chat=findViewById(R.id.chat);
        attach=findViewById(R.id.attach);
        preview_imagen=findViewById(R.id.preview_imagen);
        cam=findViewById(R.id.cam);
        cancel_img=findViewById(R.id.cancel_img);


        //ngrupo=findViewById(R.id.ngrupo);
        timerecord=findViewById(R.id.timerecord);
        grabacion=findViewById(R.id.grabacion);
        //ngrupo.setText(funciones.GetNombreGrupo());

        scroll=findViewById(R.id.scroll);


        reproduciendo=true;
        Reproduciendo reproduciendo=new Reproduciendo();
        reproduciendo.executeOnExecutor(threadPoolExecutor);

        MobileAds.initialize(this);

        AdView m = findViewById(R.id.banner);
        AdRequest adRequest = null;
        if (BuildConfig.DEBUG) {
            adRequest=new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
            funciones.Logo("anuncio","debug");
        }else{
            adRequest=new AdRequest.Builder().build();
        }
        m.loadAd(adRequest);

        if (id_grupo!=""){
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference("chat-"+id_grupo);//Sala de chat

            databaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Mensaje mensaje=snapshot.getValue(Mensaje.class);
                    mensajes.add(new Mensaje(mensaje.getId_mensaje(),mensaje.getId_usuario(),mensaje.getNombre(),mensaje.getMensaje(),mensaje.getAudio(),mensaje.getImagen(),mensaje.getFile(),mensaje.getNombrefile(),mensaje.getUbicacion(),mensaje.getFecha()));

                    //adaptadorMensajes=new AdaptadorMensajes(context,mensajes);

                    LayoutInflater inflater_contenedor = getLayoutInflater();
                    View item =null;
                    if(!mensajes.get(position).getId_usuario().contains(id_usuario)){
                        item = inflater_contenedor.inflate(R.layout.dialogo1, null);
                    }else{
                        item = inflater_contenedor.inflate(R.layout.dialogo2, null);


                    }


                    TextView textView1 = item.findViewById(R.id.nombre);
                    textView1.setText(mensajes.get(position).getNombre());

                    LinearLayout contenedor = item.findViewById(R.id.contenedor);

                    TextView textView3 = item.findViewById(R.id.hora);
                    textView3.setText(mensajes.get(position).getFecha());
                    View vista=null;

                    if(mensajes.get(position).getMensaje().length()>0 && mensajes.get(position).getAudio().length()==0 && mensajes.get(position).getImagen().length()==0){
                        vista=PintarMensaje();
                    }else if(mensajes.get(position).getAudio().length()>0){
                        vista=PintarAudio();
                    }else if(mensajes.get(position).getImagen().length()>0){
                        vista=PintarImagen();
                    }

                    contenedor.addView(vista);

                    chat.addView(item);



                    //chat.setAdapter(adaptadorMensajes);

                    position++;
                    scroll.post(new Runnable() {
                        @Override
                        public void run() {
                            scroll.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        mensaje.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(mensaje.getText().toString().replace(" ","").length()==0){
                    EnviarDisable();
                    sienvia=false;
                }else{
                    EnviarEnable();
                    sienvia=true;


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

        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funciones.Vibrar(funciones.VibrarPush());
                AbrirGaleria();
            }
        });

        cancel_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funciones.Vibrar(funciones.VibrarPush());
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) preview_imagen.getLayoutParams();
                params.height = 0;
// existing height is ok as is, no need to edit it
                preview_imagen.setLayoutParams(params);
                cancel_img.setVisibility(View.GONE);
                cam.setVisibility(View.VISIBLE);
                IsImagen=false;
                if(mensaje.getText().toString().replace(" ","").length()==0){
                    EnviarDisable();
                    sienvia=false;
                }


            }
        });



    }


    private View PintarImagen() {
        LayoutInflater inflater_imagen = getLayoutInflater();
        View vista = inflater_imagen.inflate(R.layout.imagen_globo, null);
        ImageView imageView=vista.findViewById(R.id.imagen);
        funciones.Logo("urlimagen",funciones.GetUrl()+context.getResources().getString(R.string.url_DescargaImagen)+"/"+funciones.GetIdGrupo()+"/"+mensajes.get(position).getImagen());
        //Picasso.with(context).load(funciones.GetUrl()+context.getResources().getString(R.string.url_DescargaImagen)+"/"+funciones.GetIdGrupo()+"/"+mensajes.get(position).getImagen()).into(imageView);
        funciones.DescargarArchivo(mensajes.get(position).getImagen(),"imagen",imageView);


        //imageView.setImageBitmap(bitmap);



        TextView textView2 = vista.findViewById(R.id.mensaje);
        textView2.setText(mensajes.get(position).getMensaje());

        return vista;
    }

    private View PintarAudio() {
        LayoutInflater inflater_audio = getLayoutInflater();
        View vista = inflater_audio.inflate(R.layout.audio_globo, null);
        ImageView pausa = vista.findViewById(R.id.pause);
        ImageView play = vista.findViewById(R.id.play);
        //final String url=funciones.ToFile(c.getString(c.getColumnIndex("mensaje")),c.getString(c.getColumnIndex("id_chat")));
        final String url=funciones.DescargarArchivo(mensajes.get(position).getAudio(),"audio",null);
        final SeekBar progreso = vista.findViewById(R.id.progreso);

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

                    try{
                        PlayAudio(url,progreso);
                        funciones.Logo("fbmensaje",url);
                    }catch (Exception e){
                        Toast.makeText(context, "El audio no esta listo...", Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });
        return vista;
    }

    private View PintarMensaje() {
        LayoutInflater inflater_texto = getLayoutInflater();
        View item2 = inflater_texto.inflate(R.layout.texto_globo, null);
        TextView textView2 = item2.findViewById(R.id.mensaje);
        textView2.setText(mensajes.get(position).getMensaje());
        return item2;
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

    public void Cancelar(){
        funciones.Vibrar(funciones.VibrarPush());
        EnviarDisable();
        MensajeEnable();
        IsAudio=false;
        archivo=null;
        timerecord.setText("00:00");
        sienvia=false;
    }


    public void Enviar(View view){
        funciones.Vibrar(funciones.VibrarPush());
        if(sienvia){
            if(IsAudio){
                EnviaAudio();
            }else if(IsImagen){
                EnviaImagen();
            }else{
                EnviaTexto();

            }
        }
        sienvia=false;


    }



    private void EnviaImagen() {
        String id_imagen=funciones.GetUIID();
        String imagen=funciones.Fileto64(new File(Imagen_url));
        EnviandoImagen enviandoImagen=new EnviandoImagen(id_imagen,imagen,mensaje.getText().toString());
        enviandoImagen.executeOnExecutor(threadPoolExecutor);
        EnviarDisable();
        IsImagen=false;
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) preview_imagen.getLayoutParams();
        params.height = 0;
// existing height is ok as is, no need to edit it
        preview_imagen.setLayoutParams(params);
        sienvia=false;
        mensaje.setText("");
        cancel_img.setVisibility(View.GONE);
        cam.setVisibility(View.VISIBLE);
    }

    public void EnviaAudio() {
        funciones.Logo("tiempo",timerecord.getText().toString());
        try {
            if(archivo.exists() &&  !timerecord.getText().toString().contains("00:01")){
                funciones.Logo("grabando",""+archivo);
                if(archivo.exists()){
                    //funciones.GuardarChat(funciones.Fileto64(archivo),"2");
                    String id_audio=funciones.GetUIID();
                    String audio=funciones.Fileto64(archivo);
                    EnviandoAudio enviandoAudio=new EnviandoAudio(id_audio,audio);
                    enviandoAudio.executeOnExecutor(threadPoolExecutor);


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


    private void EnviaTexto() {
        databaseReference.push().setValue(new Mensaje(funciones.GetUIID(),id_usuario,nombre,mensaje.getText().toString(),"","","","","",funciones.GetDate()));
        mensaje.setText("");
        //EnviarNotification();
    }

    class Tiempo extends AsyncTask {
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
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sienvia=true;
                EnviarEnable();
            }else{
                IsAudio=false;
                EnviarDisable();
                MensajeEnable();
            }
        }
    }


    public void EnviarEnable(){
        enviar.setEnabled(true);
        enviar.setVisibility(View.VISIBLE);
        grabar.setVisibility(View.GONE);
    }
    public void EnviarDisable(){
        enviar.setEnabled(false);
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
        }
    }

   class Reproduciendo extends AsyncTask{
       @Override
       protected Object doInBackground(Object[] objects) {

           while(reproduciendo){

               OnPlay();
               try {
                   Thread.sleep(200);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }

           }
           return null;
       }
   }

   class EnviandoAudio extends AsyncTask{
        private String id_audio="";
        private String audio="";
       public EnviandoAudio(String id_audio, String audio) {
           this.id_audio=id_audio;
           this.audio=audio;
       }

       @Override
       protected void onProgressUpdate(Object[] values) {
           super.onProgressUpdate(values);
           Toast.makeText(context, "No se pudo cargar audio", Toast.LENGTH_SHORT).show();
       }

       @Override
       protected Object doInBackground(Object[] objects) {
           if(funciones.CargarAudio(id_audio,audio)){
               databaseReference.push().setValue(new Mensaje(id_audio,id_usuario,nombre,"",id_audio+".mp3","","","","",funciones.GetDate()));
               //EnviarNotification();
           }else{
               publishProgress("1");
           }
           return null;
       }
   }

    class EnviandoImagen extends AsyncTask{
        private String id_imagen="";
        private String imagen="";
        private String mensaje="";
        public EnviandoImagen(String id_imagen, String imagen,String mensaje) {
            this.id_imagen=id_imagen;
            this.imagen=imagen;
            this.mensaje=mensaje;
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
            Toast.makeText(context, "No se pudo cargar imagen", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            if(funciones.EnviarArchivo(id_imagen,imagen,"imagen")){
                databaseReference.push().setValue(new Mensaje(id_imagen,id_usuario,nombre,mensaje,"",id_imagen+".jpg","","","",funciones.GetDate()));
                //EnviarNotification();
            }else{
                publishProgress("1");
            }
            return null;
        }
    }


    private Intent requestFileIntent;
    private ParcelFileDescriptor inputPFD;
    public void AbrirGaleria(){
        requestFileIntent = new Intent(Intent.ACTION_PICK);
        requestFileIntent.setType("image/jpg");
        startActivityForResult(requestFileIntent, 0);
        /*Intent intent = new Intent();
        intent.setType("");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccione una imagen"), SELECT_FILE);*/
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri selectedImageUri = null;
        Uri selectedImage;

        String filePath = null;
        switch (requestCode) {
            case SELECT_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri currImageURI = data.getData();
                    Imagen_url=GetRealPathFromURI(currImageURI);
                    if(Imagen_url!=null){
                        funciones.Logo("urlimagen",GetRealPathFromURI(currImageURI)+"");
                        //File mSaveBit = new File(GetRealPathFromURI(currImageURI)); // Your image file

                        Bitmap bitmap = BitmapFactory.decodeFile(GetRealPathFromURI(currImageURI));

                        preview_imagen.setImageBitmap(Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth()*(porcentaje)), (int) (bitmap.getHeight()*(porcentaje)), false));
                        //preview_imagen.setImageBitmap(bitmap);
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) preview_imagen.getLayoutParams();
                        params.height = params.width;
// existing height is ok as is, no need to edit it
                        preview_imagen.setLayoutParams(params);
                        IsImagen=true;
                        sienvia=true;
                        EnviarEnable();
                        cancel_img.setVisibility(View.VISIBLE);
                        cam.setVisibility(View.GONE);
                    }else{
                        Toast.makeText(context, "Falló el cargar imagen.", Toast.LENGTH_SHORT).show();
                    }


                }
                break;
        }

    }

    public String GetRealPathFromURI(Uri contentUri) {

        // can post image
        String [] proj={MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery( contentUri,
                proj, // Which columns to return
                null,       // WHERE clause; which rows to return (all rows)
                null,       // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }
}
