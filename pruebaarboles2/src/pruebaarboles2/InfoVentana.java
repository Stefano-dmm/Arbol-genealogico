package pruebaarboles2;

import javax.swing.*;
import java.awt.*;
import org.graphstream.graph.*;
import java.util.Map;
import java.util.Iterator;

public class InfoVentana extends JFrame {
    private JTextArea areaTexto;
    private Map<String, String> nombreCompletos;
    private Graph graph;
    private JTextField campoBusqueda;
    private JComboBox<String> tipoBusqueda;
    
    public InfoVentana(Graph graph, Map<String, String> nombreCompletos) {
        this.nombreCompletos = nombreCompletos;
        this.graph = graph;
        
        setTitle("Información de Nodos - Casa Targaryen");
        setSize(500, 600);
        setLocationRelativeTo(null);
        
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        
        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new BorderLayout());
        JPanel panelSuperior = new JPanel(new BorderLayout());
        
        // Combo box para tipo de búsqueda
        tipoBusqueda = new JComboBox<>(new String[]{
            "Búsqueda general",
            "Buscar hijos de",
            "Buscar padres de"
        });
        
        campoBusqueda = new JTextField();
        JButton botonBuscar = new JButton("Buscar");
        
        panelSuperior.add(new JLabel("Tipo: "), BorderLayout.WEST);
        panelSuperior.add(tipoBusqueda, BorderLayout.CENTER);
        
        panelBusqueda.add(panelSuperior, BorderLayout.NORTH);
        panelBusqueda.add(new JLabel("Buscar: "), BorderLayout.WEST);
        panelBusqueda.add(campoBusqueda, BorderLayout.CENTER);
        panelBusqueda.add(botonBuscar, BorderLayout.EAST);
        
        // Área de texto
        areaTexto = new JTextArea();
        areaTexto.setEditable(false);
        areaTexto.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(areaTexto);
        
