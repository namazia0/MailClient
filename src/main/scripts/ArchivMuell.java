import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javax.mail.*;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;


public class ArchivMuell extends Thread {
    /**
     * Klassenvariable Mail deklarieren, diese wird später verschoben
     * Klassenvariable gmailBefehl deklarieren, diese wird angeben in welchen Ordner auf den Gmail-Servern verschoben werden soll
     * */
    public Mail tempMail;
    public String gmailBefehl;

    /**
     * @param gmailBefehl2 übergibt in welchen Ordner die Mail auf den Gmail-Servern verschoben werden soll
     * @param tempMail2 übergibt die Mail, die verschoben werden soll
     * */
    public ArchivMuell(Mail tempMail2, String gmailBefehl2) {
        this.tempMail = tempMail2;
        this.gmailBefehl = gmailBefehl2;
    }

    /**
     * "start" wird automatisch beim erstellen einer Instanz dieser Klasse gestartet.
     * Sie verschiebt die übergebene Mail in den als Parameter übergebenen Ordner "Archiv"
     * und löscht die Mail aus dem Json-Dokument "EmailSpeicher", in dem alle Mails gespeichert sind.
     * */
    public void start() {
        Gson tempGson = new Gson();

        //Anmeldedaten werden gelesen

        File myObj = new File("src/AnmeldeDaten.json");
        Scanner myReader = null;
        try {
            myReader = new Scanner(myObj);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String temp3 = myReader.nextLine();
        AnmeldeDaten dieAnmeldeDaten = tempGson.fromJson(temp3, AnmeldeDaten.class);

        String host = "imap.gmail.com";
        String storeType = "imaps";
        String port = "993";

        if (dieAnmeldeDaten.protokol == "pop3") {
            host = "pop3.gmail.com";
            storeType = "pop3";
            port = "995";
        }

        //create properties field

        Properties properties = new Properties();

        properties.put("mail.store.protocol", storeType);
        //Define the host name of the mail server
        properties.put("mail." + storeType + ".host", host);
        //Define the Pop3 port on which the Pop3 server is listening
        properties.put("mail." + storeType + ".port", port);
        //Upgrade the regular pop3 connection on the usual port to an encrypted (TLS or SSL) connection
        properties.put("mail." + storeType + ".starttls.enable", "true");
        Session emailSession = Session.getDefaultInstance(properties);

        //create the POP3 store object and connect with the pop server
        //Store = An abstract class that models a message store and its access protocol, for storing and retrieving messages. Subclasses provide actual implementations.

        Store store = null;
        try {
            store = emailSession.getStore(storeType);
            store.connect(host, dieAnmeldeDaten.email, dieAnmeldeDaten.passwort);

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        //create the folder object and open it
        Folder emailFolder = null;
        Message[] messages = new Message[0];
        try {
            emailFolder = store.getFolder(gmailBefehl);
            emailFolder.open(Folder.READ_WRITE);
            messages = emailFolder.getMessages();

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        System.out.println(messages.length + " Mails werden geladen\n");

        ArrayList<Mail> neueMailArrayList = new ArrayList<Mail>();
        FetchProfile fetchProfile = new FetchProfile();
        fetchProfile.add(FetchProfile.Item.ENVELOPE);

        try {
            emailFolder.fetch(messages, fetchProfile);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        try {
            Message[] msgs = new Message[1];
            Message[] temp2 = messages;
            int i = 0;
            for (Message tempMessage : temp2) {

                Mail mail = new Mail(tempMessage.getFrom()[0].toString(), tempMessage.getSubject().toString(), tempMessage.getSentDate().toString(), "temp", true, i, messages.length);

                if (mail.eingangsdatum.equals(tempMail.eingangsdatum) && mail.betreff.equals(tempMail.betreff) && tempMail.sender.equals(mail.sender)) {

                    File derEmailSpeicher = new File("src/EmailSpeicher.json");
                    Scanner scanner = new Scanner(derEmailSpeicher);
                    String AlteMailsString = scanner.nextLine();
                    //MailsInFensterAuflistenServer

                    Type foundListType = new TypeToken<ArrayList<Mail>>() {
                    }.getType();
                    ArrayList<Mail> PosteingangListe = tempGson.fromJson(AlteMailsString, foundListType);
                    Mail tempLoeschen = PosteingangListe.get(0);
                    for (Mail tempMail2 : PosteingangListe) {

                        if (tempMail2.eingangsdatum.equals(tempMail.eingangsdatum) && tempMail2.betreff.equals(tempMail.betreff) && tempMail2.sender.equals(tempMail.sender)) {
                            tempLoeschen = tempMail2;
                            System.out.println("Ist true");
                        }

                    }
                    PosteingangListe.remove(tempLoeschen);
                    String NeueUndAlteMails = tempGson.toJson(PosteingangListe);
                    BufferedWriter writer = new BufferedWriter(new FileWriter("src/EmailSpeicher.json", false));
                    writer.write(NeueUndAlteMails);
                    writer.close();

                    System.out.println("Mail ist identisch und wird archiviert-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-");
                    msgs[0] = tempMessage;
                    Folder archiv = store.getFolder("[Gmail]/Wichtig");
                    emailFolder.copyMessages(msgs, archiv);
                }
            }
        } catch (MessagingException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}