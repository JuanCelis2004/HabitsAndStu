/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.io.Serializable;
import java.time.LocalTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author juand
 */
@Entity
@Table(name = "habits")
public class Habits implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "habitoNombre")
    private String habitoNombre;

    @Column(name = "frecuencia")
    private String frecuencia;

    @Column(name = "recordatorio")
    private String recordatorio;

    @Column(name = "estado")
    private boolean estado;

    @Column(name = "imagenUrl")
    private String imagenUrl;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User usuario;

    public Habits() {
    }

    public Habits(int id, String habitoNombre, String frecuencia, String recordatorio, boolean estado, String imagenUrl, User usuario) {
        this.id = id;
        this.habitoNombre = habitoNombre;
        this.frecuencia = frecuencia;
        this.recordatorio = recordatorio;
        this.estado = estado;
        this.imagenUrl = imagenUrl;
        this.usuario = usuario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHabitoNombre() {
        return habitoNombre;
    }

    public void setHabitoNombre(String habitoNombre) {
        this.habitoNombre = habitoNombre;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    public String getRecordatorio() {
        return recordatorio;
    }

    public void setRecordatorio(String recordatorio) {
        this.recordatorio = recordatorio;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Habits)) {
            return false;
        }
        Habits other = (Habits) object;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Model.Habits[ id=" + id + " ]";
    }

}
