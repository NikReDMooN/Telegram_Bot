import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;



import java.util.Map;

public class CreateHelpData {
    public CreateHelpData() {
        if (!Files.exists(Path.of("C:\\StudioVoice\\kol_seats.txt"))) {
            try {
                System.out.println("ПРОГРАМА НЕ БУДЕТ ПРАВИЛЬНО РАБОТАТЬ, ФАЙЛ НЕ БЫЛ ЗАРАНЕЕ СОЗДАН!!!!");
                Files.createFile(Path.of("C:\\StudioVoice\\kol_seats.txt"));
            } catch (IOException e) {
                System.out.println("nooo");
                throw new RuntimeException(e);
            }
        }
    }

    public HashMap<String, Integer> ParcDatesAndKol() {
        HashMap <String, Integer> parceinfo = new HashMap<String, Integer>();
        StringBuilder txt = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader("C:\\StudioVoice\\kol_seats.txt"));
            String line = br.readLine();
            while(line != null){
                String[] strings = line.split(" ");
                System.out.println(strings[1]);
                Integer kol = Integer.valueOf(Integer.parseInt(strings[1]));
                parceinfo.put(strings[0], kol);
                line = br.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return parceinfo;
    }

    public void write_new_info(HashMap<String, Integer> newinfo) {
        clearTheFile();
        Map <String, Integer> map = newinfo;
        try {

            FileWriter writer = new FileWriter("C:\\StudioVoice\\kol_seats.txt" );
            for(String key : map.keySet()) {
                writer.write(key + " " + newinfo.get(key) + "\n");
                writer.flush();
            }
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void clearTheFile() {
        try {
            new FileWriter("C:\\StudioVoice\\kol_seats.txt", false).close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
