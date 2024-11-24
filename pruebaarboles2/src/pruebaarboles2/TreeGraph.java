package pruebaarboles2;

import org.json.JSONArray;
import org.json.JSONObject;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.view.Viewer;
import java.util.*;

public class TreeGraph {
    private Graph graph;
    private Map<String, List<String>> conexiones;
    private Map<String, String> nombreCompletos;

    public TreeGraph() {
        System.setProperty("org.graphstream.ui", "swing");
        this.graph = new SingleGraph("Árbol Genealógico Targaryen");
        this.conexiones = new HashMap<>();
        this.nombreCompletos = new HashMap<>();
        
        // Estilo para el grafo
        String styleSheet = 
            "node {" +
            "   fill-color: #A40000;" +
            "   size: 30px;" +
            "   text-size: 14px;" +
            "   text-color: white;" +
            "   text-style: bold;" +
            "   text-background-mode: rounded-box;" +
            "   text-background-color: #A40000;" +
            "   text-padding: 5px;" +
            "}" +
            "edge {" +
            "   fill-color: #400000;" +
            "   arrow-size: 10px;" +
            "}";
        
        graph.setAttribute("ui.stylesheet", styleSheet);
    }

    private String obtenerNombreCompleto(String nombre, String numeroNombre) {
        return nombre + " (" + numeroNombre + ")";
    }

    private String obtenerNumeroRomano(String nombreCompleto) {
        return nombreCompleto.substring(nombreCompleto.length() - 2).trim().replace("(", "").replace(")", "");
    }

    private String obtenerNumeroRomanoPorInfo(JSONArray infoPersona) {
        for (int j = 0; j < infoPersona.length(); j++) {
            JSONObject atributo = infoPersona.getJSONObject(j);
            if (atributo.has("Of his name")) {
                String num = atributo.getString("Of his name");
                switch(num) {
                    case "First": return "I";
                    case "Second": return "II";
                    case "Third": return "III";
                    case "Fourth": return "IV";
                    case "Fifth": return "V";
                    case "Sixth": return "VI";
                    default: return "I";
                }
            }
        }
        return "I";
    }

    private int obtenerGeneracion(String nombreNodo, Map<String, Integer> generaciones) {
        if (nombreNodo.contains("Aegon") && nombreNodo.contains("_I_")) {
            return 0; // Aegon I es la primera generación
        }
        return generaciones.getOrDefault(nombreNodo, -1);
    }

    private boolean existeConexion(String nodoA, String nodoB) {
        for (Edge edge : graph.edges().toArray(Edge[]::new)) {
            String source = edge.getSourceNode().getId();
            String target = edge.getTargetNode().getId();
            if ((source.equals(nodoA) && target.equals(nodoB)) || 
                (source.equals(nodoB) && target.equals(nodoA))) {
                return true;
            }
        }
        return false;
    }

