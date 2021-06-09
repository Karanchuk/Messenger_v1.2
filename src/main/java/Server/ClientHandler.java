package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * Отвечает за обмен между клиентом и сервером (обслуживает клиента)
 */
public class ClientHandler {
    private MyServer server;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    private  String name;

    public String getName() {
        return name;
    }

    public ClientHandler(MyServer server, Socket socket, ExecutorService executorService) {
        try {
            this.socket = socket;
            this.server = server;
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
            this.name = "";

            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        authentication();
                        if (!socket.isClosed()) {
                            readMessages();
                        }
                    } catch (IOException exception) {
                        ServerApp.LOGGER.error(exception.getMessage());
                    } finally {
                        closeConnection();
                    }
                }
            });
            /*new Thread(() -> {
                try {
                    authentication();
                    if (!this.socket.isClosed()) {
                        readMessages();
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                } finally {
                    closeConnection();
                }
            }).start();*/
            
        } catch (IOException ex) {
            ServerApp.LOGGER.warn("Проблема при создании клиента");
        }
    }

    // /auth login pass - хотим отправлять такое сообщение в потоке для авторизации
    private void authentication() throws IOException {
        long currentTimeMillis = System.currentTimeMillis();
        while (true) {
            if (inputStream.available() != 0) {
                currentTimeMillis = System.currentTimeMillis();
                String message = inputStream.readUTF();
                if (message.startsWith(ChatConstants.REG)) { // создание нового пользователя
                    String[] parts = message.split("\\s+");
                    if (server.getAuthService().addNewUser(parts[1], parts[2], parts[3])) {
                        sendMsg(ChatConstants.AUTH_OK + " " + parts[1]);
                        name = parts[1];
                        server.subscribe(this);
                        server.broadcastMessage(name, name + " вошел в чат");
                        return;
                    } else {
                        sendMsg("Ник уже существует");
                    }
                } else if (message.startsWith(ChatConstants.AUTH_COMMAND)) {
                    String[] parts = message.split("\\s+"); // разбивает строку по пробелам на массив строк длиной 3
                    String nick = server.getAuthService().getNickByLoginAndPass(parts[1], parts[2]);
                    if (nick != null) { // Проверяем корректность логина + пароля
                        if (!server.isNickBusy(nick)) { // Проверяем, что в чате нет человека с таким именем
                            sendMsg(ChatConstants.AUTH_OK + " " + nick);
                            name = nick;
                            server.subscribe(this);
                            server.broadcastMessage(name, name + " вошел в чат");
                            return;
                        } else {
                            sendMsg("Ник уже используется");
                        }
                    } else {
                        sendMsg("Неверные логин/пароль");
                    }
                }
            } else if (System.currentTimeMillis() - currentTimeMillis > (long) 15000) {
                closeStreamAndSocket();
                ServerApp.LOGGER.info("Соединение завершено по таймауту");
                return;
            }
        }
    }

    public void sendMsg(String message) {
        try {
            outputStream.writeUTF(message);
        } catch (IOException exception) {
            ServerApp.LOGGER.error(exception.getMessage());
        }
    }

    private void readMessages() throws IOException {
        while (true) {
            String messageFromClient = inputStream.readUTF();
            ServerApp.LOGGER.info("от " + name + ": " + messageFromClient);
            if (messageFromClient.equals(ChatConstants.STOP_WORD)) {
                return;
            } else if (messageFromClient.startsWith(ChatConstants.RENAME)) {
                String[] parts = messageFromClient.split("\\s+");
                if (parts.length >= 2) {
                    messageFromClient = ChatConstants.DIRECT + " " + name + " ошибка при изменении имени";
                    if (server.getAuthService().changeNickname(name, parts[1])) {
                        messageFromClient = name  + " изменил имя на " + parts[1];
                        name = parts[1];
                    }
                }
            } else if (messageFromClient.startsWith(ChatConstants.CHA_PASS)) {
                String[] parts = messageFromClient.split("\\s+");
                if (parts.length >=4) {
                    messageFromClient = ChatConstants.DIRECT + " " + name + " ошибка при изменении пароля";
                    if (server.getAuthService().changePassword(parts[1], parts[2], parts[3])) {
                        messageFromClient = ChatConstants.DIRECT + " " + name + " пароль успешно изменен";
                    }
                }
            }
            server.broadcastMessage(name, "[" + name + "]: " + messageFromClient);
        }
    }
    public void closeConnection() {
        server.unsubscribe(this);
        server.broadcastMessage(name, name + " вышел из чата");
        closeStreamAndSocket();
    }

    private void closeStreamAndSocket() {
        try {
            inputStream.close();
        } catch (IOException exception) {
            ServerApp.LOGGER.error(exception.getMessage());
        }
        try {
            outputStream.close();
        } catch (IOException exception) {
            ServerApp.LOGGER.error(exception.getMessage());
        }
        try {
            socket.close();
        } catch (IOException exception) {
            ServerApp.LOGGER.error(exception.getMessage());
        }
    }
}
