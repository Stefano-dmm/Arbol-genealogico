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
        TablaHash.Persona persona = tablaPersonas.new Persona();
        persona.nombreCompleto = nombreCompleto;
        
        // Datos básicos
        persona.numero = obtenerValor(datos, "Of his name");
        persona.mote = obtenerValor(datos, "Known throughout as");
        persona.titulo = obtenerValor(datos, "Held title");
        persona.conyuge = obtenerValor(datos, "Wed to");
        persona.ojos = obtenerValor(datos, "Of eyes");
        persona.cabello = obtenerValor(datos, "Of hair");
        persona.notas = obtenerValor(datos, "Notes");
        persona.destino = obtenerValor(datos, "Fate");
        
        // Procesar padres
        String[] padres = obtenerPadres(datos);
        if (padres.length > 0) {
            System.arraycopy(padres, 0, persona.padres, 0, Math.min(padres.length, 2));
        }
        
        // Procesar hijos
        String[] hijos = obtenerLista(datos, "Father to");
        if (hijos.length > 0) {
            System.arraycopy(hijos, 0, persona.hijos, 0, Math.min(hijos.length, 10));
        }
        
        // Insertar en la tabla hash
        tablaPersonas.insertar(nombreCompleto, persona);
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
        String[] padres = new String[MAX_ELEMENTOS];
        int numPadres = 0;
        
        for (int i = 0; i < datos.size(); i++) {
            JsonObject dato = (JsonObject) datos.get(i);
            if (dato.get("Born to") != null) {
                padres[numPadres++] = (String) dato.get("Born to");
            }
        }
        
        String[] resultado = new String[numPadres];
        System.arraycopy(padres, 0, resultado, 0, numPadres);
        return resultado;
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
}
