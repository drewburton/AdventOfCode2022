package day05;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	public static void main(String... args) {
		// File f = new File("day05/test.txt");
		File f = new File("day05/input.txt");
		List<List<Character>> stacks = new ArrayList<>();
		try (Scanner s = new Scanner(f)) {
			// parse create locations
			String line = s.nextLine();
			while (!line.substring(0, 2).equals(" 1")) {
				// string manipulation to replace empty locations with a marker and remove extra
				// info
				line = line.replaceAll("    ", "*").replaceAll(" |\\[|\\]", "");
				for (int i = 0; i < line.length(); i++) {
					if (stacks.size() < i + 1)
						stacks.add(new ArrayList<>());
					if (line.charAt(i) == '*')
						continue;

					// add crates to the bottom of the stack (since reading top down)
					List<Character> currentStack = stacks.get(i);
					currentStack.add(0, line.charAt(i));
					stacks.set(i, currentStack);
				}
				line = s.nextLine();
			}

			// process commands to move crates
			s.nextLine();
			while (s.hasNextLine()) {
				Pattern pattern = Pattern.compile("move (\\d+) from (\\d+) to (\\d+)");
				Matcher matcher = pattern.matcher(s.nextLine());
				if (matcher.find()) {
					int amount = Integer.parseInt(matcher.group(1));
					int src = Integer.parseInt(matcher.group(2));
					int dst = Integer.parseInt(matcher.group(3));

					// remove amount crates from src stack
					List<Character> removalStack = stacks.get(src - 1);
					List<Character> placeholder = new ArrayList<>();
					for (int i = 0; i < amount; i++) {
						placeholder.add(removalStack.remove(removalStack.size() - 1));
					}
					stacks.set(src - 1, removalStack);

					// place amount crates into dst stack (in order of removal)
					List<Character> destinationStack = stacks.get(dst - 1);
					for (int i = amount - 1; i >= 0; i--) {
						destinationStack.add(placeholder.get(i));
					}
					stacks.set(dst - 1, destinationStack);
				}
			}

			// determine which crates are on the top of each stack
			for (int i = 0; i < stacks.size(); i++) {
				System.out.print(stacks.get(i).get(stacks.get(i).size() - 1));
			}
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}
}
