package DAO

import db
import jdbc.BDD
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

class EntraineurDAO(val bdd: BDD = db) {
    /**
     * Récupère tous les entraîneurs présents dans la base de données.
     *
     * @return Une liste mutable d'entraîneurs trouvés.
     */
    fun findAll(): MutableList<Entraineur> {
        val result = mutableListOf<Entraineur>()
        val sql = "SELECT * FROM Entraineurs"
        val requetePreparer = bdd.connectionBDD!!.prepareStatement(sql)
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
     * Recherche un entraîneur par son identifiant unique.
     *
     * @param id L'identifiant de l'entraîneur.
     * @return L'entraîneur trouvé ou `null` si aucun résultat.
     */
    fun findById(id: Int): Entraineur? {
        var result: Entraineur? = null
        val sql = "SELECT * FROM Entraineurs WHERE id = ?"
        val requetePreparer = bdd.connectionBDD!!.prepareStatement(sql)
        requetePreparer.setInt(1, id) // insere la valeur de l'id dans la requete preparer
        val resultatRequete = bdd.executePreparedStatement(requetePreparer)

        if (resultatRequete != null && resultatRequete.next()) {
            val nom = resultatRequete.getString("nom")
            val argents = resultatRequete.getInt("argents")
            result = Entraineur(id, nom, argents)
        }

        requetePreparer.close()
        return result
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


}

