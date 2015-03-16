import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.Random;
import java.util.Scanner;

public class BelZhab implements Runnable
{
	private Random random;

	public static final int A = 0;
	public static final int B = 1;
	public static final int C = 2;
	
	public static int ancho;
	public static int alto;
	public static double[][][][] reaccion;
	public static int generacion;
	
	public static double alfa;
	public static double beta;
	public static double gamma;
	
	private static int nucleos;
	private static ExecutorService threadPool;
	private static Runnable[] tareas;
	public  static CyclicBarrier barrera;
	
	private int inicio;
	private int fin;
	
	
	public BelZhab(int ancho, int alto, double alfa, double beta, double gamma, int nTareas)
	{
		this.ancho = ancho;
		this.alto  = alto;
		reaccion   = new double[2][3][alto][ancho];
		generacion = 0;
		
		this.alfa  = alfa;
		this.beta  = beta;
		this.gamma = gamma;

		nucleos(nTareas);
		
		random = new Random();
		aleatorio();
	}
	
	public BelZhab(int ancho, int alto)
	{
		this(ancho, alto, 1.2, 1, 1, Runtime.getRuntime().availableProcessors());
	}
	
	private BelZhab(int inicio, int fin, boolean dummy)
	{
		this.inicio = inicio;
		this.fin    = fin;
	}
	
	public void aleatorio()
	{		
		generacion = 0;
		
		for (int i = 0; i < alto; ++i)
		{
			for (int j = 0; j < ancho; ++j)
			{
				reaccion[0][A][i][j] = random.nextDouble();
				reaccion[0][B][i][j] = random.nextDouble();
				reaccion[0][C][i][j] = random.nextDouble();
			}
		}
	}
	
	public void nucleos(int n)
	{
		if (threadPool != null)
			this.close();
		
		nucleos    = n;
		threadPool = Executors.newFixedThreadPool(nucleos);
		barrera    = new CyclicBarrier(nucleos + 1);
		tareas     = new Runnable[nucleos];
		
		for (int i = 0; i < nucleos; ++i)
		{
			int inicioIntervalo = i * (alto / nucleos);
			int finIntervalo    = (i+1) * (alto/nucleos);
			
			if ((i+1) == nucleos)
				finIntervalo = alto;
			
			tareas[i] = new BelZhab(inicioIntervalo, finIntervalo, false);
		}
	}
	
	public static void siguienteGeneracion()
	{
		for (int i = 0; i < tareas.length; ++i)
			threadPool.execute(tareas[i]);
		
		try
		{
			barrera.await();
			++generacion;
		}
		catch (InterruptedException e)
		{
			System.out.println("InterruptedException: BelZhab.siguienteGeneracion()");
			System.out.println("Error: " + e.getMessage());
		}
		catch (BrokenBarrierException e)
		{
			System.out.println("BrokenBarrierException: BelZhab.siguienteGeneracion()");
			System.out.println("Error: " + e.getMessage());
		}
	}
	
	public static double[][][] mostrar()
	{
		return reaccion[generacion % 2];
	}
	
	public void close()
	{		
		while (!threadPool.isTerminated())
			threadPool.shutdown();
	}
	
	public void run()
	{
		double[] concentracion;
		
		for (int i = inicio; i < fin; ++i)
		{
			for (int j = 0; j < ancho; ++j)
			{
				concentracion = concentraciones(j, i);
				
				reaccion[(generacion+1) % 2][0][i][j] = parteFlotante(concentracion[0] * (1 + (alfa  * concentracion[1] - gamma * concentracion[2])));
				reaccion[(generacion+1) % 2][1][i][j] = parteFlotante(concentracion[1] * (1 + (beta  * concentracion[2] - alfa  * concentracion[0])));
				reaccion[(generacion+1) % 2][2][i][j] = parteFlotante(concentracion[2] * (1 + (gamma * concentracion[0] - beta  * concentracion[1])));
			}
		}
		
		try
		{
			barrera.await();
		}
		catch (InterruptedException e)
		{
			System.out.println("InterruptedException: run()");
			System.out.println("Error: " + e.getMessage());
		}
		catch (BrokenBarrierException e)
		{
			System.out.println("BrokenBarrierException: run()");
			System.out.println("Error: " + e.getMessage());
		}
	}
	
	private static double[] concentraciones(int x, int y)
	{
		double[] concentracion = new double[3];
		
		for (int i = -1; i <= 1; ++i)
		{
			for (int j = -1; j <= 1; ++j)
			{
				concentracion[0] += reaccion[generacion % 2][0][mod(y+i, alto)][mod(x+j, ancho)];
				concentracion[1] += reaccion[generacion % 2][1][mod(y+i, alto)][mod(x+j, ancho)];
				concentracion[2] += reaccion[generacion % 2][2][mod(y+i, alto)][mod(x+j, ancho)];
			}
		}
		
		concentracion[0] /= 9;
		concentracion[1] /= 9;
		concentracion[2] /= 9;
		
		return concentracion;
	}
	
	private static double parteFlotante(double f)
	{
		return f - Math.floor(f);
	}
	
	private static int mod(int a, int b)
	{		
		return (a + b) % b;
	}
	
	
	
	
	public static void main(String[] args)
	{
		int tam = 0;
		int tareas = 0;
		int iteraciones = 0;
		
		double alfa  = 0;
		double beta  = 0;
		double gamma = 0;
		double tic   = 0;
		double tac   = 0;
		
		
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
					
					System.out.println("Uso: java BelZhab <Tamaño> <Iteraciones> [<Tareas> [<alfa> <beta> <gamma>]]");
					
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
