import type {RequestResponse, ResponseApi} from "./response.ts";
import {
    del,
    get,
    getApiBasePath,
    getList,
    getPaginated,
    getSearchAPIPath,
    type Parameters,
    patch,
    post
} from "./api-utils.ts";
import type {GridSortModel} from "@mui/x-data-grid";

export interface FetchListParams {
    urlParams?: Parameters;
}

export interface FetchPagedParams extends FetchListParams {
    filter?: string;
    page?: number;
    size?: number;
    sort?: GridSortModel | undefined;
}

export interface FetchParams {
    uuid: string;
    urlParams?: Parameters;
}

export interface CreateParams<Payload> extends FetchListParams {
    data: Payload;
}

export interface PatchParams<Payload> extends FetchParams {
    data: Payload;
}

export type RemoveParams = FetchParams;

export type FetchListType<Entity> = (params: FetchPagedParams) => Promise<ResponseApi<Entity>>;

export interface EntityAPIType<Entity, Payload = Entity> {
    fetch: (params: FetchParams) => Promise<RequestResponse<Entity>>;
    create: (params: CreateParams<Partial<Payload>>) => Promise<RequestResponse<Entity>>;
    patch: (params: PatchParams<Partial<Payload>>) => Promise<RequestResponse<Entity>>;
    remove: (params: RemoveParams) => Promise<RequestResponse<Entity>>;
    fetchList: (params: FetchListParams) => Promise<ResponseApi<Entity>>;
    fetchPagedList: (params: FetchPagedParams) => Promise<ResponseApi<Entity>>;
}

/**
 * Fetch entity with given uuid
 * @param path entity subpath, full api path will be built by this function
 * @param uuid uuid of entity
 * @param urlParams additional parameters that should be appended to the url
 */
export function fetchEntity<Entity>(path: string, uuid: string, urlParams?: Parameters): Promise<RequestResponse<Entity>> {
    return get<Entity>({
        path: `${getApiBasePath(path)}/${uuid}`,
        urlParams,
    });
}

/**
 * Create entity with passed payload
 * @param path entity subpath, full api path will be built by this function
 * @param data payload that will be sent to the server
 * @param urlParams additional parameters that should be appended to the url
 */
export function createEntity<Entity, Payload = Partial<Entity>>(
    path: string,
    data: Payload,
    urlParams?: Parameters,
): Promise<RequestResponse<Entity>> {
    return post<Entity, Payload>({
        //path: `/api/${path}`,
        path: `${getApiBasePath(path)}`,
        urlParams,
        data,
    });
}

/**
 * Patch entity with passed payload on server
 * @param path entity subpath, full api path will be built by this function
 * @param uuid uuid of entity
 * @param data payload that will be sent to the server
 * @param urlParams additional parameters that should be appended to the url
 */
export function patchEntity<Entity, Payload = Partial<Entity>>(
    path: string,
    uuid: string,
    data: Payload,
    urlParams?: Parameters,
) {
    return patch<Entity, Payload>({
        path: `${getApiBasePath(path)}/${uuid}`,
        urlParams,
        data,
    });
}

/**
 * Set delete flag on server for given entity
 * @param path entity subpath, full api path will be built by this function
 * @param uuid uuid of entity
 * @param urlParams additional parameters that should be appended to the url
 */
export function removeEntity<Response>(
    path: string,
    uuid: string,
    urlParams?: Parameters,
) {
    return del<Response>({
        path: `${getApiBasePath(path)}/${uuid}`,
        urlParams,
    });
}

export function fetchEntityList<Entity>(path: string, urlParams?: Parameters): Promise<ResponseApi<Entity>> {
    return getList<Entity>({
        path: `${getApiBasePath(path)}`,
        urlParams,
    });
}

export function fetchPagedEntityList<Entity>(path: string, params?: FetchPagedParams): Promise<ResponseApi<Entity>> {
    let urlParams = {};
    if (params !== undefined) {
        let _sort = "";
        if (params.sort && params.sort.length > 0) {
            _sort = `${params.sort[0].field},${params.sort[0].sort}`;
        }

        urlParams = {
            ...params.urlParams,
            filter: params.filter,
            page: params.page,
            size: params.size,
            sort: _sort,
        };
    }

    return getPaginated<Entity>({
        path: `${getSearchAPIPath(path)}`,
        urlParams,
    });

    // return getPaginated<Entity>({
    //     path: `${getSearchAPIPath(path)}`,
    //     urlParams,
    // });
}

/**
 * Utility function to create general entity api, with fetch/fetchList/create/patch/remove/rebuildIndex
 * @param path entity subpath, general /api or /search subpaths will be added automatically
 */
function EntityAPI<Entity, Payload = Entity>(path: string): EntityAPIType<Entity, Payload> {
    return {
        fetch: ({uuid, urlParams}) => fetchEntity<Entity>(path, uuid, urlParams),
        create: ({data, urlParams}) => createEntity<Entity, Partial<Payload>>(path, data, urlParams),
        patch: ({uuid, data, urlParams}) => patchEntity<Entity, Partial<Payload>>(path, uuid, data, urlParams),
        remove: ({uuid, urlParams}) => removeEntity<Entity>(path, uuid, urlParams),
        fetchList: ({urlParams}) => fetchEntityList<Entity>(path, urlParams),
        fetchPagedList: ({urlParams, filter, page, size, sort}) => fetchPagedEntityList<Entity>(path, {urlParams, filter, page, size, sort} as FetchPagedParams),
    };
}

export default EntityAPI;