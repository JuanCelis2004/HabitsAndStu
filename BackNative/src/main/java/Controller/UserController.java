/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import Model.User;
import Persistence.UserJpaController;
import Persistence.exceptions.NonexistentEntityException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author juand
 */
@WebServlet(name = "UserController", urlPatterns = {"/UserController"})
public class UserController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        String action = request.getParameter("action");

        switch (action) {
            case "signup":

                String nombre = request.getParameter("nombre");
                String apellido = request.getParameter("apellido");
                String correo = request.getParameter("correo");
                String contrasenia = request.getParameter("contrasenia");

                if (nombre == null || apellido == null || correo == null || contrasenia == null
                        || nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty() || contrasenia.isEmpty()) {
                    out.print("ERROR: Todos los campos son obligatorios");
                    break;
                }

                UserJpaController userCtrl = new UserJpaController();
                boolean correoExiste = false;
                for (User u : userCtrl.findUserEntities()) {
                    if (u.getCorreo().equalsIgnoreCase(correo)) {
                        correoExiste = true;
                        break;
                    }
                }

                if (correoExiste) {
                    out.print("ERROR: Correo ya registrado");
                    break;
                }

                User user = new User();
                user.setNombre(nombre);
                user.setApellido(apellido);
                user.setCorreo(correo);
                user.setContrasenia(contrasenia);

                new UserJpaController().create(user);
                out.print("OK");

                break;

            case "login":
                String email = request.getParameter("correo");
                String pass = request.getParameter("contrasenia");

                if (email == null || pass == null || email.trim().isEmpty() || pass.trim().isEmpty()) {
                    out.print("ERROR: Todos los campos son obligatorios");
                    return;
                }

                UserJpaController ctrl = new UserJpaController();
                for (User u : ctrl.findUserEntities()) {
                    if (u.getCorreo().equals(email) && u.getContrasenia().equals(pass)) {
                        out.print(u.getId()); // Devuelve el ID del usuario logueado
                        return;
                    }
                }
                out.print("ERROR: Usuario o contraseña incorrectos");
                break;

            case "edit":
                int id = Integer.parseInt(request.getParameter("id"));
                String nuevoNombre = request.getParameter("nombre");
                String nuevoApellido = request.getParameter("apellido");

                User usuarioEditado = new UserJpaController().findUser(id);
                if (usuarioEditado != null) {
                    usuarioEditado.setNombre(nuevoNombre);
                    usuarioEditado.setApellido(nuevoApellido);
                    try {
                        new UserJpaController().edit(usuarioEditado);
                    } catch (Exception ex) {
                        Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    out.print("OK");
                } else {
                    out.print("ERROR: Usuario no encontrado");
                }
                break;

            case "delete":
                int idBorrar = Integer.parseInt(request.getParameter("id"));
                 {
                    try {
                        new UserJpaController().destroy(idBorrar);
                    } catch (NonexistentEntityException ex) {
                        Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                out.print("OK");
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
