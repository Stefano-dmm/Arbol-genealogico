/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package árbol.genealógico;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ExploradorJson {
    
    public Tree cargarDesdeArchivo(String archivo) {
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line.trim());
            }
            
            String jsonContent = content.toString();
            Tree arbol = null;
            
            // Encontrar y procesar la sección "House Baratheon"
            int startIndex = jsonContent.indexOf("\"House Baratheon\":[");
            if (startIndex != -1) {
                // Encontrar el primer nombre (raíz)
                String nombreRaiz = extraerNombre(jsonContent.substring(startIndex + 20));
                arbol = new Tree(nombreRaiz);
                
                // Dividir el contenido en secciones por persona
                String[] secciones = jsonContent.split("\\{\"");
                
                for (String seccion : secciones) {
                    if (seccion.contains("Baratheon")) {
                        // Extraer nombre de la persona
                        String nombrePersona = extraerNombre(seccion);
                        
                        // Buscar hijos
                        if (seccion.contains("\"Father to\":[")) {
                            int hijoStart = seccion.indexOf("\"Father to\":[") + 13;
                            int hijoEnd = seccion.indexOf("]", hijoStart);
                            String hijosStr = seccion.substring(hijoStart, hijoEnd);
                            
                            // Procesar lista de hijos
                            String[] hijos = hijosStr.split(",");
                            for (String hijo : hijos) {
                                hijo = hijo.trim();
                                if (hijo.startsWith("\"") && hijo.endsWith("\"")) {
                                    String nombreHijo = hijo.substring(1, hijo.length() - 1);
                                    arbol.agregarHijo(nombrePersona, nombreHijo);
                                }
                            }
                        }
                    }
                }
            }
            
            return arbol;
            
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Error al procesar el JSON: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    private String extraerNombre(String seccion) {
        int startName = seccion.indexOf("\"") + 1;
        int endName = seccion.indexOf("\"", startName);
        if (startName != -1 && endName != -1) {
            return seccion.substring(startName, endName);
        }
        return "";
    }
}
