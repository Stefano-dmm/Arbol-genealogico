package árbol.genealógico;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.view.Viewer;

public class VisualizadorArbol {
    private final TablaHash tablaPersonas;
    private final Graph graph;
    
    public VisualizadorArbol(TablaHash tablaPersonas) {
        this.tablaPersonas = tablaPersonas;
        
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
        
        crearGrafo();
        mostrarGrafo();
    }
    
    private void crearGrafo() {
        TablaHash.Persona[] personas = tablaPersonas.obtenerTodasLasPersonas();
        
        // Primero crear todos los nodos
        for (TablaHash.Persona persona : personas) {
            if (persona != null) {
                try {
                    Node node = graph.addNode(persona.nombreCompleto);
                    node.setAttribute("ui.label", persona.nombreCompleto);
                    
                    // Aplicar estilo según la casa
                    if (persona.casa != null) {
                        node.setAttribute("ui.class", persona.casa.toLowerCase());
                    }
                } catch (Exception e) {
                    // Si el nodo ya existe, continuamos
                    continue;
                }
            }
        }
        
        // Luego crear las conexiones y nodos para los hijos
        for (TablaHash.Persona persona : personas) {
            if (persona != null) {
                // Crear conexiones con todos los padres (hasta 2)
                for (String nombrePadre : persona.padres) {
                    if (nombrePadre != null) {
                        // Verificar que ambos nodos existen antes de crear la conexión
                        Node nodoPadre = graph.getNode(nombrePadre);
                        Node nodoHijo = graph.getNode(persona.nombreCompleto);
                        
                        if (nodoPadre != null && nodoHijo != null) {
                            String edgeId = nombrePadre + "-" + persona.nombreCompleto;
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
                
                // Crear nodos para los hijos si no existen
                for (String nombreHijo : persona.hijos) {
                    if (nombreHijo != null && graph.getNode(nombreHijo) == null) {
                        try {
                            Node nodoHijo = graph.addNode(nombreHijo);
                            nodoHijo.setAttribute("ui.label", nombreHijo);
                            String edgeId = persona.nombreCompleto + "-" + nombreHijo;
                            graph.addEdge(edgeId, persona.nombreCompleto, nombreHijo, true);
                        } catch (Exception e) {
                            // Si el nodo o la conexión ya existe o hay algún error, continuamos
                            continue;
                        }
                    }
                }
            }
        }
    }
    
    private void mostrarGrafo() {
        Viewer viewer = graph.display();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
        
        // Configurar el layout después de mostrar el grafo
        viewer.getDefaultView().getCamera().setAutoFitView(true);
        viewer.getDefaultView().getCamera().setGraphViewport(-5000, -5000, 5000, 5000);
    }
} 