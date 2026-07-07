package br.unicamp.padroesestruturais.legacy.ajuste;

public class DescontoFidelidadeDecorator implements AjusteValor {

    private static final double TAXA = 0.05;

    @Override
    public double aplicar(double valor) {
        return valor - valor * TAXA;
    }
}
