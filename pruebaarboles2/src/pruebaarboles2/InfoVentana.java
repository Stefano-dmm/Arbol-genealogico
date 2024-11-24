package pruebaarboles2;

import javax.swing.*;
import java.awt.*;

public class InfoVentana extends JFrame {
    private JTextArea areaTexto;
    private JTextField campoBusqueda;
    private TreeGraph treeGraph;
    
    public InfoVentana(TreeGraph treeGraph) {
        this.treeGraph = treeGraph;
        
        // Configuración básica de la ventana
        setTitle("Información del Árbol Genealógico");
        setSize(400, 600);
        setLocationRelativeTo(null);
        
        // Panel superior para búsqueda
        JPanel panelBusqueda = new JPanel(new BorderLayout());
        campoBusqueda = new JTextField();
        JButton btnBuscar = new JButton("Buscar por Nombre");
        btnBuscar.addActionListener(e -> buscarNodo());
        
        // Botones de búsqueda específica
        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton btnBuscarHijos = new JButton("Buscar Hijos");
        JButton btnBuscarPadres = new JButton("Buscar Padres");
        
        btnBuscarHijos.addActionListener(e -> buscarHijos());
        btnBuscarPadres.addActionListener(e -> buscarPadres());
        
        panelBotones.add(btnBuscarHijos);
        panelBotones.add(btnBuscarPadres);
        
        panelBusqueda.add(new JLabel("Nombre: "), BorderLayout.WEST);
        panelBusqueda.add(campoBusqueda, BorderLayout.CENTER);
        panelBusqueda.add(btnBuscar, BorderLayout.EAST);
        panelBusqueda.add(panelBotones, BorderLayout.SOUTH);
        
        // Área de texto con scroll
        areaTexto = new JTextArea();
        areaTexto.setEditable(false);
        areaTexto.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(areaTexto);
        
        // Botón para cerrar
        JButton cerrarBtn = new JButton("Cerrar");
        cerrarBtn.addActionListener(e -> dispose());
        JPanel panelBoton = new JPanel();
        panelBoton.add(cerrarBtn);
        
        // Organizar componentes
        setLayout(new BorderLayout());
        add(panelBusqueda, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelBoton, BorderLayout.SOUTH);
    }
    
    private void buscarNodo() {
        String nombreBuscado = campoBusqueda.getText().trim();
        if (nombreBuscado.isEmpty()) {
            mostrarError("Por favor ingrese un nombre para buscar");
            return;
        }
        
        StringBuilder resultado = new StringBuilder();
        resultado.append("Resultados de la búsqueda para: ").append(nombreBuscado).append("\n");
        resultado.append("=====================================\n\n");
        
        boolean encontrado = treeGraph.buscarYMostrarNodo(nombreBuscado, resultado);
        
        if (!encontrado) {
            resultado.append("No se encontraron miembros con ese nombre.");
        }
        
        actualizarInfo(resultado.toString());
    }
    
    private void buscarHijos() {
        String nombreBuscado = campoBusqueda.getText().trim();
        if (nombreBuscado.isEmpty()) {
            mostrarError("Por favor ingrese un nombre para buscar sus hijos");
            return;
        }
        
        StringBuilder resultado = new StringBuilder();
        resultado.append("Hijos de: ").append(nombreBuscado).append("\n");
        resultado.append("=====================================\n\n");
        
        boolean encontrado = treeGraph.buscarHijos(nombreBuscado, resultado);
        
        if (!encontrado) {
            resultado.append("No se encontró el miembro o no tiene hijos registrados.");
        }
        
        actualizarInfo(resultado.toString());
    }
    
    private void buscarPadres() {
        String nombreBuscado = campoBusqueda.getText().trim();
        if (nombreBuscado.isEmpty()) {
            mostrarError("Por favor ingrese un nombre para buscar sus padres");
            return;
        }
        
        StringBuilder resultado = new StringBuilder();
        resultado.append("Padres de: ").append(nombreBuscado).append("\n");
        resultado.append("=====================================\n\n");
        
        boolean encontrado = treeGraph.buscarPadres(nombreBuscado, resultado);
        
        if (!encontrado) {
            resultado.append("No se encontró el miembro o no tiene padres registrados.");
        }
        
        actualizarInfo(resultado.toString());
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public void actualizarInfo(String info) {
        areaTexto.setText(info);
    }
}