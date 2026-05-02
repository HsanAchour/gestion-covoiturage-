<?php
/**
 * Entité Trajet : un trajet proposé par un conducteur.
 * Équivalent PHP de com.ihecride.models.Trajet.
 */

class Trajet implements JsonSerializable {

    public ?int    $id = null;
    public ?int    $conducteurId = null;
    public ?string $conducteurNom = null;
    public ?string $depart = null;
    public ?string $destination = null;
    public ?string $dateHeure = null;
    public ?float  $prix = null;
    public ?int    $placesTotal = null;
    public ?int    $placesDispo = null;
    public ?string $description = null;
    public ?string $statut = null;        // OUVERT, COMPLET, EN_COURS, TERMINE, ANNULE
    public ?string $createdAt = null;

    public function jsonSerialize(): array {
        return [
            'id'             => $this->id,
            'conducteurId'   => $this->conducteurId,
            'conducteurNom'  => $this->conducteurNom,
            'depart'         => $this->depart,
            'destination'    => $this->destination,
            'dateHeure'      => $this->dateHeure,
            'prix'           => $this->prix,
            'placesTotal'    => $this->placesTotal,
            'placesDispo'    => $this->placesDispo,
            'description'    => $this->description,
            'statut'         => $this->statut,
            'createdAt'      => $this->createdAt,
        ];
    }
}
