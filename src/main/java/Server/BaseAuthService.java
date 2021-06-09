package Server;

import java.sql.*;

/**
 * Простейшая реализация сервиса аутентификации, которая работает на встоенном списке
 */
public class BaseAuthService implements AuthService {
    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement preparedStatement;

    /*private class Entry{
        private final Integer id;
        private final String nick;
        private final String login;
        private final String pass;

        public Entry(Integer id, String nick, String login, String pass) {
            this.id = id;
            this.nick = nick;
            this.login = login;
            this.pass = pass;
        }
    }

    private List<Entry> entries;

    public  BaseAuthService() {
        //////
        entries = List.of(
            new Entry(1, "nick1", "login1", "pass1"),
            new Entry(2, "nick2", "login2", "pass2"),
            new Entry(3, "nick3", "login3", "pass3")
        );
    }*/

    @Override
    public boolean start() {
        try {
            connectDB();
            System.out.println(this.getClass().getName() + " server started");
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public void stop() {
        disconnectDB();
        ServerApp.LOGGER.info(this.getClass().getName() + " server stopped");
    }

    private void connectDB() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:users.db");
        statement = connection.createStatement();
        prepareTable();
    }

    private void disconnectDB() {
        try {
            if (statement != null) statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        try {
            if (connection != null) connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void prepareTable() throws SQLException {
        statement.execute("create table if not exists users (id integer primary key autoincrement, nickname text, login text, password text);");
    }

    @Override
    public String getNickByLoginAndPass(String login, String password) {

        String selectNick = "select nickname from users where login = ? and password = ? limit 1;";

        try {
            preparedStatement = connection.prepareStatement(selectNick);
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException ex) {
            ServerApp.LOGGER.warn(ex.getMessage());
        }

        return null;

        /*return entries.stream()
                .filter(entry -> entry.login.equals(login) && entry.pass.equals(pass))
                .map(entry -> entry.nick)
                .findFirst().orElse(null);*/

        /*for (Entry entry : entries) {
            if (entry.login.equals(login) && entry.pass.equals(pass)) {
                return entry.nick;
            }
        }
        return null;*/
    }

    @Override
    public boolean addNewUser(String nickname, String login, String password){

        try {
            preparedStatement = connection.prepareStatement("select nickname from users where nickname = ? limit 1;");
            preparedStatement.setString(1, nickname);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return false;
            }

            preparedStatement = connection.prepareStatement("insert into users (nickname, login, password) values (?, ?, ?);");
            preparedStatement.setString(1, nickname);
            preparedStatement.setString(2, login);
            preparedStatement.setString(3, password);
            preparedStatement.execute();
        } catch (SQLException ex) {
            ServerApp.LOGGER.error(ex.getMessage());
        }

        return true;
    }

    @Override
    public boolean changeNickname(String nickname, String newNickname) {

        try {
            // check for user presence
            preparedStatement = connection.prepareStatement("select nickname from users where nickname = ? limit 1;");
            preparedStatement.setString(1, nickname);
            if (!preparedStatement.execute()) {
                return false;
            }

            // check for new user
            preparedStatement = connection.prepareStatement("select nickname from users where nickname = ? limit 1;");
            preparedStatement.setString(1, newNickname);
            if (preparedStatement.execute()) {
                return false;
            }

            // change nick
            preparedStatement = connection.prepareStatement("update users set nickname = ? where nickname = ?;");
            preparedStatement.setString(1, newNickname);
            preparedStatement.setString(2, nickname);
            if (preparedStatement.executeUpdate() > 0) {
                return true;
            }
        } catch (SQLException ex) {
            ServerApp.LOGGER.error(ex.getMessage());
        }

        return false;
    }

    @Override
    public boolean changePassword(String login, String password, String newPassword) {

        try {
            preparedStatement = connection.prepareStatement("select nickname from users where login = ? and password = ? limit 1;");
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next() == false) {
                return false;
            }

            preparedStatement = connection.prepareStatement("update users set password = ? where nickname = ?;");
            preparedStatement.setString(1, newPassword);
            preparedStatement.setString(2, rs.getString("nickname"));
            if (preparedStatement.executeUpdate() > 0) {
                return true;
            }
        } catch (SQLException ex) {
            ServerApp.LOGGER.error(ex.getMessage());
        }

        return false;
    }
}
