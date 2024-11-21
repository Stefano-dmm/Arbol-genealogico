/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package árbol.genealógico;


/**
 *
 * @author luisg
 */
public class Tree {
    private Nodo raiz;
    private TablaHash tabla;

    public Tree(String nombreRaiz) {
        this.raiz = new Nodo(nombreRaiz);
        this.tabla = new TablaHash();
    }

    public Nodo getRaiz() {
        return raiz;
    }

    public void setRaiz(Nodo raiz) {
        this.raiz = raiz;
    }

    public void agregarHijo(String nombrePadre, String nombreHijo) {
        Nodo padre = buscarNodo(raiz, nombrePadre);
        if (padre != null) {
            Nodo hijo = new Nodo(nombreHijo);
            if (padre.getHijos() == null) {
                padre.setHijos(new Lista());
            }
            padre.getHijos().agregarFinal(nombreHijo);
        }
    }

    private Nodo buscarNodo(Nodo actual, String nombre) {
        if (actual == null) return null;
        if (actual.getNombre().equals(nombre)) return actual;
        
        Lista hijos = actual.getHijos();
        if (hijos != null) {
            Nodo temp = hijos.getInicio();
            while (temp != null) {
                Nodo encontrado = buscarNodo(temp, nombre);
                if (encontrado != null) return encontrado;
                temp = temp.getSiguiente();
            }
        }
        return null;
    }
}
    

