import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class CreateData {

    public CreateData() {
        if (!Files.exists(Path.of("C:\\StudioVoice\\guests.txt"))) {
            try {
                Files.createFile(Path.of("C:\\StudioVoice\\guests.txt"));
            } catch (IOException e) {
                System.out.println("nooo");
                throw new RuntimeException(e);
            }
        }
    }

    public void write_name(String guest) {
        try {

            FileWriter writer = new FileWriter("C:\\StudioVoice\\guests.txt", true );
            writer.write(guest + " ");
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void write_data(String data) {
        try {
            System.out.println("data = " + data);
            FileWriter writer = new FileWriter("C:\\StudioVoice\\guests.txt", true );
            writer.write("DATA: " + data + "\n");
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public StringBuilder make_txt() {
        StringBuilder txt = new StringBuilder();
       try {
           BufferedReader br = new BufferedReader(new FileReader("C:\\StudioVoice\\guests.txt"));
           String line = br.readLine();
           while(line != null){
               txt.append(line);
               txt.append(System.lineSeparator());
               line = br.readLine();
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
       return txt;

    }



}
