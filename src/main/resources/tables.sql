-- Pour créer la table entraineur
CREATE TABLE Entraineurs(
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(255),
    argents INTEGER);

ALTER TABLE Entraineurs
    ADD COLUMN is_npc BOOLEAN DEFAULT TRUE,       -- TRUE = NPC, FALSE = vrai joueur
ADD COLUMN zone_actuelle INTEGER DEFAULT NULL; -- zone actuelle pour les vrais joueurs

INSERT INTO Entraineurs (nom, argents) VALUES
                                          ('Alice', 1000),
                                          ('Bob', 1200),
                                          ('Clara', 900);
UPDATE Entraineurs
SET is_npc = TRUE,
    zone_actuelle = NULL
WHERE nom IN ('Alice', 'Bob', 'Clara');


CREATE TABLE EspecesMonstre (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    baseAttaque INT NOT NULL,
    baseDefense INT NOT NULL,
    baseVitesse INT NOT NULL,
    baseAttaqueSpe INT NOT NULL,
    baseDefenseSpe INT NOT NULL,
    basePv INT NOT NULL,
    modAttaque DOUBLE NOT NULL,
    modDefense DOUBLE NOT NULL,
    modVitesse DOUBLE NOT NULL,
    modAttaqueSpe DOUBLE NOT NULL,
    modDefenseSpe DOUBLE NOT NULL,
    modPv DOUBLE NOT NULL,
    description TEXT,
    particularites TEXT,
    caracteres TEXT
);

CREATE TABLE IndividuMonstre (
     id INT PRIMARY KEY AUTO_INCREMENT,
     nom VARCHAR(255) NOT NULL,
     niveau INT NOT NULL,
     attaque INT NOT NULL,
     defense INT NOT NULL,
     vitesse INT NOT NULL,
     attaqueSpe INT NOT NULL,
     defenseSpe INT NOT NULL,
     pvMax INT NOT NULL,
     potentiel DOUBLE NOT NULL,
     exp DOUBLE NOT NULL,
     pv INT NOT NULL,
     espece_id INT DEFAULT NULL REFERENCES EspecesMonstre(id),
     entraineur_equipe_id INT DEFAULT NULL REFERENCES Entraineurs(id),
     entraineur_boite_id INT DEFAULT NULL REFERENCES Entraineurs(id)
);

CREATE TABLE Zones (
                      id INT PRIMARY KEY AUTO_INCREMENT,
                      nom VARCHAR(255) NOT NULL,
                      expZone INT NOT NULL);

CREATE TABLE Zones_EspecesMonstre (
                                    zone_id INT NOT NULL,
                                    espece_id INT NOT NULL,
                                    PRIMARY KEY (zone_id, espece_id),
                                    FOREIGN KEY (zone_id) REFERENCES Zones(id),
                                    FOREIGN KEY (espece_id) REFERENCES EspecesMonstre(id)
);

-- Foret Sombre contient Springleaf et Flamkip et pyrokip
INSERT INTO Zone_Espece (zone_id, espece_id) VALUES (1, 1);
INSERT INTO Zone_Espece (zone_id, espece_id) VALUES (1, 4);
INSERT INTO Zone_Espece (zone_id, espece_id) VALUES (1, 5);

-- Caverne Obscure contient Aquamy
INSERT INTO Zone_Espece (zone_id, espece_id) VALUES (2, 7);

-- Montagne Ardente contient Laoumi et Bugsyface
INSERT INTO Zone_Espece (zone_id, espece_id) VALUES (3, 8);
INSERT INTO Zone_Espece (zone_id, espece_id) VALUES (3, 10);




INSERT INTO EspecesMonstre
(id, nom, type, baseAttaque, baseDefense, baseVitesse, baseAttaqueSpe, baseDefenseSpe, basePv,
 modAttaque, modDefense, modVitesse, modAttaqueSpe, modDefenseSpe, modPv,
 description, particularites, caracteres)
