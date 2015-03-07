import java.util.Scanner;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class MainGrafico
{
	private static BelZhab reaccion;
	private static BufferedImage imagen;
	
	private static JFrame frame;
	private static JPanel panel;
	private static JLabel picLabel;
	private static JTextField tam;
	private static JTextField alfa;
	private static JTextField beta;
	private static JTextField gamma;
	private static JButton ejecutar;
	private static JButton parar;
	
	private static SwingWorker worker;
	private static boolean finalizar;
	
	public static BufferedImage convertir(double[][][] matriz)
	{
        
        BufferedImage imagen = new BufferedImage(matriz[0].length, matriz[0][0].length, BufferedImage.TYPE_4BYTE_ABGR);
        
        for(int i = 0 ; i < matriz[0].length ; ++i)
        {
            for(int j = 0 ; j < matriz[0][0].length ; ++j)
            {
				float r = (float)matriz[0][j][i];
				float g = (float)matriz[1][j][i];
				float b = (float)matriz[2][j][i];
				
                Color c = new Color(r, g, b);
                imagen.setRGB(i, j, c.getRGB());
            }
        }
        
        return imagen;
    }
	
	private static void GUI()
	{
		//Create and set up the window.
		frame = new JFrame("Belousov-Zhabotinsky CA");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		panel = new JPanel();
		JPanel parametros = new JPanel(new GridLayout(5, 2));
		panel.add(parametros);
		
		JLabel tamText   = new JLabel("Tamaño ");
		JLabel alfaText  = new JLabel("Alfa ");
		JLabel betaText  = new JLabel("Beta ");
		JLabel gammaText = new JLabel("Gamma ");
		
		tam   = new JTextField("400");
		alfa  = new JTextField("1.2");
		beta  = new JTextField("1.0");
		gamma = new JTextField("1.0");
		
		ejecutar = new JButton("Ejecutar");
		parar    = new JButton("Parar");
		
		parametros.add(tamText);
		parametros.add(tam);
		
		parametros.add(alfaText);
		parametros.add(alfa);
		
		parametros.add(betaText);
		parametros.add(beta);
		
		parametros.add(gammaText);
		parametros.add(gamma);
		
		parametros.add(ejecutar);
		parametros.add(parar);

		// picLabel = new JLabel();
		// panel.add(picLabel);
		
		frame.getContentPane().add(panel);

		//Display the window.
		frame.pack();
		frame.setVisible(true);
    }
	
	public static void main(String[] args) throws Exception
	{
		finalizar = false;
		
		GUI();
		
		ejecutar.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				while (worker != null && !worker.isDone())
					finalizar = true;
				
				int    campoTam   = Integer.parseInt(tam.getText());
				double campoAlfa  = Double.parseDouble(alfa.getText());
				double campoBeta  = Double.parseDouble(beta.getText());
				double campoGamma = Double.parseDouble(gamma.getText());
				
				reaccion = new BelZhab(campoTam, campoTam, campoAlfa, campoBeta, campoGamma);
				if (picLabel == null)
				{
					picLabel = new JLabel(new ImageIcon(convertir(reaccion.mostrar())));
					panel.add(picLabel);
				}
				else
					picLabel.setIcon(new ImageIcon(convertir(reaccion.mostrar())));
					
				panel.revalidate();
				panel.repaint();
				frame.pack();
				
				finalizar = false;
				
				worker = new SwingWorker<Void, Void>()
				{
					public Void doInBackground()
					{
						while (!finalizar)
						{
							imagen = convertir(reaccion.mostrar());
							picLabel.setIcon(new ImageIcon(imagen));
							
							reaccion.siguienteGeneracion();
						}
						
						return null;
					}
					
					protected void done()
					{
						finalizar = false;
					}
				};
				worker.execute();
			}
		});
		
		parar.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				finalizar = true;
			}
		});
	}
}