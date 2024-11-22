package árbol.genealógico;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.view.Viewer;
import árbol.genealógico.TablaHash.Persona;

public class VisualizadorArbol {
    private TablaHash tablaPersonas;
    private SingleGraph graph;
    private String parent;
    private JsonObject verticesMap;

    public VisualizadorArbol(TablaHash tablaPersonas) {
        this.tablaPersonas = tablaPersonas;
        this.verticesMap = new JsonObject();
        inicializarGrafo();
        crearArbol();
        mostrarGrafo();
    }

    private void inicializarGrafo() {
        // Configurar el estilo de visualización
        System.setProperty("org.graphstream.ui", "swing");
        
        // Crear el grafo
        graph = new SingleGraph("Árbol Genealógico");
        
        // Establecer estilos CSS para el grafo
        String styleSheet = 
            "node {" +
            "   size: 30px;" +
            "   shape: circle;" +
            "   fill-color: white;" +
            "   stroke-mode: plain;" +
            "   stroke-color: black;" +
            "   text-size: 12px;" +
            "   text-style: bold;" +
            "   text-alignment: center;" +
            "   padding: 50px;" +
            "   size-mode: normal;" +
            "}" +
            "node.stark {" +
            "   fill-color: rgb(160,160,160);" +
            "}" +
            "node.targaryen {" +
            "   fill-color: rgb(255,0,0);" +
            "}" +
            "node.lannister {" +
            "   fill-color: rgb(255,215,0);" +
            "}" +
            "edge {" +
            "   fill-color: black;" +
            "   size: 2px;" +
            "   arrow-size: 8px;" +
            "   size-mode: normal;" +
            "   shape: line;" +
            "   arrow-shape: arrow;" +
            "}" +
            "graph {" +
            "   padding: 100px;" +
            "   node-spacing: 300;" +
            "}";
        
        graph.setAttribute("ui.stylesheet", styleSheet);
        graph.setAttribute("ui.quality", 4);
        graph.setAttribute("ui.antialias", true);
        
        // Configurar el espaciado del layout con valores extremadamente grandes
        graph.setAttribute("layout.stabilization-limit", 0.9);
        graph.setAttribute("layout.weight", 0.2);
        graph.setAttribute("layout.force", 0.8);
        graph.setAttribute("layout.quality", 4);
        
        // Configurar distancias mínimas aún más grandes
        graph.setAttribute("layout.node-spacing", 5000);
        graph.setAttribute("layout.min-edge-length", 5000);
        graph.setAttribute("layout.min-node-distance", 5000);
        graph.setAttribute("layout.spring-length", 5000);
        graph.setAttribute("layout.repulsion-strength", 10000);
        
        // Configurar visualización constante
        graph.setAttribute("ui.size-mode", "normal");
        graph.setAttribute("ui.edge-size-mode", "normal");
    }

    private void crearArbol() {
        TablaHash.Persona[] personas = tablaPersonas.obtenerTodasLasPersonas();
        
        // Primero crear todos los nodos
        for (TablaHash.Persona persona : personas) {
            if (persona != null) {
                try {
                    String nombreBase = obtenerPrimerNombre(persona.nombreCompleto);
                    Node node = graph.addNode(nombreBase);
                    node.setAttribute("ui.label", nombreBase);
                    
                    // Aplicar estilo según la casa
                    if (persona.casa != null) {
                        node.setAttribute("ui.class", persona.casa.toLowerCase());
                    }
                    
                    // Crear nodos para los hijos que no existan
                    for (String nombreHijoCompleto : persona.hijos) {
                        if (nombreHijoCompleto != null) {
                            String nombreHijoBase = obtenerPrimerNombre(nombreHijoCompleto);
                            if (graph.getNode(nombreHijoBase) == null) {
                                Node nodoHijo = graph.addNode(nombreHijoBase);
                                nodoHijo.setAttribute("ui.label", nombreHijoBase);
                                // Heredar la casa del padre si está disponible
                                if (persona.casa != null) {
                                    nodoHijo.setAttribute("ui.class", persona.casa.toLowerCase());
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    continue;
                }
            }
        }
        
        // Luego crear todas las conexiones
        for (TablaHash.Persona persona : personas) {
            if (persona != null) {
                String nombrePadre = obtenerPrimerNombre(persona.nombreCompleto);
                
                // Conectar con los hijos
                for (String nombreHijoCompleto : persona.hijos) {
                    if (nombreHijoCompleto != null) {
                        String nombreHijo = obtenerPrimerNombre(nombreHijoCompleto);
                        Node nodoPadre = graph.getNode(nombrePadre);
                        Node nodoHijo = graph.getNode(nombreHijo);
                        
                        if (nodoPadre != null && nodoHijo != null) {
                            String edgeId = nombrePadre + "-" + nombreHijo;
                            if (graph.getEdge(edgeId) == null) {
                                try {
                                    graph.addEdge(edgeId, nodoPadre, nodoHijo, true);
                                } catch (Exception e) {
                                    continue;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private String obtenerPrimerNombre(String nombreCompleto) {
        if (nombreCompleto == null) return null;
        String[] partes = nombreCompleto.split(" ");
        return partes[0];
    }

    public void mostrarGrafo() {
        Viewer viewer = graph.display();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
        
        // Configurar el layout después de mostrar el grafo
        viewer.getDefaultView().getCamera().setAutoFitView(true);
        viewer.getDefaultView().getCamera().setGraphViewport(-5000, -5000, 5000, 5000);
    }

    public void resaltarNodo(String nombreCompleto) {
        // Obtener el primer nombre para la búsqueda
        String nombreBusqueda = obtenerPrimerNombre(nombreCompleto);
        
        // Restaurar el estilo del nodo anterior si existe
        if (parent != null) {
            Node nodoAnterior = graph.getNode(parent);
            if (nodoAnterior != null) {
                nodoAnterior.setAttribute("ui.style", nodoAnterior.getAttribute("ui.style", String.class));
            }
            parent = null;
        }
        
        // Buscar y resaltar el nuevo nodo
        Node nodo = graph.getNode(nombreBusqueda);
        if (nodo != null) {
            // Guardar el estilo original
            String estiloOriginal = nodo.getAttribute("ui.style", String.class);
            
            // Aplicar nuevo estilo para resaltar
            String nuevoEstilo = "size: 35px; "
                + "fill-color: rgb(144,238,144);"; // Verde claro
            
            nodo.setAttribute("ui.style", nuevoEstilo);
            parent = nombreBusqueda;
        }
    }
} 