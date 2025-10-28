package DAO

import db
import jdbc.BDD
import monstre.IndividuMonstre
import org.example.dresseur.Entraineur
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

class EntraineurDAO(val bdd: BDD = db,
                    private val individuMonstreDAO: IndividuMonstreDAO = IndividuMonstreDAO(bdd)) {
    /**
     * Récupère tous les entraîneurs présents dans la base de données.
     *
     * @return Une liste mutable d'entraîneurs trouvés.
     */
    fun findAll(): MutableList<Entraineur> {
        val result = mutableListOf<Entraineur>()
        val sql = "SELECT * FROM Entraineurs"
        val requete = bdd.connectionBDD!!.prepareStatement(sql)
        val rs = bdd.executePreparedStatement(requete)

        if (rs != null) {
            while (rs.next()) {
                val entraineur = Entraineur(
                    id = rs.getInt("id"),
                    nom = rs.getString("nom"),
                    argents = rs.getInt("argents")
                )

                // 🧠 On charge les monstres liés
                entraineur.equipeMonstre = findMonstresByEntraineurId(entraineur.id, isEquipe = true)
                entraineur.boiteMonstre = findMonstresByEntraineurId(entraineur.id, isEquipe = false)

                result.add(entraineur)
            }
        }

        requete.close()
        return result
    }

    /**
     * Recherche un entraîneur par son identifiant unique.
     *
     * @param id L'identifiant de l'entraîneur.
     * @return L'entraîneur trouvé ou `null` si aucun résultat.
     */
    fun findById(id: Int): Entraineur? {
        val sql = "SELECT * FROM Entraineurs WHERE id = ?"
        val requete = bdd.connectionBDD!!.prepareStatement(sql)
        requete.setInt(1, id)
        val rs = bdd.executePreparedStatement(requete)

        var entraineur: Entraineur? = null
        if (rs != null && rs.next()) {
            entraineur = Entraineur(
                id = id,
                nom = rs.getString("nom"),
                argents = rs.getInt("argents")
            )

            entraineur.equipeMonstre = findMonstresByEntraineurId(id, isEquipe = true)
            entraineur.boiteMonstre = findMonstresByEntraineurId(id, isEquipe = false)
        }

        requete.close()
        return entraineur
    }

    fun findByIdLight(id: Int): Entraineur? {
        val sql = "SELECT id, nom, argents FROM Entraineurs WHERE id = ?"
        val requete = bdd.connectionBDD!!.prepareStatement(sql)
        requete.setInt(1, id)
        val rs = bdd.executePreparedStatement(requete)

        var entraineur: Entraineur? = null
        if (rs != null && rs.next()) {
            entraineur = Entraineur(
                id = rs.getInt("id"),
                nom = rs.getString("nom"),
                argents = rs.getInt("argents")
            )
        }

        requete.close()
        return entraineur
    }


    /**
     * Recherche un entraîneur par son nom.
     *
     * @param nomRechercher Le nom de l'entraîneur à rechercher.
     * @return Une liste d'entraîneurs correspondant au nom donné.
     */
    fun findByNom(nomRechercher: String): MutableList<Entraineur> {
        val result = mutableListOf<Entraineur>()
        val sql = "SELECT * FROM Entraineurs WHERE nom = ?"
        val requetePreparer = bdd.connectionBDD!!.prepareStatement(sql)
        requetePreparer.setString(1, nomRechercher)
        val resultatRequete = bdd.executePreparedStatement(requetePreparer)

        if (resultatRequete != null) {
            while (resultatRequete.next()) {
                val id = resultatRequete.getInt("id")
                val nom = resultatRequete.getString("nom")
                val argents = resultatRequete.getInt("argents")
                result.add(Entraineur(id, nom, argents))
            }
        }

        requetePreparer.close()
        return result
    }

    /**
     * Insère ou met à jour un entraîneur dans la base.
     *
     * @param entraineur L'entraîneur à sauvegarder.
     * @return L'entraîneur sauvegardé avec son ID mis à jour si insertion.
     */
    fun save(entraineur: Entraineur): Entraineur? {
        val requetePreparer: PreparedStatement
        val isInsert = entraineur.id == 0

        if (isInsert) {
            // 🟢 Insertion
            val sql = "INSERT INTO Entraineurs (nom, argents) VALUES (?, ?)"
            requetePreparer = bdd.connectionBDD!!.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            requetePreparer.setString(1, entraineur.nom)
            requetePreparer.setInt(2, entraineur.argents)

        } else {
            // 🟡 Mise à jour
            val sql = "UPDATE Entraineurs SET nom = ?, argents = ? WHERE id = ?"
            requetePreparer = bdd.connectionBDD!!.prepareStatement(sql)
            requetePreparer.setString(1, entraineur.nom)
            requetePreparer.setInt(2, entraineur.argents)

            requetePreparer.setInt(3, entraineur.id)
        }

        val nbLigneMaj = requetePreparer.executeUpdate()

        if (nbLigneMaj > 0) {
            // 🔑 Récupération de la clé générée uniquement si insertion
            if (isInsert) {
                val generatedKeys = requetePreparer.generatedKeys
                if (generatedKeys.next()) {
                    entraineur.id = generatedKeys.getInt(1)
                }
                generatedKeys.close()
            }
            requetePreparer.close()
            return entraineur
        }

        requetePreparer.close()
        return null
    }

    /**
    * Supprime un entraîneur par son identifiant.
    *
    * @param id L'ID de l'entraîneur à supprimer.
    * @return `true` si la suppression a réussi, sinon `false`.
    */
    fun deleteById(id: Int): Boolean {
        val sql = "DELETE FROM Entraineurs WHERE id = ?"
        val requetePreparer = bdd.connectionBDD!!.prepareStatement(sql)
        requetePreparer.setInt(1, id)

        return try {
            val nbLigneMaj = requetePreparer.executeUpdate()
            requetePreparer.close()
            nbLigneMaj > 0
        } catch (erreur: SQLException) {
            println("Erreur lors de la suppression de l'entraîneur : ${erreur.message}")
            false
        }
    }

    /**
     * Sauvegarde plusieurs entraîneurs dans la base de données.
     *
     * @param entraineurs Liste d'entraîneurs à sauvegarder.
     * @return Liste des entraîneurs sauvegardés.
     */
    fun saveAll(entraineurs: Collection<Entraineur>): MutableList<Entraineur> {
        val result = mutableListOf<Entraineur>()
        for (e in entraineurs) {
            val sauvegarde = save(e)
            if (sauvegarde != null) result.add(sauvegarde)
        }
        return result
    }

    /**
     * Récupère tous les individus de monstres appartenant à un entraîneur.
     * (équipe ou boîte selon le paramètre)
     */
    private fun findMonstresByEntraineurId(idEntraineur: Int, isEquipe: Boolean): MutableList<IndividuMonstre> {
        val monstres = mutableListOf<IndividuMonstre>()
        val sql = if (isEquipe)
            "SELECT * FROM IndividuMonstre WHERE entraineur_equipe_id = ?"
        else
            "SELECT * FROM IndividuMonstre WHERE entraineur_boite_id = ?"

        val requete = bdd.connectionBDD!!.prepareStatement(sql)
        requete.setInt(1, idEntraineur)
        val rs = bdd.executePreparedStatement(requete)

        if (rs != null) {
            while (rs.next()) {
                val entity = individuMonstreDAO.findById(rs.getInt("id"))
                val monstre = entity?.let { individuMonstreDAO.toModel(it) }
                if (monstre != null) monstres.add(monstre)
            }
        }

        requete.close()
        return monstres
    }


}

