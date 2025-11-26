package com.example.sinensis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class activity_calendario extends AppCompatActivity {

    ImageButton feliz, regular, mal;
    SeekBar progreso;
    TextView texto_estado;
    public static int valorActual;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);


        feliz = findViewById(R.id.button_bien);
        regular = findViewById(R.id.button_regular);
        mal = findViewById(R.id.button_mal);
        progreso = findViewById(R.id.seekBar_progreso);
        texto_estado = findViewById(R.id.ficha_progreso);

        valorActual = progreso.getProgress();
        progreso.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true; // desactiva el control táctil de la SeekBar
            }
        });

        //Guardamos el
        SharedPreferences sharedPreferences = getSharedPreferences("estado_animo", MODE_PRIVATE);
        SharedPreferences sharedPreferences1 = getSharedPreferences("texto_animo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences1.edit();
        String textoGuardado = sharedPreferences1.getString("texto_animo", "");
        Toast toast = Toast.makeText(activity_calendario.this, getString(R.string.solo_un_dia), Toast.LENGTH_SHORT);

        if (!textoGuardado.isEmpty()) {
            texto_estado.setText(textoGuardado);
        }
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_YEAR);
        // Restaura el valor de la SeekBar guardado para el día actual
        valorActual = sharedPreferences.getInt(String.valueOf(today), valorActual);
        progreso.setProgress(valorActual);

        feliz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int lastDayClicked = sharedPreferences.getInt("ultimoDia", -1);
                if (lastDayClicked != today) {
                    valorActual++;
                    progreso.setProgress(valorActual);
                    String texto = getString(R.string.estado_bien);
                    texto_estado.setText(texto);
                    editor.putString("texto_animo", texto);
                    editor.apply();
                    sharedPreferences.edit().putInt("ultimoDia", today).apply();
                    sharedPreferences.edit().putInt(String.valueOf(today), valorActual).apply(); // Guarda el valor de la SeekBar para el día actual
                } else {
                    toast.show();
                }
            }
        });
        regular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int lastDayClicked = sharedPreferences.getInt("ultimoDia", -1);
                if (lastDayClicked != today) {
                    progreso.setProgress(valorActual);
                    String texto = getString(R.string.estado_regular);
                    texto_estado.setText(texto);
                    editor.putString("texto_animo", texto);
                    editor.apply();
                    sharedPreferences.edit().putInt("ultimoDia", today).apply();
                    sharedPreferences.edit().putInt(String.valueOf(today), valorActual).apply(); // Guarda el valor de la SeekBar para el día actual

                } else {
                    toast.show();
                }
            }
        });
        mal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int lastDayClicked = sharedPreferences.getInt("ultimoDia", -1);
                if (lastDayClicked != today) {
                    valorActual++;
                    progreso.setProgress(valorActual);
                    String texto = getString(R.string.estado_mal);
                    texto_estado.setText(texto);
                    editor.putString("texto_animo", texto);
                    editor.apply();
                    sharedPreferences.edit().putInt("ultimoDia", today).apply();
                    sharedPreferences.edit().putInt(String.valueOf(today), valorActual).apply(); // Guarda el valor de la SeekBar para el día actual

                } else {
                    toast.show();
                }

            }
        });

        if(valorActual == 30){
            SharedPreferences.Editor editor2 = MainActivity.sharedPreferences.edit();
            int h = MainActivity.sharedPreferences.getInt("hojas",0);
            h = h + 1;
            editor2.putInt("hojas", h); // Guarda el nuevo valor de 'hojas'
            editor2.apply();
            activity_ajustes.m = Integer.toString(MainActivity.sharedPreferences.getInt("hojas",0));
        }

    }

}