import java.io.*;
import java.util.ArrayList;

public class MessageLog {
    private final String nickname;
    private BufferedWriter writer;
    private final File file;

    public MessageLog(String nickname) {
        this.nickname = nickname;
        File dir = new File("log");
        if (!dir.exists()) {
            dir.mkdir();
        }
        file = new File(dir.getPath() + "/" + nickname + ".txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public void writeLine(String line) throws IOException {
        if (writer == null ) {
            FileWriter fileWriter = new FileWriter("log/" + nickname + ".txt", true);
            writer = new BufferedWriter(fileWriter);
        }
        writer.append(line + "\n");
    }

    public String readLast100Lines() throws IOException {
        BufferedReader reader = null;
        String lines = "";

        FileReader fileReader = null;
        try {
            fileReader = new FileReader("log/" + nickname + ".txt");
        } catch (FileNotFoundException e) {
            return lines;
        }

        if (reader == null) reader = new BufferedReader(fileReader);

        ArrayList<String> list = new ArrayList<>();
        String line = "";
        while ((line = reader.readLine()) != null) {
            list.add(line);
        }

        reader.close();

        for (int i = ((list.size() - 100) > 0 ? (list.size() - 100 - 1) : 0); i < list.size(); i++) {
            lines += list.get(i) + "\n";
        }

        return lines;
    }

    public void closeMessageLog() throws IOException {
        if (writer != null ) writer.close();
    }
}
