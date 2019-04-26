package bg.sofia.uni.fmi.mjt.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import bg.sofia.uni.fmi.mjt.constants.Constants;
import bg.sofia.uni.fmi.mjt.enums.SearchingPlayerStatus;
import bg.sofia.uni.fmi.mjt.gamemechanics.Board;

public class Client {

	private Socket socket;
	private PrintWriter writer;
	private boolean inGame = false;
	private boolean inMenu = false;
	private boolean gameEnded = false;
	private boolean turn = false;
	private boolean connected = false;
	private Board board;
	private Set<String> attackedPositions;
	private String opponentToJoinName = null;
 
	SearchingPlayerStatus searchingPlayer = SearchingPlayerStatus.WAITING;

	public void setConnected() {
		this.connected = true;
	}

	public void setConnectedFalse() {
		this.connected = false;
	}

	public void setSocketToNull() {
		this.socket = null;
	}

	public void setSeachingPlayerStatusToFound() {
		searchingPlayer = SearchingPlayerStatus.FOUND;
	}

	public void setSeachingPlayerStatusToNotFound() {
		searchingPlayer = SearchingPlayerStatus.NOT_FOUND;
	}

	public void setSeachingPlayerStatusToWaiting() {
		searchingPlayer = SearchingPlayerStatus.WAITING;
	}

	public void setOpponentToJoinName(String name) {
		this.opponentToJoinName = name;

	}

	public void setInGame() {
		this.inGame = true;
	}

	public void setInGameFalse() {
		this.inGame = false;
	}

	public void setInMenuFalse() {
		this.inMenu = false;
	}

	public void setInMenu() {
		this.inMenu = true;
	}

	public void setTurn() {
		this.turn = true;
	}

	public void setTurnFalse() {
		this.turn = false;
	}

	public boolean getTurn() {
		return turn;
	}

	public void closeWriter() {
		writer.close();
	}

	private Client() {
		board = new Board();
		attackedPositions = new HashSet<>();
	}

	public void afterGameFixing() {
		writer.println(Constants.GAME_ENDED);
		attackedPositions.clear();
		board = new Board();
		inGame = false;
		inMenu = true;
		gameEnded = true;
	}

	public Board getBoard() {
		return board;
	}

	public boolean theGameEnded() {
		return gameEnded;
	}

	public boolean checkIfGameIsLost() {

		if (board.noShipsLeft()) {
			System.out.println("YOU LOST THE GAME.You were moved to the menu.");
			afterGameFixing();
			writer.println("YOU WON THE GAME.You were moved to the menu.");
			return true;
		}
		return false;
	}

	public void sendBoardToEnemyAfterAttack() {
		writer.println("---ENEMY BOARD---");
		writer.println(board.BoardToStringForEnemy());
		if (board.checkIfshipHasSunk()) {
			writer.println("One of the enemy's ships was submerged");
		}

	}

	private void joinGame(String usernameToJoin) {
		board.placeShip(2);
		// board.placeAllShips(); // <--- if you want to place all ships before entering
		// the game
		writer.println("join-game " + usernameToJoin);
		writer.println("---ENEMY BOARD---");
		writer.println(board.BoardToStringForEnemy());
		inMenu = false;
		inGame = true;
		System.out.println("It is enemy turn");
		System.out.println("When its your turn attack enemy board (example: attack A1)");
	}

	private void help() {
		if (!inMenu && !inGame && !connected) {
			System.out.println("available commands are:");
			System.out.println("-connect " + "[name]");
		} else if (inMenu) {
			System.out.println("available commands are:");
			System.out.println("-start-game");
			System.out.println("-join-game [name]");
			System.out.println("-join-game");
			System.out.println("-list-games");
			System.out.println("-disconnect");
		} else if (inGame) {
			System.out.println("available commands are:");
			System.out.println("-attack [position]");
			// System.out.println("-exit");
		}
	}

	private void processInput(String input) {

		if (input.equals("help")) {
			help();
			return;
		}
		if (!inGame && !inMenu) {
			tryToConnect(input);
		} else if (inMenu && !inGame) {
			inMenu(input);
			
		} else if (!inMenu && inGame && turn) {
			inGameAndMyTurn(input);
		} else {
			System.out.println("Invalid command.");
		}
	}
	
