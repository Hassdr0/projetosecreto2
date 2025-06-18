    document.getElementById('currentYear').textContent = new Date().getFullYear();

    document.addEventListener('DOMContentLoaded', async () => {
      const params = new URLSearchParams(window.location.search);
      const ids = params.get('ids')?.split(',').map(id => id.trim()) || [];

      const lista = document.getElementById('listaCarrinho');
      const loading = document.getElementById('carrinhoLoading');
      const erro = document.getElementById('carrinhoErro');

      const token = localStorage.getItem("jwtToken");

      if (ids.length === 0) {
        lista.innerHTML = '<p class="text-muted">Nenhum produto selecionado.</p>';
        return;
      }

      loading.style.display = 'block';
      try {
        for (const id of ids) {
          const response = await fetch(`/api/medicamentos/${id}`, {
            headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'application/json'
            }
          });

          if (!response.ok) throw new Error('Erro ao buscar produto com id: ' + id);

          const med = await response.json();

          const precoHTML = med.promocao
            ? `<p class="preco mb-1"><del>R$ ${med.precoVenda.toFixed(2)}</del> <span class="preco-promocional">R$ ${med.precoPromocional.toFixed(2)}</span></p>`
            : `<p class="preco mb-1">R$ ${med.precoVenda.toFixed(2)}</p>`;

          const card = document.createElement('div');
          card.className = 'col';
          card.innerHTML = `
            <div class="card card-produto h-100">
              <div class="card-body">
                <h5 class="card-title">${med.nome}</h5>
                <p class="card-text mb-1"><strong>Princípio Ativo:</strong> ${med.principioAtivo.nome}</p>
                <p class="card-text mb-1"><strong>Indústria:</strong> ${med.industria.nome}</p>
                <p class="card-text mb-1"><strong>Validade:</strong> ${med.validade}</p>
                ${precoHTML}
              </div>
            </div>
          `;
          lista.appendChild(card);
        }
      } catch (e) {
        console.error(e);
        erro.style.display = 'block';
      } finally {
        loading.style.display = 'none';
      }
    });