# Aporte individual - Cola de espera

## Integrante
Lenin

## Estructura asignada
Cola FIFO para la gestión de solicitudes de proyectores.

## Archivos trabajados
- `src/estructuras/Cola.java`
- `src/modelo/Solicitud.java`
- `src/PruebaCola.java`

## Trabajo realizado
Se implementó una cola genérica utilizando nodos enlazados, manteniendo el principio FIFO (First In, First Out).

La estructura permite:
- Encolar solicitudes.
- Desencolar solicitudes.
- Consultar el elemento que se encuentra al frente.
- Buscar elementos sin eliminarlos.
- Recorrer la cola.
- Consultar el tamaño de la cola.
- Verificar si la cola está vacía.
- Consultar la posición de un docente dentro de la cola.

## Mejora implementada
Se agregó el método `posicion`, que permite conocer la posición de una solicitud utilizando un criterio de búsqueda.

La consulta recorre internamente los nodos sin utilizar el método `desencolar`, por lo que no modifica el contenido ni el orden de la cola.

Si el elemento se encuentra en la cola, se devuelve su posición comenzando desde 1. Si no existe, se devuelve -1.

## Prueba realizada
Para comprobar el funcionamiento se ingresaron tres solicitudes:

1. Docente A
2. Docente B
3. Docente C

Se realizó una consulta utilizando la cédula del Docente B y el sistema devolvió la posición 2.

Después de realizar la consulta, se volvió a recorrer la cola y se comprobó que permanecía en el mismo orden:

Docente A -> Docente B -> Docente C

## Resultado
La consulta de posición funciona correctamente y mantiene el comportamiento FIFO de la cola, ya que buscar un docente no elimina ni cambia de posición las solicitudes registradas.