	private void tryToConnect(String input) {
		String[] tokens = input.split(Constants.SPACE_SPLITTER);
		int commandIndex = 0;
		String command = tokens[commandIndex];
		
		if (Constants.CONNECT.equals(command) && !connected && tokens.length == 2) {
			int usernameIndex = 1;
			String username = tokens[usernameIndex];
			connect(username);
			setConnected();
			inMenu = true;
		}
	}
	
	private void inMenu(String input) {
		String[] tokens = input.split(Constants.SPACE_SPLITTER);
		int commandIndex = 0;
		String command = tokens[commandIndex];
		
		if (Constants.DISCONNECT.equals(input)) {
			writer.println(input);
			inMenu = false;
			inGame = false;
			socket = null;
		} else if (Constants.LIST_GAMES.equals(command)) {
			writer.println(command);

		} else if (Constants.START_GAME.equals(input)) {
			turn = true;
			inMenu = false;
			board.placeShip(2);
			// board.placeAllShips(); // <--- if you want to place all ships before entering
			// the game
			System.out.println("Waiting for another player to join");
			writer.println(command);

		} else if (Constants.JOIN_GAME.equals(command) && tokens.length == 2) {
			setSeachingPlayerStatusToWaiting();
			int playerToSearchIndex = 1;
			String playerToSearch = tokens[playerToSearchIndex];
			writer.println("searching for certain player: " + playerToSearch);
			while (searchingPlayer.equals(SearchingPlayerStatus.WAITING)) {

			}
			if (searchingPlayer.equals(SearchingPlayerStatus.FOUND)) {
				joinGame(opponentToJoinName);
			}
		} else if (Constants.JOIN_GAME.equals(command) && tokens.length == 1) {
			setSeachingPlayerStatusToWaiting();
			writer.println("searching for player");
			while (searchingPlayer.equals(SearchingPlayerStatus.WAITING)) {

			}
			if (searchingPlayer.equals(SearchingPlayerStatus.FOUND)) {
				joinGame(opponentToJoinName);
			}
		} else {
			System.out.println("Invalid command.");
		}

	}
	
	private void inGameAndMyTurn(String input) {
		String[] tokens = input.split(Constants.SPACE_SPLITTER);
		int commandIndex = 0;
		String command = tokens[commandIndex];
		
		
		if (input.contains(Constants.ATTACK) && tokens.length == 2) {
			int positionIndex = 1;
			String position = tokens[positionIndex];
			if (!command.equals(Constants.ATTACK) || !Board.validGivenString(position)
					|| !Board.stringPositionOnBoard(position)) {
				System.out.println("Invalid command.Try again.");
				return;
			}
			if (attackedPositions.contains(position)) {
				System.out.println("You have already attacked this position.Try new one.");
				return;
			}
			attackedPositions.add(position);
			writer.println(input);
			writer.println("---ENEMY BOARD---");
			writer.println(board.BoardToStringForEnemy());

			turn = false;
			System.out.println("You attacked " + position + ".Now is enemy turn.");
		} else {
			System.out.println("Invalid command.Try again or type help");
		}
	}
	
	private void run() {
		Scanner scanner = new Scanner(System.in);
		while (true) {
			String input = scanner.nextLine();
			gameEnded = false;
			processInput(input);
		}
	}

	private void connect(String username) {
		try {
			socket = new Socket(Constants.HOST_NAME, Constants.PORT);
			writer = new PrintWriter(socket.getOutputStream(), true);
			System.out.println("connected to server.Type help for more information.");
			writer.println(username);
			ClientConnection clientConnection = new ClientConnection(socket, this);
			new Thread(clientConnection).start();

		} catch (IOException e) {
			System.out.println("=> cannot connect to server , make sure that the server is started");
			System.exit(1);
		}
	}
	

	public static void main(String[] args) throws IOException {
		System.out.println("If you dont know what to do, you can always type [help]");
		Client client = new Client();
		client.run();
	}
}
