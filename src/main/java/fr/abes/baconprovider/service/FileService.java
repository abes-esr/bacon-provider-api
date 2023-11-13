package fr.abes.baconprovider.service;

import fr.abes.baconprovider.entity.Provider;
import jakarta.persistence.Column;
import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.reflect.Field;

@Service
public class FileService {

    public void writeHeaders(File file, Class clazz) throws IOException {
        StringBuilder line = new StringBuilder();
        int i = 0;
        for (Field field : clazz.getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);
            line.append(column.name());
            if (i != clazz.getDeclaredFields().length - 1)
                line.append(",");
            i++;
        }
        FileWriter toWrite = new FileWriter(file);
        toWrite.write(line + System.lineSeparator());
        toWrite.close();
    }

    public void writeLine(File file, Object objet) throws IllegalAccessException, IOException {
        StringBuilder line = new StringBuilder();
        int i = 0;
        for (Field field : objet.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getName().equals("displayName"))
                line.append("\"").append(field.get(objet)).append("\"");
            else
                line.append(field.get(objet));
            field.setAccessible(false);
            if (i != objet.getClass().getDeclaredFields().length - 1)
                line.append(",");
            i++;
        }
        FileWriter toWrite = new FileWriter(file, true);
        toWrite.write(line + System.lineSeparator());
        toWrite.close();
    }

    public boolean checkFileCSVForProviders(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String headers = reader.readLine();
        if(headers == null)
            return false;
        for (Field field : Provider.class.getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);
            if(!headers.contains(column.name()))
                return false;
        }
        return file.getName().endsWith(".csv");
    }
}
