package item

/**
 * Représente un objet générique pouvant être utilisé, possédé ou stocké par le joueur.
 *
 * Cette classe constitue la **superclasse de base** pour tous les objets du jeu.
 * Elle définit les attributs communs à tous les types d’items (ex : objets de soin,
 * objets de capture, objets spéciaux, etc.).
 *
 * Elle est **ouverte** (`open`) afin de permettre l’héritage par des classes spécialisées
 * comme [MonsterKube] ou d’autres futurs objets implémentant des comportements spécifiques.
 *
 * ### Responsabilités :
 * - Fournir une structure de base commune à tous les items.
 * - Faciliter la gestion uniforme des objets dans l’inventaire du joueur.
 *
 * ### Remarques :
 * - Aucun comportement fonctionnel n’est défini ici.
 * - Les sous-classes doivent implémenter leurs propres méthodes (ex : `utiliser`).
 *
 * @property id Identifiant unique de l’item (utilisé pour le suivi et la différenciation).
 * @property nom Nom affiché de l’objet dans le jeu.
 * @property description Texte descriptif expliquant l’effet ou la fonction de l’objet.
 *
 * @constructor Initialise un objet générique avec ses métadonnées de base.
 */
open class Item(
    var id: Int,
    var nom: String,
    var description : String,
) {



}