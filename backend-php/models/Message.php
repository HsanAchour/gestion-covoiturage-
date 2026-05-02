<?php
/**
 * Entité Message.
 * Équivalent PHP de com.ihecride.models.Message.
 */

class Message implements JsonSerializable {

    public ?int    $id = null;
    public ?int    $expediteurId = null;
    public ?int    $destinataireId = null;
    public ?string $expediteurNom = null;
    public ?string $contenu = null;
    public ?string $dateEnvoi = null;
    public bool    $lu = false;

    public function jsonSerialize(): array {
        return [
            'id'             => $this->id,
            'expediteurId'   => $this->expediteurId,
            'destinataireId' => $this->destinataireId,
            'expediteurNom'  => $this->expediteurNom,
            'contenu'        => $this->contenu,
            'dateEnvoi'      => $this->dateEnvoi,
            'lu'             => $this->lu,
        ];
    }
}
