package com.example.sinensis;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Actividades {
    @PrimaryKey(autoGenerate = true)
    public long id;


    @ColumnInfo(name = "nombre")
    public String nombre;

    @ColumnInfo(name = "nivel")
    public int nivel;

    @ColumnInfo(name = "descripcion")
    public String descripcion;


    @ColumnInfo(name = "mentor")
    public int mentor;

    @ColumnInfo(name = "foto")
    public String  foto;

    @ColumnInfo(name = "descripcion_larga")
    public String  descripcion_larga;

    @ColumnInfo(name = "bonus")
    public int bonus;

    public Actividades(long id, String nombre, int nivel, String descripcion, int mentor, String foto, String descripcion_larga, int bonus) {
        this.id = id;
        this.nombre = nombre;
        this.nivel = nivel;
        this.descripcion = descripcion;
        this.mentor = mentor;
        this.foto = foto;
        this.descripcion_larga = descripcion_larga;
        this.bonus = bonus;
    }

    @Ignore
    public Actividades(String nombre, int nivel, String descripcion, int mentor,String foto, int bonus) {
        this.nombre = nombre;
        this.nivel = nivel;
        this.descripcion = descripcion;
        this.mentor = mentor;
        this.foto = foto;
        this.bonus = bonus;
    }


    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getfoto() {
        return foto;
    }

    public String getDescripcion_larga(){return descripcion_larga;}

    public int getNivel(){return nivel;}

    public int getBonus(){return bonus;}
}
