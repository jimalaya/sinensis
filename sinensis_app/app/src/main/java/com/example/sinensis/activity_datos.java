package com.example.sinensis;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class activity_datos extends AppCompatActivity {

    EditText nombre,edad;
    SeekBar seekbar;
    int grado = 2;
    public static int grado_datos = 2;
    public static int edad_datos;
    public static String nombre_datos;
    TextView n;
    Button btn;
    String txt;
    public static SharedPreferences.Editor editor;
    public static SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos);
        // Convertimos los valores del xml para manejarlos en java
        nombre = findViewById(R.id.nombre);
        edad = findViewById(R.id.edad);
        seekbar = findViewById(R.id.seekbar);
        n = findViewById(R.id.textito);
        btn = findViewById(R.id.button);
        txt = getString(R.string.nivel_medio);
        n.setText(getString(R.string.nivel) + txt);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){ //Guarda el estado de la seekbar para saber el nivel de ansiedad introducido
                grado = seekbar.getProgress();
                if (progress == 0){
                    txt = getString(R.string.nivel_bajo);
                }else if(progress == 1){
                    txt = getString(R.string.nivel_mediobajo);
                }else if(progress == 2){
                    txt = getString(R.string.nivel_medio);
                }else if(progress == 3){
                    txt = getString(R.string.nivel_medioalto);
                }else if(progress == 4){
                    txt = getString(R.string.nivel_alto);
                }
                n.setText(getString(R.string.nivel) + txt);
                grado_datos = progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        btn.setOnClickListener(view -> {
            if(nombre.getText().toString().isEmpty() || edad.getText().toString().isEmpty()){
                AlertDialog.Builder builder = new AlertDialog.Builder(activity_datos.this);
                builder.setMessage(getString(R.string.rellenar_datos))
                        .setPositiveButton(getString(R.string.aceptar), (dialogInterface, i) -> dialogInterface.dismiss());
                AlertDialog alertDialog = builder.create(); //Nos sale una alerta si no hemos introducido algun dato
                alertDialog.show();
            }
            else{
                //Convertimos los valores
                String edad_conversor = edad.getText().toString();
                edad_datos = Integer.parseInt(edad_conversor);
                nombre_datos = nombre.getText().toString();

                // Guarda los datos ingresados por el usuario en SharedPreferences. Obtiene un editor de SharedPreferences para realizar modificaciones
                sharedPreferences = getSharedPreferences("datos12", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                sharedPreferences = getSharedPreferences("datos12", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString("nombre", activity_datos.nombre_datos);
                editor.putInt("edad", activity_datos.edad_datos);
                editor.putInt("estres",activity_datos.grado_datos);

                // Aplica los cambios
                editor.apply();
                Intent intent = new Intent(activity_datos.this, activity_mentores.class);
                startActivity(intent);
            }
        });
    }

}