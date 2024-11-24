package árbol.genealógico;

import java.io.IOException;

public class analizadorJson {
    private JsonObject[] casas;
    private String[][] motesANombres;
    private String[] nombresCompletos;
    private int numCasas;
    private int numMotes;
    private int numNombres;
    private static final int MAX_ELEMENTOS = 100;
    private TablaHash tablaPersonas;

    public analizadorJson() {
        this.casas = new JsonObject[MAX_ELEMENTOS];
        this.motesANombres = new String[MAX_ELEMENTOS][2];
        this.nombresCompletos = new String[MAX_ELEMENTOS];
        this.numCasas = 0;
        this.numMotes = 0;
        this.numNombres = 0;
        tablaPersonas = new TablaHash();
    }

    public void cargarCasa(String rutaArchivo) throws IOException {
        JsonParser parser = JsonParser.fromFile(rutaArchivo);
        JsonObject json = parser.parseObject();
        
        for (String nombreCasa : json.getClaves()) {
            JsonArray miembros = (JsonArray) json.get(nombreCasa);
            for (int i = 0; i < miembros.size(); i++) {
                JsonObject miembro = (JsonObject) miembros.get(i);
                casas[numCasas++] = miembro;
                procesarMiembro(miembro);
            }
        }
    }

    private void procesarMiembro(JsonObject miembro) {
        for (String nombreCompleto : miembro.getClaves()) {
            nombresCompletos[numNombres++] = nombreCompleto;
            JsonArray datos = (JsonArray) miembro.get(nombreCompleto);
            
            for (int i = 0; i < datos.size(); i++) {
                JsonObject dato = (JsonObject) datos.get(i);
                if (dato.get("Known throughout as") != null) {
                    motesANombres[numMotes][0] = (String) dato.get("Known throughout as");
                    motesANombres[numMotes][1] = nombreCompleto;
                    numMotes++;
                }
            }
        }
    }

    public void mostrarPersonas() {
        for (int i = 0; i < numCasas; i++) {
            JsonObject casa = casas[i];
            for (String nombreCompleto : casa.getClaves()) {
                JsonArray datos = (JsonArray) casa.get(nombreCompleto);
                procesarPersona(nombreCompleto, datos);
            }
        }
        // Imprimir los nodos después de procesarlos
        tablaPersonas.imprimirNodos();
    }

    private void procesarPersona(String nombreCompleto, JsonArray datos) {
        // Crear o obtener la persona principal
        TablaHash.Persona persona = tablaPersonas.buscarPorNombre(nombreCompleto);
        if (persona == null) {
            persona = tablaPersonas.new Persona();
            persona.nombreCompleto = nombreCompleto;
            persona.padres = new String[2];
            persona.hijos = new String[10];
        }
        
        // Procesar datos básicos
        String nombreOfHisName = obtenerValor(datos, "Of his name");
        if (nombreOfHisName != null && !nombreOfHisName.isEmpty()) {
            // Asegurarse de que el nombre sea único agregando "Of his name" si es necesario
            String nombreUnico = nombreCompleto + " " + nombreOfHisName;
            if (tablaPersonas.buscarPorNombre(nombreUnico) == null) {
                persona.nombreCompleto = nombreUnico; // Asignamos el nombre único
            }
        }
        
        // Procesar otros atributos
        persona.mote = obtenerValor(datos, "Known throughout as");
        persona.titulo = obtenerValor(datos, "Held title");
        persona.conyuge = obtenerValor(datos, "Wed to");
        persona.ojos = obtenerValor(datos, "Of eyes");
        persona.cabello = obtenerValor(datos, "Of hair");
        persona.notas = obtenerValor(datos, "Notes");
        persona.destino = obtenerValor(datos, "Fate");
        
        // Procesar "Born to" (padres)
        String[] padres = obtenerPadres(datos);
        for (String padre : padres) {
            if (padre != null && !padre.equals("[Unknown]")) {
                // Agregar padre a la lista de padres
                for (int j = 0; j < persona.padres.length; j++) {
                    if (persona.padres[j] == null) {
                        persona.padres[j] = padre;
                        break;
                    }
                }
                
                // Crear o actualizar el nodo del padre
                TablaHash.Persona nodoPadre = tablaPersonas.buscarPorNombre(padre);
                if (nodoPadre == null) {
                    nodoPadre = tablaPersonas.new Persona();
                    nodoPadre.nombreCompleto = padre;
                    nodoPadre.padres = new String[2];
                    nodoPadre.hijos = new String[10];
                }
                agregarHijoAPadre(nodoPadre, persona.nombreCompleto);
                tablaPersonas.insertar(padre, nodoPadre);
            }
        }
        
        // Procesar hijos
        String[] hijos = obtenerLista(datos, "Father to");
        if (hijos.length > 0) {
            for (String nombreCortoHijo : hijos) {
                if (nombreCortoHijo != null) {
                    // Buscar el nombre completo del hijo basado en su primer nombre
                    String nombreCompletoHijo = buscarNombreCompletoPorPrimero(nombreCortoHijo);
                    if (nombreCompletoHijo != null) {
                        // Agregar el nombre completo del hijo a la lista de hijos
                        agregarHijoAPadre(persona, nombreCompletoHijo);
                    }
                }
            }
        }
         
        tablaPersonas.insertar(persona.nombreCompleto, persona);
    }

