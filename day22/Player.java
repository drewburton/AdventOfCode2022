package day22;

public class Player {
	Cube map;
	int row, col, heading;

	Player(int row, int col, int heading) {
		this.row = row;
		this.col = col;
		this.heading = heading;
	}

	public void move(int magnitude) throws Exception {
		for (int i = 0; i < magnitude; i++) {
			int previousRow = row, previousCol = col;
			wrap();
			if (isWallAhead()) {
				row = previousRow;
				col = previousCol;
				return;
			}
			switch (heading) {
				case 0:
					col++;
					break;
				case 1:
					row++;
					break;
				case 2:
					col--;
					break;
				case 3:
					row--;
					break;
				default:
					throw new Exception("Invalid heading when moving");
			}
		}
	}

	public void turn(char direction) throws Exception {
		if (direction == 'R') {
			heading = (heading + 1) % 4;
		} else if (direction == 'L') {
			heading--;
			if (heading < 0)
				heading = 3;
		} else {
			throw new Exception("Invalid heading when turning");
		}
	}

	private boolean isWallAhead() throws Exception {
		switch (heading) {
			case 0:
				return map.get(row).get(col + 1) == '#';
			case 1:
				return map.get(row + 1).get(col) == '#';
			case 2:
				return map.get(row).get(col - 1) == '#';
			case 3:
				return map.get(row - 1).get(col) == '#';
			default:
				throw new Exception("Invalid heading when checking wall");
		}
	}

	private void wrap() throws Exception {
		switch (heading) {
			case 0:
				if (col >= map.get(row).size() - 1 || map.get(row).get(col + 1) == ' ') {
					while (col > 0 && map.get(row).get(col - 1) != ' ') {
						col--;
					}
					col--;
				}
				return;
			case 1:
				if (row >= map.size() - 1 || col > map.get(row + 1).size() - 1
						|| map.get(row + 1).get(col) == ' ') {
					while (row > 0 && map.get(row - 1).get(col) != ' ') {
						row--;
					}
					row--;
				}
				return;
			case 2:
				if (col <= 0 || map.get(row).get(col - 1) == ' ') {
					while (col < map.get(row).size() - 1 && row < map.size() && map.get(row).get(col + 1) != ' ') {
						col++;
					}
					col++;
				}
				return;
			case 3:
				if (row <= 0 || map.get(row - 1).get(col) == ' ') {
					while (row < map.size() - 1 && col < map.get(row + 1).size()
							&& map.get(row + 1).get(col) != ' ') {
						row++;
					}
					row++;
				}
				return;
			default:
				throw new Exception("Invalid heading when wrapping");
		}
	}
}
