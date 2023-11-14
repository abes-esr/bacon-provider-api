package fr.abes.baconprovider.service;

import fr.abes.baconprovider.configuration.Constants;
import fr.abes.baconprovider.exception.FileException;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Arrays;

@Service
public class FileService {
    private final String SEPARATOR = ";";
    public void writeHeaders(File file, Class clazz) throws IOException {
        StringBuilder line = new StringBuilder();
        int i = 0;
        for (Field field : clazz.getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);
            line.append(column.name());
            if (i != clazz.getDeclaredFields().length - 1)
                line.append(SEPARATOR);
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
            if( field.get(objet) != null ){
                if (field.getName().equals("displayName"))
                    line.append("\"").append(field.get(objet)).append("\"");
                else
                    line.append(field.get(objet));
            }
            field.setAccessible(false);
            if (i != objet.getClass().getDeclaredFields().length - 1)
                line.append(SEPARATOR);
            i++;
        }
        FileWriter toWrite = new FileWriter(file, true);
        toWrite.write(line + System.lineSeparator());
        toWrite.close();
    }

    public void checkCsvFile(File file, Class clazz) throws FileException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            if (!file.getName().endsWith(".csv"))
                throw new FileException(Constants.FILE_EXCEPTION_WRONG_EXTENSION);

            String headers = reader.readLine();
            if (headers == null) {
                throw new FileException(Constants.FILE_EXCEPTION_MISSING_HEADER);
            }
            int nbColumnObject = clazz.getDeclaredFields().length;
            for (Field field : clazz.getDeclaredFields()) {
                Column column = field.getAnnotation(Column.class);
                if (Arrays.stream(headers.split(SEPARATOR)).noneMatch(header -> header.equals(column.name()))) {
                    throw new FileException(String.format(Constants.FILE_EXCEPTION_MISSING_COLUMN, column.name()));
                }
            }

            if (!reader.lines().filter(line -> line.split(SEPARATOR).length != nbColumnObject).toList().isEmpty()) {
                throw new FileException(Constants.FILE_EXCEPTION_WRONG_NB_COLUMN + ((Table) clazz.getAnnotation(Table.class)).name());
            }
        } catch (IOException ex) {
            throw new FileException(Constants.FILE_EXCEPTION_ERROR_READ);
        }

    }
}
