package day13;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	public static void main(String... args) {
		// File f = new File("day13/test.txt");
		File f = new File("day13/input.txt");
		try (Scanner s = new Scanner(f)) {
			ArrayList<Packet> packets = new ArrayList<>();
			while (s.hasNextLine()) {
				String unparsed = s.nextLine();
				if (unparsed.equals(""))
					continue;
				unparsed = unparsed.substring(1, unparsed.length() - 1);
				packets.add(parsePacket(unparsed));
			}
			Packet two = parsePacket("[2]");
			Packet six = parsePacket("[6]");
			packets.add(two);
			packets.add(six);

			packets.sort((p1, p2) -> inOrder(p2, p1));
			packets.forEach((p) -> {
				printPacket(p);
				System.out.println();
			});
			int twoIndex = -1, sixIndex = -1;
			for (int i = 0; i < packets.size(); i++) {
				if (packets.get(i) == two)
					twoIndex = i + 1;
				if (packets.get(i) == six)
					sixIndex = i + 1;
			}
			System.out.println(twoIndex * sixIndex);
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void printPacket(Packet p) {
		if (p.value != -1) {
			System.out.print(p.value + " ");
			return;
		}
		System.out.print("[");
		for (int i = 0; i < p.subLists.size(); i++) {
			printPacket(p.subLists.get(i));
		}
		System.out.print("]");
	}

	public static Packet parsePacket(String unparsedInput) {
		Packet packet = new Packet();
		Packet current = packet;
		int startOfNumber = -1;
		for (int i = 0; i < unparsedInput.length(); i++) {
			if (!Character.isDigit(unparsedInput.charAt(i)) && startOfNumber != -1) {
				Packet sub = new Packet();
				sub.value = Integer.parseInt(unparsedInput.substring(startOfNumber, i));
				sub.parent = current;
				current.subLists.add(sub);
				startOfNumber = -1;
			}

			if (unparsedInput.charAt(i) == '[') {
				Packet sub = new Packet();
				sub.parent = current;
				current.subLists.add(sub);
				current = sub;
			} else if (Character.isDigit(unparsedInput.charAt(i))) {
				if (startOfNumber == -1)
					startOfNumber = i;
			} else if (unparsedInput.charAt(i) == ']') {
				current = current.parent;
			}
		}
		if (startOfNumber != -1) {
			Packet sub = new Packet();
			sub.value = Integer.parseInt(unparsedInput.substring(startOfNumber, unparsedInput.length()));
			sub.parent = current;
			current.subLists.add(sub);
		}
		return packet;
	}

	public static int inOrder(Packet left, Packet right) {
		Packet leftTemp = new Packet(left);
		Packet rightTemp = new Packet(right);
		int i = 0, j = 0;
		while (i < leftTemp.subLists.size() && j < rightTemp.subLists.size()) {
			if (leftTemp.subLists.get(i).value == -1 && rightTemp.subLists.get(j).value == -1) {
				// both lists
				int solved = inOrder(leftTemp.subLists.get(i), rightTemp.subLists.get(j));
				if (solved != 0)
					return solved;
			} else if (leftTemp.subLists.get(i).value != -1 && rightTemp.subLists.get(j).value != -1) {
				// both integers
				if (leftTemp.subLists.get(i).value != rightTemp.subLists.get(j).value)
					return Integer.compare(rightTemp.subLists.get(j).value, leftTemp.subLists.get(i).value);
			} else {
				// one integer
				if (leftTemp.subLists.get(i).value != -1)
					convertToList(leftTemp.subLists.get(i));
				else
					convertToList(rightTemp.subLists.get(j));
				continue;
			}
			i++;
			j++;
		}
		if (i >= leftTemp.subLists.size() && j >= rightTemp.subLists.size())
			return 0;
		return i >= leftTemp.subLists.size() ? 1 : -1; // if leftTemp runs out of items first it is in order
	}

	public static void convertToList(Packet p) {
		Packet sub = new Packet();
		sub.parent = p;
		sub.value = p.value;
		p.subLists.add(sub);
		p.value = -1;
	}
}

class Packet {
	ArrayList<Packet> subLists;
	int value;
	Packet parent;

	Packet() {
		subLists = new ArrayList<>();
		value = -1;
		parent = null;
	}

	Packet(Packet p) {
		subLists = new ArrayList<>();
		for (Packet subP : p.subLists) {
			subLists.add(new Packet(subP));
		}
		this.value = p.value;
		if (parent != null)
			this.parent = new Packet(p.parent);
	}
}