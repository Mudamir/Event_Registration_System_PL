package com.example.event_registration_system_pl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;


import java.io.*;
import java.util.List;

public class Database {
    private File file;

    public Database(String filePath) {
        this.file = new File(filePath);
    }

    public void storeToFile(List<String> records) {
        try (FileWriter fWrite = new FileWriter(file, true);
             BufferedWriter writer = new BufferedWriter(fWrite)) {
            boolean fileExists = file.exists();
            if (!fileExists) {
                file.createNewFile();
            }
            for (int i = 0; i < records.size(); i++) {
                writer.write(records.get(i));
                if (i < records.size() - 1) {
                    writer.write(",");
                }
            }
            writer.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String[]> readFromFile() {
        List<String[]> records = new ArrayList<>();

        try (FileReader fRead = new FileReader(file);
             BufferedReader reader = new BufferedReader(fRead)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                records.add(fields);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return records;
    }
}