    private String buscarNombreCompletoPorPrimero(String primerNombre) {
        TablaHash.Persona[] personas = tablaPersonas.obtenerTodasLasPersonas();
        for (TablaHash.Persona p : personas) {
            if (p != null && p.nombreCompleto != null) {
                String[] nombres = p.nombreCompleto.split(" ");
                if (nombres.length > 0 && nombres[0].equals(primerNombre)) {
                    return p.nombreCompleto;
                }
            }
        }
        return null;
    }

    private void agregarHijoAPadre(TablaHash.Persona padre, String nombreHijo) {
        for (int i = 0; i < padre.hijos.length; i++) {
            if (padre.hijos[i] == null) {
                padre.hijos[i] = nombreHijo;
                break;
            } else if (padre.hijos[i].equals(nombreHijo)) {
                break; // Ya existe este hijo
            }
        }
    }

    private String obtenerValor(JsonArray datos, String clave) {
        for (int i = 0; i < datos.size(); i++) {
            JsonObject dato = (JsonObject) datos.get(i);
            if (dato.get(clave) != null) {
                return (String) dato.get(clave);
            }
        }
        return null;
    }

    private String[] obtenerPadres(JsonArray datos) {
        String[] padres = new String[2];  // Array fijo de 2 elementos para padre y madre
        int numPadres = 0;
        
        for (int i = 0; i < datos.size(); i++) {
            JsonObject dato = (JsonObject) datos.get(i);
            if (dato.get("Born to") != null) {
                String padre = (String) dato.get("Born to");
                if (numPadres < 2) {  // Solo tomamos los dos primeros "Born to"
                    padres[numPadres++] = padre;
                }
            }
        }
        
        return padres;  // Retornamos el array completo, puede tener nulls
    }

    private String[] obtenerLista(JsonArray datos, String clave) {
        for (int i = 0; i < datos.size(); i++) {
            JsonObject dato = (JsonObject) datos.get(i);
            if (dato.get(clave) != null) {
                JsonArray lista = (JsonArray) dato.get(clave);
                String[] resultado = new String[lista.size()];
                for (int j = 0; j < lista.size(); j++) {
                    resultado[j] = (String) lista.get(j);
                }
                return resultado;
            }
        }
        return new String[0];
    }

    public TablaHash getTablaPersonas() {
        return tablaPersonas;
    }

    // Método auxiliar para obtener el nombre de la casa
    private String obtenerNombreCasa(int indice) {
        // Aquí deberías implementar la lógica para obtener el nombre de la casa
        // basado en el índice del array de casas
        return "Casa " + indice; // Implementación temporal
    }

    // Nuevo método para buscar el nombre completo basado en el primer nombre
    private String buscarNombreCompleto(String nombreCorto) {
        // Buscar en todos los nombres completos almacenados
        for (int i = 0; i < numNombres; i++) {
            String nombreCompleto = nombresCompletos[i];
            if (nombreCompleto != null) {
                // Obtener el primer nombre del nombre completo
                String primerNombre = nombreCompleto.split(" ")[0];
                if (primerNombre.equals(nombreCorto)) {
                    return nombreCompleto;
                }
            }
        }
        return null;
    }
}