package fr.abes.baconprovider.utils;

import fr.abes.baconprovider.entity.Provider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {ProviderMapper.class, UtilsMapper.class})
class ProviderMapperTest {
    @Autowired
    UtilsMapper mapper;

    @Test
    void testMapperProvider(){
        String[] ligneCsv = {"123","test1","test2","test3","test4","test5"};
        Provider provider = mapper.map(ligneCsv, Provider.class);

        Assertions.assertEquals(123,provider.getIdtProvider());
        Assertions.assertEquals("test1",provider.getProvider());
        Assertions.assertEquals("test2",provider.getNomContact());
        Assertions.assertEquals("test3",provider.getPrenomContact());
        Assertions.assertEquals("test4",provider.getMailContact());
        Assertions.assertEquals("test5",provider.getDisplayName());
    }

    @Test
    void testMapperProvider2(){
        String[] ligneCsv = {"","test1","test2","test3","test4","test5"};
        Provider provider = mapper.map(ligneCsv, Provider.class);

        Assertions.assertNull(provider.getIdtProvider());
        Assertions.assertEquals("test1",provider.getProvider());
        Assertions.assertEquals("test2",provider.getNomContact());
        Assertions.assertEquals("test3",provider.getPrenomContact());
        Assertions.assertEquals("test4",provider.getMailContact());
        Assertions.assertEquals("test5",provider.getDisplayName());
    }

}