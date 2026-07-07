package br.unicamp.padroesestruturais.legacy;

import br.unicamp.padroesestruturais.legacy.ajuste.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AjusteValorDecoratorTest {

    // --- Testes individuais de cada decorator ---

    @Test
    void descontoFidelidadeDeveReduzirCincoPorcento() {
        AjusteValor ajuste = new DescontoFidelidadeDecorator();
        assertEquals(950.0, ajuste.aplicar(1000.0), 0.001);
    }

    @Test
    void jurosParcelamentoDeveAcrescentarDoisVirgulaNoventaNovePorcento() {
        AjusteValor ajuste = new JurosParcelamentoDecorator();
        assertEquals(1029.9, ajuste.aplicar(1000.0), 0.001);
    }

    @Test
    void taxaInternacionalDeveAcrescentarCincoPorcento() {
        AjusteValor ajuste = new TaxaInternacionalDecorator();
        assertEquals(1050.0, ajuste.aplicar(1000.0), 0.001);
    }

    @Test
    void seguroDeveAcrescentarValorFixoDe490() {
        AjusteValor ajuste = new SeguroDecorator();
        assertEquals(1004.90, ajuste.aplicar(1000.0), 0.001);
    }

    @Test
    void taxaAntecipacaoDeveAcrescentarUmVirgulasCincoPorcento() {
        AjusteValor ajuste = new TaxaAntecipacaoReceiveisDecorator();
        assertEquals(1015.0, ajuste.aplicar(1000.0), 0.001);
    }

    @Test
    void taxaEmissaoNotaFiscalDeveAcrescentarValorFixoDe250() {
        AjusteValor ajuste = new TaxaEmissaoNotaFiscalDecorator();
        assertEquals(1002.50, ajuste.aplicar(1000.0), 0.001);
    }

    // --- Testes de composicao em diferentes ordens ---

    @Test
    void composicaoDescontoMaisJuros() {
        double valor = 1000.0;
        valor = new DescontoFidelidadeDecorator().aplicar(valor);
        valor = new JurosParcelamentoDecorator().aplicar(valor);

        double esperado = 1000.0 * 0.95 * 1.0299;
        assertEquals(esperado, valor, 0.001);
    }

    @Test
    void composicaoOrdemImportaQuandoHaValorFixo() {
        // desconto depois do seguro: (1000 + 4.90) * 0.95 = 954.655
        double seguroPrimeiro = new DescontoFidelidadeDecorator().aplicar(
                new SeguroDecorator().aplicar(1000.0));

        // seguro depois do desconto: (1000 * 0.95) + 4.90 = 954.90
        double descontoPrimeiro = new SeguroDecorator().aplicar(
                new DescontoFidelidadeDecorator().aplicar(1000.0));

        assertNotEquals(seguroPrimeiro, descontoPrimeiro, 0.001);
    }

    @Test
    void composicaoTodosOsAjustesOriginaisNaOrdemCorreta() {
        double valor = 1000.0;
        valor = new DescontoFidelidadeDecorator().aplicar(valor);
        valor = new JurosParcelamentoDecorator().aplicar(valor);
        valor = new TaxaInternacionalDecorator().aplicar(valor);
        valor = new SeguroDecorator().aplicar(valor);

        double esperado = 1000.0;
        esperado = esperado - esperado * 0.05;
        esperado = esperado + esperado * 0.0299;
        esperado = esperado + esperado * 0.05;
        esperado = esperado + 4.90;

        assertEquals(esperado, valor, 0.001);
    }

    @Test
    void composicaoNovosAjustesComExistentes() {
        double valor = 1000.0;
        valor = new DescontoFidelidadeDecorator().aplicar(valor);
        valor = new TaxaAntecipacaoReceiveisDecorator().aplicar(valor);
        valor = new TaxaEmissaoNotaFiscalDecorator().aplicar(valor);

        double esperado = 1000.0 * 0.95 * 1.015 + 2.50;
        assertEquals(esperado, valor, 0.001);
    }

    @Test
    void composicaoSomenteNovosAjustes() {
        double valor = 500.0;
        valor = new TaxaAntecipacaoReceiveisDecorator().aplicar(valor);
        valor = new TaxaEmissaoNotaFiscalDecorator().aplicar(valor);

        double esperado = 500.0 * 1.015 + 2.50;
        assertEquals(esperado, valor, 0.001);
    }

    @Test
    void composicaoSeguroETaxaNotaFiscalSaoAditivos() {
        double valor = 1000.0;
        valor = new SeguroDecorator().aplicar(valor);
        valor = new TaxaEmissaoNotaFiscalDecorator().aplicar(valor);

        assertEquals(1007.40, valor, 0.001);
    }

    @Test
    void aplicarApenasUmAjusteNaoInterferirNosOutros() {
        double base = 1000.0;
        double comDesconto = new DescontoFidelidadeDecorator().aplicar(base);
        double comJuros = new JurosParcelamentoDecorator().aplicar(base);

        assertEquals(950.0, comDesconto, 0.001);
        assertEquals(1029.9, comJuros, 0.001);
    }
}
