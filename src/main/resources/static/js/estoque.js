document.addEventListener('DOMContentLoaded', async function () {
    console.log("DOM completamente carregado.");

    const token = localStorage.getItem("jwtToken");
    if (!token) {
        alert("Token não encontrado. Faça login novamente.");
        return;
    }

    // Modal principal de estoque
    const LoteModalElement = document.getElementById("EstoqueModal");
    if (!LoteModalElement) {
        console.error("Elemento #EstoqueModal não encontrado.");
        return;
    }
    const EstoqueModal = new bootstrap.Modal(LoteModalElement);

    // Botão para abrir modal de adicionar estoque
    const btnAbrirModal = document.getElementById("btnAbrirModalAdicionar");
    if (btnAbrirModal) {
        btnAbrirModal.addEventListener("click", () => {
            document.getElementById("formEstoque").reset();
            EstoqueModal.show();
        });
    }

    // Envio do formulário de adicionar estoque
    const formEstoque = document.getElementById("formEstoque");
    if (formEstoque) {
        formEstoque.addEventListener("submit", async function (event) {
            event.preventDefault();

            const data = {
                medicamentoId: parseInt(document.getElementById("modalMedicamentoId").value),
                farmaciaId: parseInt(document.getElementById("modalFarmaciaId").value),
                quantidade: parseInt(document.getElementById("quantidade").value),
                lote: document.getElementById("lote").value,
                dataDeValidadeDoLote: document.getElementById("dataValidadeLote").value,
                precoDeCustoDoLote: parseFloat(document.getElementById("precoCusto").value)
            };

            if (
                !data.medicamentoId || !data.farmaciaId || !data.quantidade || !data.lote ||
                !data.dataDeValidadeDoLote || !data.precoDeCustoDoLote
            ) {
                alert("Por favor, preencha todos os campos obrigatórios.");
                return;
            }

            try {
                const response = await fetch("/api/estoque/entrada", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${token}`
                    },
                    body: JSON.stringify(data)
                });

                if (!response.ok) throw new Error("Erro ao salvar o estoque");

                alert("Estoque salvo com sucesso!");
                EstoqueModal.hide();
                location.reload();
            } catch (err) {
                console.error("Erro ao salvar:", err);
                alert("Erro ao salvar estoque.");
            }
        });
    }

    // --- Módulo de edição / ajuste ---

    // Modal de ajuste
    const ajusteModalEl = document.getElementById("EstoqueModal");
    if (!ajusteModalEl) {
        console.error("Elemento #ajusteModal não encontrado.");
        return;
    }
    const ajusteModal = new bootstrap.Modal(ajusteModalEl);

    // Delegação de evento para abrir modal de ajuste ao clicar em botão editar
    const tabelaBody = document.getElementById("tabelaEstoqueBody");
    if (tabelaBody) {
        tabelaBody.addEventListener("click", async (event) => {
            const btnEditar = event.target.closest(".btn-editar");
            if (!btnEditar) return;

            const estoqueId = btnEditar.getAttribute("data-id");
            if (!estoqueId) {
                alert("ID do estoque não encontrado.");
                return;
            }

            try {
                const res = await fetch(`/api/estoque/ajuste`, {
                    headers: {
                        method: 'GET',
                        "Authorization": `Bearer ${token}`
                    }
                });

                if (!res.ok) throw new Error("Erro ao buscar estoque para edição");

                const estoque = await res.json();

                // Preenche os campos do modal de ajuste
                document.getElementById("ajusteEstoqueId").value = estoque.id;
                document.getElementById("ajusteMedicamentoId").value = estoque.medicamento.id;
                document.getElementById("ajusteFarmaciaId").value = estoque.farmacia.id;
                document.getElementById("ajusteLote").value = estoque.lote;
                document.getElementById("quantidadeAjuste").value = "";
                document.getElementById("motivoAjuste").value = "";

                ajusteModal.show();
            } catch (err) {
                alert(err.message);
                console.error(err);
            }
        });
    }

    // Envio do formulário de ajuste
    const formAjuste = document.getElementById("formAjuste");
    if (formAjuste) {
        formAjuste.addEventListener("submit", async (event) => {
            event.preventDefault();

            const quantidadeAjuste = parseInt(document.getElementById("quantidadeAjuste").value);
            const motivoAjuste = document.getElementById("motivoAjuste").value.trim();
            const medicamentoId = parseInt(document.getElementById("ajusteMedicamentoId").value);
            const farmaciaId = parseInt(document.getElementById("ajusteFarmaciaId").value);
            const lote = document.getElementById("ajusteLote").value;

            if (isNaN(quantidadeAjuste) || !motivoAjuste) {
                alert("Preencha a quantidade de ajuste e o motivo.");
                return;
            }

            const data = {
                quantidadeAjuste,
                motivoAjuste,
                medicamentoId,
                farmaciaId,
                lote
            };

            try {
                const response = await fetch("/api/estoque/ajuste", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${token}`
                    },
                    body: JSON.stringify(data)
                });

                if (!response.ok) throw new Error("Erro ao enviar ajuste");

                alert("Ajuste realizado com sucesso!");
                ajusteModal.hide();
                location.reload();
            } catch (err) {
                alert(err.message);
                console.error(err);
            }
        });
    }

    // --- Botão para buscar estoque ---

    const btnBuscarEstoque = document.getElementById("btnBuscarEstoque");
    if (btnBuscarEstoque) {
        btnBuscarEstoque.addEventListener("click", async function () {
            const medicamentoId = document.getElementById("medicamentoId").value;
            const farmaciaId = document.getElementById("farmaciaId").value;

            if (!medicamentoId || !farmaciaId) {
                alert("Preencha os campos de ID para buscar.");
                return;
            }

            try {
                const response = await fetch(`/api/estoque/medicamento/${medicamentoId}/filial/${farmaciaId}`, {
                    method: 'GET',
                    headers: {
                        "Authorization": `Bearer ${token}`
                    }
                });

                if (!response.ok) throw new Error("Erro ao buscar estoque.");

                const data = await response.json();
                const tbody = document.getElementById("tabelaEstoqueBody");
                tbody.innerHTML = "";

                data.forEach(estoque => {
                    const precoCustoFormatado =
                        typeof estoque.precoCusto === "number"
                            ? estoque.precoCusto.toFixed(2)
                            : 'N/A';

                    const tr = document.createElement("tr");
                    tr.innerHTML = `
                        <td>${estoque.id}</td>
                        <td>R$ ${precoCustoFormatado}</td>
                        <td>${estoque.quantidade ?? 'N/A'}</td>
                        <td>${estoque.dataUltimaAtualizacao ?? 'N/A'}</td>
                        <td>${estoque.farmacia?.nome ?? 'N/A'}</td>
                        <td>${estoque.medicamento?.nome ?? 'N/A'}</td>
                        <td>${estoque.lote ?? 'N/A'}</td>
                        <td>
                            <button class="btn btn-sm btn-warning btn-editar" data-id="${estoque.id}" title="Editar"><span data-feather="edit-2"></span></button>
                            <button class="btn btn-sm btn-danger btn-deletar" data-id="${estoque.id}" title="Deletar"><span data-feather="trash-2"></span></button>
                        </td>
                    `;
                    tbody.appendChild(tr);
                });

                if (typeof feather !== 'undefined') feather.replace();
            } catch (err) {
                console.error("Erro ao buscar estoque:", err);
                alert("Erro ao buscar estoque.");
            }
        });
    }
});
