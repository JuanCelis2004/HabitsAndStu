package com.example.projectnativas;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;

public class CrearHabitoActivity extends AppCompatActivity {

    Spinner spinnerHabito, spinnerFrecuencia;
    TextView tvHora;
    Button btnCrear;
    int horaSeleccionada = -1, minutoSeleccionado = -1;

    String[] habitos = {"Ejercicio", "Leer", "Meditar", "Estudiar"};
    String[] frecuencias = {"Diario", "Semanal", "Mensual"};
    private int userId;  // Aquí declaras el userId a nivel de clase


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createhabit);

        // Usar la variable de clase userId y no declararla localmente
        userId = getIntent().getIntExtra("userId", -1);  // Obtén el userId desde el Intent

        ImageView backArrow = findViewById(R.id.btn_back);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Esto cierra la actividad actual y regresa a la anterior
            }
        });

        spinnerHabito = findViewById(R.id.spinner_habito);
        spinnerFrecuencia = findViewById(R.id.spinner_frecuencia);
        tvHora = findViewById(R.id.tv_hora);
        btnCrear = findViewById(R.id.btn_crear);

        ArrayAdapter<String> habitoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, habitos);
        habitoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHabito.setAdapter(habitoAdapter);

        ArrayAdapter<String> frecuenciaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, frecuencias);
        frecuenciaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrecuencia.setAdapter(frecuenciaAdapter);

        findViewById(R.id.btn_seleccionar_hora).setOnClickListener(v -> mostrarSelectorHora());

        btnCrear.setOnClickListener(v -> {
            String habito = spinnerHabito.getSelectedItem().toString();
            String frecuencia = spinnerFrecuencia.getSelectedItem().toString();

            if (habito.isEmpty() || frecuencia.isEmpty() || horaSeleccionada == -1) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                String horaFormateada = String.format("%02d:%02d", horaSeleccionada, minutoSeleccionado);
                crearHabitoEnServidor(habito, frecuencia, horaFormateada);
            }
        });
    }

    private void mostrarSelectorHora() {
        Calendar calendario = Calendar.getInstance();
        int hora = calendario.get(Calendar.HOUR_OF_DAY);
        int minuto = calendario.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (TimePicker view, int hourOfDay, int minute) -> {
            horaSeleccionada = hourOfDay;
            minutoSeleccionado = minute;
            String horaFormateada = String.format("%02d:%02d", hourOfDay, minute);
            tvHora.setText(horaFormateada);
        }, hora, minuto, true);

        timePickerDialog.show();
    }

    private void crearHabitoEnServidor(String habito, String frecuencia, String hora) {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8080/BackNative/HabitController");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String datos = "action=create" +
                        "&habitoNombre=" + URLEncoder.encode(habito, "UTF-8") +
                        "&frecuencia=" + URLEncoder.encode(frecuencia, "UTF-8") +
                        "&recordatorio=" + URLEncoder.encode(hora, "UTF-8") +
                        "&estado=true" +
                        "&userId=" + userId;  // Ahora usas la variable miembro 'userId'

                OutputStream os = conn.getOutputStream();
                os.write(datos.getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                InputStream is = (responseCode == HttpURLConnection.HTTP_OK) ? conn.getInputStream() : conn.getErrorStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
                reader.close();

                String respuesta = responseBuilder.toString();
                runOnUiThread(() -> {
                    if (respuesta.equals("OK")) {
                        Toast.makeText(CrearHabitoActivity.this, "Hábito creado exitosamente", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(CrearHabitoActivity.this, "Error: " + respuesta, Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(CrearHabitoActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}

