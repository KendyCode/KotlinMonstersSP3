import DAO.EntraineurDAO
import DAO.EspeceMonstreDAO
import DAO.IndividuMonstreDAO
import DAO.ZoneDAO
import item.Badge
import item.MonsterKube
import monde.Zone
import org.example.dresseur.Entraineur
import monstre.EspeceMonstre
import monstre.IndividuMonstre
import jeu.Partie
import jdbc.BDD
import monstre.IndividuMonstreEntity

// --- Connexion à la base de données ---
// Initialise l'objet BDD pour gérer la connexion et les opérations SQL
val db = BDD()

// --- Initialisation des DAO ---
// Chaque DAO permet d'interagir avec une table spécifique de la base
val entraineurDAO= EntraineurDAO(db) // Gestion des entraîneurs
val especeMonstreDAO = EspeceMonstreDAO(db) // Gestion des espèces de monstres
val individuMonstreDAO = IndividuMonstreDAO(db) // Gestion des individus de monstres
val zoneDAO = ZoneDAO(db, especeMonstreDAO) // Gestion des zones, nécessite EspeceMonstreDAO pour les relations

// --- Chargement des données depuis la BDD ---
// Récupération de toutes les données pour initialiser le jeu
val listeEntraineur = entraineurDAO.findAll() // Liste complète des entraîneurs
val listeEspeces = especeMonstreDAO.findAll() // Liste complète des espèces de monstres
val listeIndividus = individuMonstreDAO.findAll() // Liste complète des individus de monstres
val listeZones = zoneDAO.findAll() // Liste complète des zones du jeu

// --- Création des joueurs initiaux ---
// Joueur principal et rival du joueur
var joueur = Entraineur(1, "Sacha", 100)
var rival = Entraineur(2,"Regis",200)

// --- Création d'objets ---
// Exemples d'objets pouvant être dans le sac du joueur
var objet1 = MonsterKube(1,"cube", "description",11.0)

// --- Fonction principale ---
fun main() {
    /**
     * Démarre une nouvelle partie.
     *
     * ⚡ Étapes :
     * 1. Accueille le joueur et lui demande son nom.
     * 2. Crée une nouvelle instance de Partie avec le joueur et la première zone.
     * 3. Réinitialise l'ID du joueur pour permettre l'insertion en BDD.
     * 4. Sauvegarde le joueur dans la base via entraineurDAO.
     *
     * @return Partie initialisée pour le joueur.
     */
    fun nouvellePartie():Partie{
        println("Bienvenue dans le monde magique des Pokémon!")
        println("Rentrez votre nom : ")
        val nomJoueur = readln() // Lecture du nom du joueur depuis la console
        joueur.nom = nomJoueur

        // Création de la partie avec le joueur et la première zone du jeu
        val PartieJoueur = Partie(1,joueur,listeZones[0])

        // Réinitialisation de l'ID pour insertion en base
        joueur.id=0
        entraineurDAO.save(joueur) // Sauvegarde le joueur dans la BDD
        return PartieJoueur
    }

    // --- Gestion du sac du joueur ---
    // Ajoute un objet au sac du joueur avant le début de la partie
    joueur.sacAItems.add(objet1)

    // --- Démarrage de la partie ---
    val partie = nouvellePartie() // Crée une nouvelle partie
    partie.choixStarter() // Permet au joueur de choisir son montre de départ
    db.close() // Ferme la connexion à la BDD
    partie.jouer() // Lancement du gameplay principal


}




