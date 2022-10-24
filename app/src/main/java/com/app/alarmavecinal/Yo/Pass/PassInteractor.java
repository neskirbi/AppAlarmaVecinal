package com.app.alarmavecinal.Yo.Pass;

import android.content.Context;
import android.util.Log;

import com.app.alarmavecinal.Metodos;
import com.app.alarmavecinal.Principal.Principal;
import com.app.alarmavecinal.Yo.Datos.DatosInterface;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PassInteractor implements Pass.PassInteractor {
    PassPresenter passPresenter;
    Context context;
    Metodos metodos;

    public PassInteractor(PassPresenter passPresenter, Context context) {
        this.passPresenter=passPresenter;
        this.context=context;
        metodos=new Metodos(context);
    }

    @Override
    public void UpdatePass(String pass, String pass1, String pass2) {
        metodos.AbrirConexion();

        int cont = 0;
        if (pass.length() == 0) {
            metodos.Toast("Debe ingresar su contraseña.");
            cont++;
        }

        if (pass1.length() == 0) {
            metodos.Toast( "Debe ingresar el campo nueva contraseña.");
            cont++;
        }

        if (pass2.length() == 0) {
            metodos.Toast( "Debe ingresar en campo confirmar contraseña.");
            cont++;
        }

        if (!pass1.equals(pass2)) {
            Log.i("ActualizarPass",pass1 +"!= "+pass2);
            metodos.Toast( "Las contraseñas deben ser iguales.");
            cont++;
        }



        if (cont == 0) {

            JsonArray jsonArray=new JsonArray();
            JsonObject jsonObject=new JsonObject();

            jsonObject.addProperty("id",metodos.GetIdUsuario());
            jsonObject.addProperty("pass",pass);
            jsonObject.addProperty("pass1",pass1);
            jsonArray.add(jsonObject);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(metodos.GetUrl())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            PassInterface peticion=retrofit.create(PassInterface.class);

            Call<JsonArray> call= peticion.UpdatePass(jsonArray);
            call.enqueue(new Callback<JsonArray>() {
                @Override
                public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                    Log.i("ActualizarPass", String.valueOf(response.code()));
                    if(response.body()!=null){
                        Log.i("ActualizarPass",response.body().toString());
                        Log.i("ActualizarPass",response.body().get(0).getAsJsonObject().get("status").toString());
                        if(metodos.IsSuccess(response.body())){
                            metodos.Toast(metodos.GetMsn(response.body()));
                            passPresenter.Salir();
                        }else{
                            metodos.Toast(metodos.GetMsn(response.body()));
                        }


                    }else{
                        metodos.Toast("Error de servidor.");
                    }
                }

                @Override
                public void onFailure(Call<JsonArray> call, Throwable t) {
                    Log.i("ActualizarPass","Erro:"+t.getMessage());
                    metodos.Toast("Erro:"+t.getMessage());
                }
            });



        }
    }
}
