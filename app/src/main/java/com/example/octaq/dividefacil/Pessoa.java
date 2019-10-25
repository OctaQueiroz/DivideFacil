package com.example.octaq.dividefacil;

import java.util.ArrayList;

public class Pessoa {
    public String id;
    public String nome;
    public Double valorTotal;
    public ArrayList<ItemDeGasto> historicoItemDeGastos;
    public boolean fechouConta;

    public Pessoa(){
        id = "";
        nome  = "";
        valorTotal = 0.0;
        historicoItemDeGastos = new ArrayList<>();
        fechouConta = false;
    }
}
