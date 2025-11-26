package com.example.sinensis;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import java.util.List;

public class activity_principal extends AppCompatActivity {
    public static ListView listView; //lista de actividades
    public static AppDatabase db; //base de datos en java
    public static int currentDate;
    public static Calendar calendar;
    ImageButton btn_calendario,btn_anadir,btn_ajustes;
    public static Adaptadores adaptador;
    public static List<Actividades> lista;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        //Mostramos el nombre que hemos introducido
        String mi_nombre = MainActivity.sharedPreferences.getString("nombre", "");
        TextView textView = findViewById(R.id.textonombre);
        textView.setText(getString(R.string.bienvenida,mi_nombre));

        //Botones
        btn_calendario = (ImageButton) findViewById(R.id.calendario);
        btn_ajustes = (ImageButton) findViewById(R.id.ajustes);
        btn_anadir = (ImageButton) findViewById(R.id.button_eleccion);

        //Intents
        Intent intentC = new Intent(this,activity_calendario.class);
        Intent intentA = new Intent(this, activity_ajustes.class);
        Intent anadir = new Intent(this, activity_actividades.class);

        //Botones inferiores que nos llevaran a una actividad u otra cuando pulsemos.
        //Lanzar actividad de calendario
        btn_calendario.setOnClickListener(view -> startActivity(intentC));
        //Lanzar actividad de ajustes
        btn_ajustes.setOnClickListener(view -> startActivity(intentA));
        //Lanzar actividad de lista de actividades
        btn_anadir.setOnClickListener(view -> startActivity(anadir));

        //Creamos la list view, y depende de si no hemos metido todavia nuestros datos, es decir, no hemos runeado antes la app (p=0)
        //nos saldrá una lista que parte desde 0.
        listView = findViewById(R.id.list_view);
        if(MainActivity.p == 1){
            lista = activity_mentores.lista_actividades;
        }
        //Adaptador para mostrar la lista. Instancia de la clase Adaptadores creada.
        adaptador = new Adaptadores(this, lista);
        listView.setAdapter(adaptador);

        //Cada vez que pulsemos ne las distintas actividades, nos lleva otra actvidad donde se nos explica mejor
        listView.setOnItemClickListener((adapterView, view, position, l) -> {
            SharedPreferences sharedPreferences = getSharedPreferences("check",MODE_PRIVATE);
            Actividades a = (Actividades) adaptador.getItem(position);
            Intent intentLista = new Intent(view.getContext(), activity_actividadLista.class);
            //Parametros que haran que sea la actividad que queremos que se muestren, nosotros tenemos titulo y descripcion
            intentLista.putExtra("TIT", a.getNombre());
            intentLista.putExtra("DES", a.getDescripcion_larga());
            intentLista.putExtra("POS", position);
            startActivity(intentLista);
        });

        //Shared preferences para que cada dia nos salga un mensaje que nos preguntará nuestro progreso.
        SharedPreferences prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
        calendar = Calendar.getInstance();
        currentDate = calendar.get(Calendar.DAY_OF_MONTH);
        Intent intent_calendario = new Intent(this, activity_calendario.class);
        if(currentDate != prefs.getInt("lastShownDate", 0)){
            // Mostrar el AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.mensaje))
                    .setPositiveButton(getString(R.string.aceptar), (dialog, id) -> {
                        // Guarda el nuevo valor de 'hojas' que ganamos cada dia que iniciamos la app
                        SharedPreferences.Editor editor2 = MainActivity.sharedPreferences.edit();
                        int h = MainActivity.sharedPreferences.getInt("hojas",0);
                        h = h + 10;
                        editor2.putInt("hojas", h);
                        editor2.apply();
                        activity_ajustes.m = Integer.toString(MainActivity.sharedPreferences.getInt("hojas",0)); //mostramos el valor siempre actualizado en ajustes, lo hacemos gracias a esta variable
                        // Guardar la fecha actual como última fecha mostrada
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("lastShownDate", currentDate);
                        editor.apply();
                        startActivity(intent_calendario);
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
}