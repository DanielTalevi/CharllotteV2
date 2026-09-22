package comissiones.Charllotte.service;

import comissiones.Charllotte.model.Comissao;
import comissiones.Charllotte.model.Estoque;
import comissiones.Charllotte.model.Usuario;
import comissiones.Charllotte.model.Venda;

import comissiones.Charllotte.repository.ComissaoRepository;
import comissiones.Charllotte.repository.EstoqueRepository;
import comissiones.Charllotte.repository.UsuarioRepository;
import comissiones.Charllotte.repository.VendaRepository;

import org.openpdf.text.Document;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.FontFactory;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;

import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class RelatorioService {

    private final VendaRepository vendaRepository;
    private final EstoqueRepository estoqueRepository;
    private final UsuarioRepository usuarioRepository;
    private final ComissaoRepository comissaoRepository;

    private final DateTimeFormatter formatoData =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final NumberFormat formatoMoeda =
            NumberFormat.getCurrencyInstance(
                    Locale.of("pt", "BR")
            );


    // =====================================================
    // CONSTRUTOR
    // =====================================================

    public RelatorioService(
            VendaRepository vendaRepository,
            EstoqueRepository estoqueRepository,
            UsuarioRepository usuarioRepository,
            ComissaoRepository comissaoRepository) {

        this.vendaRepository = vendaRepository;
        this.estoqueRepository = estoqueRepository;
        this.usuarioRepository = usuarioRepository;
        this.comissaoRepository = comissaoRepository;
    }


    // =====================================================
    // RELATÓRIO DE VENDAS
    // =====================================================

    @Transactional(readOnly = true)
    public byte[] gerarRelatorioVendas() {

        List<Venda> vendas =
                vendaRepository.findAll();

        ByteArrayOutputStream saida =
                new ByteArrayOutputStream();

        Document documento =
                new Document(
                        PageSize.A4,
                        36,
                        36,
                        40,
                        40
                );

        try {

            PdfWriter.getInstance(
                    documento,
                    saida
            );

            documento.open();


            // =============================================
            // FONTES
            // =============================================

            Font fonteTitulo =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            20
                    );

            Font fonteNormal =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            10
                    );

            Font fonteResumo =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            12
                    );


            // =============================================
            // TÍTULO
            // =============================================

            Paragraph titulo =
                    new Paragraph(
                            "Relatório de Vendas - Charllotte",
                            fonteTitulo
                    );

            titulo.setAlignment(
                    Element.ALIGN_CENTER
            );

            titulo.setSpacingAfter(10);

            documento.add(titulo);


            // =============================================
            // DESCRIÇÃO
            // =============================================

            Paragraph informacao =
                    new Paragraph(
                            "Relatório geral de vendas",
                            fonteNormal
                    );

            informacao.setAlignment(
                    Element.ALIGN_CENTER
            );

            informacao.setSpacingAfter(20);

            documento.add(informacao);


            // =============================================
            // TABELA
            // =============================================

            PdfPTable tabela =
                    new PdfPTable(5);

            tabela.setWidthPercentage(100);

            tabela.setWidths(
                    new float[]{
                            1.0f,
                            2.5f,
                            2.0f,
                            2.0f,
                            2.0f
                    }
            );


            adicionarCabecalho(
                    tabela,
                    "ID"
            );

            adicionarCabecalho(
                    tabela,
                    "Funcionário"
            );

            adicionarCabecalho(
                    tabela,
                    "Data"
            );

            adicionarCabecalho(
                    tabela,
                    "Valor"
            );

            adicionarCabecalho(
                    tabela,
                    "Status"
            );


            // =============================================
            // VENDAS
            // =============================================

            BigDecimal totalVendido =
                    BigDecimal.ZERO;


            for (Venda venda : vendas) {

                adicionarCelula(
                        tabela,
                        venda.getId() != null
                                ? String.valueOf(
                                venda.getId())
                                : "-"
                );


                String nomeFuncionario =
                        venda.getFuncionario() != null
                                ? venda.getFuncionario().getNome()
                                : "Não informado";

                adicionarCelula(
                        tabela,
                        nomeFuncionario
                );


                String data =
                        venda.getDataVenda() != null
                                ? venda.getDataVenda()
                                .format(formatoData)
                                : "-";

                adicionarCelula(
                        tabela,
                        data
                );


                BigDecimal valor =
                        venda.getValorTotal() != null
                                ? venda.getValorTotal()
                                : BigDecimal.ZERO;

                adicionarCelula(
                        tabela,
                        formatoMoeda.format(valor)
                );


                String status =
                        venda.getStatus() != null
                                ? venda.getStatus().toString()
                                : "-";

                adicionarCelula(
                        tabela,
                        status
                );


                totalVendido =
                        totalVendido.add(valor);
            }


            documento.add(tabela);


            // =============================================
            // RESUMO
            // =============================================

            documento.add(
                    new Paragraph(" ")
            );


            Paragraph total =
                    new Paragraph(
                            "Total de vendas: "
                                    + vendas.size(),
                            fonteResumo
                    );

            total.setSpacingAfter(5);

            documento.add(total);


            Paragraph faturamento =
                    new Paragraph(
                            "Total vendido: "
                                    + formatoMoeda.format(
                                    totalVendido
                            ),
                            fonteResumo
                    );

            documento.add(faturamento);


        } catch (Exception erro) {

            throw new RuntimeException(
                    "Erro ao gerar relatório de vendas.",
                    erro
            );

        } finally {

            documento.close();
        }


        return saida.toByteArray();
    }


    // =====================================================
    // RELATÓRIO DE ESTOQUE
    // =====================================================

    @Transactional(readOnly = true)
    public byte[] gerarRelatorioEstoque() {

        List<Estoque> estoques =
                estoqueRepository.findAll();

        ByteArrayOutputStream saida =
                new ByteArrayOutputStream();

        Document documento =
                new Document(
                        PageSize.A4,
                        36,
                        36,
                        40,
                        40
                );

        try {

            PdfWriter.getInstance(
                    documento,
                    saida
            );

            documento.open();


            // =============================================
            // FONTES
            // =============================================

            Font fonteTitulo =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            20
                    );

            Font fonteNormal =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            10
                    );

            Font fonteResumo =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            12
                    );


            // =============================================
            // TÍTULO
            // =============================================

            Paragraph titulo =
                    new Paragraph(
                            "Relatório de Estoque - Charllotte",
                            fonteTitulo
                    );

            titulo.setAlignment(
                    Element.ALIGN_CENTER
            );

            titulo.setSpacingAfter(10);

            documento.add(titulo);


            // =============================================
            // DESCRIÇÃO
            // =============================================

            Paragraph descricao =
                    new Paragraph(
                            "Situação atual dos produtos armazenados",
                            fonteNormal
                    );

            descricao.setAlignment(
                    Element.ALIGN_CENTER
            );

            descricao.setSpacingAfter(20);

            documento.add(descricao);


            // =============================================
            // TABELA
            // =============================================

            PdfPTable tabela =
                    new PdfPTable(5);

            tabela.setWidthPercentage(100);

            tabela.setWidths(
                    new float[]{
                            1.0f,
                            3.0f,
                            1.5f,
                            1.5f,
                            2.0f
                    }
            );


            adicionarCabecalho(
                    tabela,
                    "ID"
            );

            adicionarCabecalho(
                    tabela,
                    "Produto"
            );

            adicionarCabecalho(
                    tabela,
                    "Quantidade"
            );

            adicionarCabecalho(
                    tabela,
                    "Mínimo"
            );

            adicionarCabecalho(
                    tabela,
                    "Situação"
            );


            // =============================================
            // ESTOQUE
            // =============================================

            for (Estoque estoque : estoques) {

                adicionarCelula(
                        tabela,
                        estoque.getId() != null
                                ? String.valueOf(
                                estoque.getId())
                                : "-"
                );


                String nomeProduto =
                        estoque.getProduto() != null
                                ? estoque.getProduto().getNome()
                                : "Não informado";

                adicionarCelula(
                        tabela,
                        nomeProduto
                );


                BigDecimal quantidade =
                        estoque.getQuantidade() != null
                                ? estoque.getQuantidade()
                                : BigDecimal.ZERO;

                adicionarCelula(
                        tabela,
                        quantidade.toString()
                );


                BigDecimal estoqueMinimo =
                        estoque.getEstoqueMinimo() != null
                                ? estoque.getEstoqueMinimo()
                                : BigDecimal.ZERO;

                adicionarCelula(
                        tabela,
                        estoqueMinimo.toString()
                );


                String situacao;

                if (quantidade.compareTo(
                        estoqueMinimo
                ) <= 0) {

                    situacao = "ESTOQUE BAIXO";

                } else {

                    situacao = "NORMAL";
                }


                adicionarCelula(
                        tabela,
                        situacao
                );
            }


            documento.add(tabela);


            // =============================================
            // RESUMO
            // =============================================

            documento.add(
                    new Paragraph(" ")
            );


            Paragraph totalProdutos =
                    new Paragraph(
                            "Total de registros de estoque: "
                                    + estoques.size(),
                            fonteResumo
                    );

            documento.add(totalProdutos);


            long produtosBaixos =
                    estoques.stream()
                            .filter(estoque -> {

                                BigDecimal quantidade =
                                        estoque.getQuantidade()
                                                != null
                                                ? estoque.getQuantidade()
                                                : BigDecimal.ZERO;

                                BigDecimal minimo =
                                        estoque.getEstoqueMinimo()
                                                != null
                                                ? estoque.getEstoqueMinimo()
                                                : BigDecimal.ZERO;

                                return quantidade.compareTo(
                                        minimo
                                ) <= 0;

                            })
                            .count();


            Paragraph estoqueBaixo =
                    new Paragraph(
                            "Produtos com estoque baixo: "
                                    + produtosBaixos,
                            fonteResumo
                    );

            documento.add(estoqueBaixo);


        } catch (Exception erro) {

            throw new RuntimeException(
                    "Erro ao gerar relatório de estoque.",
                    erro
            );

        } finally {

            documento.close();
        }


        return saida.toByteArray();
    }


    // =====================================================
    // RELATÓRIO DE FUNCIONÁRIOS
    // =====================================================

    @Transactional(readOnly = true)
    public byte[] gerarRelatorioFuncionarios() {

        List<Usuario> usuarios =
                usuarioRepository.findAll();

        ByteArrayOutputStream saida =
                new ByteArrayOutputStream();

        Document documento =
                new Document(
                        PageSize.A4,
                        36,
                        36,
                        40,
                        40
                );

        try {

            PdfWriter.getInstance(
                    documento,
                    saida
            );

            documento.open();


            // =============================================
            // FONTES
            // =============================================

            Font fonteTitulo =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            18
                    );

            Font fonteNormal =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            10
                    );

            Font fonteResumo =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            12
                    );


            // =============================================
            // TÍTULO
            // =============================================

            Paragraph titulo =
                    new Paragraph(
                            "Relatório de Funcionários - Charllotte",
                            fonteTitulo
                    );

            titulo.setAlignment(
                    Element.ALIGN_CENTER
            );

            titulo.setSpacingAfter(10);

            documento.add(titulo);


            // =============================================
            // DESCRIÇÃO
            // =============================================

            Paragraph descricao =
                    new Paragraph(
                            "Dados e situação dos funcionários cadastrados",
                            fonteNormal
                    );

            descricao.setAlignment(
                    Element.ALIGN_CENTER
            );

            descricao.setSpacingAfter(20);

            documento.add(descricao);


            // =============================================
            // TABELA
            // =============================================

            PdfPTable tabela =
                    new PdfPTable(5);

            tabela.setWidthPercentage(100);

            tabela.setWidths(
                    new float[]{
                            0.8f,
                            2.5f,
                            2.0f,
                            3.0f,
                            1.3f
                    }
            );


            adicionarCabecalho(
                    tabela,
                    "ID"
            );

            adicionarCabecalho(
                    tabela,
                    "Nome"
            );

            adicionarCabecalho(
                    tabela,
                    "CPF"
            );

            adicionarCabecalho(
                    tabela,
                    "E-mail"
            );

            adicionarCabecalho(
                    tabela,
                    "Status"
            );


            // =============================================
            // FUNCIONÁRIOS
            // =============================================

            long ativos = 0;
            long inativos = 0;


            for (Usuario usuario : usuarios) {

                adicionarCelula(
                        tabela,
                        usuario.getId() != null
                                ? String.valueOf(
                                usuario.getId())
                                : "-"
                );


                adicionarCelula(
                        tabela,
                        usuario.getNome()
                );


                adicionarCelula(
                        tabela,
                        usuario.getCpf()
                );


                adicionarCelula(
                        tabela,
                        usuario.getEmail()
                );


                boolean ativo =
                        Boolean.TRUE.equals(
                                usuario.getStatus()
                        );


                String status =
                        ativo
                                ? "ATIVO"
                                : "INATIVO";


                adicionarCelula(
                        tabela,
                        status
                );


                if (ativo) {

                    ativos++;

                } else {

                    inativos++;
                }
            }


            documento.add(tabela);


            // =============================================
            // RESUMO
            // =============================================

            documento.add(
                    new Paragraph(" ")
            );


            Paragraph total =
                    new Paragraph(
                            "Total de funcionários: "
                                    + usuarios.size(),
                            fonteResumo
                    );

            documento.add(total);


            Paragraph totalAtivos =
                    new Paragraph(
                            "Funcionários ativos: "
                                    + ativos,
                            fonteResumo
                    );

            documento.add(totalAtivos);


            Paragraph totalInativos =
                    new Paragraph(
                            "Funcionários inativos: "
                                    + inativos,
                            fonteResumo
                    );

            documento.add(totalInativos);


        } catch (Exception erro) {

            throw new RuntimeException(
                    "Erro ao gerar relatório de funcionários.",
                    erro
            );

        } finally {

            documento.close();
        }


        return saida.toByteArray();
    }


    // =====================================================
    // RELATÓRIO DE COMISSÕES
    // =====================================================

    @Transactional(readOnly = true)
    public byte[] gerarRelatorioComissoes() {

        List<Comissao> comissoes =
                comissaoRepository.findAll();

        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        Document document =
                new Document(
                        PageSize.A4,
                        36,
                        36,
                        40,
                        40
                );

        try {

            PdfWriter.getInstance(
                    document,
                    outputStream
            );

            document.open();


            // =============================================
            // FONTES
            // =============================================

            Font titulo =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            18
                    );

            Font normal =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            10
                    );

            Font cabecalho =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            9
                    );

            Font totalFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            12
                    );


            // =============================================
            // TÍTULO
            // =============================================

            Paragraph tituloPdf =
                    new Paragraph(
                            "RELATÓRIO DE COMISSÕES",
                            titulo
                    );

            tituloPdf.setAlignment(
                    Element.ALIGN_CENTER
            );

            document.add(tituloPdf);


            document.add(
                    new Paragraph(" ")
            );


            Paragraph descricao =
                    new Paragraph(
                            "Comissões registradas no sistema",
                            normal
                    );

            descricao.setAlignment(
                    Element.ALIGN_CENTER
            );

            document.add(descricao);


            document.add(
                    new Paragraph(" ")
            );


            // =============================================
            // TABELA
            // =============================================

            PdfPTable tabela =
                    new PdfPTable(7);

            tabela.setWidthPercentage(100);

            tabela.setWidths(
                    new float[]{
                            0.7f,
                            2.2f,
                            1.5f,
                            1.4f,
                            1.1f,
                            1.2f,
                            1.3f
                    }
            );


            adicionarCelula(
                    tabela,
                    "ID",
                    cabecalho
            );

            adicionarCelula(
                    tabela,
                    "Funcionário",
                    cabecalho
            );

            adicionarCelula(
                    tabela,
                    "Data",
                    cabecalho
            );

            adicionarCelula(
                    tabela,
                    "Venda",
                    cabecalho
            );

            adicionarCelula(
                    tabela,
                    "%",
                    cabecalho
            );

            adicionarCelula(
                    tabela,
                    "Valor Venda",
                    cabecalho
            );

            adicionarCelula(
                    tabela,
                    "Comissão",
                    cabecalho
            );


            // =============================================
            // COMISSÕES
            // =============================================

            BigDecimal totalComissao =
                    BigDecimal.ZERO;


            for (Comissao comissao : comissoes) {

                String nomeFuncionario =
                        comissao.getFuncionario() != null
                                ? comissao
                                .getFuncionario()
                                .getNome()
                                : "-";


                String data =
                        comissao.getData() != null
                                ? comissao
                                .getData()
                                .format(formatoData)
                                : "-";


                String idVenda =
                        comissao.getVenda() != null
                                && comissao
                                .getVenda()
                                .getId() != null
                                ? String.valueOf(
                                comissao
                                        .getVenda()
                                        .getId())
                                : "-";


                BigDecimal valorVenda =
                        comissao.getVenda() != null
                                && comissao
                                .getVenda()
                                .getValorTotal() != null
                                ? comissao
                                .getVenda()
                                .getValorTotal()
                                : BigDecimal.ZERO;


                BigDecimal percentual =
                        comissao.getPercentual() != null
                                ? comissao.getPercentual()
                                : BigDecimal.ZERO;


                BigDecimal valorComissao =
                        comissao.getValor() != null
                                ? comissao.getValor()
                                : BigDecimal.ZERO;


                adicionarCelula(
                        tabela,
                        comissao.getId() != null
                                ? String.valueOf(
                                comissao.getId())
                                : "-",
                        normal
                );


                adicionarCelula(
                        tabela,
                        nomeFuncionario,
                        normal
                );


                adicionarCelula(
                        tabela,
                        data,
                        normal
                );


                adicionarCelula(
                        tabela,
                        idVenda,
                        normal
                );


                adicionarCelula(
                        tabela,
                        percentual
                                .setScale(
                                        2,
                                        RoundingMode.HALF_UP
                                )
                                .toPlainString()
                                + "%",
                        normal
                );


                adicionarCelula(
                        tabela,
                        formatoMoeda.format(
                                valorVenda
                        ),
                        normal
                );


                adicionarCelula(
                        tabela,
                        formatoMoeda.format(
                                valorComissao
                        ),
                        normal
                );


                totalComissao =
                        totalComissao.add(
                                valorComissao
                        );
            }


            document.add(tabela);


            // =============================================
            // TOTAL
            // =============================================

            document.add(
                    new Paragraph(" ")
            );


            Paragraph total =
                    new Paragraph(
                            "TOTAL DE COMISSÕES: "
                                    + formatoMoeda.format(
                                    totalComissao
                            ),
                            totalFont
                    );

            total.setAlignment(
                    Element.ALIGN_RIGHT
            );

            document.add(total);


        } catch (Exception erro) {

            throw new RuntimeException(
                    "Erro ao gerar relatório de comissões.",
                    erro
            );

        } finally {

            document.close();
        }


        return outputStream.toByteArray();
    }


    // =====================================================
    // CABEÇALHO DA TABELA
    // =====================================================

    private void adicionarCabecalho(
            PdfPTable tabela,
            String texto) {

        Font fonte =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        9
                );

        PdfPCell celula =
                new PdfPCell(
                        new Phrase(
                                texto != null
                                        ? texto
                                        : "-",
                                fonte
                        )
                );

        celula.setHorizontalAlignment(
                Element.ALIGN_CENTER
        );

        celula.setPadding(6);

        tabela.addCell(celula);
    }


    // =====================================================
    // CÉLULA NORMAL
    // =====================================================

    private void adicionarCelula(
            PdfPTable tabela,
            String texto) {

        Font fonte =
                FontFactory.getFont(
                        FontFactory.HELVETICA,
                        8
                );

        adicionarCelula(
                tabela,
                texto,
                fonte
        );
    }


    // =====================================================
    // CÉLULA COM FONTE PERSONALIZADA
    // =====================================================

    private void adicionarCelula(
            PdfPTable tabela,
            String texto,
            Font fonte) {

        PdfPCell celula =
                new PdfPCell(
                        new Phrase(
                                texto != null
                                        ? texto
                                        : "-",
                                fonte
                        )
                );

        celula.setPadding(5);

        tabela.addCell(celula);
    }
}