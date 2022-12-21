package day18;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class Main {
	private static HashSet<Cube> cubes = new HashSet<>();
	private static HashSet<Cube> freeSides = new HashSet<>();
	private static HashSet<Cube> visitedCubes = new HashSet<>();
	private static int[] mins;
	private static int[] maxes;

	private static int[][] offsets = {
			{ 1, 0, 0 },
			{ -1, 0, 0 },
			{ 0, 1, 0 },
			{ 0, -1, 0 },
			{ 0, 0, 1 },
			{ 0, 0, -1 }
	};

	public static void main(String... args) {
		File f = new File("day18/test.txt");
		// File f = new File("day18/input.txt");
		try (Scanner s = new Scanner(f)) {
			mins = new int[] { Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE };
			maxes = new int[] { Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE };
			;
			while (s.hasNextLine()) {
				int[] coords = Stream.of(s.nextLine().split(",")).mapToInt(Integer::parseInt).toArray();
				Cube c = new Cube(coords[0], coords[1], coords[2]);
				mins[0] = Math.min(coords[0], mins[0]);
				mins[1] = Math.min(coords[1], mins[1]);
				mins[2] = Math.min(coords[2], mins[2]);

				maxes[0] = Math.max(coords[0], maxes[0]);
				maxes[1] = Math.max(coords[1], maxes[1]);
				maxes[2] = Math.max(coords[2], maxes[2]);
				cubes.add(c);
			}
			for (Cube c : cubes) {
				freeSides.addAll(getFreeSides(c));
			}
			while (freeSides.size() > 0) {
				System.out.println(getSurfaceArea(freeSides.iterator().next()));
			}
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	public static List<Cube> getFreeSides(Cube c) {
		List<Cube> adjacentFree = new ArrayList<>();
		int[][] offsets = {
				{ 1, 0, 0 },
				{ -1, 0, 0 },
				{ 0, 1, 0 },
				{ 0, -1, 0 },
				{ 0, 0, 1 },
				{ 0, 0, -1 }
		};
		Cube test = new Cube(c.x, c.y, c.z);
		for (int[] offset : offsets) {
			test.x += offset[0];
			test.y += offset[1];
			test.z += offset[2];
			if (!cubes.contains(test)) {
				adjacentFree.add(test);
			}
			test = new Cube(c.x, c.y, c.z);
		}
		return adjacentFree;
	}

	public static List<Cube> getNonVisitedAdjacentFree(Cube c) {
		List<Cube> adjacentFree = new ArrayList<>();
		int[][] offsets = {
				{ 1, 0, 0 },
				{ -1, 0, 0 },
				{ 0, 1, 0 },
				{ 0, -1, 0 },
				{ 0, 0, 1 },
				{ 0, 0, -1 }
		};
		Cube test = new Cube(c.x, c.y, c.z);
		for (int[] offset : offsets) {
			test.x += offset[0];
			test.y += offset[1];
			test.z += offset[2];
			if (cubes.contains(test)) {
				test = new Cube(c.x, c.y, c.z);
				continue;
			}
			if (visitedCubes.contains(test)) {
				test = new Cube(c.x, c.y, c.x);
				continue;
			}
			if (test.x >= mins[0] - 1 &&
					test.x <= maxes[0] + 1 &&
					test.y >= mins[1] - 1 &&
					test.y <= maxes[1] + 1 &&
					test.z >= mins[2] - 1 &&
					test.z <= maxes[2] + 1)
				adjacentFree.add(test);

			test = new Cube(c.x, c.y, c.z);
		}
		return adjacentFree;
	}

	public static int getSurfaceArea(Cube c) {
		if (visitedCubes.contains(c))
			return 1;
		visitedCubes.add(c);
		freeSides.remove(c);
		int sum = 0;
		for (Cube adjacentFree : getNonVisitedAdjacentFree(c)) {
			sum += getSurfaceArea(adjacentFree);
		}
		return sum;
	}

	static class Cube {
		int x, y, z;

		Cube(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		Cube(Cube c) {
			x = c.x;
			y = c.y;
			z = c.z;
		}

		@Override
		public boolean equals(Object o) {
			Cube c = (Cube) o;
			return c.x == x && c.y == y && c.z == z;
		}

		@Override
		public int hashCode() {
			return x * 7 + y * 13 + z * 31;
		}
	}
}
