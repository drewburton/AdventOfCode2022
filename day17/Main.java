package day17;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class Main {
	private static ArrayList<char[]> grid = new ArrayList<>();
	private static final char[] emptyRow = { '|', '.', '.', '.', '.', '.', '.', '.', '|' };
	private static String jets;
	private static int jetIndex = 0;
	private static Rock[] rocks;
	private static HashMap<State, Data> states = new HashMap<>();

	public static void main(String... args) {
		// File f = new File("day17/test.txt");
		File f = new File("day17/input.txt");
		try (Scanner s = new Scanner(f)) {
			jets = s.nextLine();
			char[] firstRow = { '+', '-', '-', '-', '-', '-', '-', '-', '+' };
			grid.add(firstRow);

			initializeRocks();

			long heightAdded = 0;
			boolean cycleFound = false;
			for (long i = 0; i < 1000000000000l; i++) {
				Data cycle = dropRock(new Rock(rocks[(int) (i % 5)]), i);
				if (cycle != null && !cycleFound) {
					long cycleRocks = i - cycle.numDropped;
					long cycleHeightAdded = grid.size() - 1 - cycle.height;
					// final height = grid.size() - 1 + x * heightAdded
					long numCycles = (1000000000000l - cycle.numDropped) / cycleRocks;
					heightAdded = (numCycles - 1) * cycleHeightAdded;
					i = 1000000000000l - ((1000000000000l - cycle.numDropped) % cycleRocks);
					cycleFound = true;
				}
			}
			removeExtraRows();
			System.out.println(grid.size() - 1 + heightAdded);
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}

	}

	public static void addEmptyRows(Rock rock) {
		int numEmptyRows = 0;
		while (Arrays.compare(grid.get(numEmptyRows), emptyRow) == 0) {
			numEmptyRows++;
		}
		while (numEmptyRows > 3 + rock.height) {
			grid.remove(0);
			numEmptyRows--;
		}
		while (numEmptyRows < 3 + rock.height) {
			grid.add(0, emptyRow.clone());
			numEmptyRows++;
		}
	}

	public static void removeExtraRows() {
		while (Arrays.compare(grid.get(0), emptyRow) == 0) {
			grid.remove(0);
		}
	}

	public static void initializeRocks() {
		Rock r1 = new Rock(new int[] { 0, 0, 0, 0 }, new int[] { 0, 1, 2, 3 });
		Rock r2 = new Rock(new int[] { 0, 1, 1, 1, 2 }, new int[] { 1, 0, 1, 2, 1 });
		Rock r3 = new Rock(new int[] { 0, 1, 2, 2, 2 }, new int[] { 2, 2, 0, 1, 2 });
		Rock r4 = new Rock(new int[] { 0, 1, 2, 3 }, new int[] { 0, 0, 0, 0 });
		Rock r5 = new Rock(new int[] { 0, 0, 1, 1 }, new int[] { 0, 1, 0, 1 });
		rocks = new Rock[] { r1, r2, r3, r4, r5 };
	}

	public static Data dropRock(Rock rock, long numDropped) {
		addEmptyRows(rock);

		rock.move(3, 0);
		jetPush(rock, jetIndex);
		jetIndex = (jetIndex + 1) % jets.length();
		while (canMove(rock, 0, 1)) {
			rock.move(0, 1);
			jetPush(rock, jetIndex);
			jetIndex = (jetIndex + 1) % jets.length();
		}
		for (int i = 0; i < rock.rowCoordinates.length; i++) {
			grid.get(rock.rowCoordinates[i])[rock.columnCoordinates[i]] = '#';
		}
		State cycle = new State(getTopography(), numDropped % 5, jetIndex);
		if (states.get(cycle) != null) {
			return states.get(cycle);
		}
		states.put(cycle, new Data(numDropped, grid.size() - 1));
		return null;
	}

	public static void jetPush(Rock rock, int jetIndex) {
		if (jets.charAt(jetIndex) == '>' && canMove(rock, 1, 0)) {
			rock.move(1, 0);
		} else if (jets.charAt(jetIndex) == '<' && canMove(rock, -1, 0)) {
			rock.move(-1, 0);
		}
	}

	public static boolean canMove(Rock rock, int x, int y) {
		for (int i = 0; i < rock.rowCoordinates.length; i++) {
			if (grid.get(rock.rowCoordinates[i] + y)[rock.columnCoordinates[i] + x] != '.')
				return false;
		}
		return true;
	}

	public static HashSet<Coords> getTopography() {
		removeExtraRows();
		HashSet<Coords> topography = new HashSet<>();
		for (int col = 1; col < grid.get(0).length - 1; col++) {
			int row = 0;
			while (grid.get(row)[col] == '.') {
				row++;
			}
			topography.add(new Coords(row, col));
		}
		return topography;
	}
}

class Rock {
	int[] rowCoordinates;
	int[] columnCoordinates;
	int height;

	Rock(int[] rowCoordinates, int[] columnCoordinates) {
		this.rowCoordinates = rowCoordinates;
		this.columnCoordinates = columnCoordinates;

		int minHeight = Integer.MAX_VALUE;
		int maxHeight = Integer.MIN_VALUE;
		for (int r : rowCoordinates) {
			minHeight = Math.min(minHeight, r);
			maxHeight = Math.max(maxHeight, r);
		}
		height = maxHeight - minHeight + 1;
	}

	Rock(Rock r) {
		rowCoordinates = Arrays.copyOf(r.rowCoordinates, r.rowCoordinates.length);
		columnCoordinates = Arrays.copyOf(r.columnCoordinates, r.columnCoordinates.length);
		height = r.height;
	}

	public void move(int x, int y) {
		for (int i = 0; i < rowCoordinates.length; i++) {
			rowCoordinates[i] += y;
			columnCoordinates[i] += x;
		}
	}
}

record State(HashSet<Coords> topography, long rockIndex, long jetIndex) {
}

class Data {
	long numDropped;
	long height;

	Data(long numDropped, long height) {
		this.numDropped = numDropped;
		this.height = height;
	}
}

class Coords {
	int row, col;

	Coords(int row, int col) {
		this.row = row;
		this.col = col;
	}

	@Override
	public boolean equals(Object o) {
		Coords c = (Coords) o;
		return c.row == row && c.col == col;
	}

	@Override
	public int hashCode() {
		return row > col ? row * col * 31 : -row * col * 31;
	}
}