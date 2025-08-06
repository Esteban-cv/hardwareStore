// swing-alert.js - Versión corregida y verificada
document.addEventListener('DOMContentLoaded', function() {
    // Obtener los mensajes de los atributos data-* del body
    const body = document.body;
    const success = body.getAttribute('data-success');
    const error = body.getAttribute('data-error');

    // Función para mostrar alertas Toast
    const showToast = (icon, title) => {
        const Toast = Swal.mixin({
            toast: true,
            position: 'top-end',
            showConfirmButton: false,
            timer: 5000,
            timerProgressBar: true,
            didOpen: (toast) => {
                toast.addEventListener('mouseenter', Swal.stopTimer);
                toast.addEventListener('mouseleave', Swal.resumeTimer);
            }
        });

        Toast.fire({
            icon: icon,
            title: title
        });
    };

    // Mostrar alertas si existen mensajes
    if (success) {
        console.log('Mostrando alerta de éxito:', success);
        showToast('success', success);
    }

    if (error) {
        console.log('Mostrando alerta de error:', error);
        showToast('error', error);
    }

    // Depuración adicional
    console.log('Datos recibidos:', {
        success: success,
        error: error
    });
});