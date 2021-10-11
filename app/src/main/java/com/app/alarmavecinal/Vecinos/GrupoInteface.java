package com.app.alarmavecinal.Vecinos;

import com.app.alarmavecinal.Models.Grupo;

public interface GrupoInteface {

    interface GrupoView{
        void Error(String error);
        void IraGrupo();
    }

    interface GrupoPresenter{
        void CrearGrupo(String Grupo);
        void Error(String error);
        void DejarGrupo();
        void IraGrupo();
    }

    interface GrupoInteractor{
        void GuardarGrupo(String nombre);
        void DejarGrupo();
    }


}
