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
			int i = 1;
			int sumIndeciesOfRightOrder = 0;
			do {
				String leftUnparsed = s.nextLine();
				leftUnparsed = leftUnparsed.substring(1, leftUnparsed.length() - 1);
				Packet left = parsePacket(leftUnparsed);

				String rightUnparsed = s.nextLine();
				rightUnparsed = rightUnparsed.substring(1, rightUnparsed.length() - 1);
				Packet right = parsePacket(rightUnparsed);

				if (inOrder(left, right) == 1) {
					sumIndeciesOfRightOrder += i;
					System.out.println(i);
				}
				i++;
			} while (s.hasNextLine() && s.nextLine().equals(""));
			System.out.println(sumIndeciesOfRightOrder);
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
		return packet;
	}

	public static int inOrder(Packet left, Packet right) {
		int i = 0, j = 0;
		while (i < left.subLists.size() && j < right.subLists.size()) {
			if (left.subLists.get(i).value == -1 && right.subLists.get(j).value == -1) {
				// both lists
				int solved = inOrder(left.subLists.get(i), right.subLists.get(j));
				if (solved != 0)
					return solved;
			} else if (left.subLists.get(i).value != -1 && right.subLists.get(j).value != -1) {
				// both integers
				if (left.subLists.get(i).value != right.subLists.get(j).value)
					return Integer.compare(right.subLists.get(j).value, left.subLists.get(i).value);
			} else {
				// one integer
				if (left.subLists.get(i).value != -1)
					convertToList(left.subLists.get(i));
				else
					convertToList(right.subLists.get(j));
				continue;
			}
			i++;
			j++;
		}
		if (i >= left.subLists.size() && j >= right.subLists.size())
			return 0;
		return i >= left.subLists.size() ? 1 : -1; // if left runs out of items first it is in order
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
}