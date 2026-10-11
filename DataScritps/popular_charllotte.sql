-- DADOS DE TESTE - CHARLLOTTE (PostgreSQL)
-- Execute conectado ao banco "comissionesBB", depois de criar as tabelas.
-- Este script insere dados de exemplo em todas as tabelas.
--
-- ATENÇÃO:
-- * Os dados são fictícios.
-- * As senhas abaixo são apenas exemplos em texto simples. Em uma aplicação
--   real, use senhas armazenadas com hash compatível com o mecanismo de login.
-- * Execute preferencialmente em um banco de desenvolvimento vazio.
-- * O script limpa as tabelas do módulo antes de inserir os dados para evitar
--   duplicar os registros de exemplo. NÃO execute em banco com dados reais.
-- * As tabelas e colunas seguem as entidades compartilhadas.

BEGIN;

-- Limpa dados de teste anteriores e reinicia as identidades.
TRUNCATE TABLE
    aviso_funcionario,
    mensagem,
    movimentacao_estoque,
    fraude,
    comissao,
    item_venda,
    estoque,
    venda,
    aviso,
    produto,
    funcionario
RESTART IDENTITY CASCADE;

-- 1. Funcionários
INSERT INTO funcionario
    (nome, cpf, email, senha, status, online, admin)
VALUES
    ('Maria Gomes',      '111.111.111-11', 'maria@charllotte.com',      '123456', TRUE, FALSE, FALSE),
    ('Carlos Henrique',  '222.222.222-22', 'carlos@charllotte.com',     '123456', TRUE, FALSE, FALSE),
    ('Mariana Souza',    '333.333.333-33', 'mariana@charllotte.com',    '123456', TRUE, FALSE, FALSE),
    ('Pedro Oliveira',   '444.444.444-44', 'pedro@charllotte.com',      '123456', TRUE, FALSE, FALSE),
    ('Ana Carolina',     '555.555.555-55', 'ana@charllotte.com',        '123456', TRUE, FALSE, FALSE),
    ('Lucas Mendes',     '666.666.666-66', 'lucas@charllotte.com',      '123456', FALSE, FALSE, FALSE),
    ('Daniel Administrador', '777.777.777-77', 'daniel@charllotte.com', '123456', TRUE, FALSE, TRUE);

-- 2. Produtos
INSERT INTO produto
    (nome, preco, unidade, percentual_comissao, status, quantidade_estoque, estoque_minimo)
VALUES
    ('Café tradicional 500g', 18.90, 'UN', 5.00, TRUE, 80.00, 10.00),
    ('Arroz tipo 1 5kg',      29.90, 'UN', 3.50, TRUE, 45.00, 8.00),
    ('Feijão carioca 1kg',     8.50, 'UN', 4.00, TRUE, 60.00, 12.00),
    ('Açúcar refinado 1kg',    5.99, 'UN', 3.00, TRUE, 7.00, 10.00),
    ('Óleo de soja 900ml',     7.49, 'UN', 2.50, TRUE, 30.00, 8.00),
    ('Biscoito recheado',      3.99, 'UN', 2.00, FALSE, 0.00, 5.00);

-- 3. Avisos gerais
INSERT INTO aviso (titulo, mensagem, data_criacao)
VALUES
    ('Bem-vindos ao Charllotte', 'Bem-vindos ao sistema de gestão de vendas.', CURRENT_TIMESTAMP - INTERVAL '7 days'),
    ('Conferência de estoque', 'Confira os produtos próximos do estoque mínimo.', CURRENT_TIMESTAMP - INTERVAL '2 days'),
    ('Atualização do sistema', 'O módulo de relatórios recebeu melhorias.', CURRENT_TIMESTAMP - INTERVAL '1 day');

-- 4. Estoque (uma linha por produto, conforme relação OneToOne)
INSERT INTO estoque (id_produto, quantidade, estoque_minimo, data_atualizacao)
SELECT id_produto, quantidade_estoque, estoque_minimo, CURRENT_TIMESTAMP
FROM produto
ORDER BY id_produto;

-- 5. Vendas
INSERT INTO venda (id_funcionario, data_venda, valor_total, status)
VALUES
    (1, CURRENT_TIMESTAMP - INTERVAL '10 days', 56.80, 'REALIZADA'),
    (2, CURRENT_TIMESTAMP - INTERVAL '8 days',  59.80, 'REALIZADA'),
    (3, CURRENT_TIMESTAMP - INTERVAL '5 days',  25.50, 'REALIZADA'),
    (1, CURRENT_TIMESTAMP - INTERVAL '2 days',  37.80, 'EM_ANDAMENTO'),
    (4, CURRENT_TIMESTAMP - INTERVAL '1 day',    17.00, 'CANCELADA');

