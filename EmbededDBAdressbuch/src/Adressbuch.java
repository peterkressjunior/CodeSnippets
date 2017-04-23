import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;


/**
  *
  * Adressbuch
  *
  * @version 3.0 vom 26.01.2011
  * @author Peter Kress
  */
public class Adressbuch extends JFrame {
  /**
	 * 
	 */
	private static final long serialVersionUID = 185221609903316898L;
// Anfang Attribute
  private JPanel jPKontakt = new JPanel(null);
  private JLabel jLVorname = new JLabel();
  private JTextField jTFVorname = new JTextField();
  private JTextField jTFName = new JTextField();
  private JLabel jLName = new JLabel();
  private JTextField jTFStrasse = new JTextField();
  private JTextField jTFPLZ = new JTextField();
  private JLabel jLRegion = new JLabel();
  private JTextField jTFRegion = new JTextField();
  private JTextField jTFLand = new JTextField();
  private JLabel jLLand = new JLabel();
  private JLabel jLGeschlecht = new JLabel();
  private JTextField jTFOrt = new JTextField();
  private ButtonGroup bGGeschlecht = new ButtonGroup();
  private JRadioButton jRBMaennlich = new JRadioButton();
  private JRadioButton jRBWeiblich = new JRadioButton();
  private JLabel jLTelefon = new JLabel();
  private JTextField jTFTelefon = new JTextField();
  private JLabel jLFax = new JLabel();
  private JTextField jTFFax = new JTextField();
  private JLabel jLE_Mail = new JLabel();
  private JTextField jTFE_Mail = new JTextField();
  private JLabel jLStrasse = new JLabel();
  private JLabel jLPLZ_Ort = new JLabel();
  private JPanel jPKenntnisse = new JPanel(null);
  private JLabel jLKenntnisse = new JLabel();
  private JCheckBox jCBProgrammieren = new JCheckBox();
  private JCheckBox jCBBetriebssysteme = new JCheckBox();
  private JCheckBox jCBNetzwerke = new JCheckBox();
  private JLabel jLWeitere = new JLabel();
  private JScrollPane jSPWeitere = new JScrollPane();
  private JTextArea jTAWeitere = new JTextArea("");
  private JPanel jPNotizen = new JPanel(null);
  private JLabel jLNotizen = new JLabel();
  private JScrollPane jSPNotizen = new JScrollPane();
  private JTextArea jTANotizen = new JTextArea("");
  private JPanel jPControl = new JPanel(null);
  private JButton jBAbbrechen = new JButton();
  private JButton jBNeu = new JButton();
  private JButton jBLoeschen = new JButton();
  private JButton jBUebernehmen = new JButton();
  private JLabel jLMandatoryInfo = new JLabel();
  private JPanel jPNavigation = new JPanel(null);
  private JButton jBNext = new JButton();
  private JButton jBPrevious = new JButton();
  private JLabel jLNavigation = new JLabel();
  private JButton jBSuchen = new JButton();
  private JLabel jLPosition = new JLabel();
  private JLabel jLPosition_nr = new JLabel();
  private JPanel jPTitel = new JPanel(null);
  private JLabel jLEingabeformular = new JLabel();
  private RandomAccessFile csvFile;
  public Vector<String[]> lines;
  public int currentPos = -1;
  DerbyClient dc = new DerbyClient();

  // Ende Attribute

