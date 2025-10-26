package org.example.dresseur

import monstre.EspeceMonstre
import monstre.IndividuMonstre
import item.Item

/**
 * Représente un entraîneur dans le contexte du jeu.
 *
 * Un entraîneur est responsable de gérer une équipe de monstres, une boîte pour stocker des monstres supplémentaires
 * et un sac contenant des objets appelés MonsterKubes. L'entraîneur a également une somme d'argent associée.
 *
 * @property id L'identifiant unique de l'entraîneur.
 * @property nom Le nom de l'entraîneur.
 * @property argents La quantité d'argent en possession de l'entraîneur.

 */
class Entraineur(
    var id: Int,
    var nom: String,
    var argents: Int,
    var isNpc: Boolean = true,                // TRUE = NPC, FALSE = vrai joueur
    var zoneActuelle: Int? = null,           // zone actuelle pour les vrais joueurs, null pour NPC
    var equipeMonstre: MutableList<IndividuMonstre> = mutableListOf(),
    var boiteMonstre: MutableList<IndividuMonstre> = mutableListOf(),
    var sacAItems: MutableList<Item> = mutableListOf()
    // TODO sacAKube
) {
    /**
     * Affiche les détails de l'entraîneur, y compris son nom et la quantité d'argent en sa possession.
     */
    override fun toString(): String {
        return "Entraineur(id=$id, nom=$nom, argent=$argents, isNpc=$isNpc, zoneActuelle=$zoneActuelle)"
    }

    fun afficheDetail() {
        println("Dresseur : $nom")
        println("Argents : $argents")
        println("NPC : $isNpc")
        if (!isNpc) {
            println("Zone actuelle : ${zoneActuelle ?: "Non définie"}")
        }
    }
}