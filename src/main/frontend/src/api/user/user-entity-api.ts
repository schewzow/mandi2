import EntityAPI from "../entity-api.ts";
import type {UserEntity} from "../../types/user.ts";

export const PATH = "users";

export default {

    ...EntityAPI<UserEntity>(PATH),

};