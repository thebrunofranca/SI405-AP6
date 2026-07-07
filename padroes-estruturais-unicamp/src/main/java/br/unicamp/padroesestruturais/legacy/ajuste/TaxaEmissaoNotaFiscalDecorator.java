package br.unicamp.padroesestruturais.legacy.ajuste;

public class TaxaEmissaoNotaFiscalDecorator implements AjusteValor {

    private static final double VALOR_FIXO = 2.50;

    @Override
    public double aplicar(double valor) {
        return valor + VALOR_FIXO;
    }
}
