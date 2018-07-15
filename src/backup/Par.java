import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

public class Par {

	final static int NUM_BIT = 8; // eller 6,8,9,10..

	FinnStorste getMax;

	int numBit;

	int coreCount;

	Thread traader[];

	CyclicBarrier synk;

	int[][] allCount;

	int[] a;

	int[] b;

	int maskLen;

	int shift = 0;

	int mask;

	int numDigits;

	Par(FinnStorste getMax, int a[]) {
		this.getMax = getMax;

		coreCount = Runtime.getRuntime().availableProcessors();

		synk = new CyclicBarrier(coreCount + 1);

		this.a = a;

		b = new int[a.length];

		traader = new Thread[coreCount];

	}

	int[] radixMulti() throws InterruptedException, BrokenBarrierException {

		// 1-5 digit radixSort of : a[]
		int max = a[0], numBit = 2;
		int[] bit;

		// a) finn max verdi i a[]
		max = getMax.getMax();
		while (max >= (1L << numBit))
			numBit++; // antall siffer i max

		// step a end

		// bestem antall bit i numBits sifre
		numDigits = Math.max(1, numBit / NUM_BIT);
		bit = new int[numDigits];
		int rest = numBit % NUM_BIT;
		System.out.println(max + " " + numBit + " " + numDigits);
		// fordel bitene vi skal sortere paa jevnt
		for (int i = 0; i < bit.length; i++) {
			bit[i] = numBit / numDigits;
			if (rest-- > 0)
				bit[i]++;
		}

		int chunk = a.length / coreCount;

		for (int i = 0; i < coreCount; i++) {
			traader[i] = new Thread(new worker(i, (chunk * i), chunk * (i + 1) - 1));
		}

		for (int i = 0; i < coreCount; i++) {
			traader[i].start();
		}

		System.out.println("Bit length = " + bit.length);
		int[] t = a;
		for (int i = 0; i < bit.length; i++) {

			maskLen = bit[i];

			mask = (1 << maskLen) - 1;

			allCount = new int[coreCount][mask + 1];

			// System.out.println("chunk = " + chunk);

			System.out.println(" radixSort maskLen:" + maskLen + ", shift :" + shift);

			synk.await();// call barrier to let threads start

			synk.await();// wait for threads to count

			int j = 0;
			int acumVal = 0;

			for (int y = 0; y < allCount[0].length; y++) {
				for (int x = 0; x < coreCount; x++) {
					j = allCount[x][y];
					allCount[x][y] = acumVal;
					acumVal += j;
					// System.out.println(acumVal);
				}

			}

			synk.await();// main done with summing

			synk.await();// then wait for threads to move

			shift += bit[i];
			// swap arrays (pointers only)
			t = a;
			a = b;
			b = t;

		}
		if ((bit.length & 1) != 0) {
			// et odde antall sifre, kopier innhold tilbake til original a[] (n
			// b)
			System.arraycopy(a, 0, b, 0, a.length);
		}

		for (int i = a.length - 1; i > a.length - 30; i--) {
			System.out.println(i + " " + a[i]);
		}

		testSort(a);

		return a;
	}

	void testSort(int[] a) {
		for (int i = 0; i < a.length - 1; i++) {
			if (a[i] > a[i + 1]) {
				System.out.println("SorteringsFEIL paa plass: " + i + " a[" + i + "]:" + a[i] + " > a[" + (i + 1) + "]:"
						+ a[i + 1]);
				return;
			}
		}
	}// end simple sorteingstest

	private class worker implements Runnable {

		int fra;

		int til;

		int id;

		int allCountStart;

		int allCountEnd;

		int chunk;

		worker(int id, int fra, int til) {
			this.id = id;
			this.fra = fra;
			this.til = til + 1;
			if (id == (coreCount - 1)) {

				this.til = a.length;

			}

		}

		@Override
		public void run() {
			try {
				radixSort();
			} catch (InterruptedException | BrokenBarrierException e) {

				e.printStackTrace();
			}
		}

		/**
		 * Sort a[] on one digit ; number of bits = maskLen, shiftet up 'shift'
		 * bits
		 * 
		 * @throws BrokenBarrierException
		 * @throws InterruptedException
		 */
		void radixSort() throws InterruptedException, BrokenBarrierException {
			// System.out.println(" radixSort maskLen:"+maskLen+", shift
			// :"+shift);

			int[] count;

			for (int y = 0; y < numDigits; y++) { // numDibits = bit.length
				// b) count=the frequency of each radix value in a
				System.out.println("Steg 1");
				synk.await(); // for for main to prepare the run

				count = new int[mask + 1];

				// System.out.println("Par count length = " + count.length);

				chunk = count.length / coreCount;

				for (int i = fra; i < til; i++) {
					count[(a[i] >>> shift) & mask]++;
				}

				allCount[id] = count;

				System.out.println("Steg 2");
				synk.await(); // traaden er ferdig med tellingen

				System.out.println("Steg 3");
				synk.await();// man has summed sumcount

				// d) move numbers in sorted order a to b

				for (int i = fra; i < til; i++) {

					b[allCount[id][(a[i] >>> shift) & mask]++] = a[i];

				}

				synk.await(); // signal that moving is done

			} // end radixSort
		}
	}

}
