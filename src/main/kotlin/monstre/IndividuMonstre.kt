package monstre
import kotlin.random.Random
import org.example.dresseur.Entraineur
import kotlin.math.pow
import kotlin.math.round

/**
 * Représentation d’un individu tel qu’il est stocké en base de données.
 * (Version simplifiée sans logique de jeu)
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
 * Représente une instance individuelle d’un monstre appartenant à une [EspeceMonstre].
 *
 * Cette classe encapsule :
 * - Les attributs individuels (statistiques, expérience, niveau)
 * - Les mécanismes de progression (gain d’expérience, montée de niveau)
 * - Les opérations de combat basiques (attaque, gestion des PV)
 *
 * @property id Identifiant unique de l’individu.
 * @property nom Nom propre de l’individu (modifiable).
 * @property espece Référence vers l’espèce dont l’individu dérive ses statistiques.
 * @property entraineur Entraîneur propriétaire (nullable pour les monstres sauvages).
 * @property expInit Expérience initiale injectée à l’initialisation (peut déclencher un level-up immédiat).
 */
class IndividuMonstre(
    var id : Int,
    var nom : String,
    var espece : EspeceMonstre,
    var entraineur: Entraineur?= null,
    expInit : Double){

    // --- PROPRIÉTÉS DE BASE ---

    /** Niveau actuel de l’individu. */
    var niveau : Int = 1


    /**
     * Génération des statistiques initiales :
     * les valeurs sont dérivées des bases d’espèce,
     * avec une légère variation pseudo-aléatoire ±2 ou ±5 selon la statistique.
     */
    var attaque : Int = espece.baseAttaque + listOf(-2, 2).random()
    var defense : Int = espece.baseDefense + listOf(-2, 2).random()
    var vitesse : Int = espece.baseVitesse + listOf(-2, 2).random()
    var attaqueSpe : Int = espece.baseAttaqueSpe + listOf(-2, 2).random()
    var defenseSpe : Int = espece.baseDefenseSpe + listOf(-2, 2).random()
    var pvMax : Int = espece.basePv + listOf(-5, 5).random()

    /**
     * Facteur de croissance individuel. Sert de multiplicateur pour la progression
     * des statistiques à chaque montée de niveau.
     *
     * Valeur comprise entre 0.5 et 2.0, exclusive du maximum.
     */
    var potentiel : Double = Random.nextDouble(0.5,2.000001)


    // --- EXPÉRIENCE ET PROGRESSION ---

    /**
     * Expérience cumulée de l’individu. L’accesseur `set` déclenche automatiquement
     * une vérification de palier et des incréments de niveau via [levelUp].
     */
    var exp : Double = 0.0
        get()=field
        set(value){
            field=value
            var estNiveau1 = niveau==1

            // Boucle de progression : permet plusieurs level-ups consécutifs
            while(field >= palierExp(niveau)){
                levelUp()
                if(estNiveau1==false){
                    println("Le monstre $nom est maintenant niveau $niveau !")
                }
            }

        }
    init {
        // Injection de l’expérience initiale avec déclenchement du setter
        this.exp = expInit // applique le setter et déclenche un éventuel level-up
    }





    /**
     * Calcule l'expérience totale nécessaire pour atteindre un niveau donné.
     *
     * @param niveau Niveau cible.
     * @return Expérience cumulée nécessaire pour atteindre ce niveau.
     */
    fun palierExp(niveau : Int) : Double{
        return 100 * ( (niveau - 1).toDouble().pow(2.0) )
    }

    /**
     * Incrémente le niveau et met à jour les statistiques en fonction du [potentiel].
     * Applique une croissance semi-aléatoire encadrée par les modificateurs d’espèce.
     *
     * La méthode préserve les PV relatifs en ajustant [pv] selon la variation de [pvMax].
     */
    fun levelUp(): Unit{
        niveau += 1

        // sauvegarde des anciens PV max pour calcul du gain
        val ancienPvMax = pvMax

        // maj pv max avec formule donnée
        pvMax = round(espece.modPv * potentiel).toInt() + Random.nextInt(-5, 6)

        // Mise à jour des autres statistiques (variation ±2 à ±3)
        attaque += round(espece.modAttaque * potentiel).toInt() + Random.nextInt(-2, 3)
        defense += round(espece.modDefense * potentiel).toInt() + Random.nextInt(-2, 3)
        vitesse += round(espece.modVitesse * potentiel).toInt() + Random.nextInt(-2, 3)
        attaqueSpe += round(espece.modAttaqueSpe * potentiel).toInt() + Random.nextInt(-2, 3)
        defenseSpe += round(espece.modDefenseSpe * potentiel).toInt() + Random.nextInt(-2, 3)

        // gain de PV = différence entre nouveaux et anciens PV max
        val gainPv = pvMax - ancienPvMax
        pv += gainPv


    }

    // --- POINTS DE VIE ---

    /**
     *  @property pv  Points de vie actuels.
     * Ne peut pas être inférieur à 0 ni supérieur à [pvMax].
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
     * Exécute une attaque standard sur un autre [IndividuMonstre].
     *
     * Formule simplifiée :
     * ```
     * dégâts = max(1, attaque - (defense / 2))
     * ```
     * Met à jour le PV de la cible et journalise les dégâts infligés.
     *
     * @param cible Monstre cible de l’attaque.
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
     * Demande au joueur de renommer le monstre.
     * Si l'utilisateur entre un texte vide, le nom n'est pas modifié.
     */
    fun renommer() : Unit{
        println("Renommer ${this.nom}")
        var nouveauNom = readln()
        if (nouveauNom.isNotEmpty()){
            this.nom = nouveauNom
        }
    }

    /**
     * Affiche les informations détaillées du monstre,
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






}