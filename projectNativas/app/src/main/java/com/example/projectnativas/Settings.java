package com.example.projectnativas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class Settings extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton btnMenu;
    private Button btnCerrarSesion;

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        // Obtener el ID del usuario desde el intent
        userId = getIntent().getIntExtra("userId", -1);

        // Referencias de vistas
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);
        btnMenu = findViewById(R.id.btn_menu);
        btnCerrarSesion = findViewById(R.id.btn_cerrar_sesion); // Asegúrate de que el ID esté en el XML

        // Abrir menú lateral
        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Menú lateral navegación
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            drawerLayout.closeDrawer(GravityCompat.START);

            drawerLayout.postDelayed(() -> {
                if (id == R.id.nav_inicio) {
                    Intent intent = new Intent(Settings.this, Habits.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                } else if (id == R.id.nav_configuracion) {
                    Intent intent = new Intent(Settings.this, Settings.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                } else if (id == R.id.nav_estadisticas) {
                    Intent intent = new Intent(Settings.this, Statistics.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }
            }, 250);

            return true;
        });

        // Botón cerrar sesión
        btnCerrarSesion.setOnClickListener(v -> {
            // Volver al login y limpiar historial de actividades
            Intent intent = new Intent(Settings.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
