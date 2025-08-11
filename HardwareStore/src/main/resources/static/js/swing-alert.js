document.addEventListener('DOMContentLoaded', function() {
    // Función para manejar el botón de actualizar/guardar
    const btnUpdate = document.getElementById('btnUpdate');
    
    if (btnUpdate) {
        btnUpdate.addEventListener('click', function(e) {
            e.preventDefault();
            
            const btnText = this.querySelector('span')?.textContent.trim() || '';
            const isUpdate = btnText === 'Actualizar';

            Swal.fire({
                title: isUpdate ? '¿Estás seguro?' : '¿Guardar cambios?',
                text: isUpdate ? "Se actualizará la información" : "Se guardará la información",
                icon: isUpdate ? 'warning' : 'question',
                showCancelButton: true,
                confirmButtonColor: isUpdate ? '#3085d6' : '#28a745',
                cancelButtonColor: '#d33',
                confirmButtonText: isUpdate ? 'Sí, actualizar' : 'Sí, guardar',
                cancelButtonText: 'Cancelar'
            }).then((result) => {
                if (result.isConfirmed) {
                    this.closest('form').submit();
                }
            });
        });
    }

    // Función para manejar los botones de eliminar
    const deleteButtons = document.querySelectorAll('.btn-delete');
    
    deleteButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            
            Swal.fire({
                title: '¿Estás seguro?',
                text: "Esta acción no se puede deshacer",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#d33',
                cancelButtonColor: '#3085d6',
                confirmButtonText: 'Sí, eliminar',
                cancelButtonText: 'Cancelar',
                reverseButtons: true
            }).then((result) => {
                if (result.isConfirmed) {
                    this.closest('form').submit();
                }
            });
        });
    });
});