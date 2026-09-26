import fs from "node:fs/promises";
import { Workbook, SpreadsheetFile } from "@oai/artifact-tool";
import sharp from "sharp";

const outputDir = "outputs/documentacao_banco";
const backupSource = "backup.sql fornecido pelo usuário (PostgreSQL, 26/09/2026)";

const fields = [
  ["avaliacao", "id", "integer", "Não", "PK", "sequência", "", "", "Identificador da avaliação."],
  ["avaliacao", "id_vendedor", "integer", "Não", "FK", "vendedor.id_vendedor", "vendedor(id_vendedor)", "", "Vendedor avaliado."],
  ["avaliacao", "id_cliente", "integer", "Não", "FK", "cliente.id", "cliente(id)", "", "Cliente que realizou a avaliação."],
  ["avaliacao", "nota", "double precision", "Não", "", "", "", "", "Nota atribuída ao vendedor."],
  ["avaliacao", "descricao", "character varying(255)", "Sim", "", "", "", "", "Comentário da avaliação."],

  ["cartao_credito", "id", "integer", "Não", "PK", "sequência", "", "", "Identificador do cartão de crédito."],
  ["cartao_credito", "id_cliente", "integer", "Não", "FK", "cliente.id", "cliente(id)", "", "Titular do cartão."],
  ["cartao_credito", "nome", "character varying(255)", "Não", "", "", "", "", "Nome exibido para o cartão."],
  ["cartao_credito", "bandeira", "character varying(255)", "Não", "", "", "", "", "Bandeira do cartão."],
  ["cartao_credito", "ultimos_digitos", "integer", "Não", "", "", "", "", "Últimos dígitos do cartão."],
  ["cartao_credito", "token_gateway", "character varying(255)", "Não", "", "", "", "", "Token seguro do gateway de pagamento."],
  ["cartao_credito", "is_padrao", "boolean", "Sim", "", "false", "", "", "Indica se é o cartão padrão do cliente."],

  ["cartao_debito", "id", "integer", "Não", "PK", "sequência", "", "", "Identificador do cartão de débito."],
  ["cartao_debito", "id_cliente", "integer", "Não", "FK", "cliente.id", "cliente(id)", "", "Titular do cartão."],
  ["cartao_debito", "nome", "character varying(255)", "Não", "", "", "", "", "Nome exibido para o cartão."],
  ["cartao_debito", "bandeira", "character varying(255)", "Não", "", "", "", "", "Bandeira do cartão."],
  ["cartao_debito", "ultimos_digitos", "integer", "Não", "", "", "", "", "Últimos dígitos do cartão."],
  ["cartao_debito", "token_gateway", "character varying(255)", "Não", "", "", "", "", "Token seguro do gateway de pagamento."],
  ["cartao_debito", "is_padrao", "boolean", "Sim", "", "false", "", "", "Indica se é o cartão padrão do cliente."],

  ["chave_pix", "id", "integer", "Não", "PK", "sequência", "", "", "Identificador da chave PIX."],
  ["chave_pix", "id_cliente", "integer", "Não", "FK", "cliente.id", "cliente(id)", "", "Titular da chave PIX."],
  ["chave_pix", "nome", "character varying(255)", "Não", "", "", "", "", "Nome exibido para a chave."],
  ["chave_pix", "tipo_chave", "character varying(255)", "Não", "", "", "", "", "Tipo da chave PIX."],
  ["chave_pix", "chave", "character varying(255)", "Não", "", "", "", "", "Valor da chave PIX."],
  ["chave_pix", "is_padrao", "boolean", "Sim", "", "false", "", "", "Indica se é a chave padrão do cliente."],

  ["cliente", "id", "integer", "Não", "PK", "sequência", "", "", "Identificador do cliente."],
  ["cliente", "nome", "character varying(255)", "Não", "", "", "", "", "Nome do cliente."],
  ["cliente", "cpf_or_cnpj", "character varying(14)", "Não", "", "", "", "", "CPF ou CNPJ do cliente."],
  ["cliente", "email", "character varying(255)", "Não", "", "", "", "", "E-mail de acesso e contato."],
  ["cliente", "senha", "character varying(255)", "Não", "", "", "", "", "Senha armazenada pela aplicação."],
  ["cliente", "telefone", "character varying(14)", "Sim", "", "", "", "", "Telefone de contato."],
  ["cliente", "id_endereco_entrega", "integer", "Sim", "FK", "endereco.id", "endereco(id)", "", "Endereço definido como padrão para entrega."],

  ["endereco", "id", "integer", "Não", "PK", "sequência", "", "", "Identificador do endereço."],
  ["endereco", "id_usuario", "integer", "Não", "FK", "cliente.id", "cliente(id)", "", "Cliente titular do endereço."],
  ["endereco", "cep", "character varying(8)", "Não", "", "", "", "", "CEP sem formatação."],
  ["endereco", "country", "character varying(255)", "Sim", "", "", "", "", "País."],
  ["endereco", "state", "character varying(255)", "Sim", "", "", "", "", "Estado."],
  ["endereco", "city", "character varying(255)", "Sim", "", "", "", "", "Cidade."],
  ["endereco", "adress", "character varying(255)", "Sim", "", "", "", "", "Logradouro. O nome da coluna está grafado assim no banco."],
  ["endereco", "number", "integer", "Sim", "", "", "", "", "Número do endereço."],
  ["endereco", "complement", "character varying(255)", "Sim", "", "", "", "", "Complemento do endereço."],

  ["item_pedido", "id", "integer", "Não", "PK", "sequência", "", "", "Identificador do item do pedido."],
  ["item_pedido", "id_pedido", "integer", "Não", "FK", "pedido.id", "pedido(id)", "", "Pedido ao qual o item pertence."],
  ["item_pedido", "id_produto", "integer", "Não", "FK", "produto.id", "produto(id)", "", "Produto comprado."],
  ["item_pedido", "quantidade", "integer", "Não", "", "", "", "", "Quantidade comprada."],
  ["item_pedido", "preco_unit", "numeric(10,2)", "Não", "", "", "", "", "Preço unitário no momento da compra."],
  ["item_pedido", "status", "character varying(255)", "Não", "", "", "", "Aplicação: PENDENTE, PREPARANDO, EM_TRANSITO, ENTREGUE, CANCELADO", "Situação do item para acompanhamento do vendedor e comprador."],
  ["item_pedido", "data_compra", "timestamp(6) without time zone", "Sim", "", "", "", "", "Data e hora da compra do item."],

  ["pagamento", "id", "integer", "Não", "PK", "sequência", "", "", "Identificador do pagamento."],
  ["pagamento", "id_pedido", "integer", "Não", "FK", "pedido.id", "pedido(id)", "", "Pedido relacionado ao pagamento."],
  ["pagamento", "metodo_pagamento", "character varying(255)", "Não", "", "", "", "Aplicação: CREDITO, DEBITO, PIX", "Forma de pagamento utilizada."],
  ["pagamento", "status", "character varying(255)", "Não", "", "", "", "Aplicação: PENDENTE, EM_ANALISE, APROVADO, RECUSADO, CANCELADO, ESTORNADO", "Situação da transação de pagamento."],
  ["pagamento", "data_pagamento", "timestamp without time zone", "Sim", "", "", "", "", "Data e hora do processamento do pagamento."],
  ["pagamento", "valor_pago", "numeric(10,2)", "Não", "", "", "", "", "Valor efetivamente pago."],

  ["pedido", "id", "integer", "Não", "PK", "sequência", "", "", "Identificador do pedido."],
  ["pedido", "id_cliente", "integer", "Não", "FK", "cliente.id", "cliente(id)", "", "Cliente que fez o pedido."],
  ["pedido", "preco_total", "numeric(10,2)", "Não", "", "", "", "", "Valor total do pedido."],
  ["pedido", "data_compra", "timestamp(6) without time zone", "Não", "", "", "", "", "Data e hora em que o pedido foi criado."],
  ["pedido", "status", "character varying(255)", "Não", "", "", "", "CHECK: AGUARDANDO_PAGAMENTO, PROCESSANDO, ENVIADO, ENTREGUE, CANCELADO", "Situação geral do pedido."],

  ["produto", "id", "integer", "Não", "PK", "sequência", "", "", "Identificador do produto."],
  ["produto", "id_vendedor", "integer", "Não", "FK", "vendedor.id_vendedor", "vendedor(id_vendedor)", "", "Vendedor proprietário do produto."],
  ["produto", "nome", "character varying(255)", "Não", "", "", "", "", "Nome do produto."],
  ["produto", "preco", "numeric(10,2)", "Não", "", "", "", "", "Preço atual do produto."],
  ["produto", "quantidade", "integer", "Não", "", "", "", "", "Quantidade disponível em estoque."],
  ["produto", "descricao", "character varying(255)", "Sim", "", "", "", "", "Descrição comercial do produto."],
  ["produto", "categoria", "smallint", "Não", "", "", "", "CHECK: valor entre 0 e 7", "Código numérico da categoria."],
  ["produto", "ativo", "boolean", "Não", "", "true", "", "", "Indica se o produto está disponível para venda."],

  ["vendedor", "id_vendedor", "integer", "Não", "PK / FK", "", "cliente(id)", "", "Identificador do vendedor. Também referencia o cliente correspondente."],
  ["vendedor", "avaliacao", "double precision", "Sim", "", "", "", "Atualizada pelo gatilho trg_atualiza_avaliacao_vendedor", "Média das avaliações recebidas."],
  ["vendedor", "data_admissao", "date", "Sim", "", "", "", "", "Data de admissão do vendedor."],
  ["vendedor", "conta_recebimento", "character varying(255)", "Sim", "", "", "", "", "Conta de recebimento do vendedor."],
  ["vendedor", "tipo_recebimento", "public.tipo_pagamento", "Sim", "", "", "", "ENUM: CREDITO, DEBITO, PIX", "Tipo de recebimento cadastrado."],
  ["vendedor", "tipo_pagamento", "smallint", "Sim", "", "", "", "CHECK: valor entre 0 e 2", "Código numérico do tipo de pagamento."],
];

