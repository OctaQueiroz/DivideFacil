package com.example.octaq.dividefacil;

public class TransicaoDados {
    public Role role;
    public Pessoa pessoa;

    public TransicaoDados(){
        this.role = new Role();
        this.pessoa = new Pessoa();
    }
    public TransicaoDados(Role role){
        this.role = role;
        this.pessoa = new Pessoa();
    }
    public TransicaoDados(Role role, Pessoa pessoa){
        this.role = role;
        this.pessoa = pessoa;
    }
}
