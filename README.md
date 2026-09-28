# Examen_Estructura_Grupo2
### Módulo de Reservas (Lenin Maigua)
Se implementó la lista simplemente enlazada y la clase Reserva. El método de cancelación elimina el nodo liberando el proyector sin sumar horas, y cuenta con una validación que bloquea la acción si la reserva tiene un evento activo.

### Módulo de Inventario (Cris)
**Commit 1 — `3c23b43`:** Se incorporaron `ListaSecuencial`, `Proyector`, `Estado` y `Validacion`. La lista permite insertar, buscar, modificar, eliminar y recorrer proyectores; además, amplía su arreglo cuando se llena. El modelo valida los datos del equipo y conserva código, marca y aula al actualizar su estado u horas de lámpara.
**Commit 2 — `2e08311`:** Se agregaron seis pruebas del inventario para verificar registro y búsqueda, crecimiento del arreglo, modificación y eliminación, rechazo de datos inválidos y conservación de los datos al cambiar estado u horas. Las seis pruebas finalizaron correctamente.

### Módulo de Historial (Kleber)

Se implementó la lista doblemente enlazada (`ListaDoble`) y la clase modelo `Movimiento`. El módulo permite realizar recorridos bidireccionales (hacia adelante y hacia atrás) y cuenta con un método de filtrado por tipo de movimiento (`reservas`, `devoluciones` o `mantenimiento`) que consulta y genera sublistas con las coincidencias exactas sin alterar la integridad ni modificar los datos originales del historial.

### Módulo de Mantenimiento y Pila de Deshacer (Shirley Yanchatipan)

Se implementó el módulo de mantenimiento de proyectores y una estructura de pila para registrar las acciones y permitir deshacer el último cambio realizado.

**Aportes:**
- Implementación de la clase `Pila`.
- Implementación de la clase `AccionMantenimiento`.
- Registro de acciones de mantenimiento.
- Cambio del estado del proyector a mantenimiento.
- Funcionalidad para deshacer acciones mediante una pila LIFO.
- Validaciones relacionadas con el mantenimiento y las horas de uso de la lámpara.

**Commit:** `Agrega mantenimiento y pila de deshacer`
