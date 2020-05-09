/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clases_coleccions;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bson.BasicBSONObject;

/**
 *
 * @author José Rosendo
 */
public class Mensaxe {
    private static Pattern patronHastTag = Pattern.compile("#\\p{Alpha}+");
    private String text;
    private Usuario user;
    private Date date;
    private ArrayList<String> hashtags;

    public Mensaxe(String text) {
        this.text = text;
    }

    public Mensaxe() {
    }

    public Mensaxe(String text, Usuario user, Date date, ArrayList<String> hashtags) {
        this.text = text;
        this.user = user;
        this.date = date;
        this.hashtags = hashtags;
    }

    public Mensaxe(String text, Usuario user, ArrayList<String> hashtags) {
        this.text = text;
        this.user = user;
        this.hashtags = hashtags;
    }

    public Mensaxe(String text, Usuario user) {
        this.text = text;
        this.user = user;
        this.hashtags=extraerHashTags();
        
        
    }
    public boolean enviarMensaxe(DB mDb){
        date=new Date();
        DBObject novaMensaxe=new BasicDBObject()
                .append("text", text)
                .append("user", new BasicDBObject()
                    .append("nome", user.getNome())
                    .append("username", user.getUsername()))
                .append("date",date)
                .append("hashtags",hashtags);
        DBCollection coleccion=mDb.getCollection("mensaxe");

        WriteResult resultado=coleccion.insert(novaMensaxe);
        if (resultado.wasAcknowledged()) System.out.println("Mensaxe enviada.");
        else System.err.println("Ocurriu un erro no envío da mensaxe.");
        return resultado.wasAcknowledged();
    }
    
    public ArrayList<String> extraerHashTags(){
        ArrayList<String> tempHashTags=new ArrayList<>();
        Matcher coincidencias=patronHastTag.matcher(text);
        while (coincidencias.find()){
            tempHashTags.add(coincidencias.group().substring(1));
        }
        return tempHashTags;
    }
    public static void printMensaxeFromDBObject(DBObject doc){
        System.out.printf("%-29s - %-9s - %s -> %s\n",doc.get("date"),((BasicBSONObject)doc.get("user")).get("username"),((BasicBSONObject)doc.get("user")).get("nome"),doc.get("text"));
    }
}
