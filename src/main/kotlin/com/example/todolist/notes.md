val dans le constructeur équivalent à (voir ci-dessous) dans la classe 

    maVar: MaVar

    init {

    this.maVar = maVar 

    }

RestController implémente Controller qui implémente lui-même Component. Component permet de retrouver un Bean, ici la CommandGateway passée en constructeur

    HttpServletResponse.sendRedirect("https://docs.oracle.com/javaee/6/api")

Question : comment on fait concrètement dans le code de la projection pour modifier seulement
le repository / et comment modifier seulement les données sur le mongodb

Si, dans une saga, on devait envoyer une commande à toutes les instances existantes d'un agrégat,
ça poserait problème dans le sens où on devrait sûrement faire une query (FindAll...Query) ? Pour récupérer toutes les instances.
Réponse -> c'est un problème de conception qui doit être réfléchi, il n'y a pas de solution toute faite.
Mais oui, ça pourrait être une solution d'envoyer exceptionellement une query depuis la Saga.

Pour les appels asynchrones (CompletableFuture, Optional), les méthodes .get() et .join() arrêtent le thread courant
jusqu'à ce que le Futur soit complété. Ce qui pèse un petit peu sur le temps d'exécution du programme.
Axon gère tout ça automatiquement donc pas besoin de s'en préoccuper.
Dans mon cas précis, j'avais retourné des ResponseEntity(appel async.get(), HttpStatus) pour pouvoir renvoyer à la fois la réponse souhaitée
et un code de retour. Finalement, on peut retourner seulement ce qui a été retourné par
l'appel async (et donc sans mettre le .get(), pas besoin d'en mettre quand l'appel async est dans le return).
On peut modifier le code de retour (200 par défaut) grâce à l'annotation @ResponseStatus(HttpStatus.CEQUONVEUT)