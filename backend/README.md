# IHEC Ride — Backend Java MVC

## Architecture MVC (sans Spring Boot)

```
com.ihecride
├── models/          Entités (POJO)
├── dao/             Data Access Object (JDBC MySQL)
├── services/        Logique métier
├── controllers/     Servlets (MVC Controller)
├── filters/         AuthFilter JWT + CORS
└── utils/           DBConnection, PasswordUtil, JwtUtil, AesUtil
```

## Base de données
- MySQL WAMP, port **3301**
- Base : `ihec_ride`
- Script : `../database/schema.sql`

## Déploiement
1. Lancer WAMP et importer `database/schema.sql`.
2. Compiler : `mvn clean package`
3. Déployer le WAR dans Apache Tomcat 9 : `webapps/ihec-ride.war`
4. Accéder : `http://localhost:8080/ihec-ride/`

## Endpoints API

| Méthode | URL | Description |
|--------|------|-------------|
| POST | /api/auth/register | Inscription |
| POST | /api/auth/login | Connexion |
| GET | /api/trajets/search?depart=&dest=&date= | Recherche trajets |
| POST | /api/trajets | Créer un trajet (conducteur) |
| GET | /api/trajets/mine | Mes trajets (conducteur) |
| POST | /api/reservations | Réserver |
| GET | /api/reservations | Mes réservations |
| GET | /api/reservations/trajet?trajetId= | Demandes reçues |
| POST | /api/messages | Envoyer un message |
| GET | /api/messages/conversation?userId= | Conversation |
| POST | /api/evaluations | Noter un conducteur |
| POST | /api/reclamations | Soumettre réclamation |
| POST | /api/sos | Déclencher SOS |
| GET | /api/admin/stats | KPIs admin |
| GET | /api/admin/users | Liste utilisateurs |
| POST | /api/admin/ban | Bannir un compte |

## Sécurité
- **BCrypt** facteur 12 (`PasswordUtil`)
- **JWT HS256** : access 15 min, refresh 7j (`JwtUtil`)
- **AES-256 CBC** (`AesUtil`)
- Filtre d'auth sur `/api/*` sauf `/api/auth/*`
- Validation email universitaire : `^[A-Za-z0-9._%+-]+@ihec\.ucar\.tn$`

## Réponses
Format **URL-encoded** et **pseudo-CSV** (pas de JSON comme demandé).
Exemple : `success=true&token=...&userId=3&role=CONDUCTEUR`
