import type {RequestError} from "./api-error.ts";

export interface ResponseApi<T> {
    status: "success" | "error";
    // If a GET fails with a 404 this value is set to 404
    statusCode?: number;
    error: RequestError | null;
    totalAmount: number;
    totalPages: number;
    data: T[];

    // constructor(totalAmount: number, totalPages: number, data: T[], status: "success" | "error", statusCode: number, error: string | null) {
    //     this.totalAmount = totalAmount;
    //     this.totalPages = totalPages;
    //     this.data = data;
    //     this.status = status;
    //     this.statusCode = statusCode;
    //     this.error = error;
    // }
}

export interface RequestResponse<Response>
{
    status: "success" | "error";
    // If a GET fails with a 404 this value is set to 404
    statusCode?: number;
    error: RequestError | null;
    data: Response | null;
}

// export class ResponsePagedApi<T> {
//     content: T[];
//     page: number;
//     size: number;
//     totalElements: number;
//     totalPages: number;
//     lastPage: boolean;
//
//     constructor(content: T[], page: number, size: number, totalElements: number, totalPages: number, lastPage: boolean) {
//         this.content = content;
//         this.page = page;
//         this.size = size;
//         this.totalElements = totalElements;
//         this.totalPages = totalPages;
//         this.lastPage = lastPage;
//     }
//
// }