/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package árbol.genealógico;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 *
 * @author mainp
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Crear y mostrar la ventana del explorador
        ExploradorJson explorador = new ExploradorJson();
        explorador.setVisible(true);
        
        // Iniciar sistema de búsqueda en terminal
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            
            while (true) {
                System.out.println("\n=== MENÚ DE BÚSQUEDA ===");
                System.out.println("1. Buscar por nombre");
                System.out.println("2. Buscar por título");
                System.out.println("3. Salir");
                System.out.println("Seleccione una opción (1-3):");
                
                String opcion = reader.readLine();
                
                switch (opcion) {
                    case "1":
                        if (explorador.getBuscador() != null) {
                            System.out.println("Ingrese el nombre a buscar:");
                            String nombre = reader.readLine();
                            explorador.getBuscador().buscarPorNombre(nombre);
                        } else {
                            System.out.println("Primero debe cargar un archivo JSON desde la ventana.");
                        }
                        break;
                        
                    case "2":
                        if (explorador.getBuscador() != null) {
                            System.out.println("Ingrese el título a buscar:");
                            String titulo = reader.readLine();
                            explorador.getBuscador().buscarPorTitulo(titulo);
                        } else {
                            System.out.println("Primero debe cargar un archivo JSON desde la ventana.");
                        }
                        break;
                        
                    case "3":
                        System.out.println("¡Hasta luego!");
                        System.exit(0);
                        break;
                        
                    default:
                        System.out.println("Opción no válida. Por favor, seleccione 1, 2 o 3.");
                        break;
                }
            }
            
        } catch (Exception e) {
            System.out.println("Error en la entrada: " + e.getMessage());
        }
    }
    
}
