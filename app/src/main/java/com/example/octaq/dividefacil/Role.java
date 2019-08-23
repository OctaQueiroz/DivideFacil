package com.example.octaq.dividefacil;

public class Role {
    public String idDadosPessoas;
    public String idDadosRole;
    public String nome;
    public String dia;
    public double valor;
    public boolean fechou;

    public Role(){
        idDadosPessoas = "";
        idDadosRole = "";
        nome = "";
        dia = "";
        valor = 0.0;
        fechou = false;
    }
    public Role(String idDadosRole, String idDadosPessoas, String nome, String dia){
        this.idDadosRole = idDadosRole;
        this.idDadosPessoas = idDadosPessoas;
        this.nome = nome;
        this.dia = dia;
        this.valor = 0.0;
        fechou = false;
    }
}
