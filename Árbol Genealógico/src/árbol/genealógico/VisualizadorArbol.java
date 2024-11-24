package árbol.genealógico;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.TextArea;

public class VisualizadorArbol implements ViewerListener {
    private TablaHash tablaPersonas;
    private SingleGraph graph;
    private TextArea areaResultados;
    private Viewer viewer;
    private ViewerPipe fromViewer;
    private static final String COLOR_PADRE = "rgb(255,165,0)"; // Naranja
    private static final String COLOR_HIJO = "rgb(144,238,144)"; // Verde claro
    private static final String COLOR_PADRES = "rgb(100,149,237)"; // Azul para búsqueda de padres
    private static final String COLOR_HIJO_INVERSO = "rgb(255,255,0)"; // Amarillo para búsqueda de padres
    private static final String COLOR_SELECCION = "rgb(93,199,255)"; // Azul

    public VisualizadorArbol(TablaHash tablaPersonas, TextArea areaResultados) {
        this.tablaPersonas = tablaPersonas;
        this.areaResultados = areaResultados;
        inicializarGrafo();
        crearArbol();
        configurarViewer();
    }

    private void inicializarGrafo() {
        System.setProperty("org.graphstream.ui", "swing");
        graph = new SingleGraph("Árbol Genealógico");
        
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
            "edge {" +
            "   fill-color: black;" +
            "   size: 2px;" +
            "   arrow-size: 8px;" +
            "   arrow-shape: arrow;" +
            "   direction: to;" +
            "}";
        
        graph.setAttribute("ui.stylesheet", styleSheet);
        graph.setAttribute("ui.quality");
        graph.setAttribute("ui.antialias");
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
        
        // Luego crear todas las conexiones (dirigidas del padre al hijo)
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
                                    // Crear conexión dirigida del padre al hijo
                                    graph.addEdge(edgeId, nombrePadre, nombreHijo, true);
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

    private void configurarViewer() {
        viewer = graph.display();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
        
        // Configurar el pipe para escuchar eventos del visor
        fromViewer = viewer.newViewerPipe();
        fromViewer.addViewerListener(this);
        
        // Iniciar un hilo para escuchar eventos
        new Thread(() -> {
            while(true) {
                try {
                    fromViewer.pump();
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }

    @Override
    public void viewClosed(String viewName) {}

    @Override
    public void buttonPushed(String id) {
        // Cuando se hace clic en un nodo, mostrar su información
        mostrarInformacionNodo(id);
        // También resaltamos el nodo seleccionado
        resaltarNodo(id);
    }

    @Override
    public void buttonReleased(String id) {}

    @Override
    public void mouseOver(String id) {}

    @Override
    public void mouseLeft(String id) {}

    public void resaltarNodo(String nombreCompleto) {
        limpiarResaltado(); // Limpiar resaltados anteriores
        
        String nombreCorto = obtenerPrimerNombre(nombreCompleto);
        Node nodo = graph.getNode(nombreCorto);
        
        if (nodo != null) {
            nodo.setAttribute("ui.style", 
                "size: 35px; fill-color: " + COLOR_SELECCION + "; " +  // Cambiado a azul
                "stroke-mode: plain; stroke-color: black; " +
                "stroke-width: 2px;");
        }
    }

    public void limpiarResaltado() {
        TablaHash.Persona[] personas = tablaPersonas.obtenerTodasLasPersonas();
        for (TablaHash.Persona persona : personas) {
            if (persona != null) {
                String nombre = obtenerPrimerNombre(persona.nombreCompleto);
                Node nodo = graph.getNode(nombre);
                if (nodo != null) {
                    // Restaurar el estilo original según la casa
                    if (persona.casa != null) {
                        nodo.setAttribute("ui.class", persona.casa.toLowerCase());
                    } else {
                        nodo.setAttribute("ui.style", 
                            "size: 30px; fill-color: white; " +
                            "stroke-mode: plain; stroke-color: black;");
                    }
                }
            }
        }
    }

    public void resaltarFamilia(String nombrePadreCompleto) {
        limpiarResaltado(); // Limpiar resaltados anteriores
        
        String nombrePadre = obtenerPrimerNombre(nombrePadreCompleto);
        TablaHash.Persona padre = tablaPersonas.buscarPorNombre(nombrePadreCompleto);
        
        if (padre != null) {
            // Resaltar padre en naranja
            Node nodoPadre = graph.getNode(nombrePadre);
            if (nodoPadre != null) {
                nodoPadre.setAttribute("ui.style", 
                    "size: 35px; fill-color: " + COLOR_PADRE + "; " +
                    "stroke-mode: plain; stroke-color: black; " +
                    "stroke-width: 2px;");
            }
            
            // Resaltar hijos en verde
            for (String nombreHijoCompleto : padre.hijos) {
                if (nombreHijoCompleto != null) {
                    String nombreHijo = obtenerPrimerNombre(nombreHijoCompleto);
                    Node nodoHijo = graph.getNode(nombreHijo);
                    if (nodoHijo != null) {
                        nodoHijo.setAttribute("ui.style", 
                            "size: 35px; fill-color: " + COLOR_HIJO + "; " +
                            "stroke-mode: plain; stroke-color: black; " +
                            "stroke-width: 2px;");
                    }
                }
            }
        }
    }

    public void resaltarFamiliaInversa(String nombreHijoCompleto) {
        limpiarResaltado(); // Limpiar resaltados anteriores
        
        String nombreHijo = obtenerPrimerNombre(nombreHijoCompleto);
        TablaHash.Persona hijo = tablaPersonas.buscarPorNombre(nombreHijoCompleto);
        
        if (hijo != null) {
            // Resaltar hijo en naranja
            Node nodoHijo = graph.getNode(nombreHijo);
            if (nodoHijo != null) {
                nodoHijo.setAttribute("ui.style", 
                    "size: 35px; fill-color: " + COLOR_PADRE + "; " +
                    "stroke-mode: plain; stroke-color: black; " +
                    "stroke-width: 2px;");
            }
            
            // Resaltar padres en verde
            if (hijo.padres != null) {
                for (String nombrePadreCompleto : hijo.padres) {
                    if (nombrePadreCompleto != null) {
                        String nombrePadre = obtenerPrimerNombre(nombrePadreCompleto);
                        Node nodoPadre = graph.getNode(nombrePadre);
                        if (nodoPadre != null) {
                            nodoPadre.setAttribute("ui.style", 
                                "size: 35px; fill-color: " + COLOR_HIJO + "; " +
                                "stroke-mode: plain; stroke-color: black; " +
                                "stroke-width: 2px;");
                        }
                    }
                }
            }
        }
    }

    private void mostrarInformacionNodo(String nombreCorto) {
        System.out.println("Buscando información para: " + nombreCorto);  // Debug
        
        // Buscar el nombre completo basado en el nombre corto
        String nombreCompleto = null;
        TablaHash.Persona[] personas = tablaPersonas.obtenerTodasLasPersonas();
        
        for (TablaHash.Persona p : personas) {
            if (p != null && p.nombreCompleto != null) {
                String primerNombre = obtenerPrimerNombre(p.nombreCompleto);
                if (primerNombre.equals(nombreCorto)) {
                    nombreCompleto = p.nombreCompleto;
                    System.out.println("Nombre completo encontrado: " + nombreCompleto);  // Debug
                    break;
                }
            }
        }

        if (nombreCompleto != null) {
            TablaHash.Persona persona = tablaPersonas.buscarPorNombre(nombreCompleto);
            if (persona != null) {
                StringBuilder info = new StringBuilder();
                info.append("Información de ").append(persona.nombreCompleto).append(":\n\n");
                
                // Agregar información básica
                if (persona.numero != null) info.append("Número: ").append(persona.numero).append("\n");
                if (persona.mote != null) info.append("Conocido como: ").append(persona.mote).append("\n");
                if (persona.titulo != null) info.append("Título: ").append(persona.titulo).append("\n");
                if (persona.casa != null) info.append("Casa: ").append(persona.casa).append("\n");
                if (persona.conyuge != null) info.append("Cónyuge: ").append(persona.conyuge).append("\n");
                if (persona.ojos != null) info.append("Color de ojos: ").append(persona.ojos).append("\n");
                if (persona.cabello != null) info.append("Color de cabello: ").append(persona.cabello).append("\n");
                
                // Mostrar padres
                if (persona.padres != null && persona.padres.length > 0) {
                    info.append("\nPadres:\n");
                    for (String padre : persona.padres) {
                        if (padre != null) {
                            info.append("- ").append(padre).append("\n");
                        }
                    }
                }
                
                // Mostrar hijos
                if (persona.hijos != null && persona.hijos.length > 0) {
                    info.append("\nHijos:\n");
                    for (String hijo : persona.hijos) {
                        if (hijo != null) {
                            info.append("- ").append(hijo).append("\n");
                        }
                    }
                }
                
                if (persona.notas != null) info.append("\nNotas: ").append(persona.notas).append("\n");
                if (persona.destino != null) info.append("Destino: ").append(persona.destino).append("\n");
                
                System.out.println("Actualizando área de texto");  // Debug
                areaResultados.setText(info.toString());
            }
        }
    }
} 