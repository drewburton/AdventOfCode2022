package day17;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
	private static ArrayList<char[]> grid = new ArrayList<>();
	private static final char[] emptyRow = { '|', '.', '.', '.', '.', '.', '.', '.', '|' };
	private static String jets;
	private static int jetIndex = 0;
	private static Rock[] rocks;

	public static void main(String... args) {
		// File f = new File("day17/test.txt");
		File f = new File("day17/input.txt");
		try (Scanner s = new Scanner(f)) {
			jets = s.nextLine();
			char[] firstRow = { '+', '-', '-', '-', '-', '-', '-', '-', '+' };
			grid.add(firstRow);

			initializeRocks();

			for (int i = 0; i < 2022; i++) {
				dropRock(new Rock(rocks[i % 5]));
			}
			while (Arrays.compare(grid.get(0), emptyRow) == 0) {
				grid.remove(0);
			}
			System.out.println(grid.size() - 1);
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

	public static void initializeRocks() {
		Rock r1 = new Rock(new int[] { 0, 0, 0, 0 }, new int[] { 0, 1, 2, 3 });
		Rock r2 = new Rock(new int[] { 0, 1, 1, 1, 2 }, new int[] { 1, 0, 1, 2, 1 });
		Rock r3 = new Rock(new int[] { 0, 1, 2, 2, 2 }, new int[] { 2, 2, 0, 1, 2 });
		Rock r4 = new Rock(new int[] { 0, 1, 2, 3 }, new int[] { 0, 0, 0, 0 });
		Rock r5 = new Rock(new int[] { 0, 0, 1, 1 }, new int[] { 0, 1, 0, 1 });
		rocks = new Rock[] { r1, r2, r3, r4, r5 };
	}

	public static void dropRock(Rock rock) {
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