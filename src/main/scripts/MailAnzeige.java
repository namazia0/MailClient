import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import java.io.IOException;

public class MailAnzeige {

    /**
     *JavaFx-Objekte aus MailAnzeige.fxml werden in Java deklariert
     * */
    @FXML
    public javafx.scene.control.Label Betreff1;
    @FXML
    public javafx.scene.control.Label Absender;
    @FXML
    public javafx.scene.control.Label Inhaltslabel;
    public javafx.scene.control.Label datum;
    public javafx.scene.control.ScrollPane Inhalt;
    public Mail tempMail;
    @FXML
    javafx.scene.control.Label archivFeedback;
    @FXML
    javafx.scene.control.Label LöschenFeedback;
    @FXML
    javafx.scene.control.Label foreverDeleteID;

    /**
     * Setzt die Werte der Mail-Anzeige mit den entsprechenden Parametern, so dass wir den richtigen Text nach dem anklicken der Mail sehen können.
     * @param titel Betreff der Mail
     * @param sender Absender der Mail
     * @param inhalt Nachricht der Mail
     * @param eingangsdatum Einagngsdatum der Mail
     * @param tempMail Java-Mail-Objekt der Email, damit man diese beim Löschen übergeben kann
     *
     * */
    public void setValues(String titel, String sender, String inhalt, String eingangsdatum, Mail tempMail){
        Betreff1.setText(titel);
        Absender.setText(sender);
        Inhaltslabel.setText(inhalt);
        datum.setText(eingangsdatum);
        Inhalt.setContent(Inhaltslabel);
        this.tempMail = tempMail;
    }

    /**
     * Erstellt ein Muell-Objekt, um die Mail mithilfe dieses Müll-Objektes in den Papierkorb zu verschieben.
     * Zusätzlich werden die Anzeige-Elemente, der Mail-Anzeige geleert, so dass man diese nicht mehr lesen kann
     * */
    public void LoeschenGedrueckt(MouseEvent mouseEvent) throws IOException {
        Muell tempMuell = new Muell(tempMail, "INBOX");
        tempMuell.start();
        Betreff1.setText("");
        Absender.setText("");
        Inhaltslabel.setText("");
        datum.setText("");
        archivFeedback.setText("");
        LöschenFeedback.setText("Nachricht wurde in den Papierkorb verschoben");
        LöschenFeedback.setTextFill(Color.GREEN);
    }

    /**
     * Erstellt ein ArchivMuell-Objekt, um die Mail mithilfe dieses Objektes in das Archiv zu verschieben
     * Zusätzlich werden die Anzeige-Elemente, der Mail-Anzeige geleert, so dass man diese nicht mehr in dem aktuell geöffneten Fenster lesen kann
     * */
    public void archivGedrueckt(MouseEvent mouseEvent) {
        ArchivMuell muell = new ArchivMuell(tempMail, "INBOX");
        muell.start();
        Betreff1.setText("");
        Absender.setText("");
        Inhaltslabel.setText("");
        datum.setText("");
        LöschenFeedback.setText("");
        archivFeedback.setText("Nachricht wurde archiviert");
        archivFeedback.setTextFill(Color.BLUE);
    }
}