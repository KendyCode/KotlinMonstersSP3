package monstre

import java.io.File

/**
 * Représente une espèce de monstre dans le jeu.
 *
 * Une espèce de monstre sert de "modèle" pour créer des individus. Elle définit les caractéristiques
 * de base que tous les monstres de cette espèce auront, telles que les statistiques initiales,
 * les multiplicateurs de croissance, le type et les particularités.
 *
 * @property id L'identifiant unique de l'espèce.
 * @property nom Le nom de l'espèce de monstre (ex: "Dracofeu").
 * @property type Le ou les éléments du monstre (ex: "Feu", "Eau", "Plante").
 * @property baseAttaque La valeur de base de l'attaque physique.
 * @property baseDefense La valeur de base de la défense physique.
 * @property baseVitesse La valeur de base de la vitesse.
 * @property baseAttaqueSpe La valeur de base de l'attaque spéciale.
 * @property baseDefenseSpe La valeur de base de la défense spéciale.
 * @property basePv La valeur de base des points de vie.
 * @property modAttaque Multiplicateur de croissance pour l'attaque physique lors des montées de niveau.
 * @property modDefense Multiplicateur de croissance pour la défense physique.
 * @property modVitesse Multiplicateur de croissance pour la vitesse.
 * @property modAttaqueSpe Multiplicateur de croissance pour l'attaque spéciale.
 * @property modDefenseSpe Multiplicateur de croissance pour la défense spéciale.
 * @property modPv Multiplicateur de croissance pour les points de vie.
 * @property description Une description textuelle de l'espèce.
 * @property particularites Informations supplémentaires sur les particularités ou aptitudes spéciales de l'espèce.
 * @property caracteres Représentation artistique ASCII ou symbole illustrant l'espèce.
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
     * @param deFace Détermine si l'art affiché est de face (true) ou de dos (false).
     *               La valeur par défaut est true.
     * @return Une chaîne de caractères contenant l'art ASCII du monstre avec les codes couleur ANSI.
     *         L'art est lu à partir d'un fichier texte dans le dossier resources/art.
     */
    fun afficheArt(deFace: Boolean=true): String{
        val nomFichier = if(deFace) "front" else "back";
        val art=  File("src/main/resources/art/${this.nom.lowercase()}/$nomFichier.txt").readText()
        val safeArt = art.replace("/", "∕")
        return safeArt.replace("\\u001B", "\u001B")
    }
    }

