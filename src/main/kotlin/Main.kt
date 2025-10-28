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

//La connexion a la BDD
val db = BDD()
//Les DAO
val entraineurDAO= EntraineurDAO(db)
val especeMonstreDAO = EspeceMonstreDAO(db) //  DAO pour les espèces
val individuMonstreDAO = IndividuMonstreDAO(db)
val zoneDAO = ZoneDAO(db, especeMonstreDAO)// DAO pour les zones
// --- Listes depuis la BDD ---
val listeEntraineur = entraineurDAO.findAll()
val listeEspeces = especeMonstreDAO.findAll()
val listeIndividus = individuMonstreDAO.findAll()
val listeZones = zoneDAO.findAll()



var joueur = Entraineur(1, "Sacha", 100)
var rival = Entraineur(2,"Regis",200)





var objet1 = MonsterKube(1,"cube", "description",11.0)

fun main() {
    println(listeEntraineur[2].equipeMonstre)
    println(listeZones)


//    for (entity in listeIndividus) {
//
//        val monstre = individuMonstreDAO.toModel(entity) ?: continue
//
//
//        // Trouver l'entraîneur correspondant à entraineur_equipe_id
//        val entraineur = listeEntraineur.find { it.id == entity.entraineurEquipeId }
//
//        // S’il a un entraîneur, on l’y ajoute
//        if (entraineur != null) {
//            monstre.entraineur = entraineur
//            entraineur.equipeMonstre.add(monstre)
//        }
//    }
//
//    // 3️⃣ Vérifions que ça marche
//    for (entraineur in listeEntraineur) {
//        println("👤 Entraîneur : ${entraineur.nom}")
//        if (entraineur.equipeMonstre.isEmpty()) {
//            println("   Aucun monstre dans son équipe.")
//        } else {
//            println("   Équipe : ${entraineur.equipeMonstre.joinToString { it.nom }}")
//        }
//    }






    fun nouvellePartie():Partie{
        println("Bienvenue dans le monde magique des Pokémon!")
        println("Rentrez votre nom : ")
        val nomJoueur = readln()
        joueur.nom = nomJoueur



        val PartieJoueur = Partie(1,joueur,listeZones[0])
        joueur.id=0
        entraineurDAO.save(joueur)
        return PartieJoueur
    }



    joueur.sacAItems.add(objet1)

    val partie = nouvellePartie()
    partie.choixStarter()

    db.close()
    partie.jouer()


}




