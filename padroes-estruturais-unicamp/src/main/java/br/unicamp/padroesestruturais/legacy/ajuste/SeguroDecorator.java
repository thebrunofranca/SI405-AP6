package br.unicamp.padroesestruturais.legacy.ajuste;

public class SeguroDecorator implements AjusteValor {

    private static final double VALOR_FIXO = 4.90;

    @Override
    public double aplicar(double valor) {
        return valor + VALOR_FIXO;
    }
}
