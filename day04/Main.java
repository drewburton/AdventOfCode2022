package day04;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	public static void main(String... args) {
		// File f = new File("day04/test.txt");
		File f = new File("day04/input.txt");
		try (Scanner s = new Scanner(f)) {
			int containedPairs = 0;
			while (s.hasNextLine()) {
				Pattern pattern = Pattern.compile("(\\d+)-(\\d+),(\\d+)-(\\d+)");
				Matcher matcher = pattern.matcher(s.nextLine());
				if (matcher.find()) {
					int start1 = Integer.parseInt(matcher.group(1));
					int end1 = Integer.parseInt(matcher.group(2));
					int start2 = Integer.parseInt(matcher.group(3));
					int end2 = Integer.parseInt(matcher.group(4));
					if (start1 > end2 || end1 < start2) // continue if groups don't overlap
						continue;
					containedPairs++;
				}
			}
			System.out.println(containedPairs);
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}
}
