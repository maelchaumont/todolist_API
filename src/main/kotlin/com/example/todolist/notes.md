val dans le constructeur équivalent à (voir ci-dessous) dans la classe 

    maVar: MaVar

    init {

    this.maVar = maVar 

    }

RestController implémente Controller qui implémente lui-même Component. Component permet de retrouver un Bean, ici la CommandGateway passée en constructeur

    HttpServletResponse.sendRedirect("https://docs.oracle.com/javaee/6/api")

Question : comment on fait concrètement dans le code de la projection pour modifier seulement
le repository / et comment modifier seulement les données sur le mongodb
---
Si, dans une saga, on devait envoyer une commande à toutes les instances existantes d'un agrégat,
ça poserait problème dans le sens où on devrait sûrement faire une query (FindAll...Query) ? Pour récupérer toutes les instances.
Réponse -> c'est un problème de conception qui doit être réfléchi, il n'y a pas de solution toute faite.
Mais oui, ça pourrait être une solution d'envoyer exceptionnellement une query depuis la Saga.
---
Pour les appels asynchrones (CompletableFuture, Optional), les méthodes .get() et .join() arrêtent le thread courant
jusqu'à ce que le Futur soit complété. Ce qui pèse un petit peu sur le temps d'exécution du programme.
Axon gère tout ça automatiquement donc pas besoin de s'en préoccuper.
Dans mon cas précis, j'avais retourné des ResponseEntity(appel async.get(), HttpStatus) pour pouvoir renvoyer à la fois la réponse souhaitée
et un code de retour. Finalement, on peut retourner seulement ce qui a été retourné par
l'appel async (et donc sans mettre le .get(), pas besoin d'en mettre quand l'appel async est dans le return).
On peut modifier le code de retour (200 par défaut) grâce à l'annotation @ResponseStatus(HttpStatus.CEQUONVEUT)
---
Par défaut une Saga est sauvegardée dans le RAM (et donc disparaît quand on éteint l'ordi) à cause du InMemorySagaStore par défaut.
Une solution pour les persister est de mettre un @Bean sagaStore retournant un MongoSagaStore dans une classe avec @Configuration.
---
Les Agrégats en eux-mêmes n'ont rien à voir avec la représentation qu'ils peuvent avoir en BDD : on peut choisir d'ajouter, de modifier, de supprimer
les attributs et on a pas forcément besoin des méthodes (dans la vue = la manière dont ils sont représentés en BDD).
Au début du projet j'avais créé des classes de vue Spéciales avec des converters (TodoView, SubtaskView) mais ce n'est pas nécessaire,
la solution est de créer une classe dans un contexte différent (voir la classe TodoV2Deadline dans TodoV2Repository, dont le nom réel est 
com.example.todolist.saga.queryPart.TodoV2Repository.TodoV2Deadline, tandis que le nom réel de l'aggrégat est com.example.todolist.saga.commandPart.TodoV2Deadline)
---
Un DTO est un "Data Transfer Object", soit un objet/une classe utilisée uniquement pour faire transiter de l'information.
On peut en recevoir un dans le Body de la requête, mais aussi en renvoyer dans le Body de la response (sous forme de JSON).
Attention les DTO ne sont pas forcément comme dans la base de données, on peut transmettre moins d'infos que tout ce qui est stocké en BDD  
---
Dans le cas où on voudrait indiquer plusieurs codes de retour (exemple : une Exception a été levée donc on veut renvoyer une 500),
On peut créer un @ExceptionHandler qui renvoie une ResponseEntity<Error>.
C'est le @ExceptionHandler le plus spécifique pour une classe qui domine (ex : dans le cas où on a un handler de IllegalArgumentException et un de Exception
, dans le cas où une IllegalArgumentException est levée alors ce sera le @ExceptionHandler de IllegalArgumentException qui sera appelé).