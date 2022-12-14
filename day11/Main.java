package day11;

import java.io.File;
import java.io.FileNotFoundException;
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
		// File f = new File("day11/test.txt");
		File f = new File("day11/input.txt");
		try (Scanner s = new Scanner(f)) {
			ArrayList<Monkey> monkeys = parseMonkeys(s);
			for (int round = 0; round < 10000; round++) {
				for (int monkey = 0; monkey < monkeys.size(); monkey++) {
					Monkey.throwItems(monkeys, monkeys.get(monkey));
				}
			}
			monkeys.sort((m1, m2) -> Long.compare(m2.numInspected, m1.numInspected));
			System.out.println(monkeys.get(0).numInspected * monkeys.get(1).numInspected);
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	public static ArrayList<Monkey> parseMonkeys(Scanner s) {
		ArrayList<Monkey> monkeys = new ArrayList<>();
		while (true) {
			s.nextLine();
			// parse starting items
			List<Long> items = Stream.of(s.nextLine()
					.replace("  Starting items: ", "")
					.split(", "))
					.map(o -> Long.parseLong(o))
					.collect(Collectors.toList());

			// parse operation
			Matcher matcher = Pattern.compile("Operation: new = (old|\\d+) (\\*|\\+) (old|\\d+)").matcher(s.nextLine());
			Operation operation = (long old) -> 0;
			if (matcher.find()) {
				int first;
				if (matcher.group(1).equals("old")) {
					first = -1;
				} else {
					first = Integer.parseInt(matcher.group(1));
				}

				int second;
				if (matcher.group(3).equals("old")) {
					second = -1;
				} else {
					second = Integer.parseInt(matcher.group(3));
				}

				switch (matcher.group(2)) {
					case "*":
						if (first == -1 && second == -1) {
							operation = (long old) -> old * old;
						} else if (first == -1) {
							operation = (long old) -> old * second;
						} else if (second == -1) {
							operation = (long old) -> first * old;
						} else {
							operation = (long old) -> first * second;
						}
						break;
					case "+":
						if (first == -1 && second == -1) {
							operation = (long old) -> old + old;
						} else if (first == -1) {
							operation = (long old) -> old + second;
						} else if (second == -1) {
							operation = (long old) -> first + old;
						} else {
							operation = (long old) -> first + second;
						}
						break;
				}
			}
			;

			// parse test
			int test = Integer.parseInt(s.nextLine().replace("  Test: divisible by ", ""));

			// parse monkey to throw if true
			int trueCase = Integer.parseInt(s.nextLine().replace("    If true: throw to monkey ", ""));

			// parse monkey to throw if false
			int falseCase = Integer.parseInt(s.nextLine().replace("    If false: throw to monkey ", ""));

			Monkey m = new Monkey(new LinkedList<Long>(items), operation, test, trueCase, falseCase);
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
	long calculate(long old);
}

class Monkey {
	Queue<Long> items;
	Operation operation; // define using lambda
	int test;
	int trueMonkey;
	int falseMonkey;
	long numInspected;
	static long divisibleFactor = 1;

	public Monkey(Queue<Long> items, Operation operation, int test, int trueMonkey, int falseMonkey) {
		this.items = items;
		this.operation = operation;
		this.test = test;
		divisibleFactor *= test;
		this.trueMonkey = trueMonkey;
		this.falseMonkey = falseMonkey;
		numInspected = 0;
	}

	public static void throwItems(ArrayList<Monkey> monkeys, Monkey current) {
		while (current.items.size() > 0) {
			current.numInspected = current.numInspected + 1;
			long item = current.items.remove();
			long newWorry = current.operation.calculate(item);
			newWorry %= divisibleFactor;
			if (newWorry % current.test == 0)
				monkeys.get(current.trueMonkey).items.add(newWorry);
			else
				monkeys.get(current.falseMonkey).items.add(newWorry);
		}
	}
}
