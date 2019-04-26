package bg.sofia.uni.fmi.mjt.gamemechanics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

public class Board {
	final static int BOARD_SIZE = 10;

	private char[][] board;

	private List<Ship> ships;

	private final static int fromCharToInt = 65;
	private final static int indexAdjust = 1;

	public Board() {
		ships = new ArrayList<>();

		boardInitialization();
	}

	private void boardInitialization() {
		board = new char[BOARD_SIZE][BOARD_SIZE];
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				board[i][j] = '-';
			}
		}
	}

	public boolean noShipsLeft() {
		return ships.isEmpty();
	}

	public void hit(String input) {
		if (!validGivenString(input)) {
			System.out.println("Invalid position given.Try again.");
			return;
		}
		Position attacked = Position.createPositionFromValidString(input);
		if (!positionOnBoard(attacked)) {
			System.out.println("Position given is not ot the board.Try again.");
		}
		int row = attacked.getRowIndexAsChar() - fromCharToInt;
		int column = attacked.getColumnIndex() - indexAdjust;

		removeAttackedPosition(attacked);
		System.out.println("---YOUR BOARD---");
		printBoardToMe(); // <-- if you want to see your board when playing multiplayer
	}

	public boolean checkIfshipHasSunk() {
		for (Ship ship : ships) {
			if (ship.shipDestroyed()) {
				ships.remove(ship);
				return true;
			}
		}
		return false;
	}

	private void removeAttackedPosition(Position attacked) {
		for (Ship ship : ships) {
			if (ship.containsPosition(attacked)) {
				ship.removePosition(attacked);
				setHitOnBoard(attacked);
				return;
			}
		}
		setMissOnBoard(attacked);
	}

	public void PrintShipsPositions() {
		System.out.println(ships);
	}

	public void setHitOnBoard(Position hitted) {
		removeAttackedPosition(hitted);

		int row = hitted.getRowIndexAsChar() - fromCharToInt;
		int column = hitted.getColumnIndex() - indexAdjust;
		board[row][column] = 'X';
	}

	public void setMissOnBoard(Position missed) {
		// int fromCharToInt = 65;
		// int indexAdjust = 1;
		int row = missed.getRowIndexAsChar() - fromCharToInt;
		int column = missed.getColumnIndex() - indexAdjust;

		board[row][column] = 'O';
	}

	public void setShipSignsOnBoard(Ship ship) {
		for (Position position : ship.getAllPositions()) {
			board[position.getRowIndexAsChar() - fromCharToInt][position.getColumnIndex() - indexAdjust] = '*';
		}
	}

	public void printBoardToMe() {
		System.out.println(toString());
	}

	public String toString() {
		StringBuilder boardString = new StringBuilder();
		boardString.append("  1 2 3 4 5 6 7 8 9 10");
		boardString.append(System.lineSeparator());
		int symbolAscii = 65;
		for (int i = 0; i < BOARD_SIZE; i++) {
			boardString.append((char) symbolAscii);
			for (int j = 0; j < BOARD_SIZE; j++) {
				boardString.append(" ");
				boardString.append(board[i][j]);
			}
			symbolAscii++;
			boardString.append(System.lineSeparator());
		}
		return boardString.toString();

	}

	public String BoardToStringForEnemy() {
		StringBuilder boardString = new StringBuilder();
		boardString.append("  1 2 3 4 5 6 7 8 9 10");
		boardString.append(System.lineSeparator());
		int symbolAscii = 65;
		for (int i = 0; i < BOARD_SIZE; i++) {
			boardString.append((char) symbolAscii);
			for (int j = 0; j < BOARD_SIZE; j++) {
				if (board[i][j] == '*') {
					boardString.append(" ");
					boardString.append("-");
				} else {
					boardString.append(" ");
					boardString.append(board[i][j]);
				}
			}
			symbolAscii++;
			boardString.append(System.lineSeparator());
		}
		return boardString.toString();
	}

	public List<Ship> getShips() {
		return ships;
	}

	public void placeShip(int lengthOfShip) {
		Scanner scanner = new Scanner(System.in);

		while (true) {
			System.out.println("Enter valid start position of ship with length " + lengthOfShip + ".");
			String input = scanner.nextLine();

			if (!validGivenString(input)) {
				System.out.println("Invalid start position given.Try again.");
				continue;
			}
			Position startPosition = Position.createPositionFromValidString(input);
			if (!positionOnBoard(startPosition)) {
				System.out.println("Start position is not on the board.Try again.");
			}

			System.out.println("Enter valid end position");
			input = scanner.nextLine();
			if (!validGivenString(input)) {
				System.out.println("Invalid end position given.Try again.");
				continue;
			}
			Position endPosition = Position.createPositionFromValidString(input);
			if (!positionOnBoard(endPosition)) {
				System.out.println("End position is not on the board.Try again.");
				continue;
			}
			if (!possiblePositionsForShip(startPosition, endPosition, lengthOfShip)) {
				System.out.println(
						"Start and end positions are not in a straight line, or they dont match the length of the ship.Try again.");
				continue;
			}

			List<Position> newPositions = getAllPositions(startPosition, endPosition);
			if (!allPositionsAreFree(newPositions)) {
				System.out
						.println("There is already part of ship where you are trying to place the new one.Try again.");
				continue;
			}
			System.out.println("Ship created");
			Ship newPlacedShip = new Ship(startPosition, endPosition);
			setShipSignsOnBoard(newPlacedShip);
			ships.add(newPlacedShip);
			printBoardToMe();
			break;
		}
	}

	public void placeAllShips() {
		Queue<Integer> shipsLength = new LinkedList<>(Arrays.asList(5, 4, 4, 3, 3, 3, 2, 2, 2, 2));

		while (!shipsLength.isEmpty()) {
			placeShip(shipsLength.poll());
		}

	}

	public static boolean validGivenString(String input) {
		if (input.length() < 2 || input.length() > 4) {
			return false;
		}
		char firstChar = input.charAt(0);

		if (!Character.isLetter(firstChar)) {
			return false;
		}
		if (input.length() == 3 && !input.contains("10")) {
			return false;
		}

		char row = input.charAt(0);
		input = input.substring(1);
		int column = Integer.parseInt(input);

		if (row < 'A' || row > 'J' || column < 1 || column > 10) {
			return false;
		}
		return true;
	}

	public static List<Position> getAllPositions(Position first, Position second) {
		List<Position> possibles = new ArrayList<>();

		if (first.getRowIndex() == second.getRowIndex()) {
			int smallerPosition = Math.min(first.getColumnIndex(), second.getColumnIndex());
			int greaterPosition = Math.max(first.getColumnIndex(), second.getColumnIndex());
			for (int i = smallerPosition; i <= greaterPosition; i++) {
				possibles.add(new Position(second.getRowIndexAsChar(), i));
			}
			return possibles;
		} else {
			int smallerPosition = Math.min(first.getRowIndex(), second.getRowIndex());
			int greaterPosition = Math.max(first.getRowIndex(), second.getRowIndex());
			for (int i = smallerPosition; i <= greaterPosition; i++) {
				possibles.add(new Position((char) (i + 64), second.getColumnIndex()));
			}
			return possibles;
		}
	}

	public boolean possiblePositionsForShip(Position first, Position second, int lengthOfShip) {
		if (first.getRowIndex() == second.getRowIndex()) {
			return Math.abs(first.getColumnIndex() - second.getColumnIndex()) == lengthOfShip - indexAdjust;
		} else if (first.getColumnIndex() == second.getColumnIndex()) {
			return Math.abs(first.getRowIndex() - second.getRowIndex()) == lengthOfShip - indexAdjust;
		}

		return false;

	}

	public static boolean positionOnBoard(Position position) {
		if (position.getRowIndex() < 1 || position.getRowIndex() > BOARD_SIZE || position.getColumnIndex() > BOARD_SIZE
				|| position.getColumnIndex() < 1) {
			return false;
		}

		return true;
	}

	public static boolean stringPositionOnBoard(String position) {
		Position positionMade = Position.createPositionFromValidString(position);
		return positionOnBoard(positionMade);
	}

	public boolean allPositionsAreFree(List<Position> positions) {

		for (Ship ship : ships) {
			for (Position position : positions) {
				if (ship.containsPosition(position)) {
					return false;
				}
			}
		}
		return true;
	}

}
