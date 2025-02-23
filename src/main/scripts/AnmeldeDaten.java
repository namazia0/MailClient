public class AnmeldeDaten
{
    /**
     *
     * Klassenvariablen definieren
     * */
    String email, passwort, protokol;
    Boolean save = Login.save;

    /**
     * Konstruktor
     * @param email Email-Adresse des angemeldeten Benutzer
     * @param passwort Passwort des angemeldeten Benutzer
     * @param save Boolean, der speichert, ob die Anmeldedaten gespeichert werden sollen
     * @param protokol gibt an welches Protokoll für das Empfangen von Mails genutzt werden soll (Pop3 oder Imap)
     *
     *
     * */
    public AnmeldeDaten(String email, String passwort, Boolean save, String protokol){
        this.email = email;
        this.passwort = passwort;
        this.save = save;
        this.protokol = protokol;
    }
/**
 * Gibt die Email-Adresse des angemeldeten Benutzers zurück
 * */
    public String getEmail() {
        return email;
    }
    /**
     * Gibt das Passwort des angemeldeten Benutzers zurück
     * */
    public String getPasswort() {
        return passwort;
    }
    /**
     * Gibt zurück, ob die Anmeldedaten gespeichert werden sollen
     * */
    public Boolean getSave() {
        return save;
    }
}
