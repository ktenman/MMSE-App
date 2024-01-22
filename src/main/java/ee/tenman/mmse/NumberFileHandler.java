package ee.tenman.mmse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class NumberFileHandler {
    private static final String FILE_PATH = "openai.txt";

    public static void incrementNumberInFile() {
        try {
            File file = new File(FILE_PATH);

            // Check if the file exists
            if (!file.exists()) {
                // If the file doesn't exist, create it with an initial value of 1
                file.createNewFile();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
                    writer.write("1");
                }
            } else {
                // If the file exists, read the current value, increment it, and write it back
                try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
                    String line = reader.readLine();
                    int currentValue = Integer.parseInt(line);
                    int newValue = currentValue + 1;
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
                        writer.write(Integer.toString(newValue));
                    }
                }
            }
            System.out.println("Number incremented successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
