package Server;

/**
 * Сервис авторизации
 */
public interface AuthService {
    /**
     * Запустить сервис
     */
    boolean start();

    /**
     * Остановить сервис
     */
    void stop();

    /**
     * Получить name
     * @param login
     * @param pass
     * @return
     */
    String getNickByLoginAndPass(String login, String pass);

    boolean addNewUser(String nickname, String login, String password);

    boolean changeNickname(String nickname, String newNickname);

    boolean changePassword(String login, String password, String newPassword);


}
