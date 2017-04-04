# Almundo

## Challenge de Almundo

### Diagramas UML
Los diagramas UML se encuentran en la carpeta docs.
Los hice a mano y luego les saqu� una foto ya que no sab�a que programa iban a usar ustedes para abrirlo y porque hacerlo en word me hubiera tomado m�s tiempo que hacerlos a mano.

### Resoluci�n de puntos extra

* **Dar alguna soluci�n sobre qu� pasa con una llamada cuando no hay ning�n empleado libre**
Para �ste caso lo que se utiliz� fue una cola de llamadas, implementada usando ConcurrentLinkedQueue para evitar problemas de concurrencia. Al finalizar una llamada, se notifica la finalizaci�n de la misma y en el caso de haber llamadas en la lista mencionada anteriormente, se busca un empleado AVAILABLE. Los empleados tienen 3 estados posibles AVAILABLE, ASSIGNED, ON_CALL, cuando empiezan una llamada se ponen en ON_CALL, al finalizar pasan a AVAILABLE, y cuando se los asigna a una llamada est�n en ASIGNADOS.

* **Dar alguna soluci�n sobre qu� pasa con una llamada cuando entran m�s de 10 llamadas concurrentes**
Para �sto se utiliz� un Fixed Thread Pool creado con 10 threads. �ste Executor tiene la particularidad que mantiene esos 10 threads y en el caso que se le envien nuevas calls para ejecutar, lss mismss se encolan hasta que se libere algun thread. Los empleados se asignan antes de enviarlos al executor, por lo que en cuanto se libera un thread, las calls se pueden atender de inmediato. 