package com.example.sinensis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Adaptadores extends BaseAdapter {
    private List<Actividades> lista_act = new ArrayList<>();
    private Context context;
    public static int id;

    //Contructor
    public Adaptadores(Context context, List<Actividades> lista_act) {
        this.context = context;
        this.lista_act = lista_act;
    }

    //Metodo que nos devuelve el tamaño de la lista
    public int getCount() {
        return lista_act.size();
    }

    public Actividades getItem(int position) {
        return lista_act.get(position);
    } //Devuelve el item pulsado

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void actualizarActividades(List<Actividades> actividades) { //Metodo que actualiza el adaptador
        lista_act = actividades;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Cogemos cada actividad para poder manipular como se ve
        Actividades a = (Actividades) getItem(position);
        convertView = LayoutInflater.from(context).inflate(R.layout.listas_items, null);

        // Almacenamos los textView
        TextView nombreTextView = (TextView) convertView.findViewById(R.id.tituloActividad);
        nombreTextView.setText(a.getNombre());
        TextView descripcionTextView = (TextView) convertView.findViewById(R.id.descripcionActividad);
        descripcionTextView.setText(a.getDescripcion());
        TextView nivel_actividad = (TextView) convertView.findViewById(R.id.descripcion_nivel);

        //Obtenemos el nivel para mostrarlo en cada vista de actividad
        if (a.getNivel() == 0) {
            nivel_actividad.setText("Nivel: Bajo");
        } else if (a.getNivel() == 1) {
            nivel_actividad.setText("Nivel: Medio");
        } else {
            nivel_actividad.setText("Nivel: Alto");
        }

        String ruta = a.getfoto(); // Devuelve el nombre de archivo de la imagen
        String nombreArchivo = ruta.substring(0, ruta.lastIndexOf(".")); // Elimina la extensión ".png" del nombre de archivo
        id = context.getResources().getIdentifier(nombreArchivo, "drawable", context.getPackageName()); // Obtiene el ID de recurso de la imagen sin la extensión
        ImageView imagenDraw = (ImageView) convertView.findViewById(R.id.imageactividad); //Almacenamos la imagen
        imagenDraw.setImageResource(id);

        //Nos mostrará un candado unicamente en las actividades que no podamos entrar.
        if (MainActivity.sharedPreferences.getInt("hojas", 0) < 50 && (a.getNombre().equals("Aprendes sobre los ajolotes") || a.getNombre().equals("Plan de ocio") || a.getNombre().equals("Cocinar Arepas"))) {
            ImageView imagen_candado = (ImageView) convertView.findViewById(R.id.imagecandado);
            imagen_candado.setImageResource(R.drawable.candado);
        }
        return convertView;
    }
}