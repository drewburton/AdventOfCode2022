package day21;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

	public static void main(String... args) throws Exception {
		// File f = new File("day21/test.txt");
		File f = new File("day21/input.txt");
		try (Scanner s = new Scanner(f)) {
			Monkey root = parseInput(s);
			System.out.println(getResult(root));
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Parses input to create tree of monkey operations
	 * 
	 * @param s
	 * @return root of the tree
	 */
	public static Monkey parseInput(Scanner s) {
		ArrayList<Monkey> monkeys = new ArrayList<>();
		Monkey root = null;
		while (s.hasNextLine()) {
			String line = s.nextLine();
			Matcher operationMatcher = Pattern.compile("(\\w+): (\\w+) ([\\+\\*\\/-]) (\\w+)").matcher(line);
			if (operationMatcher.find()) { // the monkey is shouting an operation
				String label = operationMatcher.group(1);
				String m1Label = operationMatcher.group(2);
				String m2Label = operationMatcher.group(4);
				char operator = operationMatcher.group(3).charAt(0);

				// retrieve this monkey and its sub monkey if they have been parsed already
				Monkey m = null, m1 = null, m2 = null;
				for (Monkey monkey : monkeys) {
					if (monkey.label.equals(m1Label)) {
						m1 = monkey;
					} else if (monkey.label.equals(m2Label)) {
						m2 = monkey;
					} else if (monkey.label.equals(label)) {
						m = monkey;
					}
				}

				// if the sub monkeys haven't been found, create a reference for later
				if (m1 == null) {
					m1 = new Monkey(m1Label);
					monkeys.add(m1);
				}
				if (m2 == null) {
					m2 = new Monkey(m2Label);
					monkeys.add(m2);
				}

				// if this monkey has already been created, pull the reference and update;
				// otherwise, create a new monkey with the parsed info
				if (m != null) {
					m.m1 = m1;
					m.m2 = m2;
					m.operator = operator;
				} else {
					m = new Monkey(label, m1, m2, operator);
					monkeys.add(m);
				}

				// store the root when we come across it for returning
				if (m.label.equals("root")) {
					root = m;
				}
			} else { // the monkey is shouting a value
				String label = line.substring(0, 4);
				int result = Integer.parseInt(line.substring(6));

				// retrieve this monkey if it has been parsed already
				Monkey m = null;
				for (Monkey monkey : monkeys) {
					if (monkey.label.equals(label)) {
						m = monkey;
					}
				}

				// if this monkey has already been created, pull the reference and update;
				// otherwise, create a new monkey with the parsed info
				if (m != null) {
					m.result = result;
				} else {
					m = new Monkey(label, result);
					monkeys.add(m);
				}
			}
		}
		return root;
	}

	public static long getResult(Monkey monkey) throws Exception {
		if (monkey.result != null)
			return monkey.result;
		return switch (monkey.operator) {
			case '+' -> getResult(monkey.m1) + getResult(monkey.m2);
			case '-' -> getResult(monkey.m1) - getResult(monkey.m2);
			case '*' -> getResult(monkey.m1) * getResult(monkey.m2);
			case '/' -> getResult(monkey.m1) / getResult(monkey.m2);
			default -> throw new Exception("missing operator on monkey");
		};
	}

	private static class Monkey {
		Monkey m1, m2;
		char operator;

		Integer result;
		String label;

		Monkey(String label, Monkey m1, Monkey m2, char operator) {
			this.label = label;
			this.m1 = m1;
			this.m2 = m2;
			this.operator = operator;
		}

		Monkey(String label, int result) {
			this.label = label;
			this.result = result;
		}

		Monkey(String label) {
			this.label = label;
		}
	}
}
