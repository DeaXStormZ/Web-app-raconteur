# Projet ACOL-WEB


Déploiement de l'application
---
A supposer que tomcat soit configuré correctement et possède ojdbc7.jar et autres jar nécessaires à
son fonctionnement, le déploiement s’effectue de la façon suivante :
1. Démarrer tomcat avec le script
startup.sh
2.  Une fois le serveur en ligne, déployer le projet avec mvn
tomcat:deploy
ou mvn
tomcat:
redeploy
.
Dès lors accéder, à
IP
_
Hote:/raconteur
_
war
permet d’accéder au site.


Mise en place de la base de données 
---
Trois scripts sont à disposition afin de mettre en place la base de données et la peupler : 
 - installBD.sql pour initialiser
 - populate.sql pour peupler
 - dropTables.sql sert à effacer complètement la base de données en cas d'erreur
 
 Une base de données est déjà en place sur le compte ensioracle suivant : 
 
 - login : kleitzn 
 - mdp : kleitzn