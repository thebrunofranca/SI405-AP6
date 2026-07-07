package br.unicamp.padroesestruturais.legacy.service;

import br.unicamp.padroesestruturais.legacy.ajuste.AjusteValor;
import br.unicamp.padroesestruturais.legacy.domain.FormaPagamento;
import br.unicamp.padroesestruturais.legacy.domain.Pedido;
import br.unicamp.padroesestruturais.legacy.domain.ResultadoCobranca;
import br.unicamp.padroesestruturais.legacy.gateway.GatewayCobranca;
import br.unicamp.padroesestruturais.legacy.gateway.GatewayPagamentoInternoAdapter;
import br.unicamp.padroesestruturais.legacy.gateway.PaySecureGatewayAdapter;
import br.unicamp.padroesestruturais.legacy.gateway.WalletPaySDKAdapter;

import java.util.ArrayList;
import java.util.List;

public class CobrancaService {

    private GatewayCobranca selecionarGateway(FormaPagamento forma) {
        if (forma == null) {
            throw new IllegalArgumentException("Forma de pagamento nao suportada: null");
        }
        return switch (forma) {
            case BOLETO, PIX -> new GatewayPagamentoInternoAdapter();
            case CARTAO_CREDITO -> new PaySecureGatewayAdapter();
            case CARTEIRA_DIGITAL -> new WalletPaySDKAdapter();
        };
    }

    public ResultadoCobranca cobrar(Pedido pedido, FormaPagamento forma, AjusteValor... ajustes) {
        double valorFinal = calcularValorFinal(pedido.getValorBase(), ajustes);
        return selecionarGateway(forma).cobrar(pedido, valorFinal, forma);
    }

    public List<ResultadoCobranca> cobrarEmLote(List<Pedido> pedidos, FormaPagamento forma, AjusteValor... ajustes) {
        GatewayCobranca gateway = selecionarGateway(forma);
        List<ResultadoCobranca> resultados = new ArrayList<>();
        for (Pedido pedido : pedidos) {
            double valorFinal = calcularValorFinal(pedido.getValorBase(), ajustes);
            resultados.add(gateway.cobrar(pedido, valorFinal, forma));
        }
        return resultados;
    }

    public double calcularValorFinal(double valorBase, AjusteValor... ajustes) {
        double valor = valorBase;
        for (AjusteValor ajuste : ajustes) {
            valor = ajuste.aplicar(valor);
        }
        return valor;
    }
}
