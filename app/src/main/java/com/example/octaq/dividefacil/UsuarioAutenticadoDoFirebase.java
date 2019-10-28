package com.example.octaq.dividefacil;

public class UsuarioAutenticadoDoFirebase {
    public String uid;
    public String email;
    public String nome;

    public UsuarioAutenticadoDoFirebase(){
        this.uid = "";
        this.email = "";
        this.nome = "";
    }
    public UsuarioAutenticadoDoFirebase(String uid, String email, String nome){
        this.uid = uid;
        this.email = email;
        this.nome = nome;
    }
}
