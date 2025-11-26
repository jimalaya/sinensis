package com.example.sinensis;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.sinensis.databinding.ActivityMapaBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class activity_mapa extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap; // Atributo del mapa
    private ActivityMapaBinding binding;    // Vínculo/enlace de esta actividad con la interfaz de usuario
    private FusedLocationProviderClient fusedLocationProviderClient;    // Para obtener la ubicación del dispositivo móvil.
    private static final int Request_code = 101;    // Para identificar una solicitud específica en onRequestPermissionsResult()
    private double lat, lng;    // Para guardar la localización de usuario


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        binding = ActivityMapaBinding.inflate(getLayoutInflater());     // Inflamos el diseño de la interfaz de usuario
        setContentView(binding.getRoot());      // Establecer el diseño de la interfaz de usuario a partir de la vista raíz de Data Binding

        // Obtenemos una instancia de Fused Location Provider

        fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(this.getApplicationContext());

        // Obtenemos una instancia del fragmento de mapa de Google Maps y configuramos su visibilidad

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        // Establecemos escuchadores de clic en dos vistas de la lupa de zoom con Data Binding
        binding.imgZoomOut.setOnClickListener(this);
        binding.imgZoomIn.setOnClickListener(this);

        // Buscamos la referencia del text view de la info de la búsqueda, y seteamos el contenido según
        // la búsqueda que sea correspondiente
        TextView busqueda = (TextView) findViewById(R.id.busqueda);

        if (activity_actividadLista.act_places == 0){
            busqueda.setText(getString(R.string.parques));
        }else{
            busqueda.setText(getString(R.string.spas));
        }

    }

    // Construimos la URL que se utilizará para hacer una solicitud a la API de Google Places y recuperar lugares cercanos
    public void buscarSitiosCercanos(String sitio){

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+ lat + "," + lng +
                "&radius=1000&type="+ sitio + "&sensor=true&key=" + getResources().getString(R.string.google_api_key);
        //System.out.println("----- " + url);
        Object dataFetch[] = new Object[2];
        dataFetch[0] = mMap;
        dataFetch[1] = url;

        FetchData fetchData = new FetchData();
        fetchData.execute(dataFetch);
    }

    // El objeto mMap se inicializa con el objeto googleMap que se recibe como parámetro.
    // Luego, se llama al método getCurrentLocation() para obtener la ubicación actual del
    // dispositivo y mostrarla en el mapa

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        getCurrentLocation();
    }


    // Comprobamos si la aplicación tiene permiso para acceder a la ubicación del dispositivo.
    // Si el permiso no está otorgado, se solicita al usuario que lo permita.
    // Creamos un objeto LocationRequest que se utiliza para solicitar actualizaciones de ubicación.
    // Después creamos un objeto LocationCallback que se utiliza para recibir actualizaciones de ubicación.
    // Finalmente realizamos la búsqueda de sitios dependiendo del tipo de destino correspondiente.

    private void getCurrentLocation(){
        if(ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_code);
            return;
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(60000);
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(5000);
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                if (locationResult == null){
                    Toast.makeText(getApplicationContext(), getString(R.string.ubi_vacia), Toast.LENGTH_SHORT).show();
                    return;
                }

                for(Location location:locationResult.getLocations()){
                    if(location != null){
                        String texto;
                        if (activity_actividadLista.act_places == 0){
                            texto = getString(R.string.parques);
                        }else{
                            texto = getString(R.string.spas);
                        }
                        Toast.makeText(getApplicationContext(), texto, Toast.LENGTH_SHORT).show();
                    }

                }
            }
        };

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if(location != null){

                    lat = location.getLatitude();
                    lng = location.getLongitude();

                    LatLng latLng = new LatLng(lat, lng);
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Ubicación actual"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                    switch (activity_actividadLista.act_places){

                        case 0:
                            buscarSitiosCercanos("park");
                            break;
                        case 1:
                            buscarSitiosCercanos("spa");
                            break;

                    }

                }

            }
        });
    }

    // Método de devolución de llamada que se llama cuando el usuario responde a la solicitud de permiso
    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch(Request_code){

            case Request_code:
                if(grantResults.length> 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getCurrentLocation();
                }

        }

    }

    // Método que implementa la interfaz View.OnClickListener para manejar los eventos de
    // click en dos botones de lupa, imgZoomIn e imgZoomOut.

    public void onClick(View view) {
        Log.i("click_event","called");
        switch (view.getId()){
            case R.id.imgZoomIn:
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
                break;
            case R.id.imgZoomOut:
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
                break;
        }
    }
}