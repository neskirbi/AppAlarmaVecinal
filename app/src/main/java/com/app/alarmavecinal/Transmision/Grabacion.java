package com.app.alarmavecinal.Transmision;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.R;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class Grabacion extends AppCompatActivity {
    static final int REQUEST_VIDEO_CAPTURE = 1;
    private Camera mCamera;
    private CameraPreview mPreview;
    Camera.PictureCallback picture;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    FrameLayout camera_preview;
    Funciones funciones;
    Context context;
    boolean bandera=true,testigo=false;
    String id_grupo="";
    String id_usuario="";
    String id_transmision="";

    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;
    int orden=0;

    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grabacion);
        context=this;
        funciones=new Funciones(context);
        camera_preview=findViewById(R.id.camera_preview);
        id_grupo=funciones.GetIdGrupo();
        id_usuario=funciones.GetIdUsuario();


        camera_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funciones.Vibrar(funciones.VibrarPush());
            }
        });

        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);



        //set camera to continually auto-focus
        Camera.Parameters params = mCamera.getParameters();
        List<Camera.Size> supportedSizes = params.getSupportedPictureSizes();
        for(int i=0;i<supportedSizes.size();i++){
            Camera.Size sizePicture = (supportedSizes.get(i));
            funciones.Logo("tamaños","tamaño"+i+": "+sizePicture.width+" X "+ sizePicture.height+"");
        }
        Camera.Size sizePicture = (supportedSizes.get(44));
//*EDIT*//params.setFocusMode("continuous-picture");
//It is better to use defined constraints as opposed to String, thanks to AbdelHady
        params.setPictureSize(sizePicture.width, sizePicture.height);
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        mCamera.setParameters(params);

         //set camera to continually auto-focus
    }

    @Override
    protected void onStart() {
        super.onStart();
        PedirIdTransmision();

    }

    private void PedirIdTransmision() {
        Enviar enviar=new Enviar("{\"id_usuario\":\""+id_usuario+"\",\"id_grupo\":\""+id_grupo+"\"}",funciones.GetUrl()+getString(R.string.url_SetStreamming),1,null);
        enviar.executeOnExecutor(threadPoolExecutor);
    }

    @Override
    protected void onStop() {
        super.onStop();
        bandera=false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bandera=false;
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            //funciones.Logo("tstreamming","id_transmision.lengt"+id_transmision.length()+" id_transmision:"+id_transmision);
            if (id_transmision.length()==32) {
                File mediaStorageDir = new File(Environment.getExternalStorageDirectory() .getPath()+getString(R.string.dir_foto_send));
                // This location works best if you want the created images to be shared
                // between applications and persist after your app has been uninstalled.

                // Create the storage directory if it does not exist
                if (! mediaStorageDir.exists()){
                    if (! mediaStorageDir.mkdirs()){
                        Log.d("MyCameraApp", "failed to create directory");
                        //return null;
                    }
                }

                // Create a media file name
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                File pictureFile;

                pictureFile = new File(mediaStorageDir.getPath() + File.separator +
                        "IMG_"+ timeStamp + ".jpg");



                if (pictureFile == null) {
                    Log.i("error", "Error creating media file, check storage permissions");
                    return;
                }

                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();

                    data=null;
                    funciones.Logo("PictureCallback", "Se guardo el archivo: " + pictureFile);
                    String foto64 = funciones.Fileto64(pictureFile);

                    Enviar enviar=new Enviar("{\"id_transmision\":\"" + id_transmision + "\",\"foto\":\"" + foto64 + "\",\"orden\":\""+orden+"\"}",funciones.GetUrl() + getString(R.string.url_GuardarStreamming),2,pictureFile);
                    enviar.executeOnExecutor(threadPoolExecutor);
                    orden++;



                } catch (FileNotFoundException e) {
                    funciones.Logo("PictureCallback", "File not found: " + e.getMessage());
                } catch (Exception e) {
                    funciones.Logo("PictureCallback", "Error accessing file: " + e.getMessage());
                }
            }else if(testigo){
                PedirIdTransmision();
            }
            //funciones.Logo("tstreamming","bandera-->>>"+bandera);
            try{
                if(bandera){
                    //funciones.Logo("tstreamming","bandera2-->>>"+bandera);
                    mCamera.takePicture(null, null, mPicture);
                }
            }catch (Exception e){
                funciones.Logo("tstreamming",e.getMessage());
            }

        }
    };



    /** Create a File for saving an image or video */
    public  File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory() .getPath()+getString(R.string.dir_foto_send));
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }


        return mediaFile;
    }



    class Enviar extends AsyncTask{
        private String data="";
        private String url="";
        private int paso=0;
        private File file=null;
        public Enviar(String data,String url,int paso,File file){
            this.data=data;
            this.url=url;
            this.paso=paso;
            this.file=file;
        }
        @Override
        protected Object doInBackground(Object[] objects) {
            String respuesta=funciones.Conexion(data,url,"POST");

            switch (paso){
                case 1:
                    try {
                        JSONObject jsonObject=new JSONObject(respuesta);
                        id_transmision=jsonObject.getString("id_transmision");
                        funciones.Logo("tstreamming","id_transmision----->: "+id_transmision);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    testigo=true;
                    break;
                case 2:
                    try {
                        JSONObject jsonObject=new JSONObject(respuesta);
                        if(jsonObject.getString("respuesta").contains("1")){
                            funciones.Logo("tstreamming","foto enviada----->: "+file.getAbsolutePath());
                            this.file.delete();
                        }else{
                            Enviar enviar=new Enviar(this.data,this.url,2,this.file);
                            enviar.executeOnExecutor(threadPoolExecutor);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
            }
            return null;
        }
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }


    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }


    /** A basic Camera preview class */
    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;
        private Camera mCamera;

        public CameraPreview(Context context, Camera camera) {
            super(context);
            mCamera = camera;

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
            // deprecated setting, but required on Android versions prior to 3.0
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        public void surfaceCreated(SurfaceHolder holder) {
            // The Surface has been created, now tell the camera where to draw the preview.
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (Exception e) {
                Log.i("error", "Error setting camera preview: " + e.getMessage());
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // empty. Take care of releasing the Camera preview in your activity.
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.

            if (mHolder.getSurface() == null){
                // preview surface does not exist
                return;
            }

            // stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception e){
                // ignore: tried to stop a non-existent preview
            }

            // set preview size and make any resize, rotate or
            // reformatting changes here

            // start preview with new settings
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
                mCamera.takePicture(null, null, mPicture);

            } catch (Exception e){
                Log.i("Error", "Error starting camera preview: " + e.getMessage());
            }
        }
    }
}
