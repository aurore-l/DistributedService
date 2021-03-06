---------------------------------------------------------------------------------------------------------------------------------------------------------------------

-----------------------------------------------
Exécution du système réparti :
-----------------------------------------------


1. Lancer le serveur de nom avec:
./serveurDeNom.sh <IpPubliqueServeurDeNom> <PortD'écoute>
ex : ./serveurDeNom.sh 132.207.12.33 5000

2. Lancer plusieurs serveurs de calcul, chacun avec sa commande associée :
./serveurDeCalcul.sh <IPPubliqueServeurDeCalcul> <NomDansLeRMIRegistry> <PortD'écoute> <IpServeurDeNom> <PortServeurDeNom> <Capacite> <TauxReponseErronée>
avec Taux de réponse érronée entre 0 et 100
ex : ./serveurDeCalcul.sh 132.207.12.34 server1 5001 132.207.12.33 5000 5 50

3. Lancer le répartiteur avec :
./repartiteur.sh <EmplacementFichierD'opérations> <IpServeurDeNom> <PortServeurDeNom>
ex : ./repartiteur.sh ./files/operations-160 132.207.12.33 5000

------------------------------------------------------------------------------------------------------------------------------------------------------------------------


-----------------------------------------------
Exécution des tests de performance :
-----------------------------------------------

--------------------
Mode sécurisé :
--------------------


1. Copier les sources sur une machine du département avec par exemple :
scp -r <Source> <Destination>
ex : scp -r DistributedService identifiant@L4712-01.info.polymtl.ca:Documents


2. Se connecter en ssh aux machines nécessaires avec :
ssh <machine>
ex : ssh identifiant@L4712-01.info.polymtl.ca


3. Se rendre dans le dossier source du code sur une machine et compiler avec la commande :
ant
Si la même session est utilisée pour toutes les connexion ssh, nul besoin de le faire pour toutes les machines. Si ce n'est pas la cas, alors le faire sur toutes les machines utilisées.

4. Lancer le serveur de nom sur une machine avec:
./serveurDeNom.sh <IpPubliqueServeurDeNom> <PortD'écoute>
ex : ./serveurDeNom.sh 132.207.12.33 5000

5. Lancer deux serveurs de calcul sur différentes machines, chacun avec sa commande associée :
./serveurDeCalcul.sh <IPPubliqueServeurDeCalcul> <NomDansLeRMIRegistry> <PortD'écoute> <IpServeurDeNom> <PortServeurDeNom> 4 0
ex : ./serveurDeCalcul.sh 132.207.12.34 server1 5001 132.207.12.33 5000 4 0


6. Lancer le répartiteur avec :
./repartiteur.sh ./files/operations-800 <IpServeurDeNom> <PortServeurDeNom>
ex : ./repartiteur.sh ./files/operations-800 132.207.12.33 5000


7. Pour exécuter le test avec 3 serveurs puis 4 serveurs, terminer les éxécutions des serveurs de calculs et du serveur de nom, et réaliser à nouveau les étapes 4 à 6, avec le nombre de serveurs correspondant.
 
 
 
-------------------------
Mode non sécurisé :
-------------------------

1. Copier les sources sur une machine du département avec par exemple :
scp -r <Source> <Destination>
ex : scp -r DistributedService identifiant@L4712-01.info.polymtl.ca:Documents


2. Se connecter en ssh aux machines nécessaires avec :
ssh <machine>
ex : ssh identifiant@L4712-01.info.polymtl.ca


3. Se rendre dans le dossier source du code sur une machine et compiler avec la commande :
ant
Si la même session est utilisée pour toutes les connexion ssh, nul besoin de le faire ^pour toutes les machines. Si ce n'est pas la cas, alors le faire sur toutes les machines utilisées.

4. Lancer le serveur de nom sur une machine avec:
./serveurDeNom.sh <IpPubliqueServeurDeNom> <PortD'écoute>
ex : ./serveurDeNom.sh 132.207.12.33 5000

5. Lancer trois serveurs de calcul sur différentes machines, chacun avec sa commande associée :
1) ./serveurDeCalcul.sh <IPPubliqueServeurDeCalcul> <NomDansLeRMIRegistry> <PortD'écoute> <IpServeurDeNom> <PortServeurDeNom> 5 0
2) ./serveurDeCalcul.sh <IPPubliqueServeurDeCalcul> <NomDansLeRMIRegistry> <PortD'écoute> <IpServeurDeNom> <PortServeurDeNom> 5 0
3) ./serveurDeCalcul.sh <IPPubliqueServeurDeCalcul> <NomDansLeRMIRegistry> <PortD'écoute> <IpServeurDeNom> <PortServeurDeNom> 5 0
ex : ./serveurDeCalcul.sh 132.207.12.34 server1 5001 132.207.12.33 5000 5 0


6. Lancer le répartiteur avec :
./repartiteur.sh ./files/operations-800 <IpServeurDeNom> <PortServeurDeNom>
ex : ./repartiteur.sh ./files/operations-800 132.207.12.33 5000


7. Terminer les éxécutions des serveurs de calculs et du serveur de nom, et réaliser à nouveau les étapes 4 à 6, avec les changements nécessaires à l'étape 5 tels que :
1) ./serveurDeCalcul.sh <IPPubliqueServeurDeCalcul> <NomDansLeRMIRegistry> <PortD'écoute> <IpServeurDeNom> <PortServeurDeNom> 5 50
2) ./serveurDeCalcul.sh <IPPubliqueServeurDeCalcul> <NomDansLeRMIRegistry> <PortD'écoute> <IpServeurDeNom> <PortServeurDeNom> 5 0
3) ./serveurDeCalcul.sh <IPPubliqueServeurDeCalcul> <NomDansLeRMIRegistry> <PortD'écoute> <IpServeurDeNom> <PortServeurDeNom> 5 0

puis 

1) ./serveurDeCalcul.sh <IPPubliqueServeurDeCalcul> <NomDansLeRMIRegistry> <PortD'écoute> <IpServeurDeNom> <PortServeurDeNom> 5 75
2) ./serveurDeCalcul.sh <IPPubliqueServeurDeCalcul> <NomDansLeRMIRegistry> <PortD'écoute> <IpServeurDeNom> <PortServeurDeNom> 5 0
3) ./serveurDeCalcul.sh <IPPubliqueServeurDeCalcul> <NomDansLeRMIRegistry> <PortD'écoute> <IpServeurDeNom> <PortServeurDeNom> 5 0
 
 
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------