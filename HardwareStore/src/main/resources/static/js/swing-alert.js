document.addEventListener('DOMContentLoaded', function () {

    // --- CONFIRMACIÓN EN EL SUBMIT DEL FORM ---
    const form = document.querySelector('form.form-content');
    if (form) {
        form.addEventListener('submit', function (e) {
            if (form.dataset.confirmed === 'true') return; // evitar doble envío

            // Validación HTML5
            if (!form.checkValidity()) {
                e.preventDefault();
                form.reportValidity();
                return;
            }

            // --- Evitar cadena vacía en RUT ---
            const rutInput = document.getElementById('rut');
            if (rutInput && rutInput.value.trim() === '') {
                rutInput.value = ''; // Para HTML5
                rutInput.removeAttribute('name'); // Evita enviarlo si está vacío
            }

            e.preventDefault();

            const idVal = document.getElementById('idClient')?.value?.trim();
            const isUpdate = !!(idVal && idVal.length > 0);

            Swal.fire({
                title: isUpdate ? '¿Actualizar cliente?' : '¿Guardar nuevo cliente?',
                text: isUpdate
                    ? 'Se actualizará la información del cliente.'
                    : 'Se guardará la información del cliente.',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: isUpdate ? '#3085d6' : '#28a745',
                cancelButtonColor: '#d33',
                confirmButtonText: isUpdate ? 'Sí, actualizar' : 'Sí, guardar',
                cancelButtonText: 'Cancelar',
                allowOutsideClick: false
            }).then((result) => {
                if (result.isConfirmed) {
                    form.dataset.confirmed = 'true';
                    form.submit();
                }
            });
        });
    }

    // --- CONFIRMACIÓN PARA ELIMINAR ---
    document.querySelectorAll('.btn-delete').forEach(btn => {
        btn.addEventListener('click', function (e) {
            e.preventDefault();
            const url = this.getAttribute('href');
            Swal.fire({
                title: '¿Eliminar registro?',
                text: 'Esta acción no se puede deshacer.',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#d33',
                cancelButtonColor: '#3085d6',
                confirmButtonText: 'Sí, eliminar',
                cancelButtonText: 'Cancelar',
                allowOutsideClick: false
            }).then((result) => {
                if (result.isConfirmed) {
                    window.location.href = url;
                }
            });
        });
    });

    // --- FUNCIÓN PARA MOSTRAR ERRORES DESDE EL BACKEND ---
    window.showDeleteError = function (message, title = 'Error al eliminar') {
        Swal.fire({
            title: title,
            text: message,
            icon: 'error',
            confirmButtonColor: '#d33',
            confirmButtonText: 'Entendido',
            allowOutsideClick: false
        });
    };

});
