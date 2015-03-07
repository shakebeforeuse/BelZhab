import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;

public class BelZab implements Runnable
{
	public static final int A = 0;
	public static final int B = 1;
	public static final int C = 2;
	
	private Random random;
	
	private static double[][][][] reaccion;
	private static double alfa;
	private static double beta;
	private static double gamma;
	private static int pasos;
	
	private static int ancho;
	private static int alto;
	
	private static int generacion;
	
	private static int nucleos;
	private static ExecutorService threadPool;
	private static Runnable[] tareas;
	private static CyclicBarrier barrera;
	private static ReentrantLock reentrant;
	
	private int inicio;
	private int fin;
	
	private BelZab(int ancho, int alto, double alfa, double beta, double gamma, int inicio, int fin)
	{
		this.inicio = inicio;
		this.fin    = fin;
	}
	
	public BelZab(int ancho, int alto, double alfa, double beta, double gamma)
	{
		this.ancho = ancho;
		this.alto  = alto;
		reaccion   = new double[2][3][alto][ancho];
		generacion = 0;
		pasos      = 1;
		
		this.alfa  = alfa;
		this.beta  = beta;
		this.gamma = gamma;

		nucleos    = Runtime.getRuntime().availableProcessors();
		threadPool = Executors.newFixedThreadPool(nucleos);
		barrera    = new CyclicBarrier(nucleos + 1);
		tareas     = new Runnable[nucleos];
		reentrant  = new ReentrantLock();
		
		for (int i = 0; i < nucleos; ++i)
		{
			int inicioIntervalo = i * (alto / nucleos);
			int finIntervalo    = (i+1) * (alto/nucleos);
			
			if ((i+1) == nucleos)
				finIntervalo = alto;
			
			tareas[i] = new BelZab(ancho, alto, alfa, beta, gamma, inicioIntervalo, finIntervalo);
		}
		
		random = new Random();
		aleatorio();
	}
	
	public BelZab(int ancho, int alto)
	{
		this(ancho, alto, 1.2, 1, 1);
	}
	
	public void aleatorio()
	{
		reentrant.lock();
		
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
		
		reentrant.unlock();
	}
	
	public void nucleos(int n)
	{
		reentrant.lock();
		
		this.close();
		
		nucleos = n;
		threadPool = Executors.newFixedThreadPool(nucleos);
		barrera    = new CyclicBarrier(nucleos + 1);
		tareas     = new Runnable[nucleos];
		
		for (int i = 0; i < nucleos; ++i)
		{
			int inicioIntervalo = i * (alto / nucleos);
			int finIntervalo    = (i+1) * (alto/nucleos);
			
			if ((i+1) == nucleos)
				finIntervalo = alto;
			
			tareas[i] = new BelZab(ancho, alto, alfa, beta, gamma, inicioIntervalo, finIntervalo);
		}
		
		reentrant.unlock();
	}
	
	public void pasos(int n)
	{
		reentrant.lock();
		
		pasos = n;
		
		reentrant.unlock();
	}
	
	public static void siguienteGeneracion()
	{
		reentrant.lock();
		
		for (int i = 0; i < tareas.length; ++i)
			threadPool.execute(tareas[i]);
		
		for (int i = 0; i < pasos; ++i)
		{
			try
			{
				if (barrera.await() == 0)
					++generacion;
			}
			catch (InterruptedException e)
			{
				System.out.println("InterruptedException: BelZab.siguienteGeneracion()");
				System.out.println("Error: " + e.getMessage());
			}
			catch (BrokenBarrierException e)
			{
				System.out.println("BrokenBarrierException: BelZab.siguienteGeneracion()");
				System.out.println("Error: " + e.getMessage());
			}
		}
		
		reentrant.unlock();
	}
	
	private void subGeneracion()
	{
		double[] concentracion;
		
		for (int i = inicio; i < fin; ++i)
		{
			for (int j = 0; j < ancho; ++j)
			{
				concentracion = concentraciones(j, i);
				
				reaccion[(generacion+1) % 2][A][i][j] = parteFlotante(concentracion[A] * (1 + (alfa  * concentracion[B] - gamma * concentracion[C])));
				reaccion[(generacion+1) % 2][B][i][j] = parteFlotante(concentracion[B] * (1 + (beta  * concentracion[C] - alfa  * concentracion[A])));
				reaccion[(generacion+1) % 2][C][i][j] = parteFlotante(concentracion[C] * (1 + (gamma * concentracion[A] - beta  * concentracion[B])));
			}
		}
	}
	
	private static double[] concentraciones(int x, int y)
	{
		double[] concentracion = new double[3];
		
		for (int i = -1; i <= 1; ++i)
		{
			for (int j = -1; j <= 1; ++j)
			{
				concentracion[A] += reaccion[generacion % 2][A][mod(y+i, alto)][mod(x+j, ancho)];
				concentracion[B] += reaccion[generacion % 2][B][mod(y+i, alto)][mod(x+j, ancho)];
				concentracion[C] += reaccion[generacion % 2][C][mod(y+i, alto)][mod(x+j, ancho)];
			}
		}
		
		concentracion[A] /= 9;
		concentracion[B] /= 9;
		concentracion[C] /= 9;
		
		return concentracion;
	}
	
	private static double parteFlotante(double f)
	{
		return f - Math.floor(f);
	}
	
	public static double[][][] mostrar()
	{
		reentrant.lock();
		
		try
		{
			return reaccion[generacion % 2];
		}
		finally
		{
			reentrant.unlock();
		}
	}
	
	public void run()
	{
		for (int i = 0; i < pasos; ++i)
		{
			subGeneracion();
			
			try
			{
				if (barrera.await() == 0)
					++generacion;
			}
			catch (InterruptedException e)
			{
				System.out.println("InterruptedException: BelZab.run()");
				System.out.println("Error: " + e.getMessage());
			}
			catch (BrokenBarrierException e)
			{
				System.out.println("BrokenBarrierException: BelZab.run()");
				System.out.println("Error: " + e.getMessage());
			}
		}
	}
	
	private static int mod(int a, int b)
	{
		int r = a % b;
		if (r < 0)
			r += b;
		
		return r;
	}
	
	public void close()
	{
		reentrant.lock();
		
		while (!threadPool.isTerminated())
			threadPool.shutdown();
			
		reentrant.unlock();
	}
	
	/*protected void finalize()
	{
		this.close();
	}*/
}
























