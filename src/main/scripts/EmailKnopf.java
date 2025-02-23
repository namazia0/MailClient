import javafx.scene.control.Label;
import java.io.IOException;

/***
 * Klasse zu der EmailKnopf.fxml
 * Jedem Knopf wird einen Style hinzugefügt.
 */

public class EmailKnopf {
    public javafx.scene.control.Button Knopf;
    public Label Absender;
    public Label Betreff;
    public Label Datum;
    public String derStyle;
    public Mail tempMail;

    public void KnopfGedrueckt() throws IOException {
        System.out.println("KnopfGedrückt");
    }

    /***
     * Jedem Parameter wird der entsprechende Label zugeordnet.
     * @param Absender1
     * @param Titel1
     * @param Datum1
     * @param Inhalt
     * @param colorCode
     * @param tempMail
     */
    public void setValue(String Absender1, String Titel1, String Datum1, String Inhalt, String colorCode, Mail tempMail){
        Absender.setText(Absender1);
        Betreff.setText(Titel1);
        Datum.setText(Datum1);
        this.tempMail = tempMail;

        Knopf.setStyle("-fx-alignment: CENTER-LEFT; -fx-background-color: #" + colorCode + "45"  + ";" + "-fx-font-weight: bold");
    }

    public void MausRein(){
        derStyle = Knopf.getStyle();
        Knopf.setStyle(derStyle + ";" + "-fx-border-color: black; -fx-font-size: 14");
    }

    public void MausRaus(){
    Knopf.setStyle(derStyle);
    }
}