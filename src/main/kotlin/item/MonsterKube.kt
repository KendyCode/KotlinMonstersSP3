package item

import monstre.IndividuMonstre
import kotlin.random.Random
import kotlin.random.nextInt

import org.example.dresseur.Entraineur


/**
 * Représente un objet de capture permettant d’attraper des monstres sauvages.
 *
 * Hérite de la classe abstraite [Item] et implémente l’interface [Utilisable],
 * ce qui lui permet d’être utilisé directement pendant un combat.
 *
 * La capture repose sur un calcul probabiliste dépendant :
 * - du pourcentage de PV restants de la cible,
 * - de la probabilité de base de capture ([chanceCapture]).
 *
 * @property chanceCapture Probabilité de capture de base (en pourcentage, 0–100).
 */
class MonsterKube(
    id: Int,
    nom: String,
    description: String,
    var chanceCapture: Double,
) : Item(id, nom, description), Utilisable {

    /**
     * Utilise un **Monster Kube** pour tenter de capturer un [IndividuMonstre] sauvage
     * au nom d’un [Entraineur].
     *
     * ### Déroulement de la tentative de capture :
     * 1. **Vérification de la capturabilité :**
     *    - Si le monstre ciblé possède déjà un entraîneur (`entraineur != null`),
     *      la capture est impossible.
     *
     * 2. **Calcul de la probabilité de capture :**
     *    - On calcule le ratio de vie restante :
     *      `ratioVie = pv / pvMax`
     *    - Puis la probabilité ajustée :
     *      `chanceEffective = chanceCapture * (1.5 - ratioVie)`
     *    - Une chance minimale de **5 %** est toujours garantie.
     *
     * 3. **Tirage aléatoire (0–100)** :
     *    - Si le nombre aléatoire est **inférieur à `chanceEffective`**, la capture réussit.
     *
     * 4. **En cas de capture réussie :**
     *    - Le joueur peut **renommer** le monstre capturé.
     *    - Si l’équipe du joueur contient déjà **6 monstres**, le monstre est
     *      stocké dans la **boîte de réserve** (`boiteMonstre`).
     *    - Sinon, il est ajouté directement à l’**équipe active** (`equipeMonstre`).
     *    - La référence du champ `entraineur` du monstre est mise à jour.
     *
     * 5. **En cas d’échec :**
     *    - Un message d’échec est affiché indiquant que la capture a échoué.
     *
     * ---
     * @param cible   Le monstre sauvage ciblé par la tentative de capture.
     * @param joueur  L'entraîneur qui utilise le Monster Kube.
     * @return `true` une fois la tentative terminée (qu’elle réussisse ou non).
     *
     * @see Utilisable
     * @see IndividuMonstre
     * @see org.example.dresseur.Entraineur
     */
    override fun utiliser(cible: IndividuMonstre,joueur: Entraineur): Boolean {
        println("Vous lancez le Monster Kube !")
        // Vérifie si le monstre a déjà un entraîneur (donc non capturable)
        if (cible.entraineur!=null){
            println("Le monstre ne peut pas etre capture")
        }
        else{
            // Calcul du ratio de PV restant
            var ratioVie = cible.pv / cible.pvMax
            // Ajustement dynamique de la probabilité de capture
            var chanceEffective = chanceCapture * (1.5 - ratioVie)

            // Garantie d'une probabilité minimale
            if (chanceEffective<5){
                chanceEffective = 5.0
            }

            // Génération d'un nombre aléatoire pour déterminer la réussite
            var nbAleatoire = Random.nextInt(0..100)

            // Capture réussie si le tirage est inférieur à la chance effective
            if (nbAleatoire<chanceEffective){
                println("Le monstre est capturé")
                println("Donnez lui un nouveau nom")
                var nouveauNom = readln()
                if (nouveauNom.isNotEmpty()){
                    cible.nom = nouveauNom
                }

                // Si l’équipe du joueur est complète → le monstre va dans la boîte
                if(joueur.equipeMonstre.size >= 6){
                    joueur.boiteMonstre += cible
                } else {
                    joueur.equipeMonstre += cible
                }
                // Le monstre appartient désormais au joueur
                cible.entraineur = joueur

            } else{
                // Échec de la tentative de capture
                println("Floppeur !!! Le Kube n'a pas pu capturer le monstre !")
            }

        }

        return true
    }
}