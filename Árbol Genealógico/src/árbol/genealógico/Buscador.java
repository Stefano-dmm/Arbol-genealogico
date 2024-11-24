/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package árbol.genealógico;

/**
 *
 * @author mainp
 */

// no se si se debe iluminar el nodo buscado pero como no e iniciado con lo grafico no lo aplico 

public class Buscador {
    private TablaHash tablaPersonas;
    private VisualizadorArbol visualizador;
    
    public Buscador(TablaHash tablaPersonas) {
        this.tablaPersonas = tablaPersonas;
        this.visualizador = null;
    }
    
    public Buscador(TablaHash tablaPersonas, VisualizadorArbol visualizador) {
        this.tablaPersonas = tablaPersonas;
        this.visualizador = visualizador;
    }
    
    public TablaHash.Persona buscarPorNombreYResaltar(String nombre) {
        TablaHash.Persona persona = tablaPersonas.buscarPorNombre(nombre);
        if (persona != null) {
            System.out.println("\nPersona encontrada:");
            System.out.println(persona.toString());
            // Usar el nuevo método de resaltado simple
            visualizador.resaltarNodo(persona.nombreCompleto);
        } else {
            System.out.println("No se encontró a la persona especificada.");
        }
        return persona;
    }

    public TablaHash.Persona buscarPorTituloYResaltar(String titulo) {
        TablaHash.Persona persona = buscarPersona(titulo, false);
        buscarPorTitulo(titulo); // Mostrar resultados en consola
        return persona;
    }

    private TablaHash.Persona buscarPersona(String texto, boolean porNombre) {
        String textoBusqueda = texto.toLowerCase().trim();
        TablaHash.Persona[] personas = tablaPersonas.obtenerTodasLasPersonas();
        
        // Buscar coincidencia exacta
        for (TablaHash.Persona p : personas) {
            if (p != null) {
                String valorComparar = porNombre ? p.nombreCompleto : p.titulo;
                if (valorComparar != null && 
                    valorComparar.equalsIgnoreCase(textoBusqueda)) {
                    return p;
                }
            }
        }
        
        // Buscar coincidencia parcial
        for (TablaHash.Persona p : personas) {
            if (p != null) {
                String valorComparar = porNombre ? p.nombreCompleto : p.titulo;
                if (valorComparar != null && 
                    valorComparar.toLowerCase().contains(textoBusqueda)) {
                    return p;
                }
            }
        }
        
        return null;
    }

    public void buscarPorNombre(String nombre) {
        String nombreBusqueda = nombre.toLowerCase().trim();
        int contadorResultados = 0;
        
        System.out.println("\n=== RESULTADOS DE BÚSQUEDA POR NOMBRE ===");
        System.out.println("Buscando: " + nombre);
        System.out.println("----------------------------------------");
        
        TablaHash.Persona[] personas = tablaPersonas.obtenerTodasLasPersonas();
        
        // Primero buscar coincidencias exactas
        for (TablaHash.Persona persona : personas) {
            if (persona != null && 
                persona.nombreCompleto != null && 
                persona.nombreCompleto.equalsIgnoreCase(nombreBusqueda)) {
                contadorResultados++;
                System.out.println("\nRESULTADO #" + contadorResultados + ":");
                mostrarPersona(persona);
            }
        }
        
        // Luego buscar coincidencias parciales
        for (TablaHash.Persona persona : personas) {
            if (persona != null && 
                persona.nombreCompleto != null && 
                !persona.nombreCompleto.equalsIgnoreCase(nombreBusqueda) && 
                persona.nombreCompleto.toLowerCase().contains(nombreBusqueda)) {
                contadorResultados++;
                System.out.println("\nRESULTADO #" + contadorResultados + ":");
                mostrarPersona(persona);
            }
        }
        
        if (contadorResultados == 0) {
            System.out.println("No se encontraron resultados para: " + nombre);
        } else {
            System.out.println("\nTotal de resultados encontrados: " + contadorResultados);
        }
        
        System.out.println("----------------------------------------");
    }
    
    public void buscarPorTitulo(String titulo) {
        String tituloBusqueda = titulo.toLowerCase().trim();
        int contadorResultados = 0;
        
        System.out.println("\n=== RESULTADOS DE BÚSQUEDA POR TÍTULO ===");
        System.out.println("Buscando título: " + titulo);
        System.out.println("----------------------------------------");
        
        TablaHash.Persona[] personas = tablaPersonas.obtenerTodasLasPersonas();
        
        for (TablaHash.Persona persona : personas) {
            if (persona != null && 
                persona.titulo != null && 
                persona.titulo.toLowerCase().contains(tituloBusqueda)) {
                contadorResultados++;
                System.out.println("\nRESULTADO #" + contadorResultados + ":");
                mostrarPersona(persona);
            }
        }
        
        if (contadorResultados == 0) {
            System.out.println("No se encontraron resultados para el título: " + titulo);
        } else {
            System.out.println("\nTotal de resultados encontrados: " + contadorResultados);
        }
        
        System.out.println("----------------------------------------");
    }
    
    private void mostrarPersona(TablaHash.Persona persona) {
        System.out.println("Nombre: " + persona.nombreCompleto);
        
        if (persona.numero != null) {
            System.out.println("Número: " + persona.numero);
        }
        
        if (persona.titulo != null) {
            System.out.println("Título: " + persona.titulo);
        }
        
        if (persona.mote != null) {
            System.out.println("Conocido como: " + persona.mote);
        }
        
        if (persona.padres[0] != null || persona.padres[1] != null) {
            System.out.print("Padres: ");
            if (persona.padres[0] != null) {
                System.out.print(persona.padres[0]);
                if (persona.padres[1] != null) {
                    System.out.print(" y " + persona.padres[1]);
                }
            }
            System.out.println();
        }
        
        if (persona.hijos[0] != null) {
            System.out.print("Hijos: ");
            boolean primero = true;
            for (String hijo : persona.hijos) {
                if (hijo != null) {
                    if (!primero) System.out.print(", ");
                    System.out.print(hijo);
                    primero = false;
                }
            }
            System.out.println();
        }
        
        if (persona.casa != null) {
            System.out.println("Casa: " + persona.casa);
        }
        
        if (persona.destino != null) {
            System.out.println("Destino: " + persona.destino);
        }
    }

    public void buscarHijosDe(String nombrePadre) {
        TablaHash.Persona padre = tablaPersonas.buscarPorNombre(nombrePadre);
        if (padre != null) {
            System.out.println("\nHijos de " + padre.nombreCompleto + ":");
            for (String hijo : padre.hijos) {
                if (hijo != null) {
                    System.out.println("- " + hijo);
                }
            }
            // Resaltar padre e hijos en el grafo
            visualizador.resaltarFamilia(padre.nombreCompleto);
        } else {
            System.out.println("No se encontró a la persona especificada.");
        }
    }

    public void buscarPadresDe(String nombreHijo) {
        TablaHash.Persona hijo = tablaPersonas.buscarPorNombre(nombreHijo);
        if (hijo != null) {
            System.out.println("\nPadres de " + hijo.nombreCompleto + ":");
            if (hijo.padres != null) {
                for (String padre : hijo.padres) {
                    if (padre != null) {
                        System.out.println("- " + padre);
                    }
                }
            }
            // Resaltar hijo y padres en el grafo
            visualizador.resaltarFamiliaInversa(hijo.nombreCompleto);
        } else {
            System.out.println("No se encontró a la persona especificada.");
        }
    }
}
