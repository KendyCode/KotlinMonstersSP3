package DAO

import db
import entraineurDAO
import especeMonstreDAO
import jdbc.BDD
import monstre.EspeceMonstre
import monstre.IndividuMonstre
import monstre.IndividuMonstreEntity

import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Statement

/**
 * DAO (Data Access Object) pour la table `IndividuMonstre`.
 *
 * Fournit une interface pour effectuer des opérations CRUD (Create, Read, Update, Delete)
 * sur les individus monstres, en s'assurant que chaque objet peut être converti entre :
 * - [IndividuMonstreEntity] : représentation de la base de données
 * - [IndividuMonstre] : objet métier du jeu
 *
 * ⚡ Fonctionnalités principales :
 * - 🔍 Lecture : findAll(), findById(), findByNom()
 * - 💾 Sauvegarde : save(), saveAll()
 * - ❌ Suppression : deleteById()
 * - 🛠 Conversion Base ↔ Objet Métier : mapResultSet(), toModel()
 *
 * @param bdd Objet de connexion à la base de données (par défaut `db` global).
 */
class IndividuMonstreDAO(val bdd: BDD = db) {

    // --- FIND ALL ---
    /**
     * Récupère tous les individus monstres de la base.
     *
     * Pour chaque ligne SQL, appelle [mapResultSet] pour créer l'entité correspondante.
     *
     * @return Liste mutable de [IndividuMonstreEntity].
     */
    fun findAll(): MutableList<IndividuMonstreEntity> {
        val result = mutableListOf<IndividuMonstreEntity>()
        val sql = "SELECT * FROM IndividuMonstre"
        val requetePreparer = bdd.connectionBDD!!.prepareStatement(sql)
        val resultatRequete = bdd.executePreparedStatement(requetePreparer)

        if (resultatRequete != null) {
            while (resultatRequete.next()) {
                result.add(mapResultSet(resultatRequete))
            }
        }

        requetePreparer.close()
        return result
    }

    // --- FIND BY ID ---
    /**
     * Recherche un individu par son identifiant unique.
     *
     * @param id Identifiant de l'individu.
     * @return [IndividuMonstreEntity] correspondant ou `null` si non trouvé.
     */
    fun findById(id: Int): IndividuMonstreEntity? {
        val sql = "SELECT * FROM IndividuMonstre WHERE id = ?"
        val requetePreparer = bdd.connectionBDD!!.prepareStatement(sql)
        requetePreparer.setInt(1, id)
        val resultatRequete = bdd.executePreparedStatement(requetePreparer)

        val espece = if (resultatRequete != null && resultatRequete.next()) {
            mapResultSet(resultatRequete)
        } else null

        requetePreparer.close()
        return espece
    }

    // --- FIND BY NOM ---
    /**
     * Recherche les individus par leur nom exact.
     *
     * @param nomRechercher Nom de l'individu à rechercher.
     * @return Liste mutable d'individus correspondant au nom.
     */
    fun findByNom(nomRechercher: String): MutableList<IndividuMonstreEntity> {
        val result = mutableListOf<IndividuMonstreEntity>()
        val sql = "SELECT * FROM IndividuMonstre WHERE nom = ?"
        val requetePreparer = bdd.connectionBDD!!.prepareStatement(sql)
        requetePreparer.setString(1, nomRechercher)
        val resultatRequete = bdd.executePreparedStatement(requetePreparer)

        if (resultatRequete != null) {
            while (resultatRequete.next()) {
                result.add(mapResultSet(resultatRequete))
            }
        }

        requetePreparer.close()
        return result
    }

