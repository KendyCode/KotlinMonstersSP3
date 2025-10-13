package DAO

import db
import entraineurDAO
import especeMonstreDAO
import jdbc.BDD
import monstre.EspeceMonstre
import monstre.IndividuMonstre

import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Statement

/**
 * DAO (Data Access Object) permettant d'interagir avec la table `Entraineurs`.
 *
 * Cette classe gère les opérations CRUD :
 * - 🔍 Lecture (findAll, findById, findByNom)
 * - 💾 Sauvegarde (save, saveAll)
 * - ❌ Suppression (deleteById)
 *
 * @param bdd L'objet de connexion à la base de données.
 */
class IndividuMonstreDAO(val bdd: BDD) {
    val entraineurDao =entraineurDAO
    val especeDao = especeMonstreDAO

    /**
     * Récupère toutes les espèces présentes dans la base de données.
     *
     * @return Une liste mutable d'entraîneurs trouvés.
     */
    fun findAll(): MutableList<IndividuMonstre> {
        val result = mutableListOf<IndividuMonstre>()
        val sql = "SELECT * FROM IndividuMonstre"
        val requetePreparer = bdd.connectionBDD!!.prepareStatement(sql)
        val resultatRequete = bdd.executePreparedStatement(requetePreparer)
        if (resultatRequete != null) {
            while (resultatRequete.next()) {
                val id = resultatRequete.getInt("id")
                val nom = resultatRequete.getString("nom")
                val idEspece = resultatRequete.getInt("fk_espece_id")
                val idEntraineur = resultatRequete.getInt("fk_entraineur_equipe_id")
                val niveau = resultatRequete.getInt("niveau")
                val attaque = resultatRequete.getInt("attaque")
                val defense = resultatRequete.getInt("defense")
                val vitesse = resultatRequete.getInt("vitesse")
                val attaqueSpe = resultatRequete.getInt("attaqueSpe")
                val defenseSpe = resultatRequete.getInt("defenseSpe")
                val pvMax = resultatRequete.getInt("pvMax")
                val potentiel = resultatRequete.getDouble("potentiel")
                val exp = resultatRequete.getDouble("exp")
                val pv = resultatRequete.getInt("pv")

                val espece = especeDao.findById(idEspece)!!
                val entraineur = entraineurDao.findById((idEntraineur))!!
                result.add(IndividuMonstre(id, nom, espece,entraineur,0.0))
            }
        }

        requetePreparer.close()
        return result
    }


    /**
     * Recherche une espece par son identifiant unique.
     *
     * @param id L'identifiant de l'espece.
     * @return L'espece trouvé ou `null` si aucun résultat.
     */
    fun findById(id: Int): IndividuMonstre? {
        var result: IndividuMonstre? = null
        val sql = "SELECT * FROM EspeceMonstre WHERE id = ?"
        val requetePreparer = bdd.connectionBDD!!.prepareStatement(sql)
        requetePreparer.setInt(1, id) // insere la valeur de l'id dans la requete preparer
        val resultatRequete = bdd.executePreparedStatement(requetePreparer)

        if (resultatRequete != null && resultatRequete.next()) {
            val id = resultatRequete.getInt("id")
            val nom = resultatRequete.getString("nom")
            val idEspece = resultatRequete.getInt("fk_espece_id")
            val idEntraineur = resultatRequete.getInt("fk_entraineur_equipe_id")
            val niveau = resultatRequete.getInt("niveau")
            val attaque = resultatRequete.getInt("attaque")
            val defense = resultatRequete.getInt("defense")
            val vitesse = resultatRequete.getInt("vitesse")
            val attaqueSpe = resultatRequete.getInt("attaqueSpe")
            val defenseSpe = resultatRequete.getInt("defenseSpe")
            val pvMax = resultatRequete.getInt("pvMax")
            val potentiel = resultatRequete.getDouble("potentiel")
            val exp = resultatRequete.getDouble("exp")
            val pv = resultatRequete.getInt("pv")

            val espece = especeDao.findById(idEspece)!!
            val entraineur = entraineurDao.findById((idEntraineur))!!
            result = IndividuMonstre(id, nom, espece, entraineur, 0.0)
        }

        requetePreparer.close()
        return result
    }


