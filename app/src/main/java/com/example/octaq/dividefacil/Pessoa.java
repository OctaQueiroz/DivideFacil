package com.example.octaq.dividefacil;

import java.util.ArrayList;

public class Pessoa {
    public String id;
    public String nome;
    public Double valorTotal;
    public ArrayList<Alimento> historicoAlimentos;
    public boolean fechouConta;

    public Pessoa(){
        id = "";
        nome  = "";
        valorTotal = 0.0;
        historicoAlimentos = new ArrayList<>();
        fechouConta = false;
    }
}