        panelPrincipal.add(panelBusqueda, BorderLayout.NORTH);
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);
        
        mostrarInformacionCompleta(graph);
        
        botonBuscar.addActionListener(e -> realizarBusqueda());
        campoBusqueda.addActionListener(e -> realizarBusqueda());
        
        add(panelPrincipal);
        setVisible(true);
    }
    
    public void mostrarInformacionCompleta(Graph graph) {
        StringBuilder info = new StringBuilder();
        info.append("Casa Targaryen - Información Completa\n");
        info.append("===================================\n\n");
        
        for (Node nodo : graph) {
            String id = nodo.getId();
            String nombre = (String) nodo.getAttribute("ui.label");
            
            info.append("Nombre: ").append(nombre).append("\n");
            
            // Buscar hijos
            info.append("Hijos: ");
            boolean tieneHijos = false;
            Iterator<Edge> edges = nodo.leavingEdges().iterator();
            while (edges.hasNext()) {
                Edge edge = edges.next();
                Node hijo = edge.getTargetNode();
                info.append(hijo.getAttribute("ui.label")).append(", ");
                tieneHijos = true;
            }
            if (!tieneHijos) {
                info.append("Ninguno");
            } else {
                info.setLength(info.length() - 2); // Eliminar última coma
            }
            info.append("\n");
            
            // Buscar padres
            info.append("Padre: ");
            boolean tienePadre = false;
            Iterator<Edge> edgesEntrantes = nodo.enteringEdges().iterator();
            while (edgesEntrantes.hasNext()) {
                Edge edge = edgesEntrantes.next();
                Node padre = edge.getSourceNode();
                info.append(padre.getAttribute("ui.label"));
                tienePadre = true;
                break; // Solo puede tener un padre
            }
            if (!tienePadre) {
                info.append("Desconocido");
            }
            
            info.append("\n\n");
        }
        
        areaTexto.setText(info.toString());
    }
    
    public void actualizarInformacion(Node nodo, Graph graph) {
        String nombre = (String) nodo.getAttribute("ui.label");
        
        StringBuilder info = new StringBuilder();
        info.append("Información del Nodo\n");
        info.append("===================\n\n");
        info.append("Nombre: ").append(nombre).append("\n");
        
        // Buscar hijos
        info.append("Hijos: ");
        boolean tieneHijos = false;
        Iterator<Edge> edges = nodo.leavingEdges().iterator();
        while (edges.hasNext()) {
            Edge edge = edges.next();
            Node hijo = edge.getTargetNode();
            info.append(hijo.getAttribute("ui.label")).append(", ");
            tieneHijos = true;
        }
        if (!tieneHijos) {
            info.append("Ninguno");
        } else {
            info.setLength(info.length() - 2); // Eliminar última coma
        }
        info.append("\n");
        
        // Buscar padre
        info.append("Padre: ");
        boolean tienePadre = false;
        Iterator<Edge> edgesEntrantes = nodo.enteringEdges().iterator();
        while (edgesEntrantes.hasNext()) {
            Edge edge = edgesEntrantes.next();
            Node padre = edge.getSourceNode();
            info.append(padre.getAttribute("ui.label"));
            tienePadre = true;
            break; // Solo puede tener un padre
        }
        if (!tienePadre) {
            info.append("Desconocido");
        }
        
        areaTexto.setText(info.toString());
    }
    
    private void realizarBusqueda() {
        String textoBusqueda = campoBusqueda.getText().trim().toLowerCase();
        String tipoBusquedaSeleccionado = (String) tipoBusqueda.getSelectedItem();
        
        StringBuilder resultados = new StringBuilder();
        resultados.append("Resultados de la búsqueda\n");
        resultados.append("========================\n\n");
        
        boolean encontrado = false;
        
        for (Node nodo : graph) {
            String nombreNodo = ((String) nodo.getAttribute("ui.label")).toLowerCase();
            
            switch (tipoBusquedaSeleccionado) {
                case "Búsqueda general":
                    if (nombreNodo.contains(textoBusqueda)) {
                        mostrarInfoNodo(nodo, resultados);
                        encontrado = true;
                    }
                    break;
                    
                case "Buscar hijos de":
                    if (nombreNodo.contains(textoBusqueda)) {
                        mostrarHijos(nodo, resultados);
                        encontrado = true;
                    }
                    break;
                    
                case "Buscar padres de":
                    if (nombreNodo.contains(textoBusqueda)) {
                        mostrarPadre(nodo, resultados);
                        encontrado = true;
                    }
                    break;
            }
        }
        
        if (!encontrado) {
            resultados.append("No se encontraron resultados para la búsqueda.");
        }
        
        areaTexto.setText(resultados.toString());
    }
    
    private void mostrarInfoNodo(Node nodo, StringBuilder sb) {
        String nombre = (String) nodo.getAttribute("ui.label");
        sb.append("Nombre: ").append(nombre).append("\n");
        mostrarHijos(nodo, sb);
        mostrarPadre(nodo, sb);
        sb.append("\n");
    }
    
    private void mostrarHijos(Node nodo, StringBuilder sb) {
        String nombre = (String) nodo.getAttribute("ui.label");
        sb.append("Hijos de ").append(nombre).append(": ");
        boolean tieneHijos = false;
        Iterator<Edge> edges = nodo.leavingEdges().iterator();
        while (edges.hasNext()) {
            Edge edge = edges.next();
            Node hijo = edge.getTargetNode();
            sb.append(hijo.getAttribute("ui.label")).append(", ");
            tieneHijos = true;
        }
        if (!tieneHijos) {
            sb.append("Ninguno");
        } else {
            sb.setLength(sb.length() - 2); // Eliminar última coma
        }
        sb.append("\n");
    }
    
    private void mostrarPadre(Node nodo, StringBuilder sb) {
        String nombre = (String) nodo.getAttribute("ui.label");
        sb.append("Padre de ").append(nombre).append(": ");
        boolean tienePadre = false;
        Iterator<Edge> edges = nodo.enteringEdges().iterator();
        while (edges.hasNext()) {
            Edge edge = edges.next();
            Node padre = edge.getSourceNode();
            sb.append(padre.getAttribute("ui.label"));
            tienePadre = true;
            break;
        }
        if (!tienePadre) {
            sb.append("Desconocido");
        }
        sb.append("\n");
    }
} 