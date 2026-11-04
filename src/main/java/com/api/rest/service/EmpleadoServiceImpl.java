package com.api.rest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.rest.entities.Empleado;
import com.api.rest.exception.ResourceNotFoundException;
import com.api.rest.repositories.EmpleadoRepository;

@Service
public class EmpleadoServiceImpl implements EmpleadoService  {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Override
    public Empleado saveEmpleado(Empleado empleado) {
        Optional<Empleado> empleadoGuardado = empleadoRepository.findByEmail(empleado.getEmail());
        if (empleadoGuardado.isPresent()) {
            throw new ResourceNotFoundException("El empleado con el email " + empleado.getEmail() + " ya existe.");
        }
        return empleadoRepository.save(empleado);
    }

    @Override
    public Optional<Empleado> getEmpleadoById(Long id) {
        return empleadoRepository.findById(id);
    }

    @Override
    public List<Empleado> getAllEmpleados() {
        return empleadoRepository.findAll();
    }

 

    @Override
    public Empleado updateEmpleado(Empleado empleadoActualizado) {
        return empleadoRepository.save(empleadoActualizado);
    }
    
    @Override
    public void deleteEmpleado(Long id) {
        empleadoRepository.deleteById(id);
    }

}

