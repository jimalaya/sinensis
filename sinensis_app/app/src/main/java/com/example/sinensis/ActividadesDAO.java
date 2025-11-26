package com.example.sinensis;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface ActividadesDAO {

    @Query("SELECT * FROM Actividades")
    List<Actividades> selectAll();

    @Query("SELECT * FROM Actividades WHERE nivel = :nivel AND mentor= :mentor AND bonus= :bonus")
    List<Actividades> selectactividad(int nivel, int mentor, int bonus);

    @Query("SELECT foto FROM Actividades WHERE nombre LIKE :nombre LIMIT 1")
     String foto(String nombre);


    @Query("SELECT * FROM Actividades WHERE id=:id")
    Actividades selectById(long id);

    @Query("SELECT * FROM Actividades WHERE nombre LIKE :nombre LIMIT 1")
    Actividades selectByNombre(String nombre);

    @Query("SELECT * FROM Actividades WHERE nivel=:nivel")
    Actividades selectByNivel(int nivel);

    @Query("SELECT * FROM Actividades WHERE descripcion LIKE :descripcion LIMIT 1")
    Actividades selectByDescripcion(String descripcion);


    @Query("SELECT * FROM Actividades WHERE mentor=:mentor")
    Actividades selectByMentor(int mentor);

    @Query("SELECT COUNT(*) FROM actividades")
    int count();

    @Query("SELECT nombre FROM actividades WHERE nivel = :nivel AND mentor= :mentor")
    List<String> getNombresActividades( int nivel, int mentor);

    @Query("SELECT nivel FROM actividades WHERE nombre LIKE :nombre LIMIT 1")
    int obtenernivel(String nombre);

    @Query("SELECT descripcion FROM actividades WHERE nivel = :nivel AND mentor= :mentor")
    List<String> getDescripcionActividades(int nivel, int mentor);


    @Query("SELECT nombre FROM actividades")
    List<String> getNombresActividades_();


    @Insert
    long insert(Actividades actividades);

    @Update
    int update(Actividades actividades);

    @Delete
    int delete(Actividades actividades);
}
