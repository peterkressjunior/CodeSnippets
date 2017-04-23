import java.awt.*;
import java.awt.event.*;

import java.io.*;
import java.io.RandomAccessFile;

import java.util.*;
import javax.swing.*;
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
	private static final long serialVersionUID = 8390489697120937617L;
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
              insertNewPerson();
              storeData();
              System.exit(0);
            } else if (areMandatoryFieldsFilled()) {
              updatePerson();
              storeData();
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
    initCSVFile();
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
  }

  /**
    * List kontakte.csv ein, erstellt eine neue Datei, falls keine existiert
    *
    */
  protected void initCSVFile() {
    try {
      System.out.println("init csv file: ");

      File file = new File("kontakte.csv");

      if (!file.exists()) {
        System.out.println("create csv file: ");
        file.createNewFile();
      }

      csvFile = new RandomAccessFile(file, "rw");
      csvFile.seek(0);
      lines = new Vector<String[]>();

      System.out.println("read csv file");

      while (csvFile.getFilePointer() < csvFile.length()) {
        lines.addElement(csvFile.readLine().split(";", 16));
        currentPos++;

        if (((String[]) lines.elementAt(currentPos)).length == 16) {
          System.out.println("line " + lines.size() + ":\t\t" +
                             Arrays.toString((Object[]) lines.elementAt(currentPos)));
        } else {
          System.out.println("delete line " + lines.size() + ":\t\t" +
                             Arrays.toString((Object[]) lines.elementAt(currentPos)));
          lines.removeElementAt(currentPos);
          currentPos--;
        }
      }

      if ((currentPos > -1) &&
            (((String[]) lines.elementAt(currentPos)).length >= 16)) {
        currentPos = 0;
        System.out.println("fill form with current data" + ":\t\t" +
                           Arrays.toString((Object[]) lines.elementAt(currentPos)));
        fillEingabeFormular((String[]) lines.elementAt(currentPos));
        jLPosition_nr.setText("" + (currentPos + 1) + " / " + lines.size());
      } else {
        jLPosition_nr.setText("leere Datenbank");
      }
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
    * Füllt die Felder auf der Maske mit Werten
    * @param data Ein Datum aus den eingelesene Adressdaten
    */
  public void fillEingabeFormular(String[] data) {
    if (data.length == 16) {
      jTFVorname.setText(data[0]);
      jTFName.setText(data[1]);
      jTFStrasse.setText(data[2]);
      jTFPLZ.setText(data[3]);
      jTFOrt.setText(data[4]);
      jTFRegion.setText(data[5]);
      jTFLand.setText(data[6]);

      if (data[7].equals("weiblich")) {
        jRBWeiblich.setSelected(true);
      } else {
        jRBMaennlich.setSelected(true);
      }

      jTFTelefon.setText(data[8]);
      jTFFax.setText(data[9]);
      jTFE_Mail.setText(data[10]);

      if (data[11].equals("Programmieren")) {
        jCBProgrammieren.setSelected(true);
      }

      if (data[12].equals("Betriebssysteme")) {
        jCBBetriebssysteme.setSelected(true);
      }

      if (data[13].equals("Netzwerke")) {
        jCBNetzwerke.setSelected(true);
      }

      jTAWeitere.setText(data[14]);
      jTANotizen.setText(data[15]);
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
          jBUebernehmen_ActionPerformed(evt);
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
    setVisible(true);
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
    } else if ((lines != null) && (lines.size() > 0)) {
      int answer = JOptionPane.showConfirmDialog(this,
                                                 "Sind sie sicher, dass" +
                                                 " Sie diesen Kontakt entgültig löschen möchten?",
                                                 "Kontakt löschen?",
                                                 JOptionPane.YES_NO_OPTION);

      if (answer == JOptionPane.YES_OPTION) {
        lines.removeElementAt(currentPos);
        currentPos--;

        if (lines.size() > 0) {
          currentPos = 0;
        }

        if (lines.size() > 0) {
          fillEingabeFormular(((String[]) lines.elementAt(currentPos)));
          jLPosition_nr.setText("" + (currentPos + 1) + " / " + lines.size());
        } else {
          currentPos = -1;
          clearAllFields();
          jLPosition_nr.setForeground(Color.YELLOW);
          jLPosition_nr.setText("leere Datenbank");
        }

        storeData();
      }
    } else {
      int answer = JOptionPane.showConfirmDialog(this, "Inhalte löschen?",
                                                 "Felder leeren?",
                                                 JOptionPane.YES_NO_OPTION);

      if (answer == JOptionPane.YES_OPTION) {
        clearAllFields();
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
  public void jBUebernehmen_ActionPerformed(ActionEvent evt) {
    if (areMandatoryFieldsFilled()) {
      if (jLPosition_nr.getText().equals("neu") || (currentPos == -1)) {
        insertNewPerson();
        storeData();
        jLPosition_nr.setText("" +
                              ((currentPos <= 0) ? "1" : ("" + currentPos)) +
                              " / " + lines.size());
        jLPosition_nr.setForeground(Color.BLACK);
        jBNeu.setVisible(true);
        jBAbbrechen.setVisible(false);
      } else {
        updatePerson();
        storeData();
        fillEingabeFormular(((String[]) lines.elementAt(currentPos)));
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
    String[] data = new String[16];
    data[0] = replaceSemicolonsAndLineBreaks(jTFVorname.getText());
    data[1] = replaceSemicolonsAndLineBreaks(jTFName.getText());
    data[2] = replaceSemicolonsAndLineBreaks(jTFStrasse.getText());
    data[3] = replaceSemicolonsAndLineBreaks(jTFPLZ.getText());
    data[4] = replaceSemicolonsAndLineBreaks(jTFOrt.getText());
    data[5] = replaceSemicolonsAndLineBreaks(jTFRegion.getText());
    data[6] = replaceSemicolonsAndLineBreaks(jTFLand.getText());

    if (jRBMaennlich.isSelected()) {
      data[7] = "männlich";
    } else {
      data[7] = "weiblich";
    }

    data[8] = replaceSemicolonsAndLineBreaks(jTFTelefon.getText());
    data[9] = replaceSemicolonsAndLineBreaks(jTFFax.getText());
    data[10] = replaceSemicolonsAndLineBreaks(jTFE_Mail.getText());

    if (jCBProgrammieren.isSelected()) {
      data[11] = "Programmieren";
    } else {
      data[11] = "";
    }

    if (jCBBetriebssysteme.isSelected()) {
      data[12] = "Betriebssysteme";
    } else {
      data[12] = "";
    }

    if (jCBNetzwerke.isSelected()) {
      data[13] = "Netzwerke";
    } else {
      data[13] = "";
    }

    data[14] = replaceSemicolonsAndLineBreaks(jTAWeitere.getText());
    data[15] = replaceSemicolonsAndLineBreaks(jTANotizen.getText());

    lines.addElement(data);
    currentPos++;
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
      if (currentPos >= lines.size()) {
        currentPos = -1;
      }

      try {
        if ((currentPos < (lines.size() - 1)) &&
              (((String[]) lines.elementAt(currentPos + 1)).length == 16)) {
          clearAllFields();
          fillEingabeFormular(((String[]) lines.elementAt(++currentPos)));
          jLPosition_nr.setForeground(Color.BLACK);
          jLPosition_nr.setText("" + (currentPos + 1) + " / " + lines.size());
          areMandatoryFieldsFilled();
        } else if (lines.size() > 0) {
          clearAllFields();
          fillEingabeFormular(((String[]) lines.elementAt(currentPos)));
          jLPosition_nr.setForeground(Color.BLACK);
          jLPosition_nr.setText("" + (currentPos + 1) + " / " + lines.size());
          areMandatoryFieldsFilled();
        } else {
          jLPosition_nr.setForeground(Color.YELLOW);
          jLPosition_nr.setText("leere Datenbank");
        }

        jBNeu.setVisible(true);
        jBAbbrechen.setVisible(false);
      } catch (Exception e) {
        e.printStackTrace();
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
      if (currentPos < 0) {
        currentPos = lines.size() - 1;
      }

      if (currentPos == lines.size()) {
        currentPos = lines.size() - 1;
      }

      try {
        if ((currentPos > 0) &&
              (((String[]) lines.elementAt(currentPos - 1)).length == 16)) {
          clearAllFields();
          fillEingabeFormular(((String[]) lines.elementAt(--currentPos)));
          jLPosition_nr.setForeground(Color.BLACK);
          jLPosition_nr.setText("" + (currentPos + 1) + " / " + lines.size());
          areMandatoryFieldsFilled();
        } else if (lines.size() > 0) {
          clearAllFields();
          fillEingabeFormular(((String[]) lines.elementAt(currentPos)));
          jLPosition_nr.setForeground(Color.BLACK);
          jLPosition_nr.setText("" + (currentPos + 1) + " / " + lines.size());
          areMandatoryFieldsFilled();
        } else {
          jLPosition_nr.setForeground(Color.YELLOW);
          jLPosition_nr.setText("leere Datenbank");
        }

        jBNeu.setVisible(true);
        jBAbbrechen.setVisible(false);
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
    if ((lines != null) && (lines.size() > 0) && (currentPos != -1)) {
      int option = JOptionPane.YES_OPTION;

      if (isSomethingChanged() || jBAbbrechen.isVisible()) {
        option = saveOrCancel("Wollen Sie die Änderungen speichern?",
                              "Änderungen speichern?");
        jBPrevious_ActionPerformed(evt);
      }

      if (option != JOptionPane.CANCEL_OPTION) {
        jBAbbrechen.setVisible(false);
        jBNeu.setVisible(true);

        SuchFormular suchFormular = new SuchFormular(this, "Kontakt suchen",
                                                     true);
      }
    }
  }

  /**
    * Bereinigt die Maske zur erfassung eines neuen Kontakts.
    * @param evt ActionEvent
    */
  public void jBNeu_ActionPerformed(ActionEvent evt) {
    if (lines != null) {
      int option = JOptionPane.YES_OPTION;

      if (isSomethingChanged()) {
        option = saveOrCancel("Wollen Sie die Änderungen speichern?",
                              "Änderungen speichern?");
      }

      if (option != JOptionPane.CANCEL_OPTION) {
        clearAllFields();
        jLPosition_nr.setText("neu");
        jLPosition_nr.setForeground(Color.YELLOW);
        currentPos = lines.size();
        jBAbbrechen.setVisible(true);
        jBNeu.setVisible(false);
      }
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
          updatePerson();
          storeData();
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

    if ((lines != null) && (lines.size() > 0) && (currentPos < lines.size())) {
      if (!jTFVorname.getText()
                       .equals(((String[]) lines.elementAt(currentPos))[0])) {
        somethingChanged = true;
        System.out.println("Vorname geändert");
      }

      if (!jTFName.getText().equals(((String[]) lines.elementAt(currentPos))[1])) {
        somethingChanged = true;
        System.out.println("Name geändert");
      }

      if (!jTFStrasse.getText()
                       .equals(((String[]) lines.elementAt(currentPos))[2])) {
        somethingChanged = true;
        System.out.println("Strasse geändert");
      }

      if (!jTFPLZ.getText().equals(((String[]) lines.elementAt(currentPos))[3])) {
        somethingChanged = true;
        System.out.println("PLZ geändert");
      }

      if (!jTFOrt.getText().equals(((String[]) lines.elementAt(currentPos))[4])) {
        somethingChanged = true;
        System.out.println("Ort geändert");
      }

      if (!jTFRegion.getText()
                      .equals(((String[]) lines.elementAt(currentPos))[5])) {
        somethingChanged = true;
        System.out.println("Region geändert");
      }

      if (!jTFLand.getText().equals(((String[]) lines.elementAt(currentPos))[6])) {
        somethingChanged = true;
        System.out.println("Land geändert");
      }

      if ((jRBMaennlich.isSelected() != ((String[]) lines.elementAt(currentPos))[7].equals("männlich")) ||
            (jRBWeiblich.isSelected() != ((String[]) lines.elementAt(currentPos))[7].equals("weiblich"))) {
        somethingChanged = true;
        System.out.println("Geschlecht geändert");
      }

      if (!jTFTelefon.getText()
                       .equals(((String[]) lines.elementAt(currentPos))[8])) {
        somethingChanged = true;
        System.out.println("Telefon geändert");
      }

      if (!jTFFax.getText().equals(((String[]) lines.elementAt(currentPos))[9])) {
        somethingChanged = true;
        System.out.println("Fax geändert");
      }

      if (!jTFE_Mail.getText()
                      .equals(((String[]) lines.elementAt(currentPos))[10])) {
        somethingChanged = true;
        System.out.println("E-Mail geändert");
      }

      if (jCBProgrammieren.isSelected() != ((String[]) lines.elementAt(currentPos))[11].equals("Programmieren")) {
        somethingChanged = true;
        System.out.println("Programmieren geändert");
      }

      if (jCBBetriebssysteme.isSelected() != ((String[]) lines.elementAt(currentPos))[12].equals("Betriebssysteme")) {
        somethingChanged = true;
        System.out.println("Betriebssysteme geändert");
      }

      if (jCBNetzwerke.isSelected() != ((String[]) lines.elementAt(currentPos))[13].equals("Netzwerke")) {
        somethingChanged = true;
        System.out.println("Netzwerke geändert");
      }

      if (!jTAWeitere.getText()
                       .equals(((String[]) lines.elementAt(currentPos))[14])) {
        somethingChanged = true;
        System.out.println("Weitere Kenntnisse geändert");
      }

      if (!jTANotizen.getText()
                       .equals(((String[]) lines.elementAt(currentPos))[15])) {
        somethingChanged = true;
        System.out.println("Notizen geändert");
      }
    }

    return somethingChanged;
  }

  /**
    * Speichert einen bereits existierenden Datensatz. Sinnvoll bei Änderungen.
    */
  public void updatePerson() {
    String[] data = new String[16];
    data[0] = replaceSemicolonsAndLineBreaks(jTFVorname.getText());
    data[1] = replaceSemicolonsAndLineBreaks(jTFName.getText());
    data[2] = replaceSemicolonsAndLineBreaks(jTFStrasse.getText());
    data[3] = replaceSemicolonsAndLineBreaks(jTFPLZ.getText());
    data[4] = replaceSemicolonsAndLineBreaks(jTFOrt.getText());
    data[5] = replaceSemicolonsAndLineBreaks(jTFRegion.getText());
    data[6] = replaceSemicolonsAndLineBreaks(jTFLand.getText());

    if (jRBMaennlich.isSelected()) {
      data[7] = "männlich";
    } else {
      data[7] = "weiblich";
    }

    data[8] = replaceSemicolonsAndLineBreaks(jTFTelefon.getText());
    data[9] = replaceSemicolonsAndLineBreaks(jTFFax.getText());
    data[10] = jTFE_Mail.getText();

    if (jCBProgrammieren.isSelected()) {
      data[11] = "Programmieren";
    } else {
      data[11] = "";
    }

    if (jCBBetriebssysteme.isSelected()) {
      data[12] = "Betriebssysteme";
    } else {
      data[12] = "";
    }

    if (jCBNetzwerke.isSelected()) {
      data[13] = "Netzwerke";
    } else {
      data[13] = "";
    }

    data[14] = replaceSemicolonsAndLineBreaks(jTAWeitere.getText());
    data[15] = replaceSemicolonsAndLineBreaks(jTANotizen.getText());

    if (lines.size() > 0) {
      lines.setElementAt(data, currentPos);
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
    private JTextField suchwort;
    private JComboBox cb;
    private JTable jt;
    private DefaultTableModel dtm;
    private Adressbuch parent;
    private HashMap<Integer, Integer> indizes;

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
            suche();
          }
        });
      container.add(jBLeeren);

      String[] columnNames = {
                               "Vorname", "Nachname", "Straße", "PLZ", "Ort",
                               "Region", "Land", "Geschlecht", "Telefon", "Fax",
                               "E-Mail", "Programmieren", "Betriebssysteme",
                               "Netzwerke", "weitere Kenntnisse", "Notizen"
                             };
      cb = new JComboBox(columnNames);
      cb.setBounds(10, 30, 180, 25);
      cb.addItemListener(new ItemListener() {
          public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() != ItemEvent.SELECTED) {
              suche();
            }
          }
        });

      container.add(cb);

      Object[][] data = new Object[lines.size()][16];

      for (int i = 0; i < lines.size(); i++) {
        data[i] = (String[]) parent.lines.elementAt(i);
      }

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
              fillEingabeFormular(jt.getSelectedRow());

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
    }

    /**
      * Sucht nach dem Suchwort in allen Datensätzen.
      * In der Ausgewählten Spalte.
      */
    public void suche() {
      ((DefaultTableModel) jt.getModel()).setRowCount(0);

      int spalte = cb.getSelectedIndex();
      indizes = new HashMap<Integer, Integer>();

      int j = 0;
      String origin;
      String find;

      for (int i = 0; i < lines.size(); i++) {
        origin = ((String[]) lines.elementAt(i))[spalte].toLowerCase();
        find = suchwort.getText().toLowerCase();

        if (origin.contains(find.subSequence(0, suchwort.getText().length()))) {
          System.out.println("Found data, position: " + (i + 1));
          indizes.put(new Integer(j), new Integer(i));
          j++;
        }
      }

      System.out.println("Found data, map: " + indizes.toString());

      for (int i = 0; i < indizes.size(); i++) {
        dtm.addRow(((String[]) lines.elementAt(((Integer) indizes.get(new Integer(i))).intValue())));
      }
    }

    /**
      * Befüllt die Adressmaske mit dem ausgewählten Datensatz aus der Suche.
      */
    public void fillEingabeFormular(int row) {
      if ((indizes == null) || (indizes.size() == 0)) {
        parent.clearAllFields();
        parent.fillEingabeFormular(((String[]) lines.elementAt(row)));
        parent.currentPos = row;
        parent.jLPosition_nr.setText((row + 1) + " / " + lines.size());
        parent.jLPosition_nr.setForeground(Color.BLACK);
      } else {
        parent.clearAllFields();
        parent.fillEingabeFormular(((String[]) lines.elementAt(((Integer) indizes.get(new Integer(row))).intValue())));
        parent.currentPos = ((Integer) indizes.get(new Integer(row))).intValue();
        parent.jLPosition_nr.setText(((((Integer) indizes.get(new Integer(row))).intValue()) +
                                     1) + " / " + lines.size());
        parent.jLPosition_nr.setForeground(Color.BLACK);
      }
    }
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
      clearAllFields();
      currentPos--;

      if (currentPos < 0) {
        currentPos = 0;
      }

      if (lines.size() > 0) {
        fillEingabeFormular(((String[]) lines.elementAt(currentPos)));
        jLPosition_nr.setForeground(Color.BLACK);
        jLPosition_nr.setText("" + (currentPos + 1) + " / " + lines.size());
      } else {
        clearAllFields();
        jLPosition_nr.setForeground(Color.YELLOW);
        jLPosition_nr.setText("leere Datenbank");
      }

      jBNeu.setVisible(true);
      jBAbbrechen.setVisible(false);
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

    new Adressbuch("Adressbuch Ver3.0");
  }
}
