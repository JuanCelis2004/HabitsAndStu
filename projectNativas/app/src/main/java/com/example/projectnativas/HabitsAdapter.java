package com.example.projectnativas;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HabitsAdapter extends RecyclerView.Adapter<HabitsAdapter.HabitViewHolder> {

    private List<Habito> habitList;
    private Context context;

    public HabitsAdapter(Context context, List<Habito> habitList) {
        this.context = context;
        this.habitList = habitList;
    }

    @Override
    public HabitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_habit, parent, false);
        return new HabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HabitViewHolder holder, int position) {
        if (holder == null) return;

        Habito habito = habitList.get(position);

        holder.habitName.setText(habito.getHabitoNombre());
        holder.habitFrequency.setText(habito.getFrecuencia());
        holder.habitTime.setText(habito.getRecordatorio());

        Picasso.get()
                .load(habito.getImagenUrl())
                .into(holder.habitImage);

        // Spinner
        String[] estados = {"Sin completar", "Completado"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.spinner_item, estados) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = view.findViewById(R.id.spinner_text);

                // Limpiar fondo previo
                text.setBackgroundColor(Color.TRANSPARENT);

                if (habito.isEstado()) {
                    text.setTextColor(context.getResources().getColor(R.color.white)); // blanco
                    text.setBackgroundColor(context.getResources().getColor(R.color.green)); // verde
                } else {
                    text.setTextColor(context.getResources().getColor(android.R.color.black)); // negro
                    text.setBackgroundColor(context.getResources().getColor(R.color.red)); // rojo
                }

                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView text = view.findViewById(R.id.spinner_text);
                text.setTextColor(context.getResources().getColor(android.R.color.black)); // desplegable siempre negro
                return view;
            }
        };
        holder.habitStatus.setAdapter(adapter);

        // Establecer estado actual
        holder.habitStatus.setSelection(habito.isEstado() ? 1 : 0);

        // Cambiar color y habilitación según el estado
        if (habito.isEstado()) {
            holder.habitStatus.getBackground().setTint(context.getResources().getColor(R.color.green)); // Verde
            holder.habitStatus.setEnabled(false); // Desactivar
        } else {
            holder.habitStatus.getBackground().setTint(context.getResources().getColor(R.color.red)); // Rojo
            holder.habitStatus.setEnabled(true); // Activar
        }

        // Manejador de cambios
        holder.habitStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean firstSelection = true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (firstSelection) {
                    firstSelection = false;
                    return;
                }

                if (pos == 1) { // Completado
                    habito.setEstado(true); // Actualiza el modelo local
                    actualizarEstadoHabito(habito.getId(), true, holder);
                    notifyItemChanged(holder.getAdapterPosition()); // Fuerza el refresco visual
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditHabitActivity.class);
            intent.putExtra("id", habito.getId());
            intent.putExtra("nombre", habito.getHabitoNombre());
            intent.putExtra("frecuencia", habito.getFrecuencia());
            intent.putExtra("recordatorio", habito.getRecordatorio());
            intent.putExtra("estado", habito.isEstado());
            intent.putExtra("imagenUrl", habito.getImagenUrl());
            context.startActivity(intent);
        });


    }

    private void actualizarEstadoHabito(int habitoId, boolean nuevoEstado, HabitViewHolder holder) {
        String url = "http://10.0.2.2:8080/BackNative/HabitController"; // Ajusta si es necesario

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (nuevoEstado) {
                        holder.habitStatus.getBackground().setTint(context.getResources().getColor(R.color.green)); // Verde
                        holder.habitStatus.setEnabled(false); // Desactivar
                    }
                },
                error -> {
                    error.printStackTrace();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "updateEstado");
                params.put("id", String.valueOf(habitoId));
                params.put("estado", nuevoEstado ? "1" : "0");
                return params;
            }
        };

        queue.add(stringRequest);
    }


    @Override
    public int getItemCount() {
        return habitList.size();
    }

    public static class HabitViewHolder extends RecyclerView.ViewHolder {
        ImageView habitImage;
        TextView habitName;
        TextView habitFrequency;
        TextView habitTime;
        Spinner habitStatus;

        public HabitViewHolder(View itemView) {
            super(itemView);
            habitImage = itemView.findViewById(R.id.image_habit);
            habitName = itemView.findViewById(R.id.text_habit_name);
            habitFrequency = itemView.findViewById(R.id.text_habit_frequency);
            habitTime = itemView.findViewById(R.id.text_habit_time);
            habitStatus = itemView.findViewById(R.id.spinner_status);
        }
    }
}
