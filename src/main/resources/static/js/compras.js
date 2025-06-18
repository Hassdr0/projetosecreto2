document.addEventListener('DOMContentLoaded', () => {
  const tabelaBody = document.getElementById('tabelaComprasBody');
  const loadingIndicator = document.getElementById('loadingIndicator');
  const alerta = document.getElementById('listaComprasAlerta');
  const formCompra = document.getElementById('formCompra');
  const btnSalvarCompra = document.getElementById('btnSalvarCompra'); // Mantido, embora não seja usado diretamente no addEventListener do form
  const CompraModal = new bootstrap.Modal(document.getElementById('CompraModal'));
  const token = localStorage.getItem('jwtToken');

  // Campos do formulário (verifique se correspondem à sua entidade Compra no backend)
  const inputCompraId = document.getElementById('CompraId'); // Usado para exibição/controle, não necessariamente enviado para o backend na criação
  const inputNome = document.getElementById('nome'); // Ex: Nome do fornecedor ou identificação da compra
  const inputUf = document.getElementById('uf'); // Ex: UF do fornecedor
  const inputCidade = document.getElementById('cidade'); // Ex: Cidade do fornecedor
  const inputCnpj = document.getElementById('cnpj'); // Ex: CNPJ do fornecedor
  const inputTelefone = document.getElementById('telefone'); // Ex: Telefone do fornecedor
  const inputEndereco = document.getElementById('endereco'); // Ex: Endereço do fornecedor

  // Estado para controle de edição
  let editandoCompraId = null;

  // Função para exibir alerta
  function mostrarAlerta(mensagem, tipo = 'success') {
    alerta.textContent = mensagem;
    alerta.className = 'alert alert-' + tipo;
    alerta.style.display = 'block';
    setTimeout(() => { alerta.style.display = 'none'; }, 4000);
  }

  // Mostrar loader
  function mostrarLoader(mostrar) {
    loadingIndicator.style.display = mostrar ? 'block' : 'none';
  }

  // --- Funções CRUD ---

  // Buscar todas as Compras
  async function carregarCompras() {
    mostrarLoader(true);
    try {
      // AJUSTE: Endpoint para buscar TODAS as compras (ex: /api/compras)
      const res = await fetch("/api/compras", {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      if (!res.ok) {
        let errorMsg = `Erro ao buscar Compras: ${res.status}`;
        try {
          const errorData = await res.json();
          errorMsg = errorData.message || `Erro ${res.status}`;
        } catch (e) {
          errorMsg = `Erro ao buscar Compras: ${res.statusText || res.status}`;
        }
        throw new Error(errorMsg);
      }

      const compras = await res.json(); // Mudança para 'compras' (minúsculas) para consistência
      preencherTabela(compras);
    } catch (error) {
      console.error('Erro ao carregar Compras: ' + error.message);
      mostrarAlerta('Erro ao carregar Compras: ' + error.message, 'danger');
    } finally {
      mostrarLoader(false);
    }
  }

  // Preencher a tabela
  function preencherTabela(compras) {
    tabelaBody.innerHTML = '';
    if (compras.length === 0) {
      tabelaBody.innerHTML = '<tr><td colspan="8" class="text-center">Nenhuma compra encontrada.</td></tr>';
      return;
    }
    compras.forEach(compra => { // Mudança para 'compra' (minúsculas)
      const tr = document.createElement('tr');
      tr.dataset.id = compra.id;

      // Os campos aqui devem refletir os atributos da sua entidade Compra no backend
      tr.innerHTML = `
        <td>${compra.id}</td>
        <td>${compra.nome || ''}</td>
        <td>${compra.uf || compra.estado || ''}</td>
        <td>${compra.cidade || ''}</td>
        <td>${compra.cnpj || ''}</td>
        <td>${compra.telefone || ''}</td>
        <td>${compra.endereco || ''}</td>
        <td>
          <button class="btn btn-sm btn-warning btn-editar" data-id="${compra.id}"><span data-feather="edit-2"></span></button>
          <button class="btn btn-sm btn-danger btn-deletar" data-id="${compra.id}"><span data-feather="trash-2"></span></button>
        </td>
      `;
      tabelaBody.appendChild(tr);
    });
    if (typeof feather !== 'undefined') feather.replace();
  }

  // Abrir modal para adicionar nova Compra
  document.getElementById('btnAbrirModalAdicionar').addEventListener('click', () => {
    editandoCompraId = null;
    formCompra.reset(); // Limpa todos os campos do formulário
    inputCompraId.value = ''; // Garante que o campo de ID (se houver) esteja vazio
    document.getElementById('CompraModalLabel').textContent = 'Adicionar Nova Compra';
    CompraModal.show(); // Abre o modal
  });

  // Abrir modal para edição (buscar Compra específica pelo ID)
  async function abrirModalEdicao(id) {
    mostrarLoader(true);
    try {
      // AJUSTE: Endpoint para buscar UMA compra específica pelo ID
      const res = await fetch(`/api/cotacoes/medicamento/{medicamentoId}`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      if (!res.ok) {
        let errorMsg = `Erro ao carregar Compra: ${res.status}`;
        try {
          const errorData = await res.json();
          errorMsg = errorData.message || `Erro ${res.status}`;
        } catch (e) {
          errorMsg = `Erro ao carregar Compra: ${res.statusText || res.status}`;
        }
        throw new Error(errorMsg);
      }

      const compra = await res.json();
      editandoCompraId = compra.id;
      document.getElementById('CompraModalLabel').textContent = 'Editar Compra';

      // Preencher formulário com os dados da compra
      inputCompraId.value = compra.id || ''; // Se seu backend retornar o ID para edição
      inputNome.value = compra.nome || '';
      inputUf.value = compra.estado || compra.uf || ''; // Adicionado `compra.estado` caso seja o nome do campo no backend
      inputCidade.value = compra.cidade || '';
      inputCnpj.value = compra.cnpj || '';
      inputTelefone.value = compra.telefone || '';
      inputEndereco.value = compra.endereco || '';

      CompraModal.show();
    } catch (error) {
      console.error('Erro ao carregar Compra para edição: ' + error.message);
      mostrarAlerta('Falha ao carregar os dados da Compra para edição. Veja o console para mais detalhes.', 'danger');
    } finally {
      mostrarLoader(false);
    }
  }

  // Enviar formulário (inserir ou editar)
  formCompra.addEventListener('submit', async e => {
    e.preventDefault();
    mostrarLoader(true);

    // Certifique-se de que estes campos correspondem EXATAMENTE aos campos que seu backend Spring Boot espera para a entidade Compra
    const compraData = {
      nome: inputNome.value.trim(),
      estado: inputUf.value.trim(), // Use 'estado' ou 'uf' conforme seu backend espera
      cidade: inputCidade.value.trim(),
      cnpj: inputCnpj.value.trim(),
      telefone: inputTelefone.value.trim(),
      endereco: inputEndereco.value.trim()
      // Adicione outros campos da compra aqui, se existirem (ex: dataCompra, valorTotal, itens, etc.)
    };

    try {
      let res;
      if (editandoCompraId) {
        // Editar Compra existente ignorar pois não tem no back
        res = await fetch(`/api/compras/${editandoCompraId}`, {
          method: 'PUT',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(compraData)
        });
      } else {
        // Criar nova Compra
        res = await fetch('/api/cotacoes', { // AJUSTE: Endpoint de POST para criar uma nova compra
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(compraData)
        });
      }

      if (!res.ok) {
        // Tenta ler a mensagem de erro do backend
        let errorBody = await res.text(); // Lê como texto primeiro
        try {
            errorBody = JSON.parse(errorBody); // Tenta parsear como JSON
        } catch (e) {
            // Não é um JSON, usa o texto puro
        }
        const errorMessage = errorBody.message || errorBody || `Erro ${res.status}`;
        throw new Error(errorMessage);
      }

      mostrarAlerta(editandoCompraId ? 'Compra atualizada com sucesso!' : 'Compra criada com sucesso!');
      CompraModal.hide();
      carregarCompras(); // Recarrega a lista para mostrar as alterações
    } catch (error) {
      console.error('Erro ao salvar Compra: ' + error.message);
      mostrarAlerta('Erro ao salvar Compra: ' + error.message, 'danger');
    } finally {
      mostrarLoader(false);
    }
  });

  // Ações dos botões da tabela (editar e deletar)
  if (tabelaBody) {
    tabelaBody.addEventListener('click', async function (event) {
      const target = event.target.closest('button');
      if (!target) return;

      const compraId = target.dataset.id; // Mudança para 'compraId'

      if (target.classList.contains('btn-editar')) {
        console.log('Editar Compra ID:', compraId);
        await abrirModalEdicao(compraId);
      } else if (target.classList.contains('btn-deletar')) {
        console.log('Deletar Compra ID:', compraId);
        if (confirm(`Tem certeza que deseja deletar a Compra com ID ${compraId}?`)) {
          await deletarCompraAPI(compraId);
        }
      }
    });
  }

  // Função para deletar Compra
  async function deletarCompraAPI(id) {
    mostrarLoader(true);
    try {
      // AJUSTE: Endpoint de DELETE para remover uma compra específica ignorar pois não existe no back
      const res = await fetch(`/api/compras/${id}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (!res.ok) {
        let errorMsg = `Erro ao deletar Compra: ${res.status}`;
        try {
          const errorData = await res.json();
          errorMsg = errorData.message || `Erro ${res.status}`;
        } catch (e) {
          errorMsg = `Erro ao deletar Compra: ${res.statusText || res.status}`;
        }
        throw new Error(errorMsg);
      }

      mostrarAlerta('Compra deletada com sucesso!');
      carregarCompras(); // Atualiza a tabela
    } catch (error) {
      console.error('Erro ao deletar Compra: ' + error.message);
      mostrarAlerta('Erro ao deletar Compra: ' + error.message, 'danger');
    } finally {
      mostrarLoader(false);
    }
  }

  // --- Inicialização ---

  // Inicializa a listagem de compras ao carregar a página
  carregarCompras();

  // Logout
  document.getElementById('logoutButtonCompras').addEventListener('click', () => {
    localStorage.removeItem('jwtToken');
    window.location.href = 'login.html'; // Redireciona para a página de login
  });

  // Feather icons (se estiver usando)
  if (typeof feather !== 'undefined') {
    feather.replace();
  }
});