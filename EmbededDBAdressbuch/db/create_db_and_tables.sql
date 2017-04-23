CONNECT 'jdbc:derby:datenbank;create=true;user=peter;password=passwort';

CREATE TABLE kontakte (
	kontakt_id INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	vorname VARCHAR(100),
	name VARCHAR(100) NOT NULL,
	strasse VARCHAR(100) NOT NULL,
	plz VARCHAR(10) NOT NULL,
	ort VARCHAR(100) NOT NULL,
	region VARCHAR(100),
	land VARCHAR(100),
	geschlecht VARCHAR(10),
	telefon VARCHAR(100),
	telefax VARCHAR(100),
	e_mail VARCHAR(300),
	programmieren BOOLEAN,
	betriebssysteme BOOLEAN,
	netzwerke BOOLEAN,
	weitere_kenntnisse VARCHAR(300),
	notizen VARCHAR(500)
);

INSERT INTO kontakte VALUES
	(
	  DEFAULT,
	  'Konservative','CDU Deutschlands','Klingelhöferstraße 8','10785','Berlin',
	  'Berlin','Deutschland','weiblich',
	  '030 22070-0','030 22070-111','info@cdu.de',
	  false,false,false,'Politische Führung',
	  'vertreten durch die Vorsitzende, Dr. Angela Merkel.'
	),
	(
	  DEFAULT,
	  'Peter','Kress','Emmertsgrundpassage 41','69126','Heidelberg',
	  'Baden-Württemberg','Deutschland','männlich',
	  '06221/383545','06221/383545','kress@web.de',
	  true,true,true,'webdesign',
	  'an Rande eine Notiz'
	),
	(
	  DEFAULT,
	  'Carsten','Brosda','Wilhelmstraße 141','10963','Berlin',
	  'Berlin','Deutschland','männlich',
	  '030 - 25991 - 0','030 - 25991 - 375','parteivorstand[at]spd.de',
	  false,false,true,'Politik',
	  'Wenn Sie Kontakt zum SPD-Parteivorstand aufnehmen wollen, wenden 
	  Sie sich bitte telefonisch an die Direktkommunikation des 
	  SPD-Parteivorstandes unter Telefon: 030-25991-500'
	),
	(
	  DEFAULT,
	  'Datendieb','Google Inc.','1600 Amphitheatre Parkway','94043','Mountain View',
	  null,'USA','männlich',
	  '+1 650-253-0000','+1 650-253-0001',null,
	  true,true,true,'Informationsverarbeitung',
	  null
	),
	(
	  DEFAULT,
	  'Vergissmeinnicht','Central Intelligence Agency','Märchenwald 7','20505','Washington',
	  null,'USA','weiblich',
	  null,null,null,
	  true,true,true,'kann alles',
	  'Geheimdienst der Vereinigten Staaten'
	),
	(
	  DEFAULT,
	  'KGB','OAO Gazprom','Strasse 16','117997','Stadt',
	  null,'Russische Föderation','weiblich',
	  '++7 (495) 719-30-01','++7 (495) 719-83-33','gazprom@gazprom.ru',
	  true,true,true,'Verkauf',
	  'Unser tägliches Gas gib uns heute'
	);