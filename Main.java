import java.util.Scanner;

public class Main
{
	private static int tam;
	
	public static void main(String[] args) throws Exception
	{
		if (args.length == 1)
			tam = Integer.parseInt(args[0]);
		else
		{
			Scanner teclado = new Scanner(System.in);
			System.out.println("Introduce un tamaño valido");
			tam = teclado.nextInt();
		}
		
		BelZhab game = new BelZhab(tam, tam);
		
		VisorImagen ventana = new VisorImagen();
		ventana.setVisible(true);
		int margen = 40; // hay que tener en cuenta la barra de titulo y los bordes
		ventana.setBounds(ventana.getX(), ventana.getY(), tam + margen, tam + margen);
		
		int counter = 0;
		double tic = System.currentTimeMillis();
		for(int i = 0;i < 100; ++i)
		{
			double[][][] grid = game.mostrar();
			
			ventana.setImagen(CargaImagen.Convertir(grid));
			//double tic = System.currentTimeMillis();
			game.siguienteGeneracion();
			//System.out.println("Generacion calculada en " + (System.currentTimeMillis()-tic) + "ms.");
		}
		double tac = System.currentTimeMillis();
		
		game.close();
		System.out.println(tac-tic);
	}
}
