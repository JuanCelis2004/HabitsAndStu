package com.example.projectnativas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Signup extends AppCompatActivity{

    EditText etNombre, etApellido, etCorreo, etContrasenia;
    Button btnRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singup);

        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etCorreo = findViewById(R.id.etCorreo);
        etContrasenia = findViewById(R.id.etContrasenia);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = etNombre.getText().toString().trim();
                String apellido = etApellido.getText().toString().trim();
                String correo = etCorreo.getText().toString().trim();
                String contrasenia = etContrasenia.getText().toString().trim();

                if (nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty() || contrasenia.isEmpty()) {
                    Toast.makeText(Signup.this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                } else {
                    registrarUsuario(nombre, apellido, correo, contrasenia);
                }
            }
    });
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Esto cierra la actividad actual y regresa a la anterior (login)
            }
        });
}

    private void registrarUsuario(String nombre, String apellido, String correo, String contrasenia) {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8080/BackNative/UserController"); // reemplaza con tu IP pública o localhost en emulador (10.0.2.2)
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String datos = "action=signup" +
                        "&nombre=" + URLEncoder.encode(nombre, "UTF-8") +
                        "&apellido=" + URLEncoder.encode(apellido, "UTF-8") +
                        "&correo=" + URLEncoder.encode(correo, "UTF-8") +
                        "&contrasenia=" + URLEncoder.encode(contrasenia, "UTF-8");

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
                        Toast.makeText(Signup.this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Signup.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Signup.this, respuesta, Toast.LENGTH_SHORT).show();
                    }
                });

                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(Signup.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}