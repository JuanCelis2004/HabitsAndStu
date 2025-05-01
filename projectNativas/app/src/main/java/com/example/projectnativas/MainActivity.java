package com.example.projectnativas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText etCorreo, etContrasenia;
    Button btnEntrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        etCorreo = findViewById(R.id.etCorreoLogin);
        etContrasenia = findViewById(R.id.etContraseniaLogin);
        btnEntrar = findViewById(R.id.btnEntrar);

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String correo = etCorreo.getText().toString().trim();
                String contrasenia = etContrasenia.getText().toString().trim();

                if (correo.trim().isEmpty() || contrasenia.trim().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                } else {
                    loginUsuario(correo, contrasenia);
                }
            }
        });

        // Agregamos la lógica para redirigir al registro
        TextView tvRegistrarse = findViewById(R.id.tvRegistrarse);
        tvRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir al Signup
                Intent intent = new Intent(MainActivity.this, Signup.class);
                startActivity(intent);
            }
        });
    }

    private void loginUsuario(String correo, String contrasenia) {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8080/BackNative/UserController");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String datos = "action=login" +
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
                    if (respuesta.startsWith("ERROR")) {
                        Toast.makeText(MainActivity.this, respuesta, Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            int userId = Integer.parseInt(respuesta); // Obtener el ID desde la respuesta
                            Toast.makeText(MainActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

                            // Enviar el ID a la siguiente pantalla
                            Intent intent = new Intent(MainActivity.this, Habits.class);
                            intent.putExtra("userId", userId);
                            startActivity(intent);
                            finish();
                        } catch (NumberFormatException e) {
                            Toast.makeText(MainActivity.this, "Error en formato de ID de usuario", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

}


