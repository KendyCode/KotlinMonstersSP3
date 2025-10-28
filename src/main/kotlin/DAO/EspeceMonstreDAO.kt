package DAO

import db
import jdbc.BDD
import monstre.EspeceMonstre

import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Statement

/**
 * DAO (Data Access Object) pour la table `EspecesMonstre`.
 *
 * Fournit une interface pour effectuer des opérations CRUD (Create, Read, Update, Delete) sur la table des espèces de monstres.
 *
 * ⚡ Fonctionnalités principales :
 * - 🔍 Lecture : findAll(), findById(), findByNom()
 * - 💾 Sauvegarde : save(), saveAll()
 * - ❌ Suppression : deleteById()
 *
 * Utilise un objet [BDD] pour gérer la connexion et l'exécution des requêtes SQL.
 *
 * @param bdd Objet de connexion à la base de données (par défaut `db` global).
 */
class EspeceMonstreDAO(val bdd: BDD = db) {

    /**
     * Récupère toutes les espèces de monstres présentes dans la base.
     *
     * Pour chaque ligne de la table `EspecesMonstre` :
     * - Instancie un objet [EspeceMonstre].
     * - Remplit tous les attributs de base, modificateurs et descriptions.
     *
     * @return Liste mutable de toutes les espèces de monstres.
     */
    fun findAll(): MutableList<EspeceMonstre> {
        val result = mutableListOf<EspeceMonstre>()
        val sql = "SELECT * FROM EspecesMonstre"
        val requetePreparer = bdd.connectionBDD!!.prepareStatement(sql)
        val resultatRequete = bdd.executePreparedStatement(requetePreparer)

        if (resultatRequete != null) {
            while (resultatRequete.next()) {
                result.add(
                    EspeceMonstre(
                        id = resultatRequete.getInt("id"),
                        nom = resultatRequete.getString("nom"),
                        type = resultatRequete.getString("type"),

                        baseAttaque = resultatRequete.getInt("baseAttaque"),
                        baseDefense = resultatRequete.getInt("baseDefense"),
                        baseVitesse = resultatRequete.getInt("baseVitesse"),
                        baseAttaqueSpe = resultatRequete.getInt("baseAttaqueSpe"),
                        baseDefenseSpe = resultatRequete.getInt("baseDefenseSpe"),
                        basePv = resultatRequete.getInt("basePv"),

                        modAttaque = resultatRequete.getDouble("modAttaque"),
                        modDefense = resultatRequete.getDouble("modDefense"),
                        modVitesse = resultatRequete.getDouble("modVitesse"),
                        modAttaqueSpe = resultatRequete.getDouble("modAttaqueSpe"),
                        modDefenseSpe = resultatRequete.getDouble("modDefenseSpe"),
                        modPv = resultatRequete.getDouble("modPv"),

                        description = resultatRequete.getString("description"),
                        particularites = resultatRequete.getString("particularites"),
                        caracteres = resultatRequete.getString("caracteres")
                    )
                )
            }
        }

        requetePreparer.close()
        return result
    }

    /**
     * Recherche une espèce de monstre par son identifiant unique.
     *
     * @param id Identifiant unique de l'espèce.
     * @return L'objet [EspeceMonstre] correspondant ou `null` si aucun résultat.
     */
    fun findById(id: Int): EspeceMonstre? {
        val sql = "SELECT * FROM EspecesMonstre WHERE id = ?"
        val requetePreparer = bdd.connectionBDD!!.prepareStatement(sql)
        requetePreparer.setInt(1, id)
        val resultatRequete = bdd.executePreparedStatement(requetePreparer)

        var espece: EspeceMonstre? = null
        if (resultatRequete != null && resultatRequete.next()) {
            espece = EspeceMonstre(
                id = id,
                nom = resultatRequete.getString("nom"),
                type = resultatRequete.getString("type"),

                baseAttaque = resultatRequete.getInt("baseAttaque"),
                baseDefense = resultatRequete.getInt("baseDefense"),
                baseVitesse = resultatRequete.getInt("baseVitesse"),
                baseAttaqueSpe = resultatRequete.getInt("baseAttaqueSpe"),
                baseDefenseSpe = resultatRequete.getInt("baseDefenseSpe"),
                basePv = resultatRequete.getInt("basePv"),

                modAttaque = resultatRequete.getDouble("modAttaque"),
                modDefense = resultatRequete.getDouble("modDefense"),
                modVitesse = resultatRequete.getDouble("modVitesse"),
                modAttaqueSpe = resultatRequete.getDouble("modAttaqueSpe"),
                modDefenseSpe = resultatRequete.getDouble("modDefenseSpe"),
                modPv = resultatRequete.getDouble("modPv"),

                description = resultatRequete.getString("description"),
                particularites = resultatRequete.getString("particularites"),
                caracteres = resultatRequete.getString("caracteres")
            )
        }

        requetePreparer.close()
        return espece
    }

