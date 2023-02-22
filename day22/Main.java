package day22;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
	private static Cube cube;
	private static List<String> commands = new ArrayList<>();

	// hardcoded ranges
	private static final int[][] faceRanges = {
			{}
	};

	public static void main(String... args) throws Exception {
		// File f = new File("day22/test.txt");
		File f = new File("day22/input.txt");
		try (Scanner s = new Scanner(f)) {
			parseInput(s);
			System.out.println(executeCommands());
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	private static void parseInput(Scanner s) {
		List<List<Character>> map = new ArrayList<>();
		while (s.hasNextLine()) {
			String line = s.nextLine();
			if (line.equals("")) {
				String movement = s.nextLine();

				int startOfNumber = -1;
				for (int i = 0; i < movement.length(); i++) {
					if (Character.isDigit(movement.charAt(i)) && startOfNumber == -1) {
						startOfNumber = i;
					} else if (!Character.isDigit(movement.charAt(i)) && startOfNumber != -1) {
						commands.add(movement.substring(startOfNumber, i));
						commands.add(movement.substring(i, i + 1));
						startOfNumber = -1;
					} else if (!Character.isDigit(movement.charAt(i))) {
						commands.add(movement.substring(i, i + 1));
					}
				}
				if (startOfNumber != -1) {
					commands.add(movement.substring(startOfNumber, movement.length()));
				}
			}

			List<Character> slice = new ArrayList<>();
			for (char c : line.toCharArray()) {
				slice.add(c);
			}
			map.add(slice);
		}
		cube = Cube.buildCube(map, faceRanges);
	}

	private static int executeCommands() throws Exception {
		Player player = cube.getInitialState();
		for (String command : commands) {
			if (command.charAt(0) == 'R' || command.charAt(0) == 'L') {
				player.turn(command.charAt(0));
			} else {
				player.move(Integer.parseInt(command));
			}
		}
		return 1000 * (player.row + 1) + 4 * (player.col + 1) + player.heading;
	}
}
