package com.example.projectnativas;

public class Habito {
    private int id;
    private String habitoNombre;
    private String frecuencia;
    private String recordatorio;
    private boolean estado;
    private String imagenUrl;

    // Constructor vacío
    public Habito() {}

    // Constructor con parámetros
    public Habito(int id, String habitoNombre, String frecuencia, String recordatorio, boolean estado, String imagenUrl) {
        this.id = id;
        this.habitoNombre = habitoNombre;
        this.frecuencia = frecuencia;
        this.recordatorio = recordatorio;
        this.estado = estado;
        this.imagenUrl = imagenUrl;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getHabitoNombre() { return habitoNombre; }
    public void setHabitoNombre(String habitoNombre) { this.habitoNombre = habitoNombre; }

    public String getFrecuencia() { return frecuencia; }
    public void setFrecuencia(String frecuencia) { this.frecuencia = frecuencia; }

    public String getRecordatorio() { return recordatorio; }
    public void setRecordatorio(String recordatorio) { this.recordatorio = recordatorio; }

    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
}
