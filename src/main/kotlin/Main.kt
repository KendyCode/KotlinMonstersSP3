import DAO.EntraineurDAO
import DAO.EspeceMonstreDAO
import DAO.IndividuMonstreDAO
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
val especeMonstreDAO = EspeceMonstreDAO(db) // 👈 ajout du DAO pour les espèces
val individuMonstreDAO = IndividuMonstreDAO(db)

// --- Listes depuis la BDD ---
val listeEntraineur = entraineurDAO.findAll()
val listeEspeces = especeMonstreDAO.findAll() // 👈 ta nouvelle variable
val listeIndividus = individuMonstreDAO.findAll()


var joueur = Entraineur(1, "Sacha", 100)
var rival = Entraineur(2,"Regis",200)


var especeSpringleaf = EspeceMonstre(id = 1, nom = "Springleaf", type = "Graine", baseAttaque = 9, baseDefense = 11, baseVitesse = 10, baseAttaqueSpe = 12, baseDefenseSpe = 14, basePv = 60, modAttaque = 6.5, modDefense = 9.0, modVitesse = 8.0, modAttaqueSpe = 7.0, modDefenseSpe = 10.0, modPv = 34.0, description = "Petit monstre espiègle rond comme une graine, adore le soleil.", particularites = "Sa feuille sur la tête indique son humeur.", caracteres = "Curieux, amical, timide")
var especeFlamkip = EspeceMonstre(id = 4, nom = "Flamkip", type = "Animal", baseAttaque = 12, baseDefense = 8, baseVitesse = 13, baseAttaqueSpe = 16, baseDefenseSpe = 7, basePv = 50, modAttaque = 10.0, modDefense = 5.5, modVitesse = 9.5, modAttaqueSpe = 9.5, modDefenseSpe = 6.5, modPv = 22.0, description = "Petit animal entouré de flammes, déteste le froid.", particularites = "Sa flamme change d’intensité selon son énergie.", caracteres = "Impulsif, joueur, loyal")
var especeAquamy = EspeceMonstre(id = 7, nom = "Aquamy", type = "Meteo", baseAttaque = 10, baseDefense = 11, baseVitesse = 9, baseAttaqueSpe = 14, baseDefenseSpe = 14, basePv = 55, modAttaque = 9.0, modDefense = 10.0, modVitesse = 7.5, modAttaqueSpe = 12.0, modDefenseSpe = 12.0, modPv = 27.0, description = "Créature vaporeuse semblable à un nuage, produit des gouttes pures.", particularites = "Fait baisser la température en s’endormant.", caracteres = "Calme, rêveur, mystérieux")
var especeLaoumi = EspeceMonstre(id = 8, nom = "Laoumi", type = "Animal", baseAttaque = 11, baseDefense = 10, baseVitesse = 9, baseAttaqueSpe = 8, baseDefenseSpe = 11, basePv = 58, modAttaque = 11.0, modDefense = 8.0, modVitesse = 7.0, modAttaqueSpe = 6.0, modDefenseSpe = 11.5, modPv = 23.0, description = "Petit ourson au pelage soyeux, aime se tenir debout.", particularites = "Son grognement est mignon mais il protège ses amis.", caracteres = "Affectueux, protecteur, gourmand")
var especeBugsyface = EspeceMonstre(id = 10, nom = "Bugsyface", type = "Insecte", baseAttaque = 10, baseDefense = 13, baseVitesse = 8, baseAttaqueSpe = 7, baseDefenseSpe = 13, basePv = 45, modAttaque = 7.0, modDefense = 11.0, modVitesse = 6.5, modAttaqueSpe = 8.0, modDefenseSpe = 11.5, modPv = 21.0, description = "Insecte à carapace luisante, se déplace par bonds et vibre des antennes.", particularites = "Sa carapace devient plus dure après chaque mue.", caracteres = "Travailleur, sociable, infatigable")
var especeGalum = EspeceMonstre(id = 13, nom = "Galum", type = "Minéral", baseAttaque = 12, baseDefense = 15, baseVitesse = 6, baseAttaqueSpe = 8, baseDefenseSpe = 12, basePv = 55, modAttaque = 9.0, modDefense = 13.0, modVitesse = 4.0, modAttaqueSpe = 6.5, modDefenseSpe = 10.5, modPv = 13.0, description = "Golem ancien de pierre, yeux lumineux en garde.", particularites = "Peut rester immobile des heures comme une statue.", caracteres = "Sérieux, stoïque, fiable")

//var zone1 = Zone(id=1, nom ="Foret Sombre", expZone = 10, especesMonstres = mutableListOf(especeSpringleaf,especeFlamkip))
//var zone2 = Zone(id=2, nom ="Caverne Obscure", expZone = 20, especesMonstres = mutableListOf(especeAquamy))
//var zone3 = Zone(id=3, nom ="Montagne Ardente", expZone = 40, especesMonstres = mutableListOf(especeAquamy,especeLaoumi))

