/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package arbolgenealogicoprue;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class ExploradorJson extends Frame {
    private String archivoSeleccionado;
    private VisualizadorArbol visualizador;
    private Tree tree;

    public ExploradorJson() {
        inicializarInterfaz();
        tree = new Tree(); // Inicializamos el árbol genealógico
    }

    private void inicializarInterfaz() {
        setTitle("Explorador JSON");
        setSize(800, 600);
        setLayout(new BorderLayout(10, 10));

        Panel panelPrincipal = new Panel();
        panelPrincipal.setLayout(new BorderLayout(10, 10));

        TextArea areaResultados = new TextArea();
        areaResultados.setEditable(false);
        panelPrincipal.add(areaResultados, BorderLayout.CENTER);

        MenuBar menuBar = new MenuBar();
        Menu menuArchivo = new Menu("Archivo");
        MenuItem abrirItem = new MenuItem("Abrir JSON");
        abrirItem.addActionListener(e -> seleccionarArchivo());
        menuArchivo.add(abrirItem);
        menuBar.add(menuArchivo);
        setMenuBar(menuBar);

        add(panelPrincipal);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
    }

    private void seleccionarArchivo() {
        FileDialog fd = new FileDialog(this, "Seleccionar archivo JSON", FileDialog.LOAD);
        fd.setFile("*.json");
        fd.setVisible(true);

        if (fd.getFile() != null) {
            archivoSeleccionado = fd.getDirectory() + fd.getFile();
            analizarArchivo();
        }
    }

    private void analizarArchivo() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(archivoSeleccionado));
            StringBuilder jsonContent = new StringBuilder();
            String line;

            // Leer todo el archivo JSON
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

            // Procesar el JSON
            procesarJson(jsonContent.toString());

            // Crear y mostrar el grafo
            visualizador = new VisualizadorArbol(tree);
            visualizador.mostrarGrafo(); // Mostrar el grafo

        } catch (IOException ex) {
            System.out.println("Error al analizar el archivo: " + ex.getMessage());
        }
    }

    // Procesar el contenido del JSON
    private void procesarJson(String json) {
        // Lógica para procesar el JSON y construir el árbol genealógico.
        // Para fines de este ejemplo, asumimos que el JSON está bien estructurado.
        
        // Parsear el JSON manualmente (suponiendo que ya está bien estructurado)
        String houseName = "House Targaryen"; // Ejemplo, en un futuro se podría obtener dinámicamente.
        
        // Aquí parseamos el JSON de forma simplificada (suponiendo que ya está bien estructurado)
        if (json.contains(houseName)) {
            // Crear nodos para las personas
            Tree.Nodo aegon = new Tree.Nodo("Aegon Targaryen", "Aegon the Conqueror", "King", "Targaryen");
            Tree.Nodo aenys = new Tree.Nodo("Aenys Targaryen", "King Abomination", "King", "Targaryen");
            aegon.agregarHijo(aenys); // Aegon es el padre de Aenys.

            // Ejemplo de agregar más personas y relaciones
            Tree.Nodo maegor = new Tree.Nodo("Maegor Targaryen", "Maegor the Cruel", "King", "Targaryen");
            aegon.agregarHijo(maegor); // Aegon es el padre de Maegor

            // Establecer la raíz del árbol (aquí se toma a Aegon como ejemplo)
            tree.setRaiz(aegon);
        }
    }
}
