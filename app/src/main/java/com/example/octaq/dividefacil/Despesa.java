package com.example.octaq.dividefacil;

import java.util.ArrayList;
import java.util.List;

public class Despesa {
    public String idDadosPessoas;
    public String idDadosDespesa;
    public String nome;
    public String dia;
    public double valorRoleAberto;
    public boolean fechou;
    public boolean excluido;
    public String tipoDeDespesa;
    public List<IntegrantesUsuariosDoFirebase> uidIntegrantes;

    public Despesa(){
        idDadosPessoas = "";
        idDadosDespesa = "";
        nome = "";
        dia = "";
        valorRoleAberto = 0.0;
        fechou = false;
        excluido = false;
        tipoDeDespesa = "";
        uidIntegrantes = new ArrayList<>();
    }
}
