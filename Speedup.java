import java.util.Scanner;

public class Speedup
{
	private static int tam, maxTareas;
	private static double tic, tac;
	private static double[] tiempos;
	
	public static void main(String[] args) throws Exception
	{
		if (args.length == 2)
		{
			tam = Integer.parseInt(args[0]);
			maxTareas = Integer.parseInt(args[1]);
		}
		else
		{
			Scanner teclado = new Scanner(System.in);
			System.out.println("Introduce un tama�o v�lido");
			tam = teclado.nextInt();
			System.out.println("Introduce el m�ximo de tareas a ejecutar");
			maxTareas = teclado.nextInt();
			teclado.close();
		}
		
		tiempos = new double[maxTareas];
		
		BelZhab reaccion = new BelZhab(tam, tam);
		reaccion.pasos(100);
		
		System.out.println("Tareas\tSpeedup\tTiempo");
		
		for (int n = 1; n <= maxTareas; ++n)
		{
			reaccion.nucleos(n);
			
			tic = System.currentTimeMillis();
			reaccion.siguienteGeneracion();
			tac = System.currentTimeMillis();
			
			tiempos[n - 1] = (tac - tic) / 100;
			
			System.out.println(n + "\t" + (tiempos[0] / tiempos[n-1]) + "\t" + tiempos[n-1]);
		}
		
		reaccion.close();
	}
}