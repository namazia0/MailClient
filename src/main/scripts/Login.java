import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import com.google.gson.Gson;

import java.io.*;
import java.util.Scanner;

public class Login extends Application {

    /***
     *  Deklaration von Buttons, Labels und Variablen
     */

    public TextField emailEingabe;
    public PasswordField passwort;
    public CheckBox LoginDatenSpeicherCheckbox;

    public static Boolean save;
    public static Stage primaryStage;

    public double fensterbreite = 1280;
    public double fensterhoehe = 720;

    public ToggleButton pop3;
    public ToggleButton iMap;

    public String protokol = "pop3";

    @FXML
    public Label LoginFeedback;

    @FXML
    public ImageView picture;

    /***
     * Laden der Login.fxml beim Starten des Programm
     * @param stage wird der primaryStage übergeben, damit ein Fenster gestartet wird
     * @throws IOException
     */
    public void start(Stage stage) throws IOException {

        primaryStage = stage;
        primaryStage.setResizable(false);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Login.fxml"));
        VBox load = fxmlLoader.load();

        Scene scene = new Scene(load,fensterbreite,fensterhoehe);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    /***
     * Initialisierer werden immer dann ausgeführt, wenn eine Instanz einer Klasse erstellt wird, unabhängig davon, welcher Konstruktor zum Erstellen der Instanz verwendet wird.
     * Beim starten des Programms wird eine Json erstellt.
     * Die Daten werden dann aus der Json und in die entsprechende Felder beim Login geschrieben.
     * @throws FileNotFoundException für den Scanner myReader, wenn dieser nicht existiert
     */

    public void initialize() throws FileNotFoundException {
        File myObj = new File("src/AnmeldeDaten.json");
        Scanner myReader = new Scanner(myObj);
        String AnmeldeDatenInJson = myReader.nextLine();
        myReader.close();

        Gson tempGson = new Gson();
        AnmeldeDaten dieAnmeldeDaten = tempGson.fromJson(AnmeldeDatenInJson, AnmeldeDaten.class);
        emailEingabe.setText(dieAnmeldeDaten.getEmail());
        passwort.setText(dieAnmeldeDaten.getPasswort());
        save = dieAnmeldeDaten.save;
        LoginDatenSpeicherCheckbox.setSelected(save);

        pop3.setSelected(true);
    }

    /***
     * Wenn der LoginKnopf gedrückt wird, wird überprüft ob die AnmeldeDaten alle eingegeben worden sind.
     * In der zweiten if-Abfrage, wird mit der Methode AnmeldedatenEntsprechenDenGespeicherten() geschaut, ob die Anmeldedaten mit der, die in der "AnmeldeDaten.json" steht, übereinstimmt.
     * Ist dies der Fall, werden die die Jsons nicht gelöscht sondern bleiben erhalten.
     * Wurden neue Anmelde Daten eingeben, die nicht mit denen aus der "AnmeldeDaten.json" übereinstimmen, werden die Jsons mit der Mails gelöscht.
     * Am Ende wird die Startseite.fxml geöffnet innerhalb der gleichen Stage.
     * @param mouseEvent
     * @throws IOException
     */

    public void LoginKnopfWurdeGedrueckt (MouseEvent mouseEvent) throws IOException {
        if (EingabenSindKorrekt()){

            File myObj = new File("src/AnmeldeDaten.json");
            Scanner myReader = new Scanner(myObj);
            String AnmeldeDatenInJson = myReader.nextLine();
            myReader.close();
            Gson tempGson = new Gson();
            AnmeldeDaten dieAnmeldeDaten = tempGson.fromJson(AnmeldeDatenInJson, AnmeldeDaten.class);

            if(!AnmeldedatenEntsprechenDenGespeicherten()){
                File AlleMails = new File("src/AlleMails.json");
                File EmailSpeicher = new File("src/EmailSpeicher.json");
                File Archiv = new File("src/Archiv.json");
                File Entwürfe = new File("src/Entwürfe.json");
                File Papierkorb = new File("src/Papierkorb.json");
                File Postausgang = new File("src/Postausgang.json");
                File Spam = new File("src/Spam.json");

                EmailSpeicher.delete();
                Archiv.delete();
                Entwürfe.delete();
                Papierkorb.delete();
                Postausgang.delete();
                AlleMails.delete();
                Spam.delete();
            }
            AnmeldeDatenInJsonSchreiben();

            Startseite tempStartseite = new Startseite();
            tempStartseite.start(primaryStage);
        }
    }

    /***
     * Die folgende Methode überprüft, ob die Daten aus "AnmeldeDaten.json" mit dem vom Benutzer eingegeben übereinstimmen oder nicht.
     * @return true oder false, je nachdem ob die AnmeldeDaten aus der Json mit dem vom Benutzer eingegebene übereinstimmen oder nicht.
     * @throws FileNotFoundException
     */
    public boolean AnmeldedatenEntsprechenDenGespeicherten() throws FileNotFoundException {
        File myObj = new File("src/AnmeldeDaten.json");
        Scanner myReader = new Scanner(myObj);
        String AnmeldeDatenInJson = myReader.nextLine();
        myReader.close();
        Gson tempGson = new Gson();
        AnmeldeDaten dieAnmeldeDaten = tempGson.fromJson(AnmeldeDatenInJson, AnmeldeDaten.class);

        AnmeldeDaten neueAnmeldeDaten = new AnmeldeDaten(emailEingabe.getText(), passwort.getText(), save, protokol);
        System.out.println(dieAnmeldeDaten.email);
        System.out.println(neueAnmeldeDaten.email);
        System.out.println(dieAnmeldeDaten.passwort);
        System.out.println(neueAnmeldeDaten.passwort);

        if(dieAnmeldeDaten.email.compareTo(neueAnmeldeDaten.email) == 0 && dieAnmeldeDaten.passwort.compareTo(neueAnmeldeDaten.passwort)== 0){
            System.out.println("Anmeldedaten sind gleich");
            return true;
        }
        else {
            System.out.println("Anmeldedaten sind ungleich");
            return false;
        }
    }

    /***
     * Es wird ein AnmeldeDaten-Objekt erstellt, die die eingegebene Daten bei der Anmeldung in die Json schreiben soll.
     */
    public void AnmeldeDatenInJsonSchreiben() {
        try {
            AnmeldeDaten tempAnmeldeDaten = new AnmeldeDaten(emailEingabe.getText(), passwort.getText(), save, protokol);

            System.out.println(emailEingabe.getText());
            Gson tempGson = new Gson();

            //Anmeldedaten in Json schreiben

            String jsonAnmeldeDaten = tempGson.toJson(tempAnmeldeDaten);
            BufferedWriter writer = new BufferedWriter(new FileWriter("src/Anmeldedaten.json", false));
            writer.write(jsonAnmeldeDaten);
            writer.close();

            System.out.println(jsonAnmeldeDaten);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * In den folgenden Methode wird überprüft, ob alle AnmeldeDaten korrekt und sinngemäß eingegeben wurden.
     * Wenn nicht, erscheint ein Label mit einem Hinweis, dass der Benutzer seine übergebene Daten überprüfen soll.
     * @return true oder false, je nachdem ob die Eingabedaten korrekt eingegeben wurden.
     */
    public boolean EingabenSindKorrekt(){
        if (PasswortKorrekt() && EmailKorrekt())
            return true;
        else if (!PasswortKorrekt() && !EmailKorrekt()) {
            LoginFeedback("Please enter your Login details");
            LoginFeedback.setTextFill(Color.RED);
            return false;
        }
        else if (!PasswortKorrekt() && EmailKorrekt()) {
            LoginFeedback("Please enter your passwort");
            LoginFeedback.setTextFill(Color.RED);
            return false;
        }
        else if (PasswortKorrekt() && !EmailKorrekt()) {
            LoginFeedback("Please enter a correct Email Adress");
            LoginFeedback.setTextFill(Color.RED);
            return false;
        }
        return false;
    }
    public boolean EmailKorrekt(){
        if (emailEingabe.getText().isEmpty() || emailEingabe.getText().contains("@")==false) {
            return false;
        }
        else return true;
    }
    public boolean PasswortKorrekt(){
        if (passwort.getText().isEmpty() && (emailEingabe.getText().isEmpty() || emailEingabe.getText().contains("@"))) {
            return false;
        }
        else{
            return true;
        }
    }
    public void LoginFeedback(String antwort){
        LoginFeedback.setText(antwort);
    }

    /***
     * Ist die Checkbox ausgewählt, werden die AnmeldeDaten in der Json gespeichert, ansonsten nicht.
     * Zudem wird die Variable save, die in AnmeldeDaten deklariert ist, mit true oder false hinterlegt.
     * @param mouseEvent
     */
    public void DieBoxZumPasswortSpeichernWurdeVereandert (MouseEvent mouseEvent) {
        if (LoginDatenSpeicherCheckbox.isSelected()) {
            save = true;
        } else if (!LoginDatenSpeicherCheckbox.isSelected()) {
            save = false;
        }
    }

    /***
     * Zwei Methoden für die Buttons iMap und Pop3.
     * Wenn eins von den beiden Optionen ausgewählt wird, steht dann in der Variable Protokol welchen Protokoll ausgewählt wurde.
     * @param mouseEvent
     */
    public void ImapGedrueckt (MouseEvent mouseEvent) {
        pop3.setSelected(false);
        iMap.setSelected(true);
        protokol = "imap";
    }
    public void Pop3Gedrueckt (MouseEvent mouseEvent) {
        iMap.setSelected(false);
        pop3.setSelected(true);
        protokol = "pop3";
    }

    /***
     * Methode für den Hyperlink.
     * getHostServices ruft den HostServices-Anbieter für diese Anwendung. Dies bietet die Möglichkeit die Anwendung abzurufen und eine Webseite in einem Browser anzuzeigen.
     * ShowDocument öffnet den Link in einem Browser.
     * @param mouseEvent
     */
    public void vergessenPasswort(MouseEvent mouseEvent) {
        getHostServices().showDocument("https://support.google.com/accounts/answer/7682439?hl=de");
    }

    /***
     * Hintergrundbild ändern mithilfe ImageView. In dem Ordner "ressources" befinden sich die Bilder.
     */
    Image myImage = new Image(getClass().getResourceAsStream("login-page.gif"));
    public void PartyModeLogin(MouseEvent mouseEvent) {
        picture.setImage(myImage);
    }

    /***
     * Party Mode abschalten und zum ürsprungliche Hintergrund wechseln.
     * @param mouseEvent
     */
    Image myImage2 = new Image(getClass().getResourceAsStream("light-grey2.png"));
    public void OffLSD(MouseEvent mouseEvent) {
        picture.setImage(myImage2);
    }
    
}