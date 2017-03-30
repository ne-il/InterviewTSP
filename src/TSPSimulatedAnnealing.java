import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TSPSimulatedAnnealing {

	private final int[][] distanceMatrix;
	private final int numberOfVertex;
	private int visitOrder[];
	private int visitOrderBackup[];

	
	private static final double NBITERATION = 100000;
	
	private static final int[][] bigMatrix = new int[][] { 
			{ 000, 374, 200, 223, 108, 178, 252, 285, 240, 356 },
			{ 374, 000, 255, 166, 433, 199, 135, 95, 136, 017 },
			{ 200, 255, 000, 128, 277, 128, 180, 160, 131, 247 },
			{ 223, 166, 128, 000, 430, 047, 052, 84, 040, 155 },
			{ 108, 433, 277, 430, 000, 453, 478, 344, 389, 423 },
			{ 178, 199, 128, 047, 453, 000, 91, 110, 064, 181 },
			{ 252, 135, 180, 052, 478, 91, 000, 114, 83, 117 },
			{ 285, 95, 160, 84, 344, 110, 114, 000, 047, 78 },
			{ 240, 136, 131, 040, 389, 064, 83, 047, 000, 118 },
			{ 356, 017, 247, 155, 423, 181, 117, 78, 118, 000 } };

	public TSPSimulatedAnnealing(int distanceMatrix[][]) {
		Objects.requireNonNull(distanceMatrix);
		this.distanceMatrix = distanceMatrix.clone();
		numberOfVertex = distanceMatrix.length;
		visitOrder = new int[numberOfVertex];
		visitOrderBackup = visitOrder.clone();
	}

	public void generateClosestNeighbourVisit() {
		int nbVisited = 0;
		int src = 0;
		int dest = 0;
		int visited[] = new int[numberOfVertex];

		visited[src] = 1;
		visitOrder[0] = src;
		nbVisited++;

		while (nbVisited < numberOfVertex) {

			int minDistance = Integer.MAX_VALUE;
			for (int i = 0; i < numberOfVertex; i++) {
				if (i != src && visited[i] == 0) {
					if (distanceMatrix[src][i] < minDistance) {
						minDistance = distanceMatrix[src][i];
						dest = i;
					}
				}
			}

			visited[dest] = 1;
			visitOrder[nbVisited] = dest;
			nbVisited++;
			src = dest;
		}
	}

	public int calculDistance() {
		int distance = 0;

		for (int i = 0; i < numberOfVertex - 1; i++) {
			int src = visitOrder[i];
			int dest = visitOrder[i + 1];
			distance += distanceMatrix[src][dest];
		}

		return distance;
	}

	public void generateRandomVisit() {
		ArrayList<Integer> l = (ArrayList<Integer>) IntStream.range(0, numberOfVertex).boxed()
				.collect(Collectors.toList());
		Collections.shuffle(l);
		int i = 0;
		for (Integer integer : l) {
			visitOrder[i] = integer.intValue();
			i++;
		}
	}

	static public void noAnnealing(double nbIteration) {

		TSPSimulatedAnnealing tsp = new TSPSimulatedAnnealing(bigMatrix);
		double bestSolution = 0;

		tsp.generateRandomVisit();
		bestSolution = tsp.calculDistance();

		for (int i = 0; i < nbIteration; i++) {
			tsp.swap();
			double currentSolution = tsp.calculDistance();
			if (currentSolution < bestSolution) {
				bestSolution = currentSolution;
			} else {
				tsp.reverseSwap();
			}
		}
		System.out.println("Best Solution WITHOUT ANNEALING: " + bestSolution);

	};

	public void swap() {
		Random r = new Random();
		int index1 = r.nextInt(numberOfVertex - 1) + 1;
		int index2 = r.nextInt(numberOfVertex - 1) + 1;

		this.visitOrderBackup = visitOrder.clone();
		int tmp = visitOrder[index2];
		visitOrder[index2] = visitOrder[index1];
		visitOrder[index1] = tmp;
	};

	public void reverseSwap() {
		this.visitOrder = this.visitOrderBackup;
	}

	static public void withAnnealing(double temperature, double coolingRate, double nbIteration) {
		TSPSimulatedAnnealing tsp = new TSPSimulatedAnnealing(bigMatrix);
		double bestSolution = 0;

		tsp.generateRandomVisit();
		bestSolution = tsp.calculDistance();

		for (int i = 0; i < nbIteration; i++) {
			if (temperature < 0.1) {
				continue;
			}
			tsp.swap();
			double currentSolution = tsp.calculDistance();
			if (currentSolution < bestSolution) {
				bestSolution = currentSolution;
			} else if (Math.exp((bestSolution - currentSolution) / temperature) < Math.random()) {
				tsp.reverseSwap();
			}
			temperature *= coolingRate;
		}
		System.out.println("Best Solution WITH ANNEALING: " + bestSolution);
	}

	public static void main(String[] args) {

		withAnnealing(10000, 0.97, NBITERATION);

		noAnnealing(NBITERATION);

	}
}