const relationships = [
  ["fk_avaliacao_cliente", "avaliacao", "id_cliente", "cliente", "id", "N:1", "Cada avaliação pertence a um cliente."],
  ["fk_avaliacao_vendedor", "avaliacao", "id_vendedor", "vendedor", "id_vendedor", "N:1", "Cada avaliação refere-se a um vendedor."],
  ["fk_cliente_endereco", "cliente", "id_endereco_entrega", "endereco", "id", "0..1:1", "O cliente pode apontar para um endereço de entrega padrão."],
  ["fk_credito_cliente", "cartao_credito", "id_cliente", "cliente", "id", "N:1", "Um cliente pode cadastrar cartões de crédito."],
  ["fk_debito_cliente", "cartao_debito", "id_cliente", "cliente", "id", "N:1", "Um cliente pode cadastrar cartões de débito."],
  ["fk_endereco_usuario", "endereco", "id_usuario", "cliente", "id", "N:1", "Um cliente pode possuir endereços."],
  ["fk_item_pedido", "item_pedido", "id_pedido", "pedido", "id", "N:1", "Um pedido pode ter vários itens."],
  ["fk_item_produto", "item_pedido", "id_produto", "produto", "id", "N:1", "Um produto pode aparecer em vários itens de pedido."],
  ["fk_pagamento_pedido", "pagamento", "id_pedido", "pedido", "id", "N:1", "Um pedido pode ter pagamentos relacionados."],
  ["fk_pedido_cliente", "pedido", "id_cliente", "cliente", "id", "N:1", "Um cliente pode realizar vários pedidos."],
  ["fk_pix_cliente", "chave_pix", "id_cliente", "cliente", "id", "N:1", "Um cliente pode cadastrar chaves PIX."],
  ["fk_produto_vendedor", "produto", "id_vendedor", "vendedor", "id_vendedor", "N:1", "Um vendedor pode cadastrar produtos."],
  ["fk_vendedor_cliente", "vendedor", "id_vendedor", "cliente", "id", "0..1:1", "Um vendedor é um cliente com dados complementares."],
];

