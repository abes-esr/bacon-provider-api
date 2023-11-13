package fr.abes.baconprovider.controller;

import fr.abes.baconprovider.configuration.RestConfiguration;
import fr.abes.baconprovider.entity.Provider;
import fr.abes.baconprovider.exception.ExceptionControllerHandler;
import fr.abes.baconprovider.service.FileService;
import fr.abes.baconprovider.service.ProviderService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {BaconProviderController.class, FileService.class})
@ContextConfiguration(classes = RestConfiguration.class)
class BaconProviderControllerTest {
    @Autowired
    WebApplicationContext context;

    @InjectMocks
    BaconProviderController controller;

    @MockBean
    ProviderService service;
    MockMvc mockMvc;

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

        StringBuilder resultStr = new StringBuilder("IDT_PROVIDER,PROVIDER,NOM_CONTACT,PRENOM_CONTACT,MAIL_CONTACT,DISPLAY_NAME");
        resultStr.append(System.lineSeparator());
        resultStr.append("1,provider1,nom1,prénom1,mail1,displayName1");
        resultStr.append(System.lineSeparator());
        resultStr.append("2,provider2,nom2,prénom2,mail2,displayName2");
        resultStr.append(System.lineSeparator());

        Mockito.when(service.getProviders()).thenReturn(Lists.newArrayList(provider1, provider2));
        this.mockMvc.perform(get("/api/v1/providers").characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(result1 -> Assertions.assertTrue(result1.getResponse().getContentType().equals("application/force-download")))
                .andExpect(result1 -> Assertions.assertTrue(result1.getResponse().getContentLength() > 0))
                .andExpect(result -> Assertions.assertEquals(resultStr, result.getResponse().getContentAsString(StandardCharsets.UTF_8)));

    }
}