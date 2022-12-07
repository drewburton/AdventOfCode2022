package day07;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {
	public static void main(String... args) {
		// java.io.File inputFile = new java.io.File("day07/test.txt");
		java.io.File inputFile = new java.io.File("day07/input.txt");
		try (Scanner s = new Scanner(inputFile)) {
			Directory currentDirectory = new Directory();
			currentDirectory.name = s.nextLine().substring(5);

			while (s.hasNextLine()) {
				String line = s.nextLine();
				if (line.matches("\\$ cd \\w+") || (line.startsWith("dir"))) {
					boolean cd = line.startsWith("$");
					line = line.replace("$ cd ", "");
					line = line.replace("dir ", "");

					Directory d = new Directory();
					d.name = line;
					d.parent = currentDirectory;
					currentDirectory.subDirectories.add(d);
					if (cd)
						currentDirectory = d;
				} else if (line.matches("\\$ cd /")) {
					while (currentDirectory.parent != null) {
						currentDirectory = currentDirectory.parent;
					}
				} else if (line.matches("\\$ cd \\.\\.")) {
					if (currentDirectory.parent != null) {
						currentDirectory = currentDirectory.parent;
					}
				} else if (!line.startsWith("$")) {
					File f = new File();
					String[] split = line.split(" ");
					f.size = Integer.parseInt(split[0]);
					f.name = split[1];
					currentDirectory.files.add(f);
				}
			}
			while (currentDirectory.parent != null)
				currentDirectory = currentDirectory.parent;

			int totalSize = sumDirectorySize(currentDirectory);
			directorySizes.sort((e1, e2) -> e1.compareTo(e2));
			for (int size : directorySizes) {
				if (70000000 - totalSize + size >= 30000000) {
					System.out.println(size);
					break;
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	static ArrayList<Integer> directorySizes = new ArrayList<>();

	public static int sumDirectorySize(Directory currentDirectory) {
		int sum = 0;
		for (File f : currentDirectory.files) {
			sum += f.size;
		}
		for (Directory d : currentDirectory.subDirectories) {
			sum += sumDirectorySize(d);
		}
		directorySizes.add(sum);
		return sum;
	}
}

class Directory {
	String name;
	Directory parent;
	Set<Directory> subDirectories;
	Set<File> files;

	public Directory() {
		name = "";
		parent = null;
		subDirectories = new HashSet<>();
		files = new HashSet<>();
	}
}

class File {
	String name;
	int size;
}
