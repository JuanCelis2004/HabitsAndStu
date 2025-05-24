/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import Model.Habits;
import Model.User;
import Persistence.HabitsJpaController;
import Persistence.UserJpaController;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalTime;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author juand
 */
@WebServlet(name = "HabitController", urlPatterns = {"/HabitController"})
public class HabitController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

    UserJpaController userJPA = new UserJpaController();

    HabitsJpaController habitsJPA = new HabitsJpaController();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        switch (action) {
            case "create":

                String nombreHabito = request.getParameter("habitoNombre");
                String frecuencia = request.getParameter("frecuencia");
                String hora = request.getParameter("recordatorio");
                String idUsuarioStr = request.getParameter("userId");

                if (nombreHabito != null && frecuencia != null && hora != null && idUsuarioStr != null) {
                    try {
                        int userId = Integer.parseInt(idUsuarioStr);
                        LocalTime horaRecordatorio = LocalTime.parse(hora);

                        User usuario = userJPA.findUser(userId);

                        Habits habito = new Habits();
                        habito.setHabitoNombre(nombreHabito);
                        habito.setFrecuencia(frecuencia);
                        habito.setRecordatorio(hora);
                        habito.setEstado(false); // Estado inicial como sin completar
                        habito.setUsuario(usuario);

                        String imagenUrl = "";
                        switch (nombreHabito.toLowerCase()) {
                            case "ejercicio":
                                imagenUrl = "https://media-public.canva.com/bM7cY/MAC8eTbM7cY/2/tl.png";
                                break;
                            case "leer":
                                imagenUrl = "https://media-public.canva.com/piBIs/MAGNENpiBIs/1/tl.png";
                                break;
                            case "meditar":
                                imagenUrl = "https://media-public.canva.com/ieJ00/MAF8kmieJ00/1/tl.png";
                                break;
                            case "estudiar":
                                imagenUrl = "https://media-public.canva.com/gmtGw/MAEqtsgmtGw/1/tl.png";
                                break;
                            default:
                                imagenUrl = "https://media-public.canva.com/YFwSk/MACxYlYFwSk/2/tl.png";
                                break;
                        }
                        habito.setImagenUrl(imagenUrl);

                        habitsJPA.create(habito);

                        response.getWriter().write("OK");
                    } catch (Exception e) {
                        response.getWriter().write("Error: " + e.getMessage());
                    }
                } else {
                    response.getWriter().write("Faltan datos");
                }

                break;
            case "edit":
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    String nombre = request.getParameter("habitoNombre");
                    String frecuenciaEdit = request.getParameter("frecuencia");
                    String recordatorioEdit = request.getParameter("recordatorio");

                    Habits habitoEdit = habitsJPA.findHabits(id);
                    habitoEdit.setHabitoNombre(nombre);
                    habitoEdit.setFrecuencia(frecuenciaEdit);
                    habitoEdit.setRecordatorio(recordatorioEdit);

                    // Actualizar imagen por nombre de hábito (opcional)
                    String imagenUrlEdit;
                    switch (nombre.toLowerCase()) {
                        case "ejercicio":
                            imagenUrlEdit = "https://media-public.canva.com/bM7cY/MAC8eTbM7cY/2/tl.png";
                            break;
                        case "leer":
                            imagenUrlEdit = "https://media-public.canva.com/piBIs/MAGNENpiBIs/1/tl.png";
                            break;
                        case "meditar":
                            imagenUrlEdit = "https://media-public.canva.com/ieJ00/MAF8kmieJ00/1/tl.png";
                            break;
                        case "estudiar":
                            imagenUrlEdit = "https://media-public.canva.com/gmtGw/MAEqtsgmtGw/1/tl.png";
                            break;
                        default:
                            imagenUrlEdit = "https://media-public.canva.com/YFwSk/MACxYlYFwSk/2/tl.png";
                            break;
                    }
                    habitoEdit.setImagenUrl(imagenUrlEdit);

                    habitsJPA.edit(habitoEdit);

                    response.getWriter().write("OK");

                } catch (Exception e) {
                    response.getWriter().write("Error al editar hábito: " + e.getMessage());
                }
                break;

            case "updateEstado":
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    String estadoParam = request.getParameter("estado");
                    boolean nuevoEstado;

                    // Convertir correctamente
                    if (estadoParam.equals("true") || estadoParam.equals("1")) {
                        nuevoEstado = true;
                    } else {
                        nuevoEstado = false;
                    }

                    Habits habito = habitsJPA.findHabits(id);
                    habito.setEstado(nuevoEstado);
                    habitsJPA.edit(habito);

                    response.getWriter().write("Estado actualizado");
                } catch (Exception e) {
                    response.getWriter().write("Error al actualizar estado: " + e.getMessage());
                }
                break;

            case "list":
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();

                try {
                    int userId = Integer.parseInt(request.getParameter("userId"));
                    List<Habits> habitos = habitsJPA.findHabitsEntities();

                    JSONArray jsonArray = new JSONArray();

                    for (Habits h : habitos) {
                        if (h.getUsuario().getId() == userId) {
                            JSONObject habitJson = new JSONObject();
                            habitJson.put("id", h.getId());
                            habitJson.put("habitoNombre", h.getHabitoNombre());
                            habitJson.put("frecuencia", h.getFrecuencia());
                            habitJson.put("recordatorio", h.getRecordatorio());
                            habitJson.put("estado", h.isEstado());
                            habitJson.put("imagenUrl", h.getImagenUrl());

                            jsonArray.put(habitJson);
                        }
                    }

                    out.print(jsonArray.toString());

                    System.out.println("Habitos enviados: " + jsonArray.toString());

                } catch (Exception e) {
                    response.getWriter().write("Error: " + e.getMessage());
                }
                break;

            case "getStats":
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                try {
                    int userId = Integer.parseInt(request.getParameter("userId"));
                    List<Habits> habitos = habitsJPA.findHabitsEntities();

                    int completados = 0;
                    int noCompletados = 0;

                    for (Habits h : habitos) {
                        if (h.getUsuario().getId() == userId) {
                            if (h.isEstado()) {
                                completados++;
                            } else {
                                noCompletados++;
                            }
                        }
                    }

                    JSONObject jsonStats = new JSONObject();
                    jsonStats.put("completados", completados);
                    jsonStats.put("noCompletados", noCompletados);

                    PrintWriter outStats = response.getWriter();
                    outStats.print(jsonStats.toString());
                    outStats.flush();
                } catch (Exception e) {
                    response.setContentType("text/plain");
                    response.getWriter().write("Error: " + e.getMessage());
                }
                break;

            default:
                throw new AssertionError();
        }

    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
