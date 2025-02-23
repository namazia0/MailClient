import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;
import javafx.scene.paint.Color;
import javax.mail.*;

public class EmailSchreiben {
    /***
     * JavaFx-Objekte aus EmailSchreiben.fxml werden in Java deklariert
     */
    @FXML
    javafx.scene.control.TextField Empfänger;
    @FXML
    javafx.scene.control.TextArea Mail;
    @FXML
    javafx.scene.control.TextField Betreff;
    @FXML
    javafx.scene.control.Label SendenFeedback;
    @FXML
    javafx.scene.control.Label entwuerfeLabel;

    /***
     * Die Methode versendet eine verfasste Mail.
     * Die Klasse PasswordAuthentication ist ein Datenbehälter, der von Authenticator verwendet wird. Es ist einfach ein Lager für einen Benutzernamen und ein Passwort.
     * Mit Transport.send() wird die verfasste Nachricht an alle in der Nachricht angegebenen Recipients gesendent.
     * Schließlich erscheint ein Label mit der Rückmeldung, dass die Nachricht versendet wurde. Dabei werden die Inhalte aller TextAreas und Textfields entleert.
     * @param mouseEvent
     * @throws Exception
     */
    public void NachrichtSendenKnopfGedrueckt(MouseEvent mouseEvent) throws Exception{
        System.out.println("Email wird versendet");

        Properties properties = new Properties();

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        File myObj = new File("src/AnmeldeDaten.json");
        Scanner myReader = new Scanner(myObj);
        String AnmeldeDatenInJson = myReader.nextLine();
        myReader.close();
        Gson tempGson = new Gson();
        AnmeldeDaten dieAnmeldeDaten = tempGson.fromJson(AnmeldeDatenInJson, AnmeldeDaten.class);

        String meineMail = dieAnmeldeDaten.email;
        String meinPassword = dieAnmeldeDaten.passwort;

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(meineMail, meinPassword);
            }
        });

        String subject = Betreff.getText();
        String mailInhalt = Mail.getText();
        String recipient = Empfänger.getText();
        Message message = prepareMessage(session, meineMail, recipient, mailInhalt, subject);

        Transport.send(message);

        SendenFeedback.setTextFill(Color.GREEN);
        Betreff.setText("");
        Empfänger.setText("");
        Mail.setText("");
        entwuerfeLabel.setText("");
        SendenFeedback.setText("Nachricht wurde gesendet");
    }

    /***
     * Ein Try-Catch Block um zu überprüfen, ob die Mail erfolgreich gesendet wurde. Wenn dies nicht der Fall ist, wird dies abgefangen.
     * @param session
     * @param meineMail
     * @param recipient
     * @param mailInhalt
     * @param subject
     * @return null.
     */
    private Message prepareMessage(Session session, String meineMail, String recipient, String mailInhalt, String subject) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(meineMail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject(subject);
            message.setText(mailInhalt);
            return message;
        } catch (Exception ex) {
            entwuerfeLabel.setText("");
            SendenFeedback.setText("Nachricht konnte nicht gesendet werden");
            SendenFeedback.setTextFill(Color.RED);
            SendenFeedback.setText("");
        }
        return null;
    }

    /***
     * Nachdem der Benutzer eine Mail verfasst hat, wird mit dieser Methode die verfasste Mail in Entwürfe verschoben.
     * Es wird ein neues Mail-Objekt erstellt und dieser werden die Strings subject, Inhalt, und Empfänger übergeben.
     * Zusätzlich wird die Methode schreibeInDatei in der Klasse InDateiSchreiben aufgerufen, die die verfasste Mail in die Json Entwürfe speichern soll.
     * @param mouseEvent
     * @throws IOException
     */
    public void mailSpeichern(MouseEvent mouseEvent) throws IOException {
        String subject = Betreff.getText();
        String mailInhalt = Mail.getText();
        String recipient = Empfänger.getText();
        SendenFeedback.setText("");

        Mail tempMail = new Mail(recipient,subject,java.time.LocalDate.now().toString(), mailInhalt, false, 187, 10000000);

        Boolean tempBoolean = new InDateiSchreiben().schreibeInDatei("Entwürfe",tempMail);

        entwuerfeLabel.setText("Nachricht wurde in Entwürfe verschoben ");
        entwuerfeLabel.setTextFill(Color.BLUE);
    }
}