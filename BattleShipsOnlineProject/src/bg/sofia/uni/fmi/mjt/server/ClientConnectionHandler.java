package bg.sofia.uni.fmi.mjt.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

import javax.management.RuntimeErrorException;

import bg.sofia.uni.fmi.mjt.constants.Constants;

public class ClientConnectionHandler implements Runnable {
	private Socket socket;
	private String username;
	private PrintWriter opponent;

	public ClientConnectionHandler(Socket socket, String username) {
		this.socket = socket;
		this.username = username;
	}

	private void startGame() {
		Server.getAllGames().put(username, "");
	}

	private void joinGame(String playerToJoin) {
		Server.getAllGames().put(playerToJoin, username);
	}

	private String listGames() {
		StringBuilder games = new StringBuilder();
		games.append("---GAMES LIST---");
		games.append(System.lineSeparator());
		if (Server.getAllGames().isEmpty()) {
			games.append("---NO EXISTING GAMES---");
			return games.toString();
		}
		for (Map.Entry<String, String> entry : Server.getAllGames().entrySet()) {
			games.append("Game: ");
			games.append(entry.getKey());
			games.append(" | opponent: ");
			if (entry.getValue().equals("")) {
				games.append(" ");
				games.append("| Players in the game: 1/2");
			} else {
				games.append(entry.getValue());
				games.append(" | Players in the game: 2/2");
			}
			games.append(System.lineSeparator());
		}
		return games.toString();
	}

	@Override
	public void run() {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
			while (true) {
				String commandInput = reader.readLine();
				if (commandInput != null) {
					String[] seperateString = commandInput.split(Constants.SPACE_SPLITTER);
					if (commandInput != null) {

						if (Constants.DISCONNECT.equals(commandInput)) {
							writer.println("disconnected");
							Server.getUsers().remove(username);
							System.out.println("disconnected");
							socket.close();
							break;

						} else if (Constants.LIST_GAMES.equals(commandInput)) {
							writer.println(listGames());

						} else if (Constants.JOIN_GAME.equals(seperateString[0])) {
							int userToJoinIndex = 1;
							String userToJoin = seperateString[userToJoinIndex];
							if (opponent == null) {
								opponent = new PrintWriter(Server.getUserSocket(userToJoin).getOutputStream(), true);
							}
							opponent.println(username + " has joined your game");

						} else if (commandInput.contains(Constants.SEARCHING_FOR_CERTAIN_PLAYER)) {
							boolean foundPlayer = false;
							int playerToJoinIndex = 4;
							String playerToJoin = commandInput.split(Constants.SPACE_SPLITTER)[playerToJoinIndex];
							System.out.println(playerToJoin);
							for (Map.Entry<String, String> entry : Server.getAllGames().entrySet()) {
								if (entry.getKey().equals(playerToJoin) && entry.getValue().equals("")) {
									writer.println(playerToJoin + " can be joined");
									foundPlayer = true;
									entry.setValue(username);
									break;
								}
							}
							if (!foundPlayer) {
								writer.println("There is no available player " + playerToJoin + " right now.");
							}

						} else if (Constants.SEARCHING_FOR_PLAYER.equals(commandInput)) {
							String opponentName = null;
							for (Map.Entry<String, String> entry : Server.getAllGames().entrySet()) {
								if (entry.getValue().equals("")) {
									entry.setValue(username);
									opponentName = entry.getKey();
									writer.println(opponentName + " can be joined");
									break;
								}
							}
							if (opponentName == null) {
								writer.println("no available games");
							}

						} else if (Constants.START_GAME.equals(commandInput)) {
							startGame();

						} else if (commandInput.contains(Constants.ATTACK)) {
							int positionIndex = 1;
							String position = seperateString[positionIndex];
							if (opponent == null) {
								opponent = new PrintWriter(
										Server.getUserSocket(Server.getEnemyUsername(username)).getOutputStream(),
										true);
							}
							opponent.println(position + " is under attack");
						} else if (commandInput.equals(Constants.GAME_ENDED)) {
							Server.getAllGames().entrySet()
									.removeIf(e -> e.getValue() == username || e.getKey() == username);
						} else {
							opponent.println(commandInput);
						}
					}
				}
			}
		} catch (IOException e) {
			fixException();
		}
	}

	private void fixException() {
		for (Map.Entry<String, String> entry : Server.getAllGames().entrySet()) {
			if (entry.getKey() == username) {
				if (entry.getValue().equals("")) {
					Server.getAllGames().entrySet().removeIf(game -> game.getKey() == username);
				} else {
					String opponentName = entry.getValue();
					try {
						PrintWriter toOpponent = new PrintWriter(Server.getUserSocket(opponentName).getOutputStream(),
								true);
						Server.getAllGames().entrySet()
								.removeIf(game -> game.getValue() == username || game.getKey() == username);
						Server.getUsers().remove(username);
						Server.getUsers().remove(opponentName);
						toOpponent.println(Constants.OPPONENT_LEFT);
					} catch (IOException e1) {
						throw new RuntimeException(e1);
					}
				}
			} else if (entry.getValue() == username) {
				Server.getAllGames().entrySet()
						.removeIf(game -> game.getValue() == username || game.getKey() == username);
				if (opponent == null) {
					try {
						opponent = new PrintWriter(Server.getUserSocket(entry.getKey()).getOutputStream(), true);
					} catch (IOException e1) {
						throw new RuntimeException(e1);
					}
				}
				Server.getUsers().remove(entry.getKey());
				opponent.println(Constants.OPPONENT_LEFT);
			}
		}
		Server.getUsers().remove(username);
	}
}
