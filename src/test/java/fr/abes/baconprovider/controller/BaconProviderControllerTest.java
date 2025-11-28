package fr.abes.baconprovider.controller;

import fr.abes.baconprovider.configuration.RestConfiguration;
import fr.abes.baconprovider.entity.Provider;
import fr.abes.baconprovider.exception.ExceptionControllerHandler;
import fr.abes.baconprovider.service.FileService;
import fr.abes.baconprovider.service.ProviderPackageDeletedService;
import fr.abes.baconprovider.service.ProviderService;
import fr.abes.baconprovider.utils.UtilsMapper;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {BaconProviderController.class, FileService.class, UtilsMapper.class})
@ContextConfiguration(classes = RestConfiguration.class)
class BaconProviderControllerTest {
    @Autowired
    WebApplicationContext context;

    @MockitoBean
    ProviderService service;

    @MockitoBean
    ProviderPackageDeletedService providerPackageDeletedService;

    MockMvc mockMvc;

    @Autowired
    UtilsMapper mapper;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(context.getBean(BaconProviderController.class))
                .setControllerAdvice(new ExceptionControllerHandler())
                .build();
    }

    @Test
    void getProviders() throws Exception {
        Provider provider1 = new Provider();
        provider1.setProvider("provider1");
        provider1.setDisplayName("displayName1");
        provider1.setIdtProvider(1);
        provider1.setNomContact("nom1");
        provider1.setMailContact("mail1");
        provider1.setPrenomContact("prénom1");

        Provider provider2 = new Provider();
        provider2.setProvider("provider2");
        provider2.setDisplayName("displayName2");
        provider2.setIdtProvider(2);
        provider2.setNomContact("nom2");
        provider2.setMailContact("mail2");
        provider2.setPrenomContact("prénom2");

        String resultStr = "IDT_PROVIDER;PROVIDER;NOM_CONTACT;PRENOM_CONTACT;MAIL_CONTACT;DISPLAY_NAME" + System.lineSeparator() +
                "1;provider1;nom1;prénom1;mail1;\"displayName1\"" +
                System.lineSeparator() +
                "2;provider2;nom2;prénom2;mail2;\"displayName2\"" +
                System.lineSeparator();

        Mockito.when(service.getProviders()).thenReturn(Lists.newArrayList(provider1, provider2));
        this.mockMvc.perform(get("/api/v1/providers").characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(result1 -> Assertions.assertEquals("application/force-download", result1.getResponse().getContentType()))
                .andExpect(result1 -> Assertions.assertTrue(result1.getResponse().getContentLength() > 0))
                .andExpect(result -> Assertions.assertEquals(resultStr, result.getResponse().getContentAsString(StandardCharsets.UTF_8)));

    }

    @Test
    void postProviders() throws Exception {
        String fileContent = "IDT_PROVIDER;PROVIDER;NOM_CONTACT;PRENOM_CONTACT;MAIL_CONTACT;DISPLAY_NAME\n" +
                "1;test;test;test;test;\"test\"";
        MockMultipartFile file = new MockMultipartFile("test.csv", fileContent.getBytes());
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/providers")
                .file("file", file.getBytes()))
                .andExpect(status().isOk());

        fileContent = "IDT_PROVIDER;PROVIDER;NOM_CONTACT;PRENOM_CONTACT;MAIL_CONTACT;DISPLAY_NAME\n" +
                "1;test;test;test;test";
        file = new MockMultipartFile("test.csv", fileContent.getBytes());
        ResultActions res = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/providers")
                .file("file", file.getBytes()))
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResponse().getContentAsString().contains("Une des lignes du fichier ne contient pas le même nombre de colonnes que la table PROVIDER")));
    }
}
