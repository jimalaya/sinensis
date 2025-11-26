package com.example.sinensis;


import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class activity_mentores extends AppCompatActivity {
    public static List<Actividades> lista_actividades = new ArrayList<>();
    public static AppDatabase db;
    Button boton,boton2,boton3;
    public static int mentor_datos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentores);

        //Nombramos los botones para referenciarlas a cada mentor
        boton = (Button) findViewById(R.id.button_ines);
        boton2 = (Button) findViewById(R.id.button_paula);
        boton3 = (Button) findViewById(R.id.button_jimena);

        //Obtenemos una instancia de la clase AppDatabase para la base de datos
        db = AppDatabase.getInstance(getApplicationContext());

        Intent intent = new Intent(this, activity_principal.class);

        //Objeto gson para poder cuardar la lista en sharedPreferences
        Gson gson = new Gson();

        //si pulsamos un boton del respectivo mentor
//Ines
        boton.setOnClickListener(view -> {
            int estres_pref = MainActivity.sharedPreferences.getInt("estres", 0);
            int estres = level(estres_pref);
            lista_actividades = Getlista(estres,0); ////se crea la lsita con los valores guardados a través de un metodo, para mostrarse en la actividad siguiente
            String listaJson = gson.toJson(lista_actividades);
            activity_datos.editor.putString("lista", listaJson); // Guardar la cadena de texto JSON en SharedPreferences
            activity_datos.editor.apply();
            startActivity(intent);
        });
        //Paula
        boton2.setOnClickListener(view -> {
            int estres_pref = MainActivity.sharedPreferences.getInt("estres", 0);
            int estres = level(estres_pref);
            lista_actividades = Getlista(estres,1);
            String listaJson = gson.toJson(lista_actividades);
            activity_datos.editor.putString("lista", listaJson);
            activity_datos.editor.apply();
            startActivity(intent);
        });
        //Jimena
        boton3.setOnClickListener(view -> {
            int estres_pref = MainActivity.sharedPreferences.getInt("estres", 0);
            int estres = level(estres_pref);
            lista_actividades = Getlista(estres,2);
            String listaJson = gson.toJson(lista_actividades);
            activity_datos.editor.putString("lista", listaJson);
            activity_datos.editor.apply();
            startActivity(intent);
        });
    }
    //<-------------  MÉTODOS FUERA DEL ONCREATE ----------->
    private List<Actividades> Getlista(int nivel, int mentor) { //metodo que crea la lista de actividades que estan en la base de datos
        List<Actividades> lista = new ArrayList<>();
        lista = db.ActividadesDAO().selectactividad(nivel,mentor,0);
        return lista;
    }
    public int level(int grado_datos){ //se guarda el valor en preferencias para su uso posterior.
        if(grado_datos == 0 || grado_datos  == 1){
            return 0;
        }else if(grado_datos  == 2 || grado_datos  == 3){
            return 1;
        }else{
            return 2;
        }
    }
}