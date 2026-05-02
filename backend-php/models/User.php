<?php
/**
 * Entité User : passager, conducteur ou admin.
 * Équivalent PHP de com.ihecride.models.User.
 */

class User implements JsonSerializable {

    public ?int    $id = null;
    public ?string $nom = null;
    public ?string $prenom = null;
    public ?string $email = null;
    public ?string $telephone = null;
    public ?string $motDePasseHash = null;
    public ?string $photo = null;
    public ?string $role = null;          // PASSAGER, CONDUCTEUR, ADMIN
    public float   $noteMoyenne = 0.0;
    public ?string $statut = null;        // ACTIF, BANNI, SUSPENDU
    public ?string $contactConfiance = null;
    public ?string $createdAt = null;

    public function __construct(
        ?string $nom = null, ?string $prenom = null, ?string $email = null,
        ?string $telephone = null, ?string $motDePasseHash = null, ?string $role = null
    ) {
        $this->nom = $nom;
        $this->prenom = $prenom;
        $this->email = $email;
        $this->telephone = $telephone;
        $this->motDePasseHash = $motDePasseHash;
        $this->role = $role;
    }

    public function jsonSerialize(): array {
        return [
            'id'                => $this->id,
            'nom'               => $this->nom,
            'prenom'            => $this->prenom,
            'email'             => $this->email,
            'telephone'         => $this->telephone,
            'photo'             => $this->photo,
            'role'              => $this->role,
            'noteMoyenne'       => $this->noteMoyenne,
            'statut'            => $this->statut,
            'contactConfiance'  => $this->contactConfiance,
            'createdAt'         => $this->createdAt,
        ];
    }
}
