/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package árbol.genealógico;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

/**
 *
 * @author mainp
 */
public class Main {

    /**
     * @param args the command line arguments
     */

    public static void main(String[] args) {
        // Configurar el UI de GraphStream antes de cualquier otra operación
        System.setProperty("org.graphstream.ui", "swing");
        
        // Crear un selector de archivos
        JFileChooser fileChooser = new JFileChooser();
        
        // Configurar el selector para mostrar solo archivos JSON
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos JSON", "json");
        fileChooser.setFileFilter(filter);
        
        // Establecer el directorio inicial como el directorio del proyecto
        String projectPath = System.getProperty("user.dir");
        File projectDir = new File(projectPath + "/src/árbol/genealógico/json");
        if (projectDir.exists()) {
            fileChooser.setCurrentDirectory(projectDir);
        }
        
        // Mostrar el diálogo de selección de archivo
        int result = fileChooser.showOpenDialog(null);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                // Obtener el archivo seleccionado
                File selectedFile = fileChooser.getSelectedFile();
                
                // Cargar y procesar el archivo
                ExploradorJson explorador = new ExploradorJson();
                Tree arbol = explorador.cargarDesdeArchivo(selectedFile.getAbsolutePath());
                
                if (arbol != null) {
                    // Crear y mostrar el grafo
                    Grafo grafo = new Grafo(arbol);
                    grafo.mostrarGrafo();
                } else {
                    System.out.println("Error al cargar el archivo JSON");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("No se seleccionó ningún archivo");
        }
    }
}


