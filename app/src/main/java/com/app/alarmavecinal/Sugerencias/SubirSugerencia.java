package com.app.alarmavecinal.Sugerencias;

import android.app.ProgressDialog;
import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.LoginPack.Login;
import com.app.alarmavecinal.Principal;
import com.app.alarmavecinal.R;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SubirSugerencia extends AppCompatActivity {
    Funciones funciones;
    Context context;
    TextView de;
    EditText sugerencia;
    String nombre;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_sugerencia);
        context=this;
        funciones=new Funciones(context);
        nombre=funciones.GetNombre();
        de=findViewById(R.id.de);
        sugerencia=findViewById(R.id.sugerencia);

        de.setText("De: "+nombre);
    }

    public void Enviar(View view){
        funciones.Vibrar(funciones.VibrarPush());
        if(sugerencia.getText().toString().trim().length()!=0){
            EnviarSugerencia();
        }else{
            Toast.makeText(context, "¡No puede mandar un mensaje en blanco!", Toast.LENGTH_SHORT).show();
        }

       /* funciones.Conexion("{\"id_usuario\":\""+funciones.GetIdUsuario()+"\",\"sugerencia\":\""+funciones.ToBase64(sugerencia.getText().toString())+"\"}",funciones.GetUrl()+getString(R.string.url_SetSugerencia),"POST");
        Toast.makeText(context, "Gracias por su sugerencia,", Toast.LENGTH_SHORT).show();*/


    }

    public void EnviarSugerencia(){
        dialog = ProgressDialog.show(SubirSugerencia.this, "", "Verificando...", true);
        funciones.AbrirConexion();

        try {

            RequestQueue queue = Volley.newRequestQueue(this);
            String url =funciones.GetUrl()+getString(R.string.url_SubirSugerencia);
            funciones.Logo("sugerencias",url);
            // Request a string response from the provided URL.
            JSONObject data = new JSONObject("{\"id_usuario\":\""+funciones.GetIdUsuario()+"\",\"sugerencia\":\""+sugerencia.getText().toString()+"\"}");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, data,
                    new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    dialog.dismiss();
                    funciones.Logo("sugerencias",response.toString());
                    Toast.makeText(getApplicationContext(), "¡Gracias por su sugerencia!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialog.dismiss();
                    funciones.Logo("sugerencias","That didn't work!: "+error.getMessage());
                    Toast.makeText(getApplicationContext(), "¡Error de conexion!", Toast.LENGTH_SHORT).show();

                }
            });

            // Add the request to the RequestQueue.
            queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
