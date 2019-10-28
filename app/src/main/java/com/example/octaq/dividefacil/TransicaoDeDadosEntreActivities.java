package com.example.octaq.dividefacil;

public class TransicaoDeDadosEntreActivities {
    public Despesa despesa;
    public Pessoa pessoa;
    public String userUid;
    public String userEmail;

    public TransicaoDeDadosEntreActivities(){
        this.despesa = new Despesa();
        this.pessoa = new Pessoa();
        this.userEmail = "";
        this.userUid = "";
    }
}
