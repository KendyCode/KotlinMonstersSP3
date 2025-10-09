package jeu

import item.Utilisable
import joueur
import monstre.IndividuMonstre

class CombatMonstre(
    var monstreJoueur : IndividuMonstre,
    var monstreSauvage : IndividuMonstre
) {
    var round : Int = 1
    /**
     * Vérifie si le joueur a perdu le combat.
     *
     * Condition de défaite :
     * - Aucun monstre de l'équipe du joueur n'a de PV > 0.
     *
     * @return `true` si le joueur a perdu, sinon `false`.
     */
    fun gameOver() : Boolean{
        var verif = true
        for (monstre in joueur.equipeMonstre){
            if (monstre.pv>0){
                verif = false
            }
        }
        return verif
    }

    fun joueurGagne() : Boolean{
        if (monstreSauvage.pv <= 0){
            println("${joueur.nom} a gagné !")
            var gainExp = monstreSauvage.exp * 0.20
            monstreJoueur.exp += gainExp
            println("${monstreJoueur.nom} gagne {$gainExp} exp")
            return true
        }
        else{
            if(monstreSauvage.entraineur == joueur){
                println("${monstreSauvage.nom} a été capturé !")
                return true
            }
            else{
                return false
            }
        }
    }

    fun actionAdversaire(){
        if(monstreSauvage.pv > 0){
            monstreSauvage.attaquer(monstreJoueur)
        }


    }

    fun actionJoueur() : Boolean{
        if(gameOver()== true){
            return false
        }
        else{
            println("Choisis entre (1 : ATTAQUER,2 : PRENDRE UN ITEM,3 : REMPLACER SON MONSTRE)")
            var choixAction = readln().toInt()
            if (choixAction==1){
                monstreJoueur.attaquer(monstreSauvage)
                return true
            }
            else if(choixAction==2){
                println(joueur.sacAItems)
                println("Saisir le nb de l'item")
                var indexChoix : Int = readln().toInt()
                var objetChoisi = joueur.sacAItems[indexChoix]

                if(objetChoisi is Utilisable){
                    var captureReussie = objetChoisi.utiliser(monstreSauvage)
                    if(captureReussie){
                        return false
                    }
                    else{
                        println("Objet non utilisable")
                    }
                }
            }
            else if(choixAction==3){
                println(joueur.equipeMonstre)
                println("Donnes le num du monstre que vous voulez")
                var indexChoix = readln().toInt()
                var choixMonstre = joueur.equipeMonstre[indexChoix]
                if(choixMonstre.pv<=0){
                    println("Impossible ! ce monstre est KO")
                }
                else{
                    println("${choixMonstre} remplace ${monstreJoueur}")
                    monstreJoueur = choixMonstre
                }
            }

        }
        return true


    }

    fun afficheCombat(){
        println("===== Début Round : ${round} ======")
        println("Niveau : ${monstreSauvage.niveau}")
        println("PV : ${monstreSauvage.pv} / ${monstreSauvage.pvMax}")
        println(monstreSauvage.espece.afficheArt())
        println(monstreSauvage.espece.afficheArt(false))

        println("MOOOOOONNNN MONSTTRRREEEEEEEEEEEEEEEEE")
        println("Niveau : ${monstreJoueur.niveau}")
        println("PV : ${monstreJoueur.pv} / ${monstreJoueur.pvMax}")
    }

    fun jouer(){
        var joueurPlusRapide = (monstreJoueur.vitesse >= monstreSauvage.vitesse)
        println(afficheCombat())
        if (joueurPlusRapide){
            var continuer = this.actionJoueur()
            if (continuer != false){
                this.actionAdversaire()
            }
        }
        else{
            this.actionAdversaire()

            if (gameOver()== false){
                var continuer = this.actionJoueur()
                if (continuer != false){
                    this.actionAdversaire()
                }
            }

        }

    }

    /**
     * Lance le combat et gère les rounds jusqu'à la victoire ou la défaite.
     *
     * Affiche un message de fin si le joueur perd et restaure les PV
     * de tous ses monstres.
     */
    fun lanceCombat() {
        while (!gameOver() && !joueurGagne()) {
            this.jouer()
            println("======== Fin du Round : $round ========")
            round++
        }
        if (gameOver()) {
            joueur.equipeMonstre.forEach { it.pv = it.pvMax }
            println("Game Over !")
        }
    }




}