    /**
     * Recherche les espèces de monstres par leur nom exact.
     *
     * @param nomRechercher Nom de l'espèce à rechercher.
     * @return Liste mutable d'espèces correspondant au nom fourni.
     */
    fun findByNom(nomRechercher: String): MutableList<EspeceMonstre> {
        val result = mutableListOf<EspeceMonstre>()
        val sql = "SELECT * FROM EspecesMonstre WHERE nom = ?"
        val requetePreparer = bdd.connectionBDD!!.prepareStatement(sql)
        requetePreparer.setString(1, nomRechercher)
        val resultatRequete = bdd.executePreparedStatement(requetePreparer)

        if (resultatRequete != null) {
            while (resultatRequete.next()) {
                result.add(
                    EspeceMonstre(
                        id = resultatRequete.getInt("id"),
                        nom = resultatRequete.getString("nom"),
                        type = resultatRequete.getString("type"),

                        baseAttaque = resultatRequete.getInt("baseAttaque"),
                        baseDefense = resultatRequete.getInt("baseDefense"),
                        baseVitesse = resultatRequete.getInt("baseVitesse"),
                        baseAttaqueSpe = resultatRequete.getInt("baseAttaqueSpe"),
                        baseDefenseSpe = resultatRequete.getInt("baseDefenseSpe"),
                        basePv = resultatRequete.getInt("basePv"),

                        modAttaque = resultatRequete.getDouble("modAttaque"),
                        modDefense = resultatRequete.getDouble("modDefense"),
                        modVitesse = resultatRequete.getDouble("modVitesse"),
                        modAttaqueSpe = resultatRequete.getDouble("modAttaqueSpe"),
                        modDefenseSpe = resultatRequete.getDouble("modDefenseSpe"),
                        modPv = resultatRequete.getDouble("modPv"),

                        description = resultatRequete.getString("description"),
                        particularites = resultatRequete.getString("particularites"),
                        caracteres = resultatRequete.getString("caracteres")
                    )
                )
            }
        }

        requetePreparer.close()
        return result
    }

