package day16;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	private static HashMap<String, Valve> valves;
	private static HashMap<State, Integer> DPStates = new HashMap<>();

	public static void main(String... args) {
		// File f = new File("day16/test.txt");
		File f = new File("day16/input.txt");
		try (Scanner s = new Scanner(f)) {
			valves = new HashMap<>();
			while (s.hasNextLine()) {
				String valveInfo = s.nextLine();
				Matcher matcher = Pattern.compile("Valve (\\w+) has flow rate=(\\d+)").matcher(valveInfo);
				if (matcher.find()) {
					String name = matcher.group(1);
					int flowRate = Integer.parseInt(matcher.group(2));
					String[] subValves = valveInfo
							.replaceAll("Valve \\w+ has flow rate=\\d+; tunnels? leads? to valves? ", "").split(", ");
					Valve v = new Valve(name, flowRate, subValves);

					valves.put(v.name, v);
				}
			}
			// part 1
			// System.out.println(getMaxPressure(valves.get("AA"), new ArrayList<>(),
			// 30, 0));

			System.out.println(getMaxPressure(valves.get("AA"), new ArrayList<>(), 26,
					1));
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	public static int getMaxPressure(Valve v, List<Valve> opened, int time, int otherPlayers) {
		if (time == 0)
			return otherPlayers > 0 ? getMaxPressure(valves.get("AA"), opened, 26, otherPlayers - 1) : 0;
		int maxPressure = 0;

		Integer flow = DPStates.get(new State(v, opened, time, otherPlayers));
		if (flow != null) {
			return flow;
		}

		if (!opened.contains(v) && v.flowRate > 0) {
			opened.add(v);
			Collections.sort(opened);
			maxPressure = Math.max(maxPressure,
					getMaxPressure(v, opened, time - 1, otherPlayers) + v.flowRate * (time - 1));
			opened.remove(v);
		}
		for (String subValveNames : v.subValves) {
			Valve subValve = valves.get(subValveNames);
			maxPressure = Math.max(maxPressure, getMaxPressure(subValve, opened, time - 1, otherPlayers));
		}
		DPStates.put(new State(v, opened, time, otherPlayers), maxPressure);
		return maxPressure;
	}
}

class Valve implements Comparable<Valve> {
	String name;
	int flowRate;
	String[] subValves;

	Valve(String name, int flowRate, String[] subValves) {
		this.name = name;
		this.flowRate = flowRate;
		this.subValves = subValves;
	}

	@Override
	public boolean equals(Object o) {
		Valve v = (Valve) o;
		return this.name.equals(v.name);
	}

	@Override
	public int compareTo(Valve v) {
		if (flowRate > v.flowRate)
			return 1;
		if (flowRate < v.flowRate)
			return -1;
		return 0;
	}
}

record State(Valve v, List<Valve> opened, int time, int otherPlayers) {
}

// class State {
// Valve v;
// List<Valve> opened;
// int time;
// int otherPlayers;

// State(Valve v, List<Valve> opened, int time, int otherPlayers) {
// this.v = v;
// this.opened = opened;
// this.time = time;
// this.otherPlayers = otherPlayers;
// }

// @Override
// public boolean equals(Object o) {
// State s = (State) o;
// return v.equals(s.v) && opened.equals(s.opened) && (time == s.time);
// }

// @Override
// public int hashCode() {
// int hash = 1;
// hash *= v.name.hashCode();
// hash *= opened.hashCode();
// return hash + time * 30 + otherPlayers;
// }
// }