const typesAndRules = [
  ["Tipo ENUM", "public.status_pagamento", "PENDENTE; EM_ANALISE; APROVADO; RECUSADO; CANCELADO; ESTORNADO", "Definido no esquema; usado pela aplicação para o status de pagamento."],
  ["Tipo ENUM", "public.status_pedido", "PENDENTE; PREPARANDO; EM_TRANSITO; ENTREGUE; CANCELADO", "Definido no esquema; usado pela aplicação para o status dos itens."],
  ["Tipo ENUM", "public.tipo_pagamento", "CREDITO; DEBITO; PIX", "Usado diretamente em vendedor.tipo_recebimento."],
  ["CHECK", "pedido.status", "AGUARDANDO_PAGAMENTO; PROCESSANDO; ENVIADO; ENTREGUE; CANCELADO", "Restrição física no banco."],
  ["CHECK", "produto.categoria", "0 a 7", "Restrição física no banco."],
  ["CHECK", "vendedor.tipo_pagamento", "0 a 2", "Restrição física no banco."],
  ["Gatilho", "trg_atualiza_avaliacao_vendedor", "AFTER INSERT OR DELETE OR UPDATE OF nota em avaliacao", "Recalcula vendedor.avaliacao com a média das notas."],
];

const esc = (text) => String(text).replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll('"', "&quot;");
const tableFields = Object.fromEntries([...new Set(fields.map((row) => row[0]))].map((name) => [name, fields.filter((row) => row[0] === name)]));
const positions = {
  cliente: [45, 45], vendedor: [510, 45], endereco: [975, 45], avaliacao: [1440, 45],
  cartao_credito: [45, 535], cartao_debito: [510, 535], chave_pix: [975, 535], produto: [1440, 535],
  pedido: [510, 990], item_pedido: [975, 990], pagamento: [1440, 990],
};

