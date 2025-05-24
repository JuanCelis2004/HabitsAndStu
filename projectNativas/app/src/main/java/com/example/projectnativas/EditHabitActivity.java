package com.example.projectnativas;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.projectnativas.R;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditHabitActivity extends AppCompatActivity {

    Spinner spinnerHabito, spinnerFrecuencia;
    TextView reminderTextView;
    ImageButton selectTimeButton;
    Button btnEditar;

    private int habitId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edithabit);

        // Vincular vistas
        spinnerHabito = findViewById(R.id.edit_spinner_habito);
        spinnerFrecuencia = findViewById(R.id.edit_spinner_frecuencia);
        reminderTextView = findViewById(R.id.tv_hora);
        selectTimeButton = findViewById(R.id.btn_seleccionar_hora);
        btnEditar = findViewById(R.id.btn_crear);

        // Adaptadores para spinners
        String[] habitos = {"Leer", "Ejercicio", "Meditar", "Estudiar"};
        ArrayAdapter<String> habitoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, habitos);
        habitoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHabito.setAdapter(habitoAdapter);

        String[] frecuencias = {"Diario", "Semanal", "Mensual"};
        ArrayAdapter<String> frecuenciaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, frecuencias);
        frecuenciaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrecuencia.setAdapter(frecuenciaAdapter);


        ImageView backArrow = findViewById(R.id.btn_back);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Esto cierra la actividad actual y regresa a la anterior
            }
        });


        // Cargar datos desde el intent
        Intent intent = getIntent();
        if (intent != null) {
            habitId = intent.getIntExtra("id", -1);
            String nombre = intent.getStringExtra("nombre");
            String frecuencia = intent.getStringExtra("frecuencia");
            String recordatorio = intent.getStringExtra("recordatorio");

            int indexNombre = Arrays.asList(habitos).indexOf(nombre);
            if (indexNombre >= 0) spinnerHabito.setSelection(indexNombre);

            int indexFrecuencia = Arrays.asList(frecuencias).indexOf(frecuencia);
            if (indexFrecuencia >= 0) spinnerFrecuencia.setSelection(indexFrecuencia);

            reminderTextView.setText(recordatorio);
        }

        // Lógica de guardar cambios
        btnEditar.setOnClickListener(v -> {
            String nuevoHabito = spinnerHabito.getSelectedItem().toString();
            String nuevaFrecuencia = spinnerFrecuencia.getSelectedItem().toString();
            String nuevaHora = reminderTextView.getText().toString();

            String url = "http://10.0.2.2:8080/BackNative/HabitController";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    response -> {
                        if (response.equals("OK")) {
                            Toast.makeText(this, "Hábito actualizado correctamente", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();  // Solo cierras esta actividad, no creas otra
                        } else {
                            Toast.makeText(this, "Error del servidor: " + response, Toast.LENGTH_LONG).show();
                        }
                    },
                    error -> Toast.makeText(this, "Error: " + error.toString(), Toast.LENGTH_LONG).show()
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("action", "edit");
                    params.put("id", String.valueOf(habitId));
                    params.put("habitoNombre", nuevoHabito);
                    params.put("frecuencia", nuevaFrecuencia);
                    params.put("recordatorio", nuevaHora);
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(stringRequest);
        });

        // Selector de hora
        selectTimeButton.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    (TimePicker view, int hourOfDay, int minute1) -> {
                        String horaSeleccionada = String.format("%02d:%02d", hourOfDay, minute1);
                        reminderTextView.setText(horaSeleccionada);
                    }, hour, minute, true); // true = formato 24h
            timePickerDialog.show();
        });
    }
}
