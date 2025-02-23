/**
 * Dieses Java-Objekt repräsentiert eine Email
 * */
public class Mail {

    /**
     * Klassenvariablen/Attribute, die eine Mail beschreiben
     * */
    public String sender, betreff, eingangsdatum, inhalt;
    public boolean gesehen;
    public int mailleange, positon;

    /**
     * @param betreff
     * @param eingangsdatum
     * @param gesehen boolean damit wir eine Mail als gelesen/ungelesen markieren können
     * @param inhalt Nachricht der Mail
     * @param positon
     * @param mailleange Länge der Mail
     **/

    public Mail(String sender, String betreff, String eingangsdatum, String inhalt, boolean gesehen,int positon, int mailleange) {
        this.sender = sender;
        this.betreff = betreff;
        this.eingangsdatum = eingangsdatum;
        this.inhalt = inhalt;
        this.gesehen = gesehen;
        this.positon = positon;
        this.mailleange = mailleange;
    }

    /**
     * Diese Methode vergleicht zwei Mail-Objekte auf Gleichheit und gibt dafür einen boolean zurück
     * @param tempMail Die Email mit der die aktuell betrachtete Mail verglichen werden soll
     * @return true -> Mails sind gleich; false -> Mails sind ungleich
     * */
    public boolean compare(Mail tempMail){

        if(sender.equals(tempMail.sender) && betreff.equals(tempMail.betreff) && eingangsdatum.equals(tempMail.eingangsdatum)) {
            /*
            System.out.println("-----------------------------------------------------");
            System.out.println(tempMail.sender + "/" +  this.sender);
            System.out.println(tempMail.betreff + "/" +  this.betreff);
            System.out.println(tempMail.eingangsdatum + "/" +  this.eingangsdatum);

            System.out.println("Sind gleich");
            System.out.println("-----------------------------------------------------");

             */
            return true;
        }
        /*
        System.out.println("-----------------------------------------------------");
        System.out.println(tempMail.sender + "/" +  this.sender);
        System.out.println(tempMail.betreff + "/" +  this.betreff);
        System.out.println(tempMail.eingangsdatum + "/" +  this.eingangsdatum);

        System.out.println("Sind ungleich");
        System.out.println("-----------------------------------------------------");

         */
        return false;
    }
}