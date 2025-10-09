package item

import monstre.IndividuMonstre
import kotlin.random.Random
import kotlin.random.nextInt
import joueur

class MonsterKube(
    id: Int,
    nom: String,
    description: String,
    var chanceCapture: Double,
) : Item(id, nom, description), Utilisable {
    override fun utiliser(cible: IndividuMonstre): Boolean {
        println("Vous lancez le Monster Kube !")
        if (cible.entraineur!=null){
            println("Le monstre ne peut etre capturé")
        }
        else{
            var ratioVie = cible.pv / cible.pvMax
            var chanceEffective = chanceCapture * (1.5 - ratioVie)

            if (chanceEffective<5){
                chanceEffective = 5.0
            }
            var nbAleatoire = Random.nextInt(0..100)

            if (nbAleatoire<chanceEffective){
                println("Le monstre est capturé")
                println("Donner lui un nouveau nom")
                var nouveauNom = readln()
                if (nouveauNom.isNotEmpty()){
                    cible.nom = nouveauNom
                }

                if(joueur.equipeMonstre.size >= 6){
                    joueur.boiteMonstre += cible
                } else {
                    joueur.equipeMonstre += cible
                }

                cible.entraineur = joueur

            } else{
                println("Presque ! Le Kube n'a pas pu capturer le monstre !")
            }

        }

        return true
    }
}
