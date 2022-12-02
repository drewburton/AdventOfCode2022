package day02;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
	public static void main(String... args) {
		// File f = new File("day02/test.txt");
		File f = new File("day02/input.txt");
		HashMap<String, Integer> points = new HashMap<>();
		points.put("A X", 3);
		points.put("A Y", 4);
		points.put("A Z", 8);
		points.put("B X", 1);
		points.put("B Y", 5);
		points.put("B Z", 9);
		points.put("C X", 2);
		points.put("C Y", 6);
		points.put("C Z", 7);

		// rock: A = 1
		// paper: B = 2
		// scissors: C = 3
		// X = lose 0
		// Y = Tie 3
		// Z = Win 6
		try (Scanner s = new Scanner(f)) {
			int totalScore = s.findAll("\\w \\w")
					.mapToInt(o -> points.get(o.group()))
					.sum();
			System.out.println(totalScore);
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}
}
