import '../../assets/css/modal.css'
import {
    DataGrid,
    type GridCellParams,
    gridClasses,
    type GridColDef,
    type GridFeatureMode,
    type GridPaginationModel,
    type GridRowClassNameParams,
    type GridRowModel,
    type GridRowParams,
    type GridSortModel,
} from "@mui/x-data-grid";
import {useEffect, useState} from "react";
import {Box, Fab, Grid, TextField} from "@mui/material";
import AddOutlinedIcon from "@mui/icons-material/AddOutlined";
import {green, red} from "@mui/material/colors";
import Typography from "@mui/material/Typography";
import {CustomPaginationActions} from "./entity-data-grid-pagination.tsx";


interface EntitiesDataGridProps<T> {
    columns: GridColDef[];
    entities: T[];
    total: number;
    handlePaginationModelChange: (pm: GridPaginationModel) => void;
    handleRowClick?: (hrc: GridRowParams) => void;
    handleSortModelChange?: (gsm: GridSortModel) => void;
    handleAddItem?: () => void;
    handleSearch?: (search: string) => void;
    getCellClassName?: (params: GridCellParams<GridRowModel, number, number>) => string;
    getRowClassName?: (params: GridRowClassNameParams) => string;
    isLoading: boolean;
    disableAddEntityButton: boolean;
    disableSearch: boolean;
    initalSortState?: GridSortModel | undefined;
    initialSearch?: string;
    paginationModel: GridPaginationModel;
    paginationMode?: GridFeatureMode;
    title?: string;
}

export function EntitiesDataGrid<T>(props: EntitiesDataGridProps<T>) {

    const {
        columns,
        entities,
        total,
        handlePaginationModelChange,
        handleRowClick,
        handleSortModelChange,
        handleAddItem,
        handleSearch,
        getCellClassName,
        getRowClassName,
        isLoading,
        disableAddEntityButton,
        disableSearch,
        initalSortState,
        initialSearch,
        paginationModel,
        paginationMode,
        title,
    } = props;

    const pageSizeOptions: number[] = [5, 10, 15, 20, 50, 100];
    const [pagingModel, setPagingModel] = useState<GridPaginationModel>(paginationModel);

    const [search, setSearch] = useState<string>(initialSearch ? initialSearch : "");
    const [previousSearch, setPreviousSearch] = useState<string>("");

    useEffect(() => {
        const timeOutId = setTimeout(() => {
            if (search === previousSearch) {
                return;
            }
            setPreviousSearch(search);
            if (handleSearch) {
                handleSearch(search);
                const firstPage: GridPaginationModel = {
                    pageSize: pagingModel.pageSize,
                    page: 0,
                };
                onPaginationModelChange(firstPage);
            }
        }, 500);
        return () => clearTimeout(timeOutId);
    }, [search]);

    function onPaginationModelChange(model: GridPaginationModel) {
        setPagingModel(model);
        handlePaginationModelChange(model);
    }

    return (
        <>
            <Grid marginBottom={4} container direction='row' justifyContent='space-between' alignItems='center'>
                <Grid>
                    <Grid container spacing={2} direction='row' alignItems='center'>
                        <Grid>
                            <Fab
                                color="primary"
                                size="small"
                                aria-label="edit"
                                sx={{margin: '0'}}
                                onClick={handleAddItem}
                                disabled={disableAddEntityButton}
                            >
                                <AddOutlinedIcon/>
                            </Fab>
                        </Grid>
                        <Grid>
                            {title ??
                                <Typography>{title}</Typography>
                            }
                        </Grid>
                    </Grid>
                </Grid>
                <Grid>
                    <TextField
                        label="Search"
                        type="search"
                        id="outlined-size-small"
                        size="small"
                        value={search}
                        onChange={e => setSearch(e.target.value)}
                        disabled={disableSearch}
                    />
                </Grid>
            </Grid>
            <Box
                sx={{
                    width: '100%',
                    [`.${gridClasses.cell}.transferRot`]: {
                        color: red[800],
                    },
                    [`.${gridClasses.cell}.transferGreen`]: {
                        color: green[800],
                    },
                    [`.${gridClasses.cell}.transferNew`]: {
                        //color: (theme) => theme.palette.primary.main,
                        color: (theme) => theme.palette.background.paper,
                        backgroundColor: (theme) => theme.palette.primary.light,
                        //fontWeight:"medium"
                    },
                }}
            >
                <DataGrid
                    getRowId={(row) => row.uuid}
                    rows={entities}
                    columns={columns}
                    rowCount={paginationMode ? undefined : total}
                    paginationModel={pagingModel}
                    onPaginationModelChange={onPaginationModelChange}
                    paginationMode={paginationMode ? paginationMode : "server"} //"server"
                    pageSizeOptions={pageSizeOptions}
                    sortingMode="server"
                    onSortModelChange={handleSortModelChange}
                    disableRowSelectionOnClick
                    density='compact'
                    getCellClassName={getCellClassName}
                    getRowClassName={getRowClassName}
                    onRowClick={handleRowClick}
                    pagination={true}
                    initialState={{
                        sorting: {
                            sortModel: initalSortState ? initalSortState : [],
                        }
                    }}
                    // slotProps={{
                    //     pagination: {
                    //         showFirstButton: true,
                    //         showLastButton: true,
                    //     },
                    // }}
                    // slotProps={{
                    //     pagination: {
                    //         ActionsComponent: CustomPaginationActions,
                    //     },
                    // }}
                    slots={{pagination: CustomPaginationActions}}
                    sx={{
                        "&.MuiDataGrid-root .MuiDataGrid-cell:focus-within": {
                            outline: "none !important",
                        },
                        "&.MuiDataGrid-root .MuiDataGrid-cell": {
                            alignContent: "center"
                        },
                    }}
                    loading={isLoading}
                />
            </Box>
        </>
    )
        ;
};
