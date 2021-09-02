package com.app.alarmavecinal.EditarInfo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.app.alarmavecinal.Principal;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.LoginPack.Login;
import com.app.alarmavecinal.R;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Datos extends AppCompatActivity {
    EditText nombres,apellidos,direccion,pass;
    Context context;
    Funciones funciones;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos);
        setTitle("Editar Info.");
        context=this;
        funciones=new Funciones(context);

        nombres=findViewById(R.id.nombre);
        apellidos=findViewById(R.id.apellidos);
        direccion=findViewById(R.id.direccion);
        pass=findViewById(R.id.pass);

        Edit();
    }

    public void Edit(){
        dialog = ProgressDialog.show(Datos.this, "", "Cargando...", true);
        funciones.AbrirConexion();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url =funciones.GetUrl()+getString(R.string.url_edit)+"/"+funciones.GetIdUsuario()+"/edit";
        funciones.Logo("edit",url);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        // Display the first 500 characters of the response string.
                        funciones.Logo("edit",response);
                        try {

                            JSONObject jsonObject=new JSONObject(response);

                            nombres.setText(jsonObject.get("nombres").toString());
                            apellidos.setText(jsonObject.get("apellidos").toString());
                            direccion.setText(jsonObject.getString("direccion"));
                            pass.setText(jsonObject.getString("pass"));

                        } catch (Exception e) {
                            funciones.Logo("edit",e.getMessage());

                        }

                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "¡Sin Servicio!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),Principal.class));
                dialog.dismiss();
                funciones.Logo("login","That didn't work!");

            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void GetInfo() {
        String data="{\"id_usuario\":\""+funciones.GetIdUsuario()+"\"}";
        String respuesta=funciones.Conexion(data,funciones.GetUrl()+getString(R.string.url_GetInfoToEdit),"POST");


        try {

            JSONArray jsonArray=new JSONArray(respuesta);
            if(jsonArray.length()!=0){
                JSONObject jsonObject=new JSONObject(jsonArray.get(0).toString());

                nombres.setText(jsonObject.get("nombres").toString());
                apellidos.setText(jsonObject.get("apellidos").toString());
                direccion.setText(jsonObject.getString("direccion"));
                pass.setText(jsonObject.getString("pass"));
            }else{
                Toast.makeText(this, "¡Datos Incorrectos!", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void Actualizar(View view){
        funciones.Vibrar(funciones.VibrarPush());
        String n=nombres.getText().toString(),a=apellidos.getText().toString(),d=direccion.getText().toString(),p=pass.getText().toString();

        int cont=0;
        if(n.length()==0){
            Toast.makeText(context, "Debe ingresar su nombre.", Toast.LENGTH_SHORT).show();
            cont++;
        }

        if(a.length()==0){
            Toast.makeText(context, "Debe ingresar sus apellidos.", Toast.LENGTH_SHORT).show();
            cont++;
        }

        if(d.length()==0){
            Toast.makeText(context, "Debe ingresar su dirección o numero de casa.", Toast.LENGTH_SHORT).show();
            cont++;
        }

        if(p.length()==0){
            Toast.makeText(context, "Debe ingresar su contraseña.", Toast.LENGTH_SHORT).show();
            cont++;
        }

         if(cont==0){
             funciones.AbrirConexion();
             RequestQueue queue = Volley.newRequestQueue(this);
             String url =funciones.GetUrl()+getString(R.string.url_edit)+"/"+funciones.GetIdUsuario();

             // Request a string response from the provided URL.
             try {
                 JSONObject data= new JSONObject("{\"id_usuario\":\""+funciones.GetIdUsuario()+"\",\"nombres\":\""+n+"\",\"apellidos\":\""+a+"\",\"direccion\":\""+d+"\",\"pass\":\""+p+"\"}");
                 funciones.Logo("update",url);
                 JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url,data,
                         new Response.Listener<JSONObject>() {
                             @Override
                             public void onResponse(JSONObject response) {
                                 funciones.Logo("update",response.toString());
                                 funciones.LogOut();
                                 startActivity(new Intent(getApplicationContext(), Login.class));

                             }


                         }, new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {
                         funciones.Logo("update","That didn't work!: "+error.getMessage());

                     }
                 });

                 // Add the request to the RequestQueue.
                 queue.add(jsonObjectRequest);
             } catch (JSONException e) {
                 funciones.Logo("registro","That didn't work!: "+e.getMessage());
             }


             ///////////////////////
           /*String data="{\"id_usuario\":\""+funciones.GetIdUsuario()+"\",\"nombres\":\""+n+"\",\"apellidos\":\""+a+"\",\"direccion\":\""+d+"\",\"pass\":\""+p+"\"}";
            String url =funciones.GetUrl()+getString(R.string.url_SetInfoEdit);
            response=funciones.Conexion(data,url,"POST");

            try {
                JSONObject jsonObject=new JSONObject(response);
                if(jsonObject.get("respuesta").toString().contains("1")){
                    Toast.makeText(context, "¡Datos actualizados!", Toast.LENGTH_SHORT).show();
                    funciones.LogOut();
                    startActivity(new Intent(this, Login.class));
                }else{
                    Toast.makeText(context, "¡Error al actualizar al datos!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }*/

        }
    }
}
