package com.projeto.pessoal.TabelaFipe.principal;

import com.projeto.pessoal.TabelaFipe.model.Dados;
import com.projeto.pessoal.TabelaFipe.model.Modelos;
import com.projeto.pessoal.TabelaFipe.model.Veiculos;
import com.projeto.pessoal.TabelaFipe.service.ConsumoApi;
import com.projeto.pessoal.TabelaFipe.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Menu {
    private Scanner scanner = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados converteDados = new ConverteDados();
    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";

    public void exibeMenu() {
        var menu = """
                ***Menu Principal da Tabela Fipe***
                1 - Carros
                2 - Motos
                3 - Caminhões
                Informe a opção desejada:
                """;
        System.out.println(menu);
        var opcao = scanner.nextLine();
        String endereco;

        if (opcao.equals("1")) {
            endereco = URL_BASE + "carros/marcas";
        } else if (opcao.equals("2")) {
            endereco = URL_BASE + "motos/marcas";
        } else {
            endereco = URL_BASE + "caminhoes/marcas";
        }

        var json = consumoApi.obterDados(endereco);
        System.out.println(json);

        var marcas = converteDados.obterLista(json, Dados.class);
        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("\nInforme o código da marca para consulta: ");
        var codigoMarca = scanner.nextLine();
        endereco = endereco + "/" + codigoMarca + "/modelos";
        json = consumoApi.obterDados(endereco);
        var modeloLista = converteDados.obterDados(json, Modelos.class);

        System.out.println("\nModelos dessa marca: ");
        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("\nDigite um trecho do nome do veículo para consulta: ");
        var nomeVeiculo = scanner.nextLine();
        List<Dados> veiculosFiltrados = modeloLista.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .collect(Collectors.toList());

        System.out.println("\nModelos filtrados: ");
        veiculosFiltrados.forEach(System.out::println);

        System.out.println("\nDigite o código do veículo para consulta: ");
        var codigoVeiculo = scanner.nextLine();
        endereco = endereco + "/" + codigoVeiculo + "/anos";
        json = consumoApi.obterDados(endereco);
        List<Dados> anos = converteDados.obterLista(json, Dados.class);
        List<Veiculos> veiculos = new ArrayList<>();

        for (int i = 0; i < anos.size(); i++) {
            endereco = endereco + "/" + anos.get(i).codigo();
            json = consumoApi.obterDados(endereco);
            Veiculos veiculo = converteDados.obterDados(json, Veiculos.class);
            veiculos.add(veiculo);
        }

        System.out.println("\nTodos veículos filtrados por avaliações anuais: ");
        veiculos.forEach(System.out::println);
    }
}
