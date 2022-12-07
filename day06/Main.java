import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {
	public static void main(String... args) {
		// File f = new File("day06/test.txt");
		File f = new File("day06/input.txt");
		try (Scanner s = new Scanner(f)) {
			String input = s.nextLine();
			for (int i = 0; i < input.length() - 3; i++) {
				Set<Character> chars = new HashSet<>();
				for (int j = i; j < i + 14; j++) {
					chars.add(input.charAt(j));
				}
				if (chars.size() == 14) {
					System.out.println(i + 14);
					break;
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}
}