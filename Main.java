import java.util.Scanner;

public class Main
{
	private static int tam, tareas, iteraciones;
	
	private static double alfa, beta, gamma;
	private static double tic, tac;
	
	public static void main(String[] args) throws Exception
	{
		if (args.length >= 2)
		{
			tam         = Integer.parseInt(args[0]);
			iteraciones = Integer.parseInt(args[1]);
			tareas      = 0;
			
			alfa   = 1.2;
			beta   = 1.0;
			gamma  = 1.0;
		}
		else
		{
			if (args.length >= 3)
				tareas = Integer.parseInt(args[2]);
			else
			{
				if (args.length == 6)
				{
					alfa  = Double.parseDouble(args[3]);
					beta  = Double.parseDouble(args[4]);
					gamma = Double.parseDouble(args[5]);
				}
				else	
				{
					Scanner teclado = new Scanner(System.in);
					
					System.out.println("Uso: java Main <Tamaño> <Iteraciones> [<Tareas> [<alfa> <beta> <gamma>]]");
					
					System.out.println("Introduce un tamaño válido");
					tam = teclado.nextInt();
					System.out.println("Introduce el número de generaciones a computar");
					iteraciones = teclado.nextInt();
					System.out.println("Introduce el número de tareas que se usarán");
					tareas = teclado.nextInt();
					
					System.out.println("Introduce el coeficiente alfa");
					alfa = teclado.nextDouble();
					System.out.println("Introduce el coeficiente beta");
					beta = teclado.nextDouble();
					System.out.println("Introduce el coeficiente gamma");
					gamma = teclado.nextDouble();
					
					teclado.close();
				}
			}
		}
		
		if (tareas < 1)
			tareas = Runtime.getRuntime().availableProcessors();

		BelZhab reaccion = new BelZhab(tam, tam, alfa, beta, gamma, tareas);
			
		tic = System.currentTimeMillis();
		
		for (int i = 0; i < iteraciones; ++i)
			reaccion.siguienteGeneracion();
			
		tac = System.currentTimeMillis();
		
		
		System.out.println("Tiempo medio: " + (tac-tic)/iteraciones + " ms.");
		
		reaccion.close();
	}
}