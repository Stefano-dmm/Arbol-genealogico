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
            "}";
        
        graph.setAttribute("ui.stylesheet", styleSheet);
        
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
        
        // Luego crear las conexiones
        for (TablaHash.Persona persona : personas) {
            if (persona != null) {
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
                                    // Si la conexión ya existe o hay algún error, continuamos
                                    continue;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void mostrarGrafo() {
        Viewer viewer = graph.display();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
    }
} 