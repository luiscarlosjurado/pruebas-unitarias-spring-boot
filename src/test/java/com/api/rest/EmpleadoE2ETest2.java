package com.api.rest;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.api.rest.entities.Empleado;
import com.api.rest.repositories.EmpleadoRepository;

import io.github.bonigarcia.wdm.WebDriverManager;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EmpleadoE2ETest2 {

    @LocalServerPort
    private int port;

    @Autowired
    private EmpleadoRepository empleadoRepository; // Inyecta tu repositorio

    private WebDriver driver;
    private String baseUrl;

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setupTest() {
        // Opcional: Limpiar la base de datos antes de cada test para tener un entorno limpio
        // empleadoRepository.deleteAll(); 
        
        driver = new ChromeDriver();
        baseUrl = "http://localhost:" + port + "/empleados";
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
    }

    //@AfterEach
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Debe crear un nuevo empleado")
    void testCrearEmpleado() {
        driver.get(baseUrl + "/nuevo");
        driver.findElement(By.id("nombre")).sendKeys("Carlos");
        driver.findElement(By.id("apellido")).sendKeys("Sanz");
        driver.findElement(By.id("email")).sendKeys("carlos@gmail.com");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        assertTrue(driver.findElement(By.tagName("body")).getText().contains("Carlos"));
    }

    @Test
    @Order(2)
    @DisplayName("Debe editar un empleado existente")
    void testEditarEmpleado() {
        // 1. PREPARACIÓN: Insertamos el dato manualmente en la DB
        // Así este test no depende de que el Test 1 haya funcionado
        Empleado emp = new Empleado("Ana", "Lopez", "ana@gmail.com");
        empleadoRepository.save(emp);
        // 2. EJECUCIÓN
        driver.get(baseUrl);
        
        // Buscamos el botón editar (puedes ser más específico si hay varios)
        WebElement botonEditar = driver.findElement(By.xpath("//td[contains(text(), 'Ana')]/following-sibling::td/a[contains(text(), 'Editar')]"));
        botonEditar.click();

        WebElement inputNombre = driver.findElement(By.id("nombre"));
        inputNombre.clear();
        inputNombre.sendKeys("Ana Modificada");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // 3. VERIFICACIÓN
        assertTrue(driver.getPageSource().contains("Ana Modificada"));
    }

    @Test
    @Order(3)
    @DisplayName("Debe eliminar un empleado")
    void testEliminarEmpleado() {
        // 1. PREPARACIÓN
        Empleado emp = new Empleado("Borrar", "Persona", "borrar@gmail.com");
        empleadoRepository.save(emp);

        // 2. EJECUCIÓN
        driver.get(baseUrl);
        WebElement botonEliminar = driver.findElement(By.xpath("//td[contains(text(), 'Borrar')]/following-sibling::td/a[contains(text(), 'Eliminar')]"));
        botonEliminar.click();

        Alert alert = driver.switchTo().alert();
        alert.accept();

        // 3. VERIFICACIÓN
        // Esperamos un poco a que la página cargue tras la alerta
        String bodyText = driver.findElement(By.tagName("body")).getText();
        assertFalse(bodyText.contains("Borrar Persona"));
    }
}
