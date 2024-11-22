/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package árbol.genealógico;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class ExploradorJson extends Frame {
    private String archivoSeleccionado;
    private analizadorJson analizador;
    private TextArea areaResultados;
    private Buscador buscador;
    private VisualizadorArbol visualizador;

    public ExploradorJson() {
        analizador = new analizadorJson();
        inicializarInterfaz();
    }

    private void inicializarInterfaz() {
        setTitle("Explorador JSON");
        setSize(800, 600);
        setLayout(new BorderLayout(10, 10));

        // Panel principal
        Panel panelPrincipal = new Panel();
        panelPrincipal.setLayout(new BorderLayout(10, 10));

        // Panel de búsqueda
        Panel panelBusqueda = new Panel();
        panelBusqueda.setLayout(new FlowLayout());
        
        // Componentes de búsqueda
        TextField campoBusqueda = new TextField(30);
        Choice tipoBusqueda = new Choice();
        tipoBusqueda.add("Buscar por Nombre");
        tipoBusqueda.add("Buscar por Título");
        Button btnBuscar = new Button("Buscar");
        Button btnLimpiar = new Button("Limpiar Búsqueda");
        
        panelBusqueda.add(tipoBusqueda);
        panelBusqueda.add(campoBusqueda);
        panelBusqueda.add(btnBuscar);
        panelBusqueda.add(btnLimpiar);

        // Área de resultados
        areaResultados = new TextArea();
        areaResultados.setEditable(false);

        // Agregar componentes al panel principal
        panelPrincipal.add(panelBusqueda, BorderLayout.NORTH);
        panelPrincipal.add(areaResultados, BorderLayout.CENTER);

        // Barra de menú
        MenuBar menuBar = new MenuBar();
        Menu menuArchivo = new Menu("Archivo");
        MenuItem abrirItem = new MenuItem("Abrir JSON");
        abrirItem.addActionListener((ActionEvent e) -> seleccionarArchivo());
        menuArchivo.add(abrirItem);
        menuBar.add(menuArchivo);
        setMenuBar(menuBar);

        add(panelPrincipal);

        // Manejador de cierre de ventana
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                System.exit(0);
            }        
        });

        // Modificar el ActionListener del botón de búsqueda
        btnBuscar.addActionListener((ActionEvent e) -> {
            if (buscador != null && visualizador != null) {
                String textoBusqueda = campoBusqueda.getText().trim();
                if (!textoBusqueda.isEmpty()) {
                    // Redirigir la salida al área de texto
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    PrintStream ps = new PrintStream(baos);
                    PrintStream old = System.out;
                    System.setOut(ps);

                    // Realizar búsqueda según el tipo seleccionado
                    TablaHash.Persona personaEncontrada = null;
                    if (tipoBusqueda.getSelectedItem().equals("Buscar por Nombre")) {
                        personaEncontrada = buscador.buscarPorNombreYResaltar(textoBusqueda);
                    } else {
                        personaEncontrada = buscador.buscarPorTituloYResaltar(textoBusqueda);
                    }

                    // Restaurar la salida y mostrar resultados
                    System.out.flush();
                    System.setOut(old);
                    areaResultados.setText(baos.toString());

                    // Resaltar el nodo en el grafo si se encontró la persona
                    if (personaEncontrada != null) {
                        visualizador.resaltarNodo(personaEncontrada.nombreCompleto);
                    }
                }
            } else {
                mostrarError("Primero debe cargar un archivo JSON");
            }
        });

        // Añadir ActionListener para el botón de limpiar
        btnLimpiar.addActionListener((ActionEvent e) -> {
            if (visualizador != null) {
                // Limpiar el resaltado del grafo
                visualizador.limpiarResaltado();
                
                // Redirigir la salida al área de texto
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream ps = new PrintStream(baos);
                PrintStream old = System.out;
                System.setOut(ps);
                
                // Mostrar todos los nodos nuevamente
                analizador.mostrarPersonas();
                
                // Restaurar la salida y mostrar resultados
                System.out.flush();
                System.setOut(old);
                areaResultados.setText(baos.toString());
                
                // Limpiar el campo de búsqueda
                campoBusqueda.setText("");
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
            analizador = new analizadorJson();
            areaResultados.setText("");

            // Redirigir la salida estándar
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            PrintStream old = System.out;
            System.setOut(ps);

            // Cargar y analizar archivo
            analizador.cargarCasa(archivoSeleccionado);
            analizador.mostrarPersonas();

            // Restaurar la salida estándar y mostrar resultados
            System.out.flush();
            System.setOut(old);
            areaResultados.setText(baos.toString());
            
            // Crear el buscador después de cargar los datos
            buscador = new Buscador(analizador.getTablaPersonas());
            
            // Crear y mostrar el grafo
            visualizador = new VisualizadorArbol(analizador.getTablaPersonas());

        } catch (IOException ex) {
            mostrarError("Error al analizar el archivo: " + ex.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        Dialog dialogo = new Dialog(this, "Error", true);
        dialogo.setLayout(new FlowLayout());
        dialogo.add(new Label(mensaje));
        Button btnOk = new Button("OK");
        btnOk.addActionListener((ActionEvent e) -> dialogo.dispose());
        dialogo.add(btnOk);
        dialogo.setSize(300, 100);
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }

    public Buscador getBuscador() {
        return buscador;
    }
}
