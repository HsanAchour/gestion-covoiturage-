# IHEC Ride | Guide d'installation

Application web de covoiturage pour les étudiants et enseignants de l'IHEC Carthage.

## Sommaire

- [Prérequis](#prérequis)
- [Étape 1 : configurer MySQL sur le port 3301](#étape-1--configurer-mysql-sur-le-port-3301)
- [Étape 2 : importer la base de données](#étape-2--importer-la-base-de-données)
- [Étape 3 : déployer le backend PHP](#étape-3--déployer-le-backend-php)
- [Étape 4 : ouvrir le frontend](#étape-4--ouvrir-le-frontend)
- [Création d'un compte](#création-dun-compte)
- [Backend Java alternatif](#backend-java-alternatif)
- [Dépannage](#dépannage)

---

## Prérequis

| Composant | Version | Rôle |
|---|---|---|
| WAMP Server | 3.2 ou plus | Apache + PHP + MySQL en bundle (Windows) |
| Navigateur | Chrome / Firefox / Edge récent | Frontend |

WAMP : téléchargement officiel sur `wampserver.aviatechno.net`. Le pack inclut tout ce qu'il faut, aucune autre installation n'est nécessaire pour la version PHP.

---

## Étape 1 : configurer MySQL sur le port 3301

Le projet utilise le port **3301** au lieu du port MySQL standard 3306.

1. Démarrer WAMP (icône verte dans la barre des tâches).
2. Clic sur l'icône WAMP -> MySQL -> `my.ini`.
3. Modifier la ligne `port = 3306` en `port = 3301` (présente deux fois : section `[client]` et section `[wampmysqld64]`).
4. Sauvegarder, puis clic sur l'icône WAMP -> MySQL -> Service -> Redémarrer.
5. Vérifier que l'icône WAMP repasse au vert.

> Si tu préfères garder le port standard 3306, modifie plutôt `backend-php/config/database.php` ligne `'port' => 3301` -> `'port' => 3306`.

---

## Étape 2 : importer la base de données

1. Ouvrir phpMyAdmin : `http://localhost/phpmyadmin/`
2. Onglet **Importer** -> choisir le fichier `database/schema.sql`.
3. Cliquer sur **Exécuter**.

La base `ihec_ride` est créée avec ses tables. Aucune donnée pré-remplie : tu créeras les comptes via la page d'inscription.

---

## Étape 3 : déployer le backend PHP

1. Localiser le dossier `www` de WAMP (par défaut `C:\wamp64\www\`).
2. Copier le contenu du ZIP dans `C:\wamp64\www\ihec-ride\` :
   ```
   C:\wamp64\www\ihec-ride\
       backend-php\
       backend\          (optionnel, voir section Backend Java)
       database\
       frontend\
       INSTALL.md
   ```
3. Vérifier que mod_rewrite est activé : clic gauche sur l'icône WAMP -> Apache -> Modules Apache -> cocher `rewrite_module`. Apache redémarre automatiquement.

Aucune compilation à faire, PHP est interprété à la volée.

---

## Étape 4 : ouvrir le frontend

Dans un navigateur :

```
http://localhost/ihec-ride/frontend/index.html
```

Le frontend appelle automatiquement le backend PHP à l'adresse `http://localhost/ihec-ride/backend-php/api/`.

> Si tu déploies dans un autre dossier que `ihec-ride`, modifier `API_BASE` en haut de `frontend/js/common.js`.

---

## Création d'un compte

La page d'inscription impose deux contraintes :

- **Email** : doit se terminer par `@ihec.ucar.tn` (validation côté frontend et backend).
- **Mot de passe** : minimum 8 caractères, au moins 1 majuscule, 1 chiffre et 1 caractère spécial parmi `! @ # $ % ^ & * ( ) , . ? " : { } | < >`.

Exemple valide : `prenom.nom@ihec.ucar.tn` / `Test1234!`

Le rôle (passager ou conducteur) se choisit lors de l'inscription. Le rôle administrateur ne peut pas être créé via le formulaire : il faut l'attribuer manuellement en SQL :

```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'admin@ihec.ucar.tn';
```

---

## Backend Java alternatif

Le dossier `backend/` contient une implémentation Java (Servlets + JDBC) équivalente, déployable sur **Tomcat 9** au lieu d'Apache + PHP. Elle est conservée à titre de référence.

Pour l'utiliser :

1. Installer JDK 17+, Apache Tomcat 9 et Maven.
2. `cd backend && mvn clean package` -> génère `target/ihec-ride.war`.
3. Copier le WAR dans `TOMCAT_HOME/webapps/`.
4. Démarrer Tomcat -> backend disponible sur `http://localhost:8080/ihec-ride/`.
5. Modifier `frontend/js/common.js` : `const API_BASE = 'http://localhost:8080/ihec-ride';`

Tu peux supprimer le dossier `backend/` si tu n'utilises que la version PHP.

---

## Dépannage

**Erreur « Serveur inaccessible »**
- Vérifier que WAMP est démarré (icône verte).
- Vider le cache du navigateur (`Ctrl+Shift+R`).
- Vérifier que l'URL `http://localhost/ihec-ride/backend-php/api/login` répond (elle doit retourner du JSON, même une erreur 405).

**Erreur de connexion MySQL**
- Vérifier que MySQL écoute bien sur le port 3301 : `netstat -an | findstr 3301` doit afficher `LISTENING`.
- Vérifier les identifiants dans `backend-php/config/database.php` (par défaut `root` sans mot de passe, qui est le défaut WAMP).

**Page blanche / erreur 500 sur les endpoints PHP**
- Activer l'affichage des erreurs PHP : éditer `php.ini` (icône WAMP -> PHP -> php.ini), mettre `display_errors = On`, redémarrer Apache.
- Consulter les logs Apache : `C:\wamp64\logs\apache_error.log`.

**Erreur 404 sur les endpoints `/api/...`**
- Le module `mod_rewrite` n'est pas activé : icône WAMP -> Apache -> Modules Apache -> cocher `rewrite_module`.
- Vérifier que `backend-php/.htaccess` est bien présent (Windows masque parfois les fichiers commençant par un point).

**Erreur CORS dans la console du navigateur**
- Si le frontend est ouvert via `file://` au lieu de `http://localhost`, certains navigateurs bloquent. Toujours servir le frontend via Apache (URL `http://localhost/ihec-ride/frontend/...`).

---

## Architecture technique

| Couche | Technologie |
|---|---|
| Frontend | HTML5, CSS3, JavaScript vanilla (aucun framework) |
| Backend | PHP 8 (Servlets Java en alternative) |
| Base de données | MySQL 8 (port non standard 3301) |
| Sécurité | BCrypt cost=12, JWT HS256, AES-256-CBC, validation email IHEC |
| Communication | HTTP/JSON, CORS configuré pour `localhost` |

Format des réponses backend : `application/json` UTF-8.
Authentification : JWT dans l'en-tête `Authorization: Bearer <token>`, stocké côté client dans `localStorage`.
