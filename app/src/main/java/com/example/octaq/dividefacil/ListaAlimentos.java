package com.example.octaq.dividefacil;

import java.util.ArrayList;
import java.util.List;

public class ListaAlimentos {
    public List<Alimento> historicoDeAlimentos;

    public ListaAlimentos(){
        historicoDeAlimentos = new ArrayList<>();
        //historicoDeAlimentos.add(new Alimento());
    }

    public List getValue(){
        return historicoDeAlimentos;
    }
    public int getCount() {
        return historicoDeAlimentos.size();
    }
}
