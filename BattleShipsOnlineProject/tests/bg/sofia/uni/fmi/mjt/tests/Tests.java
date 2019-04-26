package bg.sofia.uni.fmi.mjt.tests;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import bg.sofia.uni.fmi.mjt.gamemechanics.Board;
import bg.sofia.uni.fmi.mjt.gamemechanics.Position;
import bg.sofia.uni.fmi.mjt.gamemechanics.Ship;

public class Tests {
	@Test
	public void testPositionOnBoardWithValidInput() {
		Position tested = new Position('A', 1);
		boolean expected = true;
		assertEquals(expected, Board.positionOnBoard(tested));
	}

	@Test
	public void testPositionOnBoardWithInvalidInput() {
		Position tested = new Position('A', 11);
		boolean expected = false;
		assertEquals(expected, Board.positionOnBoard(tested));
	}

	@Test
	public void testPositionOnBoardWithInvalidInputAsString() {
		boolean expected = false;
		assertEquals(expected, Board.stringPositionOnBoard("K1"));
	}

	@Test
	public void testPositionOnBoardWithValidInputAsString() {
		boolean expected = true;
		assertEquals(expected, Board.stringPositionOnBoard("J10"));
	}

	@Test
	public void testPossiblePositionsForShipReturnsTrue() {
		Board board = new Board();
		Position start = new Position('A', 9);
		Position end = new Position('D', 9);
		boolean expected = true;
		int length = 4;
		assertEquals(expected, board.possiblePositionsForShip(start, end, length));
	}

	@Test
	public void testPossiblePositionsForShipReturnsFalseWhenLengthDoesntMatch() {
		Board board = new Board();
		Position start = new Position('A', 9);
		Position end = new Position('D', 9);
		boolean expected = false;
		int length = 99;
		assertEquals(expected, board.possiblePositionsForShip(start, end, length));
	}

	@Test
	public void testGetAllPositionsReturnsValidResultWhenTestedVertically() {
		Position start = new Position('C', 1);
		int endPositionAsInt = 1;
		Position end = new Position(endPositionAsInt, 1);
		List<Position> allPositions = new ArrayList();
		Position position1 = new Position('A', 1);
		Position position2 = new Position('B', 1);
		Position position3 = new Position('C', 1);
		allPositions.add(position1);
		allPositions.add(position2);
		allPositions.add(position3);
		assertEquals(allPositions, Board.getAllPositions(start, end));
	}

	@Test
	public void testGetAllPositionsReturnsValidResultWhenTestedHorizontally() {
		Position start = new Position('H', 1);
		Position end = new Position('H', 3);
		List<Position> allPositions = new ArrayList();
		Position position1 = new Position('H', 1);
		Position position2 = new Position('H', 2);
		Position position3 = new Position('H', 3);
		allPositions.add(position1);
		allPositions.add(position2);
		allPositions.add(position3);
		assertEquals(allPositions, Board.getAllPositions(start, end));
	}

	@Test
	public void testValidGivenStringReturnsTrue() {
		String input = "J10";
		boolean expected = true;
		assertEquals(expected, Board.validGivenString(input));
	}

	@Test
	public void testValidGivenStringReturnsFalseWhenInputLengthIsInvalid() {
		String input = "J10321";
		boolean expected = false;
		assertEquals(expected, Board.validGivenString(input));
	}

	@Test
	public void testCreatingShip() {
		Ship ship = new Ship(new Position(1, 1), new Position(1, 3));
		String expected = "[Row: A Column: 1][Row: A Column: 2][Row: A Column: 3]";
		assertEquals(expected, ship.toString());
	}

	@Test
	public void testPlacingShip() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		System.setOut(ps);

		String input = "A1\r\n" + "A5\r\n";
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		System.setIn(in);
		Board board = new Board();
		board.placeShip(5);
		Ship ship = new Ship(new Position(1, 1), new Position(1, 5)); 
		List<Ship> expected = new ArrayList<>();
		expected.add(ship);
		assertEquals(expected.toString(), board.getShips().toString());
	}

	@Test
	public void testHitWithNoShip() {
		Board board = new Board();
		board.hit("A1");
		String expected = "  1 2 3 4 5 6 7 8 9 10\r\n" + "A O - - - - - - - - -\r\n" + "B - - - - - - - - - -\r\n"
				+ "C - - - - - - - - - -\r\n" + "D - - - - - - - - - -\r\n" + "E - - - - - - - - - -\r\n"
				+ "F - - - - - - - - - -\r\n" + "G - - - - - - - - - -\r\n" + "H - - - - - - - - - -\r\n"
				+ "I - - - - - - - - - -\r\n" + "J - - - - - - - - - -\r\n";
		assertEquals(expected, board.BoardToStringForEnemy());
	}
}
