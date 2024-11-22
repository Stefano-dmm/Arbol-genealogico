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
    
    public Buscador(TablaHash tablaPersonas) {
        this.tablaPersonas = tablaPersonas;
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

    public TablaHash.Persona buscarPorNombreYResaltar(String nombre) {
        String nombreBusqueda = nombre.toLowerCase().trim();
        TablaHash.Persona personaEncontrada = null;
        
        TablaHash.Persona[] personas = tablaPersonas.obtenerTodasLasPersonas();
        
        // Primero buscar coincidencias exactas
        for (TablaHash.Persona persona : personas) {
            if (persona != null && 
                persona.nombreCompleto != null && 
                persona.nombreCompleto.equalsIgnoreCase(nombreBusqueda)) {
                personaEncontrada = persona;
                break;
            }
        }
        
        // Si no hay coincidencias exactas, buscar coincidencias parciales
        if (personaEncontrada == null) {
            for (TablaHash.Persona persona : personas) {
                if (persona != null && 
                    persona.nombreCompleto != null && 
                    persona.nombreCompleto.toLowerCase().contains(nombreBusqueda)) {
                    personaEncontrada = persona;
                    break;
                }
            }
        }
        
        // Mostrar los resultados en la consola como antes
        buscarPorNombre(nombre);
        
        return personaEncontrada;
    }

    public TablaHash.Persona buscarPorTituloYResaltar(String titulo) {
        String tituloBusqueda = titulo.toLowerCase().trim();
        TablaHash.Persona personaEncontrada = null;
        
        TablaHash.Persona[] personas = tablaPersonas.obtenerTodasLasPersonas();
        
        for (TablaHash.Persona persona : personas) {
            if (persona != null && 
                persona.titulo != null && 
                persona.titulo.toLowerCase().contains(tituloBusqueda)) {
                personaEncontrada = persona;
                break;
            }
        }
        
        // Mostrar los resultados en la consola como antes
        buscarPorTitulo(titulo);
        
        return personaEncontrada;
    }
}
