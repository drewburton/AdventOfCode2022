package day19;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	private static List<HashMap<Robot.Type, Cost>> blueprints = new ArrayList<>();
	private static State optimumPotential = new State(new ArrayList<>(), 0, 0, 0, 0);
	private static HashMap<Robot.Type, Integer> maxRobotsPerType = new HashMap<>();
	private static State previousState;

	public static void main(String... args) {
		// File f = new File("day19/test.txt");
		File f = new File("day19/input.txt");
		try (Scanner s = new Scanner(f)) {
			parseBlueprints(s);
			System.out.println(multiplyMaxGeodes());
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void parseBlueprints(Scanner s) {
		while (s.hasNextLine()) {
			if (blueprints.size() == 3) {
				s.nextLine();
				continue;
			}

			HashMap<Robot.Type, Cost> blueprint = new HashMap<>();
			Matcher matcher = Pattern.compile(
					"Blueprint \\d+: Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.")
					.matcher(s.nextLine());
			if (matcher.find()) {
				Cost oreCost = new Cost(Integer.parseInt(matcher.group(1)), 0, 0);
				Cost clayCost = new Cost(Integer.parseInt(matcher.group(2)), 0, 0);
				Cost obsidianCost = new Cost(Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4)),
						0);
				Cost geodeCost = new Cost(Integer.parseInt(matcher.group(5)), 0,
						Integer.parseInt(matcher.group(6)));
				blueprint.put(Robot.Type.ORE, oreCost);
				blueprint.put(Robot.Type.CLAY, clayCost);
				blueprint.put(Robot.Type.OBSIDIAN, obsidianCost);
				blueprint.put(Robot.Type.GEODE, geodeCost);
				blueprints.add(blueprint);
			}
		}
	}

	public static void findMaxCosts(Cost oreCost, Cost clayCost, Cost obsidianCost, Cost geodeCost) {
		int maxOreCost = Math.max(oreCost.ore,
				Math.max(clayCost.ore, Math.max(obsidianCost.ore, geodeCost.ore)));
		int maxClayCost = Math.max(oreCost.clay,
				Math.max(clayCost.clay, Math.max(obsidianCost.clay, geodeCost.clay)));
		int maxObsidianCost = Math.max(oreCost.obsidian,
				Math.max(clayCost.obsidian, Math.max(obsidianCost.obsidian, geodeCost.obsidian)));

		// put if higher than max
		if (maxRobotsPerType.get(Robot.Type.ORE) != null && maxOreCost > maxRobotsPerType.get(Robot.Type.ORE)) {
			maxRobotsPerType.put(Robot.Type.ORE, maxOreCost);
		}
		if (maxRobotsPerType.get(Robot.Type.CLAY) != null && maxClayCost > maxRobotsPerType.get(Robot.Type.CLAY)) {
			maxRobotsPerType.put(Robot.Type.CLAY, maxClayCost);
		}
		if (maxRobotsPerType.get(Robot.Type.OBSIDIAN) != null
				&& maxObsidianCost > maxRobotsPerType.get(Robot.Type.OBSIDIAN)) {
			maxRobotsPerType.put(Robot.Type.OBSIDIAN, maxObsidianCost);
		}

	}

	public static int multiplyMaxGeodes() {
		int multipliedMaxGeodes = 1;
		for (int i = 0; i < blueprints.size(); i++) {
			// reset class variables
			optimumPotential = new State(new ArrayList<>(), 0, 0, 0, 0);
			State newState = new State(List.of(new Robot(Robot.Type.ORE)), 0, 0, 0, 32);
			previousState = new State(newState);

			maxRobotsPerType = new HashMap<>();
			maxRobotsPerType.put(Robot.Type.ORE, 0);
			maxRobotsPerType.put(Robot.Type.CLAY, 0);
			maxRobotsPerType.put(Robot.Type.OBSIDIAN, 0);
			maxRobotsPerType.put(Robot.Type.GEODE, 0);
			findMaxCosts(blueprints.get(i).get(Robot.Type.ORE), blueprints.get(i).get(Robot.Type.CLAY),
					blueprints.get(i).get(Robot.Type.OBSIDIAN), blueprints.get(i).get(Robot.Type.GEODE));

			int maxGeodesForBlueprint = maximumGeodes(blueprints.get(i), newState);
			multipliedMaxGeodes *= maxGeodesForBlueprint;
		}
		return multipliedMaxGeodes;
	}

	public static int maximumGeodes(HashMap<Robot.Type, Cost> blueprint, State currentState) {
		State temp = new State(currentState);
		if (currentState.time == 0)
			return currentState.geode;

		if (isBadBranch(blueprint, currentState))
			return currentState.geode;

		int max = 0;
		List<Robot> buy = buyRobots(blueprint, currentState);

		buyOre(currentState);

		for (Robot r : buy) {
			State next = new State(currentState);
			previousState = new State(temp);
			next.robots.add(r);
			Cost cost = blueprint.get(r.type);
			next.ore -= cost.ore;
			next.clay -= cost.clay;
			next.obsidian -= cost.obsidian;
			next.time--;

			max = Math.max(max, maximumGeodes(blueprint, next));
		}
		previousState = new State(temp);
		currentState.time--;
		max = Math.max(max, maximumGeodes(blueprint, currentState));

		if (max > optimumPotential.geode) {
			optimumPotential = currentState;
		}

		return max;
	}

	public static List<Robot> buyRobots(HashMap<Robot.Type, Cost> blueprint, State currentState) {
		List<Robot> buy = new ArrayList<>();
		// don't buy any in the last minute
		if (currentState.time == 1)
			return buy;

		// only buy geode in second to last
		Cost geodeCost = blueprint.get(Robot.Type.GEODE);
		if (currentState.time == 2) {
			if (currentState.ore >= geodeCost.ore &&
					currentState.clay >= geodeCost.clay &&
					currentState.obsidian >= geodeCost.obsidian) {
				buy.add(new Robot(Robot.Type.GEODE));
			}
			return buy;
		}

		for (var entry : blueprint.entrySet()) {
			// don't buy clay two minutes before last
			if (currentState.time == 3 && entry.getKey().equals(Robot.Type.CLAY)) {
				continue;
			}

			// don't buy more than the maximum amount needed per minute
			if (entry.getKey().equals(Robot.Type.ORE)
					&& currentState.oreRobots() >= maxRobotsPerType.get(entry.getKey()))
				continue;
			if (entry.getKey().equals(Robot.Type.CLAY)
					&& currentState.clayRobots() >= maxRobotsPerType.get(entry.getKey()))
				continue;
			if (entry.getKey().equals(Robot.Type.OBSIDIAN)
					&& currentState.obsidianRobots() >= maxRobotsPerType.get(entry.getKey()))
				continue;

			// if could have bought in previous round and didn't, don't buy now
			if (couldHaveBoughtBefore(currentState, entry.getKey(), entry.getValue()))
				continue;

			if (currentState.ore >= entry.getValue().ore &&
					currentState.clay >= entry.getValue().clay &&
					currentState.obsidian >= entry.getValue().obsidian)
				buy.add(new Robot(entry.getKey()));
		}
		buy.sort(Robot::compareTo);
		return buy;
	}

	public static void buyOre(State currentState) {
		for (Robot r : currentState.robots) {
			switch (r.type) {
				case ORE:
					currentState.ore++;
					break;
				case CLAY:
					currentState.clay++;
					break;
				case OBSIDIAN:
					currentState.obsidian++;
					break;
				case GEODE:
					currentState.geode++;
			}
		}
	}

	public static boolean couldHaveBoughtBefore(State currentState, Robot.Type type, Cost cost) {
		if (currentState.robots.size() == previousState.robots.size()) {
			return previousState.ore >= cost.ore &&
					previousState.clay >= cost.clay &&
					previousState.obsidian >= cost.obsidian;
		}
		return false;
	}

	public static boolean isBadBranch(HashMap<Robot.Type, Cost> blueprint, State currentState) {
		int geodesFromCurrentRobots = currentState.geodeRobots() * currentState.time;
		int estimatedGeodesFromNew = currentState.time * (currentState.time - 1) / 2;
		int totalEstimate = geodesFromCurrentRobots + estimatedGeodesFromNew + currentState.geode;
		return totalEstimate < optimumPotential.geode;
	}
}

