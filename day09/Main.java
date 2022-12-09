package day09;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {

	private static final int TAIL_LENGTH = 10;
	private static Set<Coordinates> tailPositions = new HashSet<>();
	private static Coordinates[] positions = new Coordinates[TAIL_LENGTH];

	public static void main(String... args) {
		// File f = new File("day09/test.txt");
		File f = new File("day09/input.txt");
		try (Scanner s = new Scanner(f)) {
			for (int i = 0; i < positions.length; i++) {
				positions[i] = new Coordinates();
			}
			tailPositions.add(new Coordinates(0, 0));
			while (s.hasNextLine()) {
				String[] command = s.nextLine().split(" ");
				Character direction = command[0].charAt(0);
				int magnitude = Integer.parseInt(command[1]);
				switch (direction) {
					case 'R':
						for (int i = 0; i < magnitude; i++) {
							positions[0].setX(positions[0].getX() + 1);
							determineTailPosition(0, 1);
						}
						break;
					case 'U':
						for (int i = 0; i < magnitude; i++) {
							positions[0].setY(positions[0].getY() + 1);
							determineTailPosition(0, 1);
						}
						break;
					case 'L':
						for (int i = 0; i < magnitude; i++) {
							positions[0].setX(positions[0].getX() - 1);
							determineTailPosition(0, 1);
						}
						break;
					case 'D':
						for (int i = 0; i < magnitude; i++) {
							positions[0].setY(positions[0].getY() - 1);
							determineTailPosition(0, 1);
						}
						break;
				}
				/*
				 * printPositions();
				 * System.out.println();
				 * long start = System.currentTimeMillis();
				 * while (System.currentTimeMillis() - start < 100) {
				 * }
				 */
			}
			System.out.println(tailPositions.size());
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void determineTailPosition(int headIndex, int tailIndex) {
		if (tailIndex > TAIL_LENGTH - 1)
			return;

		Coordinates headPosition = positions[headIndex];
		Coordinates tailPosition = positions[tailIndex];

		if (headPosition.equals(tailPosition)) {
			determineTailPosition(tailIndex, tailIndex + 1);
			return;
		}

		while (headPosition.getX() - tailPosition.getX() >= 2) { // tail is left
			tailPosition.setX(tailPosition.getX() + 1);
			if (tailPosition.getY() != headPosition.getY()) {
				int direction = (headPosition.getY() - tailPosition.getY())
						/ Math.abs(headPosition.getY() - tailPosition.getY());
				tailPosition.setY(tailPosition.getY() + direction);
			}
		}
		while (headPosition.getX() - tailPosition.getX() <= -2) { // tail is right
			tailPosition.setX(tailPosition.getX() - 1);
			if (tailPosition.getY() != headPosition.getY()) {
				int direction = (headPosition.getY() - tailPosition.getY())
						/ Math.abs(headPosition.getY() - tailPosition.getY());
				tailPosition.setY(tailPosition.getY() + direction);
			}
		}
		while (headPosition.getY() - tailPosition.getY() >= 2) { // tail is down
			tailPosition.setY(tailPosition.getY() + 1);
			if (tailPosition.getX() != headPosition.getX()) {
				int direction = (headPosition.getX() - tailPosition.getX())
						/ Math.abs(headPosition.getX() - tailPosition.getX());
				tailPosition.setX(tailPosition.getX() + direction);
			}
		}
		while (headPosition.getY() - tailPosition.getY() <= -2) { // tail is up
			tailPosition.setY(tailPosition.getY() - 1);
			if (tailPosition.getX() != headPosition.getX()) {
				int direction = (headPosition.getX() - tailPosition.getX())
						/ Math.abs(headPosition.getX() - tailPosition.getX());
				tailPosition.setX(tailPosition.getX() + direction);
			}
		}

		if (tailIndex == TAIL_LENGTH - 1)
			tailPositions.add(new Coordinates(tailPosition));

		determineTailPosition(tailIndex, tailIndex + 1);
	}

	public static void printPositions() {
		int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
		for (Coordinates d : tailPositions) {
			maxX = d.getX() > maxX ? d.getX() : maxX;
			maxY = d.getY() > maxY ? d.getY() : maxY;
			minX = d.getX() < minX ? d.getX() : minX;
			minY = d.getY() < minY ? d.getY() : minY;
		}
		char[][] positions = new char[maxY - minY + 1][maxX - minX + 1];
		for (int i = 0; i < positions.length; i++) {
			for (int j = 0; j < positions[i].length; j++) {
				positions[i][j] = '.';
			}
		}
		for (Coordinates d : tailPositions) {
			positions[maxY - d.getY()][d.getX() - minX] = '#';
		}
		for (int i = 0; i < positions.length; i++) {
			for (int j = 0; j < positions[i].length; j++) {
				System.out.print(positions[i][j] + " ");
			}
			System.out.println();
		}

	}
}

class Coordinates {
	private int x;
	private int y;

	Coordinates() {
		x = 0;
		y = 0;
	}

	Coordinates(int x, int y) {
		this.x = x;
		this.y = y;
	}

	Coordinates(Coordinates copy) {
		x = copy.x;
		y = copy.y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public boolean equals(Object obj) {
		Coordinates c = (Coordinates) obj;
		if (c == null)
			return false;
		return x == c.x && y == c.y;
	}

	@Override
	public int hashCode() {
		return Integer.parseInt(x + "" + Math.abs(y));
	}
}