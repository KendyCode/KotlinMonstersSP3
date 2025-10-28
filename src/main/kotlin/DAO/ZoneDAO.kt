package DAO

import db
import jdbc.BDD
import monstre.EspeceMonstre
import monde.Zone
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Statement

/**
 * DAO (Data Access Object) pour la table `zones`.
 *
 * Fournit une interface pour effectuer des opérations CRUD (Create, Read, Update, Delete)
 * sur les zones du jeu. Chaque zone peut contenir plusieurs espèces de monstres
 * et être reliée à d'autres zones via `zoneSuivante` et `zonePrecedente`.
 *
 * ⚡ Fonctionnalités principales :
 * - 🔍 Lecture : findAll(), findById()
 * - 💾 Sauvegarde : save(), saveAll()
 * - ❌ Suppression : deleteById()
 * - 🛠 Gestion des relations Zone ↔ Espèces de Monstres
 *
 * @param bdd Objet de connexion à la base de données (par défaut `db` global)
 * @param especeMonstreDAO DAO utilisé pour récupérer les espèces de monstres liées.
 */
class ZoneDAO(
    val bdd: BDD = db,
    private val especeMonstreDAO: EspeceMonstreDAO = EspeceMonstreDAO(bdd)
) {

    // --- FIND ALL ---
    /**
     * Récupère toutes les zones de la base.
     *
     * ⚡ Étapes :
     * 1. Charge toutes les zones avec leurs propriétés de base (`id`, `nom`, `expZone`) et les espèces de monstres.
     * 2. Stocke les zones dans une map temporaire pour relier `zoneSuivante` et `zonePrecedente`.
     *
     * @return Liste mutable de zones avec relations et espèces de monstres correctement liées.
     */
    fun findAll(): MutableList<Zone> {
        val zones = mutableListOf<Zone>()
        val zoneMap = mutableMapOf<Int, Zone>()

        // Charger toutes les zones sans relations
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
                zoneMap[zone.id] = zone
            }
        }
        requete.close()

        // Relier zoneSuivante et zonePrecedente
        for (zone in zones) {
            val sqlLink = "SELECT fk_zoneSuivante_id, fk_zonePrecedente_id FROM zones WHERE id = ?"
            val reqLink = bdd.connectionBDD!!.prepareStatement(sqlLink)
            reqLink.setInt(1, zone.id)
            val rsLink = bdd.executePreparedStatement(reqLink)
            if (rsLink != null && rsLink.next()) {
                val idSuivante = rsLink.getObject("fk_zoneSuivante_id")?.let { rsLink.getInt("fk_zoneSuivante_id") }
                val idPrecedente = rsLink.getObject("fk_zonePrecedente_id")?.let { rsLink.getInt("fk_zonePrecedente_id") }
                zone.zoneSuivante = idSuivante?.let { zoneMap[it] }
                zone.zonePrecedente = idPrecedente?.let { zoneMap[it] }
            }
            reqLink.close()
        }

        return zones
    }

    // --- FIND BY ID ---
    /**
     * Recherche une zone par son identifiant unique.
     *
     * ⚡ Cette méthode utilise findAll() et filtre la zone correspondante.
     *
     * @param id Identifiant de la zone.
     * @return Zone correspondante ou `null` si non trouvée.
     */
    fun findById(id: Int): Zone? {
        return findAll().firstOrNull { it.id == id }
    }

    // --- TROUVER LES ESPÈCES LIÉES À UNE ZONE ---
    /**
     * Récupère toutes les espèces de monstres associées à une zone.
     *
     * ⚡ Utilise la table de jointure `zones_especesmonstre` et [EspeceMonstreDAO].
     *
     * @param zoneId ID de la zone pour laquelle récupérer les espèces.
     * @return Liste mutable d'espèces de monstres.
     */
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
    /**
     * Sauvegarde une zone dans la base de données.
     *
     * ⚡ Fonctionnement :
     * - Si `zone.id == 0` : insertion et récupération de l'ID généré.
     * - Sinon : mise à jour de la zone existante.
     * - Met également à jour la table de jointure `zones_especesmonstre`.
     *
     * @param zone Zone à sauvegarder.
     * @return Zone sauvegardée avec ID mis à jour si insertion.
     */
    fun save(zone: Zone): Zone? {
        val requete: PreparedStatement
        val isInsert = zone.id == 0

        if (isInsert) {
            val sql = """
                INSERT INTO zones (nom, expZone, fk_zoneSuivante_id, fk_zonePrecedente_id) 
                VALUES (?, ?, ?, ?)
            """.trimIndent()
            requete = bdd.connectionBDD!!.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            requete.setString(1, zone.nom)
            requete.setInt(2, zone.expZone)
            requete.setObject(3, zone.zoneSuivante?.id)
            requete.setObject(4, zone.zonePrecedente?.id)
        } else {
            val sql = """
                UPDATE zones SET nom = ?, expZone = ?, fk_zoneSuivante_id = ?, fk_zonePrecedente_id = ? 
                WHERE id = ?
            """.trimIndent()
            requete = bdd.connectionBDD!!.prepareStatement(sql)
            requete.setString(1, zone.nom)
            requete.setInt(2, zone.expZone)
            requete.setObject(3, zone.zoneSuivante?.id)
            requete.setObject(4, zone.zonePrecedente?.id)
            requete.setInt(5, zone.id)
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
    /**
     * Sauvegarde une collection de zones.
     *
     * Appelle [save] pour chaque zone et retourne celles sauvegardées avec succès.
     *
     * @param zones Collection de zones à sauvegarder.
     * @return Liste des zones sauvegardées.
     */
    fun saveAll(zones: Collection<Zone>): MutableList<Zone> {
        val result = mutableListOf<Zone>()
        for (z in zones) {
            val saved = save(z)
            if (saved != null) result.add(saved)
        }
        return result
    }

    // --- DELETE BY ID ---
    /**
     * Supprime une zone par son ID.
     *
     * ⚡ Étapes :
     * 1. Supprime les relations dans `zones_especesmonstre` pour éviter les clés étrangères orphelines.
     * 2. Supprime la zone elle-même.
     *
     * @param id ID de la zone à supprimer.
     * @return `true` si la suppression a réussi, `false` sinon.
     */
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

    // --- UTILITAIRE : Sauvegarder les relations Zone <-> Espèces ---
    /**
     * Met à jour les relations entre une zone et ses espèces de monstres dans la table `zones_especesmonstre`.
     *
     * ⚡ Fonctionnement :
     * 1. Supprime d'abord toutes les relations existantes pour cette zone.
     * 2. Insère les nouvelles relations correspondant à `zone.especesMonstres`.
     *
     * @param zone Zone pour laquelle mettre à jour les relations.
     */
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
