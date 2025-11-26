package com.example.sinensis;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class activity_ajustes extends AppCompatActivity {
    public Button nosotras, app, ods, datos, progreso;
    public static String texto,texto2, texto3, m;
    public static List<Actividades> graves;
    public static List<String> lista_nombres;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        //Creamos la lista de visualización
        app = (Button) findViewById(R.id.sobre_app);
        nosotras = (Button) findViewById(R.id.sobre_nosotras);
        ods = (Button) findViewById(R.id.ods);
        datos = (Button) findViewById(R.id.misdatos);
        progreso = (Button) findViewById(R.id.progreso);
        app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                texto = getString(R.string.sobre_la_app);
                showPopUp(view, null, texto,null,null,0, 0);
            }
        });
        nosotras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                texto = getString(R.string.creadoras);
                showPopUp(view, null, texto,null,null,0, 0);
            }
        });
        ods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                texto = getString(R.string.ods);
                showPopUp(view, null, texto,null,null,1, 0);
            }
        });
        datos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mi_nombre = MainActivity.sharedPreferences.getString("nombre", "");;
                int edad = MainActivity.sharedPreferences.getInt("edad", 0);
                String mi_edad = Integer.toString(edad);
                int grado = MainActivity.sharedPreferences.getInt("estres", 0);
                String mi_grado_estres = "";

                if (grado == 0){
                    mi_grado_estres = getString(R.string.nivel_bajo);
                }else if(grado == 1){
                    mi_grado_estres = getString(R.string.nivel_mediobajo);
                }else if(grado == 2){
                    mi_grado_estres = getString(R.string.nivel_medio);
                }else if(grado == 3){
                    mi_grado_estres = getString(R.string.nivel_medioalto);
                }else if(grado == 4){
                    mi_grado_estres = getString(R.string.nivel_alto);
                }

                texto = getString(R.string.datos,mi_nombre,mi_edad,mi_grado_estres);
                showPopUp(view, null,texto, null,null,0, 0);
            }
        });

        progreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                graves = act(activity_principal.lista);
                lista_nombres = obtenername(graves);
                String cadena = String.join(", ", lista_nombres);
                texto = getString(R.string.progreso);
                texto2= getString(R.string.progreso2);
                texto3 = cadena;
                showPopUp(view, m,texto, texto2,texto3,0, 1);
            }

        });

    }

    //<-------------  MÉTODOS FUERA DEL ONCREATE ----------->

    public void showPopUp(View view, String m, String texto, String texto2,String texto3, int img_ods, int progreso) { //Variamos que se mostrará en cada pop-up cuando pulsemos un monton
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_ajustes, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true );
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        TextView contenido = (TextView) popupView.findViewById(R.id.contenido);
        contenido.setText(texto);
        TextView contenido2 = (TextView)popupView.findViewById(R.id.contenido2);
        contenido2.setText(texto2 + " " +texto3);
        TextView cuentaTokens = (TextView)popupView.findViewById(R.id.cuentaTokens);
        cuentaTokens.setText(m);
        Button cerrar = (Button) popupView.findViewById(R.id.cerrar);
        Button eliminar = (Button) popupView.findViewById(R.id.boton_eliminar);
        ImageButton ods_img = (ImageButton) popupView.findViewById(R.id.imagen_ods);

        if(MainActivity.sharedPreferences.getInt("hojas",0)>=50){ //Cuando lleguemos a mas de 50 tokens podremos visualizar diferentes textos
            eliminar.setVisibility(View.VISIBLE);
            contenido2.setVisibility(View.VISIBLE);
        }
        if (img_ods != 1){ //La imagen de la ods se mostrará si estamos en el apartado indicado
            ods_img.setVisibility( View.GONE );
        }
        ods_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.enlace_ods)));
                startActivity(intent);
            }
        });


        if(progreso !=1){
            LinearLayout check = (LinearLayout) popupView.findViewById(R.id.token);
            LinearLayout check2 = (LinearLayout) popupView.findViewById(R.id.moneda);
            check2.setVisibility(View.GONE);
            check.setVisibility(View.GONE);
        }
        if(progreso==1){
            TextView text = (TextView) popupView.findViewById(R.id.progreso);
            text.setVisibility(View.VISIBLE);
        }

        //Botones para cerrar el pop-up
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });

        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        eliminar.setOnClickListener(new View.OnClickListener() { //El boton de eliminar hará que se eliminen las actividades mas duras
            @Override
            public void onClick(View view) {
                eliminar(activity_principal.lista, lista_nombres);
                activity_principal.adaptador.notifyDataSetChanged(); // para actualizar el adaptador
                Toast toast = Toast.makeText(activity_ajustes.this, getString(R.string.actividades_eliminadas), Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
    public List<Actividades> act(List<Actividades> completa){
        List<Actividades> borradas = new ArrayList<>();
        for(int i =0; i< completa.size();i++){
            if(completa.get(i).getNivel() == 2){
                Actividades a = completa.get(i);
                borradas.add(a);
            }
        }
        return borradas;
    }
    public List<String> obtenername(List<Actividades> completa){
        List<String> c = new ArrayList<>();
        for(int i =0; i< completa.size();i++){
            c.add(completa.get(i).getNombre());
        }
        return c;
    }
    // Metodo para eliminar las actividades mas duras
    public void eliminar(List<Actividades> entera, List<String> nombres_actividad_borradas) {
        List<Actividades> nuevasActividades = new ArrayList<>();
        for (int i = 0; i < entera.size(); i++) {
            if (!nombres_actividad_borradas.contains(entera.get(i).getNombre())) {
                nuevasActividades.add(entera.get(i));
            }
        }
        entera.clear();
        entera.addAll(nuevasActividades);


        SharedPreferences.Editor editor = MainActivity.sharedPreferences.edit();

        Gson gson = new Gson();
        // Convertir la lista en una representación JSON usando Gson
        String listaJson = gson.toJson(activity_principal.lista);

        // Guardar la lista actualizada en SharedPreferences
        editor.putString("lista", listaJson);

        // Aplicar los cambios
        editor.apply();
    }

}