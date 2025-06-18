document.addEventListener('DOMContentLoaded', function () {
    const token = localStorage.getItem("jwtToken");

    const grid = document.getElementById('produtosGrid');
    const loading = document.getElementById('loadingProdutos');
    const alerta = document.getElementById('listaProdutosAlerta');

    const promocoesGrid = document.getElementById('promocoesGrid');
    const loadingPromocoes = document.getElementById('loadingPromocoes');
    const alertaPromocoes = document.getElementById('listaPromocoesAlerta');

    async function carregarMedicamentos() {
        loading.style.display = 'block';
        alerta.style.display = 'none';

        try {
            const response = await fetch('/api/medicamentos', {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) throw new Error('Erro ao buscar medicamentos.');

            const medicamentos = await response.json();
            grid.innerHTML = '';

            if (medicamentos.length === 0) {
                grid.innerHTML = '<p class="text-muted">Nenhum medicamento encontrado.</p>';
                return;
            }

            medicamentos.forEach(med => grid.appendChild(criarCardProduto(med)));

        } catch (error) {
            console.error('Erro:', error);
            alerta.style.display = 'block';
            alerta.textContent = 'Erro ao carregar medicamentos.';
        } finally {
            loading.style.display = 'none';
        }
    }

    async function carregarPromocoes() {
        loadingPromocoes.style.display = 'block';
        alertaPromocoes.style.display = 'none';

        try {
            const response = await fetch('/api/medicamentos', {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) throw new Error('Erro ao buscar promoções.');

            const medicamentos = await response.json();
            const emPromocao = medicamentos.filter(m => m.promocao);
            promocoesGrid.innerHTML = '';

            if (emPromocao.length === 0) {
                promocoesGrid.innerHTML = '<p class="text-muted">Nenhuma promoção disponível.</p>';
                return;
            }

            emPromocao.forEach(med => promocoesGrid.appendChild(criarCardProduto(med)));

        } catch (error) {
            console.error('Erro:', error);
            alertaPromocoes.style.display = 'block';
            alertaPromocoes.textContent = 'Erro ao carregar promoções.';
        } finally {
            loadingPromocoes.style.display = 'none';
        }
    }

    function criarCardProduto(med) {
        const card = document.createElement('div');
        card.className = 'col position-relative';

        const precoHTML = med.promocao ? `
            <p class="preco mb-1">
                <del>R$ ${med.precoVenda.toFixed(2)}</del>
                <span class="preco-promocional"> R$ ${med.precoPromocional.toFixed(2)}</span>
            </p>` :
            `<p class="preco mb-1">R$ ${med.precoVenda.toFixed(2)}</p>`;

        card.innerHTML = `
            <div class="card card-produto h-100 d-flex flex-column justify-content-between">
                <div class="form-check position-absolute top-0 end-0 m-2">
                    <input class="form-check-input" type="checkbox" name="selecionados" value="${med.id}">
                </div>

                <div class="card-body">
                    <h5 class="card-title">${med.nome}</h5>
                    <p class="card-text mb-1"><strong>Princípio Ativo:</strong> ${med.principioAtivo.nome}</p>
                    <p class="card-text mb-1"><strong>Indústria:</strong> ${med.industria.nome}</p>
                    <p class="card-text mb-1"><strong>Validade:</strong> ${med.validade}</p>
                    ${precoHTML}
                </div>

                <div class="card-footer bg-transparent border-0 text-end">
                    <a href="detalhes.html?id=${med.id}" class="btn btn-sm btn-outline-primary">Visualizar</a>
                </div>
            </div>
        `;

        return card;
    }

    carregarMedicamentos();
    carregarPromocoes();
    const botaoCarrinhoPromocoes = document.getElementById('btnAdicionarCarrinhoPromocoes');

    if (botaoCarrinhoPromocoes) {
        botaoCarrinhoPromocoes.addEventListener('click', (e) => {
            e.preventDefault(); // <--- Impede o recarregamento da página

            const selecionados = document.querySelectorAll('input[name="selecionados"]:checked');
            const ids = Array.from(selecionados).map(cb => cb.value);

            if (ids.length === 0) {
                alert("Selecione pelo menos um produto para adicionar ao carrinho.");
                return;
            }

            // Redireciona para carrinho.html com os ids como parâmetros
            window.location.href = `carrinho.html?ids=${ids.join(',')}`;
        });

    }
    const botaoCarrinhoProduto = document.getElementById('btnAdicionarCarrinhoProduto');

        if (botaoCarrinhoProduto) {
            botaoCarrinhoProduto.addEventListener('click', (e) => {
                e.preventDefault(); // <--- Impede o recarregamento da página

                const selecionados = document.querySelectorAll('input[name="selecionados"]:checked');
                const ids = Array.from(selecionados).map(cb => cb.value);

                if (ids.length === 0) {
                    alert("Selecione pelo menos um produto para adicionar ao carrinho.");
                    return;
                }

                // Redireciona para carrinho.html com os ids como parâmetros
                window.location.href = `carrinho.html?ids=${ids.join(',')}`;
            });

        }

});
