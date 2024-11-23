/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package arbolgenealogicoprue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class analizadorJson {
    private Tree tree;
    private TablaHash tablaHash;

    public analizadorJson(Tree tree) {
        this.tree = tree;
        this.tablaHash = new TablaHash();
    }

    // Método para cargar el archivo JSON y construir el árbol genealógico
    public void cargarCasa(String archivoPath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(archivoPath));
        String linea;
        Tree.Nodo raiz = null;
        Tree.Nodo nodoPadre = null;
        
        while ((linea = reader.readLine()) != null) {
            linea = linea.trim();

            // Saltar líneas vacías o cabeceras
            if (linea.isEmpty() || linea.contains("House")) {
                continue;
            }

            // Detectar la línea con nombre de la persona
            if (linea.contains(":")) {
                String nombre = linea.substring(0, linea.indexOf(":")).trim();
                String[] detalles = parseDetalles(reader);
                
                // Crear nodo de la persona
                Tree.Nodo nodo = new Tree.Nodo(nombre, detalles[0], detalles[1], detalles[2]);

                // Almacenar en TablaHash
                TablaHash.Persona persona = new TablaHash.Persona(nombre, detalles[0], detalles[1]);
                tablaHash.insertar(nombre, persona);

                // Si no hay raíz, asignamos la raíz
                if (raiz == null) {
                    raiz = nodo;
                }

                // Establecer relaciones padre-hijo
                if (detalles[3] != null && !detalles[3].equals("")) {
                    nodoPadre = tree.buscarNodo(detalles[3]);
                    nodo.setPadre(nodoPadre);
                    nodoPadre.agregarHijo(nodo);
                }

                // Establecer relaciones de hijos
                if (detalles[4] != null && !detalles[4].equals("")) {
                    String[] hijos = detalles[4].split(",");
                    for (String hijo : hijos) {
                        Tree.Nodo hijoNodo = new Tree.Nodo(hijo, "Unknown", "Unknown", "Unknown");
                        nodo.agregarHijo(hijoNodo);
                    }
                }
            }
        }

        // Establecer la raíz del árbol
        if (raiz != null) {
            tree.setRaiz(raiz);
        }
    }

    private String[] parseDetalles(BufferedReader reader) throws IOException {
        String[] detalles = new String[5];

        String linea;
        while ((linea = reader.readLine()) != null) {
            linea = linea.trim();
            if (linea.contains("Father to")) {
                detalles[4] = linea.substring(linea.indexOf(":") + 1).trim(); // Hijos
            } else if (linea.contains("Of his name")) {
                detalles[0] = linea.substring(linea.indexOf(":") + 1).trim(); // Nombre
            } else if (linea.contains("Held title")) {
                detalles[1] = linea.substring(linea.indexOf(":") + 1).trim(); // Título
            } else if (linea.contains("Born to")) {
                detalles[3] = linea.substring(linea.indexOf(":") + 1).trim(); // Padre
            } else if (linea.contains("Of eyes")) {
                detalles[2] = linea.substring(linea.indexOf(":") + 1).trim(); // Color de ojos
            }

            // Salir del loop si encontramos todos los detalles
            if (detalles[4] != null && detalles[0] != null && detalles[1] != null && detalles[3] != null && detalles[2] != null) {
                break;
            }
        }

        return detalles;
    }
}
