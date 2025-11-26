package com.example.sinensis;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import java.util.List;

public class activity_actividades extends AppCompatActivity {
    private ListView listView;
    public static AppDatabase db2; //base de datos en java
    public static int p = 0;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividades);

        listView = findViewById(R.id.list_view_actividades); //Lista por la cual se mostrarán todas las actividades.
        db2 = AppDatabase.getInstance(getApplicationContext()); //Instancia de Base de datos de donde se cogeran las actividades.
        List<Actividades> lista_completa = db2.ActividadesDAO().selectAll(); //Seleccionamos todas las filas de la tabla de base de datos
        Adaptadores adaptador2 = new Adaptadores(this, lista_completa); //La mostramos gracias a un adaptador que hemos hecho.
        listView.setAdapter(adaptador2);

        //Cuando tocamos la actividda en concreto
        listView.setOnItemClickListener((adapterView, view, position, l) -> {
            Actividades a = (Actividades) adaptador2.getItem(position);
            for(int i =0;i<activity_principal.lista.size(); i++) {
                if (a.getNombre().equals(activity_principal.lista.get(i).getNombre())) { //If que guarda una Variable que guarda 1 si elegimos una actividad que ya esta establecida en nuestro plan y 0 en caso contario
                    p = 1;
                    break;
                }else{
                    p = 0;
                    if(a.getNombre().equals("Aprendes sobre los ajolotes") || a.getNombre().equals("Plan de ocio") || a.getNombre().equals("Cocinar Arepas")){ //If que identifica si seleccionamos a las actividades bloqueadas y que a su vez mediante otro if se guarda en una variable si tenemos mas o menos de 50 tokens
                        if(MainActivity.sharedPreferences.getInt("hojas",0) >= 100){
                            p = 0;
                        }else{
                            p = 2;
                        }
                    }
                }
            }
            //Mensajes que te indican si puedes seleccionar o no la actividad
            if(p == 1){
                Toast toast = Toast.makeText(activity_actividades.this, getString(R.string.actividad_ya_anadida), Toast.LENGTH_SHORT);
                toast.show();
            }else if(p==0){
                //Se añade la lista al plan y ademas se guarda para no perder la elección
                activity_principal.lista.add(a);
                SharedPreferences.Editor editor = MainActivity.sharedPreferences.edit();
                Gson gson = new Gson();
                String listaJson = gson.toJson(activity_principal.lista); // Convertir la lista en una representación JSON usando Gson
                editor.putString("lista", listaJson); // Guardar la lista actualizada en SharedPreferences
                editor.apply(); // Aplicar los cambios
                activity_principal.adaptador.notifyDataSetChanged();
                Toast toast = Toast.makeText(activity_actividades.this, getString(R.string.actividad_seleccionada), Toast.LENGTH_SHORT);
                toast.show();
            }else{
                Toast toast = Toast.makeText(activity_actividades.this, "", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}