    /**
     * Sauvegarde une espèce de monstre dans la base.
     *
     * - Si `espece.id == 0`, une nouvelle entrée est insérée et l'ID est récupéré.
     * - Sinon, la ligne existante est mise à jour.
     *
     * @param espece Espèce à sauvegarder.
     * @return L'objet sauvegardé avec son ID, ou `null` en cas d'échec.
     */
    fun save(espece: EspeceMonstre): EspeceMonstre? {
        if (espece.id == 0) {
            // 🆕 Insertion
            val sql = """
            INSERT INTO EspecesMonstre 
            (nom, type, baseAttaque, baseDefense, baseVitesse, baseAttaqueSpe, baseDefenseSpe, basePv,
             modAttaque, modDefense, modVitesse, modAttaqueSpe, modDefenseSpe, modPv,
             description, particularites, caracteres)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

            val requetePreparer = bdd.connectionBDD!!.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            requetePreparer.setString(1, espece.nom)
            requetePreparer.setString(2, espece.type)
            requetePreparer.setInt(3, espece.baseAttaque)
            requetePreparer.setInt(4, espece.baseDefense)
            requetePreparer.setInt(5, espece.baseVitesse)
            requetePreparer.setInt(6, espece.baseAttaqueSpe)
            requetePreparer.setInt(7, espece.baseDefenseSpe)
            requetePreparer.setInt(8, espece.basePv)
            requetePreparer.setDouble(9, espece.modAttaque)
            requetePreparer.setDouble(10, espece.modDefense)
            requetePreparer.setDouble(11, espece.modVitesse)
            requetePreparer.setDouble(12, espece.modAttaqueSpe)
            requetePreparer.setDouble(13, espece.modDefenseSpe)
            requetePreparer.setDouble(14, espece.modPv)
            requetePreparer.setString(15, espece.description)
            requetePreparer.setString(16, espece.particularites)
            requetePreparer.setString(17, espece.caracteres)

            val nbLigneMaj = requetePreparer.executeUpdate()
            if (nbLigneMaj > 0) {
                val generatedKeys = requetePreparer.generatedKeys
                if (generatedKeys.next()) {
                    espece.id = generatedKeys.getInt(1)
                }
            }
            requetePreparer.close()
            return espece
        } else {
            // 🔄 Mise à jour
            val sql = """
            UPDATE EspecesMonstre SET 
                nom=?, type=?, baseAttaque=?, baseDefense=?, baseVitesse=?, baseAttaqueSpe=?, baseDefenseSpe=?, basePv=?,
                modAttaque=?, modDefense=?, modVitesse=?, modAttaqueSpe=?, modDefenseSpe=?, modPv=?,
                description=?, particularites=?, caracteres=?
            WHERE id=?
        """.trimIndent()

            val requetePreparer = bdd.connectionBDD!!.prepareStatement(sql)
            requetePreparer.setString(1, espece.nom)
            requetePreparer.setString(2, espece.type)
            requetePreparer.setInt(3, espece.baseAttaque)
            requetePreparer.setInt(4, espece.baseDefense)
            requetePreparer.setInt(5, espece.baseVitesse)
            requetePreparer.setInt(6, espece.baseAttaqueSpe)
            requetePreparer.setInt(7, espece.baseDefenseSpe)
            requetePreparer.setInt(8, espece.basePv)
            requetePreparer.setDouble(9, espece.modAttaque)
            requetePreparer.setDouble(10, espece.modDefense)
            requetePreparer.setDouble(11, espece.modVitesse)
            requetePreparer.setDouble(12, espece.modAttaqueSpe)
            requetePreparer.setDouble(13, espece.modDefenseSpe)
            requetePreparer.setDouble(14, espece.modPv)
            requetePreparer.setString(15, espece.description)
            requetePreparer.setString(16, espece.particularites)
            requetePreparer.setString(17, espece.caracteres)
            requetePreparer.setInt(18, espece.id)

            val nbLigneMaj = requetePreparer.executeUpdate()
            requetePreparer.close()

            return if (nbLigneMaj > 0) espece else null
        }
    }

    /**
     * Supprime une espèce par son ID.
     *
     * @param id ID de l'espèce à supprimer.
     * @return `true` si la suppression a réussi, sinon `false`.
     */
    fun deleteById(id: Int): Boolean {
        val sql = "DELETE FROM EspecesMonstre WHERE id = ?"
        val requetePreparer = bdd.connectionBDD!!.prepareStatement(sql)
        requetePreparer.setInt(1, id)

        return try {
            val nbLigneMaj = requetePreparer.executeUpdate()
            requetePreparer.close()
            nbLigneMaj > 0
        } catch (erreur: SQLException) {
            println("Erreur lors de la suppression de l’espèce : ${erreur.message}")
            false
        }
    }

    /**
     * Sauvegarde une collection d'espèces de monstres.
     *
     * @param especes Collection des espèces à sauvegarder.
     * @return Liste des espèces sauvegardées avec succès.
     */
    fun saveAll(especes: Collection<EspeceMonstre>): MutableList<EspeceMonstre> {
        val result = mutableListOf<EspeceMonstre>()
        for (e in especes) {
            val sauvegarde = save(e)
            if (sauvegarde != null) result.add(sauvegarde)
        }
        return result
    }

    /**
     * Utilitaire pour transformer un ResultSet SQL en objet [EspeceMonstre].
     *
     * Permet de centraliser la logique de mapping et d’éviter les répétitions.
     */
    fun mapResultSetToEspece(rs: java.sql.ResultSet): EspeceMonstre {
        return EspeceMonstre(
            id = rs.getInt("id"),
            nom = rs.getString("nom"),
            type = rs.getString("type"),
            baseAttaque = rs.getInt("baseAttaque"),
            baseDefense = rs.getInt("baseDefense"),
            baseVitesse = rs.getInt("baseVitesse"),
            baseAttaqueSpe = rs.getInt("baseAttaqueSpe"),
            baseDefenseSpe = rs.getInt("baseDefenseSpe"),
            basePv = rs.getInt("basePv"),
            modAttaque = rs.getDouble("modAttaque"),
            modDefense = rs.getDouble("modDefense"),
            modVitesse = rs.getDouble("modVitesse"),
            modAttaqueSpe = rs.getDouble("modAttaqueSpe"),
            modDefenseSpe = rs.getDouble("modDefenseSpe"),
            modPv = rs.getDouble("modPv"),
            description = rs.getString("description"),
            particularites = rs.getString("particularites"),
            caracteres = rs.getString("caracteres")
        )
    }
}




