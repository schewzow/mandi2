import {Box} from "@mui/material";

export const NotFound = () => {
    return (
        <Box
            sx={{
                display: "flex",
                flexGrow: 1,
                justifyContent: "center",
                alignItems: "center",
            }}
        >
            Page you are looking for doesn't exist
        </Box>
    );
}
export default NotFound;