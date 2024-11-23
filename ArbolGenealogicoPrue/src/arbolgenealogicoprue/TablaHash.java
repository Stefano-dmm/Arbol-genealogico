/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package arbolgenealogicoprue;


public class TablaHash {
     private static final int TAMAÑO_INICIAL = 101; // Número primo para mejor distribución
    private Entrada[] tabla;
    private int numElementos;

    // Clase interna para manejar las entradas de la tabla
    private class Entrada {
        String clave;
        Persona valor;
        Entrada siguiente;

        Entrada(String clave, Persona valor) {
            this.clave = clave;
            this.valor = valor;
            this.siguiente = null;
        }
    }

    // Clase para almacenar la información de cada persona
    public static class Persona { // Cambiar a "static class"
        String nombreCompleto;
        String mote;
        String titulo;
        String[] padres;
        String[] hijos;
        String casa;

        // Constructor con parámetros
        public Persona(String nombreCompleto, String mote, String titulo) {
            this.nombreCompleto = nombreCompleto;
            this.mote = mote;
            this.titulo = titulo;
            this.padres = new String[2];
            this.hijos = new String[10];
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(nombreCompleto);
            if (titulo != null) sb.append(" | Título: ").append(titulo);
            if (mote != null) sb.append(" | Mote: ").append(mote);
            return sb.toString();
        }
    }

    public TablaHash() {
        tabla = new Entrada[TAMAÑO_INICIAL];
        numElementos = 0;
    }

    private int hash(String clave) {
        int hash = 0;
        for (int i = 0; i < clave.length(); i++) {
            hash = (hash * 31 + clave.charAt(i)) % TAMAÑO_INICIAL;
        }
        return Math.abs(hash);
    }

    public void insertar(String clave, Persona valor) {
        int indice = hash(clave);
        Entrada nuevaEntrada = new Entrada(clave, valor);

        if (tabla[indice] == null) {
            tabla[indice] = nuevaEntrada;
        } else {
            Entrada actual = tabla[indice];
            while (actual.siguiente != null) {
                if (actual.clave.equals(clave)) {
                    actual.valor = valor; // Actualizar si la clave ya existe
                    return;
                }
                actual = actual.siguiente;
            }
            if (actual.clave.equals(clave)) {
                actual.valor = valor;
            } else {
                actual.siguiente = nuevaEntrada;
            }
        }
        numElementos++;
    }

    public Persona buscar(String clave) {
        int indice = hash(clave);
        Entrada actual = tabla[indice];

        while (actual != null) {
            if (actual.clave.equals(clave)) {
                return actual.valor;
            }
            actual = actual.siguiente;
        }
        return null;
    }

    public Persona[] obtenerTodasLasPersonas() {
        Persona[] personas = new Persona[numElementos];
        int contador = 0;

        for (Entrada entrada : tabla) {
            Entrada actual = entrada;
            while (actual != null) {
                if (actual.valor != null) {
                    personas[contador++] = actual.valor;
                }
                actual = actual.siguiente;
            }
        }

        Persona[] resultado = new Persona[contador];
        System.arraycopy(personas, 0, resultado, 0, contador);
        return resultado;
    }
}
