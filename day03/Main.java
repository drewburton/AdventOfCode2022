package day03;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {
	public static void main(String... args) {
		// File f = new File("day03/test.txt");
		File f = new File("day03/input.txt");
		try (Scanner s = new Scanner(f)) {
			int priority = 0;
			while (s.hasNextLine()) {
				String[] group = { s.nextLine(), s.nextLine(), s.nextLine() };
				Set<Character> firstRucksack = new HashSet<>();
				Set<Character> commonFirstSecond = new HashSet<>();

				for (int i = 0; i < group[0].length(); i++) {
					firstRucksack.add(group[0].charAt(i));
				}
				for (int i = 0; i < group[1].length(); i++) {
					if (firstRucksack.contains(group[1].charAt(i))) {
						commonFirstSecond.add(group[1].charAt(i));
					}
				}
				for (int i = 0; i < group[2].length(); i++) {
					if (!commonFirstSecond.contains(group[2].charAt(i)))
						continue;

					// found common element, calculate priority
					if (Character.isLowerCase(group[2].charAt(i))) {
						priority += group[2].charAt(i) - 'a' + 1;
						break;
					}
					priority += group[2].charAt(i) - 'A' + 27;
					break;
				}
			}
			System.out.println(priority);
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}
}
