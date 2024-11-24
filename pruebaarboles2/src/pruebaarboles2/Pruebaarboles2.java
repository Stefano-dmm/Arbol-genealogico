package pruebaarboles2;

import org.json.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;

public class Pruebaarboles2 {
    public static void imprimirArbolGenealogico(JSONObject json) {
        JSONArray familia = json.getJSONArray("House Targaryen");
        int totalNodos = familia.length();
        System.out.println("Casa Targaryen - Total de miembros: " + totalNodos);
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

    public static void main(String[] args) {
        String jsonContent = leerArchivoJson();
        if (!jsonContent.isEmpty()) {
            try {
                JSONObject json = new JSONObject(jsonContent);
                TreeGraph arbol = new TreeGraph();
                arbol.construirArbol(json);
                arbol.mostrarGrafo();
            } catch (JSONException e) {
                System.err.println("Error al analizar JSON: " + e.getMessage());
            }
        }
    }
}
