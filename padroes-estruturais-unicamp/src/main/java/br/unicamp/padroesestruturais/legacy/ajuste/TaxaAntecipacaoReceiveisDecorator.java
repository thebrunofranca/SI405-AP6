package br.unicamp.padroesestruturais.legacy.ajuste;

public class TaxaAntecipacaoReceiveisDecorator implements AjusteValor {

    private static final double TAXA = 0.015;

    @Override
    public double aplicar(double valor) {
        return valor + valor * TAXA;
    }
}
