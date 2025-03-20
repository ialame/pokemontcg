export interface Serie {
    id: number;
    name: string;
    sets?: Set[];  // Optionnel car non toujours renvoyé
}

export interface Set {
    id: string;
    name: string;
    serie: Serie;
    releaseDate: string;
}

export interface Card {
    id: string;
    name: string;
    set: Set;
    imagePath: string;
}