package monstre
import kotlin.random.Random
import org.example.dresseur.Entraineur
import kotlin.math.pow
import kotlin.math.round

/**
 * Représentation d’un individu tel qu’il est stocké en base de données.
 * Cette classe est une version simplifiée destinée à la persistance des données
 * et ne contient pas la logique de progression ou de combat.
 *
 * Chaque instance correspond à un monstre individuel avec ses statistiques,
 * son niveau, ses PV actuels, son expérience, et ses relations avec l'espèce
 * et l'entraîneur.
 *
 * @property id Identifiant unique auto-incrémenté ou généré en base.
 * @property nom Nom du monstre (peut être renommé par l'utilisateur).
 * @property niveau Niveau actuel de l'individu (entier positif).
 * @property attaque Statistique d'attaque physique.
 * @property defense Statistique de défense physique.
 * @property vitesse Statistique de vitesse, influence l'ordre des combats.
 * @property attaqueSpe Statistique d'attaque spéciale.
 * @property defenseSpe Statistique de défense spéciale.
 * @property pvMax Points de vie maximum que peut avoir le monstre.
 * @property potentiel Facteur de croissance unique, influe sur l'évolution des stats.
 * @property exp Expérience cumulée du monstre, utilisée pour les level-ups.
 * @property pv Points de vie actuels (doit être ≤ pvMax et ≥ 0).
 * @property especeId Référence à l'espèce de ce monstre (nullable si inconnu).
 * @property entraineurEquipeId Référence à l'entraîneur si dans l'équipe (nullable).
 * @property entraineurBoiteId Référence à l'entraîneur si stocké en boîte (nullable).
 */
data class IndividuMonstreEntity(
    var id: Int = 0,
    var nom: String,
    var niveau: Int,
    var attaque: Int,
    var defense: Int,
    var vitesse: Int,
    var attaqueSpe: Int,
    var defenseSpe: Int,
    var pvMax: Int,
    var potentiel: Double,
    var exp: Double,
    var pv: Int,
    var especeId: Int?,
    var entraineurEquipeId: Int?,
    var entraineurBoiteId: Int?
)

/**
 * Classe représentant un monstre individuel en jeu.
 * Contrairement à la version Entity, cette classe contient la logique de progression
 * (gain d'expérience, montée de niveau), la gestion des PV, et les mécanismes de combat.
 *
 * @property id Identifiant unique de l’individu.
 * @property nom Nom personnalisable du monstre.
 * @property espece Espèce dont dépend le monstre pour ses statistiques de base et ses modificateurs.
 * @property entraineur Entraîneur propriétaire (nullable pour les monstres sauvages).
 * @param expInit Expérience initiale à injecter lors de la création (peut provoquer un level-up immédiat).
 */
