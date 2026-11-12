package com.api.rest.services;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

import com.api.rest.entities.Empleado;
import com.api.rest.exception.ResourceNotFoundException;
import com.api.rest.repositories.EmpleadoRepository;
import com.api.rest.service.EmpleadoServiceImpl;

@ExtendWith(MockitoExtension.class)
public class EmpleadoServicesTests {
    @Mock
    private EmpleadoRepository empleadoRepository;

    @InjectMocks
    private EmpleadoServiceImpl empleadoService;

    private Empleado empleado;

    @BeforeEach
    void setup() {
        empleado = Empleado.builder()
                .nombre("juan")
                .apellido("perez")
                .email("a2@gmail.com")
                .build();
    }

    @DisplayName("Test para guardar un empleado")
    @Test
    void testGuardarEmpleado() {
        // given
        given(empleadoRepository.findByEmail(empleado.getEmail()))
                .willReturn(Optional.empty());
        given(empleadoRepository.save(empleado)).willReturn(empleado);

        // when
        Empleado empleadoGuardado = empleadoService.saveEmpleado(empleado);

        // then
        assertThat(empleadoGuardado).isNotNull();
    }

    @DisplayName("Test para guardar un empleado con Throw Exception")
    @Test
    void testGuardarEmpleadoConThrowException() {
        // given
        given(empleadoRepository.findByEmail(empleado.getEmail()))
                .willReturn(Optional.of(empleado));
        // when
        assertThrows(ResourceNotFoundException.class, () -> {
            empleadoService.saveEmpleado(empleado);
        });

        // then
        verify(empleadoRepository, never()).save(any(Empleado.class));
    }

    @DisplayName("Test para listar a los empleados")
    @Test
    void testListarEmpleados() {
        // given
        Empleado empleado1 = Empleado.builder()
                .id(2L)
                .nombre("Pepito")
                .apellido("perez")
                .email("p2@gmail.com")
                .build();
        given(empleadoRepository.findAll()).willReturn(List.of(empleado, empleado1));

        // when
        List<Empleado> empleados = empleadoService.getAllEmpleados();

        // then
        assertThat(empleados).isNotNull();
        assertThat(empleados.size()).isEqualTo(1);
    }

    @DisplayName("Test para retornar una lista vacia")
    @Test
    void testListarColeccionEmpleadosVacia() {
        // given
        Empleado empleado1 = Empleado.builder()
                .id(2L)
                .nombre("Julen")
                .apellido("Oliva")
                .email("j2@gmail.com")
                .build();
        given(empleadoRepository.findAll()).willReturn(Collections.emptyList());

        // when
        List<Empleado> listaEmpleados = empleadoService.getAllEmpleados();

        // then
        assertThat(listaEmpleados).isEmpty();
        assertThat(listaEmpleados.size()).isEqualTo(0);
    }

    @DisplayName("Test para obtener un empleado por ID")
    @Test
    void testObtenerEmpleadoPorId() {
        Empleado empleado1 = Empleado.builder()
                .id(1L)
                .nombre("Julen")
                .apellido("Oliva")
                .email("j2@gmail.com")
                .build();

        // given
        given(empleadoRepository.findById(1L)).willReturn(Optional.of(empleado1));

        // when
        Empleado empleadoGuardado = empleadoService.getEmpleadoById(empleado1.getId()).get();

        // then
        assertThat(empleadoGuardado).isNotNull();
    }

    @DisplayName("Test para actualizar un empleado")
    @Test
    void testActualizarEmpleado() {
        // given
        given(empleadoRepository.save(empleado)).willReturn(empleado);
        empleado.setEmail("cl2@gmail.com");
        empleado.setNombre("Clistian Raul");

        // when
        Empleado empleadoActualizado = empleadoService.updateEmpleado(empleado);

        // then
        assertThat(empleadoActualizado.getEmail()).isEqualTo("cl2@gmail.com");
        assertThat(empleadoActualizado.getNombre()).isEqualTo("Clistian Raul");
    }

    @DisplayName("Test para eliminar un empleado")
    @Test
    void testEliminarEmpleado() {
        // given
        long empleadoId = 1L;
        willDoNothing().given(empleadoRepository).deleteById(empleadoId);

        // when
        empleadoService.deleteEmpleado(empleadoId);

        // then
        verify(empleadoRepository, times(1)).deleteById(empleadoId);
    }
}
