package bg.sofia.uni.fmi.mjt.gamemechanics;

import java.util.Objects;

public class Position {
	private final static int fromCharToInt = 64;
	private int row;
	private int column;

	public Position(char row, int column) {
		this.row = row - fromCharToInt;
		this.column = column;
	}

	public Position(int row, int column) {
		this.row = row;
		this.column = column;
	}

	public void setRowIndex(char row) {
		this.row = row - fromCharToInt;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public void setColumnIndex(int column) {
		this.column = column;
	}

	public int getRowIndex() {
		return row;
	}

	public char getRowIndexAsChar() {
		return (char) (row + fromCharToInt);
	}

	public int getColumnIndex() {
		return column;
	}

	public static Position createPositionFromValidString(String input) {
		char row = input.charAt(0);
		input = input.substring(1);
		int column = Integer.parseInt(input);

		Position position = new Position(row, column);
		return position;
	}
 
	public String toString() {
		return "[Row: " + (char) (row + fromCharToInt) + " Column: " + column + "]";

	}

	public boolean equals(Position other) {
		return this.getRowIndex() == other.getRowIndex() && this.getColumnIndex() == other.getColumnIndex();
	}

	@Override
	public boolean equals(Object o) {
		Position position = (Position) o;
		return this.getRowIndex() == position.getRowIndex() && this.getColumnIndex() == position.getColumnIndex();

	}

	@Override
	public int hashCode() {
		return Objects.hash(row, column);
	}

}
