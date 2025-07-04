# bacon-provider-api
permet la mise des à jour des providers par l'équipe BACON (ajout du DISPLAY_NAME)
[Swagger UI](http://diplotaxis2-dev.v212.abes.fr:15086/swagger-ui/index.html)

## Développement

### Génération de l'image docker
Vous pouvez avoir besoin de générer en local l'image docker de ``bacon-provider-api`` par exemple si vous cherchez à modifier des liens entre les conteneurs docker de l'application.

Pour générer l'image docker de ``bacon-provider-api`` en local voici la commande à lancer :
```bash
cd bacon-provider-api/
docker build -t abesesr/convergence:develop-bacon-provider-api .
```

Cette commande aura pour effet de générer une image docker sur votre poste en local avec le tag ``develop-bacon-provider-api``. Vous pouvez alors déployer l'application en local avec docker en vous utilisant sur le [dépot ``convergence-bacon-docker``](https://github.com/abes-esr/convergence-bacon-docker) et en prenant soins de régler la variable ``BACON_PROVIDER_API_VERSION`` sur la valeur ``develop-bacon-provider-api`` (c'est sa [valeur par défaut](https://github.com/abes-esr/convergence-bacon-docker/blob/bdcd4302131eb86688ae729b0fc016d128f1ab9c/.env-dist#L9)) dans le fichier ``.env`` de votre déploiement [``convergence-bacon-docker``](https://github.com/abes-esr/convergence-bacon-docker).

Vous pouvez utiliser la même procédure pour générer en local les autres images docker applications composant l'architecture, la seule chose qui changera sera le nom du tag docker.


Cette commande suppose que vous disposez d'un environnement Docker en local : cf la [FAQ dans la poldev](https://github.com/abes-esr/abes-politique-developpement/blob/main/10-FAQ.md#configuration-dun-environnement-docker-sous-windows-10).

## Documentation utilisateur

Ce programme permet la mise à jour de la table PROVIDER de la base Bacon. Pour cela, trois étapes sont nécessaires : 
- La récupération du contenu de la table dans son état actuel dans un fichier csv
- La modification du fichier csv récupéré
- L'envoi du fichier csv modifié pour mettre à jour la table avec les informations mises à jour

La procédure pour réaliser ces 3 étapes est présentée dans la documentation utilisateur située [ici](https://bouda.abes.fr/ApplisMetiers/bacon/Traitements/BACON_procedure_MAJ-PROVIDER.docx).



