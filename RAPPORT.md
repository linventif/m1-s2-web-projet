# Rapport Exam Grégoire Launay--Bécue

Je n'ais pas eu le temps de faire certaine implémentation, notament certain post & update, mais la structure backend est la. pas eu le temps nonplus de faire la methéo

## Installation

docker compose up -d
maven install

## Diagram de relation

![alt text](image.png)

## Endpoints

`[GET] /user/profile` : permet d'afficher le profil de l'utilisateur connecter
`[GET] /user/workout` : permet de récupérer les activité d'un utilisateur
`[GET] /user/login` : permet de se connecter
`[GET] /user/logout` : permet de ce déconnecter
`[POST] /user/profile` : permet de créer le compte

Pas eu le temps d'implémenter
`[POST] /user/:userID/friends` : demande d'ami
`[DELETE] /user/:userID/friends` : supression ami
`[GET] /user/:userID/workout` : voir le workout d'un ami
`[GET] /user/:userID/profile` : voir le profile
`[GET] /metheo/:ville` : voir la méthéo d'une ville


## Class

Utilisateur
-> sexe: enum
-> age: int
-> taille: int
-> poids: double

Sport
-> nom: String

Workout
-> durrée (s): double # car certain sport doivent être précis ex ski, natation
-> distance (m): double
-> calculCalori()

Follower
->