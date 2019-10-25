package com.example.octaq.dividefacil;

public class Despesa {
    public String idDadosPessoas;
    public String idDadosRole;
    public String nome;
    public String dia;
    public double valorRoleAberto;
    public double valorRoleFechado;
    public boolean fechou;
    public boolean excluido;
    public String tipoDeDespesa;

    public Despesa(){
        idDadosPessoas = "";
        idDadosRole = "";
        nome = "";
        dia = "";
        valorRoleAberto = 0.0;
        valorRoleFechado = 0.0;
        fechou = false;
        excluido = false;
        tipoDeDespesa = "";
    }
}
