package fr.abes.baconprovider.service;

import jakarta.persistence.Column;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
}
