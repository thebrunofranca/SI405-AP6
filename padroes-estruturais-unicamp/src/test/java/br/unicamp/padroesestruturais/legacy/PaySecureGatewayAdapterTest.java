package br.unicamp.padroesestruturais.legacy;

import br.unicamp.padroesestruturais.legacy.domain.FormaPagamento;
import br.unicamp.padroesestruturais.legacy.domain.Pedido;
import br.unicamp.padroesestruturais.legacy.domain.ResultadoCobranca;
import br.unicamp.padroesestruturais.legacy.gateway.PaySecureGatewayAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaySecureGatewayAdapterTest {

    private PaySecureGatewayAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new PaySecureGatewayAdapter();
    }

    @Test
    void deveAprovarCobrancaDentroDoLimite() {
        Pedido pedido = new Pedido("PED-001", "Joao Silva", "Notebook", 500.0);

        ResultadoCobranca resultado = adapter.cobrar(pedido, 500.0, FormaPagamento.CARTAO_CREDITO);

        assertEquals("APROVADA", resultado.getStatus());
        assertNotNull(resultado.getReferencia());
        assertTrue(resultado.getReferencia().startsWith("PSEC-"));
        assertEquals(500.0, resultado.getValorCobrado(), 0.001);
        assertEquals(FormaPagamento.CARTAO_CREDITO, resultado.getFormaPagamento());
        assertEquals("PED-001", resultado.getPedidoId());
    }

    @Test
    void deveRecusarCobrancaAcimaDoLimite() {
        Pedido pedido = new Pedido("PED-003", "Construtora ABC Ltda", "Servidor", 15000.0);

        ResultadoCobranca resultado = adapter.cobrar(pedido, 15000.0, FormaPagamento.CARTAO_CREDITO);

        assertEquals("RECUSADA", resultado.getStatus());
        assertEquals("PED-003", resultado.getPedidoId());
    }

    @Test
    void deveRetornarRecusadaParaValorInvalido() {
        Pedido pedido = new Pedido("PED-004", "Cliente X", "Produto", -10.0);

        ResultadoCobranca resultado = adapter.cobrar(pedido, -10.0, FormaPagamento.CARTAO_CREDITO);

        assertEquals("RECUSADA", resultado.getStatus());
        assertNull(resultado.getReferencia());
    }

    @Test
    void deveAdaptarInterfaceDoGatewayExternoPropriamente() {
        Pedido pedido = new Pedido("PED-005", "Maria Santos", "Cadeira", 1200.0);

        ResultadoCobranca resultado = adapter.cobrar(pedido, 1200.0, FormaPagamento.CARTAO_CREDITO);

        assertNotNull(resultado);
        assertEquals("PED-005", resultado.getPedidoId());
        assertEquals(1200.0, resultado.getValorCobrado(), 0.001);
    }
}
