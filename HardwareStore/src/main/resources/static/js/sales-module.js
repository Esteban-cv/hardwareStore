// ======================================
// VARIABLES GLOBALES
// ======================================
let cart = [];
let cartItemCounter = 0;

// ======================================
// INICIALIZACIÓN
// ======================================
document.addEventListener('DOMContentLoaded', function() {
    initializeSalesModule();
});

// ======================================
// FUNCIONES DE VALIDATION
// ======================================
function validateStock(productId, requestedQuantity) {
    const productCard = document.querySelector(`[data-id="${productId}"]`);
    if (!productCard) return false;
    
    const availableStock = parseInt(productCard.dataset.stock) || 0;
    return requestedQuantity <= availableStock;
}

function validateCartBeforeSubmit() {
    if (cart.length === 0) {
        showNotification('No puede procesar una venta sin productos', 'warning');
        return false;
    }

    // Validar stock actualizado para cada producto
    for (let item of cart) {
        if (!validateStock(item.id, item.quantity)) {
            showNotification(`Stock insuficiente para ${item.name}`, 'error');
            return false;
        }
    }

    return true;
}

// ======================================
// FUNCIÓN updateTotals
// ======================================
function updateTotals() {
    let subtotal = 0;

    // Calcular subtotal
    cart.forEach(item => {
        subtotal += item.price * item.quantity;
    });

    // Calcular impuestos (19%)
    const tax = subtotal * 0.19;

    // Calcular total
    const total = subtotal + tax;

    // Actualizar UI
    const subtotalElement = document.getElementById('subtotal');
    const taxElement = document.getElementById('tax');
    const totalElement = document.getElementById('total');
    const subTotalInputElement = document.getElementById('subTotalInput');
    const taxInputElement = document.getElementById('taxInput');
    const totalInputElement = document.getElementById('totalInput');

    if (subtotalElement) subtotalElement.textContent = formatCurrency(subtotal);
    if (taxElement) taxElement.textContent = formatCurrency(tax);
    if (totalElement) totalElement.textContent = formatCurrency(total);
    if (subTotalInputElement) subTotalInputElement.value = subtotal.toFixed(2);
    if (taxInputElement) taxInputElement.value = tax.toFixed(2);
    if (totalInputElement) totalInputElement.value = total.toFixed(2);

    // Actualizar estado del botón
    updateProcessButton();
}

function updateProcessButton() {
    const processButton = document.getElementById('processButton');
    const clientSelect = document.getElementById('client');
    const employeeSelect = document.getElementById('employee');
    
    if (processButton) {
        const hasItems = cart.length > 0;
        const hasClient = clientSelect && clientSelect.value !== '';
        const hasEmployee = employeeSelect && employeeSelect.value !== '';
        
        processButton.disabled = !(hasItems && hasClient && hasEmployee);
    }
}

// ======================================
// INICIALIZACIÓN DEL MÓDULO
// ======================================
function initializeSalesModule() {
    // Configurar funcionalidades
    setupSearchFilter();
    setupFormSubmission();
    setupClientEmployeeValidation();

    // Actualizar display inicial
    updateCartDisplay();
    updateTotals();
}

// ======================================
// VALIDACIÓN DE CLIENTE Y EMPLEADO
// ======================================
function setupClientEmployeeValidation() {
    const clientSelect = document.getElementById('client');
    const employeeSelect = document.getElementById('employee');

    if (clientSelect) {
        clientSelect.addEventListener('change', updateProcessButton);
    }

    if (employeeSelect) {
        employeeSelect.addEventListener('change', updateProcessButton);
    }
}

// ======================================
// BÚSQUEDA Y FILTRADO DE PRODUCTOS
// ======================================
function setupSearchFilter() {
    const searchInput = document.getElementById('productSearch');

    if (!searchInput) {
        console.warn('No se encontró el elemento de búsqueda');
        return;
    }

    searchInput.addEventListener('input', function(e) {
        const searchTerm = e.target.value.toLowerCase().trim();
        const productCards = document.querySelectorAll('.product-card');

        if (!productCards || productCards.length === 0) {
            console.warn('No se encontraron productos para filtrar');
            return;
        }

        productCards.forEach(card => {
            try {
                const name = card.dataset.name?.toLowerCase() || '';
                const code = card.dataset.code?.toLowerCase() || '';
                const categoryElement = card.querySelector('.product-category');
                const category = categoryElement?.textContent?.toLowerCase() || '';

                const matches = name.includes(searchTerm) ||
                               code.includes(searchTerm) ||
                               category.includes(searchTerm);

                card.style.display = matches ? 'block' : 'none';
            } catch (error) {
                console.error('Error procesando tarjeta de producto:', error);
                card.style.display = 'none';
            }
        });
    });
}

