package com.api.rest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.api.rest.entities.Empleado;
import com.api.rest.service.EmpleadoService;

@Controller
@RequestMapping("/empleados") // Diferente de la ruta /api/
public class EmpleadoWebController {

    @Autowired
    private EmpleadoService empleadoService;

    // Listar empleados
    @GetMapping
    public String listarEmpleados(Model model) {
        model.addAttribute("empleados", empleadoService.getAllEmpleados());
        return "empleados"; // Busca empleados.html en templates
    }

    // Mostrar formulario de registro
    @GetMapping("/nuevo")
    public String mostrarFormularioRegistro(Model model) {
        Empleado empleado = new Empleado();
        model.addAttribute("empleado", empleado);
        return "form-empleado"; // Busca form-empleado.html
    }

    // Guardar empleado
    @PostMapping("/guardar")
    public String guardarEmpleado(@ModelAttribute("empleado") Empleado empleado) {
        if (empleado.getId() != null) {
            empleadoService.updateEmpleado(empleado);
        } else {
            empleadoService.saveEmpleado(empleado);
        }
        return "redirect:/empleados";
    }

    // Mostrar formulario de edición
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Empleado empleado = empleadoService.getEmpleadoById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
        model.addAttribute("empleado", empleado);
        return "form-empleado";
    }

    // Eliminar empleado
    @DeleteMapping("/eliminar/{id}")
    public String eliminarEmpleado(@PathVariable Long id) {
        empleadoService.deleteEmpleado(id);
        return "redirect:/empleados";
    }
}


