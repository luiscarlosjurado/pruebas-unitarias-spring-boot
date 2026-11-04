package com.api.rest.integracion;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.api.rest.entities.Empleado;
import com.api.rest.repositories.EmpleadoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

// Levanta el contexto completo de Spring Boot
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc // Configura MockMvc para enviar peticiones HTTP
public class EmpleadoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private ObjectMapper objectMapper; // Para convertir objetos a JSON

    @BeforeEach
    void setup() {
        // Limpiar la base de datos antes de cada test
        empleadoRepository.deleteAll();
    }

    @DisplayName("Test de integración para guardar un empleado")
    @Test
    void testGuardarEmpleado() throws Exception {
        // given
        Empleado empleado = Empleado.builder()
                .nombre("pepito")
                .apellido("perez")
                .email("c1@gmail.com")
                .build();

        // when
        ResultActions response = mockMvc.perform(post("/api/empleados")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(empleado)));

        // then
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre", is(empleado.getNombre())))
                .andExpect(jsonPath("$.apellido", is(empleado.getApellido())))
                .andExpect(jsonPath("$.email", is(empleado.getEmail())));
    }

    @DisplayName("Test de integración para listar empleados")
    @Test
    void testListarEmpleados() throws Exception {
        // given
        List<Empleado> listaEmpleados = List.of(
                Empleado.builder().nombre("Ana").apellido("Perez").email("ana@gmail.com").build(),
                Empleado.builder().nombre("Juan").apellido("Lopez").email("juan@gmail.com").build()
        );
        empleadoRepository.saveAll(listaEmpleados);

        // when
        ResultActions response = mockMvc.perform(get("/api/empleados"));

        // then
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(listaEmpleados.size())));
    }

    @DisplayName("Test de integración para obtener empleado por ID")
    @Test
    void testObtenerEmpleadoPorId() throws Exception {
// given
        Empleado empleado = Empleado.builder()
                .nombre("Julen")
                        .apellido("Oliva")
                .email("j@gmail.com")
                .build();
        empleadoRepository.save(empleado);

        // when
        ResultActions response = mockMvc.perform(get("/api/empleados/{id}", empleado.getId()));

        // then
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.nombre", is(empleado.getNombre())))
                .andExpect(jsonPath("$.email", is(empleado.getEmail())));
    }

    @DisplayName("Test de integración para eliminar empleado")
    @Test
    void testEliminarEmpleado() throws Exception {
        // given
        Empleado empleado = Empleado.builder()
                .nombre("Eliminar")
                .apellido("Prueba")
                .email("delete@gmail.com")
                .build();
        empleadoRepository.save(empleado);

        // when
        ResultActions response = mockMvc.perform(delete("/api/empleados/{id}", empleado.getId()));

        // then
        response.andExpect(status().isOk())
                .andDo(print());
    }
}