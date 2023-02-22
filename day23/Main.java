package day23;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;

public class Main {
	private static HashSet<Elf> elves;

	public static void main(String... args) {
		File f = new File("day23/test.txt");
		// File f = new File("day23/input.txt");
		try (Scanner s = new Scanner(f)) {
			parseInput(s);
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	private static void parseInput(Scanner s) {
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

	private static void proposeMoves(int startingDirection) {

	}

	private static void updatePositions() {

	}

	private static void countEmptyTiles() {

	}
}

class Elf {
	int row, col;

	Elf(int row, int col) {
		this.row = row;
		this.col = col;
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