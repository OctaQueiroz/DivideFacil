package com.example.octaq.dividefacil;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class DialogoDeProgresso extends AsyncTask<Void, Void, Void> {

    private ProgressDialog progressDialog;
    private Context context;
    private String message;

    public DialogoDeProgresso (Context context, String message) {
        this.context = context;
        this.message = message;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show (context, null, message);
    }

    @Override
    protected Void doInBackground( Void... params ) {
        // Here you add your code.

        return null;
    }

    @Override
    protected void onPostExecute( Void result ) {
        super.onPostExecute (result);

        progressDialog.dismiss();

        // You can add code to be executed after the progress (may be a result).
    }

}