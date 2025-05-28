# UserController

Contrôleur REST pour la gestion des utilisateurs. Ce contrôleur permet de créer, récupérer, mettre à jour et supprimer des utilisateurs.

Tags: `user`, `rest`, `api`, `crud`

Category: user-management

## Methods

### createUser

Crée un nouvel utilisateur dans le système. L'utilisateur doit fournir un email valide et un mot de passe respectant les critères de sécurité.

### getUserById

Récupère les informations d'un utilisateur à partir de son identifiant unique. Retourne une erreur 404 si l'utilisateur n'existe pas.

### updateUser

Met à jour les informations d'un utilisateur existant. Seuls les champs fournis seront mis à jour, les autres resteront inchangés.

### deleteUser

Supprime un utilisateur du système. Cette action est irréversible et supprimera toutes les données associées à l'utilisateur. 