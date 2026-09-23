# TP JUnit - Gestion de comptes bancaires

## Présentation

Ce projet est un TP sur les tests unitaires avec JUnit 5. Il implémente une gestion
simple de comptes bancaires et la suite de tests qui va avec.

Deux classes métier :

- `CompteBancaire` : un compte identifié par un IBAN, avec dépôt, retrait dans la
  limite d'un découvert autorisé, calcul d'intérêts et détection de découvert.
- `GestionnaireComptes` : gère un ensemble de comptes indexés par IBAN, permet la
  recherche, le virement entre deux comptes, le solde total et la liste des comptes
  en découvert.

Les erreurs métier passent par quatre exceptions maison héritant de `RuntimeException` :
`MontantInvalideException`, `SoldeInsuffisantException`, `CompteInconnuException` et
`CompteDejaExistantException`.

Une interface console (`fr.sio.banque.ui.Main`) permet de manipuler les comptes à la
main, sans passer par un IDE.

## Choix de conception

### Découpage en deux classes

`CompteBancaire` ne connaît que son propre solde : elle n'a aucune raison de savoir
que d'autres comptes existent. Tout ce qui concerne un ensemble de comptes (recherche
par IBAN, virement, solde total) est donc dans `GestionnaireComptes`. Ce découpage
permet de tester le compte seul, sans avoir besoin d'un gestionnaire.

### Exceptions maison et non vérifiées

Les quatre exceptions héritent de `RuntimeException` et non de `Exception`. Un montant
négatif ou un IBAN inconnu sont des erreurs d'utilisation de l'API, pas des incidents
que l'appelant doit gérer à chaque ligne. Hériter de `Exception` aurait obligé à
entourer chaque appel d'un `try/catch`, y compris dans les tests.

Le taux négatif de `calculerInterets` fait exception : il lève une
`IllegalArgumentException` standard, parce que le sujet ne prévoit pas d'exception
métier pour ce cas et qu'un taux erroné relève de l'erreur de programmation, pas
d'une règle bancaire.

### Séparation interface / métier

La classe `Main` ne contient aucune règle métier : elle lit les saisies, appelle les
méthodes du métier, affiche les résultats et attrape les exceptions pour afficher un
message lisible plutôt qu'une trace d'erreur. Toute la logique testable reste dans
`CompteBancaire` et `GestionnaireComptes`, ce qui permet de la couvrir par des tests
unitaires sans jamais simuler une saisie clavier. L'interface console n'est donc pas
couverte par les tests, et c'est assumé.

### TDD ou tests après coup

J'ai commencé par écrire le code métier en me basant sur les règles du sujet, puis je
me suis rendu compte que je n'avais pas compris ce qu'on attendait : faire du TDD, ce
n'est pas écrire des tests sur du code déjà écrit.

Plutôt que de réorganiser mes commits pour faire croire que les tests étaient venus en
premier, j'ai repris `CompteBancaire` à zéro : j'ai remis toutes ses méthodes métier à
l'état de stubs (`UnsupportedOperationException`), puis j'ai écrit les tests, constaté
l'échec, et réimplémenté méthode par méthode jusqu'au vert. L'historique Git montre
cette reprise, commit `refactor: reprise de CompteBancaire en TDD, repart de stubs`,
suivie de trois cycles rouge-vert.

Les commits de test sont donc volontairement dans un état où les tests échouent. C'est
un choix, pas un oubli : c'est ce qui prouve que le test précède le code. Sur un projet
en équipe avec une intégration continue, je ne pousserais pas du rouge sur une branche
partagée.

`GestionnaireComptes` a été écrite avant sa suite de tests et n'a pas été reprise de la
même façon, faute de temps. Ses tests sont donc écrits après coup.

## Lancer les tests

```
cd tp-junit_comptes
mvn test
```

## Lancer l'application en console

```
cd tp-junit_comptes
mvn package
java -jar target/tp-junit-comptes-1.0-SNAPSHOT.jar
```

Sans créer le jar :

```
cd tp-junit_comptes
mvn compile
java -cp target/classes fr.sio.banque.ui.Main
```

