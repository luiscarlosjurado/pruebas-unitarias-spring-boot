package com.api.rest;

import java.time.Duration;

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
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.github.bonigarcia.wdm.WebDriverManager;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        baseUrl = "http://localhost:" + port + "/empleados";
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
    }

    @AfterEach // <-- Descomentado para que libere memoria y cierre el navegador
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

        driver.findElement(By.id("nombre")).sendKeys("Carlos");
        driver.findElement(By.id("apellido")).sendKeys("Jurado");
        // CORREGIDO: Usamos el mismo email que verificamos abajo
        driver.findElement(By.id("email")).sendKeys("carlos@gmail.com");

        // Clic en el botón Guardar
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Espera explícita a que cargue la lista con los datos nuevos
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(d -> d.findElement(By.tagName("body")).getText().contains("Carlos"));

        String bodyText = driver.findElement(By.tagName("body")).getText();
        assertTrue(bodyText.contains("Carlos"));
        assertTrue(bodyText.contains("carlos@gmail.com"));
    }

    @Test
    @Order(2)
    @DisplayName("Debe editar un empleado existente")
    void testEditarEmpleado() {
        driver.get(baseUrl);

        WebElement botonEditar = driver.findElement(By.linkText("Editar"));
        botonEditar.click();

        WebElement inputNombre = driver.findElement(By.id("nombre"));
        inputNombre.clear();
        inputNombre.sendKeys("Carlos Modificado");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // CORREGIDO: Espera a que la página se actualice con el nuevo nombre
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        boolean actualizado = wait.until(d -> d.getPageSource().contains("Carlos Modificado"));

        assertTrue(actualizado);
    }

    @Test
    @Order(3)
    @DisplayName("Debe eliminar un empleado y manejar la alerta de confirmación")
    void testEliminarEmpleado() {
        driver.get(baseUrl);

        WebElement botonEliminar = driver.findElement(By.linkText("Eliminar"));
        botonEliminar.click();

        Alert alert = driver.switchTo().alert();
        alert.accept();

        // Espera a que el texto desaparezca tras eliminar
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(d -> !d.findElement(By.tagName("body")).getText().contains("Carlos Modificado"));

        String bodyText = driver.findElement(By.tagName("body")).getText();
        assertTrue(!bodyText.contains("Carlos Modificado"));
    }
}