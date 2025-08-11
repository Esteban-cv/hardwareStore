// ======================================
// MANEJO DE ERRORES GLOBALES
// ======================================
window.addEventListener('error', function(e) {
    console.error('Error global capturado:', e.error);
    showNotification('Ha ocurrido un error inesperado', 'error');
});

document.addEventListener('DOMContentLoaded', function() {
    // Toggle submenús
    const hasSubmenuItems = document.querySelectorAll('.has-submenu');

    hasSubmenuItems.forEach(item => {
        const link = item.querySelector('.nav-link');

        link.addEventListener('click', function(e) {
            // Solo manejar clicks si no es un enlace directo
            if (this.getAttribute('href') === '#') {
                e.preventDefault();
            }

            // Cerrar otros submenús primero
            hasSubmenuItems.forEach(otherItem => {
                if (otherItem !== item) {
                    otherItem.classList.remove('active');
                }
            });

            // Toggle el submenú actual
            item.classList.toggle('active');
        });
    });

    // Filtrado de menú por búsqueda
    const sidebarSearch = document.getElementById('sidebarSearch');
    const navItems = document.querySelectorAll('.nav-item');

    if (sidebarSearch) {
        sidebarSearch.addEventListener('input', function() {
            const searchTerm = this.value.toLowerCase();

            navItems.forEach(item => {
                const text = item.textContent.toLowerCase();
                if (text.includes(searchTerm)) {
                    item.style.display = 'block';

                    // Mostrar padres de submenús si coincide
                    let parent = item.closest('.sub-menu');
                    while (parent) {
                        const parentItem = parent.closest('.nav-item');
                        if (parentItem) {
                            parentItem.style.display = 'block';
                            parentItem.classList.add('active');
                        }
                        parent = parent.parentElement.closest('.sub-menu');
                    }
                } else {
                    item.style.display = 'none';
                }
            });
        });
    }
});