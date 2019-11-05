package com.example.octaq.dividefacil;

public class TransicaoDeDadosEntreActivities {
    public Despesa despesa;
    public Pessoa pessoa;
    public String userUid;
    public String userEmail;
    public String daltonismo;

    public TransicaoDeDadosEntreActivities(){
        this.despesa = new Despesa();
        this.pessoa = new Pessoa();
        this.userEmail = "";
        this.userUid = "";
        this.daltonismo = "Nenhum";
    }
}
