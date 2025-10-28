package jeu

import item.Utilisable

import monstre.IndividuMonstre
import org.example.dresseur.Entraineur

/**
 * Gère un **combat entre un monstre du joueur et un monstre sauvage**.
 *
 * Cette classe encapsule toute la logique d'un combat :
 * - Gestion des rounds.
 * - Tour du joueur et tour de l’adversaire.
 * - Détermination de la vitesse pour l’ordre des actions.
 * - Gestion de la victoire, défaite et capture.
 *
 * @property joueur L’entraîneur contrôlant le monstre joueur.
 * @property monstreJoueur Le monstre actuellement actif du joueur.
 * @property monstreSauvage Le monstre adverse (sauvage ou appartenant à un autre joueur).
 */
class CombatMonstre(
    var joueur: Entraineur,
    var monstreJoueur : IndividuMonstre,
    var monstreSauvage : IndividuMonstre
) {

    /** Compteur de rounds, commence à 1 et s’incrémente à chaque tour complet */
    var round : Int = 1
    /**
     * Vérifie si le joueur a perdu le combat.
     *
     * Condition :
     * - Tous les monstres du joueur ont PV = 0 → défaite.
     *
     * @return true si le joueur n’a plus de monstres valides, false sinon.
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
     * 1. Le monstre sauvage est K.O. → le joueur gagne et reçoit de l’expérience.
     * 2. Le monstre sauvage a été capturé (appartient désormais au joueur).
     *
     * @return true si le joueur a remporté le combat, false sinon.
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
            // Vérifie si le joueur a capturé le monstre sauvage
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
        } else {
            println("${monstreSauvage.nom} est K.O. et ne peut plus attaquer !")
        }
    }
    /**
     * Gère le tour du joueur.
     *
     * Propose 3 actions :
     * 1. **Attaquer** → inflige des dégâts au monstre adverse.
     * 2. **Prendre un item** → utiliser un objet du sac (ex : potion, Pokéball).
     * 3. **Changer de monstre** → remplacer le monstre actif par un autre de l’équipe.
     *
     * @return true si le combat continue, false si le combat est terminé (capture réussie ou défaite).
     */
    fun actionJoueur() : Boolean{
        if(gameOver()== true){
            return false
        }
        else{
            println("Choisis entre (1 : ATTAQUER,2 : PRENDRE UN ITEM,3 : REMPLACER SON MONSTRE)")
            var choixAction = readln().toInt()
            if (choixAction == 1) {
                if (monstreJoueur.pv > 0) {
                    monstreJoueur.attaquer(monstreSauvage)
                } else {
                    println("${monstreJoueur.nom} est K.O. et ne peut plus attaquer !")
                }
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
                // Affiche l’équipe et demande le choix
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
     * - Monstre sauvage : niveau, PV, ASCII art face et dos.
     * - Monstre du joueur : niveau, PV.
     *
     * Utilisé au début de chaque round pour informer le joueur.
     */
    fun afficheCombat(){
        println("===== Debut Round : ${round} ======")
        println("Niveau : ${monstreSauvage.niveau}")
        println("PV : ${monstreSauvage.pv} / ${monstreSauvage.pvMax}")
        println(monstreSauvage.espece.afficheArt())
        println(monstreSauvage.espece.afficheArt(false))

        println("Mon Monstre")
        println(monstreJoueur.nom)
        println("Niveau : ${monstreJoueur.niveau}")
        println("PV : ${monstreJoueur.pv} / ${monstreJoueur.pvMax}")
    }

    /**
     * Exécute un round complet de combat.
     *
     * - Détermine l’ordre des actions selon la vitesse des monstres.
     * - Le plus rapide agit en premier.
     * - Gère les actions du joueur et de l’adversaire.
     */
    fun jouer(){

        // 🧱 Vérification avant tout
        if (monstreJoueur.pv <= 0) {
            println("${monstreJoueur.nom} est K.O. ! Choisis un autre monstre.")
            actionJoueur() // propose de changer de monstre
            return // on arrête ce round
        }

        if (monstreSauvage.pv <= 0) {
            println("${monstreSauvage.nom} est déjà K.O. ! Le combat est terminé.")
            return
        }

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
     * - Le combat continue tant que le joueur n’a pas perdu et n’a pas gagné.
     * - Après la fin du combat, si le joueur a perdu, tous ses monstres récupèrent leurs PV.
     * - Affiche un message de fin.
     */
    fun lanceCombat() {
        // Définit le premier monstre vivant comme monstre actif
        monstreJoueur = joueur.equipeMonstre.firstOrNull { it.pv > 0 }
            ?: run {
                println("Tous vos monstres sont K.O. !")
                return
            }



        // Boucle principale du combat
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