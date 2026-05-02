<?php
/**
 * Entité Reclamation : signalement / demande d'aide.
 * Équivalent PHP de com.ihecride.models.Reclamation.
 */

class Reclamation implements JsonSerializable {

    public ?int    $id = null;
    public ?int    $declarantId = null;
    public ?int    $cibleId = null;
    public ?string $declarantNom = null;
    public ?string $type = null;
    public ?string $description = null;
    public ?string $statut = null;          // OUVERT, EN_TRAITEMENT, CLOTURE
    public ?string $dateSoumission = null;

    public function jsonSerialize(): array {
        return [
            'id'              => $this->id,
            'declarantId'     => $this->declarantId,
            'cibleId'         => $this->cibleId,
            'declarantNom'    => $this->declarantNom,
            'type'            => $this->type,
            'description'     => $this->description,
            'statut'          => $this->statut,
            'dateSoumission'  => $this->dateSoumission,
        ];
    }
}
