package fr.abes.baconprovider.service;

import fr.abes.baconprovider.entity.Provider;
import fr.abes.baconprovider.exception.FileException;
import fr.abes.baconprovider.exception.IllegalDatabaseOperation;
import fr.abes.baconprovider.repository.ProviderRepository;
import fr.abes.baconprovider.configuration.Constants;
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
        return dao.findAllByOrderByProvider();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Throwable.class})
    public void saveListProvider(List<Provider> providers) throws IllegalDatabaseOperation, FileException {

        for(int i = 0; i < providers.size() - 1; i++){
            Provider provider = providers.get(i);
            if( provider.getProvider() == null || provider.getDisplayName() == null){
                throw new FileException(String.format(Constants.FILE_EXCEPTION_PROVIFER_OR_DISPLAYNAME_NOTNULL, i ));
            }
        }

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
            log.error("Erreur dans la requête sur la base de données pour le provider : " + currentProvider);
            throw new IllegalDatabaseOperation("Erreur dans la requête sur la base de données pour le provider : " + currentProvider + " : " + ex.getMessage(), ex.getCause());
        }
    }

}
