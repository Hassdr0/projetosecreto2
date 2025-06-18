document.addEventListener('DOMContentLoaded', () => {
  const tabelaBody = document.getElementById('tabelaFiliaisBody');
  const loadingIndicator = document.getElementById('loadingIndicator');
  const alerta = document.getElementById('listaFiliaisAlerta');
  const formFilial = document.getElementById('formFilial');
  const btnSalvarFilial = document.getElementById('btnSalvarFilial');
  const filialModal = new bootstrap.Modal(document.getElementById('FilialModal'));
  const token = localStorage.getItem('jwtToken');

  // Campos do formulário
  const inputFilialId = document.getElementById('filialId');
  const inputNome = document.getElementById('nome');
  const inputUf = document.getElementById('uf');
  const inputCidade = document.getElementById('cidade');
  const inputCnpj = document.getElementById('cnpj');
  const inputTelefone = document.getElementById('telefone');
  const inputEndereco = document.getElementById('endereco');

  // Estado para controle de edição
  let editandoFilialId = null;

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

  // Buscar todas as filiais
  async function carregarFiliais() {
    mostrarLoader(true);
    try {
      const res = await fetch('/api/filiais', {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      if (!res.ok) {
        let errorMsg = `Erro ao buscar filiais: ${res.status}`;
        try {
          const errorData = await res.json();
          errorMsg = errorData.message || `Erro ${res.status}`;
        } catch (e) {
          errorMsg = `Erro ao buscar filiais: ${res.statusText || res.status}`;
        }
        throw new Error(errorMsg);
      }

      const filiais = await res.json();
      preencherTabela(filiais);
    } catch (error) {
      console.error('Erro ao carregar filiais: ' + error.message);
    } finally {
      mostrarLoader(false);
    }
  }

  // Preencher a tabela
  function preencherTabela(filiais) {
    tabelaBody.innerHTML = '';
    filiais.forEach(filial => {
      const tr = document.createElement('tr');
      tr.dataset.id = filial.id;

      tr.innerHTML = `
        <td>${filial.id}</td>
        <td>${filial.nome}</td>
        <td>${filial.uf || filial.estado}</td>
        <td>${filial.cidade}</td>
        <td>${filial.cnpj}</td>
        <td>${filial.telefone || ''}</td>
        <td>
          <button class="btn btn-sm btn-warning btn-editar" data-id="${filial.id}"><span data-feather="edit-2"></span></button>
          <button class="btn btn-sm btn-danger btn-deletar" data-id="${filial.id}"><span data-feather="trash-2"></span></button>
        </td>
      `;

      tabelaBody.appendChild(tr);
    });
    if (typeof feather !== 'undefined') feather.replace();
  }

  // Abrir modal para adicionar nova filial
  document.getElementById('btnAbrirModalAdicionar').addEventListener('click', () => {
    editandoFilialId = null;
    formFilial.reset();
    inputFilialId.value = '';
    document.getElementById('FilialModalLabel').textContent = 'Adicionar Filial';
  });

  // Abrir modal para edição
  async function abrirModalEdicao(id) {
    mostrarLoader(true);
    try {
      const res = await fetch(`/api/filiais/${id}`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      if (!res.ok) throw new Error('Erro ao carregar filial');

      const filial = await res.json();
      editandoFilialId = filial.id;
      document.getElementById('FilialModalLabel').textContent = 'Editar Filial';

      // Preencher formulário
      inputFilialId.value = filial.id || '';
      inputNome.value = filial.nome || '';
      inputUf.value = filial.estado || filial.uf || '';
      inputCidade.value = filial.cidade || '';
      inputCnpj.value = filial.cnpj || '';
      inputTelefone.value = filial.telefone || '';
      inputEndereco.value = filial.endereco || '';

      filialModal.show();
    } catch (error) {
      console.error('Erro ao carregar filial: ' + error.message);
      alert('Falha ao carregar os dados da filial. Veja o console para mais detalhes.');
    } finally {
      mostrarLoader(false);
    }
  }

  // Enviar formulário (inserir ou editar)
  formFilial.addEventListener('submit', async e => {
    e.preventDefault();
    mostrarLoader(true);

    const filialData = {
      nome: inputNome.value.trim(),
      estado: inputUf.value.trim(),
      cidade: inputCidade.value.trim(),
      cnpj: inputCnpj.value.trim(),
      telefone: inputTelefone.value.trim(),
      endereco: inputEndereco.value.trim()
    };

    try {
      let res;
      if (editandoFilialId) {
        // Editar filial existente
        res = await fetch(`/api/filiais/${editandoFilialId}`, {
          method: 'PUT',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(filialData)
        });
      } else {
        // Criar nova filial
        res = await fetch('/api/filiais', {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(filialData)
        });
      }

      if (!res.ok) {
        const erro = await res.json();
        throw new Error(erro.message || 'Erro ao salvar filial');
      }

      mostrarAlerta(editandoFilialId ? 'Filial atualizada com sucesso!' : 'Filial criada com sucesso!');
      filialModal.hide();
      carregarFiliais();
    } catch (error) {
      console.error('Erro: ' + error.message);
      mostrarAlerta('Erro ao salvar filial: ' + error.message, 'danger');
    } finally {
      mostrarLoader(false);
    }
  });

  // Ações dos botões da tabela (editar e deletar)
  if (tabelaBody) {
    tabelaBody.addEventListener('click', async function (event) {
      const target = event.target.closest('button');
      if (!target) return;

      const filialId = target.dataset.id;

      if (target.classList.contains('btn-editar')) {
        console.log('Editar filial ID:', filialId);
        await abrirModalEdicao(filialId);
      } else if (target.classList.contains('btn-deletar')) {
        console.log('Deletar filial ID:', filialId);
        if (confirm(`Tem certeza que deseja deletar a filial com ID ${filialId}?`)) {
          await deletarFilialAPI(filialId);
        }
      }
    });
  }

  // Função para deletar filial
  async function deletarFilialAPI(id) {
    mostrarLoader(true);
    try {
      const res = await fetch(`/api/filiais/${id}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (!res.ok) throw new Error('Erro ao deletar filial');

      mostrarAlerta('Filial deletada com sucesso!');
      carregarFiliais();
    } catch (error) {
      console.error('Erro ao deletar filial: ' + error.message);
      mostrarAlerta('Erro ao deletar filial', 'danger');
    } finally {
      mostrarLoader(false);
    }
  }
    async function deletarFilialAPI(id) {
      mostrarLoader(true);
      try {
        const res = await fetch(`/api/filiais/${id}`, {
          method: 'DELETE',
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });

        if (!res.ok) throw new Error('Erro ao deletar filial');

        mostrarAlerta('Filial deletada com sucesso!');
        carregarFiliais(); // Atualiza a tabela
      } catch (error) {
        console.error('Erro ao deletar filial: ' + error.message);
        mostrarAlerta('Erro ao deletar filial', 'danger');
      } finally {
        mostrarLoader(false);
      }
    }

  // Inicializa a listagem
  carregarFiliais();

  // Logout
  document.getElementById('logoutButtonCompras').addEventListener('click', () => {
    localStorage.removeItem('jwtToken');
    window.location.href = 'login.html';
  });

  // Feather icons
  if (feather) feather.replace();
});
