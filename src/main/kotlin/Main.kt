
import item.Badge
import item.MonsterKube
import monde.Zone
import org.example.dresseur.Entraineur
import monstre.EspeceMonstre
import monstre.IndividuMonstre
import jeu.Partie
import jdbc.BDD

val db = BDD()


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

    route1.zoneSuivante = route2
    route2.zonePrecedente = route1
    joueur.sacAItems.add(objet1)

    val partie = nouvellePartie()
    partie.choixStarter()
    db.close()

    partie.jouer()

}

fun nouvellePartie():Partie{
    println("Bienvenue dans le monde magique des Pokémon! Mon nom est ChenZen! Les gens souvent m'appellent le Prof Pokémon! Ce monde est peuplé de créatures du nom de Pokémon!")
    println("Rentrez votre nom : ")
    val nomJoueur = readln()
    val PartieJoueur = Partie(1,joueur,route1)
    return PartieJoueur
}

