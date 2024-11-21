/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package árbol.genealógico;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.View;
import javax.swing.JFrame;
import java.awt.BorderLayout;

public class Grafo {
    private Graph graph;
    private Tree arbol;

    public Grafo(Tree arbol) {
        System.setProperty("org.graphstream.ui", "swing");
        this.arbol = arbol;
        inicializar();
    }

    private void inicializar() {
        graph = new SingleGraph("Árbol Genealógico");
        
        String css = """
            node {
                size: 30px;
                fill-color: #6495ED;
                text-size: 14px;
                text-color: #000000;
                text-style: bold;
                text-alignment: center;
                text-padding: 3px, 2px;
                stroke-mode: plain;
                stroke-color: #2C4C8C;
                stroke-width: 1px;
                shape: circle;
            }
            edge {
                shape: line;
                fill-color: #2C4C8C;
                arrow-size: 8px, 6px;
            }
        """;
        
        graph.setAttribute("ui.stylesheet", css);
        graph.setAttribute("ui.quality");
        graph.setAttribute("ui.antialias");
        
        construirGrafo(arbol.getRaiz(), null);
    }

    private void construirGrafo(Nodo nodo, String padreId) {
        if (nodo == null) return;
        
        String nodoId = String.valueOf(nodo.getNombre().hashCode());
        
        try {
            Node graphNode = graph.addNode(nodoId);
            graphNode.setAttribute("ui.label", nodo.getNombre());
            
            if (padreId != null) {
                graph.addEdge(padreId + "-" + nodoId, padreId, nodoId, true);
            }
            
            Lista hijos = nodo.getHijos();
            if (hijos != null) {
                Nodo hijoActual = hijos.getInicio();
                while (hijoActual != null) {
                    construirGrafo(hijoActual, nodoId);
                    hijoActual = hijoActual.getSiguiente();
                }
            }
        } catch (Exception e) {
            System.err.println("Error al construir nodo: " + nodo.getNombre());
            e.printStackTrace();
        }
    }

    public void mostrarGrafo() {
        try {
            // Crear frame
            JFrame frame = new JFrame("Árbol Genealógico de los Baratheon");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            
            // Obtener el viewer del grafo
            Viewer viewer = graph.display();
            viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
            
            // Configurar la vista
            View view = viewer.getDefaultView();
            
            // Agregar vista al frame
            if (view instanceof javax.swing.JPanel) {
                frame.add((javax.swing.JPanel) view, BorderLayout.CENTER);
                
                // Mostrar frame
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
            
        } catch (Exception e) {
            System.err.println("Error al mostrar el grafo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

