/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package árbol.genealógico;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ExploradorJson extends Frame {
    private String archivoSeleccionado;
    private analizadorJson analizador;
    private TextArea areaResultados;

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

        // Botón de selección
        Button btnSeleccionar = new Button("Seleccionar Archivo JSON");
        btnSeleccionar.addActionListener((ActionEvent e) -> {
            seleccionarArchivo();
        });
        panelPrincipal.add(btnSeleccionar, BorderLayout.NORTH);

        // Área de resultados
        areaResultados = new TextArea();
        areaResultados.setEditable(false);
        panelPrincipal.add(areaResultados, BorderLayout.CENTER);

        add(panelPrincipal);

        // Manejador de cierre de ventana
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                System.exit(0);
            }        
        });
    }

    private void seleccionarArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar archivo JSON");
        
        // Filtro para archivos JSON
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Archivos JSON (*.json)", "json");
        fileChooser.setFileFilter(filter);
        
        // Mostrar el diálogo de selección de archivo
        int resultado = fileChooser.showOpenDialog(this);
        
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            archivoSeleccionado = archivo.getAbsolutePath();
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

        } catch (IOException ex) {
            Dialog dialogo = new Dialog(this, "Error", true);
            dialogo.setLayout(new FlowLayout());
            dialogo.add(new Label("Error al analizar el archivo: " + ex.getMessage()));
            Button btnOk = new Button("OK");
            btnOk.addActionListener((ActionEvent e) -> {
                dialogo.dispose();
            });
            dialogo.add(btnOk);
            dialogo.setSize(300, 100);
            dialogo.setVisible(true);
        }
    }

    public static void main(String[] args) {
        ExploradorJson explorador = new ExploradorJson();
        explorador.setVisible(true);
    }
}
