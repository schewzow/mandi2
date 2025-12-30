import {gridPageCountSelector, gridPageSelector, gridPageSizeSelector, useGridApiContext,} from '@mui/x-data-grid';
import {Box, MenuItem, Select} from '@mui/material';
import IconButton from '@mui/material/IconButton';
import FirstPageIcon from '@mui/icons-material/FirstPage';
import LastPageIcon from '@mui/icons-material/LastPage';
import KeyboardArrowLeft from '@mui/icons-material/KeyboardArrowLeft';
import KeyboardArrowRight from '@mui/icons-material/KeyboardArrowRight';

export function CustomPaginationActions() {
    const apiRef = useGridApiContext();
    const page = gridPageSelector(apiRef);
    const pageCount = gridPageCountSelector(apiRef);
    const pageSize = gridPageSizeSelector(apiRef);
    const totalRows = apiRef.current.getRowsCount();

    const handleFirst = () => apiRef.current.setPage(0);
    const handlePrev = () => apiRef.current.setPage(Math.max(page - 1, 0));
    const handleNext = () => apiRef.current.setPage(Math.min(page + 1, pageCount - 1));
    const handleLast = () => apiRef.current.setPage(pageCount - 1);

    const handlePageSizeChange = (event: { target: { value: any; }; }) => {
        apiRef.current.setPageSize(Number(event.target.value));
    };

    const startRow = page * pageSize + 1;
    const endRow = Math.min(startRow + pageSize - 1, totalRows);

    const pageSizeOptions: number[] = [5, 10, 15, 20, 50, 100];

    return (
        <Box sx={{display: 'flex', alignItems: 'center', gap: 1, p: 1}}>
            <Box sx={{display: {xs: 'none', sm: 'flex'}, alignItems: 'center'}}>
                <Box>Rows per page:</Box>
                <Select
                    value={pageSize}
                    onChange={handlePageSizeChange}
                    size="small"
                    sx={{
                        margin: 0,
                        padding: 0,
                        boxShadow: 'none',
                        '.MuiOutlinedInput-notchedOutline': {border: 0},
                        fontSize: '0.875rem'
                    }}
                >
                    {pageSizeOptions.map((size) => (
                        <MenuItem key={size} value={size}>
                            {size}
                        </MenuItem>
                    ))}
                </Select>
            </Box>
            {/*<Box sx={{mx: 1}}>Page {page + 1} of {pageCount}</Box>*/}
            <Box sx={{display: {xs: 'none', sm: 'block'}}}>
                {startRow}–{endRow} of {totalRows}
            </Box>
            <IconButton
                onClick={handleFirst}
                disabled={page === 0}
                aria-label="first page"
                size="small"
            >
                <FirstPageIcon/>
            </IconButton>
            <IconButton
                onClick={handlePrev}
                disabled={page === 0}
                aria-label="previous page"
                size="small"
            >
                <KeyboardArrowLeft/>
            </IconButton>
            <IconButton
                onClick={handleNext}
                disabled={page >= pageCount - 1}
                aria-label="next page"
                size="small"
            >
                <KeyboardArrowRight/>
            </IconButton>
            <IconButton
                onClick={handleLast}
                disabled={page >= pageCount - 1}
                aria-label="last page"
                size="small"
            >
                <LastPageIcon/>
            </IconButton>

            {/*<Button onClick={handleFirst} disabled={page === 0}>First</Button>*/}
            {/*<Button onClick={handlePrev} disabled={page === 0}>Previous</Button>*/}
            {/*<Button onClick={handleNext} disabled={page >= pageCount - 1}>Next</Button>*/}
            {/*<Button onClick={handleLast} disabled={page >= pageCount - 1}>Last</Button>*/}
        </Box>
    );
}