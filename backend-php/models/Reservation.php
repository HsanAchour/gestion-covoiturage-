<?php
/**
 * Entité Reservation : lien passager <-> trajet.
 * Équivalent PHP de com.ihecride.models.Reservation.
 */

class Reservation implements JsonSerializable {

    public ?int    $id = null;
    public ?int    $trajetId = null;
    public ?int    $passagerId = null;
    public ?string $passagerNom = null;
    public ?string $statut = null;       // EN_ATTENTE, ACCEPTE, REFUSE, ANNULE
    public ?string $createdAt = null;

    public function jsonSerialize(): array {
        return [
            'id'           => $this->id,
            'trajetId'     => $this->trajetId,
            'passagerId'   => $this->passagerId,
            'passagerNom'  => $this->passagerNom,
            'statut'       => $this->statut,
            'createdAt'    => $this->createdAt,
        ];
    }
}
