package pruebaarboles2;

import org.json.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;

/**
 * Clase principal que maneja la creación y visualización de árboles genealógicos
 * a partir de archivos JSON.
 * 
 * Esta clase proporciona funcionalidades para:
 * - Leer archivos JSON que contienen información genealógica
 * - Imprimir estructuras familiares en consola
 * - Crear y mostrar representaciones visuales de árboles genealógicos
 * 
 * @author [Tu Nombre]
 * @version 1.0
 */
public class Pruebaarboles2 {
    /**
     * Imprime en consola la estructura completa del árbol genealógico.
     * Muestra información detallada de cada miembro incluyendo títulos,
     * relaciones familiares y notas relevantes.
     * 
     * @param json Objeto JSON que contiene la información del árbol genealógico
     */
    public static void imprimirArbolGenealogico(JSONObject json) {
        String nombreCasa = json.keys().next();
        JSONArray familia = json.getJSONArray(nombreCasa);
        int totalNodos = familia.length();
        System.out.println(nombreCasa + " - Total de miembros: " + totalNodos);
        System.out.println("====================================");
        
        for (int i = 0; i < familia.length(); i++) {
            JSONObject persona = familia.getJSONObject(i);
            String nombrePersona = persona.keys().next();
            JSONArray infoPersona = persona.getJSONArray(nombrePersona);
            
            System.out.println("\n" + (i+1) + ". " + nombrePersona);
            System.out.println("------------------------");
            
            // Contador para hijos
            int numHijos = 0;
            
            // Imprimir información básica
            for (int j = 0; j < infoPersona.length(); j++) {
                JSONObject atributo = infoPersona.getJSONObject(j);
                String key = atributo.keys().next();
                
                if (key.equals("Father to")) {
                    JSONArray hijos = atributo.getJSONArray(key);
                    numHijos = hijos.length();
                    System.out.print("Padre de (" + numHijos + " hijos): ");
                    for (int k = 0; k < hijos.length(); k++) {
                        System.out.print(hijos.getString(k));
                        if (k < hijos.length() - 1) {
                            System.out.print(", ");
                        }
                    }
                    System.out.println();
                } else {
                    System.out.println(key + ": " + atributo.get(key));
                }
            }
        }
    }

    /**
     * Abre un diálogo para seleccionar y leer un archivo JSON.
     * Permite al usuario seleccionar archivos con extensión .json para
     * cargar nuevos árboles genealógicos.
     * 
     * @return String con el contenido del archivo JSON seleccionado,
     *         o una cadena vacía si no se seleccionó ningún archivo o hubo un error
     */
    public static String leerArchivoJson() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecciona un archivo JSON");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos JSON", "json");
        fileChooser.setFileFilter(filter);

        StringBuilder contenido = new StringBuilder();
        
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            try (BufferedReader br = new BufferedReader(new FileReader(fileChooser.getSelectedFile()))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    contenido.append(linea).append("\n");
                }
            } catch (IOException e) {
                System.err.println("Error al leer el archivo: " + e.getMessage());
            }
        }
        
        return contenido.toString();
    }

    /**
     * Método principal que inicia la aplicación.
     * Permite al usuario seleccionar un archivo JSON para crear y visualizar
     * un árbol genealógico.
     * 
     * El proceso incluye:
     * 1. Selección del archivo JSON
     * 2. Lectura y parsing del contenido
     * 3. Construcción del árbol genealógico
     * 4. Visualización del grafo resultante
     * 
     * @param args Argumentos de línea de comando (no utilizados)
     */
    public static void main(String[] args) {
        String jsonContent = leerArchivoJson();
        if (!jsonContent.isEmpty()) {
            try {
                JSONObject json = new JSONObject(jsonContent);
                Tree arbol = new Tree();
                arbol.construirArbol(json);
                arbol.mostrarGrafo();
            } catch (JSONException e) {
                System.err.println("Error al analizar JSON: " + e.getMessage());
            }
        }
    }
}
