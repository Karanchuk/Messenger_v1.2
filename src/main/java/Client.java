
import Server.ChatConstants;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {

    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private Controller controller;
    private MessageLog messageLog;

    public Client(Controller controller) {
        this.controller = controller;
        try {
            openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openConnection() throws IOException {
        this.socket = new Socket(ChatConstants.HOST, ChatConstants.PORT);
        this.inputStream = new DataInputStream(this.socket.getInputStream());
        this.outputStream = new DataOutputStream(this.socket.getOutputStream());

        (new Thread(() -> {
            try {
                // auth
                while (true) {
                    String strFromServer = inputStream.readUTF();
                    if (strFromServer.startsWith(ChatConstants.AUTH_OK)) {
                        messageLog = new MessageLog(strFromServer.replace("/authok ", ""));
                        showMessage(strFromServer, true);
                        showMessage(messageLog.readLast100Lines(), true);
                        break;
                    }
                }

                // read
                while (true) {
                    String strFromServer = inputStream.readUTF();
                    if (strFromServer.equalsIgnoreCase("/end")) {
                        break;
                    }
                    showMessage(strFromServer, false);
                }
            } catch (IOException ex) {
                System.out.println("Connection closed");
            }
        })).start();

    }

    public void closeConnection() {
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (messageLog != null) {
            try {
                messageLog.closeMessageLog();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public void showMessage(String message, boolean withoutLogging) {
        controller.appendToTextArea(message);
        if (withoutLogging) {
            return;
        }
        try {
            messageLog.writeLine(message);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        if (!message.trim().isEmpty()) {
            try {
                outputStream.writeUTF(message);
                /*if (messageLog != null) {
                    messageLog.writeLine(message);
                }*/
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error sending message");
            }
        }
    }

}
