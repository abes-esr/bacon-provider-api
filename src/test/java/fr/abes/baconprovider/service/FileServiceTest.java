package fr.abes.baconprovider.service;

import fr.abes.baconprovider.entity.Provider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {FileService.class})
class FileServiceTest {
    @Autowired
    FileService service;

    File file;

    @BeforeEach
    void init() throws IOException {
        file = File.createTempFile("test", ".csv");
    }

    @Test
    void writeHeaders() throws IOException {
        service.writeHeaders(file, Provider.class);

        BufferedReader reader = new BufferedReader(new FileReader(file));
        Assertions.assertEquals("IDT_PROVIDER,PROVIDER,NOM_CONTACT,PRENOM_CONTACT,MAIL_CONTACT,DISPLAY_NAME", reader.readLine());

        file.delete();
    }

    @Test
    void writeLine1() throws IOException, IllegalAccessException {
        Provider provider = new Provider();
        provider.setProvider("provider");
        provider.setDisplayName("displayName");
        provider.setIdtProvider(1);
        provider.setNomContact("nom");
        provider.setMailContact("mail");
        provider.setPrenomContact("prenom");
        service.writeLine(file, provider);

        BufferedReader reader = new BufferedReader(new FileReader(file));
        Assertions.assertEquals("1,provider,nom,prenom,mail,displayName", reader.readLine());
    }

    @Test
    void writeLine2() throws IOException, IllegalAccessException {
        Provider provider = new Provider();
        provider.setProvider("provider");
        provider.setDisplayName("displayName");
        provider.setIdtProvider(1);
        provider.setNomContact("nom");
        provider.setMailContact("mail");
        provider.setPrenomContact("prenom");

        service.writeLine(file, provider);
        service.writeLine(file, provider);

        BufferedReader reader = new BufferedReader(new FileReader(file));
        Assertions.assertEquals("1,provider,nom,prenom,mail,displayName", reader.readLine());
        Assertions.assertEquals("1,provider,nom,prenom,mail,displayName", reader.readLine());
    }
}