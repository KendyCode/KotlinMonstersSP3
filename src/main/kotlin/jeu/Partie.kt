package jeu

import especeAquamy
import especeFlamkip
import especeSpringleaf
import monde.Zone
import monstre.EspeceMonstre
import monstre.IndividuMonstre
import org.example.dresseur.Entraineur

class Partie(
    var id : Int,
    var joueur : Entraineur,
    var zone : Zone
) {

    fun choixStarter(){
        val monstre1 = IndividuMonstre(1, "springleaf", especeSpringleaf, null,1500.0)
        val monstre2 = IndividuMonstre(2, "flamkip",especeFlamkip,null,1500.0)
        val monstre3 = IndividuMonstre(3, "aquamy",especeAquamy,null,1500.0)

        fun choix(){
            monstre1.afficheDetail()
            monstre2.afficheDetail()
            monstre3.afficheDetail()

            println("Chosis entre le monstre 1, 2 et 3")
            var choixSelection = readln().toInt()
            if (choixSelection !in 1..3){
                choix()
            }
            else{
                var starter = monstre1
                when (choixSelection) {
                    1 -> starter = monstre1
                    2 -> starter = monstre2
                    else -> starter = monstre3
                }
                starter.renommer()
                joueur.equipeMonstre.add(starter)
                starter.entraineur = joueur
            }



        }

        choix()


    }

    fun modifierOrdreEquipe(){
        if (joueur.equipeMonstre.size>1){
            for(i in 0..joueur.equipeMonstre.size-1){
                println("${i} : ${joueur.equipeMonstre[i].nom}")
            }
            println("Donner l'index du monstre à déplace")
            var anciennePosition = readln().toInt()
            println("Donner l'index de la nouvelle position")
            var nouvellePosition = readln().toInt()

            var temp = joueur.equipeMonstre[nouvellePosition]

            joueur.equipeMonstre[nouvellePosition] = joueur.equipeMonstre[anciennePosition]
            joueur.equipeMonstre[anciennePosition] = temp

        }

    }

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

    fun jouer(){
        println("Vous êtes dans ${zone.nom}")
        println("1 => Rencontrer un monstre sauvage")
        println("2 => Examiner l’équipe de monstres ")
        println("3 => Aller à la zone suivante ")
        println("4 => Aller à la zone précédente  ")
        var choix = readln().toInt()
        when(choix){
            1 -> {
                zone.rencontreMonstre()
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

