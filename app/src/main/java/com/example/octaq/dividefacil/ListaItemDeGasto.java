package com.example.octaq.dividefacil;

import java.util.ArrayList;
import java.util.List;

public class ListaItemDeGasto {
    public List<ItemDeGasto> historicoDeItemDeGastos;

    public ListaItemDeGasto(){
        historicoDeItemDeGastos = new ArrayList<>();
        //historicoDeItemDeGastos.add(new ItemDeGasto());
    }

    public List getValue(){
        return historicoDeItemDeGastos;
    }
    public int getCount() {
        return historicoDeItemDeGastos.size();
    }
}
