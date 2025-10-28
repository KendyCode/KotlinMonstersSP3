package jeu

import entraineurDAO
import especeMonstreDAO
import individuMonstreDAO
import joueur
import monde.Zone
import monstre.EspeceMonstre
import monstre.IndividuMonstre
import monstre.IndividuMonstreEntity
import org.example.dresseur.Entraineur



/**
 * Représente une session de jeu active.
 *
 * Une [Partie] relie un [Entraineur] (le joueur) à une [Zone] du monde.
 * Elle gère la progression du joueur, les rencontres de monstres,
 * l'équipe de monstres et les transitions entre zones.
 *
 * Fonctionnalités principales :
 * - Choix du monstre de départ (starter)
 * - Exploration et rencontres de monstres sauvages
 * - Gestion de l’équipe (affichage, réorganisation)
 * - Navigation entre zones
 *
 * @property id Identifiant unique de la partie.
 * @property joueur Référence vers l’entraîneur actif.
 * @property zone Zone actuelle dans laquelle le joueur se trouve.
 */
class Partie(
    var id : Int,
    var joueur : Entraineur,
    var zone : Zone
) {
    /**
     * Permet au joueur de choisir son monstre de départ (starter).
     *
     * Trois espèces sont proposées : Springleaf, Flamkip, Aquamy.
     * Le choix est effectué via saisie utilisateur.
     *
     * Étapes :
     * 1. Chargement des espèces depuis la base de données via `especeMonstreDAO`.
     * 2. Création de trois [IndividuMonstre] avec expérience initiale fixe (1500).
     * 3. Affichage des détails des trois monstres pour le choix.
     * 4. Sélection et renommage facultatif.
     * 5. Ajout du starter choisi dans l'équipe du joueur.
     */
    fun choixStarter(){

        // Récupération des espèces depuis la base de données
        val especeSpringleaf = especeMonstreDAO.findByNom("Springleaf").firstOrNull()
        val especeFlamkip = especeMonstreDAO.findByNom("Flamkip").firstOrNull()
        val especeAquamy = especeMonstreDAO.findByNom("Aquamy").firstOrNull()

        if (especeSpringleaf == null || especeFlamkip == null || especeAquamy == null) {
            println("Erreur: Impossible de charger les starters depuis la base de données")
            return
        }

        // Création des trois starters avec ID fictif et expérience initiale
        val monstre1 = IndividuMonstre(1, "Springleaf", especeSpringleaf, joueur, 1500.0)
        println(monstre1.entraineur)
        val monstre2 = IndividuMonstre(2, "Flamkip", especeFlamkip, joueur, 1500.0)
        val monstre3 = IndividuMonstre(3, "Aquamy", especeAquamy, joueur, 1500.0)


        /**
         * Fonction récursive interne pour gérer le processus de sélection.
         * Si l'utilisateur saisit une valeur invalide, la fonction se relance.
         */
        fun choix(){
            // Affichage des détails des trois monstres pour guider le choix
            monstre1.afficheDetail()
            monstre2.afficheDetail()
            monstre3.afficheDetail()

            println("Chosis entre le monstre 1, 2 et 3")
            var choixSelection = readln().toInt()
            // Vérification de la validité du choix
            if (choixSelection !in 1..3){
                choix()
            }
            else{
                // Attribution du starter sélectionné
                var starter = monstre1
                when (choixSelection) {
                    1 -> starter = monstre1
                    2 -> starter = monstre2
                    else -> starter = monstre3
                }
                // Renommage facultatif
                starter.renommer()
                // Ajout à l’équipe du joueur
                joueur.equipeMonstre.add(starter)
                starter.entraineur = joueur
            }



        }
        // Lancement du choix
        choix()


    }

    /**
     * Permet de modifier manuellement l’ordre des monstres dans l’équipe du joueur.
     *
     * Fonctionnement :
     * 1. Affiche l’équipe avec indices.
     * 2. Demande à l’utilisateur de choisir l’indice du monstre à déplacer.
     * 3. Demande l’indice de la nouvelle position.
     * 4. Échange les positions via variable temporaire.
     *
     * Ne fait rien si l’équipe contient moins de deux monstres.
     */
    fun modifierOrdreEquipe(){
        if (joueur.equipeMonstre.size>1){
            // Affichage de l’équipe actuelle avec indices
            for(i in 0..joueur.equipeMonstre.size-1){
                println("${i} : ${joueur.equipeMonstre[i].nom}")
            }
            println("Donner l'index du monstre à déplace")
            var anciennePosition = readln().toInt()
            println("Donner l'index de la nouvelle position")
            var nouvellePosition = readln().toInt()

            // Échange des positions via variable temporaire
            var temp = joueur.equipeMonstre[nouvellePosition]

            joueur.equipeMonstre[nouvellePosition] = joueur.equipeMonstre[anciennePosition]
            joueur.equipeMonstre[anciennePosition] = temp

        }

    }

    /**
     * Permet au joueur d’examiner son équipe actuelle.
     *
     * Options disponibles :
     * - Entrer un index numérique → affiche les détails du monstre correspondant.
     * - `m` → modifie l’ordre de l’équipe.
     * - `q` → quitte le menu.
     */
    fun examineEquipe(){
        println("Équipe :")
        for (i in joueur.equipeMonstre.indices) {
            println("$i : ${joueur.equipeMonstre[i].nom}")
        }
        println("numéro du monstre pour voir son détail ,q -> quitter le menu, m -> modifier l'ordre de l'équipe")
        var choix = readln()
        when(choix){
            "q" -> return
            "m" -> this.modifierOrdreEquipe()
            else -> joueur.equipeMonstre[choix.toInt()].afficheDetail()
        }

    }


    /**
     * Boucle principale du jeu. Fournit une interface textuelle
     * pour interagir avec la [Zone] actuelle.
     *
     * Propose plusieurs actions :
     * 1. Rencontrer un monstre sauvage.
     * 2. Examiner l’équipe de monstres.
     * 3. Aller à la zone suivante.
     * 4. Revenir à la zone précédente.
     *
     * La fonction se rappelle récursivement pour maintenir la session active.
     */
    fun jouer(){
        println("Vous êtes dans ${zone.nom}")
        zone.especesMonstres.forEach { espece ->
            println(" - ${espece.nom} [Type: ${espece.type}]")
        }
        println()

        println("1 => Rencontrer un monstre sauvage")
        println("2 => Examiner l’équipe de monstres ")
        println("3 => Aller à la zone suivante ")
        println("4 => Aller à la zone précédente  ")


        var choix = readln().toInt()
        when(choix){
            1 -> {
                zone.rencontreMonstre(joueur)
                jouer()
            }
            2 -> {
                this.examineEquipe()
                jouer()
            }
            3 -> if(zone.zoneSuivante != null){
                zone = zone.zoneSuivante!!
                jouer()
            }else{
                println("Pas de zone suivante")
                jouer()
            }
            4 -> if(zone.zonePrecedente!= null){
                zone = zone.zonePrecedente!!
                jouer()
            }else{
                println("Pas de zone précédente")
                jouer()
            }


        }

    }




}