var route1 = Zone(1,"route1",50,mutableListOf<EspeceMonstre>(especeSpringleaf,especeFlamkip,especeAquamy))
var route2 = Zone(2,"route2",75,mutableListOf<EspeceMonstre>(especeLaoumi,especeBugsyface,especeGalum))

var objet1 = MonsterKube(1,"cube", "description",11.0)

fun main() {

    fun nouvellePartie():Partie{
        println("Bienvenue dans le monde magique des Pokémon!")
        println("Rentrez votre nom : ")
        val nomJoueur = readln()
        joueur.nom = nomJoueur
        val PartieJoueur = Partie(1,joueur,route1)
        joueur.id=0
        entraineurDAO.save(joueur)
        return PartieJoueur
    }

    route1.zoneSuivante = route2
    route2.zonePrecedente = route1
    joueur.sacAItems.add(objet1)

    val partie = nouvellePartie()
    partie.choixStarter()
    db.close()
    partie.jouer()



}



// Test DAO Entraineur

//    // ✅ TEST DU DAO AVANT DE LANCER LE JEU
//    println("=== Test DAO Entraineur ===")
//    // 2️⃣ Lecture initiale
//    println("\n📋 Liste initiale des entraîneurs :")
//    val listeInitiale = entraineurDAO.findAll()
//    listeInitiale.forEach { println(it) }
//
//    // 3️⃣ Insertion simple avec save()
//    println("\n💾 Test save() : ajout de 'Pierre'")
//    val pierre = Entraineur(0, "Pierre", 300)
//    val pierreSauvegarde = entraineurDAO.save(pierre)
//    println("Résultat de save() : $pierreSauvegarde")
//
//    // 4️⃣ Lecture par ID
//    if (pierreSauvegarde != null) {
//        println("\n🔍 Test findById(${pierreSauvegarde.id}) :")
//        val trouveParId = entraineurDAO.findById(pierreSauvegarde.id)
//        println("Trouvé : $trouveParId")
//    }
//
//    // 5️⃣ Mise à jour
//    if (pierreSauvegarde != null) {
//        println("\n✏️ Test update (save sur objet existant) :")
//        pierreSauvegarde.argents = 999
//        val maj = entraineurDAO.save(pierreSauvegarde)
//        println("Après mise à jour : $maj")
//    }
//
//    // 6️⃣ Recherche par nom
//    println("\n🔎 Test findByNom('Pierre') :")
//    val listePierre = entraineurDAO.findByNom("Pierre")
//    listePierre.forEach { println(it) }
//
//    // 7️⃣ Insertion multiple avec saveAll()
//    println("\n💾 Test saveAll() : insertion multiple")
//    val nouveauxEntraineurs = listOf(
//        Entraineur(0, "Ondine", 400),
//        Entraineur(0, "Major Bob", 500),
//        Entraineur(0, "Giovanni", 1000)
//    )
//    val sauvegardes = entraineurDAO.saveAll(nouveauxEntraineurs)
//    sauvegardes.forEach { println("Ajouté : $it") }
//
//    // 8️⃣ Vérification globale
//    println("\n📋 Liste après toutes les insertions :")
//    val listeApresInsert = entraineurDAO.findAll()
//    listeApresInsert.forEach { println(it) }
//
//    // 9️⃣ Suppression des ajouts pour nettoyage
//    println("\n🧹 Test deleteById() : suppression des entraîneurs ajoutés")
//    val aSupprimer = mutableListOf<Entraineur>()
//    pierreSauvegarde?.let { aSupprimer.add(it) }
//    aSupprimer.addAll(sauvegardes)
//
//    for (e in aSupprimer) {
//        if (entraineurDAO.deleteById(e.id)) {
//            println("Supprimé : ${e.nom} (id=${e.id})")
//        } else {
//            println("Échec suppression : ${e.nom} (id=${e.id})")
//        }
//    }
//
//    // 🔟 Liste finale
//    println("\n📋 Liste finale après nettoyage :")
//    entraineurDAO.findAll().forEach { println(it) }



// TEST DU DAO EspeceMonstre

//    println("=== 🧪 TEST DU DAO EspeceMonstre ===")
//    val especeDAO = EspeceMonstreDAO(db)
//    // 1️⃣ - Test findAll (lecture)
//    println("\n📘 Toutes les espèces existantes :")
//    val allEspeces = especeDAO.findAll()
//    allEspeces.forEach { println(it) }

//    // 2️⃣ - Test save (insertion)
//    println("\n💾 Insertion d'une nouvelle espèce :")
//    val especeTest = EspeceMonstre(
//        id = 0,
//        nom = "Testomon",
//        type = "Fantôme",
//        baseAttaque = 12,
//        baseDefense = 9,
//        baseVitesse = 15,
//        baseAttaqueSpe = 17,
//        baseDefenseSpe = 8,
//        basePv = 55,
//        modAttaque = 9.0,
//        modDefense = 8.5,
//        modVitesse = 10.0,
//        modAttaqueSpe = 11.5,
//        modDefenseSpe = 7.0,
//        modPv = 24.0,
//        description = "Un monstre de test qui hante la base de données.",
//        particularites = "Sert uniquement pour les tests.",
//        caracteres = "Calme, loyal, invisible"
//    )
//    val savedEspece = especeDAO.save(especeTest)
//    println("✅ Espèce insérée : $savedEspece")

