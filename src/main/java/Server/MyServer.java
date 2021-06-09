package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Непосредственно сервер
 */
public class MyServer {
    private List<ClientHandler> clients;
    private AuthService authService;
    private ExecutorService executorService = Executors.newCachedThreadPool();


    public MyServer() {
        try (ServerSocket server = new ServerSocket(ChatConstants.PORT)){
            authService = new BaseAuthService();
            if (authService.start()) {
                clients = new ArrayList<>();
                while (true) {
                    ServerApp.LOGGER.info("Сервер ожидает подключения");
                    Socket socket = server.accept();
                    ServerApp.LOGGER.info("Клиент подключился");
                    new ClientHandler(this, socket, executorService); // Создаем нового клиента, передаем ему сервер и сокет
                }
            }
        } catch (IOException exception) {
            ServerApp.LOGGER.error(exception.getMessage());
        } finally {
            if (authService != null) {
                authService.stop();
                executorService.shutdown();
            }
        }
    }

    public AuthService getAuthService() {
        return authService;
    }

    /**
     * synchronized для того, чтобы одновременно несколько пользователей
     * не подключались по одним авторизационным данным
     */
    public synchronized boolean isNickBusy(String nick) {
        return clients.stream().anyMatch(client -> client.getName().equals(nick));
        /*for (ClientHandler client : clients) {
            if (client.getName().equals(nick)) {
                return true;
            }
        }
        return false;*/
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    /**
     * Отправляет сообщение всем пользователям либо выбранному
     */
    public void broadcastMessage(String name, String message) {

        String[] parts = message.split("\\s+");
        if (parts[1].equals(ChatConstants.DIRECT) && parts.length > 3) {
            String newMessage = parts[0] + " " + Arrays.stream(parts).skip(3).collect(Collectors.joining(" "));
            clients.stream().filter(c -> c.getName().equals(name) || c.getName().equals(parts[2])).forEach(c -> c.sendMsg(newMessage));
        } else {
            clients.forEach(client -> client.sendMsg(message));
        }
    }
}
