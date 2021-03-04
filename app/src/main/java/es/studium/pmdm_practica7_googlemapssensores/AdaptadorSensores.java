package es.studium.pmdm_practica7_googlemapssensores;

import java.util.List;

import es.studium.pmdm_practica7_googlemapssensores.modelos.Sensores;

public class AdaptadorSensores {
    private List<Sensores> listaSensores;

    public void setListaDeFrases(List<Sensores> listaDeFrases) {
        this.listaSensores = listaDeFrases;
    }
    public AdaptadorSensores(List<Sensores> sensores) {
        this.listaSensores = sensores; }
    public int getItemCount() {
        return listaSensores.size();
    }
}
