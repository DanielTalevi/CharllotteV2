-- CONSULTAS SQL - CHARLLOTTE (PostgreSQL)
-- Execute conectado ao banco "comissionesBB".
-- São consultas de leitura (SELECT), sem alteração dos dados.

-- ============================================================
-- 1. FUNCIONÁRIOS
-- ============================================================

-- Listar todos os funcionários
SELECT *
FROM funcionario
ORDER BY nome;

-- Listar apenas funcionários ativos
SELECT id_funcionario, nome, email, cpf, admin, online
FROM funcionario
WHERE status = TRUE
ORDER BY nome;

-- Buscar funcionário por nome (substitua o texto)
SELECT id_funcionario, nome, email, status, admin
FROM funcionario
WHERE nome ILIKE '%Maria%'
ORDER BY nome;

-- Listar funcionários administradores
SELECT id_funcionario, nome, email
FROM funcionario
WHERE admin = TRUE
ORDER BY nome;

-- Funcionários atualmente marcados como online
SELECT id_funcionario, nome, email
FROM funcionario
WHERE online = TRUE
ORDER BY nome;


-- ============================================================
-- 2. PRODUTOS E ESTOQUE
-- ============================================================

-- Listar produtos ativos
SELECT id_produto, nome, preco, unidade, percentual_comissao,
       quantidade_estoque, estoque_minimo
FROM produto
WHERE status = TRUE
ORDER BY nome;

-- Produtos com estoque abaixo ou igual ao mínimo (colunas da tabela produto)
SELECT id_produto, nome, quantidade_estoque, estoque_minimo
FROM produto
WHERE quantidade_estoque <= estoque_minimo
ORDER BY quantidade_estoque, nome;

-- Consultar dados da tabela estoque com o produto correspondente
SELECT e.id_estoque, p.id_produto, p.nome,
       e.quantidade, e.estoque_minimo, e.data_atualizacao
FROM estoque e
JOIN produto p ON p.id_produto = e.id_produto
ORDER BY p.nome;

-- Pesquisar produto pelo nome
SELECT id_produto, nome, preco, unidade, quantidade_estoque
FROM produto
WHERE nome ILIKE '%café%'
ORDER BY nome;

-- Histórico de movimentações do estoque
SELECT me.id_movimentacao_estoque, p.nome AS produto,
       f.nome AS funcionario, me.tipo, me.quantidade,
       me.data, me.motivo
FROM movimentacao_estoque me
JOIN produto p ON p.id_produto = me.id_produto
JOIN funcionario f ON f.id_funcionario = me.id_funcionario
ORDER BY me.data DESC;


-- ============================================================
-- 3. VENDAS
-- ============================================================

-- Listar todas as vendas com o funcionário responsável
SELECT v.id_venda, v.data_venda, f.nome AS funcionario,
       v.valor_total, v.status
FROM venda v
JOIN funcionario f ON f.id_funcionario = v.id_funcionario
ORDER BY v.data_venda DESC;

-- Consultar vendas de um funcionário específico (substitua o ID)
SELECT v.id_venda, v.data_venda, v.valor_total, v.status
FROM venda v
WHERE v.id_funcionario = 1
ORDER BY v.data_venda DESC;

-- Consultar vendas por status
SELECT id_venda, data_venda, valor_total, status
FROM venda
WHERE status = 'REALIZADA'
ORDER BY data_venda DESC;

-- Consultar vendas de um período (ajuste as datas)
SELECT id_venda, data_venda, valor_total, status
FROM venda
WHERE data_venda >= TIMESTAMP '2026-01-01 00:00:00'
  AND data_venda <  TIMESTAMP '2027-01-01 00:00:00'
ORDER BY data_venda DESC;

-- Itens de uma venda específica (substitua o ID da venda)
SELECT iv.id_item_venda, iv.id_venda, p.nome AS produto,
       iv.quantidade, iv.preco_unitario, iv.subtotal
FROM item_venda iv
JOIN produto p ON p.id_produto = iv.id_produto
WHERE iv.id_venda = 1
ORDER BY iv.id_item_venda;


-- ============================================================
-- 4. COMISSÕES
-- ============================================================

-- Listar comissões com funcionário e venda
SELECT c.id_comissao, f.nome AS funcionario, c.id_venda,
       c.percentual, c.valor, c.data
FROM comissao c
JOIN funcionario f ON f.id_funcionario = c.id_funcionario
ORDER BY c.data DESC;

