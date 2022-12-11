package day11;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
	public static void main(String... args) {
		File f = new File("day11/test.txt");
		// File f = new File("day11/input.txt");
		try (Scanner s = new Scanner(f)) {
			ArrayList<Monkey> monkeys = parseMonkeys(s);
			for (int round = 0; round < 10000; round++) {
				for (int monkey = 0; monkey < monkeys.size(); monkey++) {
					Monkey.throwItems(monkeys, monkeys.get(monkey));
				}
				System.out.println(round);
			}
			monkeys.sort((m1, m2) -> m2.numInspected.compareTo(m1.numInspected));
			System.out.println(monkeys.get(0).numInspected.multiply(monkeys.get(1).numInspected));
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	public static ArrayList<Monkey> parseMonkeys(Scanner s) {
		ArrayList<Monkey> monkeys = new ArrayList<>();
		while (true) {
			s.nextLine();
			// parse starting items
			List<BigInteger> items = Stream.of(s.nextLine()
					.replace("  Starting items: ", "")
					.split(", "))
					.map(o -> BigInteger.valueOf(Integer.parseInt(o)))
					.collect(Collectors.toList());

			// parse operation
			Matcher matcher = Pattern.compile("Operation: new = (old|\\d+) (\\*|\\+) (old|\\d+)").matcher(s.nextLine());
			Operation operation = (BigInteger old) -> BigInteger.ZERO;
			if (matcher.find()) {
				operation = (BigInteger old) -> {
					BigInteger first;
					if (matcher.group(1).equals("old")) {
						first = old;
					} else {
						first = BigInteger.valueOf(Integer.parseInt(matcher.group(1)));
					}

					BigInteger second;
					if (matcher.group(3).equals("old")) {
						second = old;
					} else {
						second = BigInteger.valueOf(Integer.parseInt(matcher.group(3)));
					}

					switch (matcher.group(2)) {
						case "*":
							return first.multiply(second);
						case "+":
							return first.add(second);
						default:
							return BigInteger.ZERO;
					}
				};
			}

			// parse test
			int test = Integer.parseInt(s.nextLine().replace("  Test: divisible by ", ""));

			// parse monkey to throw if true
			int trueCase = Integer.parseInt(s.nextLine().replace("    If true: throw to monkey ", ""));

			// parse monkey to throw if false
			int falseCase = Integer.parseInt(s.nextLine().replace("    If false: throw to monkey ", ""));

			Monkey m = new Monkey(new LinkedList<BigInteger>(items), operation, test, trueCase, falseCase);
			monkeys.add(m);
			if (!s.hasNextLine()) {
				break;
			}
			s.nextLine();
		}
		return monkeys;
	}
}

@FunctionalInterface
interface Operation {
	BigInteger calculate(BigInteger old);
}

class Monkey {
	Queue<BigInteger> items;
	Operation operation; // define using lambda
	int test;
	int trueMonkey;
	int falseMonkey;
	BigInteger numInspected;

	public Monkey(Queue<BigInteger> items, Operation operation, int test, int trueMonkey, int falseMonkey) {
		this.items = items;
		this.operation = operation;
		this.test = test;
		this.trueMonkey = trueMonkey;
		this.falseMonkey = falseMonkey;
		numInspected = BigInteger.ZERO;
	}

	public static void throwItems(ArrayList<Monkey> monkeys, Monkey current) {
		while (current.items.size() > 0) {
			current.numInspected = current.numInspected.add(BigInteger.ONE);
			BigInteger item = current.items.remove();
			BigInteger newWorry = current.operation.calculate(item);
			if (newWorry.mod(BigInteger.valueOf(current.test)).equals(BigInteger.ZERO))
				monkeys.get(current.trueMonkey).items.add(newWorry);
			else
				monkeys.get(current.falseMonkey).items.add(newWorry);
		}
	}
}
