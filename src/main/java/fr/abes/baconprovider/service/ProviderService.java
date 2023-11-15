package fr.abes.baconprovider.service;

import fr.abes.baconprovider.entity.Provider;
import fr.abes.baconprovider.exception.IllegalDatabaseOperation;
import fr.abes.baconprovider.repository.ProviderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class ProviderService {
    private final ProviderRepository dao;

    public ProviderService(ProviderRepository dao) {
        this.dao = dao;
    }

    public List<Provider> getProviders() {
        return dao.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Throwable.class})
    public void saveListProvider(List<Provider> providers) throws IllegalDatabaseOperation {
        Set<Provider> newProviders = new HashSet<>();
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
        //ajout des enregistrements à update dans la liste des providers à créer
        newProviders.addAll(providers);
        Provider currentProvider = new Provider();
        try {
            for (Provider provider : newProviders) {
                currentProvider = provider;
                dao.save(provider);
            }
            for (Provider provider : deletedProviders) {
                currentProvider = provider;
                dao.delete(provider);
            }
            dao.flush();
        } catch (DataIntegrityViolationException ex) {
            log.error("Erreur de suppression dans la base de données sur provider " + currentProvider);
            throw new IllegalDatabaseOperation("Impossible de supprimer le provider " + currentProvider + " possédant encore des packages", ex.getCause());
        }
    }

}
