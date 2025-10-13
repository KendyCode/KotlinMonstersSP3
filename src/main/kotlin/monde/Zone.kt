package monde
import jeu.CombatMonstre

import monstre.EspeceMonstre
import monstre.IndividuMonstre
import org.example.dresseur.Entraineur


/**
 * Représente une zone géographique du monde du jeu.
 *
 * Une zone définit :
 * - Les espèces de monstres sauvages qui peuvent y apparaître.
 * - La plage d'expérience associée aux rencontres locales.
 * - Les liens avec les zones adjacentes (précédente et suivante).
 *
 * Elle sert principalement de contexte pour la génération et la
 * gestion des combats contre des monstres sauvages.
 *
 * @property id Identifiant unique de la zone.
 * @property nom Nom lisible de la zone.
 * @property expZone Niveau d'expérience de base des monstres sauvages dans la zone.
 * @property especesMonstres Liste mutable des espèces pouvant apparaître dans la zone.
 * @property zoneSuivante Référence vers la zone suivante dans la progression du monde.
 * @property zonePrecedente Référence vers la zone précédente (retour possible du joueur).
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
     * Génère dynamiquement un monstre sauvage en fonction des paramètres de la zone.
     *
     * Le processus de génération s'appuie sur :
     * - Une espèce aléatoire issue de [especesMonstres].
     * - Une expérience initiale basée sur [expZone], modulée de ±20 points.
     *
     * @return Une instance d'[IndividuMonstre] représentant le monstre sauvage généré.
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
     * Déclenche une rencontre entre le joueur et un monstre sauvage.
     *
     * Cette fonction orchestre :
     * 1. La génération d’un monstre sauvage via [genereMonstre].
     * 2. La sélection automatique du premier monstre vivant du joueur.
     * 3. L’instanciation d’un [CombatMonstre] et le lancement du combat.
     *  @param joueur L’entraîneur affrontant le monstre sauvage.
     * @throws NullPointerException si le joueur n’a aucun monstre vivant.
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



}