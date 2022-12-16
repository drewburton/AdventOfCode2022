package day14;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
	private static final int PADDING = 500;
	private static char[][] map;
	private static int minX = Integer.MAX_VALUE;
	private static int maxX = Integer.MIN_VALUE;
	private static int maxY = Integer.MIN_VALUE;

	public static void main(String... args) {
		// File f = new File("day14/test.txt");
		File f = new File("day14/input.txt");
		try (Scanner s = new Scanner(f)) {
			ArrayList<Rock> rocks = new ArrayList<>();
			while (s.hasNextLine()) {
				String path = s.nextLine();
				String[] coordinates = path.replaceAll(" ", "").split("->");
				Rock rock = new Rock(Arrays.stream(coordinates)
						.map(o -> {
							String[] split = o.split(",");
							return new int[] { Integer.parseInt(split[0]), Integer.parseInt(split[1]) };
						})
						.toList());
				rocks.add(rock);
				minX = Math.min(minX, rock.minX());
				maxX = Math.max(maxX, rock.maxX());
				maxY = Math.max(maxY, rock.maxY());
			}
			drawRocks(rocks);
			int sandUnits = fillSand();
			System.out.println(sandUnits);
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void drawRocks(List<Rock> rocks) {
		map = new char[maxY + 3][maxX - minX + 3 + PADDING];
		for (int i = 0; i < map.length - 1; i++) {
			for (int j = 0; j < map[i].length; j++) {
				map[i][j] = '.';
			}
		}
		for (int j = 0; j < map[0].length; j++) {
			map[map.length - 1][j] = '#';
		}

		for (Rock rock : rocks) {
			for (int i = 0; i < rock.locations.size() - 1; i++) {
				int startX = rock.locations.get(i)[0] - minX + 1 + (PADDING / 2);
				int startY = rock.locations.get(i)[1];
				int endX = rock.locations.get(i + 1)[0] - minX + 1 + (PADDING / 2);
				int endY = rock.locations.get(i + 1)[1];
				while (startX > endX) {
					map[startY][startX] = '#';
					startX--;
				}
				while (startX < endX) {
					map[startY][startX] = '#';
					startX++;
				}
				while (startY > endY) {
					map[startY][startX] = '#';
					startY--;
				}
				while (startY < endY) {
					map[startY][startX] = '#';
					startY++;
				}
				map[startY][startX] = '#';
			}
		}
	}

	public static int fillSand() {
		try {
			int totalSandUnits = 0;
			while (dropSandUnit()) {
				totalSandUnits++;
			}
			totalSandUnits++;
			return totalSandUnits;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return 0;
	}

	public static boolean dropSandUnit() throws Exception {
		int sandX = 500 - minX + 1 + (PADDING / 2);
		int sandY = 0;
		boolean canMove = true;
		while (canMove) {
			if (sandX == 0 || sandX == map[0].length - 1)
				throw new Exception("Increase map size");

			if (map[sandY + 1][sandX] == '.') {
				sandY++;
			} else if (map[sandY + 1][sandX - 1] == '.') {
				sandY++;
				sandX--;
			} else if (map[sandY + 1][sandX + 1] == '.') {
				sandY++;
				sandX++;
			} else {
				canMove = false;
			}
		}
		if (sandY == 0 && sandX == 500 - minX + 1 + (PADDING / 2)) {
			map[sandY][sandX] = '+';
			return false;
		}

		map[sandY][sandX] = '+';
		return true;
	}

	public static void printMap() {
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				System.out.print(map[i][j]);
			}
			System.out.println();
		}
	}
}

class Rock {
	List<int[]> locations;

	Rock(List<int[]> locations) {
		this.locations = locations;
	}

	public int minX() {
		int min = Integer.MAX_VALUE;
		for (int[] location : locations) {
			if (location[0] < min)
				min = location[0];
		}
		return min;
	}

	public int maxX() {
		int max = Integer.MIN_VALUE;
		for (int[] location : locations) {
			if (location[0] > max)
				max = location[0];
		}
		return max;
	}

	public int minY() {
		int min = Integer.MAX_VALUE;
		for (int[] location : locations) {
			if (location[1] < min)
				min = location[1];
		}
		return min;
	}

	public int maxY() {
		int max = Integer.MIN_VALUE;
		for (int[] location : locations) {
			if (location[1] > max)
				max = location[1];
		}
		return max;
	}
}
