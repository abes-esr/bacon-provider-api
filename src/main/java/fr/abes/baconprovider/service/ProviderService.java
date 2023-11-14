package fr.abes.baconprovider.service;

import fr.abes.baconprovider.entity.Provider;
import fr.abes.baconprovider.repository.ProviderRepository;
import org.springframework.stereotype.Service;

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

}
