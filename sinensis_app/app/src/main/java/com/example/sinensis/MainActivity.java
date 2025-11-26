package com.example.sinensis;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static SharedPreferences sharedPreferences; //Variable correspondiente a shared preferences para guardar nuestros datos
    public static int p; // Variable que servirá para saber si hemos guardado datos o no para utilizarlo en otras clases
    public static List<Actividades> listaRecuperada; //Variable donde se almacenará la lista guardada de actividades

    @Override
    public void onCreate(Bundle savedInstanceState) {

        sharedPreferences = getSharedPreferences("datos12", Context.MODE_PRIVATE);
        int datos = carga(sharedPreferences); //Variable que nos indica si hay datos guardados
        //Implementación Splash Screen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(datos==1) { //Hay datos en sharedPreferences
                    Intent intent2 = new Intent(MainActivity.this, activity_principal.class);
                    startActivity(intent2); //pasamos a la activity principal
                    finish();
                }else{ //No hay datos en sharedPreferences
                    Intent intent = new Intent(MainActivity.this, activity_datos.class);
                    startActivity(intent); //pasamos a la actividad donde guadaremos nuestros datos
                    finish();
                }
            }
        },2000);
    }
    public int carga(SharedPreferences sharedPreferences){ //metodo que recupera los datos y además nos dice si hay elementos guadados
        // Recuperamos los datos guardados en SharedPreferences
        String nombre = sharedPreferences.getString("nombre", "");
        int edad = sharedPreferences.getInt("edad", 0);
        int gradoEstres = sharedPreferences.getInt("estres", 0);
        String listaJson = sharedPreferences.getString("lista", null);
        activity_ajustes.m = Integer.toString(sharedPreferences.getInt("hojas",0));
        activity_actividadLista.hojas = sharedPreferences.getInt("hojas",0);
        Gson gson = new Gson();
        // Convertir la cadena de texto JSON a una lista de objetos "Item"
        if (listaJson != null) {
            Type tipoLista = new TypeToken<List<Actividades>>(){}.getType();
            List<Actividades> listaRecuperada = gson.fromJson(listaJson, tipoLista);
            activity_principal.lista = listaRecuperada;
        }
        if(sharedPreferences.contains("nombre") && sharedPreferences.contains("edad") && sharedPreferences.contains("estres")) {
            return 1;
        }else{
            p = 1;
            return 0;
        }
    }









}