-- 6. Itens de venda
INSERT INTO item_venda (id_venda, id_produto, quantidade, preco_unitario, subtotal)
VALUES
    (1, 1, 2.000, 18.90, 37.80),
    (1, 3, 2.000,  8.50, 17.00),
    (1, 4, 1.000,  5.99,  5.99),
    (2, 2, 2.000, 29.90, 59.80),
    (3, 3, 3.000,  8.50, 25.50),
    (4, 5, 4.000,  7.49, 29.96),
    (5, 3, 2.000,  8.50, 17.00);

-- 7. Comissões
INSERT INTO comissao (id_venda, id_funcionario, percentual, valor, data)
VALUES
    (1, 1, 5.00, 2.84, CURRENT_TIMESTAMP - INTERVAL '10 days'),
    (2, 2, 3.50, 2.09, CURRENT_TIMESTAMP - INTERVAL '8 days'),
    (3, 3, 4.00, 1.02, CURRENT_TIMESTAMP - INTERVAL '5 days');

-- 8. Fraudes
INSERT INTO fraude (id_venda, tipo, descricao, data_identificacao, status)
VALUES
    (2, 'VALOR_ELEVADO', 'Venda sinalizada para revisão de valor.', CURRENT_TIMESTAMP - INTERVAL '8 days', 'PENDENTE'),
    (4, 'STATUS_INVALIDO', 'Venda em andamento marcada para conferência.', CURRENT_TIMESTAMP - INTERVAL '2 days', 'EM_ANALISE'),
    (5, 'VALOR_FORA_PADRAO', 'Venda cancelada encaminhada para análise.', CURRENT_TIMESTAMP - INTERVAL '1 day', 'DESCARTADA');

-- 9. Movimentações de estoque
INSERT INTO movimentacao_estoque
    (id_produto, id_funcionario, tipo, quantidade, data, motivo)
VALUES
    (1, 7, 'ENTRADA', 100.000, CURRENT_TIMESTAMP - INTERVAL '30 days', 'Estoque inicial'),
    (2, 7, 'ENTRADA',  60.000, CURRENT_TIMESTAMP - INTERVAL '20 days', 'Reposição de mercadoria'),
    (3, 1, 'SAIDA',     5.000, CURRENT_TIMESTAMP - INTERVAL '3 days', 'Ajuste após venda'),
    (4, 2, 'AJUSTE',    2.000, CURRENT_TIMESTAMP - INTERVAL '1 day', 'Contagem de estoque');

-- 10. Mensagens para funcionários
INSERT INTO mensagem (id_funcionario, mensagem, data_envio)
VALUES
    (1, 'Lembrete: conferir as vendas do dia.', CURRENT_TIMESTAMP - INTERVAL '2 days'),
    (2, 'Reposição de produtos prevista para amanhã.', CURRENT_TIMESTAMP - INTERVAL '1 day'),
    (3, 'Verifique as metas semanais de vendas.', CURRENT_TIMESTAMP);

-- 11. Avisos destinados a funcionários
INSERT INTO aviso_funcionario (id_aviso, id_funcionario, lido, data_leitura)
VALUES
    (1, 1, TRUE,  CURRENT_TIMESTAMP - INTERVAL '6 days'),
    (1, 2, FALSE, NULL),
    (2, 1, FALSE, NULL),
    (2, 3, TRUE,  CURRENT_TIMESTAMP - INTERVAL '1 day'),
    (3, 4, FALSE, NULL);

COMMIT;

-- Conferência rápida: quantidade de registros por tabela
SELECT 'funcionario' AS tabela, COUNT(*) AS registros FROM funcionario
UNION ALL SELECT 'produto', COUNT(*) FROM produto
UNION ALL SELECT 'aviso', COUNT(*) FROM aviso
UNION ALL SELECT 'venda', COUNT(*) FROM venda
UNION ALL SELECT 'estoque', COUNT(*) FROM estoque
UNION ALL SELECT 'item_venda', COUNT(*) FROM item_venda
UNION ALL SELECT 'comissao', COUNT(*) FROM comissao
UNION ALL SELECT 'fraude', COUNT(*) FROM fraude
UNION ALL SELECT 'movimentacao_estoque', COUNT(*) FROM movimentacao_estoque
UNION ALL SELECT 'mensagem', COUNT(*) FROM mensagem
UNION ALL SELECT 'aviso_funcionario', COUNT(*) FROM aviso_funcionario
ORDER BY tabela;
