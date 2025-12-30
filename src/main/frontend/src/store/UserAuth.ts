export interface UserAuth {
    uuid: string;
    jwtToken: string;
    roles: string[];
    username: string;
    language: string;
}