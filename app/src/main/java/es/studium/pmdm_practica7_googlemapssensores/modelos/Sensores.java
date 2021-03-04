package es.studium.pmdm_practica7_googlemapssensores.modelos;

public class Sensores {
    private String Latitud;
    private String Longitud;
    private int battery;

    private long id; // El ID de la BD

    public Sensores(String Latitud, String Longitud, int battery) {
        this.Latitud = Latitud;
        this.Longitud = Longitud;
        this.battery = battery;
    }

    // Constructor para cuando instanciamos desde la BD
    public Sensores(String Latitud, String Longitud, int battery, long id) {
        this.Latitud = Latitud;
        this.Longitud = Longitud;
        this.battery = battery;
        this.id = id;
    }

    public String getLatitud() {
        return Latitud;
    }

    public void setLatitud(String latitud) {
        Latitud = latitud;
    }

    public String getLongitud() {
        return Longitud;
    }

    public void setLongitud(String longitud) {
        Longitud = longitud;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return Latitud+","+Longitud+","+battery;
    }
}