// ======================================
// GESTIÓN DEL CARRITO
// ======================================
function addToCart(button) {
    const productCard = button.closest('.product-card');

    if (!productCard) {
        showNotification('Error: No se pudo encontrar la información del producto', 'error');
        return;
    }

    const productData = extractProductData(productCard);

    if (productData.stock <= 0) {
        showNotification('Producto sin stock disponible', 'error');
        return;
    }

    // Animación visual
    button.classList.add('loading');
    productCard.classList.add('adding');

    setTimeout(() => {
        const existingItem = cart.find(item => item.id === productData.id);

        if (existingItem) {
            if (existingItem.quantity < productData.stock) {
                existingItem.quantity += 1;
                showNotification(`Cantidad actualizada: ${productData.name}`, 'success');
            } else {
                showNotification('No hay suficiente stock disponible', 'warning');
            }
        } else {
            cart.push({
                id: productData.id,
                name: productData.name,
                code: productData.code,
                price: productData.price,
                stock: productData.stock,
                quantity: 1
            });
            showNotification(`Agregado al carrito: ${productData.name}`, 'success');
        }

        updateCartDisplay();
        updateTotals();

        // Remover animaciones
        button.classList.remove('loading');
        productCard.classList.remove('adding');
    }, 300);
}

function extractProductData(productCard) {
    return {
        id: parseInt(productCard.dataset.id) || 0,
        name: productCard.dataset.name || 'Producto sin nombre',
        code: productCard.dataset.code || 'Sin código',
        price: parseFloat(productCard.dataset.price) || 0,
        stock: parseInt(productCard.dataset.stock) || 0
    };
}

function updateQuantity(productId, newQuantity) {
    const item = cart.find(item => item.id === productId);

    if (!item) {
        showNotification('Producto no encontrado en el carrito', 'error');
        return;
    }

    if (newQuantity <= 0) {
        removeFromCart(productId);
        return;
    }

    if (!validateStock(productId, newQuantity)) {
        showNotification('Cantidad excede el stock disponible', 'warning');
        // Restaurar el valor anterior en el input
        const quantityInput = document.querySelector(`input[data-product-id="${productId}"]`);
        if (quantityInput) {
            quantityInput.value = item.quantity;
        }
        return;
    }

    item.quantity = newQuantity;
    updateCartDisplay();
    updateTotals();
}

function removeFromCart(productId) {
    const itemIndex = cart.findIndex(item => item.id === productId);

    if (itemIndex > -1) {
        const itemName = cart[itemIndex].name;
        cart.splice(itemIndex, 1);
        updateCartDisplay();
        updateTotals();
        showNotification(`Removido del carrito: ${itemName}`, 'info');
    }
}

function clearCart() {
    if (cart.length === 0) {
        showNotification('El carrito ya está vacío', 'info');
        return;
    }

    if (confirm('¿Está seguro de que desea limpiar el carrito?')) {
        cart = [];
        updateCartDisplay();
        updateTotals();
        showNotification('Carrito limpiado exitosamente', 'info');
    }
}

// ======================================
// ACTUALIZACIÓN DE LA UI
// ======================================
function updateCartDisplay() {
    const cartCount = document.getElementById('cartCount');
    const emptyCart = document.getElementById('emptyCart');
    const cartItems = document.getElementById('cartItems');
    const processButton = document.getElementById('processButton');

    // Actualizar contador
    if (cartCount) {
        cartCount.textContent = `(${cart.length})`;
    }

    // Mostrar/ocultar elementos
    if (cart.length === 0) {
        if (emptyCart) emptyCart.style.display = 'block';
        if (cartItems) cartItems.classList.remove('active');
    } else {
        if (emptyCart) emptyCart.style.display = 'none';
        if (cartItems) cartItems.classList.add('active');
    }

    // Generar HTML de items del carrito
    if (cartItems) {
        cartItems.innerHTML = cart.map(item => `
            <div class="cart-item" data-id="${item.id}">
                <div class="cart-item-info">
                    <div class="cart-item-name">${escapeHtml(item.name)}</div>
                    <div class="cart-item-code">${escapeHtml(item.code)}</div>
                </div>
                <div class="cart-item-controls">
                    <div class="cart-item-price">${formatCurrency(item.price)}</div>
                    <div class="quantity-controls">
                        <button class="quantity-btn" onclick="updateQuantity(${item.id}, ${item.quantity-1})" type="button">-</button>
                        <input type="number" class="quantity-input" value="${item.quantity}"
                               data-product-id="${item.id}"
                               onchange="updateQuantity(${item.id}, parseInt(this.value) || 0)"
                               min="1" max="${item.stock}" title="Cantidad (Max: ${item.stock})">
                        <button class="quantity-btn" onclick="updateQuantity(${item.id}, ${item.quantity+1})" type="button">+</button>
                    </div>
                    <button class="remove-item-btn" onclick="removeFromCart(${item.id})" title="Eliminar producto" type="button">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </div>
        `).join('');
    }

    // Actualizar estado del botón de procesar
    updateProcessButton();
}

