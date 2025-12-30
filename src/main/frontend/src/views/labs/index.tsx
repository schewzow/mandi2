import {StyledLayoutWrapper} from "../utils/styled-layout-wrapper.tsx";
import {Box} from "@mui/material";
import {LabsList} from "./components/labs-list.tsx";

export const LabsPage = () => {

    return (
        <StyledLayoutWrapper>
            <Box sx={{
                padding: 2,
                borderRadius: 1,
                bgcolor: (theme) => theme.palette.background.paper,
                marginBottom: 2,
            }}>
                <LabsList/>
            </Box>

        </StyledLayoutWrapper>);
}