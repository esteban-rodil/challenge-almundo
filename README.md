# Almundo

## Challenge de Almundo

### Diagramas UML
Los diagramas UML se encuentran en la carpeta docs.
Los hice a mano y luego les saqué una foto ya que no sabía que programa iban a usar ustedes para abrirlo y porque hacerlo en word me hubiera tomado más tiempo que hacerlos a mano.

### Resolución de puntos extra

* **Dar alguna solución sobre qué pasa con una llamada cuando no hay ningún empleado libre**
Para éste caso lo que se utilizó fue una cola de llamadas, implementada usando ConcurrentLinkedQueue para evitar problemas de concurrencia. Al finalizar una llamada, se notifica la finalización de la misma y en el caso de haber llamadas en la lista mencionada anteriormente, se busca un empleado AVAILABLE. Los empleados tienen 3 estados posibles AVAILABLE, ASSIGNED, ON_CALL, cuando empiezan una llamada se ponen en ON_CALL, al finalizar pasan a AVAILABLE, y cuando se los asigna a una llamada están en ASIGNADOS.

* **Dar alguna solución sobre qué pasa con una llamada cuando entran más de 10 llamadas concurrentes**
Para ésto se utilizó un Fixed Thread Pool creado con 10 threads. Éste Executor tiene la particularidad que mantiene esos 10 threads y en el caso que se le envien nuevas calls para ejecutar, lss mismss se encolan hasta que se libere algun thread. Los empleados se asignan antes de enviarlos al executor, por lo que en cuanto se libera un thread, las calls se pueden atender de inmediato. 