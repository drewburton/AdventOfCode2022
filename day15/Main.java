package day15;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	private static final int RANGE = 4000000;
	private static ArrayList<Sensor> sensors = new ArrayList<>();

	public static void main(String... args) {
		// File f = new File("day15/test.txt");
		File f = new File("day15/input.txt");
		try (Scanner s = new Scanner(f)) {
			while (s.hasNextLine()) {
				Matcher matcher = Pattern
						.compile("Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)")
						.matcher(s.nextLine());
				if (matcher.find()) {
					int Sx = Integer.parseInt(matcher.group(1));
					int Sy = Integer.parseInt(matcher.group(2));
					int Bx = Integer.parseInt(matcher.group(3));
					int By = Integer.parseInt(matcher.group(4));
					Sensor sensor = new Sensor(Sx, Sy, Bx, By);
					sensors.add(sensor);
				}
			}

			for (Sensor sensor : sensors) {
				if (searchAroundSensor(sensor))
					return;
			}
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	public static boolean scan(int x, int y) {
		if (x > RANGE || y > RANGE || x < 0 || y < 0)
			return false;
		boolean valid = true;
		for (Sensor sensor : sensors) {
			if ((sensor.Bx == x && sensor.By == y) || (sensor.Sx == x && sensor.Sy == y)) {
				valid = false;
				break;
			}
			if (sensor.isInSensorRange(x, y)) {
				valid = false;
				break;
			}
		}
		if (valid) {
			System.out.println(x * 4000000l + y);
			return true;
		}
		return false;
	}

	public static boolean searchAroundSensor(Sensor s) {
		return searchQuadrant(s, s.Sx, s.Sy + s.manhattanDistance + 1, 1, -1) ||
				searchQuadrant(s, s.Sx + s.manhattanDistance + 1, s.Sy, -1, -1) ||
				searchQuadrant(s, s.Sx, s.Sy - s.manhattanDistance - 1, -1, 1) ||
				searchQuadrant(s, s.Sx - s.manhattanDistance - 1, s.Sy, 1, 1);
	}

	public static boolean searchQuadrant(Sensor s, int x, int y, int xDirection, int yDirection) {
		do {
			if (scan(x, y))
				return true;
			x += xDirection;
			y += yDirection;
		} while (x != s.Sx && y != s.Sy);
		return false;
	}
}

class Sensor {
	int Sx, Sy, Bx, By;
	int manhattanDistance;

	Sensor(int Sx, int Sy, int Bx, int By) {
		this.Sx = Sx;
		this.Sy = Sy;
		this.Bx = Bx;
		this.By = By;
		manhattanDistance = Math.abs(Sy - By) + Math.abs(Sx - Bx);
	}

	public boolean isInSensorRange(int x, int y) {
		return Math.abs(Sx - x) + Math.abs(Sy - y) <= manhattanDistance;
	}
}