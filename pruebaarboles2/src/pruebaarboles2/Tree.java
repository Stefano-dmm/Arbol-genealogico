package pruebaarboles2;

import org.json.JSONArray;
import org.json.JSONObject;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.view.Viewer;
import pruebaarboles2.estructuras.SimpleMap;
import pruebaarboles2.estructuras.SimpleSet;

/**
 * Clase que representa un árbol genealógico utilizando una estructura de grafo.
 * Permite la visualización, búsqueda y manipulación de relaciones familiares.
 * 
 * Esta clase utiliza la biblioteca GraphStream para la representación visual
 * y manejo del grafo, permitiendo:
 * - Construcción del árbol a partir de datos JSON
 * - Visualización interactiva del árbol genealógico
 * - Búsqueda de miembros por nombre
 * - Visualización de relaciones padre-hijo
 * - Exploración de líneas ancestrales completas
 * - Identificación de conexiones familiares múltiples
 *
 * @author [Tu Nombre]
 * @version 1.0
 */
public class Tree {
    private Graph graph;
    private SimpleMap<String, String> nombreCompletos;
    private static final String STYLESHEET = 
        "node {" +
        "   size: 30px;" +
        "   shape: circle;" +
        "   fill-color: white;" +
        "   stroke-mode: plain;" +
        "   stroke-color: black;" +
        "   text-alignment: center;" +
        "   text-size: 12;" +
        "   text-style: bold;" +
        "}" +
        "node.found {" +
        "   fill-color: #90EE90;" +
        "}" +
        "node.base {" +
        "   fill-color: #FFA500;" +
        "}" +
        "edge {" +
        "   shape: line;" +
        "   fill-color: black;" +
        "   arrow-size: 5px, 4px;" +
        "}";

    private static Viewer currentViewer = null; // Variable estática para mantener referencia al viewer actual

    public Tree() {
        System.setProperty("org.graphstream.ui", "swing");
        this.graph = new SingleGraph("Árbol Genealógico Targaryen");
        this.nombreCompletos = new SimpleMap<>();
        graph.setAttribute("ui.stylesheet", STYLESHEET);
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
        // Obtener el nombre de la primera (y única) clave del JSON
        String nombreCasa = json.keys().next();
        JSONArray familia = json.getJSONArray(nombreCasa);
        SimpleSet<String> nodosConectados = new SimpleSet<>();
        SimpleSet<String> nodosConHijos = new SimpleSet<>();
        SimpleMap<String, Integer> contadorNombres = new SimpleMap<>();
        SimpleMap<String, String> nodoPadreMap = new SimpleMap<>();
        
        // Actualizar el nombre del grafo
        graph.setAttribute("ui.label", "Árbol Genealógico " + nombreCasa);
        
        // Determinar el tipo de casa
        boolean esTargaryen = nombreCasa.equals("House Targaryen");
        boolean esBaratheon = nombreCasa.equals("House Baratheon");
        
        // Primera pasada: crear nodos
        if (esTargaryen) {
            crearNodosTargaryen(familia, contadorNombres, nodoPadreMap);
        } else if (esBaratheon) {
            crearNodosBaratheon(familia, contadorNombres, nodoPadreMap);
        }
        
        // Segunda pasada: establecer conexiones
        if (esTargaryen) {
            establecerConexionesTargaryen(familia, nodosConectados, nodosConHijos, contadorNombres, nodoPadreMap);
        } else if (esBaratheon) {
            establecerConexionesBaratheon(familia, nodosConectados, nodosConHijos, contadorNombres, nodoPadreMap);
        }
        
        // Conectar nodos aislados solo para Targaryen
        if (esTargaryen) {
            conectarNodosAislados(nodosConectados, nodosConHijos, nodoPadreMap);
        }
        
        imprimirEstadisticas();
    }

