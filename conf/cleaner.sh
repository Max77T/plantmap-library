#!/bin/sh

##----------------------------------
## Script  : cleaner
## Auteur  : GreenMapper Team
## Date    : 24-02-2016
## Version : 1.0.1
## Objet   : Supprime les fichiers temporaire de plantmap-library
##			 Doit etre execute en tant que root
##
#----------------------------------
## Syntaxes :
## --------
##
##      prompt> sudo ./cleaner.sh
##
##----------------------------------

## Variables configurable
PLANTMAP_FOLDER=/var/plantmap_files/tmp # Chemin du dossier temporaire de plantmap-library
RET=2 # Nombre de jour de retention des fichiers

##----------------------------------

if [ ! -d "${PLANTMAP_FOLDER}" ]
then
	echo "Veuillez configurer la variable 'PLANTMAP_FOLDER' du script"
	echo "Dossier ' $PLANTMAP_FOLDER ' introuvable"
	exit 1
fi

FOLDERS=`ls $PLANTMAP_FOLDER | awk '{print $1}'`

if [ -n "${FOLDERS}" ]
then
	cd $PLANTMAP_FOLDER
	for i in ${FOLDERS}
	do
		find ${i}/* -type f -name "*.zip"  -mtime +${RET} -exec rm -f {} \; >> /dev/null
		find ${i} -type f -name "*.zip"  -mtime +${RET} -exec rm -f {} \; >> /dev/null
		find ${i} -type d -empty -exec rm -rf {} \; >> /dev/null
	done
fi

exit 0