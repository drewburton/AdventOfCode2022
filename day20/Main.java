package day20;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

public class Main {
	private static List<Long> encryptedCoordinates = new ArrayList<>();
	private static HashSet<Mapping> locationMappings;

	public static void main(String... args) {
		// File f = new File("day20/test.txt");
		File f = new File("day20/input.txt");
		try (Scanner s = new Scanner(f)) {
			while (s.hasNextLine()) {
				encryptedCoordinates.add(Long.parseLong(s.nextLine()) * 811589153);
			}
			locationMappings = new HashSet<>();
			for (int i = 0; i < encryptedCoordinates.size(); i++) {
				locationMappings.add(new Mapping(i, i));
			}
			for (int i = 0; i < 10; i++) {
				mixList();
			}
			System.out.println(findCoordinates());
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	private static void mixList() {
		for (int i = 0; i < encryptedCoordinates.size(); i++) {
			Mapping currentNumMapping = new Mapping(i, i);
			for (Mapping m : locationMappings) {
				if (m.originalIndex == i) {
					currentNumMapping = m;
					break;
				}
			}
			moveNumber(currentNumMapping);
		}
	}

	private static void moveNumber(Mapping currentNumMapping) {
		// 1. figure out index where number should be
		long num = encryptedCoordinates.get(currentNumMapping.currentIndex);
		long targetIndex = (num + currentNumMapping.currentIndex) % (encryptedCoordinates.size() - 1);
		while (targetIndex < 0)
			targetIndex += encryptedCoordinates.size() - 1;

		// 2. remove number from current location
		encryptedCoordinates.remove(currentNumMapping.currentIndex);

		// 3. insert number at new location
		encryptedCoordinates.add((int) targetIndex, num);

		// 4. shift all of the numbers between start and end in map
		List<Mapping> removalList = new ArrayList<>();
		List<Mapping> addList = new ArrayList<>();
		for (Mapping m : locationMappings) {
			if (m.currentIndex < currentNumMapping.currentIndex && m.currentIndex >= targetIndex) {
				removalList.add(m);
				addList.add(new Mapping(m.originalIndex, m.currentIndex + 1));
			} else if (m.currentIndex > currentNumMapping.currentIndex && m.currentIndex <= targetIndex) {
				removalList.add(m);
				addList.add(new Mapping(m.originalIndex, m.currentIndex - 1));
			}
		}
		locationMappings.removeAll(removalList);
		locationMappings.addAll(addList);

		// 5. store numbers new location
		locationMappings.remove(currentNumMapping);
		locationMappings.add(new Mapping(currentNumMapping.originalIndex, (int) targetIndex));
	}

	private static long findCoordinates() {
		int indexOfZero = encryptedCoordinates.indexOf(0l);
		long oneThousandAfter = encryptedCoordinates.get((indexOfZero + 1000) % encryptedCoordinates.size());
		long twoThousandAfter = encryptedCoordinates.get((indexOfZero + 2000) % encryptedCoordinates.size());
		long threeThousandAfter = encryptedCoordinates.get((indexOfZero + 3000) % encryptedCoordinates.size());
		return oneThousandAfter + twoThousandAfter + threeThousandAfter;
	}

	private static class Mapping {
		int originalIndex;
		int currentIndex;

		Mapping(int originalIndex, int currentIndex) {
			this.originalIndex = originalIndex;
			this.currentIndex = currentIndex;
		}

		@Override
		public boolean equals(Object o) {
			Mapping m = (Mapping) o;
			return originalIndex == m.originalIndex && currentIndex == m.currentIndex;
		}

		@Override
		public int hashCode() {
			return originalIndex * 7 + currentIndex * 31;
		}
	}
}