Depuis IntelliJ : ouvrir `src/main/java/fr/sio/banque/ui/Main.java` et cliquer la
flèche verte à gauche de `public static void main`. IntelliJ crée une configuration
de lancement `Main`, ensuite disponible dans le sélecteur en haut de la fenêtre, à
côté des configurations de test.

Le projet cible Java 17 (`maven.compiler.release`), il se compile donc avec n'importe
quel JDK 17 ou supérieur.

## Récapitulatif des tests

| Classe de test | Nombre de tests | Ce qu'elle couvre |
|---|---|---|
| `CompteBancaireTest` | 12 | Dépôt, retrait, intérêts, découvert |
| `GestionnaireComptesTest` | à écrire | Ajout, recherche, virement atomique, solde total, comptes en découvert |

Détail de `CompteBancaireTest` :

| Méthode testée | Tests | Cas couverts |
|---|---|---|
| `deposer` | 3 | Dépôt nominal, montant négatif, montant à zéro |
| `retirer` | 4 | Retrait nominal, retrait amenant exactement à `-decouvertAutorise`, un centime de plus, montant négatif |
| `calculerInterets` | 3 | Solde positif (et solde inchangé), solde négatif, taux négatif |
| `estEnDecouvert` | 2 | Solde positif, solde négatif |

Deux points sur la façon dont ces tests sont écrits :

Les cas limites vont par paires. Un retrait de 200 sur un découvert de 200 doit passer,
un retrait de 200,01 doit échouer. Tester un cas confortable au milieu de l'intervalle
n'aurait rien prouvé sur la frontière.

Le test du retrait refusé vérifie deux choses : que l'exception est bien levée, et que
le solde n'a pas bougé. Une opération qui échoue ne doit rien laisser derrière elle.

## Difficultés rencontrées

**Le projet n'était pas un projet Maven pour l'IDE.** Je l'avais créé comme un projet
IntelliJ classique, donc l'IDE prenait `src` comme racine des sources et affichait mes
classes dans un package `main.java.fr.sio.banque` au lieu de `fr.sio.banque`. Surtout,
il ne lisait pas le `pom.xml` : JUnit n'était pas dans le classpath et aucun test ne
pouvait être lancé. Réglé en rouvrant le projet directement depuis le `pom.xml`.

**Un correctif automatique d'IntelliJ a cassé la portabilité du projet.** En acceptant
une suggestion sans la lire, j'ai laissé ajouter un `maven-compiler-plugin` configuré
en `<source>25</source>`, ma version locale du JDK. Le projet compilait chez moi mais
aurait échoué sur toute machine avec un JDK plus ancien. J'ai supprimé le bloc et gardé
`maven.compiler.release` à 17.

**Du code non testé disparaît sans que personne s'en aperçoive.** Quand j'ai remis
`CompteBancaire` à l'état de stubs pour la reprise en TDD, les validations du
constructeur (IBAN vide, titulaire vide) ont disparu avec le reste. Aucun de mes douze
tests ne les réclamait, donc rien n'a signalé leur absence : tout était vert, et
pourtant on pouvait créer un compte sans IBAN. Je ne m'en suis rendu compte qu'en
testant l'interface console à la main. C'est l'illustration la plus concrète que j'aie
eue de ce que la couverture de tests veut dire.

**La comparaison des `double`.** `assertEquals(50, interets)` sur des nombres à
virgule ne marche pas de façon fiable : les `double` accumulent des erreurs d'arrondi.
Il faut passer un troisième argument, la tolérance : `assertEquals(50, interets, 0.001)`.

## Bilan

Ce que je retiens surtout, c'est la différence entre écrire des tests et faire du TDD.
Écrire les tests après coup, c'est vérifier que le code fait ce qu'il fait déjà. Écrire
le test d'abord oblige à décider ce que la méthode doit faire avant de savoir comment
l'écrire, et le test échoue tant que ce n'est pas le cas.

J'ai aussi compris à quoi sert un test de cas limite. Les tests qui m'ont appris
quelque chose ne sont pas ceux qui vérifient qu'un dépôt de 100 donne 100, mais ceux
qui tapent exactement sur la frontière du découvert autorisé.

Enfin, la reprise en stubs m'a montré qu'une suite de tests verte ne veut pas dire que
le code est correct : elle veut dire que le code fait ce que les tests demandent. Tout
ce que les tests ne demandent pas peut disparaître sans bruit.
