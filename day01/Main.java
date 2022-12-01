package day01;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		File f = new File("day01/input.txt");
		try (Scanner scanner = new Scanner(f)) {
			List<Integer> sortedCalories = scanner.findAll("(?>\\d+\\n)+|\\d+")
					.map(o -> o.group())
					.map(o -> Arrays.stream(o.split("\n"))
							.mapToInt(Integer::parseInt)
							.sum())
					.sorted((a, b) -> b.compareTo(a))
					.toList();
			System.out.println(sortedCalories.get(0) + sortedCalories.get(1) + sortedCalories.get(2));
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}
}