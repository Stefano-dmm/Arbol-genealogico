/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package árbol.genealógico;

/**
 *
 * @author luisg
 */
public class Nodo {
    private String nombre;
    private Lista hijos;
    private Nodo siguiente;
    private Nodo anterior;

    public Nodo(String nombre) {
        this.nombre = nombre;
        this.hijos = new Lista();
        this.siguiente = null;
        this.anterior = null;
    }

    public String getNombre() {
        return nombre;
    }

    public Lista getHijos() {
        return hijos;
    }

    public void setHijos(Lista hijos) {
        this.hijos = hijos;
    }

    public Nodo getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(Nodo siguiente) {
        this.siguiente = siguiente;
    }

    public Nodo getAnterior() {
        return anterior;
    }

    public void setAnterior(Nodo anterior) {
        this.anterior = anterior;
    }
}


