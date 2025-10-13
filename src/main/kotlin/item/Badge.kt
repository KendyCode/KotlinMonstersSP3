package item
import org.example.dresseur.Entraineur
/**
 * Représente un **badge** obtenu après avoir vaincu un **champion** dans une arène.
 *
 * Un badge est un **objet symbolique** qui hérite de la classe [Item].
 * Il sert principalement à marquer la progression du joueur dans le jeu,
 * attestant de sa victoire contre un champion d’arène.
 *
 * ### Caractéristiques :
 * - Hérite de la structure commune des [Item]s (id, nom, description).
 * - Associe un badge à un **champion spécifique**, représenté par un objet [Entraineur].
 * - Ne possède pas de comportement particulier (pas de méthode `utiliser()`).
 *
 * ### Responsabilités :
 * - Identifier un badge unique (via `id` et `nom`).
 * - Relier chaque badge à un **champion vaincu**.
 * - Permettre au système de jeu de vérifier la **progression** du joueur
 *   (ex. nombre de badges obtenus).
 *
 * ### Exemple d’utilisation :
 * ```kotlin
 * val badgeFeu = Badge(
 *     id = 1,
 *     nom = "Badge Flamme",
 *     description = "Symbole de victoire contre le champion de l’arène de feu.",
 *     champion = championArèneFeu
 * )
 * joueur.badges.add(badgeFeu)
 * ```
 *
 * @property champion Le [Entraineur] champion d’arène ayant remis ce badge au joueur après sa défaite.
 *
 * @constructor Crée un badge lié à un champion, avec un identifiant, un nom et une description.
 */
class Badge(id: Int, nom: String, description: String,var champion: Entraineur): Item(id,nom,description) {
}