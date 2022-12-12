package day12;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Main {
	static ArrayList<ArrayList<Integer>> elevationMap = new ArrayList<>();
	static PriorityQueue<Location> closestLocations;
	static ArrayList<ArrayList<Location>> optimalStepTable;

	public static void main(String... args) {
		// File f = new File("day12/test.txt");
		File f = new File("day12/input.txt");
		try (Scanner s = new Scanner(f)) {
			ArrayList<Location> startingLocations = new ArrayList<>();
			int endingRow = 0, endingColumn = 0;
			while (s.hasNextLine()) {
				ArrayList<Integer> row = new ArrayList<>();
				String[] chars = s.nextLine().split("");
				for (int i = 0; i < chars.length; i++) {
					if (chars[i].charAt(0) == 'S' || chars[i].charAt(0) == 'a') {
						int startingRow = elevationMap.size();
						int startingColumn = i;
						Location start = new Location(startingRow, startingColumn, 0);
						startingLocations.add(start);
						row.add(0);
					} else if (chars[i].charAt(0) == 'E') {
						endingRow = elevationMap.size();
						endingColumn = i;
						row.add('z' - 'a');
					} else
						row.add(chars[i].charAt(0) - 'a');
				}
				elevationMap.add(row);
			}

			int minSteps = Integer.MAX_VALUE;
			for (Location l : startingLocations) {
				int stepsToEnd = dijkstras(l, endingRow, endingColumn);
				if (stepsToEnd < minSteps)
					minSteps = stepsToEnd;
			}
			System.out.println(minSteps);
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	public static int dijkstras(Location startingLocation, int endingRow, int endingColumn) {
		optimalStepTable = new ArrayList<>();
		closestLocations = new PriorityQueue<>();
		for (int i = 0; i < elevationMap.size(); i++) {
			ArrayList<Location> row = new ArrayList<>();
			for (int j = 0; j < elevationMap.get(i).size(); j++) {
				row.add(new Location(i, j, Integer.MAX_VALUE));
			}
			optimalStepTable.add(row);
		}
		optimalStepTable.get(startingLocation.i).get(startingLocation.j).steps = 0;
		closestLocations.add(optimalStepTable.get(startingLocation.i).get(startingLocation.j));
		while (closestLocations.size() > 0) {
			visitNeighbors();
		}
		return optimalStepTable.get(endingRow).get(endingColumn).steps;
	}

	public static void visitNeighbors() {
		Location current = closestLocations.remove();
		current.visited = true;
		int i = current.i;
		int j = current.j;
		handleNeighbor(current, i, j, i - 1, j);
		handleNeighbor(current, i, j, i + 1, j);
		handleNeighbor(current, i, j, i, j - 1);
		handleNeighbor(current, i, j, i, j + 1);
	}

	public static void handleNeighbor(Location current, int i, int j, int i2, int j2) {
		if (isValid(i, j, i2, j2) && optimalStepTable.get(i2).get(j2).visited == false) {
			if (current.steps + 1 < optimalStepTable.get(i2).get(j2).steps) {
				optimalStepTable.get(i2).get(j2).steps = current.steps + 1;
				closestLocations.add(optimalStepTable.get(i2).get(j2));
			}
		}
	}

	public static boolean isValid(int i, int j, int i2, int j2) {
		if (i2 < 0 || i2 >= optimalStepTable.size() || j2 < 0 || j2 >= optimalStepTable.get(i).size())
			return false;
		return elevationMap.get(i2).get(j2) <= elevationMap.get(i).get(j) + 1;
	}

	public static void printOptimalSolutionTable() {
		for (var row : optimalStepTable) {
			for (var location : row) {
				System.out.print(location.steps + " ");
			}
			System.out.println();
		}
	}
}

class Location implements Comparable<Location> {
	int i, j, steps;
	boolean visited;

	Location(int i, int j, int steps) {
		this.i = i;
		this.j = j;
		this.steps = steps;
		visited = false;
	}

	@Override
	public int compareTo(Location l) {
		if (steps > l.steps)
			return 1;
		if (steps < l.steps)
			return -1;
		return 0;
	}

	@Override
	public String toString() {
		return steps + " ";
	}
}
