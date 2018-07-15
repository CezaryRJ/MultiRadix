import java.util.Arrays;

public class Timer {

	private double timer = 0;

	double run[] = new double[5];

	int runIndex = 0;

	public void start() {

		timer = System.nanoTime();

	}

	public void done() {
		run[runIndex] = stop();
		System.out.println("Run " + runIndex + " " + run[runIndex] + "ms");
		runIndex++;

	}

	public double getMedian() {

		Arrays.sort(run);

		return run[2];

	}

	public double stop() {

		return ((System.nanoTime() - timer) / 1000000);

	}

	public void reset() {
		timer = 0;
	}
}
