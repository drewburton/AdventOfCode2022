package day18;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.stream.Stream;

public class Main {
	private static HashSet<Cube> cubes = new HashSet<>();
	private static HashSet<Cube> freeSides = new HashSet<>();
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
		// File f = new File("day18/test.txt");
		File f = new File("day18/input.txt");
		try (Scanner s = new Scanner(f)) {
			mins = new int[] { Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE };
			maxes = new int[] { Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE };
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

			// store all of the free sides in each cube
			for (Cube c : cubes) {
				freeSides.addAll(getFreeSides(c));
			}

			// dfs search to find open areas
			List<HashSet<Cube>> cubeSets = new ArrayList<>();
			while (freeSides.size() > 0) {
				HashSet<Cube> connectedCubes = new HashSet<>();
				getConnectedComponents(freeSides.iterator().next(), connectedCubes);
				cubeSets.add(connectedCubes);
			}

			// remove the largest set, as it will be the exterior points
			int largest = 0;
			int index = 0;
			for (int i = 0; i < cubeSets.size(); i++) {
				if (cubeSets.get(i).size() > largest) {
					largest = cubeSets.get(i).size();
					index = i;
				}
			}
			cubeSets.remove(index);

			// merge enclosed areas into one set of coordinates
			HashSet<Cube> mergedEnclosedAreas = new HashSet<>();
			for (HashSet<Cube> enclosedArea : cubeSets) {
				mergedEnclosedAreas.addAll(enclosedArea);
			}

			// look at all free areas adjacent to cubes, ignore if part of the enclosed sets
			System.out.println(getSurfaceArea(mergedEnclosedAreas));
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	public static List<Cube> getFreeSides(Cube c) {
		List<Cube> adjacentFree = new ArrayList<>();
		Cube test = new Cube(c.x, c.y, c.z);
		for (int[] offset : offsets) {
			test.x += offset[0];
			test.y += offset[1];
			test.z += offset[2];
			if (!cubes.contains(test)) {
				adjacentFree.add(test);
				c.adjacentFree.add(test);
			}
			test = new Cube(c.x, c.y, c.z);
		}
		return adjacentFree;
	}

	public static List<Cube> getNonVisitedAdjacentFree(Cube c, HashSet<Cube> visited) {
		List<Cube> adjacentFree = new ArrayList<>();
		Cube test;
		for (int[] offset : offsets) {
			test = new Cube(c.x, c.y, c.z);
			test.x += offset[0];
			test.y += offset[1];
			test.z += offset[2];
			if (cubes.contains(test)) {
				continue;
			}
			if (visited.contains(test)) {
				continue;
			}
			if (test.x >= mins[0] - 1 &&
					test.x <= maxes[0] + 1 &&
					test.y >= mins[1] - 1 &&
					test.y <= maxes[1] + 1 &&
					test.z >= mins[2] - 1 &&
					test.z <= maxes[2] + 1) {
				adjacentFree.add(test);
			}
		}
		return adjacentFree;
	}

	public static void getConnectedComponents(Cube c, HashSet<Cube> visited) {
		Queue<Cube> nextCubes = new LinkedList<>();
		nextCubes.add(c);
		while (nextCubes.size() > 0) {
			Cube next = nextCubes.remove();
			if (visited.contains(next))
				continue;
			visited.add(next);
			freeSides.remove(next);
			nextCubes.addAll(getNonVisitedAdjacentFree(next, visited));
		}
	}

	public static int getSurfaceArea(HashSet<Cube> enclosedAreas) {
		int surfaceArea = 0;
		for (Cube c : cubes) {
			for (Cube adj : c.adjacentFree) {
				if (!enclosedAreas.contains(adj)) {
					surfaceArea++;
				}
			}
		}
		return surfaceArea;
	}

	static class Cube {
		int x, y, z;
		List<Cube> adjacentFree = new ArrayList<>();

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
