/**
 * Modal Reutilizable para el proyecto Ferretería Store
 */
class UniversalModal {
    constructor() {
        this.modal = null;
        this.isOpen = false;
        this.init();
    }

    init() {
        this.createModalHTML();
        this.bindEvents();
    }

    createModalHTML() {
        // Crear el HTML del modal si no existe
        if (!document.getElementById('universalModal')) {
            const modalHTML = `
                <div id="universalModal" class="modal-overlay">
                    <div class="modal-container">
                        <div class="modal-header">
                            <h2 class="modal-title" id="modalTitle">
                                <i class="fas fa-info-circle"></i>
                                <span>Información</span>
                            </h2>
                            <button class="modal-close" id="modalClose">
                                <i class="fas fa-times"></i>
                            </button>
                        </div>
                        <div class="modal-body" id="modalBody">
                            <div class="modal-loading">
                                <div class="modal-spinner"></div>
                            </div>
                        </div>
                    </div>
                </div>
            `;
            document.body.insertAdjacentHTML('beforeend', modalHTML);
        }
        
        this.modal = document.getElementById('universalModal');
    }

    bindEvents() {
        const closeBtn = document.getElementById('modalClose');
        const overlay = this.modal;

        // Cerrar con botón X
        closeBtn.addEventListener('click', () => this.close());

        // Cerrar clickeando fuera del modal
        overlay.addEventListener('click', (e) => {
            if (e.target === overlay) {
                this.close();
            }
        });

        // Cerrar con tecla Escape
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && this.isOpen) {
                this.close();
            }
        });
    }

    open(title, content, icon = 'fas fa-info-circle') {
        const titleElement = document.querySelector('#modalTitle span');
        const iconElement = document.querySelector('#modalTitle i');
        const bodyElement = document.getElementById('modalBody');

        titleElement.textContent = title;
        iconElement.className = icon;
        bodyElement.innerHTML = content;

        this.modal.classList.add('active');
        this.isOpen = true;
        document.body.style.overflow = 'hidden'; // Prevenir scroll del body
    }

    close() {
        this.modal.classList.remove('active');
        this.isOpen = false;
        document.body.style.overflow = ''; // Restaurar scroll del body
    }

    // Método para cargar datos via AJAX
    async loadData(url, title, icon = 'fas fa-info-circle') {
        this.open(title, '<div class="modal-loading"><div class="modal-spinner"></div></div>', icon);
        
        try {
            const response = await fetch(url);
            if (!response.ok) throw new Error('Error al cargar los datos');
            
            const data = await response.json();
            const content = this.formatContent(data);
            
            document.getElementById('modalBody').innerHTML = content;
        } catch (error) {
            console.error('Error:', error);
            document.getElementById('modalBody').innerHTML = `
                <div style="text-align: center; padding: 20px; color: #ef4444;">
                    <i class="fas fa-exclamation-triangle" style="font-size: 2rem; margin-bottom: 10px;"></i>
                    <p>Error al cargar la información</p>
                </div>
            `;
        }
    }

    // Formatear contenido según el tipo de datos
    formatContent(data) {
        if (data.type === 'sale') {
            return this.formatSaleContent(data);
        } else if (data.type === 'purchase') {
            return this.formatPurchaseContent(data);
        } else if (data.type === 'inventory') {
            return this.formatInventoryContent(data);
        }
        // Agregar más tipos según necesites
        return this.formatGenericContent(data);
    }

    formatSaleContent(data) {
        return `
            <div class="modal-section">
                <div class="modal-section-title">
                    <i class="fas fa-user"></i>
                    Información General
                </div>
                <div class="modal-info-grid">
                    <div class="modal-info-item">
                        <div class="modal-info-label">ID Venta</div>
                        <div class="modal-info-value">${data.idSale}</div>
                    </div>
                    <div class="modal-info-item">
                        <div class="modal-info-label">Fecha</div>
                        <div class="modal-info-value">${data.date}</div>
                    </div>
                    <div class="modal-info-item">
                        <div class="modal-info-label">Cliente</div>
                        <div class="modal-info-value">${data.clientName}</div>
                    </div>
                    <div class="modal-info-item">
                        <div class="modal-info-label">Empleado</div>
                        <div class="modal-info-value">${data.employeeName}</div>
                    </div>
                </div>
            </div>
            
            <div class="modal-section">
                <div class="modal-section-title">
                    <i class="fas fa-shopping-cart"></i>
                    Productos Vendidos
                </div>
                <table class="modal-table">
                    <thead>
                        <tr>
                            <th>Producto</th>
                            <th>Código</th>
                            <th>Cantidad</th>
                            <th>Precio Unit.</th>
                            <th>Total</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${data.details.map(detail => `
                            <tr>
                                <td>${detail.articleName}</td>
                                <td>${detail.articleCode}</td>
                                <td>${detail.quantity}</td>
                                <td>$${detail.unitPrice.toLocaleString()}</td>
                                <td>$${detail.total.toLocaleString()}</td>
                            </tr>
                        `).join('')}
                        <tr class="modal-summary-row">
                            <td colspan="4"><strong>Subtotal:</strong></td>
                            <td><strong>$${data.subTotal.toLocaleString()}</strong></td>
                        </tr>
                        <tr class="modal-summary-row">
                            <td colspan="4"><strong>IVA (19%):</strong></td>
                            <td><strong>$${data.tax.toLocaleString()}</strong></td>
                        </tr>
                        <tr class="modal-summary-row total-row">
                            <td colspan="4"><strong>TOTAL:</strong></td>
                            <td><strong>$${data.total.toLocaleString()}</strong></td>
                        </tr>
                    </tbody>
                </table>
            </div>
            
            ${data.observations ? `
                <div class="modal-section">
                    <div class="modal-section-title">
                        <i class="fas fa-comment"></i>
                        Observaciones
                    </div>
                    <div class="modal-info-item">
                        <div class="modal-info-value">${data.observations}</div>
                    </div>
                </div>
            ` : ''}
        `;
    }

    formatPurchaseContent(data) {
        return `
            <div class="modal-section">
                <div class="modal-section-title">
                    <i class="fas fa-truck"></i>
                    Información de Compra
                </div>
                <div class="modal-info-grid">
                    <div class="modal-info-item">
                        <div class="modal-info-label">ID Compra</div>
                        <div class="modal-info-value">${data.idPurchase}</div>
                    </div>
                    <div class="modal-info-item">
                        <div class="modal-info-label">Fecha</div>
                        <div class="modal-info-value">${data.date}</div>
                    </div>
                    <div class="modal-info-item">
                        <div class="modal-info-label">Proveedor</div>
                        <div class="modal-info-value">${data.supplierName}</div>
                    </div>
                    <div class="modal-info-item">
                        <div class="modal-info-label">Total</div>
                        <div class="modal-info-value">$${data.total.toLocaleString()}</div>
                    </div>
                </div>
            </div>
            
            <div class="modal-section">
                <div class="modal-section-title">
                    <i class="fas fa-boxes"></i>
                    Productos Comprados
                </div>
                <table class="modal-table">
                    <thead>
                        <tr>
                            <th>Producto</th>
                            <th>Cantidad</th>
                            <th>Precio Unit.</th>
                            <th>Total</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${data.details.map(detail => `
                            <tr>
                                <td>${detail.articleName}</td>
                                <td>${detail.quantity}</td>
                                <td>$${detail.unitPrice.toLocaleString()}</td>
                                <td>$${detail.total.toLocaleString()}</td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
        `;
    }

    formatInventoryContent(data) {
        return `
            <div class="modal-section">
                <div class="modal-section-title">
                    <i class="fas fa-box"></i>
                    Información del Producto
                </div>
                <div class="modal-info-grid">
                    <div class="modal-info-item">
                        <div class="modal-info-label">Código</div>
                        <div class="modal-info-value">${data.code}</div>
                    </div>
                    <div class="modal-info-item">
                        <div class="modal-info-label">Nombre</div>
                        <div class="modal-info-value">${data.name}</div>
                    </div>
                    <div class="modal-info-item">
                        <div class="modal-info-label">Categoría</div>
                        <div class="modal-info-value">${data.categoryName}</div>
                    </div>
                    <div class="modal-info-item">
                        <div class="modal-info-label">Stock Actual</div>
                        <div class="modal-info-value">${data.quantity}</div>
                    </div>
                    <div class="modal-info-item">
                        <div class="modal-info-label">Precio Venta</div>
                        <div class="modal-info-value">$${data.salePrice.toLocaleString()}</div>
                    </div>
                    <div class="modal-info-item">
                        <div class="modal-info-label">Ubicación</div>
                        <div class="modal-info-value">${data.locationName || 'No asignada'}</div>
                    </div>
                </div>
            </div>
            
            ${data.description ? `
                <div class="modal-section">
                    <div class="modal-section-title">
                        <i class="fas fa-info"></i>
                        Descripción
                    </div>
                    <div class="modal-info-item">
                        <div class="modal-info-value">${data.description}</div>
                    </div>
                </div>
            ` : ''}
        `;
    }

    formatGenericContent(data) {
        return `
            <div class="modal-section">
                <div class="modal-info-grid">
                    ${Object.entries(data).map(([key, value]) => {
                        if (key !== 'type') {
                            return `
                                <div class="modal-info-item">
                                    <div class="modal-info-label">${key}</div>
                                    <div class="modal-info-value">${value}</div>
                                </div>
                            `;
                        }
                    }).join('')}
                </div>
            </div>
        `;
    }
}

// Inicializar el modal cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', function() {
    window.universalModal = new UniversalModal();
    
    // Bind event listeners para botones de vista
    bindViewButtons();
});

// Función para vincular los botones de vista
function bindViewButtons() {
    document.addEventListener('click', function(e) {
        if (e.target.closest('.btn-view')) {
            e.preventDefault();
            const button = e.target.closest('.btn-view');
            const id = button.getAttribute('data-id');
            const type = button.getAttribute('data-type');
            
            if (id && type) {
                showDetails(id, type);
            }
        }
    });
}

// Función para mostrar detalles
function showDetails(id, type) {
    const endpoints = {
        'sale': `/sales/view/${id}`,
        'purchase': `/purchases/view/${id}`,
        'inventory': `/inventory/view/${id}`,
        'entry': `/entries/view/${id}`,
        'issue': `/issue/view/${id}`
    };
    
    const titles = {
        'sale': 'Detalle de Venta',
        'purchase': 'Detalle de Compra', 
        'inventory': 'Detalle de Producto',
        'entry': 'Detalle de Entrada',
        'issue': 'Detalle de Salida'
    };
    
    const icons = {
        'sale': 'fas fa-shopping-cart',
        'purchase': 'fas fa-truck',
        'inventory': 'fas fa-box',
        'entry': 'fas fa-right-to-bracket',
        'issue': 'fas fa-right-from-bracket'
    };
    
    const url = endpoints[type];
    const title = titles[type] || 'Detalles';
    const icon = icons[type] || 'fas fa-info-circle';
    
    if (url) {
        window.universalModal.loadData(url, title, icon);
    }
}