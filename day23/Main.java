package day23;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;

public class Main {
	private static HashSet<Elf> elves;
	private static HashMap<Elf, Elf> proposals; // proposed location, current location

	public static void main(String... args) throws Exception {
		//File f = new File("C:\\users\\ABURTO57\\dev\\AdventOfCode2022\\day23\\test.txt");
		File f = new File("C:\\users\\ABURTO57\\dev\\AdventOfCode2022\\day23\\input.txt");
		try (Scanner s = new Scanner(f)) {
			parseInput(s);
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}

		for (int i = 0; i < 10; i++) {
			performRound(i % 4);
		}
		System.out.println(countEmptyTiles());
	}

	private static void parseInput(Scanner s) {
		elves = new HashSet<>();
		int r = 0;
		while (s.hasNextLine()) {
			String line = s.nextLine();
			for (int c = 0; c < line.length(); c++) {
				if (line.charAt(c) == '#')
					elves.add(new Elf(r, c));
			}
			r++;
		}
	}

	private static void performRound(int startingDirection) throws Exception {
		proposeMoves(startingDirection);
		updatePositions();

		// printBoard();
		// System.out.println();
	}


	private static void proposeMoves(int startingDirection) throws Exception {
		proposals = new HashMap<>();
		for (Elf e : elves) {
			// check all areas surrounding elf
			boolean alone = true;
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					if (i == 0 && j == 0)
						continue;
					if (elves.contains(new Elf(e.row + i, e.col + j))) {
						alone = false;
						break;
					}
				}
			}
			// do not move if elf has no surroudning elves
			if (alone)
				continue;

			// check all four proposal directions
			ArrayList<Elf> proposalsToRemove = new ArrayList<>();
			testDirections:
			for (int i = 0; i < 4; i++) {
				int direction = (i + startingDirection) % 4;
				switch (direction) {
					case 0:
						if (!elves.contains(new Elf(e.row - 1, e.col - 1)) &&
							!elves.contains(new Elf(e.row - 1, e.col)) &&
							!elves.contains(new Elf(e.row - 1, e.col + 1))) {
							proposeLocation(new Elf(e.row - 1, e.col), e, proposalsToRemove);
							break testDirections;
						}
						break;
					case 1:
						if (!elves.contains(new Elf(e.row + 1, e.col - 1)) &&
							!elves.contains(new Elf(e.row + 1, e.col)) &&
							!elves.contains(new Elf(e.row + 1, e.col + 1))) {
							proposeLocation(new Elf(e.row + 1, e.col), e, proposalsToRemove);
							break testDirections;
						}
						break;
					case 2:
						if (!elves.contains(new Elf(e.row - 1, e.col - 1)) &&
							!elves.contains(new Elf(e.row, e.col - 1)) &&
							!elves.contains(new Elf(e.row + 1, e.col - 1))) {
							proposeLocation(new Elf(e.row, e.col - 1), e, proposalsToRemove);
							break testDirections;
						}
						break;
					case 3:
						if (!elves.contains(new Elf(e.row - 1, e.col + 1)) &&
							!elves.contains(new Elf(e.row, e.col + 1)) &&
							!elves.contains(new Elf(e.row + 1, e.col + 1))) {
							proposeLocation(new Elf(e.row, e.col + 1), e, proposalsToRemove);
							break testDirections;
						}
						break;
					default:
						throw new Exception("Invalid direction");
				}	
			}

			// remove proposals where mutliple elves proposed the same location
			for (Elf remove : proposalsToRemove) {
				proposals.remove(remove);
			}
		}
	}

	private static void proposeLocation(Elf proposed, Elf current, ArrayList<Elf> propsToRemove) {
		if (proposals.containsKey(proposed)) {
			// need to save rather than remove in case more than two elves propose same location
			propsToRemove.add(proposed);
		} else {
			// if first elf to propose there, add to map
			proposals.put(new Elf(proposed), new Elf(current));
		}
	}

	private static void updatePositions() {
		// moves all elves to proposed locations
		for (Map.Entry<Elf, Elf> entry : proposals.entrySet()) {
			Elf e = entry.getValue();
			Elf prop = entry.getKey();
			elves.remove(e);
			elves.add(prop);
		}
	}

	private static int countEmptyTiles() {
		int count = 0;
		int[] sizes = getAreaSize();
		for (int i = sizes[0]; i <= sizes[1]; i++) {
			for (int j = sizes[2]; j <= sizes[3]; j++) {
				if (!elves.contains(new Elf(i, j))) {
					count++;
				}
			}
		}
		return count;
	}

	private static int[] getAreaSize() {
		int minR = Integer.MAX_VALUE;
		int maxR = Integer.MIN_VALUE;
		int minC = Integer.MAX_VALUE;
		int maxC = Integer.MIN_VALUE;

		for (Elf e : elves) {
			if (e.row < minR) minR = e.row;
			if (e.row > maxR) maxR = e.row;
			if (e.col < minC) minC = e.col;
			if (e.col > maxC) maxC = e.col;
		}

		return new int[]{ minR, maxR, minC, maxC };
	}

	private static void printBoard() {
		int[] sizes = getAreaSize();
		for (int i = sizes[0] - 1; i <= sizes[1] + 1; i++) {
			for (int j = sizes[2] - 1; j <= sizes[3] + 1; j++) {
				if (elves.contains(new Elf(i, j))) {
					System.out.print("#");
				} else {
					System.out.print(".");
				}
			}
			System.out.println();
		}
	}
}

class Elf {
	int row, col;
	//int prevRow, prevCol;

	Elf(int row, int col) {
		this.row = row;
		this.col = col;
	}

	Elf(Elf e) {
		this.row = e.row;
		this.col = e.col;
	}

	@Override
	public boolean equals(Object o) {
		Elf e = (Elf) o;
		if (e.row == row && e.col == col)
			return true;
		return false;
	}

	@Override
	public int hashCode() {
		return row * 7 + col * 31;
	}
}