/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package árbol.genealógico;

/**
 *
 * @author mainp
 */
public class JsonObject {
    private String[] claves;
    private Object[] valores;
    private int tamaño;
    private static final int MAX_ELEMENTOS = 100;

    public JsonObject() {
        this.claves = new String[MAX_ELEMENTOS];
        this.valores = new Object[MAX_ELEMENTOS];
        this.tamaño = 0;
    }

    public void put(String clave, Object valor) {
        if (tamaño >= MAX_ELEMENTOS) {
            throw new RuntimeException("Objeto JSON lleno");
        }
        claves[tamaño] = clave;
        valores[tamaño] = valor;
        tamaño++;
    }

    public Object get(String clave) {
        for (int i = 0; i < tamaño; i++) {
            if (claves[i].equals(clave)) {
                return valores[i];
            }
        }
        return null;
    }

    public String[] getClaves() {
        String[] resultado = new String[tamaño];
        System.arraycopy(claves, 0, resultado, 0, tamaño);
        return resultado;
    }

    public int size() {
        return tamaño;
    }
}
