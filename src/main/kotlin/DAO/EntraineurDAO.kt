package DAO

import db
import jdbc.BDD
import monstre.IndividuMonstre
import org.example.dresseur.Entraineur
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Statement

/**
 * DAO (Data Access Object) pour la table `Entraineurs`.
 *
 * Cette classe encapsule toutes les opérations nécessaires pour interagir avec
 * la base de données concernant les entraîneurs.
 *
 * Elle fournit des méthodes CRUD complètes :
 * - 🔍 Lecture : findAll(), findById(), findByNom(), findByIdLight()
 * - 💾 Sauvegarde : save(), saveAll()
 * - ❌ Suppression : deleteById()
 *
 * Elle utilise un objet [BDD] pour gérer la connexion et l'exécution des requêtes SQL.
 *
 * @param bdd Objet de connexion à la base de données.
 * @param individuMonstreDAO DAO associé pour récupérer les monstres liés à un entraîneur.
 */

class EntraineurDAO(val bdd: BDD = db,
                    private val individuMonstreDAO: IndividuMonstreDAO = IndividuMonstreDAO(bdd)) {
    /**
     * Récupère tous les entraîneurs de la base de données.
     *
     * Pour chaque entraîneur trouvé :
     * 1. Instancie un objet [Entraineur].
     * 2. Charge les monstres de l'équipe et de la boîte via [findMonstresByEntraineurId].
     *
     * @return Liste mutable contenant tous les entraîneurs trouvés.
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
     * Recherche un entraîneur par son identifiant.
     *
     * @param id Identifiant unique de l'entraîneur.
     * @return Objet [Entraineur] ou `null` si non trouvé.
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
            // Récupération des monstres associés
            entraineur.equipeMonstre = findMonstresByEntraineurId(id, isEquipe = true)
            entraineur.boiteMonstre = findMonstresByEntraineurId(id, isEquipe = false)
        }

        requete.close()
        return entraineur
    }

    /**
     * Version "light" de findById, ne charge que les informations essentielles.
     * Utile pour afficher des listes ou éviter de charger tous les monstres.
     */
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
     * Recherche un ou plusieurs entraîneurs par nom exact.
     *
     * @param nomRechercher Nom de l'entraîneur recherché.
     * @return Liste mutable contenant les entraîneurs correspondants.
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
     * Sauvegarde un entraîneur dans la base.
     *
     * Si `entraineur.id == 0`, il s'agit d'une insertion.
     * Sinon, il s'agit d'une mise à jour.
     *
     * @param entraineur Entraîneur à sauvegarder.
     * @return L'entraîneur avec l'ID mis à jour (pour insertion) ou null en cas d'échec.
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
     * Supprime un entraîneur via son identifiant.
     *
     * @param id ID de l'entraîneur à supprimer.
     * @return `true` si la suppression a réussi, `false` sinon.
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
     * Sauvegarde une collection d'entraîneurs.
     *
     * @param entraineurs Liste d'entraîneurs à sauvegarder.
     * @return Liste des entraîneurs ayant été sauvegardés avec succès.
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
     * Récupère tous les monstres liés à un entraîneur.
     *
     * @param idEntraineur ID de l'entraîneur.
     * @param isEquipe True → récupère l’équipe active ; False → récupère la boîte.
     * @return Liste mutable des monstres correspondants.
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

