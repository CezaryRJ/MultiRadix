
public class FinnStorste {

	int[] numbers;
	int workerAreaSize;
	int sortedAreaSize;

	int tmp;

	Thread[] threads;

	FinnStorste(int[] numbers, int sortedAreaSize) {

		this.numbers = numbers;
		this.sortedAreaSize = sortedAreaSize;

		threads = new Thread[Runtime.getRuntime().availableProcessors()];

		workerAreaSize = numbers.length / threads.length;
	}
	
	public int getMax(){
		sort();
		return numbers[0];
	}

	void sort() {

		int workerStart = 0;
		int workerEnd = workerAreaSize - 1;
		for (int i = 0; i < threads.length - 1; i++) {// preapre the threads

			threads[i] = new Thread(new worker(numbers, sortedAreaSize, workerStart, workerEnd));

			workerStart = workerEnd + 1;
			workerEnd += workerAreaSize;

		}
		// last thread need the pick up the slack left over by uneven division
		threads[threads.length - 1] = new Thread(new worker(numbers, sortedAreaSize, workerStart, numbers.length - 1));

		for (int i = 0; i < threads.length; i++) {
			threads[i].start();
		}
		for (int i = 0; i < threads.length; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		int placeOnIndex = sortedAreaSize;

		for (int i = 1; i < threads.length; i++) {
			for (int x = (workerAreaSize * i); x < (workerAreaSize * i) + sortedAreaSize; x++) {
				tmp = numbers[placeOnIndex];
				numbers[placeOnIndex] = numbers[x];
				numbers[x] = tmp;
				placeOnIndex++;
			}
		}

		insertSort(numbers, 0, (sortedAreaSize * threads.length));

	}

	private class worker implements Runnable {

		int[] numbers;
		int start;
		int end;
		int sortedAreaSize;

		int tmp;

		worker(int[] numbers, int sortedAreaSize, int start, int end) {

			this.numbers = numbers;
			this.sortedAreaSize = sortedAreaSize;
			this.start = start;
			this.end = end;
		}

		public void run() {

			insertSort(numbers, start, (start + sortedAreaSize));

			for (int i = (start + sortedAreaSize); i < (end + 1); i++) {

				if (numbers[i] > numbers[(start + sortedAreaSize - 1)]) {
					tmp = numbers[(start + sortedAreaSize - 1)];
					numbers[(start + sortedAreaSize - 1)] = numbers[i];
					numbers[i] = tmp;
					insertOneElement(numbers, start, (start + sortedAreaSize - 1));
				}
			}

		}

		void insertOneElement(int[] a, int start, int end) {

			int t, i;
			t = a[end];
			i = end - 1;
			while (i >= start && a[i] < t) {
				a[i + 1] = a[i];
				i--;
			}
			a[i + 1] = t;
		}

	}

	/** Denne sorterer a[v..h] i stigende rekkefolge med innstikk-algoritmen */
	void insertSort(int[] a, int v, int h) {
		int i, t;
		for (int k = v; k < h; k++) {
			// invariant: a[v..k] er naa sortert stigende (minste forst)
			t = a[k + 1];
			i = k;
			while (i >= v && a[i] < t) {
				a[i + 1] = a[i];
				i--;
			}
			a[i + 1] = t;
		} // end for k
	} // end insertSort

}