package es.studium.pmdm_practica7_googlemapssensores;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import es.studium.pmdm_practica7_googlemapssensores.controllers.BDController;
import es.studium.pmdm_practica7_googlemapssensores.modelos.Sensores;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener{

    private List<Sensores> listaSensores;
    private GoogleMap mapa;
    private BDController bdController;
    LatLng MiPosicionActual;
    String Cordenadas;
    String Latitud;
    String Longitud;
    String battery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Comprobamos los permisos de GPS, si no los tiene nos preguntara para dar permisos
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new
                    String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        }
        else
        {
            //Obtenemos el mapa de forma asíncrona (notificará cuando esté lista)
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa);
            mapFragment.getMapAsync((OnMapReadyCallback) this);

            //Crear el controlador
            bdController = new BDController(MainActivity.this);

            // Por defecto es una lista vacía
            listaSensores = new ArrayList<>();

            //Ejecutamos el metodo
            locationStart();
        }
    }
    private void locationStart()
    {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setMainActivity(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        //Comprobamos qque el GPS este encendido
        if (!gpsEnabled)
        {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        //Volvemos a comprobar los permisos
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }

        //Actualizamos la localizacion cada 5 minutos
        //mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 300000, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300000, 0, (LocationListener) Local);
    }
    public void setLocation(Location loc)
    {
        refrescarListaSensores();
        //Obtener la dirección de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0)
        {
            try
            {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty())
                {
                    //Optenemos el nivel de bateria
                    IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                    Intent batteryStatus = registerReceiver(null, ifilter);
                    Double level = Double.valueOf(batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1));
                    Double scale = Double.valueOf(batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1));
                    Double bateria = (level/scale)*100;
                    battery = String.valueOf(bateria);

                    //Ponemos Marcador en posicion actual
                    MiPosicionActual = new LatLng(loc.getLatitude(), loc.getLongitude());
                    mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(MiPosicionActual, 15));
                    mapa.addMarker(new MarkerOptions()
                            .position(MiPosicionActual)
                            .title(Cordenadas)
                            .snippet(battery+" %")
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.mipmap.ic_marcadorpasitospersonalizado_round))
                                    .anchor(0f, 1f));
//                                    .fromResource(android.R.drawable.ic_menu_myplaces))
//                            .anchor(0.5f, 0.5f));

                    //Guardamos en la BD
                    Sensores sensores = new Sensores(Latitud, Longitud, battery);
                    //Comprobamos si se ha guardado
                    long id = bdController.nuevoSensor(sensores);
                    if (id == -1) {
                        //De alguna manera ocurrio un error
                        Toast.makeText(MainActivity.this, R.string.errorGuardar, Toast.LENGTH_SHORT).show();
                    } else {
                        //Terminar
                        Toast.makeText(MainActivity.this, R.string.mensajeGPSMarcador, Toast.LENGTH_SHORT).show();
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
        mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL); //Indicamos tipo de mapa
        mapa.getUiSettings().setZoomControlsEnabled(true); //Pone los botones de aumentar y reducir tamaño
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mapa.setMyLocationEnabled(true);
            mapa.getUiSettings().setCompassEnabled(true);
        }
    }

    @Override public void onMapClick(LatLng puntoPulsado){
        mapa.addMarker(new MarkerOptions().position(puntoPulsado)
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
    }
    public class Localizacion implements LocationListener
    {
        MainActivity mainActivity;
        public MainActivity getMainActivity()
        {
            return mainActivity;
        }
        public void setMainActivity(MainActivity mainActivity)
        {
            this.mainActivity = mainActivity;
        }
        @Override
        public void onLocationChanged(Location loc)
        {
            // Este método se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la detección de un cambio de ubicación
            Latitud=String.valueOf(loc.getLatitude());
            Longitud=String.valueOf(loc.getLongitude());

            Cordenadas = loc.getLatitude() + ", " + loc.getLongitude();
            this.mainActivity.setLocation(loc);
        }
        @Override
        public void onProviderDisabled(String provider)
        {
            // Este método se ejecuta cuando el GPS es desactivado
            Toast.makeText(getApplicationContext(), R.string.mensajeGPSDesactivado, Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onProviderEnabled(String provider)
        {
            // Este método se ejecuta cuando el GPS es activado
            Toast.makeText(getApplicationContext(), R.string.mensajeGPSActivado, Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            switch (status)
            {
                case 0:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case 1:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
                case 2:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
            }
        }
    }

    BroadcastReceiver onBattery=new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            int pct= 100 * intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 1)
                    / intent.getIntExtra(BatteryManager.EXTRA_SCALE, 1);
        }
    };
    public void refrescarListaSensores() {
        //Rellenamos la lista con los datos de la BD
        listaSensores = bdController.obtenerSensor();
        //Si la lista no esta vacia
        if (listaSensores!=null){
            //Recorremos la lista
            for (int i=0; i<listaSensores.size();i++) {
                //Cogemos todos los valores de la lista a un string
                String lista = String.valueOf(listaSensores.get(i));
                //Separamos los valores de la lista separados por una coma
                String[] separador = lista.split(",");
                Double lati = Double.valueOf(separador[0]);
                Double longi = Double.valueOf(separador[1]);
                String corde= lati+", "+longi;
                String bate = separador[2];
                //Ponemos Marcador en posicion Guardada
                LatLng MiPosicionActual2 = new LatLng(lati, longi);
                mapa.addMarker(new MarkerOptions()
                        .position(MiPosicionActual2)
                        .title(corde)
                        .snippet(bate+"%")
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.mipmap.ic_marcadorpasitospersonalizado_round))
                        .anchor(0f, 1f));
//                                    .fromResource(android.R.drawable.ic_menu_myplaces))
//                            .anchor(0.5f, 0.5f));
                //Toast.makeText(MainActivity.this, lista, Toast.LENGTH_SHORT).show();
            }
        }
    }
}