    public void construirArbol(JSONObject json) {
        JSONArray familia = json.getJSONArray("House Targaryen");
        Set<String> nodosConectados = new HashSet<>();
        Set<String> nodosConHijos = new HashSet<>();
        Map<String, Integer> contadorNombres = new HashMap<>();
        Map<String, String> nodoPadreMap = new HashMap<>();
        
        // Primera pasada: crear todos los nodos y registrar relaciones padre-hijo
        for (int i = 0; i < familia.length(); i++) {
            JSONObject persona = familia.getJSONObject(i);
            String nombreBase = persona.keys().next();
            JSONArray infoPersona = persona.getJSONArray(nombreBase);
            
            contadorNombres.putIfAbsent(nombreBase, 0);
            int contador = contadorNombres.get(nombreBase);
            contadorNombres.put(nombreBase, contador + 1);
            
            String numeroNombre = obtenerNumeroRomanoPorInfo(infoPersona);
            String nombreCompleto = obtenerNombreCompleto(nombreBase, numeroNombre);
            String nodoId = nombreBase.replaceAll("\\s+", "_") + "_" + numeroNombre + "_" + contador;
            nombreCompletos.put(nombreBase + "_" + contador, nombreCompleto);
            
            Node node = graph.addNode(nodoId);
            node.setAttribute("ui.label", nombreCompleto);
            
            for (int j = 0; j < infoPersona.length(); j++) {
                JSONObject atributo = infoPersona.getJSONObject(j);
                String key = atributo.keys().next();
                if (key.equals("Born to")) {
                    String padre = atributo.getString(key);
                    if (!padre.equals("[Unknown]")) {
                        nodoPadreMap.put(nodoId, padre + " Targaryen");
                    }
                }
            }
            
            System.out.println("Creado nodo: " + nombreCompleto + " (ID: " + nodoId + ")");
        }
        
        // Crear mapa de generaciones
        Map<String, Integer> generaciones = new HashMap<>();
        
        // Primera pasada: establecer generaciones basadas en "Born to"
        for (int i = 0; i < familia.length(); i++) {
            JSONObject persona = familia.getJSONObject(i);
            String nombreBase = persona.keys().next();
            JSONArray infoPersona = persona.getJSONArray(nombreBase);
            
            String nodoId = nombreBase.replaceAll("\\s+", "_") + "_" + 
                           obtenerNumeroRomanoPorInfo(infoPersona) + "_" + 
                           contadorNombres.get(nombreBase);
            
            for (int j = 0; j < infoPersona.length(); j++) {
                JSONObject atributo = infoPersona.getJSONObject(j);
                String key = atributo.keys().next();
                if (key.equals("Born to")) {
                    String padre = atributo.getString(key);
                    if (!padre.equals("[Unknown]")) {
                        String nombrePadre = padre + " Targaryen";
                        // Si el padre es Aegon I, establecer generación
                        if (padre.equals("Aegon") && nombrePadre.contains("I")) {
                            generaciones.put(nodoId, 1);
                        } else if (generaciones.containsKey(nombrePadre)) {
                            generaciones.put(nodoId, generaciones.get(nombrePadre) + 1);
                        }
                    }
                }
            }
        }
        
        // Segunda pasada: establecer conexiones padre-hijo con validación de generaciones
        for (int i = 0; i < familia.length(); i++) {
            JSONObject persona = familia.getJSONObject(i);
            String nombreBase = persona.keys().next();
            JSONArray infoPersona = persona.getJSONArray(nombreBase);
            
            String padreId = null;
            String nombrePadreCompleto = null;
            for (Map.Entry<String, String> entry : nombreCompletos.entrySet()) {
                if (entry.getKey().startsWith(nombreBase + "_")) {
                    padreId = nombreBase.replaceAll("\\s+", "_") + "_" + 
                             obtenerNumeroRomano(entry.getValue()) + "_" + 
                             entry.getKey().substring(entry.getKey().lastIndexOf("_") + 1);
                    nombrePadreCompleto = entry.getValue();
                    break;
                }
            }
            
            if (padreId != null) {
                for (int j = 0; j < infoPersona.length(); j++) {
                    JSONObject atributo = infoPersona.getJSONObject(j);
                    String key = atributo.keys().next();
                    
                    if (key.equals("Father to")) {
                        JSONArray hijos = atributo.getJSONArray(key);
                        for (int k = 0; k < hijos.length(); k++) {
                            String nombreHijo = hijos.getString(k) + " Targaryen";
                            
                            for (Map.Entry<String, String> entry : nombreCompletos.entrySet()) {
                                if (entry.getKey().startsWith(nombreHijo + "_")) {
                                    String hijoId = nombreHijo.replaceAll("\\s+", "_") + "_" + 
                                                  obtenerNumeroRomano(entry.getValue()) + "_" +
                                                  entry.getKey().substring(entry.getKey().lastIndexOf("_") + 1);
                                    
                                    // Validar que no exista ninguna conexión en ninguna dirección
                                    if (!existeConexion(padreId, hijoId)) {
                                        String padrePotencial = nodoPadreMap.get(padreId);
                                        if (padrePotencial == null || !nombreHijo.equals(padrePotencial)) {
                                            try {
                                                String edgeId = padreId + "_to_" + hijoId;
                                                if (!nodosConectados.contains(hijoId)) {
                                                    graph.addEdge(edgeId, padreId, hijoId, true);
                                                    nodosConectados.add(hijoId);
                                                    nodosConHijos.add(padreId);
                                                    System.out.println("Conexión válida: " + nombrePadreCompleto + " -> " + entry.getValue());
                                                }
                                            } catch (Exception e) {
                                                System.err.println("Error al conectar: " + e.getMessage());
                                            }
                                        } else {
                                            System.out.println("Evitada conexión inversa: " + nombrePadreCompleto + " -> " + entry.getValue());
                                        }
                                    } else {
                                        System.out.println("Evitada conexión bidireccional entre: " + padreId + " y " + hijoId);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Conectar nodos aislados solo si es necesario
        for (Node nodo : graph) {
            String nodoId = nodo.getId();
            if (!nodosConectados.contains(nodoId) && !nodosConHijos.contains(nodoId)) {
                for (String nodoPadreId : nodosConHijos) {
                    String padrePotencial = nodoPadreMap.get(nodoId);
                    if ((padrePotencial == null || !nodoPadreId.contains(padrePotencial.replaceAll("\\s+", "_"))) 
                        && !existeConexion(nodoPadreId, nodoId)) {
                        try {
                            String edgeId = nodoPadreId + "_to_" + nodoId;
                            graph.addEdge(edgeId, nodoPadreId, nodoId, true);
                            System.out.println("Conexión adicional: " + nodoPadreId + " -> " + nodoId);
                            break;
                        } catch (Exception e) {
                            System.err.println("Error en conexión adicional: " + e.getMessage());
                        }
                    }
                }
            }
        }
        
        System.out.println("\nEstadísticas finales:");
        System.out.println("Total de nodos: " + graph.getNodeCount());
        System.out.println("Total de conexiones: " + graph.getEdgeCount());
        System.out.println("Nodos con hijos: " + nodosConHijos.size());
    }

    public void mostrarGrafo() {
        Viewer viewer = graph.display();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
    }
} 