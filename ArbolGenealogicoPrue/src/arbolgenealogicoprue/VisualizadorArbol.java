/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package arbolgenealogicoprue;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.view.Viewer;

public class VisualizadorArbol {
    private Tree tree;
    private SingleGraph graph;

    public VisualizadorArbol(Tree tree) {
        this.tree = tree;
        inicializarGrafo();
        crearArbol();
    }

    private void inicializarGrafo() {
        System.setProperty("org.graphstream.ui", "swing");

        graph = new SingleGraph("Árbol Genealógico");

        // Estilo del grafo
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
                "node.targaryen {" +
                "   fill-color: rgb(255,0,0);" +
                "}" +
                "edge {" +
                "   fill-color: black;" +
                "   size: 2px;" +
                "}";

        graph.setAttribute("ui.stylesheet", styleSheet);
        graph.setAttribute("ui.quality", 4);
        graph.setAttribute("ui.antialias", true);
    }

    private void crearArbol() {
        Tree.Nodo raiz = tree.getRaiz();
        if (raiz != null) {
            crearNodo(raiz);
            Tree.Nodo hijo = raiz.getPrimerHijo();
            while (hijo != null) {
                crearConexiones(raiz, hijo);
                hijo = hijo.getSiguienteHermano();
            }
        }
    }

    private void crearNodo(Tree.Nodo nodo) {
        if (graph.getNode(nodo.getNombreCompleto()) == null) {
            Node nuevoNodo = graph.addNode(nodo.getNombreCompleto());
            nuevoNodo.setAttribute("ui.label", nodo.getNombreCompleto());
            nuevoNodo.setAttribute("ui.class", nodo.getCasa().toLowerCase());
        }
    }

    private void crearConexiones(Tree.Nodo padre, Tree.Nodo hijo) {
    // Crear el nodo padre si no existe
    if (graph.getNode(padre.getNombreCompleto()) == null) {
        crearNodo(padre); // Asegurarnos de que el nodo padre esté creado
    }

    // Crear el nodo hijo si no existe
    if (graph.getNode(hijo.getNombreCompleto()) == null) {
        crearNodo(hijo); // Asegurarnos de que el nodo hijo esté creado
    }

    // Verificar si ya existe una conexión entre el padre y el hijo
    if (graph.getEdge(padre.getNombreCompleto() + "-" + hijo.getNombreCompleto()) == null) {
        graph.addEdge(padre.getNombreCompleto() + "-" + hijo.getNombreCompleto(), padre.getNombreCompleto(), hijo.getNombreCompleto());
    }

    // Recursivamente, si el hijo tiene más hermanos, los conectamos
    Tree.Nodo hermano = hijo.getSiguienteHermano();
    while (hermano != null) {
        crearConexiones(hijo, hermano);  // Conectar hermanos
        hermano = hermano.getSiguienteHermano();
    }
}


    public void mostrarGrafo() {
        try {
            Viewer viewer = graph.display();
            viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY); // Evitar que se cierre el programa al cerrar la ventana
        } catch (Exception e) {
            System.err.println("Error al intentar mostrar el grafo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