class State {
	List<Robot> robots;
	int ore, clay, obsidian, geode;
	int time;

	State(List<Robot> robots, int ore, int clay, int obsidian, int time) {
		this.robots = new ArrayList<>(robots);
		this.ore = ore;
		this.clay = clay;
		this.obsidian = obsidian;
		this.time = time;
	}

	State(State s) {
		robots = new ArrayList<>(s.robots);
		ore = s.ore;
		clay = s.clay;
		obsidian = s.obsidian;
		geode = s.geode;
		time = s.time;
	}

	public int oreRobots() {
		return (int) robots.stream().filter(r -> r.type.equals(Robot.Type.ORE)).count();
	}

	public int clayRobots() {
		return (int) robots.stream().filter(r -> r.type.equals(Robot.Type.CLAY)).count();
	}

	public int obsidianRobots() {
		return (int) robots.stream().filter(r -> r.type.equals(Robot.Type.OBSIDIAN)).count();
	}

	public int geodeRobots() {
		return (int) robots.stream().filter(r -> r.type.equals(Robot.Type.GEODE)).count();
	}
}

class Cost {
	int ore, clay, obsidian;

	Cost(int ore, int clay, int obsidian) {
		this.ore = ore;
		this.clay = clay;
		this.obsidian = obsidian;
	}
}

class Robot implements Comparable<Robot> {
	public enum Type {
		ORE, CLAY, OBSIDIAN, GEODE
	};

	Type type;

	Robot(Type type) {
		this.type = type;
	}

	@Override
	public int compareTo(Robot o) {
		if (type.equals(o.type))
			return 0;
		if (type.equals(Type.GEODE))
			return -1;
		if (type.equals(Type.OBSIDIAN) && !o.type.equals(Type.GEODE))
			return -1;
		if (type.equals(Type.CLAY) && !o.type.equals(Type.OBSIDIAN) && !o.type.equals(Type.GEODE))
			return -1;
		return 1;
	}
}