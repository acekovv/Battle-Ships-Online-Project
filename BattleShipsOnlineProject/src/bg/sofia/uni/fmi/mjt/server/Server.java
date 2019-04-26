package bg.sofia.uni.fmi.mjt.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import bg.sofia.uni.fmi.mjt.constants.Constants;

public class Server {
	private static Map<String, Socket> users = new ConcurrentHashMap<>();
	private static Map<String, String> gamesBetweenPlayers = new ConcurrentHashMap<>();

	public static Map<String, Socket> getUsers() {
		return users;
	}

	public static int getGamesSize() {
		return gamesBetweenPlayers.size();
	}

	public static Map<String, String> getAllGames() {
		return gamesBetweenPlayers;
	}

	public static Socket getUserSocket(String user) {
		return users.get(user);
	}

	public static String getEnemyUsername(String user) {
		return gamesBetweenPlayers.get(user);
	}

	private void run() {
		try (ServerSocket serverSocket = new ServerSocket(Constants.PORT)) {
			System.out.printf("server is running on localhost:%d%n", Constants.PORT);

			while (true) {
				Socket socket = serverSocket.accept();
				System.out.println("A client connected to server " + socket.getInetAddress());

				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String username = reader.readLine();

				if (users.containsKey(username)) {
					System.out.println("this username was already taken");
					PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
					writer.println("this username was already taken");
					continue;
				}
				users.put(username, socket);
				System.out.println(username + " connected");

				ClientConnectionHandler runnable = new ClientConnectionHandler(socket, username);
				new Thread(runnable).start();
				System.out.println("new thread started");
			}
		} catch (IOException e) {
			System.out.println("maybe another server is running or port 8080");
		}
	}
 
	public static void main(String[] args) {
		new Server().run();
	}
}