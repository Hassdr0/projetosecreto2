document.addEventListener('DOMContentLoaded', function () {
    console.log('SCRIPT: medicamentos.js - LOG 1: DOM carregado.');
    const token = localStorage.getItem('jwtToken');
    console.log('SCRIPT: medicamentos.js - LOG 2: Token do localStorage:', token);

    const tabelaMedicamentosBody = document.getElementById('tabelaMedicamentosBody');
    const loadingIndicator = document.getElementById('loadingIndicator');
    const listaMedicamentosAlerta = document.getElementById('listaMedicamentosAlerta');
    const formMedicamento = document.getElementById('formMedicamento');
    const medicamentoModalElement = document.getElementById('medicamentoModal');
    let medicamentoModal;
    if (medicamentoModalElement) {
         medicamentoModal = new bootstrap.Modal(medicamentoModalElement);
    } else {
        console.error('SCRIPT: medicamentos.js - LOG ERRO: Elemento do modal #medicamentoModal não encontrado!');
    }
    const medicamentoModalLabel = document.getElementById('medicamentoModalLabel');
    const medicamentoIdInput = document.getElementById('medicamentoId');
    const nomeInput = document.getElementById('nome');
    const codigoDeBarrasInput = document.getElementById('codigoDeBarras');
    const descricaoInput = document.getElementById('descricao');
    const principioAtivoIdInput = document.getElementById('principioAtivoId');
    const industriaIdInput = document.getElementById('industriaId');
    const precoCompraInput = document.getElementById('precoCompra');
    const precoVendaInput = document.getElementById('precoVenda');
    const validadeInput = document.getElementById('validade');
    const promocaoInput = document.getElementById('promocao');
    const precoPromocionalInput = document.getElementById('precoPromocional');
    const divPrecoPromocional = document.getElementById('divPrecoPromocional');
    const formMedicamentoError = document.getElementById('formMedicamentoError');


    if (!token) {
        console.error('SCRIPT: medicamentos.js - LOG 3: Token JWT não encontrado!');
        if (listaMedicamentosAlerta) {
            listaMedicamentosAlerta.textContent = 'Sessão expirada ou token não encontrado. Por favor, faça login novamente.';
            listaMedicamentosAlerta.className = 'alert alert-danger d-block';
            listaMedicamentosAlerta.style.display = 'block';
        }
        // window.location.href = 'login.html';
        return;
    }

    if (promocaoInput && divPrecoPromocional) {
        promocaoInput.addEventListener('change', function() {
            divPrecoPromocional.style.display = this.checked ? 'block' : 'none';
            if (!this.checked && precoPromocionalInput) precoPromocionalInput.value = '';
        });
    }

    async function carregarMedicamentos() {
        console.log('SCRIPT: medicamentos.js - LOG A: Iniciando carregarMedicamentos()...');
        if (loadingIndicator) loadingIndicator.style.display = 'block';
        if (listaMedicamentosAlerta) listaMedicamentosAlerta.style.display = 'none';
        if (tabelaMedicamentosBody) tabelaMedicamentosBody.innerHTML = '<tr><td colspan="7" class="text-center"><div class="loader"></div>Carregando...</td></tr>';

        try {
            const response = await fetch('/api/medicamentos', { // URL relativa
                method: 'GET',
                headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' }
            });
            if (tabelaMedicamentosBody) tabelaMedicamentosBody.innerHTML = '';

            if (!response.ok) {
                let errorMsg = `Erro ao buscar medicamentos: ${response.status}`;
                try {
                    const errorData = await response.json();
                    errorMsg = errorData.message || `Erro ${response.status}`;
                } catch (e) { errorMsg = `Erro ao buscar medicamentos: ${response.statusText || response.status}`; }
                throw new Error(errorMsg);
            }

            const medicamentos = await response.json();
            if (medicamentos.length === 0) {
                tabelaMedicamentosBody.innerHTML = '<tr><td colspan="7" class="text-center">Nenhum medicamento encontrado.</td></tr>';
            } else {
                medicamentos.forEach(med => {
                    const tr = document.createElement('tr');
                    const precoVendaF = med.precoVenda !== null ? med.precoVenda.toFixed(2) : 'N/A';
                    const precoPromoF = med.precoPromocional !== null ? med.precoPromocional.toFixed(2) : precoVendaF;
                    tr.innerHTML = `
                        <td>${med.id}</td>
                        <td>${med.nome || '-'}</td>
                        <td>${med.codigoDeBarras || '-'}</td>
                        <td>${med.principioAtivo ? med.principioAtivo.nome : '-'}</td>
                        <td>R$ ${precoVendaF}</td>
                        <td>${med.promocao ? `Sim (R$ ${precoPromoF})` : 'Não'}</td>
                        <td>
                            <button class="btn btn-sm btn-warning btn-editar" data-id="${med.id}" title="Editar"><span data-feather="edit-2"></span></button>
                            <button class="btn btn-sm btn-danger btn-deletar" data-id="${med.id}" title="Deletar"><span data-feather="trash-2"></span></button>
                        </td>
                    `;
                    tabelaMedicamentosBody.appendChild(tr);
                });
                if (typeof feather !== 'undefined') feather.replace();
            }
        } catch (error) {
            console.error('SCRIPT: medicamentos.js - LOG G: Falha ao carregar medicamentos:', error);
            if (listaMedicamentosAlerta) {
                listaMedicamentosAlerta.textContent = `${error.message}`;
                listaMedicamentosAlerta.className = 'alert alert-danger d-block';
                listaMedicamentosAlerta.style.display = 'block';
            }
            if (tabelaMedicamentosBody) tabelaMedicamentosBody.innerHTML = `<tr><td colspan="7" class="text-center">Erro ao carregar medicamentos.</td></tr>`;
        } finally {
            if (loadingIndicator) loadingIndicator.style.display = 'none';
        }
    }

    if (formMedicamento) {
        formMedicamento.addEventListener('submit', async function (event) {
            event.preventDefault();
            if (formMedicamentoError) formMedicamentoError.style.display = 'none';

            const id = medicamentoIdInput.value;
            const dadosMedicamento = {
                nome: nomeInput.value,
                codigoDeBarras: codigoDeBarrasInput.value || null,
                descricao: descricaoInput.value || null,
                principioAtivoId: parseInt(principioAtivoIdInput.value),
                industriaId: industriaIdInput.value ? parseInt(industriaIdInput.value) : null,
                precoCompra: parseFloat(precoCompraInput.value),
                precoVenda: parseFloat(precoVendaInput.value),
                validade: validadeInput.value || null,
                promocao: promocaoInput.checked,
                precoPromocional: promocaoInput.checked && precoPromocionalInput.value ? parseFloat(precoPromocionalInput.value) : null
            };

            if (dadosMedicamento.promocao === false) dadosMedicamento.precoPromocional = null;

            const url = id ? `/api/medicamentos/${id}` : '/api/medicamentos'; // URL relativa
            const method = id ? 'PUT' : 'POST';

            try {
                const response = await fetch(url, {
                    method: method,
                    headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
                    body: JSON.stringify(dadosMedicamento)
                });

                if (!response.ok) {
                    const errorData = await response.json(); // Espera JSON do backend para erros
                    const errorMessage = errorData.message || (errorData.errors ? errorData.errors.map(e => e.defaultMessage).join(', ') : `Erro ${response.status}`);
                    throw new Error(errorMessage);
                }

                if (medicamentoModal) medicamentoModal.hide();
                formMedicamento.reset();
                if(medicamentoIdInput) medicamentoIdInput.value = '';
                if(divPrecoPromocional) divPrecoPromocional.style.display = 'none';
                if(promocaoInput) promocaoInput.checked = false;
                if(medicamentoModalLabel) medicamentoModalLabel.textContent = 'Adicionar Medicamento';

                alert(`Medicamento ${id ? 'atualizado' : 'adicionado'} com sucesso!`);
                carregarMedicamentos();
            } catch (error) {
                console.error('Erro ao salvar medicamento:', error);
                if (formMedicamentoError) {
                    formMedicamentoError.textContent = `Erro: ${error.message}`;
                    formMedicamentoError.style.display = 'block';
                } else {
                    alert(`Erro ao salvar medicamento: ${error.message}`);
                }
            }
        });
    }

    const btnAbrirModalAdicionar = document.getElementById('btnAbrirModalAdicionar');
    if (btnAbrirModalAdicionar) {
        btnAbrirModalAdicionar.addEventListener('click', () => {
            if (formMedicamento) formMedicamento.reset();
            if (medicamentoIdInput) medicamentoIdInput.value = '';
            if (divPrecoPromocional) divPrecoPromocional.style.display = 'none';
            if (promocaoInput) promocaoInput.checked = false;
            if (medicamentoModalLabel) medicamentoModalLabel.textContent = 'Adicionar Medicamento';
            if (formMedicamentoError) formMedicamentoError.style.display = 'none';
        });
    }

    if (token) {
        carregarMedicamentos();
    }

    // TODO: Implementar delegação de eventos para Editar e Deletar
    if (tabelaMedicamentosBody) {
        tabelaMedicamentosBody.addEventListener('click', async function(event) {
            const target = event.target.closest('button');
            if (!target) return;

            const medicamentoId = target.dataset.id;

            if (target.classList.contains('btn-editar')) {
                console.log('Editar medicamento ID:', medicamentoId);
                // Lógica para buscar dados do medicamento e popular o modal para edição
                // Ex: await carregarMedicamentoParaEdicao(medicamentoId);
                alert('Funcionalidade Editar ainda não implementada.');
            } else if (target.classList.contains('btn-deletar')) {
                console.log('Deletar medicamento ID:', medicamentoId);
                if (confirm(`Tem certeza que deseja deletar o medicamento com ID ${medicamentoId}?`)) {
                    // Lógica para chamar o endpoint DELETE /api/medicamentos/${medicamentoId}
                    // Ex: await deletarMedicamentoAPI(medicamentoId);
                    alert('Funcionalidade Deletar ainda não implementada.');
                }
            }
        });
    }
});