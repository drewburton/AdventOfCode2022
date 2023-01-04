package day21;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
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
			resolveKnownValue(root);
			System.out.println(determineUnknownValue(root, BigInteger.ZERO));
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
			Matcher operationMatcher = Pattern.compile("(\\w+): (\\w+) ([=\\+\\*\\/-]) (\\w+)").matcher(line);
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
				BigInteger result = BigInteger.valueOf(Integer.parseInt(line.substring(6)));
				if (label.equals("humn"))
					result = null;

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

	public static void resolveKnownValue(Monkey monkey) {
		if (monkey.label.equals("humn"))
			return;
		if (monkey.result != null)
			return;
		resolveKnownValue(monkey.m1);
		resolveKnownValue(monkey.m2);
		if (monkey.m1.result == null || monkey.m2.result == null)
			return;
		monkey.result = switch (monkey.operator) {
			case '+' -> monkey.m1.result.add(monkey.m2.result);
			case '-' -> monkey.m1.result.subtract(monkey.m2.result);
			case '*' -> monkey.m1.result.multiply(monkey.m2.result);
			case '/' -> monkey.m1.result.divide(monkey.m2.result);
			default -> null;
		};
	}

	public static BigInteger determineUnknownValue(Monkey monkey, BigInteger shoutDetermination) {
		if (monkey.label.equals("humn")) {
			return shoutDetermination;
		}

		Monkey nullMonkey;
		Monkey nonNullMonkey;
		if (monkey.m1.result == null) {
			nullMonkey = monkey.m1;
			nonNullMonkey = monkey.m2;
		} else {
			nullMonkey = monkey.m2;
			nonNullMonkey = monkey.m1;
		}

		// reverse the operator to solve for what the next level should be
		BigInteger newShoutDetermination = switch (monkey.operator) {
			case '+' -> shoutDetermination.subtract(nonNullMonkey.result);
			case '-' -> shoutDetermination.add(nonNullMonkey.result);
			case '*' -> shoutDetermination.divide(nonNullMonkey.result);
			case '/' -> shoutDetermination.multiply(nonNullMonkey.result);
			case '=' -> nonNullMonkey.result;
			default -> BigInteger.ZERO;
		};

		return determineUnknownValue(nullMonkey, newShoutDetermination);
	}

	private static class Monkey {
		Monkey m1, m2;
		char operator;

		BigInteger result;
		String label;

		Monkey(String label, Monkey m1, Monkey m2, char operator) {
			this.label = label;
			this.m1 = m1;
			this.m2 = m2;
			this.operator = operator;
		}

		Monkey(String label, BigInteger result) {
			this.label = label;
			this.result = result;
		}

		Monkey(String label) {
			this.label = label;
		}
	}
}
