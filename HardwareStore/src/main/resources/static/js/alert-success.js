//Función para manejar las alertas de guardado, actualización y eliminación.

    document.addEventListener("DOMContentLoaded", function () {
            let alert = document.querySelector(".alert");
            if (alert) {
                setTimeout(() => alert.style.display = 'none', 5000);
            }
        });