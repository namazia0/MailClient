import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Scanner;

public class InDateiSchreiben {

    /***
     * Eine Methode die in der Klasse EmailSchreiben innerhalb der Methode mailSpeichern() aufgerufen wird.
     * Die Methode schreibt, wenn der Benutzer eine Mails speichern will, in die Json mit dem übergebenen Dateinamen als Parameter.
     * @param dateiName
     * @param tempMail
     * @return true
     * @throws IOException
     */

 public Boolean schreibeInDatei(String dateiName, Mail tempMail) throws IOException {
     Gson tempGson = new Gson();

     File derEmailSpeicher = new File("src/" + dateiName + ".json");
     Scanner scanner = new Scanner(derEmailSpeicher);
     String AlteMailsString = scanner.nextLine();

     Type foundListType = new TypeToken<ArrayList<Mail>>() {}.getType();
     ArrayList<Mail> tempArrayList = tempGson.fromJson(AlteMailsString, foundListType);

     tempArrayList.add(tempMail);

     String NeueUndAlteMails = tempGson.toJson(tempArrayList);

     BufferedWriter writer = new BufferedWriter(new FileWriter("src/" + dateiName + ".json", false));
     writer.write(NeueUndAlteMails);
     writer.close();

     return true;
    }
}