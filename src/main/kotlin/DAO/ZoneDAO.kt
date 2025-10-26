package DAO

import db
import jdbc.BDD
import monstre.EspeceMonstre
import monde.Zone
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Statement
import DAO.EspeceMonstreDAO

/**
 * DAO (Data Access Object) pour la table `zones`.
 *
 * Gère :
 * - 🔍 Lecture (findAll, findById)
 * - 💾 Sauvegarde (save, saveAll)
 * - ❌ Suppression (deleteById)
 *
 * Utilise EspeceMonstreDAO pour reconstruire les espèces de chaque zone.
 */
class ZoneDAO(
    val bdd: BDD = db,
    private val especeMonstreDAO: EspeceMonstreDAO = EspeceMonstreDAO(bdd)
) {

    // --- FIND ALL ---
    fun findAll(): MutableList<Zone> {
        val zones = mutableListOf<Zone>()
        val sql = "SELECT * FROM zones"
        val requete = bdd.connectionBDD!!.prepareStatement(sql)
        val result = bdd.executePreparedStatement(requete)

        if (result != null) {
            while (result.next()) {
                val zone = Zone(
                    id = result.getInt("id"),
                    nom = result.getString("nom"),
                    expZone = result.getInt("expZone"),
                    especesMonstres = findEspecesByZoneId(result.getInt("id"))
                )
                zones.add(zone)
            }
        }

        requete.close()
        return zones
    }

    // --- FIND BY ID ---
    fun findById(id: Int): Zone? {
        val sql = "SELECT * FROM zones WHERE id = ?"
        val requete = bdd.connectionBDD!!.prepareStatement(sql)
        requete.setInt(1, id)
        val result = bdd.executePreparedStatement(requete)

        var zone: Zone? = null
        if (result != null && result.next()) {
            zone = Zone(
                id = result.getInt("id"),
                nom = result.getString("nom"),
                expZone = result.getInt("expZone"),
                especesMonstres = findEspecesByZoneId(id)
            )
        }

        requete.close()
        return zone
    }

    // --- TROUVER LES ESPÈCES LIÉES À UNE ZONE ---
    private fun findEspecesByZoneId(zoneId: Int): MutableList<EspeceMonstre> {
        val especes = mutableListOf<EspeceMonstre>()
        val sql = """
            SELECT e.* FROM zones_especesmonstre ze
            JOIN EspecesMonstre e ON e.id = ze.espece_id
            WHERE ze.zone_id = ?
        """.trimIndent()
        val requete = bdd.connectionBDD!!.prepareStatement(sql)
        requete.setInt(1, zoneId)
        val result = bdd.executePreparedStatement(requete)

        if (result != null) {
            while (result.next()) {
                especes.add(especeMonstreDAO.mapResultSetToEspece(result))
            }
        }

        requete.close()
        return especes
    }

    // --- SAVE (INSERT / UPDATE) ---
    fun save(zone: Zone): Zone? {
        val requete: PreparedStatement
        val isInsert = zone.id == 0

        if (isInsert) {
            val sql = "INSERT INTO zones (nom, expZone) VALUES (?, ?)"
            requete = bdd.connectionBDD!!.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            requete.setString(1, zone.nom)
            requete.setInt(2, zone.expZone)
        } else {
            val sql = "UPDATE zones SET nom = ?, expZone = ? WHERE id = ?"
            requete = bdd.connectionBDD!!.prepareStatement(sql)
            requete.setString(1, zone.nom)
            requete.setInt(2, zone.expZone)
            requete.setInt(3, zone.id)
        }

        val nbLigneMaj = requete.executeUpdate()
        if (nbLigneMaj > 0 && isInsert) {
            val keys = requete.generatedKeys
            if (keys.next()) zone.id = keys.getInt(1)
            keys.close()
        }
        requete.close()

        // 🔁 Met à jour la table zones_especesmonstre (relations zone <-> espèces)
        saveZoneEspeces(zone)
        return zone
    }

    // --- SAVE ALL ---
    fun saveAll(zones: Collection<Zone>): MutableList<Zone> {
        val result = mutableListOf<Zone>()
        for (z in zones) {
            val saved = save(z)
            if (saved != null) result.add(saved)
        }
        return result
    }

    // --- DELETE BY ID ---
    fun deleteById(id: Int): Boolean {
        // Supprimer d'abord les relations dans zones_especesmonstre
        val sqlRelations = "DELETE FROM zones_especesmonstre WHERE zone_id = ?"
        val reqRelations = bdd.connectionBDD!!.prepareStatement(sqlRelations)
        reqRelations.setInt(1, id)
        reqRelations.executeUpdate()
        reqRelations.close()

        // Supprimer la zone
        val sql = "DELETE FROM zones WHERE id = ?"
        val requete = bdd.connectionBDD!!.prepareStatement(sql)
        requete.setInt(1, id)
        return try {
            val nb = requete.executeUpdate()
            requete.close()
            nb > 0
        } catch (err: SQLException) {
            println("Erreur lors de la suppression de la zone : ${err.message}")
            false
        }
    }

    // --- UTILITAIRE : Sauvegarder les relations Zone <-> Especes ---
    private fun saveZoneEspeces(zone: Zone) {
        // Supprimer d’abord les anciennes relations
        val deleteSql = "DELETE FROM zones_especesmonstre WHERE zone_id = ?"
        val deleteReq = bdd.connectionBDD!!.prepareStatement(deleteSql)
        deleteReq.setInt(1, zone.id)
        deleteReq.executeUpdate()
        deleteReq.close()

        // Puis recréer les nouvelles
        val insertSql = "INSERT INTO zones_especesmonstre (zone_id, espece_id) VALUES (?, ?)"
        for (espece in zone.especesMonstres) {
            val insertReq = bdd.connectionBDD!!.prepareStatement(insertSql)
            insertReq.setInt(1, zone.id)
            insertReq.setInt(2, espece.id)
            insertReq.executeUpdate()
            insertReq.close()
        }
    }
}
