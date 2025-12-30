export const EntityTypes = {
    laboratories:           "laboratories",
    users:                  "users",
} as const;

export type EntityType = (typeof EntityTypes)[keyof typeof EntityTypes];
