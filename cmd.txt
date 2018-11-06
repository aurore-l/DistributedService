scp -r DistributedServicePourTest algoue@L4712-01.info.polymtl.ca:Documents

./serveurDeNom.sh 10.200.19.217 5000
./serveurDeCalcul.sh 10.200.19.217 server1 5001 10.200.19.217 5000 5 0 
./repartiteur.sh ./files/operations-160 10.200.19.217 5000



SN : 132.207.12.33 5000
SC1 : 132.207.12.35 5002
SC2 : 132.207.12.36 5003
SC3 : 132.207.12.38 5006
SC4 : 132.207.12.39 5007
RP : 

