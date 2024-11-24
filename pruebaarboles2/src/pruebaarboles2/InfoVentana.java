package pruebaarboles2;

import javax.swing.*;
import java.awt.*;

public class InfoVentana extends JFrame {
    private JTextArea areaTexto;
    
    public InfoVentana() {
        // Configuración básica de la ventana
        setTitle("Información de los Nodos");
        setSize(400, 600);
        setLocationRelativeTo(null);
        
        // Crear componentes
        areaTexto = new JTextArea();
        areaTexto.setEditable(false);
        areaTexto.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        // Agregar scroll
        JScrollPane scrollPane = new JScrollPane(areaTexto);
        
        // Agregar al frame
        add(scrollPane);
        
        // Botón para cerrar
        JButton cerrarBtn = new JButton("Cerrar");
        cerrarBtn.addActionListener(e -> dispose());
        
        JPanel panelBoton = new JPanel();
        panelBoton.add(cerrarBtn);
        add(panelBoton, BorderLayout.SOUTH);
    }
    
    public void actualizarInfo(String info) {
        areaTexto.setText(info);
    }
} 