package com.example.projectnativas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout; // IMPORTANTE
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.habits);

        userId = getIntent().getIntExtra("userId", -1);

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

        // Listener para SwipeRefresh
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
                        swipeRefreshLayout.setRefreshing(false); // Ocultar animación de carga
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            habitList.clear(); // Limpiamos antes de agregar

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
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        swipeRefreshLayout.setRefreshing(false); // Ocultar animación también en error
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("action", "list");
                params.put("userId", String.valueOf(userId)); // ⬅️ Manda el userId también
                return params;
            }
        };

        queue.add(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarHabitosServidor(); // Cada vez que regresas a esta pantalla, recarga hábitos automáticamente
    }
}