-- Total de comissão por funcionário
SELECT f.id_funcionario, f.nome,
       COALESCE(SUM(c.valor), 0) AS total_comissao
FROM funcionario f
LEFT JOIN comissao c ON c.id_funcionario = f.id_funcionario
GROUP BY f.id_funcionario, f.nome
ORDER BY total_comissao DESC;

-- Total de comissões por mês
SELECT DATE_TRUNC('month', c.data)::date AS mes,
       SUM(c.valor) AS total_comissoes
FROM comissao c
GROUP BY DATE_TRUNC('month', c.data)
ORDER BY mes DESC;

-- Comissões de uma venda específica
SELECT c.id_comissao, c.percentual, c.valor, c.data,
       f.nome AS funcionario
FROM comissao c
JOIN funcionario f ON f.id_funcionario = c.id_funcionario
WHERE c.id_venda = 1
ORDER BY c.data DESC;


-- ============================================================
-- 5. FRAUDES
-- ============================================================

-- Listar fraudes com informações da venda
SELECT fr.id_fraude, fr.id_venda, fr.tipo, fr.descricao,
       fr.data_identificacao, fr.status,
       v.valor_total, f.nome AS funcionario
FROM fraude fr
JOIN venda v ON v.id_venda = fr.id_venda
JOIN funcionario f ON f.id_funcionario = v.id_funcionario
ORDER BY fr.data_identificacao DESC;

-- Fraudes pendentes
SELECT id_fraude, id_venda, tipo, descricao, data_identificacao, status
FROM fraude
WHERE status = 'PENDENTE'
ORDER BY data_identificacao DESC;

-- Contagem de fraudes por status
SELECT status, COUNT(*) AS quantidade
FROM fraude
GROUP BY status
ORDER BY quantidade DESC;


-- ============================================================
-- 6. AVISOS
-- ============================================================

-- Listar avisos mais recentes
SELECT id_aviso, titulo, mensagem, data_criacao
FROM aviso
ORDER BY data_criacao DESC;

-- Avisos recebidos por um funcionário (substitua o ID)
SELECT a.id_aviso, a.titulo, a.mensagem, a.data_criacao,
       af.lido, af.data_leitura
FROM aviso_funcionario af
JOIN aviso a ON a.id_aviso = af.id_aviso
WHERE af.id_funcionario = 1
ORDER BY a.data_criacao DESC;

-- Avisos ainda não lidos por um funcionário (substitua o ID)
SELECT a.id_aviso, a.titulo, a.data_criacao
FROM aviso_funcionario af
JOIN aviso a ON a.id_aviso = af.id_aviso
WHERE af.id_funcionario = 1
  AND af.lido = FALSE
ORDER BY a.data_criacao DESC;


-- ============================================================
-- 7. MENSAGENS
-- ============================================================

-- Histórico de mensagens com funcionário
SELECT m.id_mensagem, f.nome AS funcionario,
       m.mensagem, m.data_envio
FROM mensagem m
JOIN funcionario f ON f.id_funcionario = m.id_funcionario
ORDER BY m.data_envio DESC;


-- ============================================================
-- 8. RESUMOS E INDICADORES
-- ============================================================

-- Quantidade de vendas por status
SELECT status, COUNT(*) AS quantidade_vendas
FROM venda
GROUP BY status
ORDER BY quantidade_vendas DESC;

-- Faturamento de vendas realizadas
SELECT COALESCE(SUM(valor_total), 0) AS faturamento_total
FROM venda
WHERE status = 'REALIZADA';

-- Faturamento por funcionário (somente vendas realizadas)
SELECT f.id_funcionario, f.nome,
       COUNT(v.id_venda) AS quantidade_vendas,
       COALESCE(SUM(v.valor_total), 0) AS faturamento_total
FROM funcionario f
LEFT JOIN venda v
       ON v.id_funcionario = f.id_funcionario
      AND v.status = 'REALIZADA'
GROUP BY f.id_funcionario, f.nome
ORDER BY faturamento_total DESC;

-- Quantidade de produtos cadastrados por status
SELECT status, COUNT(*) AS quantidade
FROM produto
GROUP BY status
ORDER BY status DESC;

-- Quantidade de fraudes por tipo
SELECT tipo, COUNT(*) AS quantidade
FROM fraude
GROUP BY tipo
ORDER BY quantidade DESC, tipo;
