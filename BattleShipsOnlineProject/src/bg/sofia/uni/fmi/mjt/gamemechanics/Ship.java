package bg.sofia.uni.fmi.mjt.gamemechanics;

import java.util.HashSet;
import java.util.Set;

public class Ship {
	private Set<Position> allPositions;

	public Set<Position> getAllPositions() {
		return allPositions;
	}

	public Ship(Position startPosition, Position endPosition) {
		allPositions = new HashSet<>();
		allPositions.addAll(Board.getAllPositions(startPosition, endPosition));
	}

	public Ship() {
		allPositions = new HashSet<>();
	}

	public void addPosition(Position given) {
		allPositions.add(given);
	}

	public boolean containsPosition(Position given) {
		return allPositions.contains(given);
	}

	public void printPositions() {
		System.out.println(allPositions);
	}

	public void removePosition(Position given) {
		allPositions.remove(given);
	}

	public boolean shipDestroyed() {
		return allPositions.isEmpty();
	}

	public String toString() {
		StringBuilder toReturn = new StringBuilder();
		toReturn.append("");
		for (Position position : allPositions) {
			toReturn.append(position.toString());
		}
		return toReturn.toString();
	}
}
