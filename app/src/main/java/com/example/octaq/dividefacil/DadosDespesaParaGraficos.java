package com.example.octaq.dividefacil;

public class DadosDespesaParaGraficos {
    public double valor;
    public String tipoDeDespesa;
    public double porcentagem;
    public int red;
    public int green;
    public int blue;
    public String tipoDaltonismo;
    public DadosDespesaParaGraficos(String tipoDeDespesa, String daltonismo){
        this.valor = 0.0;
        this.tipoDeDespesa = tipoDeDespesa;
        this.porcentagem = 0.0;
        this.tipoDaltonismo = daltonismo;
        defineCoresDoGrafico();
    }
    public DadosDespesaParaGraficos(double valor, String tipoDeDespesa, double porcentagem, String daltonismo){
        this.valor = valor;
        this.tipoDeDespesa = tipoDeDespesa;
        this.porcentagem = porcentagem;
        this.tipoDaltonismo = daltonismo;
        defineCoresDoGrafico();
    }

    public void defineCoresDoGrafico(){
        if(tipoDaltonismo.equals("Nenhum")){
            if(tipoDeDespesa.equals("Bar e Restaurante")){
                this.red = 188;
                this.green = 211;
                this.blue = 0;
            }else if(tipoDeDespesa.equals("Transporte")){
                this.red = 238;
                this.green = 76;
                this.blue = 7;
            }else if(tipoDeDespesa.equals("Saude")){
                this.red = 84;
                this.green = 187;
                this.blue = 173;
            }else if(tipoDeDespesa.equals("Supermercado")){
                this.red = 83;
                this.green = 62;
                this.blue = 139;
            }else if(tipoDeDespesa.equals("Contas de Casa")){
                this.red = 249;
                this.green = 173;
                this.blue = 0;
            }else if(tipoDeDespesa.equals("Lazer")){
                this.red = 170;
                this.green = 33;
                this.blue = 89;
            }
        }else if(tipoDaltonismo.equals("Protanopia")){
            if(tipoDeDespesa.equals("Bar e Restaurante")){
                this.red = 253;
                this.green = 187;
                this.blue = 0;
            }else if(tipoDeDespesa.equals("Transporte")){
                this.red = 60;
                this.green = 63;
                this.blue = 79;
            }else if(tipoDeDespesa.equals("Saude")){
                this.red = 123;
                this.green = 115;
                this.blue = 27;
            }else if(tipoDeDespesa.equals("Supermercado")){
                this.red = 52;
                this.green = 79;
                this.blue = 141;
            }else if(tipoDeDespesa.equals("Contas de Casa")){
                this.red = 138;
                this.green = 155;
                this.blue = 206;
            }else if(tipoDeDespesa.equals("Lazer")){
                this.red = 182;
                this.green = 181;
                this.blue = 177;
            }
        }else if(tipoDaltonismo.equals("Deuteranopia")){
            if(tipoDeDespesa.equals("Bar e Restaurante")){
                this.red = 251;
                this.green = 240;
                this.blue = 6;
            }else if(tipoDeDespesa.equals("Transporte")){
                this.red = 99;
                this.green = 95;
                this.blue = 83;
            }else if(tipoDeDespesa.equals("Saude")){
                this.red = 127;
                this.green = 143;
                this.blue = 196;
            }else if(tipoDeDespesa.equals("Supermercado")){
                this.red = 32;
                this.green = 57;
                this.blue = 123;
            }else if(tipoDeDespesa.equals("Contas de Casa")){
                this.red = 172;
                this.green = 172;
                this.blue = 174;
            }else if(tipoDeDespesa.equals("Lazer")){
                this.red = 147;
                this.green = 135;
                this.blue = 27;
            }
        }else if(tipoDaltonismo.equals("Tritanopia")){
            if(tipoDeDespesa.equals("Bar e Restaurante")){
                this.red = 242;
                this.green = 116;
                this.blue = 123;
            }else if(tipoDeDespesa.equals("Transporte")){
                this.red = 60;
                this.green = 140;
                this.blue = 158;
            }else if(tipoDeDespesa.equals("Saude")){
                this.red = 94;
                this.green = 185;
                this.blue = 203;
            }else if(tipoDeDespesa.equals("Supermercado")){
                this.red = 173;
                this.green = 40;
                this.blue = 35;
            }else if(tipoDeDespesa.equals("Contas de Casa")){
                this.red = 200;
                this.green = 193;
                this.blue = 214;
            }else if(tipoDeDespesa.equals("Lazer")){
                this.red = 92;
                this.green = 51;
                this.blue = 57;
            }
        }
    }
}
