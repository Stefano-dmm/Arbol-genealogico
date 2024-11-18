/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package árbol.genealógico;

/**
 *
 * @author mainp
 */
public class JsonArray {
    private Object[] elementos;
    private int tamaño;
    private static final int MAX_ELEMENTOS = 100;

    public JsonArray() {
        this.elementos = new Object[MAX_ELEMENTOS];
        this.tamaño = 0;
    }

    public void add(Object elemento) {
        if (tamaño >= MAX_ELEMENTOS) {
            throw new RuntimeException("Array JSON lleno");
        }
        elementos[tamaño++] = elemento;
    }

    public Object get(int indice) {
        if (indice >= tamaño) {
            throw new RuntimeException("Índice fuera de rango");
        }
        return elementos[indice];
    }

    public int size() {
        return tamaño;
    }
}
