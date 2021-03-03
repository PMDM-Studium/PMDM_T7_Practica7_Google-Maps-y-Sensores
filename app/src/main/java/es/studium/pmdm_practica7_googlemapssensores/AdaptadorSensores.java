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

    public void onBindViewHolder(int i) {
// Obtener la frase de nuestra lista gracias al índice i
        Sensores sensores = listaSensores.get(i);
// Qbtgngr los datQS de la lista
        String txtlatitud = String.valueOf(sensores.getLatitud());
        String txtlongitud = String.valueOf(sensores.getLongitud());
        String txtbatery = String.valueOf(sensores.getBattery());
    }
    public int getItemCount() {
        return listaSensores.size();
    }
}
