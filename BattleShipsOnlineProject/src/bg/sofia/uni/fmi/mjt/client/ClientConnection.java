package bg.sofia.uni.fmi.mjt.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import bg.sofia.uni.fmi.mjt.constants.Constants;

public class ClientConnection implements Runnable {

	private Socket socket;
	private Client client;

	public ClientConnection(Socket socket, Client client) {
		this.socket = socket;
		this.client = client;
	}

	@Override
	public void run() {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
			while (true) {
				if (socket.isClosed()) {
					System.out.println(
							"client socket is closed and you are disconnected from server, stop waiting for server messages");
					client.setInMenuFalse();
					client.setConnectedFalse();
					return;
				}

				String messageFromServer = reader.readLine();
				if (Constants.DISCONNECTED.equals(messageFromServer)) {
					System.out.println(String.format("disconnected from server on %s:%d",
							socket.getInetAddress().getHostName(), socket.getPort()));

					reader.close();
					socket.close();
					client.closeWriter();
					client.setInGameFalse();
					client.setInMenuFalse(); 
					client.setConnectedFalse();
					break;

				} else if (messageFromServer.equals(Constants.USERNAME_TAKEN)) {
					System.out.println(messageFromServer + ".Choose other username.");
					socket.close();
				}

				else if (messageFromServer.contains(Constants.CAN_BE_JOINED)) {
					String usernameToJoin = messageFromServer.split(Constants.SPACE_SPLITTER)[0];
					client.setOpponentToJoinName(usernameToJoin);
					client.setSeachingPlayerStatusToFound();
				}

				else if (messageFromServer.contains(Constants.NO_AVAILABLE_PLAYER)
						|| messageFromServer.contains(Constants.NO_AVAILABLE_GAME)) {
					client.setSeachingPlayerStatusToNotFound();
					System.out.println(messageFromServer);
				}

				else if (messageFromServer.contains(Constants.HAS_JOINED_YOUR_GAME)) {
					System.out.println(messageFromServer);
					client.setInGame();
					System.out.println("It is your turn.Attack enemy board (example: attack A1)");

				} else if (messageFromServer.contains(Constants.UNDER_ATTACK)) {
					int positionIndex = 0;
					String position = messageFromServer.split(Constants.SPACE_SPLITTER)[positionIndex];
					System.out.println("Your enemy attacked " + position + " Now is your turn!");
					client.getBoard().hit(position);
					client.sendBoardToEnemyAfterAttack();
					if (!client.checkIfGameIsLost()) {
						client.setTurn();
					}

				} else if (messageFromServer.contains(Constants.GAME_WON)) {
					System.out.println(messageFromServer);
					client.afterGameFixing();

				} else if (messageFromServer.equals(Constants.OPPONENT_LEFT)) {
					System.out.println(messageFromServer);
					reader.close();
					socket.close();
					client.closeWriter();
					client.setInGameFalse();
					client.setInMenuFalse();
					client.setConnectedFalse();
					System.exit(0);
				} else {
					if (!client.theGameEnded()) {
						System.out.println(messageFromServer);
					}

				}
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

}