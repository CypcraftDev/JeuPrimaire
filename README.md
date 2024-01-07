<Mettre ici le nom de votre jeu>
===========

Développé par <prénom1 nom1> Cypcraft et Raphaël TAFANI
Contacts : <mail1>

# Présentation de <le nom de votre jeu>

Un jeu éducatif Shell proposant aux enfants de défier Einstein à un jeu de quiz.
Le jeu est prévu pour être joué par de jeunes enfants en classe encadrés par leur professeur.
Les questions sont réparties par matière et des niveau de difficulté par classe.
Tout est fait pour guider l'enfant dans l'utilisation du logiciel de manière simple et ludique.
Après 5 bonnes réponses l'enfant est récompensé par un temp de jeu(Platformer simple).

# Utilisation de <le nom de votre jeu>

Afin d'utiliser le projet, il suffit de taper les commandes suivantes dans un terminal :

```
./compile.sh
```
Permet la compilation des fichiers présents dans 'src' et création des fichiers '.class' dans 'classes'

```
./run.sh
```
Permet le lancement du jeu

Ainsi le jeu peut être lancé directement avec cette ligne de commande:
```
./sh compile.sh;sh run.sh
```

Vous pouvez créer des niveau en ajoutant un dossier dans questions, le nom du dossier correspond au nom de la matière. Vous pouvez également créer des matières en créant un fichier .csv le nom du fichier correspond au nom de la matière qui sera affiché en jeu.
Dans ce fichier .csv vous pouvez rajouter des question et/ou en supprimer pour cela il faut suivre le patrene suivant : Question(énoncé),Possibilité1,Possibilité2,Possibilité3,Possibilité4,IdRéponseDeBase(0 à 3),difficulté. il est important de ne pas mettre d'espace après les virgules et de ne pas mettre de virgules dans la question.
Le création de difficltés et possible ainsi que de map du platformer. Pour afficher plus de place sur le scoreboard il suffit de rajouter des lignes (?,?,0,0 secondes,0).