    /**
     * Recherche une espece par son nom.
     *
     * @param nomRechercher Le nom de l'espece à rechercher.
     * @return Une liste d'espece correspondant au nom donné.
     */
    fun findByNom(nomRechercher: String): MutableList<IndividuMonstre> {
        val result = mutableListOf<IndividuMonstre>()
        val sql = "SELECT * FROM IndividuMonstre WHERE nom = ?"
        val requetePreparer = bdd.connectionBDD!!.prepareStatement(sql)
        requetePreparer.setString(1, nomRechercher)
        val resultatRequete = bdd.executePreparedStatement(requetePreparer)

        if (resultatRequete != null) {
            while (resultatRequete.next()) {
                val id = resultatRequete.getInt("id")
                val nom = resultatRequete.getString("nom")
                val idEspece = resultatRequete.getInt("fk_espece_id")
                val idEntraineur = resultatRequete.getInt("fk_entraineur_equipe_id")
                val niveau = resultatRequete.getInt("niveau")
                val attaque = resultatRequete.getInt("attaque")
                val defense = resultatRequete.getInt("defense")
                val vitesse = resultatRequete.getInt("vitesse")
                val attaqueSpe = resultatRequete.getInt("attaqueSpe")
                val defenseSpe = resultatRequete.getInt("defenseSpe")
                val pvMax = resultatRequete.getInt("pvMax")
                val potentiel = resultatRequete.getDouble("potentiel")
                val exp = resultatRequete.getDouble("exp")
                val pv = resultatRequete.getInt("pv")

                val espece = especeDao.findById(idEspece)!!
                val entraineur = entraineurDao.findById(idEntraineur)!!
                val individu = IndividuMonstre(id, nom, espece, entraineur, 0.0)
                result.add(individu)
            }
        }

        requetePreparer.close()
        return result
    }

    // --- Sauvegarder un IndividuMonstre ---
    fun save(individu: IndividuMonstre): IndividuMonstre? {
        val requetePreparer: PreparedStatement

        if (individu.id == 0) {
            // Insertion
            val sql =
                "INSERT INTO IndividuMonstre (nom, espece, entraineur, niveau, attaque, defense, vitesse, attaqueSpe, defenseSpe, pvMax, potentiel, exp, pv) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
            requetePreparer = bdd.connectionBDD!!.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            requetePreparer.setString(1, individu.nom)
            requetePreparer.setInt(2, individu.espece.id)
            requetePreparer.setInt(3, individu.entraineur!!.id)
            requetePreparer.setInt(4, individu.niveau)
            requetePreparer.setInt(5, individu.attaque)
            requetePreparer.setInt(6, individu.defense)
            requetePreparer.setInt(7, individu.vitesse)
            requetePreparer.setInt(8, individu.attaqueSpe)
            requetePreparer.setInt(9, individu.defenseSpe)
            requetePreparer.setInt(10, individu.pvMax)
            requetePreparer.setDouble(11, individu.potentiel)
            requetePreparer.setDouble(12, individu.exp)
            requetePreparer.setInt(13, individu.pv)
        } else {
            // Mise à jour
            val sql =
                "UPDATE IndividuMonstre SET nom=?, espece=?, entraineur=?, niveau=?, attaque=?, defense=?, vitesse=?, attaqueSpe=?, defenseSpe=?, pvMax=?, potentiel=?, exp=?, pv=? WHERE id=?"
            requetePreparer = bdd.connectionBDD!!.prepareStatement(sql)
            requetePreparer.setString(1, individu.nom)
            requetePreparer.setInt(2, individu.espece.id)
            requetePreparer.setInt(3, individu.entraineur!!.id)
            requetePreparer.setInt(4, individu.niveau)
            requetePreparer.setInt(5, individu.attaque)
            requetePreparer.setInt(6, individu.defense)
            requetePreparer.setInt(7, individu.vitesse)
            requetePreparer.setInt(8, individu.attaqueSpe)
            requetePreparer.setInt(9, individu.defenseSpe)
            requetePreparer.setInt(10, individu.pvMax)
            requetePreparer.setDouble(11, individu.potentiel)
            requetePreparer.setDouble(12, individu.exp)
            requetePreparer.setInt(13, individu.pv)
            requetePreparer.setInt(14, individu.id)
        }

        val nbLigneMaj = requetePreparer.executeUpdate()

        if (nbLigneMaj > 0) {
            val generatedKeys = requetePreparer.generatedKeys
            if (generatedKeys.next()) {
                individu.id = generatedKeys.getInt(1)
            }
            requetePreparer.close()
            return individu
        }

        requetePreparer.close()
        return null
    }

    fun deleteById(id: Int): Boolean {
        val sql = "DELETE FROM IndividuMonstre WHERE id = ?"
        val requetePreparer = bdd.connectionBDD!!.prepareStatement(sql)
        requetePreparer.setInt(1, id)

        return try {
            val nbLigneMaj = requetePreparer.executeUpdate()
            requetePreparer.close()
            nbLigneMaj > 0
        } catch (erreur: SQLException) {
            println("Erreur lors de la suppression de l'individu : ${erreur.message}")
            false
        }
    }

    fun saveAll(individus: Collection<IndividuMonstre>): MutableList<IndividuMonstre> {
        val result = mutableListOf<IndividuMonstre>()
        for (e in individus) {
            val sauvegarde = save(e)  // Ici il voit bien save() car au même niveau
            if (sauvegarde != null) result.add(sauvegarde)
        }
        return result
    }
}