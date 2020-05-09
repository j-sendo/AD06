/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.adtarea6;

import com.mongodb.MongoClientURI;

/**
 *
 * @author José Rosendo
 */
public class Configuracion {
    String address,port,dbname,username,password;

    public Configuracion(String address, String port, String dbname, String username, String password) {
        this.address = address;
        this.port = port;
        this.dbname = dbname;
        this.username = username;
        this.password = password;
    }

    public MongoClientURI getURI(){
        MongoClientURI resultado;
        if (username.isEmpty()) resultado=new MongoClientURI("mongodb://"+this.getAddress()+":"+this.getPort()+"/?"+"retryWrites=false"); 
        else resultado=new MongoClientURI("mongodb://"+this.getUsername()+":"+this.getPassword()+"@"+this.getAddress()+":"+this.getPort()+"/"+this.getDbname()+"?"+"retryWrites=false");
        return resultado;
    }
    public Configuracion() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
}
