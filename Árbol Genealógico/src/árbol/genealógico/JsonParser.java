/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package árbol.genealógico;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class JsonParser {
    private String contenido;
    private int posicion;

    public JsonParser(String contenido) {
        this.contenido = contenido;
        this.posicion = 0;
    }

    public static JsonParser fromFile(String rutaArchivo) throws IOException {
        StringBuilder contenido = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                contenido.append(linea);
            }
        }
        return new JsonParser(contenido.toString());
    }

    public JsonObject parseObject() {
        JsonObject objeto = new JsonObject();
        consumirEspacios();
        if (contenido.charAt(posicion) != '{') {
            throw new RuntimeException("Se esperaba '{'");
        }
        posicion++;

        while (posicion < contenido.length()) {
            consumirEspacios();
            if (contenido.charAt(posicion) == '}') {
                posicion++;
                break;
            }

            String clave = parseString();
            consumirEspacios();
            if (contenido.charAt(posicion) != ':') {
                throw new RuntimeException("Se esperaba ':'");
            }
            posicion++;
            consumirEspacios();

            if (contenido.charAt(posicion) == '[') {
                objeto.put(clave, parseArray());
            } else if (contenido.charAt(posicion) == '{') {
                objeto.put(clave, parseObject());
            } else {
                objeto.put(clave, parseString());
            }

            consumirEspacios();
            if (posicion < contenido.length() && contenido.charAt(posicion) == ',') {
                posicion++;
            }
        }

        return objeto;
    }

    public JsonArray parseArray() {
        JsonArray array = new JsonArray();
        consumirEspacios();
        if (contenido.charAt(posicion) != '[') {
            throw new RuntimeException("Se esperaba '['");
        }
        posicion++;

        while (posicion < contenido.length()) {
            consumirEspacios();
            if (contenido.charAt(posicion) == ']') {
                posicion++;
                break;
            }

            if (contenido.charAt(posicion) == '{') {
                array.add(parseObject());
            } else {
                array.add(parseString());
            }

            consumirEspacios();
            if (posicion < contenido.length() && contenido.charAt(posicion) == ',') {
                posicion++;
            }
        }

        return array;
    }

    private String parseString() {
        consumirEspacios();
        StringBuilder valor = new StringBuilder();
        boolean enComillas = false;

        if (contenido.charAt(posicion) == '"') {
            enComillas = true;
            posicion++;
        }

        while (posicion < contenido.length()) {
            char c = contenido.charAt(posicion);
            if (enComillas && c == '"') {
                posicion++;
                break;
            } else if (!enComillas && (c == ',' || c == '}' || c == ']')) {
                break;
            }
            valor.append(c);
            posicion++;
        }

        return valor.toString();
    }

    private void consumirEspacios() {
        while (posicion < contenido.length() && 
               Character.isWhitespace(contenido.charAt(posicion))) {
            posicion++;
        }
    }
}