VALUES
    (1, 'springleaf', 'Graine', 9, 11, 10, 12, 14, 60,
     6.5, 9.0, 8.0, 7.0, 10.0, 14.0,
     'Un petit monstre espiègle au corps rond comme une graine. Il aime se cacher dans l’herbe haute et se dorer au soleil.',
     'Sa feuille sur la tête s’incline pour indiquer son humeur.',
     'Curieux, amical, un peu timide.'),

    (4, 'flamkip', 'Animal', 12, 8, 13, 16, 7, 50,
     10.0, 5.5, 9.5, 9.5, 6.5, 12.0,
     'Ce petit animal est toujours entouré d’une flamme dansante. Il déteste le froid et s’énerve facilement quand on tente d’éteindre son feu.',
     'Sa flamme change d’intensité selon son niveau d’énergie.',
     'Impulsif, joueur, loyal.'),

    (5, 'pyrokip', 'Animal', 18, 12, 15, 22, 11, 70,
     12.0, 8.0, 11.0, 12.5, 8.0, 15.0,
     'Pyrokip, l’évolution de Flamkip. Son feu est devenu intense et ses flammes sont capables de fondre la pierre. Fier et courageux, il protège son dresseur à tout prix.',
     'Ses flammes changent de couleur selon son humeur : rouge vif en colère, dorées quand il est calme.',
     'Fier, protecteur, explosif.'),

    (7, 'aquamy', 'Meteo', 10, 11, 9, 14, 14, 55,
     9.0, 10.0, 7.5, 12.0, 12.0, 13.5,
     'Une créature vaporeuse qui ressemble à un petit nuage. Les gouttes qui tombent de son corps sont pures et rafraîchissantes.',
     'Fait légèrement baisser la température autour de lui quand il s’endort.',
     'Calme, rêveur, mystérieux.'),

    (8, 'laoumi', 'Animal', 11, 10, 9, 8, 11, 58,
     11.0, 8.0, 7.0, 6.0, 11.5, 14.0,
     'Un petit ourson au pelage soyeux. Il adore se tenir debout et brandir ses petites pattes comme s’il dansait.',
     'Son grognement est plus mignon qu’effrayant, mais il devient redoutable pour défendre ses amis.',
     'Affectueux, protecteur, gourmand.'),

    (10, 'bugsyface', 'Insecte', 10, 13, 8, 7, 13, 45,
     7.0, 11.0, 6.5, 8.0, 11.5, 10.0,
     'Un insecte à la carapace luisante qui se déplace par petits bonds. Il communique en faisant vibrer ses antennes.',
     'Sa carapace devient plus dure après chaque mue, augmentant sa défense.',
     'Travailleur, sociable, infatigable.'),

    (13, 'galum', 'Mineral', 12, 15, 6, 8, 12, 55,
     9.0, 13.0, 4.0, 6.5, 10.5, 13.0,
     'Un golem ancien sculpté dans la pierre. Ses yeux s’illuminent d’une lueur mystérieuse quand il se met en garde.',
     'Peut rester immobile pendant des heures, le faisant passer pour une statue.',
     'Sérieux, stoïque, fiable.');






INSERT INTO IndividuMonstre
(nom, niveau, attaque, defense, vitesse, attaqueSpe, defenseSpe, pvMax, potentiel, exp, pv, espece_id, entraineur_equipe_id, entraineur_boite_id)
VALUES
    ('Springleaf_Alice', 5, 9, 11, 10, 12, 14, 60, 1.0, 0.0, 60, 1, 1, NULL),
    ('Aquamy_Bob', 7, 10, 11, 9, 14, 14, 55, 1.0, 0.0, 55, 7, 2, NULL),
    ('Bugsyface_Bob', 6, 10, 13, 8, 7, 13, 45, 1.0, 0.0, 45, 10, 2, NULL),
    ('Galum_Clara', 8, 12, 15, 6, 8, 12, 55, 1.0, 0.0, 55, 13, 3, NULL),
    ('Flamkip_Clara', 5, 12, 8, 13, 16, 7, 50, 1.0, 0.0, 50, 4, 3, NULL);