    // --- SAVE (INSERT / UPDATE) ---
    /**
     * Sauvegarde un individu monstre.
     *
     * ⚡ Fonctionnement :
     * - Si `individu.id == 0`, insertion dans la base et récupération de l'ID généré.
     * - Sinon, mise à jour de la ligne existante.
     * - Les relations vers les espèces et entraîneurs peuvent être `null` et sont correctement gérées.
     *
     * @param individu Entité à sauvegarder.
     * @return L'entité sauvegardée avec son ID, ou `null` si la mise à jour a échoué.
     */
    fun save(individu: IndividuMonstreEntity): IndividuMonstreEntity? {
        val requetePreparer: PreparedStatement

        if (individu.id == 0) {
            // INSERT
            val sql = """
                INSERT INTO IndividuMonstre 
                (nom, niveau, attaque, defense, vitesse, attaqueSpe, defenseSpe, pvMax,
                 potentiel, exp, pv, espece_id, entraineur_equipe_id, entraineur_boite_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent()

            requetePreparer = bdd.connectionBDD!!.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            requetePreparer.setString(1, individu.nom)
            requetePreparer.setInt(2, individu.niveau)
            requetePreparer.setInt(3, individu.attaque)
            requetePreparer.setInt(4, individu.defense)
            requetePreparer.setInt(5, individu.vitesse)
            requetePreparer.setInt(6, individu.attaqueSpe)
            requetePreparer.setInt(7, individu.defenseSpe)
            requetePreparer.setInt(8, individu.pvMax)
            requetePreparer.setDouble(9, individu.potentiel)
            requetePreparer.setDouble(10, individu.exp)
            requetePreparer.setInt(11, individu.pv)
            if (individu.especeId != null) requetePreparer.setInt(12, individu.especeId!!) else requetePreparer.setNull(12, java.sql.Types.INTEGER)
            if (individu.entraineurEquipeId != null) requetePreparer.setInt(13, individu.entraineurEquipeId!!) else requetePreparer.setNull(13, java.sql.Types.INTEGER)
            if (individu.entraineurBoiteId != null) requetePreparer.setInt(14, individu.entraineurBoiteId!!) else requetePreparer.setNull(14, java.sql.Types.INTEGER)

            val nbLigneMaj = requetePreparer.executeUpdate()
            if (nbLigneMaj > 0) {
                val generatedKeys = requetePreparer.generatedKeys
                if (generatedKeys.next()) {
                    individu.id = generatedKeys.getInt(1)
                }
            }

        } else {
            // UPDATE
            val sql = """
                UPDATE IndividuMonstre SET 
                    nom=?, niveau=?, attaque=?, defense=?, vitesse=?, attaqueSpe=?, defenseSpe=?, pvMax=?,
                    potentiel=?, exp=?, pv=?, espece_id=?, entraineur_equipe_id=?, entraineur_boite_id=?
                WHERE id=?
            """.trimIndent()

            requetePreparer = bdd.connectionBDD!!.prepareStatement(sql)
            requetePreparer.setString(1, individu.nom)
            requetePreparer.setInt(2, individu.niveau)
            requetePreparer.setInt(3, individu.attaque)
            requetePreparer.setInt(4, individu.defense)
            requetePreparer.setInt(5, individu.vitesse)
            requetePreparer.setInt(6, individu.attaqueSpe)
            requetePreparer.setInt(7, individu.defenseSpe)
            requetePreparer.setInt(8, individu.pvMax)
            requetePreparer.setDouble(9, individu.potentiel)
            requetePreparer.setDouble(10, individu.exp)
            requetePreparer.setInt(11, individu.pv)
            if (individu.especeId != null) requetePreparer.setInt(12, individu.especeId!!) else requetePreparer.setNull(12, java.sql.Types.INTEGER)
            if (individu.entraineurEquipeId != null) requetePreparer.setInt(13, individu.entraineurEquipeId!!) else requetePreparer.setNull(13, java.sql.Types.INTEGER)
            if (individu.entraineurBoiteId != null) requetePreparer.setInt(14, individu.entraineurBoiteId!!) else requetePreparer.setNull(14, java.sql.Types.INTEGER)
            requetePreparer.setInt(15, individu.id)

            val nbLigneMaj = requetePreparer.executeUpdate()
            if (nbLigneMaj == 0) {
                requetePreparer.close()
                return null
            }
        }

        requetePreparer.close()
        return individu
    }

    // --- SAVE ALL ---
    /**
     * Sauvegarde une collection d'individus.
     *
     * Appelle [save] pour chaque individu et ne retient que ceux sauvegardés avec succès.
     *
     * @param individus Collection d'individus à sauvegarder.
     * @return Liste des individus sauvegardés.
     */// --- SAVE ALL ---
    /**
     * Sauvegarde une collection d'individus.
     *
     * Appelle [save] pour chaque individu et ne retient que ceux sauvegardés avec succès.
     *
     * @param individus Collection d'individus à sauvegarder.
     * @return Liste des individus sauvegardés.
     */
    fun saveAll(individus: Collection<IndividuMonstreEntity>): MutableList<IndividuMonstreEntity> {
        val result = mutableListOf<IndividuMonstreEntity>()
        for (i in individus) {
            val sauvegarde = save(i)
            if (sauvegarde != null) result.add(sauvegarde)
        }
        return result
    }

    // --- DELETE BY ID ---
    /**
     * Supprime un individu par son ID.
     *
     * Utilise un PreparedStatement pour éviter l'injection SQL.
     * Gère les exceptions SQL et retourne `false` en cas d'échec.
     *
     * @param id ID de l'individu à supprimer.
     * @return `true` si la suppression a réussi, `false` sinon.
     */
    fun deleteById(id: Int): Boolean {
        val sql = "DELETE FROM IndividuMonstre WHERE id = ?"
        val requetePreparer = bdd.connectionBDD!!.prepareStatement(sql)
        requetePreparer.setInt(1, id)

        return try {
            val nbLigneMaj = requetePreparer.executeUpdate()
            requetePreparer.close()
            nbLigneMaj > 0
        } catch (erreur: SQLException) {
            println("Erreur lors de la suppression de l’individu : ${erreur.message}")
            false
        }
    }

    // --- UTILITAIRE : map ResultSet vers Objet ---
    /**
     * Transforme une ligne SQL en [IndividuMonstreEntity].
     *
     * Gère les valeurs nullables pour les relations (espece_id, entraineur_equipe_id, entraineur_boite_id)
     * en utilisant `getObject()` pour détecter la nullité.
     *
     * @param rs ResultSet positionné sur la ligne.
     * @return [IndividuMonstreEntity] correspondant.
     */
    private fun mapResultSet(rs: java.sql.ResultSet): IndividuMonstreEntity {
        return IndividuMonstreEntity(
            id = rs.getInt("id"),
            nom = rs.getString("nom"),
            niveau = rs.getInt("niveau"),
            attaque = rs.getInt("attaque"),
            defense = rs.getInt("defense"),
            vitesse = rs.getInt("vitesse"),
            attaqueSpe = rs.getInt("attaqueSpe"),
            defenseSpe = rs.getInt("defenseSpe"),
            pvMax = rs.getInt("pvMax"),
            potentiel = rs.getDouble("potentiel"),
            exp = rs.getDouble("exp"),
            pv = rs.getInt("pv"),
            especeId = rs.getObject("espece_id")?.let { rs.getInt("espece_id") },
            entraineurEquipeId = rs.getObject("entraineur_equipe_id")?.let { rs.getInt("entraineur_equipe_id") },
            entraineurBoiteId = rs.getObject("entraineur_boite_id")?.let { rs.getInt("entraineur_boite_id") }
        )
    }
    // --- CONVERSION ENTITY → MÉTIER --- AZYYY CETAIT HARD
    /**
     * Transforme un [IndividuMonstreEntity] en objet métier [IndividuMonstre].
     *
     * ⚡ Étapes :
     * 1. Récupère l'espèce correspondante via [especeMonstreDAO].
     * 2. Récupère l'entraîneur (équipe ou boîte) via [entraineurDAO].
     * 3. Crée l'objet [IndividuMonstre].
     * 4. Réinjecte toutes les valeurs exactes de la base pour éviter la randomisation du constructeur.
     *
     * @param entity Entité à convertir.
     * @return Objet métier [IndividuMonstre] ou `null` si l'espèce n'existe pas.
     */
    fun toModel(entity: IndividuMonstreEntity): IndividuMonstre? {
        // Récupère l'espèce correspondante
        val espece = entity.especeId?.let { especeMonstreDAO.findById(it) } ?: return null

        // Récupère l'entraîneur (équipe ou boîte)
        val entraineur = entity.entraineurEquipeId?.let { entraineurDAO.findByIdLight(it) }
            ?: entity.entraineurBoiteId?.let { entraineurDAO.findByIdLight(it) }

        // Crée l’objet de jeu
        val monstre = IndividuMonstre(
            id = entity.id,
            nom = entity.nom,
            espece = espece,
            entraineur = entraineur,
            expInit = entity.exp
        )

        // Réinjecte les valeurs exactes de la base (pour éviter la randomisation du constructeur)
        monstre.niveau = entity.niveau
        monstre.attaque = entity.attaque
        monstre.defense = entity.defense
        monstre.vitesse = entity.vitesse
        monstre.attaqueSpe = entity.attaqueSpe
        monstre.defenseSpe = entity.defenseSpe
        monstre.pvMax = entity.pvMax
        monstre.potentiel = entity.potentiel
        monstre.exp = entity.exp
        monstre.pv = entity.pv

        return monstre
    }
}