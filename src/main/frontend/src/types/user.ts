import type {BaseEntity} from "./base-entity.ts";

export interface UserEntity extends BaseEntity
{
    userName: string;
    firstname: string;
    lastname: string;
    email?: string;
}

// type UserEntityPick = "uuid" | "firstname" | "lastname" | "email";
// export type UserReferenceEntity = Pick<UserEntity, UserEntityPick>;