    private void crearNodosTargaryen(JSONArray familia, SimpleMap<String, Integer> contadorNombres, 
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
            
            procesarPadreTargaryen(infoPersona, nodoId, nodoPadreMap);
        }
    }

    private void crearNodosBaratheon(JSONArray familia, SimpleMap<String, Integer> contadorNombres, 
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
            
            // Procesar información específica de Baratheon
            for (int j = 0; j < infoPersona.length(); j++) {
                JSONObject atributo = infoPersona.getJSONObject(j);
                if (atributo.has("Born to") && !atributo.getString("Born to").equals("[Unknown]")) {
                    String padre = atributo.getString("Born to");
                    if (!padre.contains("Baratheon")) {
                        padre += " Baratheon";
                    }
                    nodoPadreMap.put(nodoId, padre);
                }
            }
        }
    }

    private void procesarPadreTargaryen(JSONArray infoPersona, String nodoId, SimpleMap<String, String> nodoPadreMap) {
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

    private void establecerConexionesTargaryen(JSONArray familia, SimpleSet<String> nodosConectados,
                                                SimpleSet<String> nodosConHijos, SimpleMap<String, Integer> contadorNombres,
                                                SimpleMap<String, String> nodoPadreMap) {
        for (int i = 0; i < familia.length(); i++) {
            JSONObject persona = familia.getJSONObject(i);
            String nombreBase = persona.keys().next();
            JSONArray infoPersona = persona.getJSONArray(nombreBase);
            
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
    }

    private void establecerConexionesBaratheon(JSONArray familia, SimpleSet<String> nodosConectados,
                                               SimpleSet<String> nodosConHijos, SimpleMap<String, Integer> contadorNombres,
                                               SimpleMap<String, String> nodoPadreMap) {
        for (int i = 0; i < familia.length(); i++) {
            JSONObject persona = familia.getJSONObject(i);
            String nombreBase = persona.keys().next();
            JSONArray infoPersona = persona.getJSONArray(nombreBase);
            
            String padreId = obtenerIdPadre(nombreBase);
            if (padreId != null) {
                for (int j = 0; j < infoPersona.length(); j++) {
                    JSONObject atributo = infoPersona.getJSONObject(j);
                    if (atributo.has("Father to")) {
                        JSONArray hijos = atributo.getJSONArray("Father to");
                        for (int k = 0; k < hijos.length(); k++) {
                            String nombreHijo = hijos.getString(k);
                            if (!nombreHijo.contains("Baratheon")) {
                                nombreHijo += " Baratheon";
                            }
                            crearConexionPadreHijoBaratheon(padreId, nombreHijo, nodosConectados, nodosConHijos);
                        }
                    }
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

    private void crearConexionPadreHijoBaratheon(String padreId, String nombreHijo, 
                                                SimpleSet<String> nodosConectados,
                                                SimpleSet<String> nodosConHijos) {
        for (Node nodo : graph.nodes().toArray(Node[]::new)) {
            String label = (String) nodo.getAttribute("ui.label");
            if (label.startsWith(nombreHijo + " (")) {
                String hijoId = nodo.getId();
                if (!existeConexion(padreId, hijoId)) {
                    agregarConexion(padreId, hijoId, nodosConectados, nodosConHijos);
                }
                break;
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
        // Si hay un viewer existente, cerrarlo
        if (currentViewer != null) {
            currentViewer.close();
        }
        
        // Crear nuevo viewer
        currentViewer = graph.display();
        currentViewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
        
        InfoVentana infoVentana = new InfoVentana(this);
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

    public boolean buscarYMostrarNodo(String nombre, StringBuilder resultado) {
        // Limpiar resaltados anteriores
        graph.nodes().forEach(n -> n.removeAttribute("ui.class"));
        
        boolean encontrado = false;
        for (Node nodo : graph.nodes().toArray(Node[]::new)) {
            String label = (String) nodo.getAttribute("ui.label");
            String nombreBase = obtenerNombreBase(nodo.getId());
            
            if (coincideConBusqueda(label, nombreBase, nombre)) {
                encontrado = true;
                nodo.setAttribute("ui.class", "found");  // Resaltar nodo
                resultado.append("Nombre completo: ").append(label).append("\n");
                resultado.append("Nombre: ").append(nombreBase).append("\n");
                resultado.append("ID: ").append(nodo.getId()).append("\n");
                resultado.append("------------------\n");
            }
        }
        return encontrado;
    }

    public boolean buscarHijos(String nombre, StringBuilder resultado) {
        // Limpiar resaltados anteriores
        graph.nodes().forEach(n -> n.removeAttribute("ui.class"));
        
        boolean encontrado = false;
        for (Node nodo : graph.nodes().toArray(Node[]::new)) {
            String label = (String) nodo.getAttribute("ui.label");
            String nombreBase = obtenerNombreBase(nodo.getId());
            
            if (coincideConBusqueda(label, nombreBase, nombre)) {
                encontrado = true;
                nodo.setAttribute("ui.class", "base");  // Nodo base en naranja
                resultado.append("Hijos de ").append(label).append(":\n");
                
                boolean tieneHijos = false;
                for (Edge edge : nodo.leavingEdges().toArray(Edge[]::new)) {
                    Node hijo = edge.getTargetNode();
                    hijo.setAttribute("ui.class", "found");  // Hijos en verde
                    String hijoLabel = (String) hijo.getAttribute("ui.label");
                    String hijoNombre = obtenerNombreBase(hijo.getId());
                    resultado.append("- ").append(hijoLabel).append(" (").append(hijoNombre).append(")\n");
                    tieneHijos = true;
                }
                
                if (!tieneHijos) {
                    resultado.append("No tiene hijos registrados.\n");
                }
                resultado.append("------------------\n");
            }
        }
        return encontrado;
    }

    public boolean buscarPadres(String nombre, StringBuilder resultado) {
        // Limpiar resaltados anteriores
        graph.nodes().forEach(n -> n.removeAttribute("ui.class"));
        
        boolean encontrado = false;
        for (Node nodo : graph.nodes().toArray(Node[]::new)) {
            String label = (String) nodo.getAttribute("ui.label");
            String nombreBase = obtenerNombreBase(nodo.getId());
            
            if (coincideConBusqueda(label, nombreBase, nombre)) {
                encontrado = true;
                nodo.setAttribute("ui.class", "base");  // Nodo base en naranja
                resultado.append("Padres de ").append(label).append(":\n");
                
                boolean tienePadres = false;
                for (Edge edge : nodo.enteringEdges().toArray(Edge[]::new)) {
                    Node padre = edge.getSourceNode();
                    padre.setAttribute("ui.class", "found");  // Padres en verde
                    String padreLabel = (String) padre.getAttribute("ui.label");
                    String padreNombre = obtenerNombreBase(padre.getId());
                    resultado.append("- ").append(padreLabel).append(" (").append(padreNombre).append(")\n");
                    tienePadres = true;
                }
                
                if (!tienePadres) {
                    resultado.append("No tiene padres registrados.\n");
                }
                resultado.append("------------------\n");
            }
        }
        return encontrado;
    }

    // Métodos auxiliares nuevos
    private String obtenerNombreBase(String id) {
        // El ID tiene el formato: NombreBase_NumeroRomano_Contador
        return id.split("_")[0].replaceAll("_", " ");
    }

    private boolean coincideConBusqueda(String label, String nombreBase, String busqueda) {
        busqueda = busqueda.toLowerCase();
        return label.toLowerCase().contains(busqueda) || 
               nombreBase.toLowerCase().contains(busqueda);
    }
} 