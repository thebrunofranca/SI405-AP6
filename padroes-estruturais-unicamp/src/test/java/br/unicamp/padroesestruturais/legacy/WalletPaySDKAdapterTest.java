package br.unicamp.padroesestruturais.legacy;

import br.unicamp.padroesestruturais.legacy.domain.FormaPagamento;
import br.unicamp.padroesestruturais.legacy.domain.Pedido;
import br.unicamp.padroesestruturais.legacy.domain.ResultadoCobranca;
import br.unicamp.padroesestruturais.legacy.gateway.WalletPaySDKAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WalletPaySDKAdapterTest {

    private WalletPaySDKAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new WalletPaySDKAdapter();
    }

    @Test
    void deveAprovarCobrancaDentroDoLimite() {
        Pedido pedido = new Pedido("PED-001", "Joao Silva", "Notebook", 500.0);

        ResultadoCobranca resultado = adapter.cobrar(pedido, 500.0, FormaPagamento.CARTEIRA_DIGITAL);

        assertEquals("APROVADA", resultado.getStatus());
        assertNotNull(resultado.getReferencia());
        assertTrue(resultado.getReferencia().startsWith("WPAY-"));
        assertEquals(500.0, resultado.getValorCobrado(), 0.001);
        assertEquals(FormaPagamento.CARTEIRA_DIGITAL, resultado.getFormaPagamento());
        assertEquals("PED-001", resultado.getPedidoId());
    }

    @Test
    void deveRecusarCobrancaAcimaDoLimite() {
        Pedido pedido = new Pedido("PED-003", "Construtora ABC Ltda", "Servidor", 15000.0);

        ResultadoCobranca resultado = adapter.cobrar(pedido, 15000.0, FormaPagamento.CARTEIRA_DIGITAL);

        assertEquals("RECUSADA", resultado.getStatus());
        assertEquals("PED-003", resultado.getPedidoId());
    }

    @Test
    void deveRetornarRecusadaParaValorZero() {
        Pedido pedido = new Pedido("PED-004", "Cliente X", "Produto", 0.0);

        ResultadoCobranca resultado = adapter.cobrar(pedido, 0.0, FormaPagamento.CARTEIRA_DIGITAL);

        assertEquals("RECUSADA", resultado.getStatus());
        assertNull(resultado.getReferencia());
    }

    @Test
    void deveConverterValorParaCentavosCorretamente() {
        Pedido pedido = new Pedido("PED-006", "Ana Lima", "Fone", 99.99);

        ResultadoCobranca resultado = adapter.cobrar(pedido, 99.99, FormaPagamento.CARTEIRA_DIGITAL);

        assertNotNull(resultado);
        assertEquals("APROVADA", resultado.getStatus());
        assertEquals(99.99, resultado.getValorCobrado(), 0.001);
    }

    @Test
    void deveAdaptarInterfaceDoSDKExternoPropriamente() {
        Pedido pedido = new Pedido("PED-007", "Carlos Mendes", "Tablet", 2500.0);

        ResultadoCobranca resultado = adapter.cobrar(pedido, 2500.0, FormaPagamento.CARTEIRA_DIGITAL);

        assertNotNull(resultado);
        assertEquals("PED-007", resultado.getPedidoId());
        assertEquals(2500.0, resultado.getValorCobrado(), 0.001);
        assertEquals(FormaPagamento.CARTEIRA_DIGITAL, resultado.getFormaPagamento());
    }
}
