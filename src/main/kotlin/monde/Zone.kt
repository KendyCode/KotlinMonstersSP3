package monde
import jeu.CombatMonstre

import monstre.EspeceMonstre
import monstre.IndividuMonstre
import org.example.dresseur.Entraineur


/**
 * Représente une zone géographique dans le monde du jeu.
 *
 * Une zone sert de contexte pour les rencontres avec des monstres sauvages.
 * Elle définit :
 *  - Les espèces pouvant apparaître dans la zone ([especesMonstres]).
 *  - Le niveau d'expérience de base des monstres locaux ([expZone]).
 *  - Les liens avec les zones adjacentes ([zoneSuivante], [zonePrecedente]).
 *
 * Cette structure permet d'organiser le monde en étapes progressives et
 * de gérer la difficulté et la diversité des rencontres.
 *
 * @property id Identifiant unique pour la zone, utile pour la persistance et la navigation.
 * @property nom Nom lisible par le joueur pour cette zone (ex : "Forêt de Luma").
 * @property expZone Niveau d'expérience moyen des monstres sauvages rencontrés.
 * @property especesMonstres Liste mutable des espèces de monstres pouvant apparaître dans cette zone.
 * @property zoneSuivante Référence vers la zone suivante pour la progression du joueur.
 * @property zonePrecedente Référence vers la zone précédente pour permettre le retour.
 */
class Zone(
    var id : Int,
    var nom : String,
    var expZone : Int,
    var especesMonstres: MutableList<EspeceMonstre> = mutableListOf(),
    var zoneSuivante : Zone? = null,
    var zonePrecedente : Zone? = null,


    ){

    /**
     * Génère dynamiquement un monstre sauvage correspondant à la zone.
     *
     * Le processus de génération suit les étapes suivantes :
     * 1. Sélection aléatoire d'une espèce parmi celles présentes dans la zone.
     * 2. Détermination d'une expérience initiale basée sur [expZone], avec une variance aléatoire de ±20 points
     *    pour éviter que tous les monstres aient exactement la même force.
     * 3. Instanciation d'un [IndividuMonstre] représentant le monstre sauvage.
     *
     * @return Un monstre sauvage ([IndividuMonstre]) prêt à combattre.
     */
    fun genereMonstre() : IndividuMonstre{
        // Sélection aléatoire d’une espèce présente dans la zone
        var genere_Espece_Monstre = especesMonstres.random()

        // Ajustement de l’expérience de base avec une légère variance aléatoire
        var genere_Exp_Zone : Int = listOf(-20, 20).random() + expZone

        // Instanciation du monstre sauvage
        var monstreZone = IndividuMonstre(4000,"Savage",genere_Espece_Monstre,null,genere_Exp_Zone.toDouble())
        return monstreZone
    }

    /**
     * Déclenche une rencontre entre le joueur et un monstre sauvage dans cette zone.
     *
     * Fonctionnement :
     * 1. Génère un monstre sauvage via [genereMonstre].
     * 2. Sélectionne le premier monstre vivant du joueur pour le combat.
     * 3. Instancie un [CombatMonstre] et lance le combat.
     *
     * Cette méthode simplifie la gestion d'une rencontre aléatoire et
     * automatise le choix du monstre du joueur pour le combat.
     *
     * @param joueur L'entraîneur affrontant le monstre sauvage.
     * @throws NullPointerException Si le joueur n'a aucun monstre vivant dans son équipe.
     */
    fun rencontreMonstre(joueur: Entraineur){
        // Génération du monstre sauvage
        val monstreSauvage = genereMonstre()

        // Sélection du premier monstre du joueur encore en état de combattre
        var premierPokemon: IndividuMonstre? = null
        joueur.equipeMonstre.forEach(){monstre ->
            if(monstre.pv>0){
                premierPokemon = monstre
            }
        }
        // Déclenchement du combat : la non-nullité est forcée ici
        // car un joueur est supposé toujours avoir au moins un monstre actif.
        val combat = CombatMonstre(joueur,premierPokemon!!,monstreSauvage)

        // Lancement du système de combat (gestion du tour à tour)
        combat.lanceCombat()
    }

    /**
     * Représentation textuelle complète de la zone pour le debug ou l'affichage console.
     *
     * Affiche :
     *  - ID, nom et expérience de base de la zone
     *  - ID des zones adjacentes
     *  - Liste des noms des espèces présentes
     */
    override fun toString(): String {
        return "Zone(id=$id, nom=$nom, expZone=$expZone, " +
                "zoneSuivante=${zoneSuivante?.id}, zonePrecedente=${zonePrecedente?.id}, " +
                "especes=${especesMonstres.map { it.nom }})"
    }


}