//    // 3️⃣ - Test findById
//    println("\n🔍 Recherche par ID 8 ")
//    val foundById = especeDAO.findById(8)
//    println(foundById)
//
//    // 4️⃣ Recherche par nom
//    println("\n🔎 Test findByNom('galum') :")
//    val espTestomon = especeDAO.findByNom("Testomon")
//    espTestomon.forEach { println(it) }

//    // 5️⃣   Test update (modification)
//    val savedEspece = especeMonstreDAO.findById(14) // par exemple id = 3
//    if (savedEspece != null) {
//        println("\n✏️ Mise à jour de l'espèce (${savedEspece.nom}) :")
//        savedEspece.nom = "Testomon Évolué"
//        especeDAO.save(savedEspece)
//        val updated = especeDAO.findById(savedEspece.id)
//        println("✅ Espèce mise à jour : $updated")
//    }

//    // 5️⃣ - Test saveAll (insertion multiple)
//    println("\n📦 Insertion multiple (saveAll) :")
//    val espece1 = EspeceMonstre(0, "MultiA", "Feu", 10, 9, 11, 12, 8, 50, 8.0, 9.0, 8.5, 10.0, 9.0, 23.0, "Premier test multiple", "Brille au soleil", "Joyeux")
//    val espece2 = EspeceMonstre(0, "MultiB", "Eau", 11, 10, 10, 13, 9, 53, 9.5, 8.0, 8.0, 11.0, 8.5, 25.0, "Deuxième test multiple", "Aime la pluie", "Calme")
//    val savedList = especeDAO.saveAll(listOf(espece1, espece2))
//    println("✅ Espèces insérées :")
//    savedList.forEach { println(it) }

//    // 6️⃣ - Test deleteById
//    val savedEspece = especeMonstreDAO.findById(15) // par exemple id = 3
//    if (savedEspece != null) {
//        println("\n🗑️ Suppression de l'espèce ${savedEspece.nom} (id=${savedEspece.id})")
//        val deleted = especeDAO.deleteById(savedEspece.id)
//        println("✅ Suppression réussie : $deleted")
//    }
//
//    println("\n=== ✅ FIN DES TESTS EspeceMonstreDAO ===")


















// TEST DU DAO IndividuMonstreDAO

//println("=== 🧪 TEST DU DAO IndividuMonstreDAO ===")
//val individuMonstreDAO = IndividuMonstreDAO(db)
//val especeDAO = EspeceMonstreDAO(db)
// --- Récupérer une espèce pour la FK ---
//val listeEspeces = especeDAO.findAll()
//if (listeEspeces.isEmpty()) {
//    println("Pas d'espèces en base. Ajoutez-en avant de tester.")
//    db.close()
//    return
//}
//val espece = listeEspeces.first()
//
//    // --- CREATE / INSERT ---
//    val nouvelIndividu = IndividuMonstreEntity(
//        id = 0,
//        nom = "Testomon",
//        niveau = 1,
//        attaque = 10,
//        defense = 8,
//        vitesse = 12,
//        attaqueSpe = 9,
//        defenseSpe = 7,
//        pvMax = 50,
//        potentiel = 1.2,
//        exp = 0.0,
//        pv = 50,
//        especeId = espece.id,
//        entraineurEquipeId = null,
//        entraineurBoiteId = null
//    )
//
//    val insere = dao.save(nouvelIndividu)
//    println("Inséré : $insere")
//
// --- READ ALL ---
//val all = individuMonstreDAO.findAll()
//println("Tous les individus : $all")
//
// --- READ BY ID ---
//val byId = individuMonstreDAO.findById(6)
//println("Recherche par ID : $byId")
//
// --- READ BY NOM ---
//val byNom = individuMonstreDAO.findByNom("Aquamy_Bob")
//println("Recherche par nom : $byNom")
//
//    // --- UPDATE ---
//    val savedEspece = individuMonstreDAO.findById(6)
//    if (savedEspece != null){
//        savedEspece.nom = "TestomonUpdated"
//        val updated = individuMonstreDAO.save(savedEspece)
//        println("Mise à jour : $updated")
//    }
//
//    // --- SAVE ALL ---
//    val listeIndividus = listOf(
//        IndividuMonstreEntity(0, "MonoRock1", 1, 10, 10, 10, 10, 10, 50, 1.0, 0.0, 50, espece.id, null, null),
//        IndividuMonstreEntity(0, "MonoRock2", 1, 11, 11, 11, 11, 11, 55, 1.1, 0.0, 55, espece.id, null, null)
//    )
//    val sauvegardeMultiple = individuMonstreDAO.saveAll(listeIndividus)
//    println("SaveAll : $sauvegardeMultiple")
//
// --- DELETE ---
//val supprime = individuMonstreDAO.deleteById(8)
//println("Suppression réussie ? $supprime")




