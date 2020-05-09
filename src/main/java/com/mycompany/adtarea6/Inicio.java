/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.adtarea6;

import clases_coleccions.Mensaxe;
import clases_coleccions.Usuario;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoTimeoutException;
import com.mongodb.client.model.DBCollectionFindOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Pattern;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;

/**
 *
 * @author José Rosendo
 */
public class Inicio {
    private final static Scanner ENTRADA_TECLADO=new Scanner(System.in);
    //Conexion BD
    private static File ficheiroConf;
    private  static Configuracion configuracion;
    private  static MongoClientURI uridb;
    private  static MongoClient clientedb;
    private  static DB basedatos;
    
    private static void continuar(){
        System.out.print("\nPulse Enter para continuar....");
        ENTRADA_TECLADO.nextLine();
        System.out.println("\n");
    }

    private static Usuario userLogueado=null;
    /**
     *
     * @param args
     */
    public static void main(String[] args){
        int opcion=-1,opcion2=-1;

        
        conexion();
        while(true){
            try {
            if (userLogueado==null){
                mostrarMenuInicial();
                opcion=Integer.parseInt(ENTRADA_TECLADO.nextLine());
            }
            
            switch(opcion){
                case 1:
                    formularioRexisto();
                break;
                case 2:
                    if (userLogueado==null)formularioLogin();
                    else{
                        mostrarMenuInicio();
                        opcion2=Integer.parseInt(ENTRADA_TECLADO.nextLine());
                        switch(opcion2){
                            case 1:
                                pantallaMostrarMensaxes();
                            break;
                            case 2:
                                if (!userLogueado.getFollows().isEmpty()) pantallaMostrarMensaxesUsuarios();
                                else System.err.println("Non segue a ningún usuario.\n");
                            break;
                            case 3:
                                pantallaMostrarPorHashTag();
                            break;
                            case 4:
                                pantallaEscribirMensaxe();
                            break;
                            case 5:
                                pantallaBuscarUsuarios();
                            break;
                            case 0:
                                System.exit(0);
                            break;
                            default:
                                System.out.println("Non existe a opción introducida.");
                            break;
                        }
                        continuar();
                    }
  
                break;
                case 0:
                    System.exit(0);
                break;
                default:
                    System.out.println("Non existe a opción introducida.");
                break;
            }
        } catch (NumberFormatException e){
                System.err.println("\nERRO. Esperábase un número enteiro. Operación cancelada.\n");    
        } catch (Exception e){
            if (e.getLocalizedMessage()!=null)System.err.println(e.getLocalizedMessage());
        } 
        }

    }
    
    private static void conexion(){
        try {
            ficheiroConf=new File("datosconexion.json");
            configuracion=cargarConfiguracion(ficheiroConf);
            uridb=configuracion.getURI();
            clientedb=new MongoClient(uridb);
            basedatos=clientedb.getDB(configuracion.getDbname());
            clientedb.getAddress();
        } catch (MongoTimeoutException e){
            System.err.println("Non foi posile conectar co servidor. Revise o estado do mesmo.");
            System.exit(0);
        }
    }
    
    private static void mostrarMenuInicial(){
        System.out.println("Operacións dispoñibles:");
        System.out.println("1.-Rexistrarse");
        System.out.println("2.-Login");
        System.out.println("0.-Saír da aplicación.");
        System.out.print("Indique a operación desexada: ");
    }
    
    private static void mostrarMenuInicio(){
        System.out.println("Operacións dispoñibles:");
        System.out.println("1.-Ver tódalas mensaxes.");
        System.out.println("2.-Ver mensaxes de usuarios que sigo.");
        System.out.println("3.-Buscar por hashtag. Un hashtag ten a seguinte forma “#palabra”.");
        System.out.println("4.-Escribir unha mensaxe.");
        System.out.println("5.-Buscar usuarios.");
        
        System.out.println("0.-Saír da aplicación.");
        System.out.print("Indique a operación desexada: ");
    }
    private static void formularioRexisto() throws Exception{
        System.out.print("Introduza o nome de usuario que desexa: ");
        String user=ENTRADA_TECLADO.nextLine();
        if (user.isEmpty()) throw new Exception("Erro. O username é obrigatorio. Operación cancelada.");
        System.out.print("Introduza o seu nome completo: ");
        String nome=ENTRADA_TECLADO.nextLine();
        if (nome.isEmpty()) throw new Exception("Erro. O nome do usuario é obrigatorio. Operación cancelada.");
        System.out.print("Introduza un contrasinal: ");
        String contrasinal=ENTRADA_TECLADO.nextLine();
        if (contrasinal.isEmpty()) throw new Exception("Erro. É obrigatorio insertar un contrasinal. Operación cancelada.");
        System.out.println();
        Usuario usuarioNovo=new Usuario(user,nome,contrasinal);
        usuarioNovo.insertarUsuario(basedatos);
    }
    
