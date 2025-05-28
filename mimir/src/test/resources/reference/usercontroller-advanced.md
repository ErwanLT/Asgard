# UserController

Contrôleur REST pour la gestion des utilisateurs. Ce contrôleur permet de créer, récupérer, mettre à jour et supprimer des utilisateurs.

Tags: `user`, `rest`, `api`, `crud`

Category: user-management

## Diagramme de Classe

![Diagramme UML](diagrams/usercontroller_diagram.png)

## Methods

### createUser

Crée un nouvel utilisateur dans le système. L'utilisateur doit fournir un email valide et un mot de passe respectant les critères de sécurité.

#### Parameters

- `userDTO` : Données de l'utilisateur à créer, incluant email et mot de passe

#### Returns

L'utilisateur créé avec son identifiant unique

#### Throws

- `InvalidEmailException` : L'email fourni n'est pas valide
- `InvalidPasswordException` : Le mot de passe ne respecte pas les critères de sécurité
- `UserAlreadyExistsException` : Un utilisateur avec cet email existe déjà

### getUserById

Récupère les informations d'un utilisateur à partir de son identifiant unique. Retourne une erreur 404 si l'utilisateur n'existe pas.

#### Parameters

- `id` : Identifiant unique de l'utilisateur

#### Returns

Les informations complètes de l'utilisateur

#### Throws

- `UserNotFoundException` : L'utilisateur n'a pas été trouvé

### updateUser

Met à jour les informations d'un utilisateur existant. Seuls les champs fournis seront mis à jour, les autres resteront inchangés.

#### Parameters

- `id` : Identifiant unique de l'utilisateur
- `userDTO` : Données à mettre à jour

#### Returns

L'utilisateur mis à jour

#### Throws

- `UserNotFoundException` : L'utilisateur n'a pas été trouvé
- `InvalidEmailException` : Le nouvel email n'est pas valide

### deleteUser

Supprime un utilisateur du système. Cette action est irréversible et supprimera toutes les données associées à l'utilisateur.

#### Parameters

- `id` : Identifiant unique de l'utilisateur

#### Returns

Confirmation de la suppression

#### Throws

- `UserNotFoundException` : L'utilisateur n'a pas été trouvé
- `UserDeletionException` : Impossible de supprimer l'utilisateur 