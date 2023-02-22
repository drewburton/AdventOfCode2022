package day22;

import java.util.List;

public class Cube {
	private Face[] faces;

	private Cube(Face[] faces) {
		this.faces = faces;
	}

	public static Cube buildCube(List<List<Character>> map, int[][] faceRanges) {
		Face[] faces = new Face[6];
		// use bfs to find all of the faces
		// only start in top left, if in range of current corner, add to that face

		Cube c = new Cube(faces);
		return c;
	}

	public Player getInitialState() {
		return null;
	}
}
