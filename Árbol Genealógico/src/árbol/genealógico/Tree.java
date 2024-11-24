/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package árbol.genealógico;

public class Tree {
    private TreeNode[] nodes;
    private int size;

    public Tree() {
        nodes = new TreeNode[100]; // Capacidad inicial
        size = 0;
    }

    // Clase interna para representar un nodo en el árbol
    private class TreeNode {
        String nombreCompleto;
        String mote;
        String titulo;
        String conyuge;
        String[] padres;
        String[] hijos;
        int numHijos;

        TreeNode(String nombreCompleto) {
            this.nombreCompleto = nombreCompleto;
            this.mote = null;
            this.titulo = null;
            this.conyuge = null;
            this.padres = new String[2]; // Suponiendo que cada persona puede tener hasta 2 padres
            this.hijos = new String[10]; // Suponiendo que cada persona puede tener hasta 10 hijos
            this.numHijos = 0;
        }
    }

    // Método para agregar una persona al árbol
    public void agregarPersona(String nombreCompleto, String mote, String titulo, String conyuge, String[] padres) {
        TreeNode nodo = new TreeNode(nombreCompleto);
        nodo.mote = mote;
        nodo.titulo = titulo;
        nodo.conyuge = conyuge;
        nodo.padres = padres;
        nodes[size++] = nodo; // Agregar el nodo al arreglo
    }

    // Método para construir el árbol a partir de los datos de TablaHash
    public void construirArbolDesdeJson(TablaHash tablaPersonas) {
        for (TablaHash.Persona persona : tablaPersonas.obtenerTodasLasPersonas()) {
            String[] padres = persona.padres;
            agregarPersona(persona.nombreCompleto, persona.mote, persona.titulo, persona.conyuge, padres);
            
            // Agregar hijos al nodo correspondiente
            TreeNode nodo = obtenerNodo(persona.nombreCompleto);
            if (nodo != null) {
                for (String hijo : persona.hijos) {
                    if (hijo != null) {
                        nodo.hijos[nodo.numHijos++] = hijo; // Agregar hijo
                    }
                }
            }
        }
    }

    // Método para obtener un nodo por nombre
    public TreeNode obtenerNodo(String nombreCompleto) {
        for (int i = 0; i < size; i++) {
            if (nodes[i].nombreCompleto.equals(nombreCompleto)) {
                return nodes[i];
            }
        }
        return null; // Si no se encuentra el nodo
    }

    // Método para mostrar el árbol
    public void mostrarArbol() {
        for (int i = 0; i < size; i++) {
            TreeNode nodo = nodes[i];
            System.out.println("Nombre: " + nodo.nombreCompleto + ", Mote: " + nodo.mote);
            // Aquí puedes agregar más detalles sobre el nodo
        }
    }
}