function box(name, x, y) {
  const rows = tableFields[name];
  const width = 390;
  const rowHeight = 28;
  const height = 48 + rows.length * rowHeight;
  const lines = rows.map((row, index) => {
    const isKey = row[4].includes("PK") || row[4].includes("FK");
    const color = row[4].includes("PK") ? "#8a4b08" : row[4].includes("FK") ? "#0f5e73" : "#24303c";
    const flag = row[4] ? ` [${row[4]}]` : "";
    return `<text x="${x + 16}" y="${y + 72 + index * rowHeight}" class="field" fill="${color}" font-weight="${isKey ? "700" : "400"}">${esc(row[1])}${esc(flag)} <tspan class="type">${esc(row[2])}</tspan></text>`;
  }).join("");
  return `<g><rect x="${x}" y="${y}" width="${width}" height="${height}" rx="8" class="table"/><rect x="${x}" y="${y}" width="${width}" height="44" rx="8" class="tableHead"/><text x="${x + 16}" y="${y + 29}" class="tableName">${esc(name)}</text>${lines}</g>`;
}

function connector(from, to, offset = 0) {
  const [x1, y1] = positions[from];
  const [x2, y2] = positions[to];
  const cx1 = x1 + 195;
  const cy1 = y1 + 180 + offset;
  const cx2 = x2 + 195;
  const cy2 = y2 + 180 + offset;
  const midY = (cy1 + cy2) / 2;
  return `<path d="M ${cx1} ${cy1} V ${midY} H ${cx2} V ${cy2}" class="relation" marker-end="url(#arrow)"/>`;
}

const diagram = `<?xml version="1.0" encoding="UTF-8"?>
<svg xmlns="http://www.w3.org/2000/svg" width="1900" height="1325" viewBox="0 0 1900 1325">
  <defs><marker id="arrow" markerWidth="10" markerHeight="10" refX="8" refY="3" orient="auto"><path d="M0,0 L0,6 L9,3 z" fill="#536878"/></marker></defs>
  <style>
    .title{font:700 28px Arial,sans-serif;fill:#142634}.subtitle{font:14px Arial,sans-serif;fill:#536878}.table{fill:#fff;stroke:#9bb0bd;stroke-width:1.5}.tableHead{fill:#dceff5}.tableName{font:700 18px Arial,sans-serif;fill:#143645}.field{font:14px Arial,sans-serif}.type{fill:#657580;font-size:12px}.relation{stroke:#536878;stroke-width:1.5;fill:none;opacity:.9}.legend{font:13px Arial,sans-serif;fill:#314452}
  </style>
  <rect width="1900" height="1325" fill="#f7fafb"/>
  <text x="45" y="31" class="title">Modelo relacional — Mesa do Campo</text>
  <text x="45" y="52" class="subtitle">Banco PostgreSQL | Campos destacados como PK e FK participam de relacionamentos.</text>
  ${connector("vendedor", "cliente", -100)}${connector("endereco", "cliente", -40)}${connector("cliente", "endereco", 40)}${connector("avaliacao", "cliente", -50)}${connector("avaliacao", "vendedor", 40)}${connector("cartao_credito", "cliente", -100)}${connector("cartao_debito", "cliente", -30)}${connector("chave_pix", "cliente", 40)}${connector("produto", "vendedor", 100)}${connector("pedido", "cliente", -50)}${connector("item_pedido", "pedido", -50)}${connector("item_pedido", "produto", 60)}${connector("pagamento", "pedido", 70)}
  ${Object.entries(positions).map(([name, [x, y]]) => box(name, x, y)).join("")}
  <rect x="45" y="1280" width="1810" height="26" rx="5" fill="#eaf2f5"/>
  <text x="60" y="1298" class="legend"><tspan font-weight="700" fill="#8a4b08">[PK]</tspan> chave primária    <tspan font-weight="700" fill="#0f5e73">[FK]</tspan> chave estrangeira    Setas apontam da tabela que contém a FK para a tabela referenciada.</text>
</svg>`;