    private static void formularioLogin(){
        System.out.print("Introduza o seu usuario: ");
        String usuario=ENTRADA_TECLADO.nextLine();
        System.out.print("Introduza o seu contrasinal contrasinal: ");
        String contrasinal=ENTRADA_TECLADO.nextLine();
        Usuario usuarioTmp=Usuario.obterDendeBD(basedatos, usuario);
        if (usuarioTmp==null) System.err.println("\nNON EXISTE o usuario indicado.\n");
        else if (usuarioTmp.getPassword().equals(contrasinal)) {
            userLogueado=usuarioTmp;
            
        } else System.err.println("\nContrasinal INCORRECTO.\n");
    }
    
    private static Configuracion cargarConfiguracion(File ficheiro){
        try {
            Gson gson=new GsonBuilder().setPrettyPrinting().create();
            
            return gson.fromJson(new FileReader(ficheiro), Configuracion.class);
        } catch (FileNotFoundException ex) {
            System.err.println("Non se encontrou o ficheiro de configuración. Copie o ficheiro .json no directorio da aplicación.");
            System.exit(0);
            return null;
        }
        
    }

    private static void pantallaEscribirMensaxe() throws Exception {
        System.out.println("Escriba a mensaxe e pulse enter para enviar:");
        String mensaxeTexto=ENTRADA_TECLADO.nextLine();
        if (mensaxeTexto.isEmpty()) throw new Exception("Erro. A mensaxe non pode estar baleira. Operación cancelada.");
        Mensaxe mensaxe=new Mensaxe(mensaxeTexto,userLogueado);
        mensaxe.enviarMensaxe(basedatos);
    }

