import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;

/**
 * Diese Klasse ist dafür da jegliche Mails, vom Gmail-Server zu holen.
 * */


public class GetMails {

    /**
     * Diese Methode holt sich die Mails vom Gmail-Server und speichert diese in unsere dafür vorgegebene Json-Datei
     * @param gmailBefehl Gibt an von welchem Gmail-Ordner wir die Mails anfragen
     * @param JsonDatei Gibt an in welche Json-Datei wir die angeforderten Mails speichern wollen
     * */
    public static ArrayList<Mail> getMailsFromServer(String JsonDatei, String gmailBefehl) {
        try {
            Gson tempGson = new Gson();

            //Anmeldedaten werden gelesen

            File myObj = new File("src/AnmeldeDaten.json");
            Scanner myReader = new Scanner(myObj);
            String temp3 = myReader.nextLine();
            AnmeldeDaten dieAnmeldeDaten = tempGson.fromJson(temp3, AnmeldeDaten.class);

            //Alte Mails werden gelesen

            ArrayList<Mail> alteMailArrayList = new ArrayList<Mail>();
            try {
                try {
                    File derEmailSpeicher = new File("src/" + JsonDatei +".json");
                    Scanner scanner = new Scanner(derEmailSpeicher);
                    String AlteMailsString = scanner.nextLine();
                    //MailsInFensterAuflistenServer

                    Type foundListType = new TypeToken<ArrayList<Mail>>() {
                    }.getType();
                    alteMailArrayList = tempGson.fromJson(AlteMailsString, foundListType);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("EmailSpeicher Ist Leer");
                }
            }
            catch (NoSuchElementException a){
                System.out.println("EmailSpeicher Ist Leer");
            }
            Mail leereMail = new Mail("","","","", false, 0, 0);

            if(alteMailArrayList.isEmpty()){
                alteMailArrayList.add(leereMail);
            }

            //Konfigurationen für die Verbindung mit dem Gmail-Server

            String host = "imap.gmail.com";
            String storeType = "imaps";
            String port = "993";

            if (dieAnmeldeDaten.protokol == "pop3") {
                host = "pop3.gmail.com";
                storeType = "pop3";
                port = "995";
            }

            //Verbindung mit Gmail-Servern wird aufgesetzt

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

            Store store = emailSession.getStore(storeType);

            store.connect(host, dieAnmeldeDaten.email, dieAnmeldeDaten.passwort);

            //create the folder object and open it
            Folder emailFolder = store.getFolder(gmailBefehl);
            emailFolder.open(Folder.READ_ONLY);

            // retrieve the messages from the folder in an array and print it
            Message[] messages = emailFolder.getMessages();

            System.out.println(messages.length + " Mails werden geladen\n");

            ArrayList<Mail> neueMailArrayList = new ArrayList<Mail>();

            FetchProfile fetchProfile = new FetchProfile();
            fetchProfile.add(FetchProfile.Item.ENVELOPE);

            //Es wird geschaut wie viele Mails der angeforderten neu sind bzw. noch nicht in der angegebenen Json-Datei

            for (int i = messages.length-1; i >= 0; i--) {
                Message message = messages[i];
                System.out.println("Email Nummer " + (i + 1) + " wird geladen, und überprüft ob sie schon gespeichert ist\n");
                Object msgContent = messages[i].getContent();
                String content = "";
                String textVonNAchricht = "Platzhalter";

                textVonNAchricht = getTextFromMessage(messages[i]);
                boolean gesehen = false;

                Mail mail = new Mail(message.getFrom()[0].toString(), message.getSubject().toString(), message.getSentDate().toString(), textVonNAchricht, gesehen, i, messages.length);

                if (mail.compare(alteMailArrayList.get(0))){
                    System.out.println("Mail " + (i+1) + " ist schon gespeichert, das Laden der Mails wird abgebrochen\n");
                    break;
                }
                else{
                    neueMailArrayList.add(mail);
                }
               // message.setFlag(Flags.Flag.SEEN, true);
                System.out.println("For Schleife iteration beendet");

            }
            //Alte und neue Mails werden in einer Liste zusammengefügt
            if(alteMailArrayList.get(0) == leereMail) alteMailArrayList.remove(0);
            neueMailArrayList.addAll(alteMailArrayList);

            //Die Liste mit den alten und Neuen Mails werden in die Json-Datei geschrieben
            String NeueUndAlteMails = tempGson.toJson(neueMailArrayList);
            BufferedWriter writer = new BufferedWriter(new FileWriter("src/" + JsonDatei + ".json", false));
            writer.write(NeueUndAlteMails);
            writer.close();

            return neueMailArrayList;

        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Da wir die Mails vom Server durch die Klasse Message bekommen und nicht in unserem Mail-Klassen-Format müssen wir die Attribute unseres Java-Mail-Objektes
     * erst aus dem Message-Objekt entziehen. Da das Nachrichten-Format des Message-Objektes das Multipart-Format ist, welches für den Benutzer schwer zu lesen sein könnte,
     * müssen wir dieses Format umkonvertieren zu einer lebaren Nachricht. Sind in einer Message Multiparts enthalten, so ist der String "message.getContent().toString()",
     * nicht einfach lesbar.
     *
     * Diese Methode kontrolliert, ob die Nachricht Multiparts enthält und wenn ja, wird die Methode "getTextFromMimeMultipart" aufgerufen und
     * dessen Rückgabewer zurückgegeben, ansonsten wird der Inhalt der Nachricht einfach zurückgegeben.
     *
     * @param message Das Message-Objekt von dem wir die Nachricht wollen
     * @return Der lesbare String der Nachricht wird zurückgegeben
     * @throws MessagingException, sollte etwas mit dem Message-Objekt nicht stimmen, z.B. kein Text in der Nachricht vorhanden sein
     * @throws IOException, falls die Message null ist
     * */
    private static String getTextFromMessage(Message message) throws MessagingException, IOException {
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }
/**
 * Hilfsfunktion für Methode "getTextFromMessage", um die Nachricht der Mail vom Multipart-Format in einen lesbaren String umzukonvertieren
 * @return Gibt die Multipart-Nachricht als lesbaren String zurück
 * */
    private static String getTextFromMimeMultipart(
            MimeMultipart mimeMultipart)  throws MessagingException, IOException{
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "\n" + bodyPart.getContent();
                break; // without break same text appears twice in my tests
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();

                result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
            } else if (bodyPart.getContent() instanceof MimeMultipart){
                result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
            }
        }
        return result;
    }

/**
 * Diese Methode gibt die 
 * @param JsonDatei Übergibt die Json-Datei aus der gelsen werden soll
 * @return Gibt die Json-Datei als Liste zurück
 * */
    public static ArrayList<Mail> getMailsFromJson (String JsonDatei) {

        ArrayList<Mail> alteMailArrayList = new ArrayList<>();
        Gson tempGson = new Gson();
        try {
            File derEmailSpeicher = new File("src/" + JsonDatei + ".json");
            Scanner scanner = new Scanner(derEmailSpeicher);
            String AlteMailsString = scanner.nextLine();


            Type foundListType = new TypeToken<ArrayList<Mail>>() {
            }.getType();
            alteMailArrayList = tempGson.fromJson(AlteMailsString, foundListType);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("EmailSpeicher Ist Leer");
        }
        return alteMailArrayList;
    }
}