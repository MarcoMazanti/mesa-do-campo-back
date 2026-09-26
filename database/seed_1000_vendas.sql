-- Carga de demonstração para PostgreSQL, baseada no esquema de backup.sql.
-- Adiciona 1.000 vendas completas: produto + pedido + item_pedido + pagamento.
-- É incremental: não apaga nem altera registros já existentes.
-- Pré-requisito: existir ao menos um cliente e um vendedor no banco.

BEGIN;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM public.cliente) THEN
        RAISE EXCEPTION 'A carga exige pelo menos um cliente cadastrado.';
    END IF;

    IF NOT EXISTS (SELECT 1 FROM public.vendedor) THEN
        RAISE EXCEPTION 'A carga exige pelo menos um vendedor cadastrado.';
    END IF;
END $$;

CREATE TEMP TABLE carga_1000_vendas (
    indice integer PRIMARY KEY,
    produto_id integer NOT NULL,
    pedido_id integer NOT NULL,
    item_id integer NOT NULL,
    pagamento_id integer NOT NULL,
    id_vendedor integer NOT NULL,
    id_cliente integer NOT NULL,
    nome_produto varchar(255) NOT NULL,
    preco_unit numeric(10,2) NOT NULL,
    quantidade_comprada integer NOT NULL,
    estoque_final integer NOT NULL,
    categoria smallint NOT NULL,
    data_compra timestamp NOT NULL,
    status_pedido varchar(255) NOT NULL,
    status_item varchar(255) NOT NULL,
    metodo_pagamento varchar(255) NOT NULL,
    status_pagamento varchar(255) NOT NULL
) ON COMMIT DROP;

INSERT INTO carga_1000_vendas (
    indice, produto_id, pedido_id, item_id, pagamento_id,
    id_vendedor, id_cliente, nome_produto, preco_unit, quantidade_comprada,
    estoque_final, categoria, data_compra, status_pedido, status_item,
    metodo_pagamento, status_pagamento
)
WITH vendedores AS (
    SELECT array_agg(id_vendedor ORDER BY id_vendedor) AS ids FROM public.vendedor
), clientes AS (
    SELECT array_agg(id ORDER BY id) AS ids FROM public.cliente
)
SELECT
    serie.indice,
    nextval('public.produto_id_seq')::integer,
    nextval('public.pedido_id_seq')::integer,
    nextval('public.item_pedido_id_seq')::integer,
    nextval('public.pagamento_id_seq')::integer,
    vendedores.ids[1 + ((serie.indice - 1) % cardinality(vendedores.ids))],
    clientes.ids[1 + ((serie.indice - 1) % cardinality(clientes.ids))],
    format('Produto carga %s', lpad(serie.indice::text, 4, '0')),
    round((4 + ((serie.indice * 37) % 25000) / 100.0)::numeric, 2),
    1 + (serie.indice % 5),
    100 - (1 + (serie.indice % 5)),
    ((serie.indice - 1) % 8)::smallint,
    timestamp '2026-01-01 08:00:00' + (serie.indice * interval '37 minutes'),
    CASE serie.indice % 5
        WHEN 0 THEN 'AGUARDANDO_PAGAMENTO'
        WHEN 1 THEN 'PROCESSANDO'
        WHEN 2 THEN 'ENVIADO'
        WHEN 3 THEN 'ENTREGUE'
        ELSE 'CANCELADO'
    END,
    CASE serie.indice % 5
        WHEN 0 THEN 'PENDENTE'
        WHEN 1 THEN 'PREPARANDO'
        WHEN 2 THEN 'EM_TRANSITO'
        WHEN 3 THEN 'ENTREGUE'
        ELSE 'CANCELADO'
    END,
    CASE serie.indice % 3
        WHEN 0 THEN 'CREDITO'
        WHEN 1 THEN 'DEBITO'
        ELSE 'PIX'
    END,
    CASE serie.indice % 6
        WHEN 0 THEN 'PENDENTE'
        WHEN 1 THEN 'EM_ANALISE'
        WHEN 2 THEN 'APROVADO'
        WHEN 3 THEN 'RECUSADO'
        WHEN 4 THEN 'CANCELADO'
        ELSE 'ESTORNADO'
    END
FROM generate_series(1, 1000) AS serie(indice)
CROSS JOIN vendedores
CROSS JOIN clientes;

INSERT INTO public.produto (
    id, id_vendedor, nome, preco, quantidade, descricao, categoria, ativo
)
SELECT
    produto_id,
    id_vendedor,
    nome_produto,
    preco_unit,
    estoque_final,
    format('Produto de demonstração %s, criado pela carga de 1.000 vendas.', indice),
    categoria,
    true
FROM carga_1000_vendas;

INSERT INTO public.pedido (
    id, id_cliente, preco_total, data_compra, status
)
SELECT
    pedido_id,
    id_cliente,
    preco_unit * quantidade_comprada,
    data_compra,
    status_pedido
FROM carga_1000_vendas;

INSERT INTO public.item_pedido (
    id, id_pedido, id_produto, quantidade, preco_unit, status, data_compra
)
SELECT
    item_id,
    pedido_id,
    produto_id,
    quantidade_comprada,
    preco_unit,
    status_item,
    data_compra
FROM carga_1000_vendas;

INSERT INTO public.pagamento (
    id, id_pedido, metodo_pagamento, status, data_pagamento, valor_pago
)
SELECT
    pagamento_id,
    pedido_id,
    metodo_pagamento,
    status_pagamento,
    CASE WHEN status_pagamento = 'PENDENTE' THEN NULL ELSE data_compra + interval '2 minutes' END,
    preco_unit * quantidade_comprada
FROM carga_1000_vendas;

-- Conferência dos totais e distribuição de status antes do COMMIT.
SELECT 'produtos inseridos' AS indicador, count(*) AS total FROM carga_1000_vendas
UNION ALL SELECT 'pedidos inseridos', count(*) FROM carga_1000_vendas
UNION ALL SELECT 'itens inseridos', count(*) FROM carga_1000_vendas
UNION ALL SELECT 'pagamentos inseridos', count(*) FROM carga_1000_vendas;

SELECT 'pedido' AS entidade, status_pedido AS status, count(*) AS total
FROM carga_1000_vendas
GROUP BY status_pedido
UNION ALL
SELECT 'item', status_item, count(*)
FROM carga_1000_vendas
GROUP BY status_item
UNION ALL
SELECT 'pagamento', status_pagamento, count(*)
FROM carga_1000_vendas
GROUP BY status_pagamento
ORDER BY entidade, status;

COMMIT;
