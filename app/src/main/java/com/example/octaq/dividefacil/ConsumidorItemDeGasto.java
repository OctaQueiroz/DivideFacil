package com.example.octaq.dividefacil;

public class ConsumidorItemDeGasto {
    public String nomeConsumidor;
    public String uidConsumidor;

    public ConsumidorItemDeGasto(){
        nomeConsumidor = "";
        uidConsumidor = "";
    }
    public ConsumidorItemDeGasto(String nomeConsumidor, String uidConsumidor){
        this.nomeConsumidor = nomeConsumidor;
        this.uidConsumidor = uidConsumidor;
    }
}
