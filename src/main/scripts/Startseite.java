import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.io.*;
import java.util.*;

public class Startseite {

    public javafx.scene.control.Label labelMail;
    public AnchorPane inhaltsFenster;
    public Button NachrichtVerfassenID;
    public Button Posteingang;
    public Button Papierkorb;
    public Button Entwürfe;
    public Button Postausgang;
    public Button Archiv;
    public Button Spam;
    public Button AlleEMails;
    public Button Abmelden;
    public AnmeldeDaten dieAnmeldeDaten;
    public boolean entwuerfe = false;
    public static Stage primaryStage;
    @FXML
    public ImageView image2;

    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        primaryStage.setResizable(false);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Startseite.fxml"));
        VBox load = fxmlLoader.load();
        primaryStage.setScene(new Scene(load, 1280, 720));
        primaryStage.show();
        abmeldungBeiFensterSchliessung();
    }

    public void initialize() throws IOException {
        //E-Mail wird angezeigt
        File myObj = new File("src/AnmeldeDaten.json");
        Scanner myReader = new Scanner(myObj);
        String temp3 = myReader.nextLine();
        myReader.close();

        Gson temp = new Gson();
        dieAnmeldeDaten = temp.fromJson(temp3, AnmeldeDaten.class);
        labelMail.setText(dieAnmeldeDaten.getEmail());

        MailsInFensterAuflistenServer("EmailSpeicher","INBOX",Posteingang,"Posteingang");
    }

    /***
     * Wenn auf die einzelnen Buttons gedrückt wird, werden die Mails aus den Gmail-Servern geladen.
     * @param mouseEvent
     * @throws IOException
     */
    public void PosteingangGedrueckt(MouseEvent mouseEvent) throws IOException {
        MailsInFensterAuflistenServer("EmailSpeicher","INBOX",Posteingang,"Posteingang");
    }
    public void PapierkorbGedrueckt(MouseEvent mouseEvent) throws IOException {
        MailsInFensterAuflistenServer("Papierkorb","[Gmail]/Papierkorb",Papierkorb,"Papierkorb");
    }
    public void ArchivGedrueckt(MouseEvent mouseEvent) throws IOException {
        MailsInFensterAuflistenServer("Archiv","[Gmail]/Wichtig",Archiv,"Archiv");
    }
    public void PostausgangGedrueckt(MouseEvent mouseEvent) throws IOException {
        MailsInFensterAuflistenServer("Postausgang","[Gmail]/Gesendet",Postausgang,"Postausgang");
    }
    public void EntwuerfeGedrueckt(MouseEvent mouseEvent) throws IOException {
        entwuerfe = true;
        MailsInFensterAuflistenServer("Entwürfe","[Gmail]/Entwürfe",Entwürfe,"Entwürfe");
    }
    public void SpamGedrueckt(MouseEvent mouseEvent) throws IOException {
        MailsInFensterAuflistenServer("Spam","[Gmail]/Spam",Spam,"Spam");
    }
    public void AlleEMailsGedrueckt(MouseEvent mouseEvent) throws IOException {
        MailsInFensterAuflistenServer("AlleMails","[Gmail]/Alle Nachrichten",AlleEMails,"Alle E-Mails");
    }
    public void PosteingangUngeklickt(MouseEvent mouseEvent) throws IOException {
        MailsInFensterAuflistenServer("EmailSpeicher","INBOX",Posteingang,"Posteingang");
    }
    public void NachrichtSchreiben(MouseEvent mouseEvent) throws Exception {
        inhaltsFenster.getChildren().remove(0,inhaltsFenster.getChildren().size());
        AnchorPane nachrichtenFenster = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("EmailSchreiben.fxml")));
        inhaltsFenster.getChildren().add(nachrichtenFenster);
    }

    /***
     * Diese Methode getMailsFromServer() holt sich die Mails vom Gmail-Server und speichert diese in unsere dafür vorgegebene Json-Datei
     * Dabei wird eine ListView erstellt, die zu jeder geladen Mail, einen Button erstellt.
     * Dabei werden die ganzen Objekte aus der Liste dem AnchorPane hinzugefügt.
     * @param jsonDatei
     * @param gmailBefehl
     * @param knopf
     * @param knopfName
     * @throws IOException
     */
    public void MailsInFensterAuflistenServer(String jsonDatei, String gmailBefehl, Button knopf, String knopfName) throws IOException {
        System.out.println("MailsInFensterAuflistenServer");

        ListView<Button> newListview = new ListView();
        newListview.setPrefHeight(685);
        newListview.setPrefWidth(1068);

        newListview.setMaxSize(1068,685);

        ArrayList<Mail> mailListServer = GetMails.getMailsFromServer(jsonDatei,gmailBefehl);
        ArrayList<Button> mailButtonsServer = mailButtonsGenerieren(mailListServer);

        knopf.setText(knopfName + " " + mailListServer.size());
        newListview.getItems().addAll(mailButtonsServer);

        inhaltsFenster.getChildren().remove(0,inhaltsFenster.getChildren().size());
        inhaltsFenster.getChildren().add(newListview);
    }

    /***
     * Die Methode generiert aus jedem Mailobkjekt der Liste einen Button, der Sender, Betreff und Eingangsdatum darstellt, und wenn er gedrückt wird, ein Objekt der "MailAnzeige.fxml" im Fenster öffnet, dass die Mail abbildet.
     * @param mailList eine Liste aus Mailobjekten
     * @return eine Liste aus Button Objekten
     * @throws IOException
     */
    public ArrayList<Button> mailButtonsGenerieren (ArrayList<Mail> mailList) throws IOException {

        ArrayList<Button> buttonArrayList = new ArrayList<Button>();
        ArrayList<String> reducedColorCode = getReducedColorCodes(mailList.size());

        for(int i = 0; i <= mailList.size()-1; i++){
            String sender =  (mailList.get(i).sender);
            String betreff = (mailList.get(i).betreff);
            String eingansgdatum = (mailList.get(i).eingangsdatum);
            String inhalt = mailList.get(i).inhalt;

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "EmailKnopf.fxml"
                    )
            );

            Button tempButton = null;
            try {
                tempButton = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            EmailKnopf tempEmailknopf = loader.getController();
            tempEmailknopf.setValue(sender, betreff, eingansgdatum, inhalt, reducedColorCode.get(i), mailList.get(i));

            int finalI = i;
            if(entwuerfe==true){
                tempButton.setOnAction((new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        inhaltsFenster.getChildren().remove(0, inhaltsFenster.getChildren().size());

                        FXMLLoader loader = new FXMLLoader(
                                getClass().getResource(
                                        "EmailSchreiben.fxml"
                                )
                        );

                        System.out.println(loader);

                        AnchorPane tempMailFenster = null;
                        try {
                            tempMailFenster = loader.load();
                            System.out.println("Erflogreich gelesen" + tempMailFenster.getId());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        EmailSchreiben tempEmailSchreiben = loader.getController();

                        tempEmailSchreiben.Empfänger.setText(sender);
                        tempEmailSchreiben.Betreff.setText(betreff);
                        tempEmailSchreiben.Mail.setText(inhalt);
                        inhaltsFenster.getChildren().add(tempMailFenster);
                    }
                }));

            }else {

    tempButton.setOnAction((new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            //inhaltsFenster.getChildren().removeAll();
            inhaltsFenster.getChildren().remove(0, inhaltsFenster.getChildren().size());

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "MailAnzeige.fxml"
                    )
            );

            System.out.println(loader);

            AnchorPane tempMailFenster = null;
            try {
                tempMailFenster = loader.load();
                System.out.println("Erflogreich gelesen" + tempMailFenster.getId());
            } catch (IOException e) {
                e.printStackTrace();
            }
            MailAnzeige tempMailanzeige = loader.getController();
            tempMailanzeige.setValues(sender, betreff, inhalt, eingansgdatum, mailList.get(finalI));
            inhaltsFenster.getChildren().add(tempMailFenster);
        }
    }));
}
            buttonArrayList.add(tempButton);
        }
            entwuerfe = false;
        return buttonArrayList;
    }

    /***
     * Die Methode generiert über die Methode getColorsButton eine Liste an Farbcodes, und Streckt oder Schrumpft die dann auf die übergebene Länge, wobei der Farbverlauf bestehen bleibt.
     * @param length Wie lang die Liste an Farbcodes sein soll, die zurückgegegeben wird.
     * @return Eine Liste aus Strings, die jeweils einen Farbcode im Hexadezimal Format representieren.
     */
    public ArrayList<String> getReducedColorCodes(int length){
        ArrayList<String> colorCode = getColorsButton();
        ArrayList<String> reducedColorCode = new ArrayList<>();
        for(int i = 0; i <= length-1; i++) {
            double a = i;
            double b = a * ((double)colorCode.size() / (double)length-1);
            int colorStelle = (int) b;
            reducedColorCode.add(colorCode.get(colorStelle));
        }
        return reducedColorCode;
    }

    /***
     * Die Methode generiert eine Liste aus Farbcodes, die den RGB Farbverlauf representieren.
     * @return Eine Liste aus Strings, die jeweils einen Farbcode im Hexadezimal Format representieren.
     */
    public ArrayList<String> getColorsButton (){
        ArrayList<String> tempArrayList = new ArrayList<>();
        int R = 255;
        int G = 0;
        int B = 0;
        // Rot zu Gelb
        for (int i = 1; i <= 255; i++){
            G = i;
            String charR = ("0" + Integer.toHexString(R)).substring((Integer.toHexString(R)).length() - 1);
            String charG = ("0" + Integer.toHexString(G)).substring((Integer.toHexString(G)).length() - 1);
            String charB =  "00";
            String colorcode = charR + charG + charB;
        }

        // Gelb zu Grün
        for (int i = 255; i >= 0; i--){
            R = i;
            String charR = ("0" + Integer.toHexString(R)).substring((Integer.toHexString(R)).length() - 1);
            String charG = ("0" + Integer.toHexString(G)).substring((Integer.toHexString(G)).length() - 1);
            String charB =  "00";
            String colorcode = charR + charG + charB;
            tempArrayList.add(colorcode);
        }

        // Grün zu Cyan
        for (int i = 1; i <= 255; i++){
            B = i;
            String charR = "00";
            String charG = ("0" + Integer.toHexString(G)).substring((Integer.toHexString(G)).length() - 1);
            String charB = ("0" + Integer.toHexString(B)).substring((Integer.toHexString(B)).length() - 1);
            String colorcode = charR + charG + charB;
            tempArrayList.add(colorcode);
        }

        // Cyan zu Blau
        for (int i = 255; i >= 0; i--){
            G = i;
            String charR = "00";
            String charG = ("0" + Integer.toHexString(G)).substring((Integer.toHexString(G)).length() - 1);
            String charB = ("0" + Integer.toHexString(B)).substring((Integer.toHexString(B)).length() - 1);
            String colorcode = charR + charG + charB;
            tempArrayList.add(colorcode);
        }

        //Blau zu PInk
        for (int i = 1; i <= 255; i++){
            R = i;
            String charR = ("0" + Integer.toHexString(R)).substring((Integer.toHexString(R)).length() - 1);
            String charG =  "00";
            String charB = ("0" + Integer.toHexString(B)).substring((Integer.toHexString(B)).length() - 1);
            String colorcode = charR + charG + charB;
            tempArrayList.add(colorcode);
        }

        // Pink zu Rot
        for (int i = 255; i >= 0; i--){
            B = i;
            String charR = ("0" + Integer.toHexString(R)).substring((Integer.toHexString(R)).length() - 1);
            String charG =  "00";
            String charB = ("0" + Integer.toHexString(B)).substring((Integer.toHexString(B)).length() - 1);
            String colorcode = charR + charG + charB;
            tempArrayList.add(colorcode);
        }
        return tempArrayList;
    }

    /***
     * Diese Methode wird aufgerufen, wenn der Abmeldungsknopf in der Startseite gedrückt wurde
     * @param mouseEvent
     * @throws IOException
     */
    public void AbmeldungsKnopfGedrueckt(MouseEvent mouseEvent) throws IOException {
        abmeldung();
    }

    /***
     * Mit dieser Methode wird festgelegt, dass wenn das Fenster von Benutzer geschlossen wird, vorher noch Methode "abmeldung" aufgerufen wird.
     */
    public void abmeldungBeiFensterSchliessung() {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                try {
                    abmeldung();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        });
    }

    /***
     * In dieser Methode werden die Anmeldedaten aus der "AnmeldeDaten.json" geladen, und wenn "save" == false, werden die AnmeldeDaten aus der Json gelöscht, der Nutzer wird also Abgemeldet, und in jedem Fall wird daraufhin das Logib Fenster geöffnet
     * @throws IOException
     */
    public void abmeldung() throws IOException {
        //Anmeldedaten werden aus der Json geladen
        File myObj = new File("src/AnmeldeDaten.json");
        Scanner myReader = new Scanner(myObj);
        String temp3 = myReader.nextLine();
        myReader.close();
        Gson temp = new Gson();
        AnmeldeDaten dieAnmeldeDaten = temp.fromJson(temp3, AnmeldeDaten.class);

        //Wenn Anmeldedaten nicht gespeichert werden sollen -> überschreibe Json mit leeren Werten
        if (dieAnmeldeDaten.save == false) {
            AnmeldeDaten temp2 = new AnmeldeDaten("", "", false, "pop3");
            Gson tempJson = new Gson();
            try {
                String jsonAnmeldung = tempJson.toJson(temp2);
                BufferedWriter writer = new BufferedWriter(new FileWriter("src/Anmeldedaten.json", false));
                writer.write(jsonAnmeldung);
                System.out.println(jsonAnmeldung);
                writer.close();
            } catch (Exception ex) {
                System.out.println("error: " + ex.toString());
            }
        }
        //öffne Login-Fenster beim ausloggen
        Login tempLogin = new Login();
        tempLogin.start(primaryStage);

    }


    /***
     * Hintergrundbild ändern mithilfe ImageView
     */
    Image myImage3 = new Image(getClass().getResourceAsStream("login-page.gif"));
    public void PartyMode(MouseEvent mouseEvent) {
        image2.setImage(myImage3);
    }

    /***
     * Party Mode abschalten und zu ürsprungliche Hintergrund wechseln
     */
    Image myImage4 = new Image(getClass().getResourceAsStream("light-grey2.png"));
    public void pictureBasic(MouseEvent mouseEvent) {
        image2.setImage(myImage4);
    }
}