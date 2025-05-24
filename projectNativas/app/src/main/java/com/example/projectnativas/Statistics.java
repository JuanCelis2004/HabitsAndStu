package com.example.projectnativas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Statistics extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton btnMenu;
    private PieChart pieChart;

    private int userId; // 👈 Ajusta con el ID real del usuario logueado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);
        btnMenu = findViewById(R.id.btn_menu);
        pieChart = findViewById(R.id.pieChart);

        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            drawerLayout.closeDrawer(GravityCompat.START);

            drawerLayout.postDelayed(() -> {
                if (id == R.id.nav_inicio) {
                    Intent intent = new Intent(Statistics.this, Habits.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                } else if (id == R.id.nav_configuracion) {
                    Intent intent = new Intent(Statistics.this, Settings.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                } else if (id == R.id.nav_estadisticas) {
                    Intent intent = new Intent(Statistics.this, Statistics.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }
            }, 250);

            return true;
        });

        // Cargar datos desde el backend
        userId = getIntent().getIntExtra("userId", -1);
        cargarDatosEstadisticas();
    }

    private void cargarDatosEstadisticas() {
        String url = "http://10.0.2.2:8080/BackNative/HabitController?action=getStats&userId=" + userId;

        new Thread(() -> {
            try {
                java.net.URL link = new java.net.URL(url);
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) link.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                java.io.InputStream inputStream = conn.getInputStream();
                java.util.Scanner scanner = new java.util.Scanner(inputStream).useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";

                runOnUiThread(() -> procesarJSON(response));

            } catch (Exception e) {
                Log.e("Statistics", "Error cargando datos: " + e.getMessage());
            }
        }).start();
    }

    private void procesarJSON(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);

            int completados = jsonObject.getInt("completados");
            int noCompletados = jsonObject.getInt("noCompletados");

            // Dibujar PieChart
            ArrayList<PieEntry> entries = new ArrayList<>();
            if (completados > 0) {
                entries.add(new PieEntry(completados, "Completados"));
            }
            if (noCompletados > 0) {
                entries.add(new PieEntry(noCompletados, "Sin completar"));
            }

            PieDataSet dataSet = new PieDataSet(entries, "");
            dataSet.setColors(new int[]{
                    android.graphics.Color.parseColor("#90EE90"), // verde
                    android.graphics.Color.parseColor("#FF6347")  // rojo
            });
            dataSet.setValueTextSize(18f);
            dataSet.setValueTextColor(android.graphics.Color.BLACK);

            PieData pieData = new PieData(dataSet);

            pieChart.setData(pieData);
            pieChart.setUsePercentValues(true);
            pieChart.setCenterText("Progreso");
            pieChart.setCenterTextSize(22f);
            pieChart.setHoleRadius(40f);
            pieChart.getDescription().setEnabled(false);

            Legend legend = pieChart.getLegend();
            legend.setEnabled(false); // usamos leyenda personalizada del layout

            pieChart.invalidate(); // refresca la gráfica

        } catch (Exception e) {
            Log.e("Statistics", "Error procesando JSON: " + e.getMessage());
        }
    }

}
