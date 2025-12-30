import {useEffect, useState} from "react";
import {getErrorMessage} from "../../../utils/ErrorHandling.ts";
import type {ResponseApi} from "../../../api/response.ts";
import LaboratoryEntityApi, {PATH as LaboratoryAPIPath} from "../../../api/labs/laboratory-entity-api.ts";
import {Box} from "@mui/material";
import {type GridColDef, type GridPaginationModel, type GridRowParams, type GridSortModel} from "@mui/x-data-grid";
import {type LaboratoryEntity} from "../../../types/laboratory.ts";
import {useNavigate} from "react-router-dom";
import {useCookies} from 'react-cookie';
import {EntitiesDataGrid} from "../../utils/entities-data-grid.tsx";


export const LabsList = () => {

    const navigate = useNavigate();

    const paginationCookieName = "labs-pagination";
    const sortingCookieName = "labs-sorting";
    const searchCookieName = "labs-search";

    const [cookiePagination, setCookiePagination] = useCookies([paginationCookieName])
    const [cookieSorting, setCookieSorting] = useCookies([sortingCookieName])
    const [cookieSearch, setCookieSearch] = useCookies([searchCookieName])

    const [httpError, setHttpError] = useState<string>("");
    const [isLoading, setIsLoading] = useState<boolean>(true);

    const [itemsPaged, setItemsPaged] = useState<LaboratoryEntity[]>([]);

    const [totalAmountOfItems, setTotalAmountOfItems] = useState<number>(0);
    //const [reloadChange, setReloadChange] = useState<boolean>(false);

    let storedPaginationModel: GridPaginationModel = cookiePagination[paginationCookieName];
    if (!storedPaginationModel) {
        storedPaginationModel = {
            pageSize: 10, page: 0,
        };
    }
    const [paginationModel, setPaginationModel] = useState(storedPaginationModel);

    const initialSortState: GridSortModel = [{field: 'name', sort: 'asc'}];
    const storedSortingModel: GridSortModel = cookieSorting[sortingCookieName];
    if (storedSortingModel) {
        if (storedSortingModel.length > 0) {
            initialSortState[0].field = storedSortingModel[0].field;
            initialSortState[0].sort = storedSortingModel[0].sort;
        }
    }
    const [sortState, setSortState] = useState<GridSortModel | undefined>(initialSortState);

    const storedSearch: string = cookieSearch[searchCookieName] ? cookieSearch[searchCookieName] : "";
    const [query, setQuery] = useState<string>(storedSearch);

    // const closeItemDialog = (reload: boolean) => {
    //     setShowModal(false);
    //     if (reload) {
    //         setReloadChange(!reloadChange);
    //     }
    // };

    useEffect(() => {
        const fetchItemsPaged = async () => {
            setIsLoading(true);

            //store pagination
            setCookiePagination(paginationCookieName, paginationModel, {path: '/'});
            setCookieSorting(sortingCookieName, sortState, {path: '/'});
            setCookieSearch(searchCookieName, query, {path: '/'});

            const response: ResponseApi<LaboratoryEntity> = await LaboratoryEntityApi.fetchPagedList( {
                filter: query, page: paginationModel.page, size: paginationModel.pageSize, sort: sortState
            });

            if (response.status === "error") {
                throw response.error;
            }
            setTotalAmountOfItems(response.totalAmount);
            setItemsPaged(response.data);
            setIsLoading(false);
        }
        fetchItemsPaged().catch((error: unknown) => {
            setIsLoading(false);
            setHttpError(getErrorMessage(error));
        });
        //window.scrollTo(0, 0);
    }, [sortState, paginationModel]);

    if (httpError) {
        return (<div className='container mt-5'>
                <p>{httpError}</p>
            </div>);
    }

    function handleAddItem() {
        //incPositionDialogKey();
        //setShowModal(true);
    }

    const handleRowClick = (params: GridRowParams, // GridRowParams
    ) => {
        navigate(`/${LaboratoryAPIPath}/${params.row.uuid}`);
    };


    const columns: GridColDef[] = [{
        field: 'name',
        headerName: 'Name',
        type: 'string',
        minWidth: 300,
        flex: 0.25
    }, {field: 'shortName', headerName: 'Short Name', type: 'string', minWidth: 300, flex: 0.25},];

    return (<>
            <Box>
                <EntitiesDataGrid<LaboratoryEntity>
                    columns={columns}
                    entities={itemsPaged}
                    total={totalAmountOfItems}
                    handlePaginationModelChange={setPaginationModel}
                    handleRowClick={handleRowClick}
                    handleSortModelChange={setSortState}
                    handleAddItem={handleAddItem}
                    handleSearch={setQuery}
                    isLoading={isLoading}
                    disableAddEntityButton={false}
                    disableSearch={false}
                    initalSortState={initialSortState}
                    initialSearch={query}
                    paginationModel={paginationModel}
                />
            </Box>
        </>);
}