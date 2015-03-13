import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.Random;

public class BelZhab
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
			
			tareas[i] = new BelZhabWorker(inicioIntervalo, finIntervalo);
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
}
