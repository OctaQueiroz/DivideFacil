package com.example.octaq.dividefacil;

import java.util.ArrayList;
import java.util.List;

public class ItemDeGasto {
    public String id;
    public Double valor;
    public String nome;
    public List<ConsumidorItemDeGasto> usuariosQueConsomemEsseitem;

    public ItemDeGasto(){
        id = "";
        valor = 0.0;
        nome = "";
        usuariosQueConsomemEsseitem = new ArrayList<>();
    }
}
