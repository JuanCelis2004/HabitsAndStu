package com.example.projectnativas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Habits extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HabitsAdapter habitsAdapter;
    private List<Habito> habitList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int userId;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageButton btnMenu;

    LinearLayout contentMain;
    FrameLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.habits);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);
        btnMenu = findViewById(R.id.btn_menu);

        // Abrir drawer al tocar el ícono del menú
        btnMenu.setOnClickListener(v -> {
            Log.d("MENU_NAV", "Seleccionaste Estadísticas");
            drawerLayout.openDrawer(GravityCompat.START);
        });

        // Manejar clics en los ítems del menú lateral
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            drawerLayout.closeDrawer(GravityCompat.START);

            drawerLayout.postDelayed(() -> {
                if (id == R.id.nav_estadisticas) {
                    Intent intent = new Intent(Habits.this, Statistics.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                } else if (id == R.id.nav_configuracion) {
                    Intent intent = new Intent(Habits.this, Settings.class);
                    intent.putExtra("userId", userId); // ✅ PASAR userId
                    startActivity(intent);                } else if (id == R.id.nav_inicio) {
                    Intent intent = new Intent(Habits.this, Habits.class);
                    intent.putExtra("userId", userId);  // <--- PASAR userId AQUÍ
                    startActivity(intent);
                }
            }, 250);

            return true;
        });




        userId = getIntent().getIntExtra("userId", -1);
        Log.d("USER_ID", "ID del usuario recibido: " + userId);

        recyclerView = findViewById(R.id.recyclerHabits);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        habitList = new ArrayList<>();
        habitsAdapter = new HabitsAdapter(this, habitList);
        recyclerView.setAdapter(habitsAdapter);

        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Habits.this, CrearHabitoActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cargarHabitosServidor();
            }
        });

        cargarHabitosServidor();
    }

    private void cargarHabitosServidor() {
        swipeRefreshLayout.setRefreshing(true);

        String url = "http://10.0.2.2:8080/BackNative/HabitController";

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        swipeRefreshLayout.setRefreshing(false);
                        Log.d("HABITS_RESPONSE", response);

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            habitList.clear();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);

                                int id = obj.getInt("id");
                                String nombre = obj.getString("habitoNombre");
                                String frecuencia = obj.getString("frecuencia");
                                String recordatorio = obj.getString("recordatorio");
                                boolean estado = obj.getBoolean("estado");
                                String imagenUrl = obj.getString("imagenUrl");

                                Habito habito = new Habito(id, nombre, frecuencia, recordatorio, estado, imagenUrl);
                                habitList.add(habito);
                            }

                            habitsAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            Log.e("JSON_ERROR", "Error al parsear hábitos: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        swipeRefreshLayout.setRefreshing(false);
                        Log.e("VOLLEY_ERROR", "Error en la solicitud: " + error.getMessage());
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("action", "list");
                params.put("userId", String.valueOf(userId));
                return params;
            }
        };

        queue.add(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarHabitosServidor();
    }
}
