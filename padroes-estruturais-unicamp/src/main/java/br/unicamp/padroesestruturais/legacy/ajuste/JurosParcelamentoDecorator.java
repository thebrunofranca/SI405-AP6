package br.unicamp.padroesestruturais.legacy.ajuste;

public class JurosParcelamentoDecorator implements AjusteValor {

    private static final double TAXA = 0.0299;

    @Override
    public double aplicar(double valor) {
        return valor + valor * TAXA;
    }
}
