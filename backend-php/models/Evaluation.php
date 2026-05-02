<?php
/**
 * Entité Evaluation : note + commentaire après un trajet.
 * Équivalent PHP de com.ihecride.models.Evaluation.
 */

class Evaluation implements JsonSerializable {

    public ?int    $id = null;
    public ?int    $trajetId = null;
    public ?int    $evaluateurId = null;
    public ?int    $evalueId = null;
    public ?string $evaluateurNom = null;
    public ?int    $note = null;
    public ?string $commentaire = null;
    public ?string $date = null;

    public function jsonSerialize(): array {
        return [
            'id'             => $this->id,
            'trajetId'       => $this->trajetId,
            'evaluateurId'   => $this->evaluateurId,
            'evalueId'       => $this->evalueId,
            'evaluateurNom'  => $this->evaluateurNom,
            'note'           => $this->note,
            'commentaire'    => $this->commentaire,
            'date'           => $this->date,
        ];
    }
}
