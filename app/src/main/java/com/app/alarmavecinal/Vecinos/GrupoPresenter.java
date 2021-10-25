package com.app.alarmavecinal.Vecinos;

import android.content.Context;
import android.util.Log;

import com.app.alarmavecinal.Models.Grupo;

public class GrupoPresenter implements GrupoInteface.GrupoPresenter {
    Context context;
    GrupoView grupoView;
    GrupoInteractor grupoInteractor;
    public GrupoPresenter(GrupoView grupoView,Context context) {
        this.grupoView=grupoView;
        this.context=context;
        grupoInteractor=new GrupoInteractor(this,context);
    }



    @Override
    public void CrearGrupo(String nombre) {
        Log.i("CrearGrupo","presenter");

        if(nombre.replace(" ","").length()!=0){

            grupoInteractor.GuardarGrupo(nombre);
        }else{
            grupoView.Error("El nombre de grupo no puede estar vacío.");
        }

    }

    @Override
    public void Error(String error) {
        grupoView.Error(error);
    }

    @Override
    public void UnirseGrupo(String id) {
        grupoInteractor.UnirseGrupo(id);
    }

    @Override
    public void DejarGrupo() {
        grupoInteractor.DejarGrupo();
    }

    @Override
    public void IraGrupo() {
        grupoView.IraGrupo();
    }
}
