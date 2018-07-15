import java.util.Random;
import java.util.concurrent.BrokenBarrierException;

public class Main {

	public static void main(String args[]) throws InterruptedException, BrokenBarrierException {

		if (args.length < 3) {
			System.out.println("Add missing arguments, 'arrayLength' , 'seed' and ammount of cores to be used");
			System.exit(0);
		}

		Timer parTimer = new Timer();
		Timer sekvTimer = new Timer();
		
		
		int arrayLength = Integer.parseInt(args[0]);

		int seed = Integer.parseInt(args[1]);
		
		int coreCount = Integer.parseInt(args[2]);
		
		if(coreCount < 1){
			System.out.println("Select 1 or more threads");
			System.exit(1);
		}

		System.out.println("Running with array length of " + arrayLength + " and seed " + seed + "\nUsing " + coreCount + " threads\n");
		
		int[] array1;

		int[] array2;
		
		Par par;
		
		Sekv sekv;

		for (int x = 0; x < 5; x++) {

			array1 = generateArray(arrayLength, seed);

			array2 = generateArray(arrayLength, seed);

			par = new Par(new FinnStorste(array1, 1), array1,coreCount);

			sekv = new Sekv();

		
			array2 = sekv.radixMulti(array2,sekvTimer);
			
			array1 = par.radixMulti(parTimer);

			for (int i = 0; i < array2.length; i++) {
				if (array2[i] != array1[i]) {
					System.out.println("Feil");
					System.exit(1);
				}
			}
			System.out.println("All tests passed\n");
		}
		
		System.out.println("\nSekv median = " + sekvTimer.getMedian() + "\nPar median  = " + parTimer.getMedian()
		+ "\nSpeedup     = " + (sekvTimer.getMedian()/parTimer.getMedian()));

	}

	public static int[] generateArray(int n, int seed) {
		int[] ret = new int[n];

		Random rnd = new Random(seed);

		for (int i = 0; i < ret.length; i++)
			ret[i] = rnd.nextInt(n);

		return ret;

	}
}
