package com.example.sinensis;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.gson.Gson;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class activity_actividadLista extends AppCompatActivity {
    private Button btn_fecha, btn_hora;
    private Calendar fecha, hora;
    private Button btn_anadirA;
    private TextView titulo,descripcion;
    private ImageButton btn_link;
    private SeekBar seekBarVol;
    Button btn_eliminar_actividad;
    public static int val, act_places, hojas;
    protected static CheckBox checkSi;

    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_lista);

        //BOTONES PARA CALENDARIO
        btn_fecha = findViewById(R.id.btn_fecha);
        btn_hora =  findViewById(R.id.btn_hora);
        btn_anadirA = findViewById(R.id.btn_anadirA);
        btn_fecha.setOnClickListener(view -> {
            fecha = Calendar.getInstance();
            elegirFecha(btn_fecha, fecha);
        });
        btn_hora.setOnClickListener(view -> {
            hora = Calendar.getInstance();
            elegirHora(btn_hora, hora);
        });
        btn_anadirA.setOnClickListener(view -> {
            // Obtener valores de los botones
            String date1 = btn_fecha.getText().toString();
            String date2 = btn_hora.getText().toString();
            try {
                addEvent(date1, date2);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });

        //<------------- BOTONES PRINCIPALES INFERIORES ----------->

        ImageButton btn_calendario = findViewById(R.id.calendario);
        ImageButton btn_anadir = findViewById(R.id.button_eleccion);
        ImageButton btn_ajustes = findViewById(R.id.ajustes);

        Intent intentH = new Intent(this, activity_actividades.class);
        Intent intentA = new Intent(this, activity_ajustes.class);
        //Lanzar actividad de calendario
        btn_calendario.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(getString(R.string.enlace_calendario)));
            startActivity(intent);
        });
        //Lanzar actividad de lista de actividades
        btn_anadir.setOnClickListener(view -> startActivity(intentH));
        //Lanzar actividad de ajustes
        btn_ajustes.setOnClickListener(view -> startActivity(intentA));

        //<------------- ACTIVIDAD ELEGIDA ----------->

        titulo = findViewById(R.id.titulo_activity);
        descripcion = findViewById(R.id.descripcion_activity);
        Intent i = getIntent();
        Bundle b = i.getExtras();

        //Dependiendo de que actividad hayamos seleccionado de nuestro plan en activity_principal se guarda el titulo y la descripción para luego mostrarla.
        if (b != null) {
            titulo.setText(b.getString("TIT"));
            descripcion.setText(b.getString("DES"));
        }

        //Opción de eliminar actividad
        btn_eliminar_actividad = findViewById(R.id.boton_eliminar);
        btn_eliminar_actividad.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity_actividadLista.this);
            builder.setMessage(getString(R.string.alerta))
                    .setPositiveButton(R.string.aceptar, (dialogInterface, i1) -> {
                        eliminar(activity_principal.lista, titulo);
                        activity_principal.adaptador.notifyDataSetChanged(); // para actualizar el adaptador
                        Toast toast = Toast.makeText(activity_actividadLista.this, getString(R.string.actividad_eliminada), Toast.LENGTH_SHORT);
                        toast.show();
                        Intent intent = new Intent(view.getContext(), activity_principal.class);
                        startActivity(intent);
                    })
                    .setNegativeButton(R.string.cancelar, (dialogInterface, i1) -> dialogInterface.dismiss());
            AlertDialog alertDialog = builder.create(); //Alert que te indique si estas seguro de eliminar la actividad.
            alertDialog.show();
        });

        //Boton de link a cada enlace dependiendo de la actividad que hayamos pulsado
        btn_link = findViewById(R.id.boton_link);
        String nombreActividad = titulo.getText().toString();
        elegirFoto(btn_link);
        //Music player
        seekBarVol = findViewById(R.id.seekBar_vol);
        if (nombreActividad.equals("Respiraciones guiadas") || nombreActividad.equals("Sonidos Relajantes") || nombreActividad.equals("Audioguia")) {
            btn_link.setVisibility(View.VISIBLE);
            seekBarVol.setVisibility(View.VISIBLE);
            if (nombreActividad.equals("Respiraciones guiadas")) {
                MediaPlayer mp = MediaPlayer.create(this, R.raw.respiraciones);
                audio(mp);
            } else if (nombreActividad.equals("Sonidos Relajantes")) {
                MediaPlayer mp = MediaPlayer.create(this, R.raw.sonidosrelajantes);
                audio(mp);
            } else if (nombreActividad.equals("Audioguia")) {
                MediaPlayer mp = MediaPlayer.create(this, R.raw.audioguia);
                audio(mp);
            }
        }



        checkSi = findViewById(R.id.checkSi); //Comprobar si han realizado la actividad

        AppDatabase db;
        db = AppDatabase.getInstance(getApplicationContext());
        int m = db.ActividadesDAO().obtenernivel(nombreActividad); //variable de los tokens.
        SharedPreferences sharedPreferences = getSharedPreferences("check",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int position = getIntent().getIntExtra("POS", -1);
        checkSi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            private CountDownTimer timer;
            private boolean isChecked = false;
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                this.isChecked = isChecked;
                if(isChecked){
                    timer = new CountDownTimer(4000,1000) {
                        @Override
                        public void onTick(long l) {
                            checkSi.setEnabled(false); //Inhabilitamos el check
                        }
                        @Override
                        public void onFinish() {
                            checkSi.setEnabled(true); //Volvemos a habilitar el check
                            checkSi.setChecked(false); //Descamarcamos el check
                        }
                    }.start();

                    //Variable donde se almacenan los tokens
                    hojas = obtenerHojas(); // recuperamos el valor sumado
                    if(m == 0){
                        hojas += 5;
                    }else if(m==1){
                        hojas +=10;
                    }else if(m == 2){
                        hojas +=10;
                    }
                    guardarHojas(hojas);//guardamos el valor ya sumado
                    activity_ajustes.m = Integer.toString(obtenerHojas());
                    Toast toast = Toast.makeText(activity_actividadLista.this,getString(R.string.monedas,hojas) , Toast.LENGTH_SHORT);
                    toast.show();
                    val = 1;
                }
            }
        });
        boolean checkboxState = sharedPreferences.getBoolean("check1"+position, false);
        checkSi.setChecked(checkboxState);
    }

    //<-------------  MÉTODOS FUERA DEL ONCREATE ----------->

    //Metodo para eliminar actividad
    public void eliminar(List<Actividades> a, TextView t) {
        String nombreActividad = t.getText().toString();
        for (int i = 0; i < a.size(); i++) {
            Actividades actividad = a.get(i);
            if (actividad.getNombre().equals(nombreActividad)) {
                a.remove(actividad);
                break;
            }
        }
        SharedPreferences.Editor editor = MainActivity.sharedPreferences.edit();
        Gson gson = new Gson();
        String listaJson = gson.toJson(activity_principal.lista);// Convertir la lista en una representación JSON usando Gson
        editor.putString("lista", listaJson); //Guardar la lista actualizada en SharedPreferences
        editor.apply(); // Aplicar los cambios
    }

    //Music player
    public void audio(MediaPlayer mp) {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        int max_vol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        btn_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mp.isPlaying()) {
                    mp.pause();
                    Toast.makeText(activity_actividadLista.this, "Pausa", Toast.LENGTH_SHORT).show();
                } else {
                    mp.start();
                    Toast.makeText(activity_actividadLista.this, "Play", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //Barra de volumen para poder controlarlo
        seekBarVol.setMax(max_vol);
        seekBarVol.setProgress(currentVolume);
        seekBarVol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //papa
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    //Calendario
    public void elegirFecha(Button boton, Calendar calendar) {
        DatePickerDialog dpd = new DatePickerDialog(this, (datePicker, day, month, year) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            String date = year + "/" + (month + 1) + "/" + day;
            boton.setText(date);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dpd.show();
    }
    public void elegirHora(Button boton, Calendar calendar) {
        TimePickerDialog tpd = new TimePickerDialog(this, (timePicker, hour, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String timeString = sdf.format(calendar.getTime());
            boton.setText(timeString);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        tpd.show();
    }
    public void addEvent(String beginDate, String beginHour) throws ParseException {
        // Convertir fechas y horas a milisegundos
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date startDate = null;
        try {
            startDate = sdf.parse(beginDate + " " + beginHour);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long startTimeInMillis = startDate.getTime();
        TextView titulo = findViewById(R.id.titulo_activity);
        // Crear Intent para agregar evento al calendario
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, titulo.getText().toString())
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTimeInMillis)
                .putExtra(CalendarContract.Reminders.MINUTES, 5)
                .putExtra(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT); // Notificación de alerta

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    //Para que en cada actividad te salga una foto diferente dependiendo de la actividad que pulses
    public void elegirFoto(ImageButton botonLink) {
        String nombreActividad = titulo.getText().toString();
        LinearLayout linearLayout = findViewById(R.id.linear1);
        int edad_recogida = activity_datos.edad_datos;
        int id = 0;
        String url = "";
        switch (nombreActividad) {
            case "Correr":
            case "Caminar":
                id = R.drawable.googlemaps;
                url = "mapa";
                linearLayout.removeView(seekBarVol);
                act_places = 0;
                break;
            case "Ir a un spa":
                id = R.drawable.googlemaps;
                url = "mapa";
                linearLayout.removeView(seekBarVol);
                act_places = 1;
                break;
            case "Estiramientos":
                id = R.drawable.youtube;
                url = getString(R.string.enlace_estiramientos);
                linearLayout.removeView(seekBarVol);
                break;
            case "Yoga":
                id = R.drawable.youtube;
                url = getString(R.string.enlace_yoga);
                linearLayout.removeView(seekBarVol);
                break;
            case "Escuchar música":
                id = R.drawable.spotify;
                url = getString(R.string.enlace_spotify);
                linearLayout.removeView(seekBarVol);
                break;
            case "Libros de autoayuda":
                id = R.drawable.libro;
                url = getString(R.string.enlace_libros_autoayuda);
                linearLayout.removeView(seekBarVol);
                break;
            case "Respiración con postura de Loto":
                id = R.drawable.loto;
                url = getString(R.string.enlace_loto);
                linearLayout.removeView(seekBarVol);
                break;
            case "Cocinar":
                id = R.drawable.hornear;
                url = getString(R.string.enlace_cocinar);
                linearLayout.removeView(seekBarVol);
                break;
            case "Practicar Fotografía":
                id = R.drawable.camara;
                url = "foto";
                break;
            case "Artes Marciales":
                if (edad_recogida > 50) {
                    id = R.drawable.artesmarcialesa;
                    url = getString(R.string.enlace_marciales_abuelos);

                } else {
                    id = R.drawable.artesmarcialesj;
                    url = getString(R.string.enlace_maricales_jovenes);
                }
                linearLayout.removeView(seekBarVol);
                break;

            case "Leer Libros":
                if (edad_recogida > 50) {
                    id = R.drawable.leerlibrosa;
                    url = getString(R.string.enlace_leer_abuelos);

                } else {
                    id = R.drawable.leerlibrosj;
                    url = getString(R.string.enlace_leer_jovenes);
                }
                linearLayout.removeView(seekBarVol);
                break;
            case "Ir al cine o al teatro":
                id = R.drawable.cartelera;
                url = getString(R.string.enlace_cartelera);
                linearLayout.removeView(seekBarVol);
                break;
            case "Respiraciones guiadas":
            case "Sonidos Relajantes":
            case "Audioguia":
                id = R.drawable.play;
                break;
            default:
                LinearLayout linearLayout1 = findViewById(R.id.linear);
                linearLayout1.removeView(btn_link);
                linearLayout.removeView(seekBarVol);
        }
        if (id != 0 || !url.isEmpty()) {
            botonLink.setVisibility(View.VISIBLE);
            botonLink.setImageResource(id);
            String finalUrl = url;
            botonLink.setOnClickListener(view -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl));
                startActivity(intent);
            });
        }
        if (url.equals("mapa")) {
            botonLink.setOnClickListener(view -> {
                Intent intent = new Intent(view.getContext(), activity_mapa.class);
                startActivity(intent);
            });
        }
        if (url.equals("foto")) {
            botonLink.setOnClickListener(view -> {
                //Permisos para acceder a la cámara
                if (ContextCompat.checkSelfPermission(activity_actividadLista.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    //Inicio de la cámara
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                } else {
                    //Si no se ha concedido el permiso lo vuelvo a pedir
                    ActivityCompat.requestPermissions(activity_actividadLista.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                }
            });
        }

    }

    //Metodo para controlar shared preferences y la contabilización de tokens
    private void guardarHojas(int hojas) {
        MainActivity.sharedPreferences = getSharedPreferences("datos12", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = MainActivity.sharedPreferences.edit();
        editor.putInt("hojas", hojas); // gaurdamos
        editor.apply();
    }
    public int obtenerHojas() {
        MainActivity.sharedPreferences = getSharedPreferences("datos12", Context.MODE_PRIVATE);
        return MainActivity.sharedPreferences.getInt("hojas", 0); // 0 es el valor predeterminado si no se encuentra la clave "hojas"
    }
}