await fs.mkdir(outputDir, { recursive: true });
await fs.writeFile(`${outputDir}/modelo_relacional_mesa_do_campo.svg`, diagram, "utf8");
await sharp(Buffer.from(diagram)).png().toFile(`${outputDir}/modelo_relacional_mesa_do_campo.png`);

const workbook = Workbook.create();
const dictionary = workbook.worksheets.add("Dicionário de Dados");
const relationsSheet = workbook.worksheets.add("Relacionamentos");
const rulesSheet = workbook.worksheets.add("Tipos e Regras");

for (const sheet of [dictionary, relationsSheet, rulesSheet]) {
  sheet.showGridLines = false;
  sheet.tabColor = "#0F5E73";
}

dictionary.getRange("A1:I1").merge();
dictionary.getRange("A1").values = [["Dicionário de Dados — Mesa do Campo"]];
dictionary.getRange("A2:I2").merge();
dictionary.getRange("A2").values = [[`Fonte: ${backupSource}`]];
dictionary.getRange("A4:I4").values = [["Tabela", "Campo", "Tipo SQL", "Aceita nulo?", "Chave", "Padrão", "Referência", "Restrições / valores", "Descrição"]];
dictionary.getRange(`A5:I${fields.length + 4}`).values = fields;
dictionary.tables.add(`A4:I${fields.length + 4}`, true, "DicionarioDados");
dictionary.getRange("A1:I1").format = { font: { name: "Arial", size: 16, bold: true, color: "#143645" } };
dictionary.getRange("A2:I2").format = { font: { name: "Arial", size: 10, italic: true, color: "#536878" } };
dictionary.getRange("A4:I4").format = { fill: "#143645", font: { name: "Arial", size: 10, bold: true, color: "#FFFFFF" }, horizontalAlignment: "center", verticalAlignment: "center", wrapText: true };
dictionary.getRange(`A5:I${fields.length + 4}`).format = { font: { name: "Arial", size: 10, color: "#24303C" }, verticalAlignment: "top", wrapText: true };
dictionary.getRange(`D5:D${fields.length + 4}`).format.horizontalAlignment = "center";
dictionary.getRange(`E5:E${fields.length + 4}`).format.horizontalAlignment = "center";
dictionary.getRange("A:A").format.columnWidth = 18;
dictionary.getRange("B:B").format.columnWidth = 22;
dictionary.getRange("C:C").format.columnWidth = 32;
dictionary.getRange("D:D").format.columnWidth = 13;
dictionary.getRange("E:E").format.columnWidth = 13;
dictionary.getRange("F:F").format.columnWidth = 18;
dictionary.getRange("G:G").format.columnWidth = 24;
dictionary.getRange("H:H").format.columnWidth = 50;
dictionary.getRange("I:I").format.columnWidth = 58;
dictionary.getRange("4:4").format.rowHeight = 28;
dictionary.freezePanes.freezeRows(4);

