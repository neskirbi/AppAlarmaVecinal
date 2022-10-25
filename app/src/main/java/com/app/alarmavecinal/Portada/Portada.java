package com.app.alarmavecinal.Portada;

public interface Portada {
    interface PortadaView{
        void IrPrincipal();
    }

    interface PortadaPresenter{
        void GetGrupo();
        void IrPrincipal();
    }

    interface PortadaInteractor{
          void GetGrupo();
    }
}
