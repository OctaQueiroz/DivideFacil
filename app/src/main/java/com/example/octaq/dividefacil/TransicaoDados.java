package com.example.octaq.dividefacil;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TransicaoDados {
    public Role role;
    public Pessoa pessoa;
    public String userUid;

    public TransicaoDados(){
        this.role = new Role();
        this.pessoa = new Pessoa();
        this.userUid = "";
    }
}
