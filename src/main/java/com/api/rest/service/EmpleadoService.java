package com.api.rest.service;

import java.util.List;
import java.util.Optional;

import com.api.rest.entities.Empleado;



public interface  EmpleadoService {
    Empleado saveEmpleado(Empleado empleado);
    List<Empleado> getAllEmpleados();

    Optional<Empleado> getEmpleadoById(Long id);

    Empleado updateEmpleado(Empleado empleadoActualizado);

    void deleteEmpleado(Long id);

}


