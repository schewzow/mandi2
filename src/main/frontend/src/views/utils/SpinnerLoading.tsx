import {Box, CircularProgress} from "@mui/material";

export const SpinnerLoading = () => {
    return (
        <Box
            sx={{
                display: "flex",
                flexGrow: 1,
                justifyContent: "center",
                alignItems: "center",
            }}
        >
            <CircularProgress/>
        </Box>
    );
}
export default SpinnerLoading;