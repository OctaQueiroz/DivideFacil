package com.example.octaq.dividefacil;

public class Role {
    public String idDadosPessoas;
    public String idDadosRole;
    public String nome;
    public String dia;
    public double valorRoleAberto;
    public double valorRoleFechado;
    public boolean fechou;
    public boolean excluido;

    public Role(){
        idDadosPessoas = "";
        idDadosRole = "";
        nome = "";
        dia = "";
        valorRoleAberto = 0.0;
        valorRoleFechado = 0.0;
        fechou = false;
        excluido = false;
    }
    public Role(String idDadosRole, String idDadosPessoas, String nome, String dia){
        this.idDadosRole = idDadosRole;
        this.idDadosPessoas = idDadosPessoas;
        this.nome = nome;
        this.dia = dia;
        this.valorRoleAberto = 0.0;
        this.valorRoleFechado = 0.0;
        fechou = false;
        excluido = false;
    }
}
