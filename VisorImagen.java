
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.JPanel;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author usuario
 */
public class VisorImagen extends javax.swing.JFrame {

    private MiPanel jPanelImagen;
    
    public VisorImagen() {
        initComponents();
    }    
    
    private void initComponents() {

        jPanelImagen = new MiPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout jPanelImagenLayout = new javax.swing.GroupLayout(jPanelImagen);
        jPanelImagen.setLayout(jPanelImagenLayout);
        jPanelImagenLayout.setHorizontalGroup(
            jPanelImagenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jPanelImagenLayout.setVerticalGroup(
            jPanelImagenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelImagen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelImagen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }
    
    void setImagen(BufferedImage imagen) {
        this.jPanelImagen.setImagen(imagen);
		this.jPanelImagen.repaint();
    }
    
    public static void main(String[] args) {
        BufferedImage imagen = null;
        try {
            int[][] matriz = CargaImagen.cargar("uca.png");
            imagen = CargaImagen.Convertir(matriz);
        } catch (IOException ex) {
            System.out.println("Error cargando imagen: " + ex.getMessage());
        }

        if(imagen != null)
        {                
            VisorImagen ventana = new VisorImagen();
            ventana.setVisible(true);
            int margen = 40; // hay que tener en cuenta la barra de titulo y los bordes
            ventana.setBounds(ventana.getX(), ventana.getY()
                    , imagen.getWidth() + margen, imagen.getHeight() + margen);
            ventana.setImagen(imagen);
        }
    }
}

class MiPanel extends JPanel {

    private BufferedImage imagen = null;
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(imagen != null)
        g.drawImage(imagen, 0, 0, null);         
    }
    
    void setImagen(BufferedImage imagen) {
        this.imagen = imagen;
    }
}
