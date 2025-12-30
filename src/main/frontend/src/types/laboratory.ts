import {type BaseEntity, getEmptyBaseEntity} from "./base-entity.ts";
import type {UserEntity} from "./user.ts";

export interface LaboratoryEntity extends BaseEntity {
    name: string;
    shortName: string;
    resultValue: number;
    labUser: UserEntity | null;
    labDate: string;
    labSwitchOn: boolean;
    labSwitchOff: boolean;
}

export function getEmptyEntity(): LaboratoryEntity {
    const empty: LaboratoryEntity = {
        name: "",
        shortName: "",
        resultValue: 0,
        labUser: null,
        labDate: "",
        labSwitchOn: true,
        labSwitchOff: false,
        ...getEmptyBaseEntity()
    }
    return empty;
}