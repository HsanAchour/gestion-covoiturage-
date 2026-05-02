<?php
/**
 * Entité SosAlert : alerte SOS avec géolocalisation.
 * Équivalent PHP de com.ihecride.models.SosAlert.
 */

class SosAlert implements JsonSerializable {

    public ?int    $id = null;
    public ?int    $userId = null;
    public ?float  $latitude = null;
    public ?float  $longitude = null;
    public ?string $timestamp = null;
    public ?string $statut = null;        // ENVOYE, TRAITE

    public function jsonSerialize(): array {
        return [
            'id'         => $this->id,
            'userId'     => $this->userId,
            'latitude'   => $this->latitude,
            'longitude'  => $this->longitude,
            'timestamp'  => $this->timestamp,
            'statut'     => $this->statut,
        ];
    }
}
