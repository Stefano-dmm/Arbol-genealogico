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

    public analizadorJson() {
        this.casas = new JsonObject[MAX_ELEMENTOS];
        this.motesANombres = new String[MAX_ELEMENTOS][2];
        this.nombresCompletos = new String[MAX_ELEMENTOS];
        this.numCasas = 0;
        this.numMotes = 0;
        this.numNombres = 0;
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
                mostrarPersona(nombreCompleto, (JsonArray) casa.get(nombreCompleto));
            }
        }
    }

    private void mostrarPersona(String nombreCompleto, JsonArray datos) {
        StringBuilder linea = new StringBuilder(nombreCompleto);
        
        String numero = obtenerValor(datos, "Of his name");
        if (numero != null) {
            linea.append(" ").append(numero);
        }

        String[] padres = obtenerPadres(datos);
        if (padres.length > 0) {
            linea.append(" | Hijo de: ");
            for (int i = 0; i < padres.length; i++) {
                if (i > 0) linea.append(", ");
                linea.append(padres[i]);
            }
        }

        String mote = obtenerValor(datos, "Known throughout as");
        if (mote != null) {
            linea.append(" | Conocido como: ").append(mote);
        }

        String titulo = obtenerValor(datos, "Held title");
        if (titulo != null) {
            linea.append(" | Título: ").append(titulo);
        }

        String conyuge = obtenerValor(datos, "Wed to");
        if (conyuge != null) {
            linea.append(" | Casado con: ").append(conyuge);
        }

        String ojos = obtenerValor(datos, "Of eyes");
        if (ojos != null) {
            linea.append(" | Ojos: ").append(ojos);
        }

        String cabello = obtenerValor(datos, "Of hair");
        if (cabello != null) {
            linea.append(" | Cabello: ").append(cabello);
        }

        String[] hijos = obtenerLista(datos, "Father to");
        if (hijos.length > 0) {
            linea.append(" | Hijos: ");
            for (int i = 0; i < hijos.length; i++) {
                if (i > 0) linea.append(", ");
                linea.append(hijos[i]);
            }
        }

        String notas = obtenerValor(datos, "Notes");
        if (notas != null) {
            linea.append(" | Notas: ").append(notas);
        }

        String destino = obtenerValor(datos, "Fate");
        if (destino != null) {
            linea.append(" | Destino: ").append(destino);
        }

        System.out.println(linea);
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
