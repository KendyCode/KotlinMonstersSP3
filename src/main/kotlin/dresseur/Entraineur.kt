package org.example.dresseur

import monstre.EspeceMonstre
import monstre.IndividuMonstre
import item.Item

/**
 * Représente un entraîneur dans le contexte du jeu.
 *
 * Un entraîneur est une entité jouable ou non-jouable qui :
 * - Possède et gère une équipe de monstres actifs.
 * - Peut stocker des monstres supplémentaires dans une boîte.
 * - Possède un sac contenant des objets utilisables en combat ou pour la progression.
 * - Dispose d'une quantité d'argent pour acheter des objets ou services.
 *
 * Cette classe constitue le point central pour toutes les interactions du joueur
 * avec ses monstres et objets.
 *
 * @property id Identifiant unique de l'entraîneur.
 * @property nom Nom de l'entraîneur (ex: "Alice", "Red").
 * @property argents Montant d'argent possédé par l'entraîneur, utilisé pour achats.
 * @property equipeMonstre Liste mutable des monstres actuellement actifs dans l’équipe de combat.
 *                          La taille maximale peut être limitée par les règles du jeu.
 * @property boiteMonstre Liste mutable de monstres supplémentaires appartenant à l'entraîneur.
 *                         Ces monstres ne participent pas directement aux combats.
 * @property sacAItems Liste mutable d’objets (Items) disponibles pour l’entraîneur.
 *                      Contient des objets consommables ou utilisables sur les monstres.
 *                      Exemples : potions, MonsterKubes, Pokéballs, etc.
 */
class Entraineur(
    var id: Int,
    var nom: String,
    var argents: Int,
    var equipeMonstre: MutableList<IndividuMonstre> = mutableListOf(),
    var boiteMonstre: MutableList<IndividuMonstre> = mutableListOf(),
    var sacAItems: MutableList<Item> = mutableListOf()
    // TODO sacAKube
) {
    /**
     * Fournit une représentation textuelle complète de l'entraîneur.
     *
     * Cette méthode est utile pour :
     * - Le débogage.
     * - Afficher rapidement l’état complet du joueur.
     * - Inclut le nom, l’argent, l’équipe active, la boîte et le sac d’items.
     *
     * @return Une chaîne de caractères descriptive contenant toutes les informations principales.
     */
    override fun toString(): String {
        return "Entraineur(id=$id, nom=$nom, argent=$argents , JE SUIS LE GOAT, equipeMonstre=$equipeMonstre, boiteMonstre=$boiteMonstre, sacAItems=$sacAItems)"
    }

    /**
     * Affiche les détails essentiels de l’entraîneur à l’utilisateur.
     *
     * Différence avec `toString()` :
     * - `toString()` est plus technique, utile pour le débogage.
     * - `afficheDetail()` est destiné à l’interface utilisateur.
     *
     * Affiche :
     * 1. Le nom de l’entraîneur.
     * 2. La quantité d’argent disponible.
     */
    fun afficheDetail() {
        println("Dresseur : $nom")
        println("Argents : $argents")

    }
}