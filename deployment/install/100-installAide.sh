#!/bin/bash

if [ -z "$1" ] ; then

cat << EOF
$0: Install and configure the AIDE (intrusion detection based on checksum)
-----------------------------

### Les detecteurs d'intrusion

http://www.debian.org/doc/manuals/securing-debian-howto/ch4.fr.html#s4.14

Êtes-vous sûr que le /bin/login présent sur votre disque dur soit le même que celui que vous aviez installé il y a de cela quelques mois ? Que faire si c'est une version piratée, qui enregistre les mots de passe entrés dans un fichier caché ou les envoie en clair à travers l'Internet ?

La seule méthode pour avoir un semblant de protection est de vérifier vos fichiers tous les heures/jours/mois (je préfère quotidiennement) en comparant l'actuel et l'ancien md5sum de ce fichier. Deux fichiers ne peuvent avoir le même md5sum, donc de ce côté tout est ok, à moins que quelqu'un ait piraté également l'algorithme qui crée les md5sums sur cette machine. 

Les outils couramment utilisés pour ceci sont sxid, aide (Advanced Intrusion Detection Environment), tripwire, integrit et samhain. Installer debsums vous aidera également à vérifier l'intégrité du système de fichiers en comparant le md5sum de chaque fichier avec celui utilisé dans l'archive des paquets Debian.

FIXME: mentionner les binaires signés utilisant bsign ou elfsign 

### AIDE

Penser à :

 * Faire un backup readonly de chaque version de la db

 * mettre à jour la db à chaque mise à jour du système.

 * Envoyer automatiquement des mails à chaque check

Aide memoire :

> # Update the db
> sudo aide -c /var/lib/aide/aide.conf.autogenerated --init
> sudo scp /var/lib/aide/aide.db.new elveos-backup@f2.b219.org:aide.db.$(date +%m-%d-%y-%R)
> sudo scp /var/lib/aide/aide.db.new elveos-backup@b219.org:aide.db.$(date +%m-%d-%y-%R)
> sudo cp /var/lib/aide/aide.db{.new,}

EOF

elif [ "$1" = "exec" ] ; then

    # Install ...
    sudo apt-get install aide

    # Configure the mailto :
    read -p "You have to change the MAILTO value to: sysadmin@linkeos.com. <enter to continue> "
    sudo vim /etc/default/aide

    # Init db
    sudo aideinit

    # cp the db
    FILENAME=/var/lib/aide/aide.db.new
    sudo cp $FILENAME /var/lib/aide/aide.db
    echo you should : 
    echo    sudo scp $FILENAME elveos-backup@f2.b219.org:aide.db.$(date +%m-%d-%y-%R)
    echo    sudo scp $FILENAME elveos-backup@b219.org:aide.db.$(date +%m-%d-%y-%R)
    

    echo creating a helper script in ~/aide.sh
    cat << EOF > ~/aide.sh
#!/bin/bash

if [ "$1" = "update" ] ; then
    sudo aide -c /var/lib/aide/aide.conf.autogenerated --update
elif [ "$1" = "copy" ] ; then 
    sudo cp /var/lib/aide/aide.db{.new,}
elif [ "$1" = "backup" ] ; then
    sudo scp /var/lib/aide/aide.db.new elveos-backup@f2.b219.org:aide.db.$(date +%m-%d-%y-%R)
    sudo scp /var/lib/aide/aide.db.new elveos-backup@b219.org:aide.db.$(date +%m-%d-%y-%R)
else
    echo "usage: $0 update | copy | backup"
fi

EOF

    read -p "Make sure the backup script is up to date ! <return>"
    read -p "Make sure to cron the cp /var/lib/aide/aide.db{.new,}"

fi
