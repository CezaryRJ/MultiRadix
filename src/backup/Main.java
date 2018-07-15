import java.util.Random;
import java.util.concurrent.BrokenBarrierException;

public class Main {

	public static void main(String args[]) throws InterruptedException, BrokenBarrierException{
		
		int[] array1 = generateArray(15500000,45858);
		
		
		int[] array2 = generateArray(15500000,45858);
		
		Par par = new Par(new FinnStorste(array1,1), array1);
		
		Sekv sekv = new Sekv();
		
		sekv.radixMulti(array2);
		
		par.radixMulti();
		
	
	}
	
	 public static int[] generateArray(int n, int seed) {
	        int[] ret = new int[n];
	        
	        Random rnd = new Random(seed);
	        
	        for(int i=0; i < ret.length; i++)
	            ret[i] = rnd.nextInt(n);
	        
	        return ret;
	        
	    }
}
