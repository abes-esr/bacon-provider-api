package fr.abes.baconprovider.service;

import fr.abes.baconprovider.configuration.Constants;
import fr.abes.baconprovider.entity.Provider;
import fr.abes.baconprovider.exception.FileException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;

@SpringBootTest(classes = {FileService.class})
class FileServiceTest {
    @Autowired
    FileService service;

    File file;

    @Value("classpath:checkFileOk.csv")
    private File fileOk;

    @Value("classpath:checkFileWrongExtension.txt")
    private File fileWrongExtension;

    @Value("classpath:checkFileWrongHeader.csv")
    private File fileWrongHeader;

    @Value("classpath:checkFileWrongColumnNumber.csv")
    private File checkFileWrongColumnNumber;

    @BeforeEach
    void init() throws IOException {
        file = File.createTempFile("test", ".csv");
    }

    @Test
    void writeHeaders() throws IOException {
        service.writeHeaders(file, Provider.class);

        BufferedReader reader = new BufferedReader(new FileReader(file));
        Assertions.assertEquals("IDT_PROVIDER;PROVIDER;NOM_CONTACT;PRENOM_CONTACT;MAIL_CONTACT;DISPLAY_NAME", reader.readLine());

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
        Assertions.assertEquals("1;provider;nom;prenom;mail;\"displayName\"", reader.readLine());
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
        provider.setDisplayName("displayName2");
        service.writeLine(file, provider);

        BufferedReader reader = new BufferedReader(new FileReader(file));
        Assertions.assertEquals("1;provider;nom;prenom;mail;\"displayName\"", reader.readLine());
        Assertions.assertEquals("1;provider;nom;prenom;mail;\"displayName2\"", reader.readLine());
    }

    @Test
    void checkFileCSVForProviders() throws FileException {
        service.checkCsvFile(fileOk, Provider.class);

        FileException exception = Assertions.assertThrows(FileException.class, () -> service.checkCsvFile(fileWrongExtension, Provider.class));
        Assertions.assertEquals(Constants.FILE_EXCEPTION_WRONG_EXTENSION, exception.getMessage());

        exception = Assertions.assertThrows(FileException.class, () -> service.checkCsvFile(fileWrongHeader, Provider.class));
        Assertions.assertEquals(String.format(Constants.FILE_EXCEPTION_MISSING_COLUMN, "PROVIDER"), exception.getMessage());

        exception = Assertions.assertThrows(FileException.class, () -> service.checkCsvFile(checkFileWrongColumnNumber, Provider.class));
        Assertions.assertEquals(Constants.FILE_EXCEPTION_WRONG_NB_COLUMN + "PROVIDER", exception.getMessage());

        File nonExistingFile = new File("");
        exception = Assertions.assertThrows(FileException.class, () -> service.checkCsvFile(nonExistingFile, Provider.class));
        Assertions.assertEquals(Constants.FILE_EXCEPTION_ERROR_READ, exception.getMessage());
    }
}
