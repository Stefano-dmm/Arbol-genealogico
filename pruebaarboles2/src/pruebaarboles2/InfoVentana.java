package pruebaarboles2;

import javax.swing.*;
import java.awt.*;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Ventana de información que proporciona una interfaz gráfica para interactuar con el árbol genealógico.
 * Permite realizar búsquedas de miembros de la familia y visualizar sus relaciones.
 * 
 * Esta clase extiende JFrame y proporciona las siguientes funcionalidades:
 * - Búsqueda de miembros por nombre
 * - Visualización de hijos de un miembro específico
 * - Visualización de líneas ancestrales completas
 * - Apertura de nuevos archivos JSON para construir diferentes árboles
 * 
 * @author [Tu Nombre]
 * @version 1.0
 */
public class InfoVentana extends JFrame {
    /**
     * Área de texto donde se muestra la información del árbol y los resultados de búsqueda
     */
    private JTextArea areaTexto;
    
    /**
     * Campo de texto para ingresar los términos de búsqueda
     */
    private JTextField campoBusqueda;
    
    /**
     * Referencia al árbol genealógico que se está visualizando
     */
    private Tree treeGraph;
    
    /**
     * Constructor que inicializa la ventana de información y configura sus componentes.
     * 
     * @param treeGraph Referencia al árbol genealógico que se visualizará
     */
    public InfoVentana(Tree treeGraph) {
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
        
        // Modificar el botón para cerrar
        JButton abrirJsonBtn = new JButton("Abrir JSON");
        abrirJsonBtn.addActionListener(e -> {
            String jsonContent = Pruebaarboles2.leerArchivoJson();
            if (!jsonContent.isEmpty()) {
                try {
                    JSONObject json = new JSONObject(jsonContent);
                    Tree nuevoArbol = new Tree();
                    nuevoArbol.construirArbol(json);
                    nuevoArbol.mostrarGrafo();
                    dispose(); // Cerrar la ventana actual
                } catch (JSONException ex) {
                    System.err.println("Error al analizar JSON: " + ex.getMessage());
                }
            }
        });
        JPanel panelBoton = new JPanel();
        panelBoton.add(abrirJsonBtn);
        
        // Organizar componentes
        setLayout(new BorderLayout());
        add(panelBusqueda, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelBoton, BorderLayout.SOUTH);
    }
    
    /**
     * Realiza una búsqueda de miembros por nombre y muestra los resultados.
     * El miembro encontrado se resalta en el grafo y su información se muestra en el área de texto.
     */
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
    
    /**
     * Busca y muestra los hijos directos de un miembro específico.
     * El miembro buscado se resalta en naranja y sus hijos en verde.
     */
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
    
    /**
     * Busca y muestra la línea ancestral completa de un miembro específico.
     * El miembro buscado se resalta en naranja y todos sus ancestros en verde.
     */
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
    
    /**
     * Muestra un diálogo de error con un mensaje específico.
     * 
     * @param mensaje El mensaje de error que se mostrará al usuario
     */
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Actualiza el contenido del área de texto con nueva información.
     * 
     * @param info El texto que se mostrará en el área de información
     */
    public void actualizarInfo(String info) {
        areaTexto.setText(info);
    }
}