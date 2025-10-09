-- Pour créer la table entraineur
CREATE TABLE Entraineurs(
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(255),
    argents INTEGER);

INSERT INTO Entraineur (nom, argents) VALUES
                                          ('Alice', 1000),
                                          ('Bob', 1200),
                                          ('Clara', 900);


CREATE TABLE EspecesMonstre (
    id INT PRIMARY KEY,
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

INSERT INTO IndividuMonstre
(nom, niveau, attaque, defense, vitesse, attaqueSpe, defenseSpe, pvMax, potentiel, exp, pv, espece_id, entraineur_equipe_id, entraineur_boite_id)
VALUES
    ('Springleaf_Alice', 5, 9, 11, 10, 12, 14, 60, 1.0, 0.0, 60, 1, 1, NULL),
    ('Aquamy_Bob', 7, 10, 11, 9, 14, 14, 55, 1.0, 0.0, 55, 7, 2, NULL),
    ('Bugsyface_Bob', 6, 10, 13, 8, 7, 13, 45, 1.0, 0.0, 45, 10, 2, NULL),
    ('Galum_Clara', 8, 12, 15, 6, 8, 12, 55, 1.0, 0.0, 55, 13, 3, NULL),
    ('Flamkip_Clara', 5, 12, 8, 13, 16, 7, 50, 1.0, 0.0, 50, 4, 3, NULL);



