// export enum ActionTypeEnum {
//     CREATE = "CREATE",
//     UPDATE = "UPDATE",
//     DELETE = "DELETE",
// }

//export type ActionTypeEnum = "CREATE" | "UPDATE" | "DELETE";
export const ActionTypes = ["CREATE", "UPDATE", "DELETE"] as const;
export type ActionType = typeof ActionTypes[number];