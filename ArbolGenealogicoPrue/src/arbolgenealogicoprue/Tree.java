/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package arbolgenealogicoprue;

public class Tree {

    private Nodo raiz;

    // Constructor que inicializa el árbol
    public Tree() {
        this.raiz = null;
    }

    // Método para establecer la raíz del árbol genealógico
    public void setRaiz(Nodo raiz) {
        this.raiz = raiz;
    }

    // Método para obtener la raíz del árbol
    public Nodo getRaiz() {
        return raiz;
    }

    // Método para buscar un nodo por nombre
    public Nodo buscarNodo(String nombre) {
        return buscarNodoRecursivo(raiz, nombre);
    }

    private Nodo buscarNodoRecursivo(Nodo nodo, String nombre) {
        if (nodo == null) return null;

        if (nodo.getNombreCompleto().equals(nombre)) {
            return nodo;
        }

        // Buscar en los hijos (solo hijos varones)
        Nodo hijo = nodo.getPrimerHijo();
        while (hijo != null) {
            Nodo encontrado = buscarNodoRecursivo(hijo, nombre);
            if (encontrado != null) {
                return encontrado;
            }
            hijo = hijo.getSiguienteHermano();
        }

        return null;
    }

    public static class Nodo {
        private String nombreCompleto;
        private Nodo padre; 
        private Nodo siguienteHermano; 
        private Nodo primerHijo; 
        private String mote;
        private String titulo;
        private String casa;

        // Constructor para el nodo con los datos básicos
        public Nodo(String nombreCompleto, String mote, String titulo, String casa) {
            this.nombreCompleto = nombreCompleto;
            this.mote = mote;
            this.titulo = titulo;
            this.casa = casa;
            this.padre = null;
            this.siguienteHermano = null;
            this.primerHijo = null;
        }

        // Getter para obtener el nombre completo
        public String getNombreCompleto() {
            return nombreCompleto;
        }

        // Getter y Setter para el padre
        public Nodo getPadre() {
            return padre;
        }

        public void setPadre(Nodo padre) {
            this.padre = padre;
        }

        // Getter para el primer hijo
        public Nodo getPrimerHijo() {
            return primerHijo;
        }

        // Método para agregar un hijo (solo varones)
        public void agregarHijo(Nodo hijo) {
            if (this.primerHijo == null) {
                this.primerHijo = hijo;
            } else {
                Nodo ultimoHijo = this.primerHijo;
                while (ultimoHijo.siguienteHermano != null) {
                    ultimoHijo = ultimoHijo.siguienteHermano;
                }
                ultimoHijo.siguienteHermano = hijo;
            }
        }

        // Getter para obtener el siguiente hermano
        public Nodo getSiguienteHermano() {
            return siguienteHermano;
        }

        // Setter para establecer el siguiente hermano
        public void setSiguienteHermano(Nodo siguienteHermano) {
            this.siguienteHermano = siguienteHermano;
        }

        // Getter para obtener la casa
        public String getCasa() {
            return casa;
        }

        // Método para obtener la cadena representativa del Nodo
        @Override
        public String toString() {
            return nombreCompleto + " (" + mote + ")";
        }
    }
}

