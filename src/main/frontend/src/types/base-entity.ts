import type {UserEntity} from "./user.ts";

export interface BaseEntity {
    uuid: string;
    active: boolean;
    createdDate: string;
    createdBy: UserEntity | null;
    lastModifiedDate: string;
    lastModifiedBy: UserEntity | null;
}

export function getEmptyBaseEntity(): BaseEntity {
    const empty: BaseEntity = {
        uuid: "",
        active: true,
        createdDate: "",
        createdBy: null,
        lastModifiedDate: "",
        lastModifiedBy: null,
    }
    return empty;
}