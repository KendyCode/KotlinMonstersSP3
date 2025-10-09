package monstre
import kotlin.random.Random
import org.example.dresseur.Entraineur
import kotlin.math.pow
import kotlin.math.round

class IndividuMonstre(
    var id : Int,
    var nom : String,
    var espece : EspeceMonstre,
    var entraineur: Entraineur?= null,
    expInit : Double){

    var niveau : Int = 1
    var attaque : Int = espece.baseAttaque + listOf(-2, 2).random()
    var defense : Int = espece.baseDefense + listOf(-2, 2).random()
    var vitesse : Int = espece.baseVitesse + listOf(-2, 2).random()
    var attaqueSpe : Int = espece.baseAttaqueSpe + listOf(-2, 2).random()
    var defenseSpe : Int = espece.baseDefenseSpe + listOf(-2, 2).random()
    var pvMax : Int = espece.basePv + listOf(-5, 5).random()
    var potentiel : Double = Random.nextDouble(0.5,2.000001)

    var exp : Double = 0.0
        get()=field
        set(value){
            field=value
            var estNiveau1 = niveau==1


            while(field >= palierExp(niveau)){
                levelUp()
                if(estNiveau1==false){
                    println("Le monstre $nom est maintenant niveau $niveau !")
                }
            }

        }
    init {
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

    fun levelUp(): Unit{
        niveau += 1

        // sauvegarde des anciens PV max pour calcul du gain
        val ancienPvMax = pvMax

        // maj pv max avec formule donnée
        pvMax = round(espece.modPv * potentiel).toInt() + Random.nextInt(-5, 6)

        // maj stats avec formule donnée
        attaque += round(espece.modAttaque * potentiel).toInt() + Random.nextInt(-2, 3)
        defense += round(espece.modDefense * potentiel).toInt() + Random.nextInt(-2, 3)
        vitesse += round(espece.modVitesse * potentiel).toInt() + Random.nextInt(-2, 3)
        attaqueSpe += round(espece.modAttaqueSpe * potentiel).toInt() + Random.nextInt(-2, 3)
        defenseSpe += round(espece.modDefenseSpe * potentiel).toInt() + Random.nextInt(-2, 3)

        // gain de PV = différence entre nouveaux et anciens PV max
        val gainPv = pvMax - ancienPvMax
        pv += gainPv


    }


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

    /**
     * Attaque un autre [IndividuMonstre] et inflige des dégâts.
     *
     * Les dégâts sont calculés de manière très simple pour le moment :
     * `dégâts = attaque - (défense / 2)` (minimum 1 dégât).
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