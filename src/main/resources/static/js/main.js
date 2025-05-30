// Funções para serem usadas em múltiplas páginas

// Ativar ícones Feather
if (typeof feather !== 'undefined') {
    feather.replace();
}

// Lógica para ativar o link do menu lateral correspondente à página atual
(function () {
    'use strict';
    const sidebar = document.getElementById('sidebarMenu');
    if (!sidebar) return;

    const currentPage = window.location.pathname.split("/").pop() || 'index.html'; // Default to index if path is just /
    let navId;

    if (currentPage === 'dashboard.html' || currentPage === 'index.html' || currentPage === '') navId = 'navDashboard';
    else if (currentPage === 'compras.html') navId = 'navCompras';
    else if (currentPage === 'ordens_cliente.html') navId = 'navOrdemCliente';
    else if (currentPage === 'medicamentos.html') navId = 'navMedicamentos';
    else if (currentPage === 'estoque.html') navId = 'navEstoque';
    else if (currentPage === 'filiais.html') navId = 'navFiliais';
    else if (currentPage === 'relatorio.html') navId = 'navRelatorio';
    else if (currentPage === 'financas.html') navId = 'navFinancas';
    else if (currentPage === 'config_item.html') navId = 'navConfigItem';
    else if (currentPage === 'config_usuario.html') navId = 'navConfigUsuario';
    else if (currentPage === 'config_opcoes.html') navId = 'navConfigOpcoes';

    if (navId) {
        const activeNavLink = document.getElementById(navId);
        if (activeNavLink) {
            // Remove 'active' de qualquer outro link que possa ter (segurança)
            sidebar.querySelectorAll('.nav-link.active').forEach(link => {
                link.classList.remove('active');
                link.removeAttribute('aria-current');
            });
            // Adiciona 'active' ao link correto
            activeNavLink.classList.add('active');
            activeNavLink.setAttribute('aria-current', 'page');
        }
    }
})();

// Lógica de Logout (pode ser otimizada para não repetir IDs)
function setupLogoutButton(buttonId) {
    const logoutBtn = document.getElementById(buttonId);
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function(event) {
            event.preventDefault();
            localStorage.removeItem('jwtToken');
            window.location.href = 'login.html';
        });
    }
}

// Chame para os IDs dos botões de logout nas diferentes páginas
// Isso pode ser chamado condicionalmente ou você pode usar uma classe comum para o botão de logout
setupLogoutButton('logoutButtonDash');
setupLogoutButton('logoutButtonMed');
setupLogoutButton('logoutButtonCompras'); // Adicione para cada página placeholder
// ... e assim por diante para os outros IDs de logout que você criar