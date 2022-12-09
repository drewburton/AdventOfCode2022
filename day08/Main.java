package day08;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
	public static void main(String... args) {
		// File f = new File("day08/test.txt");
		File f = new File("day08/input.txt");
		try (Scanner s = new Scanner(f)) {
			ArrayList<ArrayList<Integer>> trees = new ArrayList<>();
			while (s.hasNextLine()) {
				trees.add(Arrays.stream(s.nextLine().split("")).map(Integer::parseInt)
						.collect(Collectors.toCollection(ArrayList::new)));
			}

			solveVisibility(trees);
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void solveVisibility(ArrayList<ArrayList<Integer>> trees) {
		int maxScore = 0;
		for (int i = 1; i < trees.size(); i++) {
			for (int j = 1; j < trees.size(); j++) {
				int left = GetNeighbor(trees, i, j, i, j - 1);
				int up = GetNeighbor(trees, i, j, i - 1, j);
				int right = GetNeighbor(trees, i, j, i, j + 1);
				int down = GetNeighbor(trees, i, j, i + 1, j);

				int score = left * right * up * down;
				maxScore = score > maxScore ? score : maxScore;
			}
		}
		System.out.println(maxScore);
	}

	public static int GetNeighbor(ArrayList<ArrayList<Integer>> trees, int i, int j,
			int i2, int j2) {
		while (i2 < trees.size() && j2 < trees.size() && i2 >= 0 && j2 >= 0 &&
				trees.get(i).get(j) > trees.get(i2).get(j2)) {
			int[] nextCoords = getNextCoordinates(i, j, i2, j2);
			i2 = nextCoords[0];
			j2 = nextCoords[1];
		}
		if (i2 == trees.size() || j2 == trees.size() || i2 < 0 || j2 < 0)
			return Math.max(Math.abs(i2 - i), Math.abs(j2 - j)) - 1;
		return Math.max(Math.abs(i2 - i), Math.abs(j2 - j));
	}

	public static int[] getNextCoordinates(int i, int j, int i2, int j2) {
		if (i2 > i)
			return new int[] { i2 + 1, j2 };
		if (i2 < i)
			return new int[] { i2 - 1, j2 };
		if (j2 > j)
			return new int[] { i2, j2 + 1 };
		return new int[] { i2, j2 - 1 };
	}
}