/**
 * fetch specified resource from backend
 * While request is in progress isFetching is set to true
 * This will automatically handle unresolved backend errors and display them as global errors
 */
import {useEffect, useState} from "react";
import type {EntityAPIType} from "../api/entity-api.ts";
import type {ResponseApi} from "../api/response.ts";
import type {BaseEntity} from "../types/base-entity.ts";


export interface UseFetchListResponse<Entity extends BaseEntity> {
    items: Entity[];
    isFetching: boolean;
    setItems: (cb: (items: Entity[]) => Entity[]) => void;
}

export type UseFetchListProps<Entity extends BaseEntity> =
    {
        entityApi: EntityAPIType<Entity>,
        filter: string,
        fetchAll?: boolean,
    }

function useFetchEntityList<Entity extends BaseEntity>(
    {
        entityApi,
        filter,
        fetchAll = true, // usually this hook used for listboxes, so fetching all is true by default
    }: UseFetchListProps<Entity>): UseFetchListResponse<Entity> {

    const [isFetching, setIsFetching] = useState(false);
    const [data, setData] = useState<Entity[] | null>(null);
    //const { addError } = useErrorContext();

    //console.log("useFetchEntityList, isFetching: ", isFetching);

    // Can't use useEffectAsync as the cleanup function is ignored there
    useEffect(() => {
        let didCancel = false;
        setIsFetching(true);
        setData(null);

        let fetching = true;
        //let pagination = data ? data.pagination : undefined;
        let currentPage = 0;
        const items: Entity[] = [];

        const fetchData = async () => {
            while (!didCancel && fetching) {
                const response: ResponseApi<Entity> = await entityApi.fetchPagedList({
                    filter: filter, page: currentPage, size: 100,
                });

                if (response.status === "success" && response.data && !didCancel) {
                    // if we don't fetch all items, or pagination is not supported, respond here
                    if (!fetchAll) {
                        // Single fetch, dispatch complete and break the loop
                        setIsFetching(false);
                        if (response.status === "success" && response.data) setData(response.data);
                        fetching = false;
                    } else {
                        // Fetch until all pages are fetched.
                        items.push(...response.data);

                        // if (!pagination)
                        // {
                        //    pagination = response.data.pagination;
                        // }
                        // else
                        // {
                        //    pagination.numberOfElements = items.length;
                        // }

                        if (response.totalPages > currentPage + 1) {
                            currentPage++;
                        } else {
                            setData(items);
                            setIsFetching(false);
                            fetching = false;
                        }
                    }
                } else if (!didCancel) {
                    // addError({
                    //    key: "list.fetch.failed",
                    //    message: "Error fetching resources",
                    // });
                    fetching = false;
                    setIsFetching(false);
                }
            }
        };

        // Call async code to perform action
        fetchData();

        // Return clean up hook
        return () => {
            didCancel = true;
        };
    }, [filter]);

    return {
        isFetching,
        setItems: (getNextItems) => {
            setData(
                prevData => prevData ? ({
                    ...prevData,
                    items: getNextItems(prevData),
                }) : null,
            );
        },
        items: data ?? [],
    };
}

export default useFetchEntityList;
