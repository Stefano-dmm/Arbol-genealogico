package pruebaarboles2;

import org.json.JSONArray;
import org.json.JSONObject;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.view.Viewer;
import pruebaarboles2.estructuras.SimpleMap;
import pruebaarboles2.estructuras.SimpleSet;

public class TreeGraph {
    private Graph graph;
    private SimpleMap<String, String> nombreCompletos;
    private static final String STYLE_SHEET = 
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

    public TreeGraph() {
        System.setProperty("org.graphstream.ui", "swing");
        this.graph = new SingleGraph("Árbol Genealógico Targaryen");
        this.nombreCompletos = new SimpleMap<>();
        graph.setAttribute("ui.stylesheet", STYLE_SHEET);
    }

    private String obtenerNumeroRomano(JSONArray infoPersona) {
        for (int j = 0; j < infoPersona.length(); j++) {
            JSONObject atributo = infoPersona.getJSONObject(j);
            if (atributo.has("Of his name")) {
                switch(atributo.getString("Of his name")) {
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
        SimpleSet<String> nodosConectados = new SimpleSet<>();
        SimpleSet<String> nodosConHijos = new SimpleSet<>();
        SimpleMap<String, Integer> contadorNombres = new SimpleMap<>();
        SimpleMap<String, String> nodoPadreMap = new SimpleMap<>();
        
        // Primera pasada: crear nodos
        crearNodos(familia, contadorNombres, nodoPadreMap);
        
        // Segunda pasada: establecer conexiones
        establecerConexiones(familia, nodosConectados, nodosConHijos, contadorNombres, nodoPadreMap);
        
        // Conectar nodos aislados
        conectarNodosAislados(nodosConectados, nodosConHijos, nodoPadreMap);
        
        imprimirEstadisticas();
    }

    private void crearNodos(JSONArray familia, SimpleMap<String, Integer> contadorNombres, 
                           SimpleMap<String, String> nodoPadreMap) {
        for (int i = 0; i < familia.length(); i++) {
            JSONObject persona = familia.getJSONObject(i);
            String nombreBase = persona.keys().next();
            JSONArray infoPersona = persona.getJSONArray(nombreBase);
            
            contadorNombres.putIfAbsent(nombreBase, 0);
            int contador = contadorNombres.get(nombreBase);
            contadorNombres.put(nombreBase, contador + 1);
            
            String numeroNombre = obtenerNumeroRomano(infoPersona);
            String nombreCompleto = nombreBase + " (" + numeroNombre + ")";
            String nodoId = nombreBase.replaceAll("\\s+", "_") + "_" + numeroNombre + "_" + contador;
            
            nombreCompletos.put(nombreBase + "_" + contador, nombreCompleto);
            
            Node node = graph.addNode(nodoId);
            node.setAttribute("ui.label", nombreCompleto);
            
            procesarPadre(infoPersona, nodoId, nodoPadreMap);
        }
    }

    private void procesarPadre(JSONArray infoPersona, String nodoId, SimpleMap<String, String> nodoPadreMap) {
        for (int j = 0; j < infoPersona.length(); j++) {
            JSONObject atributo = infoPersona.getJSONObject(j);
            if (atributo.has("Born to")) {
                String padre = atributo.getString("Born to");
                if (!padre.equals("[Unknown]")) {
                    nodoPadreMap.put(nodoId, padre + " Targaryen");
                }
                break;
            }
        }
    }

    private void establecerConexiones(JSONArray familia, SimpleSet<String> nodosConectados,
                                    SimpleSet<String> nodosConHijos, SimpleMap<String, Integer> contadorNombres,
                                    SimpleMap<String, String> nodoPadreMap) {
        for (int i = 0; i < familia.length(); i++) {
            JSONObject persona = familia.getJSONObject(i);
            String nombreBase = persona.keys().next();
            JSONArray infoPersona = persona.getJSONArray(nombreBase);
            
            procesarConexiones(nombreBase, infoPersona, nodosConectados, nodosConHijos, 
                             contadorNombres, nodoPadreMap);
        }
    }

    private void procesarConexiones(String nombreBase, JSONArray infoPersona, 
                                  SimpleSet<String> nodosConectados, SimpleSet<String> nodosConHijos,
                                  SimpleMap<String, Integer> contadorNombres,
                                  SimpleMap<String, String> nodoPadreMap) {
        String padreId = obtenerIdPadre(nombreBase);
        if (padreId != null) {
            for (int j = 0; j < infoPersona.length(); j++) {
                JSONObject atributo = infoPersona.getJSONObject(j);
                if (atributo.has("Father to")) {
                    procesarHijos(padreId, atributo.getJSONArray("Father to"), 
                                nodosConectados, nodosConHijos, nodoPadreMap);
                }
            }
        }
    }

    private String obtenerIdPadre(String nombreBase) {
        for (SimpleMap.Entry<String, String> entry : nombreCompletos.entrySet()) {
            if (entry.getKey().startsWith(nombreBase + "_")) {
                String nombreCompleto = entry.getValue();
                String numeroRomano = nombreCompleto.substring(
                    nombreCompleto.indexOf("(") + 1, 
                    nombreCompleto.indexOf(")")
                );
                return nombreBase.replaceAll("\\s+", "_") + "_" + numeroRomano + "_" + 
                       entry.getKey().substring(entry.getKey().lastIndexOf("_") + 1);
            }
        }
        return null;
    }

    private void procesarHijos(String padreId, JSONArray hijos, SimpleSet<String> nodosConectados,
                             SimpleSet<String> nodosConHijos, SimpleMap<String, String> nodoPadreMap) {
        for (int k = 0; k < hijos.length(); k++) {
            String nombreHijo = hijos.getString(k) + " Targaryen";
            crearConexionPadreHijo(padreId, nombreHijo, nodosConectados, nodosConHijos, nodoPadreMap);
        }
    }

    private void crearConexionPadreHijo(String padreId, String nombreHijo, 
                                      SimpleSet<String> nodosConectados,
                                      SimpleSet<String> nodosConHijos,
                                      SimpleMap<String, String> nodoPadreMap) {
        for (SimpleMap.Entry<String, String> entry : nombreCompletos.entrySet()) {
            if (entry.getKey().startsWith(nombreHijo + "_")) {
                String nombreCompleto = entry.getValue();
                String numeroRomano = nombreCompleto.substring(
                    nombreCompleto.indexOf("(") + 1, 
                    nombreCompleto.indexOf(")")
                );
                
                String hijoId = nombreHijo.replaceAll("\\s+", "_") + "_" + 
                               numeroRomano + "_" +
                               entry.getKey().substring(entry.getKey().lastIndexOf("_") + 1);
                
                if (!existeConexion(padreId, hijoId)) {
                    String padrePotencial = nodoPadreMap.get(padreId);
                    if (padrePotencial == null || !nombreHijo.equals(padrePotencial)) {
                        agregarConexion(padreId, hijoId, nodosConectados, nodosConHijos);
                    }
                }
            }
        }
    }

    private void agregarConexion(String padreId, String hijoId, 
                               SimpleSet<String> nodosConectados,
                               SimpleSet<String> nodosConHijos) {
        try {
            if (!nodosConectados.contains(hijoId)) {
                graph.addEdge(padreId + "_to_" + hijoId, padreId, hijoId, true);
                nodosConectados.add(hijoId);
                nodosConHijos.add(padreId);
            }
        } catch (Exception e) {
            System.err.println("Error al conectar: " + e.getMessage());
        }
    }

    private void conectarNodosAislados(SimpleSet<String> nodosConectados,
                                     SimpleSet<String> nodosConHijos,
                                     SimpleMap<String, String> nodoPadreMap) {
        for (Node nodo : graph) {
            String nodoId = nodo.getId();
            if (!nodosConectados.contains(nodoId) && !nodosConHijos.contains(nodoId)) {
                conectarNodoAislado(nodoId, nodosConHijos, nodoPadreMap);
            }
        }
    }

    private void conectarNodoAislado(String nodoId, SimpleSet<String> nodosConHijos,
                                   SimpleMap<String, String> nodoPadreMap) {
        String[] padres = nodosConHijos.toArray();
        for (String nodoPadreId : padres) {
            String padrePotencial = nodoPadreMap.get(nodoId);
            if ((padrePotencial == null || 
                 !nodoPadreId.contains(padrePotencial.replaceAll("\\s+", "_"))) && 
                !existeConexion(nodoPadreId, nodoId)) {
                try {
                    graph.addEdge(nodoPadreId + "_to_" + nodoId, nodoPadreId, nodoId, true);
                    break;
                } catch (Exception e) {
                    System.err.println("Error en conexión adicional: " + e.getMessage());
                }
            }
        }
    }

    private void imprimirEstadisticas() {
        System.out.println("\nEstadísticas finales:");
        System.out.println("Total de nodos: " + graph.getNodeCount());
        System.out.println("Total de conexiones: " + graph.getEdgeCount());
    }

    public void mostrarGrafo() {
        Viewer viewer = graph.display();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
        
        // Crear y mostrar la ventana de información
        InfoVentana infoVentana = new InfoVentana();
        actualizarInformacionNodos(infoVentana);
        infoVentana.setVisible(true);
    }

    private void actualizarInformacionNodos(InfoVentana ventana) {
        StringBuilder info = new StringBuilder();
        info.append("INFORMACIÓN DE LA CASA TARGARYEN\n");
        info.append("================================\n\n");
        
        // Información general
        info.append("Total de miembros: ").append(graph.getNodeCount()).append("\n");
        info.append("Total de conexiones: ").append(graph.getEdgeCount()).append("\n\n");
        
        // Información de cada nodo
        info.append("MIEMBROS DE LA FAMILIA:\n");
        info.append("====================\n\n");
        
        for (Node nodo : graph) {
            String id = nodo.getId();
            String nombreCompleto = (String) nodo.getAttribute("ui.label");
            info.append("ID: ").append(id).append("\n");
            info.append("Nombre: ").append(nombreCompleto).append("\n");
            
            // Contar conexiones entrantes (padres)
            int padres = 0;
            Edge[] edgesEntrantes = nodo.enteringEdges().toArray(Edge[]::new);
            for (Edge e : edgesEntrantes) {
                padres++;
                info.append("   Padre: ").append(e.getSourceNode().getAttribute("ui.label")).append("\n");
            }
            
            // Contar conexiones salientes (hijos)
            int hijos = 0;
            Edge[] edgesSalientes = nodo.leavingEdges().toArray(Edge[]::new);
            for (Edge e : edgesSalientes) {
                hijos++;
                info.append("   Hijo: ").append(e.getTargetNode().getAttribute("ui.label")).append("\n");
            }
            
            info.append("Total de padres: ").append(padres).append("\n");
            info.append("Total de hijos: ").append(hijos).append("\n");
            info.append("------------------\n\n");
        }
        
        ventana.actualizarInfo(info.toString());
    }

    public SimpleMap<String, String> getNombreCompletos() {
        return nombreCompletos;
    }

    public Graph getGraph() {
        return graph;
    }
} 