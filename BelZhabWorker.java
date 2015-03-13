import java.util.concurrent.BrokenBarrierException;

public class BelZhabWorker implements Runnable
{
	private int inicio;
	private int fin;
	
	public BelZhabWorker(int inicio, int fin)
	{
		this.inicio = inicio;
		this.fin    = fin;
	}
	
	public void run()
	{
		double[] concentracion;
		
		for (int i = inicio; i < fin; ++i)
		{
			for (int j = 0; j < BelZhab.ancho; ++j)
			{
				concentracion = concentraciones(j, i);
				
				BelZhab.reaccion[(BelZhab.generacion+1) % 2][0][i][j] = parteFlotante(concentracion[0] * (1 + (BelZhab.alfa  * concentracion[1] - BelZhab.gamma * concentracion[2])));
				BelZhab.reaccion[(BelZhab.generacion+1) % 2][1][i][j] = parteFlotante(concentracion[1] * (1 + (BelZhab.beta  * concentracion[2] - BelZhab.alfa  * concentracion[0])));
				BelZhab.reaccion[(BelZhab.generacion+1) % 2][2][i][j] = parteFlotante(concentracion[2] * (1 + (BelZhab.gamma * concentracion[0] - BelZhab.beta  * concentracion[1])));
			}
		}
		
		try
		{
			BelZhab.barrera.await();
		}
		catch (InterruptedException e)
		{
			System.out.println("InterruptedException: BelZhab.run()");
			System.out.println("Error: " + e.getMessage());
		}
		catch (BrokenBarrierException e)
		{
			System.out.println("BrokenBarrierException: BelZhab.run()");
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
				concentracion[0] += BelZhab.reaccion[BelZhab.generacion % 2][0][mod(y+i, BelZhab.alto)][mod(x+j, BelZhab.ancho)];
				concentracion[1] += BelZhab.reaccion[BelZhab.generacion % 2][1][mod(y+i, BelZhab.alto)][mod(x+j, BelZhab.ancho)];
				concentracion[2] += BelZhab.reaccion[BelZhab.generacion % 2][2][mod(y+i, BelZhab.alto)][mod(x+j, BelZhab.ancho)];
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
}