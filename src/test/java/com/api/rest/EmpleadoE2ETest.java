package com.api.rest;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.github.bonigarcia.wdm.WebDriverManager;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Para ejecutar en orden
public class EmpleadoE2ETest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private String baseUrl;

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setupTest() {
        driver = new ChromeDriver();
        baseUrl = "http://localhost:" + port + "/empleados";
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2)); // Espera si el navegador es lento
    }

    //@AfterEach
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Debe crear un nuevo empleado y mostrarlo en la lista")
    void testCrearEmpleado() {
        driver.get(baseUrl + "/nuevo");

        // Thymeleaf genera IDs automáticamente basados en th:field
        driver.findElement(By.id("nombre")).sendKeys("ninguno");
        driver.findElement(By.id("apellido")).sendKeys("ninguno");
        driver.findElement(By.id("email")).sendKeys("ninguno@gmail.com");

        // Clic en el botón Guardar
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Verificar que volvimos a la lista y aparece el nuevo empleado
        String bodyText = driver.findElement(By.tagName("body")).getText();
        assertTrue(bodyText.contains("Carlos"));
        assertTrue(bodyText.contains("carlos@gmail.com"));
    }

    @Test
    @Order(2)
    @DisplayName("Debe editar un empleado existente")
    void testEditarEmpleado() {
        driver.get(baseUrl);

        // Buscar el botón 'Editar' del primer empleado en la tabla
        WebElement botonEditar = driver.findElement(By.linkText("Editar"));
        botonEditar.click();

        // Modificar el nombre
        WebElement inputNombre = driver.findElement(By.id("nombre"));
        inputNombre.clear();
        inputNombre.sendKeys("Carlos Modificado");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Verificar cambio
        assertTrue(driver.getPageSource().contains("Carlos Modificado"));
    }

    @Test
    @Order(3)
    @DisplayName("Debe eliminar un empleado y manejar la alerta de confirmación")
    void testEliminarEmpleado() {
        driver.get(baseUrl);

        // Clic en el botón Eliminar
        WebElement botonEliminar = driver.findElement(By.linkText("Eliminar"));
        botonEliminar.click();

        // Manejar la alerta de JavaScript: onclick="return confirm('¿Estás seguro?')"
        Alert alert = driver.switchTo().alert();
        alert.accept(); // Equivale a darle a "Aceptar"

        // Verificar que el texto ya no existe (asumiendo que era el único)
        String bodyText = driver.findElement(By.tagName("body")).getText();
        assertTrue(!bodyText.contains("Carlos Modificado"));
    }
}