/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package árbol.genealógico;

/**
 *
 * @author luisg
 */
public class Lista {
    private Nodo inicio;
    private Nodo fin;

    public Lista() {
        this.inicio = null;
        this.fin = null;
    }

    public void agregarFinal(String dato) {
        Nodo nuevo = new Nodo(dato);
        if (inicio == null) {
            inicio = nuevo;
            fin = nuevo;
        } else {
            nuevo.setSiguiente(null);
            fin.setSiguiente(nuevo);
            fin = nuevo;
        }
    }

    public Nodo getInicio() {
        return inicio;
    }

    public int tamano() {
        int count = 0;
        Nodo actual = inicio;
        while (actual != null) {
            count++;
            actual = actual.getSiguiente();
        }
        return count;
    }
}