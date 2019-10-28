package com.example.octaq.dividefacil;

import java.util.ArrayList;
import java.util.List;

public class ItemDeGasto {
    public String id;
    public Double valor;
    public String nome;
    public List<String> uidUsuariosQueConsomemEsseitem;

    public ItemDeGasto(){
        id = "";
        valor = 0.0;
        nome = "";
        uidUsuariosQueConsomemEsseitem = new ArrayList<>();
    }
}
