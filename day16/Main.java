package day16;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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

			System.out.println(getMaxPressure(valves.get("AA"), new ArrayList<>(), 30));
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	public static int getMaxPressure(Valve v, List<Valve> opened, int time) {
		if (time == 0)
			return 0;
		int maxPressure = 0;

		Integer flow = DPStates.get(new State(v, opened, time));
		if (flow != null && flow > 0) {
			return flow;
		}

		if (!opened.contains(v) && v.flowRate > 0) {
			List<Valve> tempOpened = new ArrayList<>(opened);
			tempOpened.add(v);
			maxPressure = Math.max(maxPressure,
					getMaxPressure(v, tempOpened, time - 1) + v.flowRate * (time - 1));
		}
		for (String subValveNames : v.subValves) {
			Valve subValve = valves.get(subValveNames);
			maxPressure = Math.max(maxPressure, getMaxPressure(subValve, opened, time - 1));
		}
		DPStates.put(new State(v, opened, time), maxPressure);
		return maxPressure;
	}
}

class Valve {
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
}

class State {
	Valve v;
	List<Valve> opened;
	int time;

	State(Valve v, List<Valve> opened, int time) {
		this.v = v;
		this.opened = opened;
		this.time = time;
	}

	@Override
	public boolean equals(Object o) {
		State s = (State) o;
		return v.equals(s.v) && opened.equals(s.opened) && (time == s.time);
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash *= v.name.hashCode();
		for (Valve v : opened) {
			hash *= v.name.hashCode();
		}
		return hash + time * 30;
	}
}