  /**
    * Konstruktor
    * @param title Fenstertitel
    */
  public Adressbuch(String title) {
    super(title);
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent we) {
          int option = JOptionPane.NO_OPTION;

          if (isSomethingChanged() || jBAbbrechen.isVisible()) {
            option = JOptionPane.showConfirmDialog(null,
                                                   "Änderungen speichern?",
                                                   "Änderungen speichern?",
                                                   JOptionPane.YES_NO_OPTION,
                                                   JOptionPane.QUESTION_MESSAGE);
          }

          if (option == JOptionPane.YES_OPTION) {
            if (jBAbbrechen.isVisible() && areMandatoryFieldsFilled()) {
              updatePerson();
              System.exit(0);
            } else if (areMandatoryFieldsFilled()) {
              updatePerson();
              System.exit(0);
            }
          } else {
            System.exit(0);
          }
        }
      });
    setSize(903, 468);
    this.setLocationRelativeTo(null);
    initUI();
    connect();
    this.setVisible(true);
    jBAbbrechen.setBounds(440, 8, 107, 25);
    jBAbbrechen.setText("Abbrechen");
    jBAbbrechen.setMargin(new Insets(2, 2, 2, 2));
    jBAbbrechen.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          jBAbbrechen_ActionPerformed(evt);
        }
      });
    jBAbbrechen.setVisible(false);
    jPControl.add(jBAbbrechen);

    // Anfang Komponenten
    // Ende Komponenten
  }

  // Anfang Methoden

  /**
    * List kontakte.csv ein, erstellt eine neue Datei, falls keine existiert
    *
    */
  protected void connect() {
    try {
      System.out.println("connecting to database");
      dc.connect();
      System.out.println("connected ... " + dc.connected());
      dc.select();

      if ((dc.resultSet != null) && dc.resultSet.isBeforeFirst()) {
        dc.resultSet.next();
        System.out.println(dc.resultSet.getString(1));
        fillEingabeFormular(dc.resultSet);

        int pos = dc.resultSet.getRow();
        dc.resultSet.last();

        int count = dc.resultSet.getRow();
        dc.resultSet.first();
        jLPosition_nr.setText("" + pos + " / " + count);
      } else {
        jLPosition_nr.setText("leere Datenbank");
      }
    } catch (SQLException sqle) {
      sqle.printStackTrace(System.err);
      System.out.println(sqle.getMessage());
    }
  }

  /**
    * Füllt die Felder auf der Maske mit Werten
    * @param data Ein Datum aus den eingelesene Adressdaten
    */

  // Call By Reference so zu sagen
  public void fillEingabeFormular(ResultSet data) {
    try {
      jTFVorname.setText(data.getString("vorname"));
      jTFName.setText(data.getString("name"));
      jTFStrasse.setText(data.getString("strasse"));
      jTFPLZ.setText(data.getString("plz"));
      jTFOrt.setText(data.getString("ort"));
      jTFRegion.setText(data.getString("region"));
      jTFLand.setText(data.getString("land"));

      if (data.getString("geschlecht").equals("weiblich")) {
        jRBWeiblich.setSelected(true);
      } else if (data.getString("geschlecht").equals("männlich")) {
        jRBMaennlich.setSelected(true);
      }

      jTFTelefon.setText(data.getString("telefon"));
      jTFFax.setText(data.getString("telefax"));
      jTFE_Mail.setText(data.getString("e_mail"));

      jCBProgrammieren.setSelected(data.getBoolean("Programmieren"));
      jCBBetriebssysteme.setSelected(data.getBoolean("Betriebssysteme"));
      jCBNetzwerke.setSelected(data.getBoolean("Netzwerke"));

      jTAWeitere.setText(data.getString("weitere_kenntnisse"));
      jTANotizen.setText(data.getString("notiz"));
    } catch (SQLException sqle) {
      //
    }
  }

  /**
    * Initialisiert das Graphische User Interface
    * Setzt alle benötigten Komponenten auf die Maske
    */
  public void initUI() {
    System.out.println("init user interface");

    Container cp = getContentPane();
    cp.setLayout(null);
    jPKontakt.setBounds(16, 48, 425, 329);
    cp.add(jPKontakt);
    jLVorname.setBounds(8, 8, 120, 16);
    jLVorname.setText("Vorname:*");
    jLVorname.setFont(new Font("MS Sans Serif", Font.PLAIN, 13));
    jPKontakt.add(jLVorname);
    jTFVorname.setBounds(144, 8, 273, 24);
    jTFVorname.setText("");
    jPKontakt.add(jTFVorname);
    jPKenntnisse.setBounds(456, 48, 425, 201);
    cp.add(jPKenntnisse);
    jLKenntnisse.setBounds(8, 8, 407, 20);
    jLKenntnisse.setText("Kentnisse:");
    jLKenntnisse.setFont(new Font("MS Sans Serif", Font.BOLD, 15));
    jLKenntnisse.setText("Kenntnisse:");
    jPKenntnisse.add(jLKenntnisse);
    jCBProgrammieren.setBounds(8, 32, 249, 17);
    jCBProgrammieren.setText("Programmieren");
    jPKenntnisse.add(jCBProgrammieren);
    jTFName.setBounds(144, 40, 273, 24);
    jTFName.setText("");
    jPKontakt.add(jTFName);
    jLName.setBounds(8, 40, 120, 16);
    jLName.setText("Name:*");
    jLName.setFont(new Font("MS Sans Serif", Font.PLAIN, 13));
    jPKontakt.add(jLName);
    jTFStrasse.setBounds(144, 72, 273, 24);
    jTFStrasse.setText("");
    jPKontakt.add(jTFStrasse);
    jTFPLZ.setBounds(144, 104, 57, 24);
    jTFPLZ.setText("");
    jPKontakt.add(jTFPLZ);
    jLRegion.setBounds(8, 136, 120, 16);
    jLRegion.setText("Staat / Region:");
    jLRegion.setFont(new Font("MS Sans Serif", Font.PLAIN, 13));
    jPKontakt.add(jLRegion);
    jTFRegion.setBounds(144, 136, 273, 24);
    jTFRegion.setText("");
    jPKontakt.add(jTFRegion);
    jTFLand.setBounds(144, 168, 273, 24);
    jTFLand.setText("");
    jPKontakt.add(jTFLand);
    jLLand.setBounds(8, 168, 120, 16);
    jLLand.setText("Land:");
    jLLand.setFont(new Font("MS Sans Serif", Font.PLAIN, 13));
    jPKontakt.add(jLLand);
    jLGeschlecht.setBounds(8, 200, 120, 16);
    jLGeschlecht.setText("Geschlecht:*");
    jLGeschlecht.setFont(new Font("MS Sans Serif", Font.PLAIN, 13));
    jPKontakt.add(jLGeschlecht);
    jTFOrt.setBounds(208, 104, 209, 24);
    jTFOrt.setText("");
    jPKontakt.add(jTFOrt);
    jRBMaennlich.setBounds(160, 200, 113, 17);
    jRBMaennlich.setText("männlich");
    bGGeschlecht.add(jRBMaennlich);
    jPKontakt.add(jRBMaennlich);
    jRBWeiblich.setBounds(288, 200, 113, 17);
    jRBWeiblich.setText("weiblich");
    bGGeschlecht.add(jRBWeiblich);
    jPKontakt.add(jRBWeiblich);
    jLTelefon.setBounds(8, 232, 120, 16);
    jLTelefon.setText("Telefon:");
    jLTelefon.setFont(new Font("MS Sans Serif", Font.PLAIN, 13));
    jPKontakt.add(jLTelefon);
    jTFTelefon.setBounds(144, 232, 273, 24);
    jTFTelefon.setText("");
    jPKontakt.add(jTFTelefon);
    jLFax.setBounds(8, 264, 120, 16);
    jLFax.setText("Fax:");
    jLFax.setFont(new Font("MS Sans Serif", Font.PLAIN, 13));
    jPKontakt.add(jLFax);
    jTFFax.setBounds(144, 264, 273, 24);
    jTFFax.setText("");
    jPKontakt.add(jTFFax);
    jLE_Mail.setBounds(8, 296, 120, 16);
    jLE_Mail.setText("E-Mail:");
    jLE_Mail.setFont(new Font("MS Sans Serif", Font.PLAIN, 13));
    jPKontakt.add(jLE_Mail);
    jTFE_Mail.setBounds(144, 296, 273, 24);
    jTFE_Mail.setText("");
    jPKontakt.add(jTFE_Mail);
    jLStrasse.setBounds(8, 72, 128, 16);
    jLStrasse.setText("Straße:*");
    jLStrasse.setFont(new Font("MS Sans Serif", Font.PLAIN, 13));
    jLStrasse.setText("Strae:*");
    jLStrasse.setText("Strase:*");
    jLStrasse.setText("Strasse:*");
    jLStrasse.setText("Strase:*");
    jLStrasse.setText("Strae:*");
    jLStrasse.setText("Stre:*");
    jLStrasse.setText("Strße:*");
    jLStrasse.setText("Straße:*");
    jPKontakt.add(jLStrasse);
    jLPLZ_Ort.setBounds(8, 104, 120, 16);
    jLPLZ_Ort.setText("PLZ / Ort:*");
    jLPLZ_Ort.setFont(new Font("MS Sans Serif", Font.PLAIN, 13));
    jPKontakt.add(jLPLZ_Ort);
    jPKontakt.setBorder(BorderFactory.createMatteBorder(2, 0, 2, 0,
                                                        Color.DARK_GRAY));
    jCBBetriebssysteme.setBounds(8, 56, 265, 17);
    jCBBetriebssysteme.setText("Betriebssysteme");
    jPKenntnisse.add(jCBBetriebssysteme);
    jCBNetzwerke.setBounds(8, 80, 273, 17);
    jCBNetzwerke.setText("Netzwerke");
    jPKenntnisse.add(jCBNetzwerke);
    jLWeitere.setBounds(8, 104, 46, 16);
    jLWeitere.setText("weitere:");
    jLWeitere.setFont(new Font("MS Sans Serif", Font.PLAIN, 13));
    jPKenntnisse.add(jLWeitere);
    jPKenntnisse.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0,
                                                           Color.DARK_GRAY));
    jSPWeitere.setBounds(8, 128, 409, 65);
    jPKenntnisse.add(jSPWeitere);
    jTAWeitere.setBounds(-10, -2, 417, 65);
    jTAWeitere.setText("");
    jTAWeitere.setLineWrap(true);
    jSPWeitere.setViewportView(jTAWeitere);
    jPNotizen.setBounds(456, 256, 425, 121);
    cp.add(jPNotizen);
    jLNotizen.setBounds(8, 8, 67, 20);
    jLNotizen.setText("Notizen:");
    jLNotizen.setFont(new Font("MS Sans Serif", Font.BOLD, 15));
    jPNotizen.add(jLNotizen);
    jPNotizen.setBorder(BorderFactory.createMatteBorder(1, 0, 2, 0,
                                                        Color.DARK_GRAY));
    jSPNotizen.setBounds(8, 32, 409, 81);
    jPNotizen.add(jSPNotizen);
    jTANotizen.setBounds(-2, -2, 409, 81);
    jTANotizen.setText("");
    jTANotizen.setLineWrap(true);
    jSPNotizen.setViewportView(jTANotizen);
    jPControl.setBounds(16, 384, 865, 41);
    cp.add(jPControl);
    jBLoeschen.setBounds(756, 8, 100, 25);
    jBLoeschen.setText("Löschen");
    jBLoeschen.setMargin(new Insets(2, 2, 2, 2));
    jBLoeschen.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          jBLoeschen_ActionPerformed(evt);
        }
      });
    jBLoeschen.setText("Löschen");
    jPControl.add(jBLoeschen);
    jBUebernehmen.setBounds(650, 8, 100, 25);
    jBUebernehmen.setText("Speichern");
    jBUebernehmen.setMargin(new Insets(2, 2, 2, 2));
    jBUebernehmen.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          jBUebernehmen_ActionPerformed();
        }
      });
    jBUebernehmen.setText("Speichern");
    jPControl.add(jBUebernehmen);
    jLMandatoryInfo.setBounds(8, 8, 335, 16);
    jLMandatoryInfo.setText("* mit Sternchen gekennzeichnete Felder sind Pflichtfelder");
    jLMandatoryInfo.setFont(new Font("MS Sans Serif", Font.PLAIN, 11));
    jPControl.add(jLMandatoryInfo);
    jPNavigation.setBounds(456, 8, 425, 25);
    cp.add(jPNavigation);
    jBNext.setBounds(112, 0, 25, 25);
    jBNext.setText(">");
    jBNext.setMargin(new Insets(2, 2, 2, 2));
    jBNext.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          jBNext_ActionPerformed(evt);
        }
      });
    jPNavigation.add(jBNext);
    jBPrevious.setBounds(80, 0, 25, 25);
    jBPrevious.setText("<");
    jBPrevious.setMargin(new Insets(2, 2, 2, 2));
    jBPrevious.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          jBPrevious_ActionPerformed(evt);
        }
      });
    jPNavigation.add(jBPrevious);
    jLNavigation.setBounds(8, 4, 68, 16);
    jLNavigation.setText("Navigation:");
    jLNavigation.setFont(new Font("MS Sans Serif", Font.PLAIN, 13));
    jPNavigation.add(jLNavigation);
    jBSuchen.setBounds(336, 0, 84, 25);
    jBSuchen.setText("Suchen");
    jBSuchen.setMargin(new Insets(2, 2, 2, 2));
    jBSuchen.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          jBSuchen_ActionPerformed(evt);
        }
      });
    jPNavigation.add(jBSuchen);
    jLPosition.setBounds(156, 4, 51, 16);
    jLPosition.setText("Position:");
    jLPosition.setFont(new Font("MS Sans Serif", Font.PLAIN, 13));
    jPNavigation.add(jLPosition);
    jLPosition_nr.setBounds(210, 4, 116, 16);
    jLPosition_nr.setText("_/_");
    jLPosition_nr.setFont(new Font("MS Sans Serif", Font.BOLD, 13));
    jPNavigation.add(jLPosition_nr);
    jPTitel.setBounds(16, 8, 425, 25);
    cp.add(jPTitel);
    jLEingabeformular.setBounds(6, 1, 281, 24);
    jLEingabeformular.setText("Adresse & Zusatzinformationen");
    jLEingabeformular.setFont(new Font("MS Sans Serif", Font.BOLD, 17));
    jPTitel.add(jLEingabeformular);
    jBNeu.setBounds(553, 8, 91, 25);
    jBNeu.setText("Neu anlegen");
    jBNeu.setMargin(new Insets(2, 2, 2, 2));
    jBNeu.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          jBNeu_ActionPerformed(evt);
        }
      });
    jPControl.add(jBNeu);
    setResizable(false);
    System.out.println("show user interface");
    setVisible(false);
  }

  /**
    * Gibt zurück welche Auswahl in einer ButtonGroup getroffen wurde
    */
  public String getSelectedRadioButton(ButtonGroup bg) {
    for (java.util.Enumeration<AbstractButton> e = bg.getElements();
           e.hasMoreElements();) {
      AbstractButton b = (JRadioButton) e.nextElement();

      if (b.isSelected()) {
        return b.getText();
      }
    }

    return null;
  }

  /**
    * Wird aktiviert, wenn der 'Löschen' - Knopf betätigt wird
    * Löscht alle Felder bei Neuanlage, löscht den ausgewählten Datensatz ansonsten
    * @param evt ActionEvent
    */
  public void jBLoeschen_ActionPerformed(ActionEvent evt) {
    if (jLPosition_nr.getText().equals("neu")) {
      int answer = JOptionPane.showConfirmDialog(this,
                                                 "Sind sie sicher, dass" +
                                                 " Sie die Inhalte aller Felder löschen möchten?",
                                                 "Alle Inhalte löschen?",
                                                 JOptionPane.YES_NO_OPTION);

      if (answer == JOptionPane.YES_OPTION) {
        clearAllFields();
      }
    } else {
      int answer = JOptionPane.showConfirmDialog(this,
                                                 "Sind sie sicher, dass" +
                                                 " Sie diesen Kontakt entgültig löschen möchten?",
                                                 "Kontakt löschen?",
                                                 JOptionPane.YES_NO_OPTION);

      if (answer == JOptionPane.YES_OPTION) {
        try {
          dc.resultSet.last();

          if (dc.resultSet.getRow() != 0) {
            dc.resultSet.deleteRow();
            dc.connection.commit();
          }

          dc.select();
          dc.resultSet.first();

          if (dc.resultSet.getRow() != 0) {
            fillEingabeFormular(dc.resultSet);

            int pos = dc.resultSet.getRow();
            dc.resultSet.last();

            int count = dc.resultSet.getRow();
            dc.resultSet.absolute(pos);
            jLPosition_nr.setText("" + pos + " / " + count);
          } else {
            clearAllFields();
            jLPosition_nr.setText("leere Datenbank");
          }
        } catch (SQLException sqle) {
          sqle.printStackTrace(System.err);
          System.out.println(sqle.getMessage());
        }
      }
    }
  }

  /**
    * Bereinigt alle Eingabefelder, löscht alle Werte auf der Maske
    */
  public void clearAllFields() {
    jTFName.setText("");
    jTFName.setBackground(Color.WHITE);
    jTFName.setForeground(Color.BLACK);

    jTFVorname.setText("");
    jTFVorname.setBackground(Color.WHITE);
    jTFVorname.setForeground(Color.BLACK);

    jTFPLZ.setText("");
    jTFPLZ.setBackground(Color.WHITE);
    jTFPLZ.setForeground(Color.BLACK);

    jTFOrt.setText("");
    jTFOrt.setBackground(Color.WHITE);
    jTFOrt.setForeground(Color.BLACK);

    jTFStrasse.setText("");
    jTFStrasse.setBackground(Color.WHITE);
    jTFStrasse.setForeground(Color.BLACK);

    jTAWeitere.setText("");
    jTFRegion.setText("");
    jTFLand.setText("");
    jTFTelefon.setText("");
    jTFFax.setText("");
    jTFE_Mail.setText("");
    jTANotizen.setText("");

    bGGeschlecht.clearSelection();
    jRBMaennlich.setBackground(jRBMaennlich.getTopLevelAncestor().getBackground());
    jRBMaennlich.setForeground(Color.BLACK);
    jRBWeiblich.setBackground(jRBMaennlich.getTopLevelAncestor().getBackground());
    jRBWeiblich.setForeground(Color.BLACK);

    jCBProgrammieren.setSelected(false);
    jCBBetriebssysteme.setSelected(false);
    jCBNetzwerke.setSelected(false);

    jLEingabeformular.setForeground(Color.BLACK);
    jLEingabeformular.setText("Adresse & Zusatzinformationen");
  }

  /**
    * Wird ausgeführt, wenn der 'Speichern'-Knopf betätigt wird
    * Speichert den Datensatz
    * @param evt ActionEvent
    */
  public void jBUebernehmen_ActionPerformed() {
    if (areMandatoryFieldsFilled()) {
      try {
        if (jLPosition_nr.getText().equals("neu") ||
              (dc.resultSet.getRow() == 0)) {
          insertNewPerson();
          dc.select();
          dc.resultSet.last();
        } else {
          updatePerson();
        }

        int pos = dc.resultSet.getRow();
        dc.resultSet.last();

        int count = dc.resultSet.getRow();
        dc.resultSet.absolute(pos);
        jLPosition_nr.setText(pos + " / " + count);
        jBNeu.setVisible(true);
        jBAbbrechen.setVisible(false);
      } catch (SQLException sqle) {
        sqle.printStackTrace(System.err);
      }
    }
  }

  /**
    * Ersetzt alle Semikolons und Zeilenumbrüche,
    * da diese als Trennzeichen der CSV-Datei besetzt sind
    * @param s
    */
  public String replaceSemicolonsAndLineBreaks(String s) {
    s = s.replaceAll(";", "%SEMICOLON%");
    s = s.replaceAll("[\r\n]", "%LINEBREAK%");

    return s;
  }

  /**
    * Speichert einen neuen Kontakt.
    */
  public void insertNewPerson() {
    Vector<Comparable> data = new Vector<Comparable>();
    data.addElement((String) replaceSemicolonsAndLineBreaks(jTFVorname.getText()));
    data.addElement((String) replaceSemicolonsAndLineBreaks(jTFName.getText()));
    data.addElement((String) replaceSemicolonsAndLineBreaks(jTFStrasse.getText()));
    data.addElement((String) replaceSemicolonsAndLineBreaks(jTFPLZ.getText()));
    data.addElement((String) replaceSemicolonsAndLineBreaks(jTFOrt.getText()));
    data.addElement((String) replaceSemicolonsAndLineBreaks(jTFRegion.getText()));
    data.addElement((String) replaceSemicolonsAndLineBreaks(jTFLand.getText()));

    if (jRBMaennlich.isSelected()) {
      data.addElement(new String("männlich"));
    } else {
      data.addElement(new String("weiblich"));
    }

    data.addElement((String) replaceSemicolonsAndLineBreaks(jTFTelefon.getText()));
    data.addElement((String) replaceSemicolonsAndLineBreaks(jTFFax.getText()));
    data.addElement((String) replaceSemicolonsAndLineBreaks(jTFE_Mail.getText()));
    data.addElement((Boolean) jCBProgrammieren.isSelected());
    data.addElement((Boolean) jCBBetriebssysteme.isSelected());
    data.addElement((Boolean) jCBNetzwerke.isSelected());
    data.addElement((String) replaceSemicolonsAndLineBreaks(jTAWeitere.getText()));
    data.addElement((String) replaceSemicolonsAndLineBreaks(jTANotizen.getText()));
    dc.insert(data);
  }

  /**
    * Überprüft ob alle Pflichtfelder gefüllt sind.
    */
  public boolean areMandatoryFieldsFilled() {
    boolean notAllMandatoryFieldsAreFilled = false;
    Color manadatoryFieldColor = new Color(255, 200, 200);

    if (jTFVorname.getText().length() == 0) {
      jTFVorname.setBackground(manadatoryFieldColor);
      notAllMandatoryFieldsAreFilled = true;
    } else {
      jTFVorname.setBackground(Color.WHITE);
    }

    if (jTFName.getText().length() == 0) {
      jTFName.setBackground(manadatoryFieldColor);
      notAllMandatoryFieldsAreFilled = true;
    } else {
      jTFName.setBackground(Color.WHITE);
    }

    if (jTFPLZ.getText().length() == 0) {
      jTFPLZ.setBackground(manadatoryFieldColor);
      notAllMandatoryFieldsAreFilled = true;
    } else {
      String plz = jTFPLZ.getText();
      boolean notAllAreFigures = false;

      for (int i = 0; i < plz.length(); i++) {
        if ((plz.charAt(i) < '0') || (plz.charAt(i) > '9')) {
          notAllAreFigures = true;
        }
      }

      if (notAllAreFigures) {
        JOptionPane.showMessageDialog(this,
                                      "Die Postleitzahl muss aus Ziffern bestehen!",
                                      "PLZ hat den falschen Format",
                                      JOptionPane.ERROR_MESSAGE);
        jTFPLZ.setBackground(manadatoryFieldColor);
        notAllMandatoryFieldsAreFilled = true;
      } else {
        jTFPLZ.setBackground(Color.WHITE);
      }
    }

    if (jTFOrt.getText().length() == 0) {
      jTFOrt.setBackground(manadatoryFieldColor);
      notAllMandatoryFieldsAreFilled = true;
    } else {
      jTFOrt.setBackground(Color.WHITE);
    }

    if (jTFStrasse.getText().length() == 0) {
      jTFStrasse.setBackground(manadatoryFieldColor);
      notAllMandatoryFieldsAreFilled = true;
    } else {
      jTFStrasse.setBackground(Color.WHITE);
    }

    if (!jRBMaennlich.isSelected() && !jRBWeiblich.isSelected()) {
      jRBMaennlich.setBackground(manadatoryFieldColor);
      jRBWeiblich.setBackground(manadatoryFieldColor);
      notAllMandatoryFieldsAreFilled = true;
    } else {
      jRBMaennlich.setBackground(jRBMaennlich.getTopLevelAncestor()
                                             .getBackground());
      jRBMaennlich.setForeground(Color.BLACK);
      jRBWeiblich.setBackground(jRBMaennlich.getTopLevelAncestor()
                                            .getBackground());
      jRBWeiblich.setForeground(Color.BLACK);
    }

    //
    if (notAllMandatoryFieldsAreFilled) {
      jLEingabeformular.setForeground(Color.RED);
      jLEingabeformular.setText("Bitte alle Pflicht-Felder ausfüllen!");

      return false;
    } else {
      jLEingabeformular.setForeground(Color.BLACK);
      jLEingabeformular.setText("Adresse & Zusatzinformationen");

      return true;
    }
  }

  /**
    * Wechselt zum nächsten Datensatz. Bei dem letzten Datensatz passiert nichts.
    * @param evt ActionEvent
    */
  public void jBNext_ActionPerformed(ActionEvent evt) {
    int option = JOptionPane.YES_OPTION;

    if (isSomethingChanged() || jBAbbrechen.isVisible()) {
      option = saveOrCancel("Wollen Sie die Änderungen speichern?",
                            "Änderungen speichern?");
    }

    if (option != JOptionPane.CANCEL_OPTION) {
      try {
        if (dc.resultSet.next()) {
          clearAllFields();
          fillEingabeFormular(dc.resultSet);

          int pos = dc.resultSet.getRow();
          dc.resultSet.last();

          int count = dc.resultSet.getRow();
          dc.resultSet.absolute(pos);
          jLPosition_nr.setText("" + pos + " / " + count);
          areMandatoryFieldsFilled();
          jBNeu.setVisible(true);
          jBAbbrechen.setVisible(false);
        } else {
          dc.resultSet.previous();
          clearAllFields();
          fillEingabeFormular(dc.resultSet);

          int pos = dc.resultSet.getRow();
          dc.resultSet.last();

          int count = dc.resultSet.getRow();
          dc.resultSet.absolute(pos);
          jLPosition_nr.setText("" + pos + " / " + count);
          areMandatoryFieldsFilled();
          jBNeu.setVisible(true);
          jBAbbrechen.setVisible(false);
        }
      } catch (SQLException sqle) {
        sqle.printStackTrace();
      }
    }
  }

  /**
    * Wechselt zum vorhergehenden Datensatz. Bei dem ersten passiert nichts.
    */
  public void jBPrevious_ActionPerformed(ActionEvent evt) {
    System.out.println("jBPrevious_ActionPerformed_TO_" + currentPos);

    int option = JOptionPane.YES_OPTION;

    if (isSomethingChanged() || jBAbbrechen.isVisible()) {
      option = saveOrCancel("Wollen Sie die Änderungen speichern?",
                            "Änderungen speichern?");
    }

    if (option != JOptionPane.CANCEL_OPTION) {
      try {
        if (dc.resultSet.previous()) {
          clearAllFields();
          fillEingabeFormular(dc.resultSet);

          int pos = dc.resultSet.getRow();
          dc.resultSet.last();

          int count = dc.resultSet.getRow();
          dc.resultSet.absolute(pos);
          jLPosition_nr.setText("" + pos + " / " + count);
          areMandatoryFieldsFilled();
          jBNeu.setVisible(true);
          jBAbbrechen.setVisible(false);
        } else {
          dc.resultSet.next();
          clearAllFields();
          fillEingabeFormular(dc.resultSet);

          int pos = dc.resultSet.getRow();
          dc.resultSet.last();

          int count = dc.resultSet.getRow();
          dc.resultSet.absolute(pos);
          jLPosition_nr.setText("" + pos + " / " + count);
          areMandatoryFieldsFilled();
          jBNeu.setVisible(true);
          jBAbbrechen.setVisible(false);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
    * Öffnet die Suchmaske
    * @param evt ActionEvent
    */
  public void jBSuchen_ActionPerformed(ActionEvent evt) {
    int option = JOptionPane.YES_OPTION;

    if (isSomethingChanged() || jBAbbrechen.isVisible()) {
      option = saveOrCancel("Wollen Sie die Änderungen speichern?",
                            "Änderungen speichern?");
      jBPrevious_ActionPerformed(evt);
    }

    if (option != JOptionPane.CANCEL_OPTION) {
      jBAbbrechen.setVisible(false);
      jBNeu.setVisible(true);

      SuchFormular suchFormular = new SuchFormular(this, "Kontakt suchen", true);
    }
  }

  /**
    * Bereinigt die Maske zur erfassung eines neuen Kontakts.
    * @param evt ActionEvent
    */
  public void jBNeu_ActionPerformed(ActionEvent evt) {
    int option = JOptionPane.YES_OPTION;

    if (isSomethingChanged()) {
      option = saveOrCancel("Wollen Sie die Änderungen speichern?",
                            "Änderungen speichern?");
    }

    if (option != JOptionPane.CANCEL_OPTION) {
      clearAllFields();
      jLPosition_nr.setText("neu");
      jBAbbrechen.setVisible(true);
      jBNeu.setVisible(false);
    }
  }

  /**
    * Fragt den Benutzer bei ungespeicherten Änderungen, ob er verwerfen oder
    * diese speichern möchten.
    * @param message
    * @param title
    */
  public int saveOrCancel(String message, String title) {
    switch (JOptionPane.showConfirmDialog(this, message, title,
                                          JOptionPane.YES_NO_CANCEL_OPTION,
                                          JOptionPane.QUESTION_MESSAGE)) {
      case JOptionPane.YES_OPTION:

        if (areMandatoryFieldsFilled()) {
          if (jLPosition_nr.getText().equals("neu")) {
            insertNewPerson();
          } else {
            updatePerson();
          }

          clearAllFields();

          return JOptionPane.YES_OPTION;
        } else {
          return JOptionPane.CANCEL_OPTION;
        }

      case JOptionPane.NO_OPTION:
        clearAllFields();

        return JOptionPane.NO_OPTION;

      default:
        return JOptionPane.CANCEL_OPTION;
    }
  }

  /**
    * Überprüft ob der Benutzer den aktuellen Datensatz verändert hat.
    */
  public boolean isSomethingChanged() {
    boolean somethingChanged = false;

    try {
      if (dc.resultSet.getRow() > 0) {
        if (!jTFVorname.getText().equals(dc.resultSet.getString("vorname"))) {
          somethingChanged = true;
          System.out.println("Vorname geändert");
        }

        if (!jTFName.getText().equals(dc.resultSet.getString("name"))) {
          somethingChanged = true;
          System.out.println("Name geändert");
        }

        if (!jTFStrasse.getText().equals(dc.resultSet.getString("strasse"))) {
          somethingChanged = true;
          System.out.println("Strasse geändert");
        }

        if (!jTFPLZ.getText().equals(dc.resultSet.getString("plz"))) {
          somethingChanged = true;
          System.out.println("PLZ geändert");
        }

        if (!jTFOrt.getText().equals(dc.resultSet.getString("ort"))) {
          somethingChanged = true;
          System.out.println("Ort geändert");
        }

        if (!jTFRegion.getText().equals(dc.resultSet.getString("region"))) {
          somethingChanged = true;
          System.out.println("Region geändert");
        }

        if (!jTFLand.getText().equals(dc.resultSet.getString("land"))) {
          somethingChanged = true;
          System.out.println("Land geändert");
        }

        if ((jRBMaennlich.isSelected() != (dc.resultSet.getString("geschlecht")
                                                         .equals("männlich"))) ||
              (jRBWeiblich.isSelected() != (dc.resultSet.getString("geschlecht")
                                                          .equals("weiblich")))) {
          somethingChanged = true;
          System.out.println("Geschlecht geändert");
        }

        if (!jTFTelefon.getText().equals(dc.resultSet.getString("telefon"))) {
          somethingChanged = true;
          System.out.println("Telefon geändert");
        }

        if (!jTFFax.getText().equals(dc.resultSet.getString("telefax"))) {
          somethingChanged = true;
          System.out.println("Fax geändert");
        }

        if (!jTFE_Mail.getText().equals(dc.resultSet.getString("e_mail"))) {
          somethingChanged = true;
          System.out.println("E-Mail geändert");
        }

        if (jCBProgrammieren.isSelected() != (dc.resultSet.getBoolean("programmieren"))) {
          somethingChanged = true;
          System.out.println("Programmieren geändert");
        }

        if (jCBBetriebssysteme.isSelected() != (dc.resultSet.getBoolean("betriebssysteme"))) {
          somethingChanged = true;
          System.out.println("Betriebssysteme geändert");
        }

        if (jCBNetzwerke.isSelected() != (dc.resultSet.getBoolean("netzwerke"))) {
          somethingChanged = true;
          System.out.println("Netzwerke geändert");
        }

        if (!jTAWeitere.getText()
                         .equals(dc.resultSet.getString("weitere_kenntnisse"))) {
          somethingChanged = true;
          System.out.println("Weitere Kenntnisse geändert");
        }

        if (!jTANotizen.getText().equals(dc.resultSet.getString("notizen"))) {
          somethingChanged = true;
          System.out.println("Notizen geändert");
        }
      }
    } catch (SQLException sqle) {
      sqle.printStackTrace();
    }

    return somethingChanged;
  }

  /**
    * Speichert einen bereits existierenden Datensatz. Sinnvoll bei Änderungen.
    */
  public void updatePerson() {
    try {
      System.out.println("update");
      dc.resultSet.updateString("vorname",
                                replaceSemicolonsAndLineBreaks(jTFVorname.getText()));
      dc.resultSet.updateString("name",
                                replaceSemicolonsAndLineBreaks(jTFName.getText()));
      dc.resultSet.updateString("strasse",
                                replaceSemicolonsAndLineBreaks(jTFStrasse.getText()));
      dc.resultSet.updateString("plz",
                                replaceSemicolonsAndLineBreaks(jTFPLZ.getText()));
      dc.resultSet.updateString("ort",
                                replaceSemicolonsAndLineBreaks(jTFOrt.getText()));
      dc.resultSet.updateString("region",
                                replaceSemicolonsAndLineBreaks(jTFRegion.getText()));
      dc.resultSet.updateString("land",
                                replaceSemicolonsAndLineBreaks(jTFLand.getText()));

      if (jRBMaennlich.isSelected()) {
        dc.resultSet.updateString("geschlecht", "männlich");
      } else {
        dc.resultSet.updateString("geschlecht", "weiblich");
      }

      dc.resultSet.updateString("telefon",
                                replaceSemicolonsAndLineBreaks(jTFTelefon.getText()));
      dc.resultSet.updateString("telefax",
                                replaceSemicolonsAndLineBreaks(jTFFax.getText()));
      dc.resultSet.updateString("e_mail",
                                replaceSemicolonsAndLineBreaks(jTFE_Mail.getText()));

      dc.resultSet.updateBoolean("Programmieren", jCBProgrammieren.isSelected());
      dc.resultSet.updateBoolean("Betriebssysteme",
                                 jCBBetriebssysteme.isSelected());

      dc.resultSet.updateBoolean("Netzwerke", jCBNetzwerke.isSelected());

      dc.resultSet.updateString("weitere_kenntnisse",
                                replaceSemicolonsAndLineBreaks(jTAWeitere.getText()));
      dc.resultSet.updateString("notizen",
                                replaceSemicolonsAndLineBreaks(jTANotizen.getText()));
      dc.resultSet.updateRow();
      System.out.println("update ... " + dc.resultSet.rowUpdated());

      int pos = dc.resultSet.getRow();
      dc.connection.commit();
      dc.select();
      dc.resultSet.absolute(pos);
    } catch (SQLException sqle) {
      sqle.printStackTrace(System.err);
    }
  }

  /**
    * Speichert alle Datensätze in eine Datei.
    */
  public void storeData() {
    try {
      csvFile.setLength(0);
      csvFile.seek(0);

      if ((lines != null) && (lines.size() > 0)) {
        System.out.println("store data");

        for (int i = 0; i < lines.size(); i++) {
          csvFile.writeBytes(((String[]) lines.elementAt(i))[0]);
          csvFile.writeBytes(";");
          csvFile.writeBytes(((String[]) lines.elementAt(i))[1]);
          csvFile.writeBytes(";");
          csvFile.writeBytes(((String[]) lines.elementAt(i))[2]);
          csvFile.writeBytes(";");
          csvFile.writeBytes(((String[]) lines.elementAt(i))[3]);
          csvFile.writeBytes(";");
          csvFile.writeBytes(((String[]) lines.elementAt(i))[4]);
          csvFile.writeBytes(";");
          csvFile.writeBytes(((String[]) lines.elementAt(i))[5]);
          csvFile.writeBytes(";");
          csvFile.writeBytes(((String[]) lines.elementAt(i))[6]);
          csvFile.writeBytes(";");
          csvFile.writeBytes(((String[]) lines.elementAt(i))[7]);
          csvFile.writeBytes(";");
          csvFile.writeBytes(((String[]) lines.elementAt(i))[8]);
          csvFile.writeBytes(";");
          csvFile.writeBytes(((String[]) lines.elementAt(i))[9]);
          csvFile.writeBytes(";");
          csvFile.writeBytes(((String[]) lines.elementAt(i))[10]);
          csvFile.writeBytes(";");
          csvFile.writeBytes(((String[]) lines.elementAt(i))[11]);
          csvFile.writeBytes(";");
          csvFile.writeBytes(((String[]) lines.elementAt(i))[12]);
          csvFile.writeBytes(";");
          csvFile.writeBytes(((String[]) lines.elementAt(i))[13]);
          csvFile.writeBytes(";");
          csvFile.writeBytes(((String[]) lines.elementAt(i))[14]);
          csvFile.writeBytes(";");
          csvFile.writeBytes(((String[]) lines.elementAt(i))[15]);
          csvFile.writeBytes("\r\n");
        }
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this,
                                    "Konnte nicht gespeichert werden: " +
                                    e.getMessage(), "Fehler",
                                    JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
    * Suchmaske
    * @author Peter Kress
    * @version 1.0
    */
  class SuchFormular extends JDialog {
    // Anfang Attribute1
    private JTextField suchwort;
    private JComboBox<?> cb;
    private JTable jt;
    private DefaultTableModel dtm;
    private Adressbuch parent;
    private HashMap<Integer, Integer> indizes;

    // Ende Attribute1

    /**
      * Kostruktor
      */
    public SuchFormular(Adressbuch parent, String title, boolean modal) {
      super(parent, title, modal);
      this.parent = parent;
      setSize(800, 260);
      setLocationRelativeTo(parent);

      Container container = this.getContentPane();
      JLabel suchenIn = new JLabel("Suchen in:");
      suchenIn.setBounds(10, 10, 180, 16);
      container.add(suchenIn);

      JLabel suchenNach = new JLabel("Suchen nach:");
      suchenNach.setBounds(192, 10, 280, 16);
      container.add(suchenNach);

      suchwort = new JTextField("");
      container.setLayout(null);
      suchwort.setBounds(192, 30, 488, 25);
      suchwort.addKeyListener(new KeyAdapter() {
          public void keyReleased(KeyEvent e) {
            suche();
          }
        });
      container.add(suchwort);

      JButton jBLeeren = new JButton("Löschen");
      jBLeeren.setBounds(684, 30, 97, 25);
      jBLeeren.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            suchwort.setText("");
            indizes = null;
            suche();
          }
        });
      container.add(jBLeeren);

      String[] columnNames = {
                               "ID", "Vorname", "Nachname", "Straße", "PLZ",
                               "Ort", "Region", "Land", "Geschlecht", "Telefon",
                               "Fax", "E-Mail", "Programmieren",
                               "Betriebssysteme", "Netzwerke",
                               "weitere Kenntnisse", "Notizen"
                             };
      cb = new JComboBox<Object>(columnNames);
      cb.setBounds(10, 30, 180, 25);
      cb.addItemListener(new ItemListener() {
          public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() != ItemEvent.SELECTED) {
              suche();
            }
          }
        });

      container.add(cb);

      int count = -1;
      int pos = -1;

      try {
        pos = dc.resultSet.getRow();
        dc.resultSet.last();
        count = dc.resultSet.getRow();
        dc.resultSet.absolute(pos);
      } catch (SQLException sqle) {
        sqle.printStackTrace(System.err);
        System.out.println(sqle.getMessage());
      }

      if (count <= 0) {
        count = 0;
      }

      Object[][] data = new Object[count][17];
      String[] tmp;
      int n = 0;

      try {
        dc.resultSet.beforeFirst();

        while (dc.resultSet.next()) {
          System.out.println(dc.resultSet);
          tmp = new String[17];
          tmp[0] = String.valueOf(dc.resultSet.getInt(1));
          tmp[1] = String.valueOf(dc.resultSet.getString(2));
          tmp[2] = String.valueOf(dc.resultSet.getString(3));
          tmp[3] = String.valueOf(dc.resultSet.getString(4));
          tmp[4] = String.valueOf(dc.resultSet.getString(5));

          tmp[5] = String.valueOf(dc.resultSet.getString(6));
          tmp[6] = String.valueOf(dc.resultSet.getString(7));
          tmp[7] = String.valueOf(dc.resultSet.getString(8));
          tmp[8] = String.valueOf(dc.resultSet.getString(9));
          tmp[9] = String.valueOf(dc.resultSet.getString(10));

          tmp[10] = String.valueOf(dc.resultSet.getString(11));
          tmp[11] = String.valueOf(dc.resultSet.getString(12));

          tmp[12] = String.valueOf(dc.resultSet.getBoolean(13));
          tmp[13] = String.valueOf(dc.resultSet.getBoolean(14));
          tmp[14] = String.valueOf(dc.resultSet.getBoolean(15));

          tmp[15] = String.valueOf(dc.resultSet.getString(16));
          tmp[16] = String.valueOf(dc.resultSet.getString(17));

          data[n] = tmp;
          n++;
        }
      } catch (SQLException sqle) {
        sqle.printStackTrace(System.err);
        System.out.println(sqle.getMessage());
      }

      //      for (int i = 0; i < lines.size(); i++) {
      //        data[i] = (String[]) parent.lines.elementAt(i);
      //      }
      dtm = new DefaultTableModel(data, columnNames) {
          public boolean isCellEditable(int row, int column) {
            return false;
          }
        };
      jt = new JTable(dtm);
      jt.getColumnModel().setColumnMargin(10);
      jt.setGridColor(Color.lightGray);
      jt.setSelectionMode(0);
      jt.setSelectionBackground(Color.WHITE);
      jt.setSelectionForeground(Color.BLUE);
      jt.setShowHorizontalLines(true);
      jt.setShowVerticalLines(false);
      jt.setFocusable(false);
      jt.addMouseListener(new MouseAdapter() {
          public void mouseReleased(MouseEvent me) { //must be mouseReleased

            if (jt.getSelectedRow() >= 0) {
              try {
                if (indizes == null) {
                  dc.resultSet.absolute(jt.getSelectedRow() + 1);
                } else {
                  dc.resultSet.absolute(indizes.get((Integer) jt.getSelectedRow()));
                }
              } catch (SQLException sqle) {
                sqle.printStackTrace();
              }

              fillEingabeFormular(); /*jt.getSelectedRow());*/

              if (me.getClickCount() == 2) {
                dispose();
              }
            }

            System.out.println("Mouse on row " + jt.getSelectedRow());
          }
        });

      JScrollPane sp = new JScrollPane(jt);
      sp.setBounds(10, 60, 770, 162);
      jt.setFillsViewportHeight(true);
      jt.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

      for (int i = 0; i < jt.getColumnModel().getColumnCount(); i++) {
        jt.getColumnModel().getColumn(i).setWidth(150);
        jt.getColumnModel().getColumn(i).setPreferredWidth(150);
      }

      sp.setBorder(BorderFactory.createBevelBorder(1));
      container.add(sp);
      this.setResizable(false);
      setVisible(true);

      // Anfang Komponenten1
      // Ende Komponenten1
    }

    // Anfang Methoden1

    /**
      * Sucht nach dem Suchwort in allen Datensätzen.
      * In der Ausgewählten Spalte.
      */
    public void suche() {
      ((DefaultTableModel) jt.getModel()).setRowCount(0);

      int spalte = cb.getSelectedIndex() + 1;
      indizes = new HashMap<Integer, Integer>();

      int j = 0;
      String origin;
      String find;

      try {
        dc.resultSet.beforeFirst();

        while (dc.resultSet.next()) {
          origin = dc.resultSet.getString(spalte).toLowerCase();
          find = suchwort.getText().toLowerCase();

          if (origin.contains(find.subSequence(0, suchwort.getText().length()))) {
            indizes.put(new Integer(j), new Integer(dc.resultSet.getRow()));
            j++;
          }
        }

        System.out.println("Found data, map: " + indizes.toString());

        String[] tmp;

        for (int i = 0; i < indizes.size(); i++) {
          dc.resultSet.absolute(((Integer) indizes.get(new Integer(i))).intValue());
          tmp = new String[17];
          tmp[0] = String.valueOf(dc.resultSet.getInt(1));
          tmp[1] = String.valueOf(dc.resultSet.getString(2));
          tmp[2] = String.valueOf(dc.resultSet.getString(3));
          tmp[3] = String.valueOf(dc.resultSet.getString(4));
          tmp[4] = String.valueOf(dc.resultSet.getString(5));

          tmp[5] = String.valueOf(dc.resultSet.getString(6));
          tmp[6] = String.valueOf(dc.resultSet.getString(7));
          tmp[7] = String.valueOf(dc.resultSet.getString(8));
          tmp[8] = String.valueOf(dc.resultSet.getString(9));
          tmp[9] = String.valueOf(dc.resultSet.getString(10));

          tmp[10] = String.valueOf(dc.resultSet.getString(11));
          tmp[11] = String.valueOf(dc.resultSet.getString(12));

          tmp[12] = String.valueOf(dc.resultSet.getBoolean(13));
          tmp[13] = String.valueOf(dc.resultSet.getBoolean(14));
          tmp[14] = String.valueOf(dc.resultSet.getBoolean(15));

          tmp[15] = String.valueOf(dc.resultSet.getString(16));
          tmp[16] = String.valueOf(dc.resultSet.getString(17));
          dtm.addRow(tmp);
        }
      } catch (SQLException sqle) {
        sqle.printStackTrace(System.err);
      }
    }

    /**
      * Befüllt die Adressmaske mit dem ausgewählten Datensatz aus der Suche.
      */
    public void fillEingabeFormular() {
      try {
        parent.clearAllFields();
        parent.fillEingabeFormular(dc.resultSet);

        int pos = dc.resultSet.getRow();
        dc.resultSet.last();

        int count = dc.resultSet.getRow();
        dc.resultSet.absolute(pos);
        parent.jLPosition_nr.setText(pos + " / " + count);
      } catch (SQLException sqle) {
        //
      }
    }

    // Ende Methoden1
  }

  /**
    * Wird ausgeführt, wenn der 'Abrechen'-Knopf betätigt wird.
    */
  public void jBAbbrechen_ActionPerformed(ActionEvent evt) {
    int answer = JOptionPane.showConfirmDialog(this,
                                               "Sind sie sicher, dass " +
                                               "Sie abbrechen möchten?",
                                               "Neuen Kontakt nicht speichern?",
                                               JOptionPane.YES_NO_OPTION);

    if (answer == JOptionPane.YES_OPTION) {
      try {
        clearAllFields();
        fillEingabeFormular(dc.resultSet);

        int pos = dc.resultSet.getRow();
        dc.resultSet.last();

        int count = dc.resultSet.getRow();
        dc.resultSet.absolute(pos);
        jLPosition_nr.setText("" + pos + " / " + count);
        jBNeu.setVisible(true);
        jBAbbrechen.setVisible(false);
      } catch (SQLException sqle) {
        //sqle
      }
    }
  }

  /**
    * Startpunkt für das Programm
    */
  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      e.printStackTrace();
    }

    new Adressbuch("Adressbuch Version 4.0");
  }

  // Ende Methoden
}
