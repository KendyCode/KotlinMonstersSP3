package monde
import jeu.CombatMonstre
import joueur
import monstre.EspeceMonstre
import monstre.IndividuMonstre

class Zone(
    var id : Int,
    var nom : String,
    var expZone : Int,
    var especesMonstres: MutableList<EspeceMonstre> = mutableListOf(),
    var zoneSuivante : Zone? = null,
    var zonePrecedente : Zone? = null,

    //TODO faire la méthode genereMonstre()
    //TODO faire la méthode rencontreMonstre()
    ){
    fun genereMonstre() : IndividuMonstre{
        var genere_Espece_Monstre = especesMonstres.random()
        var genere_Exp_Zone : Int = listOf(-20, 20).random() + expZone
        var monstreZone = IndividuMonstre(4000,"Savage",genere_Espece_Monstre,null,genere_Exp_Zone.toDouble())
        return monstreZone
    }

    fun rencontreMonstre(){
        val monstreSauvage = genereMonstre()
        var premierPokemon: IndividuMonstre? = null
        joueur.equipeMonstre.forEach(){monstre ->
            if(monstre.pv>0){
                premierPokemon = monstre
            }
        }
        val combat = CombatMonstre(premierPokemon!!,monstreSauvage)
        combat.lanceCombat()
    }



}