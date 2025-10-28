package monstre

import java.io.File

/**
 * Représente une espèce de monstre dans le jeu.
 *
 * Cette classe sert de "modèle" pour créer des individus ([IndividuMonstre]).
 * Chaque espèce définit :
 *  - Les statistiques de base que tous ses individus possèdent.
 *  - Les multiplicateurs de croissance pour les montées de niveau.
 *  - Le type et les particularités de l'espèce.
 *  - Une représentation artistique ASCII pour l'affichage.
 *
 * Une espèce ne contient pas de statistiques spécifiques à un individu,
 * elles sont partagées par tous les monstres de cette espèce.
 *
 * @property id Identifiant unique permettant de référencer cette espèce dans une base de données.
 * @property nom Nom de l'espèce (ex : "Dracofeu").
 * @property type Type élémentaire du monstre (ex : "Feu", "Eau") pouvant influencer
 *               l'efficacité des attaques et défenses contre d'autres types.
 * @property baseAttaque Valeur initiale de l'attaque physique pour un individu de cette espèce.
 * @property baseDefense Valeur initiale de la défense physique.
 * @property baseVitesse Valeur initiale de la vitesse.
 * @property baseAttaqueSpe Valeur initiale de l'attaque spéciale.
 * @property baseDefenseSpe Valeur initiale de la défense spéciale.
 * @property basePv Valeur initiale des points de vie.
 * @property modAttaque Multiplicateur de croissance de l'attaque physique lors des level-ups.
 * @property modDefense Multiplicateur de croissance de la défense physique.
 * @property modVitesse Multiplicateur de croissance de la vitesse.
 * @property modAttaqueSpe Multiplicateur de croissance de l'attaque spéciale.
 * @property modDefenseSpe Multiplicateur de croissance de la défense spéciale.
 * @property modPv Multiplicateur de croissance des points de vie.
 * @property description Description textuelle de l'espèce pour l'utilisateur.
 * @property particularites Informations sur les particularités ou aptitudes spéciales.
 * @property caracteres Représentation artistique ASCII ou symbole du monstre.
 */
class EspeceMonstre(
    var id : Int,
    var nom: String,
    var type: String,
    val baseAttaque: Int,
    val baseDefense: Int,
    val baseVitesse: Int,
    val baseAttaqueSpe: Int,
    val baseDefenseSpe: Int,
    val basePv: Int,
    val modAttaque: Double,
    val modDefense: Double,
    val modVitesse: Double,
    val modAttaqueSpe: Double,
    val modDefenseSpe: Double,
    val modPv: Double,
    val description: String = "",
    val particularites: String = "",
    val caracteres: String = ""){

    /**
     * Affiche la représentation artistique ASCII du monstre.
     *
     * Les fichiers ASCII sont stockés dans :
     * `src/main/resources/art/<nom_de_l_espece_lowercase>/front.txt` ou `back.txt`.
     * Cela permet de différencier la vue de face et la vue de dos du monstre.
     *
     * Le contenu est sécurisé en remplaçant certains caractères :
     * - "/" devient "∕" pour éviter la confusion avec le séparateur de chemin.
     * - Les codes ANSI (\u001B) sont conservés pour le formatage couleur dans la console.
     *
     * @param deFace Si true, renvoie l'art de face, sinon de dos.
     * @return Chaîne de caractères contenant l'art ASCII avec les codes couleur.
     */
    fun afficheArt(deFace: Boolean=true): String{
        val nomFichier = if(deFace) "front" else "back";
        val art=  File("src/main/resources/art/${this.nom.lowercase()}/$nomFichier.txt").readText()
        val safeArt = art.replace("/", "∕")
        return safeArt.replace("\\u001B", "\u001B")
    }

    /**
     * Représentation textuelle simplifiée de l'espèce.
     *
     * Utile pour le debug ou l'affichage rapide dans la console.
     * Ne montre que les informations essentielles : id, nom, type et points de vie de base.
     */
    override fun toString(): String {
        return "EspeceMonstre(id=$id, nom=$nom, type=$type, basePv=$basePv)"
    }
}