relationsSheet.getRange("A1:G1").merge();
relationsSheet.getRange("A1").values = [["Relacionamentos entre Tabelas"]];
relationsSheet.getRange("A2:G2").merge();
relationsSheet.getRange("A2").values = [["Chaves estrangeiras extraídas do backup fornecido."]];
relationsSheet.getRange("A4:G4").values = [["Nome da FK", "Tabela origem", "Campo origem", "Tabela destino", "Campo destino", "Cardinalidade", "Descrição"]];
relationsSheet.getRange(`A5:G${relationships.length + 4}`).values = relationships;
relationsSheet.tables.add(`A4:G${relationships.length + 4}`, true, "RelacionamentosTabela");
relationsSheet.getRange("A1:G1").format = { font: { name: "Arial", size: 16, bold: true, color: "#143645" } };
relationsSheet.getRange("A2:G2").format = { font: { name: "Arial", size: 10, italic: true, color: "#536878" } };
relationsSheet.getRange("A4:G4").format = { fill: "#143645", font: { name: "Arial", size: 10, bold: true, color: "#FFFFFF" }, horizontalAlignment: "center", verticalAlignment: "center", wrapText: true };
relationsSheet.getRange(`A5:G${relationships.length + 4}`).format = { font: { name: "Arial", size: 10, color: "#24303C" }, verticalAlignment: "top", wrapText: true };
relationsSheet.getRange("A:A").format.columnWidth = 27;
relationsSheet.getRange("B:E").format.columnWidth = 20;
relationsSheet.getRange("F:F").format.columnWidth = 16;
relationsSheet.getRange("G:G").format.columnWidth = 55;
relationsSheet.getRange("4:4").format.rowHeight = 28;
relationsSheet.freezePanes.freezeRows(4);

rulesSheet.getRange("A1:D1").merge();
rulesSheet.getRange("A1").values = [["Tipos, Restrições e Gatilhos"]];
rulesSheet.getRange("A2:D2").merge();
rulesSheet.getRange("A2").values = [["Regras declaradas no esquema e regras usadas pela aplicação."]];
rulesSheet.getRange("A4:D4").values = [["Categoria", "Elemento", "Valores / definição", "Observação"]];
rulesSheet.getRange(`A5:D${typesAndRules.length + 4}`).values = typesAndRules;
rulesSheet.tables.add(`A4:D${typesAndRules.length + 4}`, true, "TiposRegras");
rulesSheet.getRange("A1:D1").format = { font: { name: "Arial", size: 16, bold: true, color: "#143645" } };
rulesSheet.getRange("A2:D2").format = { font: { name: "Arial", size: 10, italic: true, color: "#536878" } };
rulesSheet.getRange("A4:D4").format = { fill: "#143645", font: { name: "Arial", size: 10, bold: true, color: "#FFFFFF" }, horizontalAlignment: "center", verticalAlignment: "center", wrapText: true };
rulesSheet.getRange(`A5:D${typesAndRules.length + 4}`).format = { font: { name: "Arial", size: 10, color: "#24303C" }, verticalAlignment: "top", wrapText: true };
rulesSheet.getRange("A:A").format.columnWidth = 18;
rulesSheet.getRange("B:B").format.columnWidth = 36;
rulesSheet.getRange("C:C").format.columnWidth = 68;
rulesSheet.getRange("D:D").format.columnWidth = 58;
rulesSheet.getRange("4:4").format.rowHeight = 28;
rulesSheet.freezePanes.freezeRows(4);

const dictionaryCheck = await workbook.inspect({ kind: "table", range: `Dicionário de Dados!A1:I${fields.length + 4}`, include: "values,formulas", tableMaxRows: 8, tableMaxCols: 9 });
console.log(dictionaryCheck.ndjson);
const errorScan = await workbook.inspect({ kind: "match", searchTerm: "#REF!|#DIV/0!|#VALUE!|#NAME\\?|#N/A|#NUM!|#NULL!|#SPILL!|#CALC!", options: { useRegex: true, maxResults: 300 }, summary: "verificação de erros de fórmula" });
console.log(errorScan.ndjson);

for (const [sheetName, range] of [["Dicionário de Dados", "A1:I20"], ["Relacionamentos", "A1:G18"], ["Tipos e Regras", "A1:D12"]]) {
  const preview = await workbook.render({ sheetName, range, scale: 1.5, format: "png" });
  await fs.writeFile(`${outputDir}/${sheetName.replaceAll(" ", "_").toLowerCase()}_preview.png`, new Uint8Array(await preview.arrayBuffer()));
}

const output = await SpreadsheetFile.exportXlsx(workbook);
await output.save(`${outputDir}/dicionario_de_dados_mesa_do_campo.xlsx`);
console.log(JSON.stringify({ fields: fields.length, relationships: relationships.length, outputDir }));
