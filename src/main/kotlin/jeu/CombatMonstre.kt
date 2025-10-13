package jeu

import item.Utilisable

import monstre.IndividuMonstre
import org.example.dresseur.Entraineur

/**
 * Gère un **combat entre un monstre du joueur et un monstre sauvage**.
 *
 * Le combat se déroule en plusieurs rounds jusqu’à :
 * - la **victoire** du joueur (monstre sauvage vaincu ou capturé), ou
 * - la **défaite** (tous les monstres du joueur sont KO).
 *
 * @property joueur L’entraîneur contrôlant le monstre joueur.
 * @property monstreJoueur Le monstre appartenant au joueur.
 * @property monstreSauvage Le monstre adverse (sauvage ou d’un autre entraîneur).
 */
class CombatMonstre(
    var joueur: Entraineur,
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
    /**
     * Vérifie si le joueur a gagné le combat.
     *
     * Conditions de victoire :
     * - Le monstre sauvage a 0 PV → le joueur gagne et reçoit de l’expérience.
     * - Ou bien, le monstre sauvage appartient désormais au joueur (capture réussie).
     *
     * @return `true` si le combat est remporté, sinon `false`.
     */
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

    /**
     * Exécute l’action du monstre adverse (sauvage ou d’un autre entraîneur).
     *
     * - Si le monstre sauvage est encore en vie (`pv > 0`), il attaque le monstre du joueur.
     * - Si le monstre est K.O., aucune action n’est effectuée.
     */
    fun actionAdversaire(){
        if(monstreSauvage.pv > 0){
            monstreSauvage.attaquer(monstreJoueur)
        }


    }
    /**
     * Gère le tour d’action du joueur.
     *
     * Propose plusieurs choix d’action :
     * 1. **Attaquer** → le monstre du joueur attaque le monstre adverse.
     * 2. **Prendre un item** → permet d’utiliser un objet du sac.
     * 3. **Remplacer le monstre** → change le monstre actif.
     *
     * Si le joueur a perdu (`gameOver == true`), la fonction s’interrompt.
     *
     * @return `true` si le combat continue, `false` sinon (capture réussie ou défaite).
     */
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
                for (i in joueur.sacAItems.indices) {
                    println("$i : ${joueur.sacAItems[i].nom}")
                }
                println("Saisir le nb de l'item")
                var indexChoix : Int = readln().toInt()
                var objetChoisi = joueur.sacAItems[indexChoix]
                // Vérifie si l’objet sélectionné est utilisable
                if(objetChoisi is Utilisable){
                    var captureReussie = objetChoisi.utiliser(monstreSauvage,joueur)
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

    /**
     * Affiche l’état actuel du combat :
     * - Détails du monstre sauvage (niveau, PV, ASCII art).
     * - Détails du monstre du joueur.
     *
     * Utilisé au début de chaque round pour informer le joueur.
     */
    fun afficheCombat(){
        println("===== Debut Round : ${round} ======")
        println("Niveau : ${monstreSauvage.niveau}")
        println("PV : ${monstreSauvage.pv} / ${monstreSauvage.pvMax}")
        println(monstreSauvage.espece.afficheArt())
        println(monstreSauvage.espece.afficheArt(false))

        println("MOOOOOONNNN MONSTTRRREEEEEEEEEEEEEEEEE")
        println("Niveau : ${monstreJoueur.niveau}")
        println("PV : ${monstreJoueur.pv} / ${monstreJoueur.pvMax}")
    }

    /**
     * Exécute un round complet de combat :
     * - Compare la vitesse des deux monstres pour déterminer l’ordre des actions.
     * - Fait agir le joueur et/ou l’adversaire selon le cas.
     *
     * La logique s’adapte :
     * - Si le joueur est plus rapide, il agit en premier.
     * - Sinon, le monstre sauvage attaque d’abord.
     */
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
     * Lance la boucle principale du combat.
     *
     * Le combat se poursuit tant que :
     * - Le joueur n’a pas perdu (`!gameOver()`),
     * - Le joueur n’a pas gagné (`!joueurGagne()`).
     *
     * Après la fin du combat :
     * - Si le joueur perd, tous ses monstres voient leurs PV restaurés.
     * - Affiche un message de fin de partie.
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