// ======================================
// GENERACIÓN DE INPUTS OCULTOS PARA FORMULARIO
// ======================================
function generateCartInputs() {
    const cartItemsHidden = document.getElementById('cartItemsHidden');

    if (!cartItemsHidden) {
        console.error('No se encontró el contenedor de inputs ocultos');
        return false;
    }

    cartItemsHidden.innerHTML = ''; // Limpiar inputs anteriores

    if (cart.length === 0) {
        console.warn('No hay items en el carrito para generar inputs');
        return false;
    }

    cart.forEach((item, index) => {
        // Input para ID del artículo
        const inputId = document.createElement('input');
        inputId.type = 'hidden';
        inputId.name = 'articleIds';
        inputId.value = item.id;
        cartItemsHidden.appendChild(inputId);

        // Input para cantidad
        const inputQuantity = document.createElement('input');
        inputQuantity.type = 'hidden';
        inputQuantity.name = 'quantities';
        inputQuantity.value = item.quantity;
        cartItemsHidden.appendChild(inputQuantity);

        // Input para precio unitario
        const inputPrice = document.createElement('input');
        inputPrice.type = 'hidden';
        inputPrice.name = 'prices';
        inputPrice.value = item.price.toFixed(2);
        cartItemsHidden.appendChild(inputPrice);
    });

    return true;
}

// ======================================
// CONFIGURACIÓN DEL FORMULARIO
// ======================================
function setupFormSubmission() {
    const saleForm = document.getElementById('saleForm');

    if (!saleForm) {
        console.warn('No se encontró el formulario de ventas');
        return;
    }

    saleForm.addEventListener('submit', function(e) {
        // Prevenir envío por defecto
        e.preventDefault();

        // Asegurar que los totales estén actualizados justo antes de enviar
        updateTotals();

        // Validaciones
        if (!validateCartBeforeSubmit()) {
            return;
        }

        // Validar cliente y empleado
        const clientSelect = document.getElementById('client');
        const employeeSelect = document.getElementById('employee');

        if (!clientSelect.value) {
            showNotification('Debe seleccionar un cliente', 'warning');
            clientSelect.focus();
            return;
        }

        if (!employeeSelect.value) {
            showNotification('Debe seleccionar un empleado', 'warning');
            employeeSelect.focus();
            return;
        }

        // Generar inputs ocultos para el carrito
        if (!generateCartInputs()) {
            showNotification('Error al preparar los datos de la venta', 'error');
            return;
        }

        // Deshabilitar botón y mostrar indicador de carga
        const processButton = document.getElementById('processButton');
        if (processButton) {
            processButton.disabled = true;
            processButton.innerHTML = '<i class="fas fa-spinner fa-spin"></i> <span>Procesando...</span>';
        }

        // Mostrar notificación de procesamiento
        showNotification('Procesando venta...', 'info');

        // Enviar formulario
        try {
            saleForm.submit();
        } catch (error) {
            console.error('Error al enviar formulario:', error);
            showNotification('Error al procesar la venta', 'error');
            
            // Restaurar botón
            if (processButton) {
                processButton.disabled = false;
                processButton.innerHTML = '<i class="fas fa-cash-register"></i> <span>Procesar Venta</span>';
            }
        }
    });
}

// ======================================
// SISTEMA DE NOTIFICACIONES
// ======================================
function showNotification(message, type = 'info') {
    // Crear el contenedor de notificaciones si no existe
    let notificationContainer = document.getElementById('notificationContainer');

    if (!notificationContainer) {
        notificationContainer = document.createElement('div');
        notificationContainer.id = 'notificationContainer';
        notificationContainer.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 10000;
            pointer-events: none;
        `;
        document.body.appendChild(notificationContainer);
    }

    // Crear la notificación
    const notification = document.createElement('div');
    notification.style.cssText = `
        background: ${getNotificationColor(type)};
        color: white;
        padding: 12px 20px;
        border-radius: 6px;
        margin-bottom: 10px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        opacity: 0;
        transform: translateX(100%);
        transition: all 0.3s ease;
        pointer-events: auto;
        max-width: 300px;
        word-wrap: break-word;
    `;

    notification.textContent = message;
    notificationContainer.appendChild(notification);

    // Animar entrada
    setTimeout(() => {
        notification.style.opacity = '1';
        notification.style.transform = 'translateX(0)';
    }, 10);

    // Remover después de 4 segundos
    setTimeout(() => {
        notification.style.opacity = '0';
        notification.style.transform = 'translateX(100%)';

        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    }, 4000);
}

function getNotificationColor(type) {
    const colors = {
        success: '#10b981',
        error: '#ef4444',
        warning: '#f59e0b',
        info: '#3b82f6'
    };
    return colors[type] || colors.info;
}

// ======================================
// FUNCIONES UTILITARIAS
// ======================================
function formatCurrency(amount) {
    return new Intl.NumberFormat('es-CO', {
        style: 'currency',
        currency: 'COP',
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    }).format(amount);
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// ======================================
// MANEJO DE ERRORES GLOBALES
// ======================================
window.addEventListener('error', function(e) {
    console.error('Error global capturado:', e.error);
    showNotification('Ha ocurrido un error inesperado', 'error');
});

// ======================================
// NAVEGACIÓN DEL SIDEBAR (CÓDIGO EXISTENTE)
// ======================================
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