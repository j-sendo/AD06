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
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import java.util.ArrayList;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;

/**
 *
 * @author José Rosendo
 */
public class Usuario {
    private String username,nome,password;
    private ArrayList<String> follows;

    public Usuario() {
    }

    public Usuario(String username, String nome) {
        this.username = username;
        this.nome = nome;
    }

    public Usuario(String username, String nome, String password) {
        this.username = username;
        this.nome = nome;
        this.password = password;
        this.follows=new ArrayList<>();
    }

    public Usuario(String username, String nome, String password, ArrayList<String> follows) {
        this.username = username;
        this.nome = nome;
        this.password = password;
        this.follows = follows;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<String> getFollows() {
        return follows;
    }

    public void setFollows(ArrayList<String> follows) {
        this.follows = follows;
    }
    public boolean insertarUsuario(DB mDb){
        DBObject novoUser=new BasicDBObject()
                .append("nome", nome)
                .append("username", username)
                .append("password", password)
                .append("follows",follows);
        DBCollection coleccion=mDb.getCollection("usuario");
        if (existeUsuarioEnBD(mDb,username)) {
            System.err.println("O usuario indicado xa existe, operación cancelada.\n");
            return false;
        }
        else {
            WriteResult resultado=coleccion.insert(novoUser);
            System.out.println("Creouse o usuario correctamente.\n");
            return resultado.wasAcknowledged();
        } 
    }
    
    public boolean engadirUsuarioSeguidos(DB mDb, Usuario usuario){
        DBCollection coleccion=mDb.getCollection("usuario");
        Bson updateAux=Updates.push("follows", usuario.getUsername());
        DBObject update=new BasicDBObject(updateAux.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
        Bson filtro=Filters.eq("username", username);
        DBObject consulta=new BasicDBObject(filtro.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
        
        WriteResult resultado=coleccion.update(consulta, update);
        if (resultado.wasAcknowledged()){
            this.follows.add(usuario.getUsername());
            return true;
        } else return false;
    }
    
    public static boolean existeUsuarioEnBD(DB mDb,String username){
        DBCollection coleccion=mDb.getCollection("usuario");
        DBObject consulta=new BasicDBObject("username",username);
        DBObject resultado=coleccion.findOne(consulta);
        
        if (resultado==null) return false;
        else return true;  
    }
    public static Usuario obterDendeBD(DB mDb,String username){
        DBCollection coleccion=mDb.getCollection("usuario");
        DBObject consulta=new BasicDBObject("username",username);
        DBObject resultado=coleccion.findOne(consulta);

        if (resultado==null) return null;

        Usuario usuarioTmp=new Usuario((String)resultado.get("username"),
                (String)resultado.get("nome"),(String)resultado.get("password"),
                (ArrayList<String>)resultado.get("follows"));
        
        return usuarioTmp;
    }
    public static void printUsuarioFromDBObject(DBObject doc){
        System.out.printf("%-10s - %s\n",doc.get("username"),doc.get("nome"));
    }

    @Override
    public String toString() {
        return String.format("%-10s - %s",username,nome);
    }
    
}