    private static void pantallaMostrarMensaxes() {
        DBCollection coleccionMensaxes=basedatos.getCollection("mensaxe");
        DBCollectionFindOptions opcions=new DBCollectionFindOptions();
        Bson ordenAux=Sorts.descending("date");
        DBObject orden=new BasicDBObject(ordenAux.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
        
        Bson proxecAuax=Projections.include(Arrays.asList("date","user.username","user.nome","text"));
        DBObject proxect=new BasicDBObject(proxecAuax.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
        
        opcions.projection(proxect);
        opcions.limit(5);
        opcions.sort(orden);
        DBCursor cursor;
        
        System.out.printf("%-29s - %-9s - %s -> %s\n","DATA","USUARIO","NOME","MENSAXE");
        System.out.printf("-------------------------------------------------------\n");
        for(int i=0;i<coleccionMensaxes.getCount();i+=5){ 
            opcions.skip(i);
            cursor=coleccionMensaxes.find(new BasicDBObject(), opcions);
           
            DBObject doc;
            while(cursor.hasNext()){
                doc=cursor.next();
                Mensaxe.printMensaxeFromDBObject(doc);
            }
            
            if ((coleccionMensaxes.getCount()-i-cursor.size())==0) System.out.println("Non hai máis mensaxes.\n");
            else {
                System.out.print("Pulse enter para ver as seguintes 5 mensaxes......");
                ENTRADA_TECLADO.nextLine();
            }
            
        }

    }

    private static void pantallaMostrarMensaxesUsuarios() {
        DBCollection coleccionMensaxes=basedatos.getCollection("mensaxe");
        DBCollectionFindOptions opcions=new DBCollectionFindOptions();
        Bson ordenAux=Sorts.descending("date");
        DBObject orden=new BasicDBObject(ordenAux.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
        
        Bson proxecAuax=Projections.include(Arrays.asList("date","user.username","user.nome","text"));
        DBObject proxect=new BasicDBObject(proxecAuax.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
        
        ArrayList<Bson> filtros=new ArrayList<>();
        
        for (String s:userLogueado.getFollows()){
            filtros.add(Filters.eq("user.username", s));
        }
           
        Bson filtroAux=Filters.or(filtros);
        DBObject consulta=new BasicDBObject(filtroAux.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
        
        opcions.projection(proxect);
        opcions.limit(5);
        opcions.sort(orden);
        DBCursor cursor=coleccionMensaxes.find(consulta, opcions);
        DBObject doc;
        
        int i=0;
        System.out.printf("%-29s - %-9s - %s -> %s\n","DATA","USUARIO","NOME","MENSAXE");
        System.out.printf("-------------------------------------------------------\n");
        while(true){
            while(cursor.hasNext()){
                doc=cursor.next();
                Mensaxe.printMensaxeFromDBObject(doc);
            }
            opcions.skip(i+=5);
            cursor=coleccionMensaxes.find(consulta, opcions);
            if (!cursor.hasNext()) {
                System.out.println("Non hai máis mensaxes.\n");
                break;
            } else {
                System.out.print("Pulse enter para ver as máis mensaxes......");
                ENTRADA_TECLADO.nextLine();
            }   
        }
    }

    private static void pantallaMostrarPorHashTag() {
        DBCollection coleccionMensaxes=basedatos.getCollection("mensaxe");
        DBCollectionFindOptions opcions=new DBCollectionFindOptions();
        Bson ordenAux=Sorts.descending("date");
        DBObject orden=new BasicDBObject(ordenAux.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
        
        Bson proxecAuax=Projections.include(Arrays.asList("date","user.username","user.nome","text"));
        DBObject proxect=new BasicDBObject(proxecAuax.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
        
        System.out.print("Introduza o hashtag para ver as mensaxes que o conteñen: ");
        String hashtag=ENTRADA_TECLADO.nextLine();
        hashtag=hashtag.replace("#", "");
        Bson filtroAux=Filters.eq("hashtags", hashtag);
        DBObject consulta=new BasicDBObject(filtroAux.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
        
        opcions.projection(proxect);
        opcions.limit(5);
        opcions.sort(orden);
        DBCursor cursor=coleccionMensaxes.find(consulta, opcions);
        DBObject doc;
        
        int i=0;
        
        if (cursor.size()==0){
            System.out.println("Non hai mensaxes con ese hashtag.\n");
        } else {
            System.out.printf("%-29s - %-9s - %s -> %s\n","DATA","USUARIO","NOME","MENSAXE");
            System.out.printf("-------------------------------------------------------\n");
            while(true){
                while(cursor.hasNext()){
                    doc=cursor.next();
                    Mensaxe.printMensaxeFromDBObject(doc);
                }
                opcions.skip(i+=5);
                cursor=coleccionMensaxes.find(consulta, opcions);
                if (!cursor.hasNext()) {
                    System.out.println("Non hai máis mensaxes.\n");
                    break;
                } else {
                    System.out.print("Pulse enter para ver as máis mensaxes......");
                    ENTRADA_TECLADO.nextLine();
                }

            }
        }

    }

    private static void pantallaBuscarUsuarios() throws Exception {
        DBCollection coleccionMensaxes=basedatos.getCollection("usuario");
        DBCollectionFindOptions opcions=new DBCollectionFindOptions();
        Bson ordenAux=Sorts.ascending("username");
        DBObject orden=new BasicDBObject(ordenAux.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
        
        Bson proxecAuax=Projections.include(Arrays.asList("username","nome"));
        DBObject proxect=new BasicDBObject(proxecAuax.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
        
        ArrayList<Bson> filtros=new ArrayList<>();
        filtros.add(Filters.not(Filters.eq("username", userLogueado.getUsername())));
        
        System.out.print("Introduza o usuario a buscar: ");
        String termBusc=ENTRADA_TECLADO.nextLine();
        filtros.add(Filters.regex("username", Pattern.compile(termBusc)));
        Bson filtroAux=Filters.and(filtros);
        DBObject consulta=new BasicDBObject(filtroAux.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
        
        opcions.projection(proxect);
        opcions.limit(5);
        opcions.sort(orden);
        DBCursor cursor=coleccionMensaxes.find(consulta, opcions);
        DBObject doc;
        Usuario tmpUser;
        String seguido="Non";
        
        TreeMap<Integer,Usuario> resultados=new TreeMap<>();
        System.out.println();
        int i=0,j=1;
        if (cursor.size()==0){
            System.out.println("Non hai ningún usuario que coincida coa búsqueda.\n");
        } else {
            while(true){
                while(cursor.hasNext()){
                    doc=cursor.next();
                    tmpUser=new Usuario((String)doc.get("username"),(String)doc.get("nome"));
                    resultados.put(j,tmpUser);
                    if (userLogueado.getFollows().contains(tmpUser.getUsername())) seguido="Si";
                    else seguido="Non";
                    System.out.println(j+".- "+tmpUser+" - Seguido: "+seguido);
                    j++;
                }
                opcions.skip(i+=5);
                cursor=coleccionMensaxes.find(consulta, opcions);
                if (!cursor.hasNext()) {
                    System.out.println("\nFin da lista de usuarios encontrados.\n");
                    break;
                } else {
                    System.out.print("Pulse enter para ver as máis usuarios.");
                    ENTRADA_TECLADO.nextLine();
                }

            }
            
            System.out.print("Indique o código do usuario a seguir ou 'S' para sair: ");
            String tmpIntro=ENTRADA_TECLADO.nextLine();
            if (tmpIntro.equals("s")||tmpIntro.equals("S")) throw new Exception(); //Volver ó menú.
            
            Integer seleccion=Integer.parseInt(tmpIntro);
            
            Usuario userSeleccionado=(Usuario)resultados.get(seleccion);
            if (resultados.get(seleccion)==null) System.err.println("\nO código introducido non corresponde con ningún dos mostrados. Operación cancelada\n");
            else if (userLogueado.getFollows().contains(userSeleccionado.getUsername())) System.err.println("\nXa é seguidor do usuario indicado. Operación cancelada\n");
            else {
                if (userLogueado.engadirUsuarioSeguidos(basedatos, userSeleccionado)) System.out.println("Fíxose seguidor do usuario: "+userSeleccionado.getUsername());
                else System.out.println("Ocurriu un erro o engadir o usuario ós seguidos.");
            }
        }
    }
}
