package fr.abes.baconprovider.service;

import fr.abes.baconprovider.entity.Provider;
import fr.abes.baconprovider.repository.ProviderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProviderService {
    private final ProviderRepository dao;

    public ProviderService(ProviderRepository dao) {
        this.dao = dao;
    }

    public List<Provider> getProviders() {
        return dao.findAll();
    }

    @Transactional
    public void saveListProvider(List<Provider> providers) {
        List<Provider> newProviders = new ArrayList<>();
        List<Provider> deletedProviders = new ArrayList<>();
        //récupération des enregistrements existants pour faire un diff
        List<Provider> providerInBdd = dao.findAll();

        //alimentation de la liste des providers supprimés (présents en bdd mais absent de la liste en paramètre)
        for (Provider provider : providerInBdd) {
            if (!(providers.contains(provider))) {
                deletedProviders.add(provider);
            }
        }
        //alimentation de la liste des nouveaux providers (présents en paramètre mais absents en bdd)
        for (Provider provider : providers) {
            if (!(providerInBdd.contains(provider))) {
                newProviders.add(provider);
            }
        }

        dao.saveAll(newProviders);
        dao.deleteAll(deletedProviders);
    }

}