class IndividuMonstre(
    var id : Int,
    var nom : String,
    var espece : EspeceMonstre,
    var entraineur: Entraineur?= null,
    expInit : Double){

    // --- PROPRIÉTÉS DE BASE ---

    /** Niveau actuel de l’individu, initialisé à 1 par défaut. */
    var niveau : Int = 1


    /**
     * Statistiques initiales générées à partir des valeurs de base de l'espèce
     * avec une petite variation aléatoire pour créer des individus uniques.
     *
     * Les variations utilisent des listes ou Random pour simuler une différence légère
     * entre les individus de la même espèce.
     */
    var attaque : Int = espece.baseAttaque + listOf(-2, 2).random()
    var defense : Int = espece.baseDefense + listOf(-2, 2).random()
    var vitesse : Int = espece.baseVitesse + listOf(-2, 2).random()
    var attaqueSpe : Int = espece.baseAttaqueSpe + listOf(-2, 2).random()
    var defenseSpe : Int = espece.baseDefenseSpe + listOf(-2, 2).random()
    var pvMax : Int = espece.basePv + listOf(-5, 5).random()

    /**
     * Potentiel de croissance individuelle, valeur décimale entre 0.5 et 2.0.
     * Il sert de multiplicateur pour la progression des statistiques lors des montées de niveau.
     */
    var potentiel : Double = Random.nextDouble(0.5,2.000001)


    // --- EXPÉRIENCE ET PROGRESSION ---

    /**
     * Expérience cumulée de l’individu.
     * Le setter déclenche automatiquement la vérification du palier et
     * les level-ups successifs.
     *
     * L’utilisation d’une boucle while permet de gérer plusieurs niveaux
     * gagnés d’un coup si l’expérience est importante.
     */
    var exp : Double = 0.0
        get()=field
        set(value){
            field=value
            var estNiveau1 = niveau==1

            // Level-up automatique si l'expérience dépasse le palier
            while(field >= palierExp(niveau)){
                levelUp()
                if(estNiveau1==false){
                    println("Le monstre $nom est maintenant niveau $niveau !")
                }
            }

        }
    init {
        // Injection initiale de l'expérience et déclenchement potentiel de level-up
        this.exp = expInit // applique le setter et déclenche un éventuel level-up
    }





    /**
     * Calcule le palier d'expérience requis pour atteindre un niveau donné.
     * Formule quadratique simple : 100 * (niveau - 1)^2
     *
     * @param niveau Niveau cible.
     * @return Expérience totale nécessaire pour atteindre ce niveau.
     */
    fun palierExp(niveau : Int) : Double{
        return 100 * ( (niveau - 1).toDouble().pow(2.0) )
    }

    /**
     * Augmente le niveau du monstre et fait croître ses statistiques
     * selon le potentiel et les modificateurs de l'espèce.
     *
     * La croissance est semi-aléatoire pour simuler la variabilité naturelle
     * entre les individus.
     *
     * Les PV sont ajustés proportionnellement et récupèrent les gains.
     */
    fun levelUp() {
        niveau += 1

        val ancienPvMax = pvMax

        // Croissance statistique avec facteur de potentiel et variation aléatoire
        attaque += round(espece.modAttaque * potentiel).toInt() + Random.nextInt(-2, 3)
        defense += round(espece.modDefense * potentiel).toInt() + Random.nextInt(-2, 3)
        vitesse += round(espece.modVitesse * potentiel).toInt() + Random.nextInt(-2, 3)
        attaqueSpe += round(espece.modAttaqueSpe * potentiel).toInt() + Random.nextInt(-2, 3)
        defenseSpe += round(espece.modDefenseSpe * potentiel).toInt() + Random.nextInt(-2, 3)

        // Croissance des PV proportionnelle au potentiel et modificateur de l'espèce
        val gainPv = round(espece.modPv * potentiel / 5).toInt() + Random.nextInt(-2, 3)
        pvMax += gainPv

        // Mise à jour des PV actuels en ajoutant les PV gagnés
        pv += gainPv

        // On s'assure que les PV ne dépassent pas le max
        if (pv > pvMax) pv = pvMax
    }


    // --- POINTS DE VIE ---

    /**
     * Points de vie actuels de l'individu.
     * La valeur est automatiquement encadrée entre 0 et pvMax pour éviter
     * les états invalides.
     */
    var pv : Int = pvMax
        get() = field
        set(nouveauPv) {
            field=nouveauPv
            if (nouveauPv > pvMax){
                field = pvMax
            }
            else if(nouveauPv<0){
                field = 0
            }
        }

    // --- MÉCANIQUES DE COMBAT ---

    /**
     * Effectue une attaque physique simple sur une cible.
     *
     * Formule de calcul simplifiée :
     * 1. dégât brut = attaque du monstre
     * 2. réduction = moitié de la défense du monstre (ceci semble une erreur conceptuelle : défense de la cible ?)
     * 3. dégâts finaux = max(1, dégât brut - réduction)
     *
     * @param cible Monstre cible de l'attaque.
     */
    fun attaquer(cible: IndividuMonstre){
        var degatBrut = this.attaque
        var degatTotal = degatBrut - (this.defense/2)
        if (degatTotal<1){
            degatTotal=1
        }
        var pvAvant = cible.pv
        cible.pv -= degatTotal
        var pvApres = cible.pv
        println("${nom} inflige ${pvAvant-pvApres} dégâts à ${cible.nom}")
    }

    // --- INTERACTIONS UTILISATEUR ---

    /**
     * Permet à l'utilisateur de renommer le monstre.
     * Si l'entrée est vide, le nom reste inchangé.
     */
    fun renommer() : Unit{
        println("Renommer ${this.nom}")
        var nouveauNom = readln()
        if (nouveauNom.isNotEmpty()){
            this.nom = nouveauNom
        }
    }

    /**
     * Affiche les informations complètes du monstre,
     * incluant ses statistiques actuelles et son art ASCII.
     */
    fun afficheDetail():Unit{


        var nomDetail = this.nom
        var niveauDetail = this.niveau
        var expDetail = this.exp
        var pvDetail = this.pv
        var pvMaxDetail = this.pvMax

        var attaqueDetail = this.attaque
        var defDetail = this.defense
        var vitesseDetail = this.vitesse
        var attaqueSpeDetail = this.attaqueSpe
        var defSpeDetail = this.defenseSpe

        println(espece.afficheArt())
        println("""
        
        Nom : $nomDetail
        Niveau : $niveauDetail
        Exp : $expDetail
        PV : $pvDetail / $pvMaxDetail
        Attaque : $attaqueDetail
        Défense : $defDetail
        Vitesse : $vitesseDetail
        Attaque Spéciale : $attaqueSpeDetail
        Défense Spéciale : $defSpeDetail
    """.trimIndent())

    }
    /**
     * Représentation textuelle simplifiée du monstre.
     * Utile pour le debug et l'affichage console rapide.
     */
    override fun toString(): String {
        return "IndividuMonstre(nom=$nom, niveau=$niveau, espece=${espece.nom})"
    }







}