package day10;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
	static int cycle = 0;

	public static void main(String... args) {
		// File f = new File("day10/test.txt");
		File f = new File("day10/input.txt");
		try (Scanner s = new Scanner(f)) {
			int[] sprite = new int[] { 0, 1, 2 };
			while (s.hasNextLine()) {
				String[] op = s.nextLine().split(" ");
				String type = op[0];
				int value = 0;
				if (!"noop".equals(type)) {
					handleCycle(sprite);
					value = Integer.parseInt(op[1]);
				}
				handleCycle(sprite);
				sprite = new int[] { sprite[0] + value, sprite[1] + value, sprite[2] + value };
			}
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void handleCycle(int[] sprite) {
		cycle++;
		int cyclePosition = (cycle - 1) % 40;
		if (sprite[0] == cyclePosition || sprite[1] == cyclePosition || sprite[2] == cyclePosition)
			System.out.print("#");
		else
			System.out.print(" ");

		if (cycle % 40 == 0)
			System.out.println();
	}
}
