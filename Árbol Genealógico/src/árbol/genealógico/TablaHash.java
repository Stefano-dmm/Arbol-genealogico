/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package árbol.genealógico;

/**
 *
 * @author mainp
 */
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
    public class Persona {
        String nombreCompleto;
        String numero;
        String[] padres;
        String mote;
        String titulo;
        String conyuge;
        String ojos;
        String cabello;
        String[] hijos;
        String notas;
        String destino;
        String casa;
        
        public Persona() {
            this.padres = new String[2];
            this.hijos = new String[10];
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(nombreCompleto);
            
            if (numero != null) {
                sb.append(" ").append(numero);
            }
            
            if (padres[0] != null) {
                sb.append(" | Hijo de: ");
                if (padres[1] != null) {
                    sb.append(padres[0]).append(" y ").append(padres[1]);
                } else {
                    sb.append(padres[0]);
                }
            }
            
            if (mote != null) {
                sb.append(" | Conocido como: ").append(mote);
            }
            
            if (titulo != null) {
                sb.append(" | Título: ").append(titulo);
            }
            
            if (conyuge != null) {
                sb.append(" | Casado con: ").append(conyuge);
            }
            
            if (ojos != null) {
                sb.append(" | Ojos: ").append(ojos);
            }
            
            if (cabello != null) {
                sb.append(" | Cabello: ").append(cabello);
            }
            
            if (hijos[0] != null) {
                sb.append(" | Hijos: ");
                boolean primero = true;
                for (String hijo : hijos) {
                    if (hijo != null) {
                        if (!primero) {
                            sb.append(", ");
                        }
                        sb.append(hijo);
                        primero = false;
                    }
                }
            }
            
            if (notas != null) {
                sb.append(" | Notas: ").append(notas);
            }
            
            if (destino != null) {
                sb.append(" | Destino: ").append(destino);
            }
            
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
            // Manejo de colisiones por encadenamiento
            Entrada actual = tabla[indice];
            while (actual.siguiente != null) {
                if (actual.clave.equals(clave)) {
                    actual.valor = valor; // Actualizar valor si la clave ya existe
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
    
    public void eliminar(String clave) {
        int indice = hash(clave);
        Entrada actual = tabla[indice];
        Entrada anterior = null;
        
        while (actual != null && !actual.clave.equals(clave)) {
            anterior = actual;
            actual = actual.siguiente;
        }
        
        if (actual != null) {
            if (anterior == null) {
                tabla[indice] = actual.siguiente;
            } else {
                anterior.siguiente = actual.siguiente;
            }
            numElementos--;
        }
    }
    
    public int tamaño() {
        return numElementos;
    }
    
    public boolean estaVacia() {
        return numElementos == 0;
    }
    
    public void mostrarTodos() {
        for (Entrada entrada : tabla) {
            Entrada actual = entrada;
            while (actual != null) {
                System.out.println(actual.valor.toString());
                actual = actual.siguiente;
            }
        }
    }
    
    // Método para obtener todas las personas de una casa específica
    public Persona[] obtenerPersonasPorCasa(String nombreCasa) {
        Persona[] personas = new Persona[numElementos];
        int contador = 0;
        
        for (Entrada entrada : tabla) {
            Entrada actual = entrada;
            while (actual != null) {
                if (actual.valor.casa.equals(nombreCasa)) {
                    personas[contador++] = actual.valor;
                }
                actual = actual.siguiente;
            }
        }
        
        // Crear un nuevo array con el tamaño exacto
        Persona[] resultado = new Persona[contador];
        System.arraycopy(personas, 0, resultado, 0, contador);
        return resultado;
    }
    
    // Método para obtener los hijos de una persona
    public Persona[] obtenerHijos(String nombrePadre) {
        Persona padre = buscar(nombrePadre);
        if (padre == null || padre.hijos == null) {
            return new Persona[0];
        }
        
        Persona[] hijos = new Persona[padre.hijos.length];
        int contador = 0;
        
        for (String nombreHijo : padre.hijos) {
            if (nombreHijo != null) {
                Persona hijo = buscar(nombreHijo);
                if (hijo != null) {
                    hijos[contador++] = hijo;
                }
            }
        }
        
        Persona[] resultado = new Persona[contador];
        System.arraycopy(hijos, 0, resultado, 0, contador);
        return resultado;
    }
    
    public void imprimirNodos() {
        System.out.println("\n=== NODOS DEL ÁRBOL GENEALÓGICO ===");
        System.out.println("Total de personas registradas: " + numElementos);
        System.out.println("----------------------------------------");
        
        for (Entrada entrada : tabla) {
            Entrada actual = entrada;
            while (actual != null) {
                Persona p = actual.valor;
                System.out.println("\nNODO: " + p.nombreCompleto);
                System.out.println("----------------------------------------");
                if (p.numero != null) {
                    System.out.println("Número: " + p.numero);
                }
                if (p.padres[0] != null || p.padres[1] != null) {
                    System.out.print("Padres: ");
                    if (p.padres[0] != null) System.out.print(p.padres[0]);
                    if (p.padres[1] != null) System.out.print(" y " + p.padres[1]);
                    System.out.println();
                }
                if (p.hijos[0] != null) {
                    System.out.print("Hijos: ");
                    boolean primero = true;
                    for (String hijo : p.hijos) {
                        if (hijo != null) {
                            if (!primero) System.out.print(", ");
                            System.out.print(hijo);
                            primero = false;
                        }
                    }
                    System.out.println();
                }
                if (p.casa != null) {
                    System.out.println("Casa: " + p.casa);
                }
                System.out.println("----------------------------------------");
                actual = actual.siguiente;
            }
        }
    }
    
    public Persona[] obtenerTodasLasPersonas() {
        Persona[] personas = new Persona[numElementos];
        int indice = 0;
        
        for (Entrada entrada : tabla) {
            Entrada actual = entrada;
            while (actual != null) {
                if (actual.valor != null) {
                    personas[indice++] = actual.valor;
                }
                actual = actual.siguiente;
            }
        }
        
        // Eliminar nulls y ajustar el tamaño del array
        int contadorNoNulos = 0;
        for (Persona p : personas) {
            if (p != null) contadorNoNulos++;
        }
        
        Persona[] resultado = new Persona[contadorNoNulos];
        int j = 0;
        for (Persona p : personas) {
            if (p != null) {
                resultado[j++] = p;
            }
        }
        
        return resultado;
    }
}
