import {Env} from "../Env.ts";
import type {RequestResponse, ResponseApi} from "./response.ts";
import api from "./api.ts";
import type {AxiosError} from "axios";
import {createAndCheckErrorResponse} from "./api-error.ts"

export function getAPIPath(path: string, uuid?: string) {
    //return `${Env.API_BASE_URL}/${path}${uuid ? `/${uuid}` : ""}`;
    return `${path}${uuid ? `/${uuid}` : ""}`;
}

export function getSearchAPIPath(path: string) {
    return `${path}/search`;
    //return `${Env.API_BASE_URL}/${path}/search`;
}

export function getApiBasePath(path: string) {
    return `${Env.API_BASE_URL}/${path}`;
}

export function getBasePath(path: string) {
    return `${Env.BASE_URL}/${path}`;
}

type ParameterValue = string | number | string[] | undefined | boolean;

export interface Parameters {
    [key: string]: ParameterValue;
}

export interface Pagination {
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
    lastPage: boolean;
}

export interface PaginatedResponseData<Entity> {
    items: Entity[];
    pagination: Pagination;
}

export interface PaginatedResponse<Entity> {
    data: PaginatedResponseData<Entity> | null;
    error: string | null;
    status: "error" | "success";
    statusCode: number;
}

export const buildUrl = (pathname: string, params: Parameters = {}) => {
    const stringifiedParams = stringifyParams(params);
    return `${pathname}?${stringifiedParams}`;
};

export const stringifyParams = (params: Parameters): string => {
    return Object.keys(params).map(
        key => {
            const param = params[key];
            if (typeof param === "undefined") return null;
            if (Array.isArray(param)) {
                if (!param.length) return null;
                return (param).map(value => `${key}=${encodeURIComponent(value)}`).join("&");
            }
            return `${key}=${encodeURIComponent(param as string)}`;
        },
    )
        .filter(value => value !== null)
        .join("&");
};

export interface GetParams {
    path: string;
    urlParams?: Parameters;
}

export interface PostParams<Payload> extends GetParams {
    data?: Payload;
}

export type PatchParams<Payload> = PostParams<Payload>;
export type DeleteParams = GetParams;

// function getJwtFromLocalStorage(): string {
//     let jwt: string = "";
//     const oktaTokenJson: string | null = localStorage.getItem("okta-token-storage");
//     if (oktaTokenJson) {
//         const authState: AuthState = JSON.parse(oktaTokenJson);
//         if (authState.accessToken) {
//             if (authState.accessToken.accessToken) {
//                 jwt = authState.accessToken.accessToken;
//             }
//         }
//     }
//     return jwt;
// }

export type RequestMethod = "GET" | "POST" | "PATCH" | "PUT" | "DELETE";

export function get<Response>(
    params: GetParams,
) {

    return makeRequest<Response, null>(
        "GET",
        params,
    );
}

export async function getList<Entity>(
    params: GetParams,
): Promise<ResponseApi<Entity>> {
    const response: RequestResponse<Entity> = await makeRequest<Entity, null>(
        "GET",
        params,
    );

    if (response.status === "success" && response.data) {
        const responseJson = await response.data as any;

        return {
            status: response.status,
            statusCode: response.statusCode,
            error: response.error,
            totalAmount: 0,
            totalPages: 0,
            data: responseJson?._embedded?.content,
        };
    }

    return {
        totalAmount: 0,
        totalPages: 0,
        data: [],
        status: response.status,
        statusCode: response.statusCode,
        error: response.error
    };
}

export async function getPaginated<Entity>(
    params: GetParams,
): Promise<ResponseApi<Entity>> {

    //console.log("getPaginated params: ", params);

    const response: RequestResponse<Entity> = await makeRequest<Entity, null>(
        "GET",
        params,
    );

    if (response.status === "success" && response.data) {
        const responseJson = await response.data as any;
        //console.log("responseJson: ", responseJson);
        return {
            totalAmount: responseJson.page.totalElements,
            totalPages: responseJson.page.totalPages,
            data: responseJson?.embedded?.content,
            status: response.status,
            statusCode: response.statusCode,
            error: response.error
        };
    }

    return {
        totalAmount: 0,
        totalPages: 0,
        data: [],
        status: response.status,
        statusCode: response.statusCode,
        error: response.error
    };
}

// export async function getPaginated<Entity>(
//     params: GetParams,
// ): Promise<PaginatedResponse<Entity>>
// {
//     const response: RequestResponse<ResponsePagedApi<Entity>> = await makeRequest<ResponsePagedApi<Entity>, null>(
//         "GET",
//         params,
//         [200],
//     );
//
//     if (response.status === "success" && response.data)
//     {
//         console.log("response.data: ", response.data);
//         const page: ResponsePagedApi<Entity> = response.data as ResponsePagedApi<Entity>;
//
//         return {
//             ...response,
//             data: {
//                 items: page.content,
//                 pagination: {
//                     page: page.page,
//                     size: page.size,
//                     totalElements: page.totalElements,
//                     totalPages: page.totalPages,
//                     lastPage: page.lastPage
//                 },
//             },
//         };
//     }
//     return {
//         status: response.status,
//         statusCode: response.statusCode,
//         error: response.error,
//         data: null,
//     };
// }

export function post<Response, Payload>(
    params: PostParams<Payload>,
) {

    return makeRequest<Response, Payload>(
        "POST",
        params,
    );
}

export function patch<Response, Payload>(
    params: PatchParams<Payload>,
) {
    return makeRequest<Response, Payload>(
        "PATCH",
        params,
    );
}

export function del<Response>(
    params: DeleteParams,
) {
    return makeRequest<Response, null>(
        "DELETE",
        params,
    );
}

async function makeRequest<Response, Payload>(
    method: RequestMethod,
    {
        data,
        path,
        urlParams,
    }: GetParams & Partial<PostParams<Payload>>,
): Promise<RequestResponse<Response>> {

    let url: string = buildUrl(path, urlParams);
    if (url.slice(-1) === "?") {
        url = url.slice(0, -1);
        // if(method==="PATCH"){
        //     url = url + "23"; // just to get a global error for testing...
        // }
    }

    const requestOptions = {
        method: method,
        url: url,
        //headers: headers,
        data: data
    };

    //console.log("requestOptions: ", requestOptions);

    try {
        return await api.request(requestOptions)
            //.then(response => new RequestResponse<Response>("success", response.status, response.statusText, response.data))
            .then(response =>
                ({
                    status: "success",
                    statusCode: response.status,
                    error: null,
                    data: response.data
                })
            );

    } catch (error: AxiosError | any) {
        switch (error.status) {
            // If we are not authorized find out if we are logged out and request the user to log in
            case 401:
                // This means we are not logged in
                console.log("Unauthorized request (AS) ", error.status);
                window.location.href = `${Env.LOGIN_URL}`;
        }
        return createAndCheckErrorResponse(error.response?.data);
    }
}



