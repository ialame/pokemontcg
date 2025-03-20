export interface Serie {
    id: number;
    name: string;
}

export interface Set {
    id: string;
    name: string;
    releaseDate?: string;
}

export interface Card {
    id: string;
    name: string;
    imagePath?: string;
}